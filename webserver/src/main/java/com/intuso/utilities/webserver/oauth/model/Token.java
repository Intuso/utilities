package com.intuso.utilities.webserver.oauth.model;

/**
 * Created by tomc on 21/01/17.
 */
public class Token {

    private String id;
    private Client client;
    private String userId;
    private String token;
    private String refreshToken;
    private long expiresAt;

    public Token() {}

    public Token(String id, Client client, String userId, String token, String refreshToken, long expiresAt) {
        this.id = id;
        this.client = client;
        this.userId = userId;
        this.token = token;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(long expiresAt) {
        this.expiresAt = expiresAt;
    }
}
