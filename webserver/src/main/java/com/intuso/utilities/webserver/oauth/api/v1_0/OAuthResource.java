package com.intuso.utilities.webserver.oauth.api.v1_0;

import com.intuso.utilities.webserver.oauth.OAuthStore;
import com.intuso.utilities.webserver.oauth.SessionUtils;
import com.intuso.utilities.webserver.oauth.model.Authorisation;
import com.intuso.utilities.webserver.oauth.model.Client;
import com.intuso.utilities.webserver.oauth.model.Token;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuer;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.request.OAuthTokenRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.message.types.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * Created by tomc on 21/01/17.
 */
@Path("/")
public class OAuthResource {

    private final static Logger logger = LoggerFactory.getLogger(OAuthResource.class);

    private final static long TOKEN_LIFETIME = 24 * 60 * 60 * 1000; // 1 day

    private final OAuthStore oAuthStore;

    @Inject
    public OAuthResource(OAuthStore oAuthStore) {
        this.oAuthStore = oAuthStore;
    }

    @GET
    @Path("/authorize")
    public Response authorize(@Context HttpServletRequest request) throws URISyntaxException, OAuthSystemException {

        logger.debug("Authorise request received");

        try {

            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

            String userId = SessionUtils.getUserId(request.getSession(false));
            if(userId == null)
                return buildBadRequestResponse("Could not find user in session");
            Client client = oAuthStore.getClient(oauthRequest.getClientId());
            if(client == null)
                return buildBadRequestResponse("Could not find client: " + oauthRequest.getClientId());

            //build response according to response_type
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);

            OAuthASResponse.OAuthAuthorizationResponseBuilder builder = OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);

            if (responseType.equals(ResponseType.CODE.toString())) {
                String code = oauthIssuerImpl.authorizationCode();
                Authorisation authorisation = new Authorisation(client, userId, code);
                oAuthStore.updateAuthorisation(authorisation);
                builder.setCode(code);
            }

            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);
            final OAuthResponse response = builder.location(redirectURI).buildQueryMessage();

            logger.debug("Authorise request for client {} granted for user {}. Redirecting to {}", client.getId(), userId, response.getLocationUri());

            return Response.status(response.getResponseStatus())
                    .location(new URI(response.getLocationUri()))
                    .build();
        } catch (OAuthProblemException e) {
            logger.debug("Authorise request failed", e);
            return buildBadOAuthRequestResponse(e);
        }
    }

    @POST
    @Path("/token")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("application/json")
    public Response token(@Context HttpServletRequest request, MultivaluedMap<String, String> form) throws OAuthSystemException {

        logger.debug("Token request received");

        // Attempt to build an OAuth request from the HTTP request.
        OAuthTokenRequest oauthRequest;
        try {
            oauthRequest = new OAuthTokenRequest(new OAuthRequestWrapper(request, form));

        // If the HTTP request was not a valid OAuth token request, then we
        // have no other choice but to reject it as a bad request.
        } catch(OAuthProblemException e) {
            // Build the OAuth response.
            return buildBadOAuthRequestResponse(e);
        }

        // Attempt to get the client.
        Client client = oAuthStore.getClient(oauthRequest.getClientId());
        // If the client is unknown, respond as such.
        if(client == null)
            return buildBadRequestResponse("Unknown client for id " + oauthRequest.getClientId());

        // Get the client secret and check it's correct
        String clientSecret = oauthRequest.getClientSecret();
        if(clientSecret == null)
            return buildBadRequestResponse("The client secret is required.");
        // Make sure the client gave the right secret.
        else if(!clientSecret.equals(client.getSecret()))
            return buildBadRequestResponse("The client secret is incorrect.");

        OAuthIssuer oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());

        GrantType grantType;
        try {
            grantType = GrantType.valueOf(oauthRequest.getGrantType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return buildBadRequestResponse("Unknown grant type " + oauthRequest.getGrantType());
        }

        Token token;
        switch (grantType) {
            case AUTHORIZATION_CODE:

                logger.debug("Requesting token for authorisation code");

                // Get the authorization code from the request.
                Authorisation authorisation = oAuthStore.getAuthorisation(oauthRequest.getCode());
                if (authorisation == null)
                    return buildBadRequestResponse("Unknown authorisation code: " + oauthRequest.getParam(OAuth.OAUTH_CODE));

                // check the client is the same
                if(!authorisation.getClient().getId().equals(client.getId()))
                    return buildBadRequestResponse("OAuth Client does not match the client for the authorisation code");

                token = new Token(
                        UUID.randomUUID().toString(),
                        authorisation.getClient(),
                        authorisation.getUserId(),
                        oauthIssuerImpl.accessToken(),
                        oauthIssuerImpl.refreshToken(),
                        System.currentTimeMillis() + TOKEN_LIFETIME); // + 1 day
                oAuthStore.deleteAuthorisation(authorisation.getCode());

                logger.debug("Swapping authorisation code {} for token {}", authorisation.getCode(), token.getToken());

                // todo
                // check the authorization code hasn't expired
//              if(System.currentTimeMillis() > authorisation.getExpirationTime())
//                    return buildBadRequestResponse("Authorization code has expired");

                // todo
                // check the authorization code has been granted by a user
//              if(authorisation.getUser() == null)
//                    return buildBadRequestResponse("Authorization code has not been granted by a user");
                break;
            case REFRESH_TOKEN:

                logger.debug("Requesting token refresh");

                // Get the refresh token from the request.
                String refreshToken = oauthRequest.getRefreshToken();
                if(refreshToken == null)
                    return buildBadRequestResponse("Missing refresh token");

                // get the token for the refresh token
                token = oAuthStore.getTokenForRefreshToken(refreshToken);
                if(token == null)
                    return buildBadRequestResponse("Could not find token for refresh token");

                if(!token.getClient().getId().equals(oauthRequest.getClientId()))
                    return buildBadRequestResponse("Current client does not match the one in the token");

                String previousToken = token.getToken();

                token.setToken(oauthIssuerImpl.accessToken());
                token.setRefreshToken(oauthIssuerImpl.refreshToken());
                token.setExpiresAt(System.currentTimeMillis() + TOKEN_LIFETIME);
                oAuthStore.updateToken(token);

                logger.debug("Refreshed token from {} to {}", previousToken, token.getToken());

                break;
            default:
                return buildBadRequestResponse("Unknown grant type " + oauthRequest.getGrantType());
        }

        oAuthStore.updateToken(token);

        logger.debug("Token request succeeded");

        OAuthResponse oAuthResponse = OAuthASResponse
                        .tokenResponse(HttpServletResponse.SC_OK)
                        .setAccessToken(token.getToken())
                        .setExpiresIn(Long.toString((token.getExpiresAt() - System.currentTimeMillis()) / 1000))
                        .setRefreshToken(token.getRefreshToken())
                        .setTokenType(TokenType.BEARER.toString())
                        .buildJSONMessage();

        return Response.status(oAuthResponse.getResponseStatus())
                .entity(oAuthResponse.getBody()).build();
    }

    private Response buildBadOAuthRequestResponse(OAuthProblemException e) throws OAuthSystemException {

        logger.error("Request failed", e);

        OAuthResponse res = OAuthASResponse
                .errorResponse(HttpServletResponse.SC_BAD_REQUEST)
                .error(e)
                .buildJSONMessage();
        return Response
                .status(res.getResponseStatus()).entity(res.getBody())
                .build();
    }

    private Response buildBadRequestResponse(String message) {
        logger.error("Request failed: {}", message);
        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(message)
                .build();
    }
}
