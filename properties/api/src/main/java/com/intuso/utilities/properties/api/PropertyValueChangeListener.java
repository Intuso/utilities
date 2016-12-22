package com.intuso.utilities.properties.api;

public interface PropertyValueChangeListener {
    void propertyValueChanged(String key, String oldValue, String newValue);
}
