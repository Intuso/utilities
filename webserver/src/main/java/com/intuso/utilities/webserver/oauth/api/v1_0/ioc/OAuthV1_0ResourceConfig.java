package com.intuso.utilities.webserver.oauth.api.v1_0.ioc;

import com.intuso.utilities.webserver.oauth.api.v1_0.OAuthResource;
import com.intuso.utilities.webserver.ioc.GuiceHK2BridgedResourceConfig;

/**
 * Created by tomc on 21/01/17.
 */
public class OAuthV1_0ResourceConfig extends GuiceHK2BridgedResourceConfig {
    public OAuthV1_0ResourceConfig() {
        super(OAuthResource.class);
    }
}
