package com.intuso.utilities.webserver.ioc;

import com.google.inject.Provider;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Created by Ravn Systems
 * User: wills
 * Date: 1/17/14
 * Time: 10:35 AM
 */
public class HTTPSConnectorProvider implements ConnectorProvider {

    private final String host;
    private final int port;
    private final Provider<SslContextFactory> sslContextFactoryProvider;

    public HTTPSConnectorProvider(String host, int port, Provider<SslContextFactory> sslContextFactoryProvider) {
        this.host = host;
        this.port = port;
        this.sslContextFactoryProvider = sslContextFactoryProvider;
    }

    @Override
    public Connector get(Server server) {

        // configure ssl stuff
        ConnectionFactory sslConnectionFactory = new SslConnectionFactory(sslContextFactoryProvider.get(), org.eclipse.jetty.http.HttpVersion.HTTP_1_1.toString());
        HttpConfiguration config = new HttpConfiguration();
        config.setSecureScheme("https");
        config.setSecurePort(port);
        HttpConfiguration sslConfiguration = new HttpConfiguration(config);
        sslConfiguration.addCustomizer(new SecureRequestCustomizer());
        ConnectionFactory httpConnectionFactory = new HttpConnectionFactory(sslConfiguration);

        // create the connector
        ServerConnector connector = new ServerConnector(server, sslConnectionFactory, httpConnectionFactory);

        // configure the host and port
        if (host != null && host.length() > 0)
            connector.setHost(host);
        connector.setPort(port);

        return connector;
    }
}
