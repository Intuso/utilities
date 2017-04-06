package com.intuso.utilities.webserver.oauth;

import com.intuso.utilities.webserver.oauth.model.Client;

import javax.servlet.http.HttpSession;

/**
 * Created by tomc on 12/02/17.
 */
public class SessionUtils {

    public final static String USER_ID = "oauth.userId";
    public final static String CLIENT = "oauth.client";

    public static String getUserId(HttpSession session) {
        return session != null ? (String) session.getAttribute(USER_ID) : null;
    }

    public static void setUserId(HttpSession session, String userId) {
        if(session != null)
            session.setAttribute(USER_ID, userId);
    }

    public static Client getClient(HttpSession session) {
        return session != null ? (Client) session.getAttribute(CLIENT) : null;
    }

    public static void setClient(HttpSession session, Client client) {
        if(session != null)
            session.setAttribute(CLIENT, client);
    }
}
