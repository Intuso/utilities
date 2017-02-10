package com.intuso.utilities.webserver.ioc;

import org.eclipse.jetty.server.*;

/**
 * Created by Ravn Systems
 * User: wills
 * Date: 9/18/13
 * Time: 3:09 PM
 */
public class HTTPConnectorProvider implements ConnectorProvider {

    private final String host;
    private final int port;

    public HTTPConnectorProvider(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public Connector get(Server server) {

        // create the connector
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(new HttpConfiguration()));

        // configure the host and port
        if (host != null && host.length() > 0)
            connector.setHost(host);
        connector.setPort(port);

        return connector;
    }
}
