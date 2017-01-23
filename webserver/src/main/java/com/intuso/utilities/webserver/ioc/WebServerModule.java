package com.intuso.utilities.webserver.ioc;

import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.eclipse.jetty.server.Server;

/**
 * Created by tomc on 23/01/17.
 */
public class WebServerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Server.class).toProvider(ServerProvider.class);
        Multibinder.newSetBinder(binder(), Service.class).addBinding().to(ServerService.class);
    }
}
