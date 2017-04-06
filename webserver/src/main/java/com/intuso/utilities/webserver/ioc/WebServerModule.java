package com.intuso.utilities.webserver.ioc;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Service;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;
import com.intuso.utilities.webserver.ServerService;
import com.intuso.utilities.webserver.config.PortConfig;
import com.intuso.utilities.webserver.filter.security.ioc.SecurityModule;
import com.intuso.utilities.webserver.oauth.ioc.OAuthApiModule;
import org.eclipse.jetty.server.Server;

import java.util.Set;

/**
 * Created by tomc on 23/01/17.
 */
public class WebServerModule extends AbstractModule {

    private final Provider<Set<PortConfig>> configsProvider;
    private final Class<? extends Provider<Set<PortConfig>>> configsProviderClass;
    private final String loginPage;
    private final String nextParam;
    private final Multimap<String, String> unsecuredResources;

    private WebServerModule(Provider<Set<PortConfig>> configsProvider, Class<? extends Provider<Set<PortConfig>>> configsProviderClass, String loginPage, String nextParam, Multimap<String, String> unsecuredResources) {
        this.configsProvider = configsProvider;
        this.configsProviderClass = configsProviderClass;
        this.loginPage = loginPage;
        this.nextParam = nextParam;
        this.unsecuredResources = unsecuredResources;
    }

    @Override
    protected void configure() {

        // setup the ports
        if(configsProvider != null)
            bind(new TypeLiteral<Set<PortConfig>>() {}).toProvider(configsProvider);
        else
            bind(new TypeLiteral<Set<PortConfig>>() {}).toProvider(configsProviderClass);
        bind(new TypeLiteral<Set<ConnectorProvider>>() {}).toProvider(ConnectorsProvider.class);

        // bind the server itself
        bind(Server.class).toProvider(ServerProvider.class);
        Multibinder.newSetBinder(binder(), Service.class).addBinding().to(ServerService.class);

        // install the security filter
        install(new SecurityModule(loginPage, nextParam, unsecuredResources));

        // install the oauth api resources
        install(new OAuthApiModule());
    }

    public static class Builder {

        private Provider<Set<PortConfig>> configsProvider;
        private Class<? extends Provider<Set<PortConfig>>> configsProviderClass;
        private String loginPage = "/login/index.html";
        private String nextParam = "next";
        private Multimap<String, String> unsecuredResources = HashMultimap.create();

        public Builder portConfigs(PortConfig... portConfigs) {
            this.configsProvider = Providers.of(Sets.newHashSet(portConfigs));
            return this;
        }

        public Builder portConfigsProvider(Provider<Set<PortConfig>> configsProvider) {
            this.configsProvider = configsProvider;
            return this;
        }

        public Builder portConfigsProvider(Class<? extends Provider<Set<PortConfig>>> configsProviderClass) {
            this.configsProviderClass = configsProviderClass;
            return this;
        }

        public Builder loginPage(String loginPage) {
            this.loginPage = loginPage;
            return this;
        }

        public Builder nextParam(String nextParam) {
            this.nextParam = nextParam;
            return this;
        }

        public Builder unsecuredResource(String method, String path) {
            this.unsecuredResources.put(method, path);
            return this;
        }

        public WebServerModule build() {
            return new WebServerModule(configsProvider, configsProviderClass, loginPage, nextParam, unsecuredResources);
        }
    }
}
