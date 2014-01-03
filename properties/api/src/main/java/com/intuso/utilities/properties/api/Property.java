package com.intuso.utilities.properties.api;

import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public final class Property {

    private final PriorityQueue<PropertyValue> values = new PriorityQueue<PropertyValue>();

    public final void addValue(PropertyValue value) {

        // find and remove the previous value for that source, if one exists
        PropertyValue previous = null;
        for(PropertyValue existingValue : values) {
            if(existingValue.getSourceName().equals(value.getSourceName())) {
                previous = existingValue;
                break;
            }
        }
        if(previous != null)
            values.remove(previous);

        // add the new value
        values.add(value);
    }

    public final String getValue() {
        return values.size() == 0 ? null : values.peek().getValue();
    }

    public final PropertyValue getPropertyValue() {
        return values.size() == 0 ? null : values.peek();
    }
}
