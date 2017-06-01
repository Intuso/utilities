package com.intuso.utilities.webserver.security;

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
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by tomc on 21/01/17.
 */
public class SecurityHandler extends HandlerWrapper {

    public final static String X_FORWARDED_FOR = "X-Forwarded-For";

    private final static Logger logger = LoggerFactory.getLogger(SecurityHandler.class);

    private final OAuthStore oAuthStore;

    private final Config config;

    @Inject
    public SecurityHandler(OAuthStore oAuthStore,
                           Config config) {
        this.oAuthStore = oAuthStore;
        this.config = config;
    }

    @Override
    public void destroy() {}

    @Override
    public void handle(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        if (isOAuthRequest(httpServletRequest)) {
            logger.debug("Received oAuth request");
            handleOAuthRequest(s, request, httpServletRequest, httpServletResponse);
        } else if (isValidSession(httpServletRequest)) {
            logger.debug("Received request for session {}", httpServletRequest.getSession().getId());
            if(_handler != null)
                _handler.handle(s,request, httpServletRequest, httpServletResponse);
        } else if (isUnsecuredEndpoint(httpServletRequest)) {
            logger.debug("Received request for unsecured endpoint {} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
            if(_handler != null)
                _handler.handle(s,request, httpServletRequest, httpServletResponse);
        } else {
            // not authorised to access the resource so redirect to login page
            logger.debug("Redirecting to login page: no oAuth or session for secured endpoint {} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
            String encodedURL = URLEncoder.encode(getOriginalUrl(httpServletRequest), "UTF-8");
            String loginPageRoot = httpServletRequest.getContextPath();
            if(loginPageRoot == null)
                loginPageRoot = "";
            else
                loginPageRoot = loginPageRoot + "/..";
            httpServletResponse.sendRedirect(loginPageRoot + config.getLoginPage() + "?" + config.getNextParam() + "=" + encodedURL);
        }
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

    private void handleOAuthRequest(String s, Request request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, ServletException {
        try {

            // Make the OAuth Request out of this request
            OAuthAccessResourceRequest oauthRequest = new OAuthAccessResourceRequest(httpServletRequest, ParameterStyle.HEADER);

            // Get the access token
            String tokenString = oauthRequest.getAccessToken();
            if(tokenString == null) {
                logger.error("Received oAuth request with no token");
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "No OAuth access token in request");
                return;
            }

            // get the token object
            Token token = oAuthStore.getTokenForToken(tokenString);
            if(token == null) {
                logger.error("Received oAuth for unknown token {}", tokenString);
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown access token" + tokenString);
                return;
            }

            // validate the token
            if(token.getExpiresAt() < System.currentTimeMillis()) {
                logger.error("Received oAuth request for expired token {} (Expired {})", tokenString, new Date(token.getExpiresAt()));
                httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Token has expired");
                return;
            }

            logger.debug("Received oAuth request for valid token {}", tokenString);

            // put the details in the session
            HttpSession session = httpServletRequest.getSession(true);
            SessionUtils.setUserId(session, token.getUserId());
            SessionUtils.setClient(session, token.getClient());

            if(_handler != null)
                _handler.handle(s, request, httpServletRequest, httpServletResponse);

        } catch (OAuthSystemException | OAuthProblemException e) {
            logger.error("Unexpected problem processing OAuth request", e);
            httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unexpected problem processing OAuth request: " + e.getMessage());
        }
    }

    private boolean isValidSession(HttpServletRequest request) {
        return request.getSession(false) != null;
    }

    private boolean isUnsecuredEndpoint(HttpServletRequest request) {
        return config.getUnsecuredEndpoints().containsEntry(request.getMethod(), request.getRequestURI());
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

    public static class Config {

        private final String loginPage;
        private final String nextParam;
        private final Multimap<String, String> unsecuredEndpoints;

        public Config(String loginPage, String nextParam) {
            this(loginPage, nextParam, HashMultimap.create());
        }

        public Config(String loginPage, String nextParam, Multimap<String, String> unsecuredEndpoints) {
            this.loginPage = loginPage;
            this.nextParam = nextParam;
            this.unsecuredEndpoints = unsecuredEndpoints;
        }

        String getLoginPage() {
            return loginPage;
        }

        String getNextParam() {
            return nextParam;
        }

        Multimap<String, String> getUnsecuredEndpoints() {
            return unsecuredEndpoints;
        }

        public void addUnsecuredEndpoint(String method, String path) {
            unsecuredEndpoints.put(method, path);
        }
    }
}
