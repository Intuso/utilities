package com.intuso.utilities.webserver.ioc;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;
import com.intuso.utilities.webserver.ServerService;
import com.intuso.utilities.webserver.config.PortConfig;
import org.eclipse.jetty.server.Server;

import java.util.Set;

/**
 * Created by tomc on 23/01/17.
 */
public class WebServerModule extends AbstractModule {

    private final Provider<Set<PortConfig>> configsProvider;
    private final Class<? extends Provider<Set<PortConfig>>> configsProviderClass;

    public WebServerModule(PortConfig ... configs) {
        this(Sets.newHashSet(configs));
    }

    public WebServerModule(Set<PortConfig> configs) {
        this(Providers.of(configs));
    }

    public WebServerModule(Provider<Set<PortConfig>> configsProvider) {
        this(configsProvider, null);
    }

    public WebServerModule(Class<? extends Provider<Set<PortConfig>>> configsProviderClass) {
        this(null, configsProviderClass);
    }

    public WebServerModule(Provider<Set<PortConfig>> configsProvider, Class<? extends Provider<Set<PortConfig>>> configsProviderClass) {
        this.configsProvider = configsProvider;
        this.configsProviderClass = configsProviderClass;
    }

    @Override
    protected void configure() {
        if(configsProvider != null)
            bind(new TypeLiteral<Set<PortConfig>>() {}).toProvider(configsProvider);
        else
            bind(new TypeLiteral<Set<PortConfig>>() {}).toProvider(configsProviderClass);
        bind(new TypeLiteral<Set<ConnectorProvider>>() {}).toProvider(ConnectorsProvider.class);
        bind(Server.class).toProvider(ServerProvider.class);
        Multibinder.newSetBinder(binder(), Service.class).addBinding().to(ServerService.class);
    }
}
