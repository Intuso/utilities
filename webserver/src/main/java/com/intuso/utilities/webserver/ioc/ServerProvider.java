package com.intuso.utilities.webserver.ioc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;

import javax.servlet.DispatcherType;
import java.util.EnumSet;
import java.util.Set;

/**
 * Created by tomc on 23/01/17.
 */
public class ServerProvider implements Provider<Server> {

    private final GuiceFilter guiceFilter;
    private final Set<ConnectorProvider> connectors;

    @Inject
    public ServerProvider(GuiceFilter guiceFilter, Set<ConnectorProvider> connectors) {
        this.guiceFilter = guiceFilter;
        this.connectors = connectors;
    }

    @Override
    public Server get() {
        Server server = new Server();
        ServletContextHandler handler = new ServletContextHandler();
        handler.setContextPath("/");
        handler.addServlet(ServletHandler.Default404Servlet.class, "/");
        FilterHolder guiceFilterHolder = new FilterHolder(guiceFilter);
        handler.addFilter(guiceFilterHolder, "/*", EnumSet.allOf(DispatcherType.class));
        server.setHandler(handler);
        for(ConnectorProvider connector : connectors)
            server.addConnector(connector.get(server));
        return server;
    }
}
