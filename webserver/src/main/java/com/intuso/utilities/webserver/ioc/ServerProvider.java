package com.intuso.utilities.webserver.ioc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.session.SessionHandler;
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

        // create the program handler, in this case a guice filter and default 404 servlet
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(ServletHandler.Default404Servlet.class, "/");
        FilterHolder guiceFilterHolder = new FilterHolder(guiceFilter);
        servletContextHandler.addFilter(guiceFilterHolder, "/*", EnumSet.allOf(DispatcherType.class));

        // create the session handler and wrap the servlet context handler in it
        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setSessionCookie("INTUSO_HM_SESSION");
        sessionHandler.setHttpOnly(true);
        sessionHandler.setHandler(servletContextHandler);

        // create the server and set the main handler as the context handler
        Server server = new Server();
        server.setHandler(sessionHandler);
        for(ConnectorProvider connector : connectors)
            server.addConnector(connector.get(server));
        return server;
    }
}
