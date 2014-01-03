package com.intuso.utilities.properties.api;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
public class PropertyDefinition {

    private final String key;
    private final String description;

    public PropertyDefinition(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public final String getKey() {
        return key;
    }

    public final String getDescription() {
        return description;
    }
}
