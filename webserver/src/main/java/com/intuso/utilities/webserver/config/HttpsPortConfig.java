package com.intuso.utilities.webserver.config;

import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Created by tomc on 28/01/15.
 */
public class HttpsPortConfig extends HttpPortConfig {

    private SslContextFactory sslContextFactory;

    public HttpsPortConfig() {}

    public HttpsPortConfig(String host, int port, SslContextFactory sslContextFactory) {
        super(host, port);
        this.sslContextFactory = sslContextFactory;
    }

    public String getType() {
        return "https";
    }

    public SslContextFactory getSslContextFactory() {
        return sslContextFactory;
    }

    public void setSslContextFactory(SslContextFactory sslContextFactory) {
        this.sslContextFactory = sslContextFactory;
    }
}
