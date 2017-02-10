package com.intuso.utilities.webserver.config;

/**
 * Created by tomc on 28/01/15.
 */
public class HttpPortConfig implements PortConfig {

    private String host;
    private int port;
    private String id;

    public HttpPortConfig() {}

    public HttpPortConfig(String host, int port) {
        this.host = host;
        this.port = port;
        calculateId();
    }

    private void calculateId() {
        id = getType() + ":" + host + ":" + port;
    }

    public String getType() {
        return "http";
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
        calculateId();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
        calculateId();
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof HttpPortConfig && ((HttpPortConfig) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        if(id == null) // NB should never happen but some deserialisers don't call constructors or setters
            calculateId();
        return id.hashCode();
    }
}
