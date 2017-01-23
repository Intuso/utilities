package com.intuso.utilities.webserver.ioc;

import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;
import java.util.Set;

/**
 * Created by tomc on 21/01/17.
 */
public class GuiceHK2BridgedResourceConfig extends ResourceConfig {

    @com.google.inject.Inject
    protected static Injector injector;

    public GuiceHK2BridgedResourceConfig() {
    }

    public GuiceHK2BridgedResourceConfig(Set<Class<?>> classes) {
        super(classes);
    }

    public GuiceHK2BridgedResourceConfig(Class<?>... classes) {
        super(classes);
    }

    public GuiceHK2BridgedResourceConfig(ResourceConfig original) {
        super(original);
    }

    @Inject
    protected void setServiceLocator(ServiceLocator serviceLocator){
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(serviceLocator);
        GuiceIntoHK2Bridge guiceBridge = serviceLocator.getService(GuiceIntoHK2Bridge.class);
        guiceBridge.bridgeGuiceInjector(injector);
    }
}
