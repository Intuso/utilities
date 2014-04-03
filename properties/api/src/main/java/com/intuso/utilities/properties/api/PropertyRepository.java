package com.intuso.utilities.properties.api;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intuso.utilities.listener.ListenerRegistration;
import com.intuso.utilities.listener.Listeners;
import com.intuso.utilities.listener.ListenersFactory;

import java.util.Map;
import java.util.Set;

public abstract class PropertyRepository {

    private final ListenersFactory listenersFactory;

    private final PropertyRepository parent;
    private final Listeners<PropertyValueChangeListener> listeners;
    private final Map<String, Listeners<PropertyValueChangeListener>> keyedListeners = Maps.newHashMap();

    public PropertyRepository(ListenersFactory listenersFactory, PropertyRepository parent) {
        this.listenersFactory = listenersFactory;
        this.parent = parent;
        this.listeners = listenersFactory.create();
        if(parent != null)
            parent.addListener(new OriginalsListener());
    }

    public final ListenerRegistration addListener(PropertyValueChangeListener listener) {
        return listeners.addListener(listener);
    }

    public final ListenerRegistration addListener(String key, PropertyValueChangeListener listener) {
        if(!keyedListeners.containsKey(key))
            keyedListeners.put(key, listenersFactory.<PropertyValueChangeListener>create());
        return keyedListeners.get(key).addListener(listener);
    }

    protected final void notifyListeners(String key, String oldValue, String newValue) {
        // only notify if it has actually changed
        if(oldValue == null && newValue == null
                || (oldValue != null && oldValue.equals(newValue)))
            return;
        for(PropertyValueChangeListener listener : listeners)
            listener.propertyValueChanged(key, oldValue, newValue);
        if(keyedListeners.containsKey(key))
            for(PropertyValueChangeListener listener : keyedListeners.get(key))
                listener.propertyValueChanged(key, oldValue, newValue);
    }

    public final Set<String> keySet() {
        Set<String> result = Sets.newHashSet(_keySet());
        if(parent != null)
            result.addAll(parent.keySet());
        return result;
    }

    public final String get(String key) {
        String value = _get(key);
        if(value != null)
            return value;
        else if(parent != null)
            return parent.get(key);
        return null;
    }

    public final void set(String key, String value) {
        String oldValue = get(key);
        _set(key, value);
        notifyListeners(key, oldValue, get(key));
    }

    public final void remove(String key) {
        if(keySet().contains(key)) {
            String oldValue = get(key);
            _remove(key);
            notifyListeners(key, oldValue, get(key));
        } else if(parent != null) {
            parent.remove(key);
        }
    }

    protected abstract Set<String> _keySet();
    protected abstract String _get(String key);
    protected abstract void _set(String key, String value);
    protected abstract void _remove(String key);

    private class OriginalsListener implements PropertyValueChangeListener {
        public final void propertyValueChanged(String key, String oldValue, String newValue) {
            // if this repository has the same key, then the value is overriden so this change doesn't matter
            if(!_keySet().contains(key))
                notifyListeners(key, oldValue, newValue);
        }
    }
}
