package com.intuso.utilities.webserver.oauth;

import com.intuso.utilities.webserver.oauth.model.Authorisation;
import com.intuso.utilities.webserver.oauth.model.Client;
import com.intuso.utilities.webserver.oauth.model.Token;

/**
 * Created by tomc on 29/03/17.
 */
public interface OAuthStore {

    Client getClient(String id);

    void updateAuthorisation(Authorisation authorisation);
    Authorisation getAuthorisation(String code);
    void deleteAuthorisation(String code);

    void updateToken(Token token);
    Token getToken(String id);
    Token getTokenForToken(String token);
    Token getTokenForRefreshToken(String refreshToken);
}
