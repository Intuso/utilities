package com.intuso.utilities.webserver.ioc;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.servlet.GuiceFilter;
import com.intuso.utilities.webserver.ScopedContextHandlerCollection;
import com.intuso.utilities.webserver.security.SecurityHandler;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.Rule;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
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
    private final Set<Rule> rewriteRules;
    private final String cookieName;
    private final SecurityHandler securityHandler;

    @Inject
    public ServerProvider(GuiceFilter guiceFilter,
                          Set<ConnectorProvider> connectors,
                          Set<ContextHandler> handlers,
                          Set<Rule> rewriteRules,
                          @SessionCookie String cookieName,
                          SecurityHandler securityHandler) {
        this.guiceFilter = guiceFilter;
        this.connectors = connectors;
        this.handlers = handlers;
        this.rewriteRules = rewriteRules;
        this.cookieName = cookieName;
        this.securityHandler = securityHandler;
    }

    @Override
    public Server get() {

        // create a handler collection
        ScopedContextHandlerCollection contexts = new ScopedContextHandlerCollection();

        // create the handler for the api requests. Wrap it in a session handler
        {
            ServletContextHandler apiHandler = new ServletContextHandler();
            apiHandler.setContextPath("/api");
            apiHandler.addServlet(ServletHandler.Default404Servlet.class, "/");
            apiHandler.addFilter(new FilterHolder(guiceFilter), "/*", EnumSet.allOf(DispatcherType.class));
            contexts.addHandler(apiHandler);
        }

        // add the application's own handlers
        for(ContextHandler handler : handlers)
            contexts.addHandler(handler);

        securityHandler.setHandler(contexts);

        SessionHandler sessionHandler = new SessionHandler();
        sessionHandler.setSessionCookie(cookieName);
        sessionHandler.setHttpOnly(true);
        sessionHandler.setHandler(securityHandler);

        // add any rewrite rules
        RewriteHandler rewriteHandler = new RewriteHandler();
        rewriteHandler.setRules(rewriteRules.toArray(new Rule[rewriteRules.size()]));

        HandlerCollection root = new HandlerCollection();
        root.addHandler(rewriteHandler);
        root.addHandler(sessionHandler);
        root.addHandler(new DefaultHandler());

        // create the actual server and set the main server handler as the root handler
        Server server = new Server();
        server.setHandler(root);

        // add the connectors
        for(ConnectorProvider connector : connectors)
            server.addConnector(connector.get(server));

        // and we're done!
        return server;
    }
}
