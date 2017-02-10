package com.intuso.utilities.webserver.ioc;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;

/**
 * Created by tomc on 03/06/16.
 */
public interface ConnectorProvider {
    Connector get(Server server);
}
