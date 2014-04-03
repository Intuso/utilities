package com.intuso.utilities.properties.api;

import com.intuso.utilities.listener.Listener;

public interface PropertyValueChangeListener extends Listener {
    public void propertyValueChanged(String key, String oldValue, String newValue);
}
