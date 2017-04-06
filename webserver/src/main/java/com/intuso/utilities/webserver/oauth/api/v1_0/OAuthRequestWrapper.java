package com.intuso.utilities.webserver.oauth.api.v1_0;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.MultivaluedMap;

/**
 * Fix taken from http://stackoverflow.com/questions/22376810/unable-to-retrive-post-data-using-context-httpservletrequest-when-passed-to-oa
 */
public class OAuthRequestWrapper extends HttpServletRequestWrapper {

    private MultivaluedMap<String, String> form;

    public OAuthRequestWrapper(HttpServletRequest request, MultivaluedMap<String, String> form) {
        super(request); this.form = form;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null)
         value = form.getFirst(name);
        return value;
    }
}
