package com.intuso.utilities.properties.api;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public final class PropertyValue implements Comparable<PropertyValue> {

    private final String sourceName;
    private final int priority;
    private final String value;

    public PropertyValue(String sourceName, int priority, String value) {
        this.sourceName = sourceName;
        this.priority = priority;
        this.value = value;
    }

    public final String getSourceName() {
        return sourceName;
    }

    public final int getPriority() {
        return priority;
    }

    public final String getValue() {
        return value;
    }

    @Override
    public final int compareTo(PropertyValue another) {
        // higher priority comes first.
        return another.priority - priority;
    }
}
