package com.intuso.utilities.properties.api;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 12:00
 * To change this template use File | Settings | File Templates.
 */
public final class PropertyContainer {

    private final Map<String, Property> values = Maps.newHashMap();

    public final void set(String key, PropertyValue value) {
        if(!values.containsKey(key))
            values.put(key, new Property());
        values.get(key).addValue(value);
    }

    public final void setAll(Map<String, Property> values) {
        this.values.putAll(values);
    }

    public final void read(Reader reader) {
        for(Reader.Entry entry : reader)
            set(entry.getKey(), new PropertyValue(reader.getSourceName(), reader.getPriority(), entry.getValue()));
    }

    public final Property getProperty(String key) {
        return values.get(key);
    }

    public final Property getProperty(PropertyDefinition propertyDefinition) {
        return values.get(propertyDefinition.getKey());
    }

    public final String get(String key) {
        Property property = values.get(key);
        return property == null ? null : property.getValue();
    }

    public final String get(PropertyDefinition propertyDefinition) {
        Property property = values.get(propertyDefinition.getKey());
        return property == null ? null : property.getValue();
    }

    public final boolean containsKey(String key) {
        return values.containsKey(key);
    }
}
