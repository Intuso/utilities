package com.intuso.utilities.webserver.ioc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
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
    private final Set<ContextHandler> handlers;
    private final String cookieName;

    @Inject
    public ServerProvider(GuiceFilter guiceFilter,
                          Set<ConnectorProvider> connectors,
                          Set<ContextHandler> handlers,
                          @SessionCookie String cookieName) {
        this.guiceFilter = guiceFilter;
        this.connectors = connectors;
        this.handlers = handlers;
        this.cookieName = cookieName;
    }

    @Override
    public Server get() {

        // create a handler collection
        ContextHandlerCollection contexts = new ContextHandlerCollection();

        // create the handler for the api requests. Wrap it in a session handler
        {
            ServletContextHandler apiHandler = new ServletContextHandler();
            apiHandler.setContextPath("/api");
            apiHandler.addServlet(ServletHandler.Default404Servlet.class, "/");
            apiHandler.addFilter(new FilterHolder(guiceFilter), "/*", EnumSet.allOf(DispatcherType.class));
            SessionHandler sessionHandler = new SessionHandler();
            sessionHandler.setSessionCookie(cookieName);
            sessionHandler.setHttpOnly(true);
            sessionHandler.setHandler(apiHandler);
            contexts.addHandler(sessionHandler);
        }

        // add the application's own handlers
        for(Handler handler : handlers) {
            // create a session handler and wrap the app handler with it
            SessionHandler sessionHandler = new SessionHandler();
            sessionHandler.setSessionCookie(cookieName);
            sessionHandler.setHttpOnly(true);
            sessionHandler.setHandler(handler);
            contexts.addHandler(sessionHandler);
        }

        HandlerCollection root = new HandlerCollection();
        root.addHandler(contexts);
        root.addHandler(new DefaultHandler());

        // create the server and set the main handler as the session handler
        Server server = new Server();
        server.setHandler(root);

        // add the connectors
        for(ConnectorProvider connector : connectors)
            server.addConnector(connector.get(server));

        // and we're done!
        return server;
    }
}
