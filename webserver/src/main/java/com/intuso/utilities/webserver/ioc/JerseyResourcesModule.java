package com.intuso.utilities.webserver.ioc;

import com.google.inject.servlet.ServletModule;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Created by tomc on 21/01/17.
 */
public class JerseyResourcesModule extends ServletModule {

    private final String resourceRoot;
    private final Class<? extends GuiceHK2BridgedResourceConfig> resourceConfigClass;

    public JerseyResourcesModule(String resourceRoot, Class<? extends GuiceHK2BridgedResourceConfig> resourceConfigClass) {
        this.resourceRoot = resourceRoot.endsWith("/") ? resourceRoot : resourceRoot + "/";
        this.resourceConfigClass = resourceConfigClass;
    }

    @Override
    protected void configureServlets() {
        super.configureServlets();
        requestStaticInjection(GuiceHK2BridgedResourceConfig.class);
        ServletContainer servletContainer = new ServletContainer(ResourceConfig.forApplicationClass(resourceConfigClass));
        serve(resourceRoot + "*").with(servletContainer);
    }
}
