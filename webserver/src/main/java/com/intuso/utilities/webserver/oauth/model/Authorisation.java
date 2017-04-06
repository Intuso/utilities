package com.intuso.utilities.webserver.oauth.model;

/**
 * Created by tomc on 21/01/17.
 */
public class Authorisation {

    private Client client;
    private String userId;
    private String code;

    public Authorisation() {}

    public Authorisation(Client client, String userId, String code) {
        this.client = client;
        this.userId = userId;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
