package com.intuso.utilities.webserver.ioc;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.util.Providers;
import com.intuso.utilities.webserver.config.HttpPortConfig;
import com.intuso.utilities.webserver.config.HttpsPortConfig;
import com.intuso.utilities.webserver.config.PortConfig;

import java.util.Set;

/**
 * Created by tomc on 10/02/17.
 */
public class ConnectorsProvider implements Provider<Set<ConnectorProvider>> {

    private final Set<PortConfig> configs;

    @Inject
    public ConnectorsProvider(Set<PortConfig> configs) {
        this.configs = configs;
    }

    @Override
    public Set<ConnectorProvider> get() {
        Set<ConnectorProvider> connectors = Sets.newHashSet();
        if(configs != null) {
            for (PortConfig config : configs) {
                ConnectorProvider connectorProvider = makeConnectorProvider(config);
                if(connectorProvider != null)
                    connectors.add(connectorProvider);
            }
        }
        return connectors;
    }

    private ConnectorProvider makeConnectorProvider(PortConfig config) {
        if(config == null)
            return null;
        else if(config instanceof HttpsPortConfig)
            return makeHttpsConnectorProvider((HttpsPortConfig) config);
        else if(config instanceof HttpPortConfig)
            return makeHttpConnectorProvider((HttpPortConfig) config);
        return null;
    }

    private ConnectorProvider makeHttpConnectorProvider(HttpPortConfig portConfig) {
        return new HTTPConnectorProvider(portConfig.getHost(), portConfig.getPort());
    }

    private ConnectorProvider makeHttpsConnectorProvider(HttpsPortConfig portConfig) {
        return new HTTPSConnectorProvider(portConfig.getHost(), portConfig.getPort(), Providers.of(portConfig.getSslContextFactory()));
    }
}

