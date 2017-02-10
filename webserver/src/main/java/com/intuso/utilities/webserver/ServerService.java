package com.intuso.utilities.webserver;

import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;
import org.eclipse.jetty.server.Server;

/**
 * Created by tomc on 23/01/17.
 */
public class ServerService extends AbstractIdleService {

    private final Server server;

    @Inject
    public ServerService(Server server) {
        this.server = server;
    }

    @Override
    protected void startUp() throws Exception {
        server.start();
    }

    @Override
    protected void shutDown() throws Exception {
        server.stop();
    }
}
