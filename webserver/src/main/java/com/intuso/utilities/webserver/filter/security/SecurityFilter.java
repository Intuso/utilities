package com.intuso.utilities.webserver.filter.security;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.intuso.utilities.webserver.oauth.OAuthStore;
import com.intuso.utilities.webserver.oauth.SessionUtils;
import com.intuso.utilities.webserver.oauth.model.Token;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.ParameterStyle;
import org.apache.oltu.oauth2.rs.request.OAuthAccessResourceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by tomc on 21/01/17.
 */
public class SecurityFilter implements Filter {

    public final static String LOGIN_PAGE = "loginPage";
    public final static String NEXT_PARAM = "nextParam";
    public final static String UNSECURED_PREFIX = "unsecured.";
    public final static String METHOD_PATH_SEPARATOR = ":";
    public final static String X_FORWARDED_FOR = "X-Forwarded-For";

    private final static Logger logger = LoggerFactory.getLogger(SecurityFilter.class);

    private String loginPage;
    private String nextParam;

    private final Multimap<String, String> unsecuredEndpoints = HashMultimap.create();

    private final OAuthStore oAuthStore;

    @Inject
    public SecurityFilter(OAuthStore oAuthStore) {
        this.oAuthStore = oAuthStore;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        loginPage = filterConfig.getInitParameter(LOGIN_PAGE);
        nextParam = filterConfig.getInitParameter(NEXT_PARAM);
        unsecuredEndpoints.put("GET", loginPage);
        Enumeration<String> parameterNames = filterConfig.getInitParameterNames();
        while(parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            if(parameterName.startsWith(UNSECURED_PREFIX)) {
                String value = filterConfig.getInitParameter(parameterName);
                int index = value.indexOf(METHOD_PATH_SEPARATOR);
                if(index > 0)
                    unsecuredEndpoints.put(value.substring(0, index), value.substring(index + 1));
            }
        }
    }

    @Override
    public void destroy() {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            if (isOAuthRequest(httpRequest)) {
                logger.debug("Received oAuth request");
                handleOAuthRequest(httpRequest, httpResponse, chain);
            } else if (isValidSession(httpRequest)) {
                logger.debug("Received request for session {}", httpRequest.getSession().getId());
                chain.doFilter(request, response);
            } else if (isUnsecuredEndpoint(httpRequest)) {
                logger.debug("Received request for unsecured endpoint {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
                chain.doFilter(request, response);
            } else {
                // not authorised to access the resource so redirect to login page
                logger.debug("Redirecting to login page: no oAuth or session for secured endpoint {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
                String encodedURL = URLEncoder.encode(getOriginalUrl(httpRequest), "UTF-8");
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/.." + loginPage + "?" + nextParam + "=" + encodedURL);
            }
        }

        // not an http request, just ignore it
    }

    private boolean isOAuthRequest(HttpServletRequest request) {
        try {
            // If we can make a valid OAuth Request out of this request, then we should treat this as an oauth request
            new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);
            return true;
        } catch (OAuthSystemException | OAuthProblemException e) {
            return false;
        }
    }

    private void handleOAuthRequest(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {

            // Make the OAuth Request out of this request
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(request, ParameterStyle.HEADER);

            // Get the access token
            String tokenString = oauthRequest.getAccessToken();
            if(tokenString == null) {
                logger.error("Received oAuth request with no token");
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No OAuth access token in request");
                return;
            }

            // get the token object
            Token token = oAuthStore.getTokenForToken(tokenString);
            if(token == null) {
                logger.error("Received oAuth for unknown token {}", tokenString);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown access token" + tokenString);
                return;
            }

            // validate the token
            if(token.getExpiresAt() < System.currentTimeMillis()) {
                logger.error("Received oAuth request for expired token {} (Expired {})", tokenString, new Date(token.getExpiresAt()));
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token has expired");
                return;
            }

            logger.debug("Received oAuth request for valid token {}", tokenString);

            // put the details in the session
            HttpSession session = request.getSession(true);
            SessionUtils.setUserId(session, token.getUserId());
            SessionUtils.setClient(session, token.getClient());

            chain.doFilter(request, response);

        } catch (OAuthSystemException | OAuthProblemException e) {
            logger.error("Unexpected problem processing OAuth request", e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unexpected problem processing OAuth request: " + e.getMessage());
        }
    }

    private boolean isValidSession(HttpServletRequest request) {
        return request.getSession(false) != null;
    }

    private boolean isUnsecuredEndpoint(HttpServletRequest request) {
        return unsecuredEndpoints.containsEntry(request.getMethod(), request.getRequestURI());
    }

    private String getOriginalUrl(HttpServletRequest httpRequest) {

        // get server and path info.
        String url;
        // If X-Forwarded-For header is set, use that as the base and append the uri
        if(httpRequest.getHeader(X_FORWARDED_FOR) != null)
            url = httpRequest.getHeader(X_FORWARDED_FOR) + httpRequest.getRequestURI();
        // otherwise use the full url
        else
            url = httpRequest.getRequestURL().toString();

        // if there are any query params, add them
        if(httpRequest.getQueryString() != null && httpRequest.getQueryString().length() > 0)
            url += "?" + httpRequest.getQueryString();

        return url;
    }
}
