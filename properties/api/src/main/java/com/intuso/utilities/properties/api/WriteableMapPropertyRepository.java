package com.intuso.utilities.properties.api;

import com.intuso.utilities.listener.ListenersFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WriteableMapPropertyRepository extends PropertyRepository {

    private final Map<String, String> values;

    public static WriteableMapPropertyRepository newEmptyRepository(ListenersFactory listenersFactory) {
        return newEmptyRepository(listenersFactory, null);
    }

    public static WriteableMapPropertyRepository newEmptyRepository(ListenersFactory listenersFactory, PropertyRepository parent) {
        return new WriteableMapPropertyRepository(listenersFactory, parent, new HashMap<String, String>());
    }

    public WriteableMapPropertyRepository(ListenersFactory listenersFactory, Map<String, String> values) {
        this(listenersFactory, null, values);
    }

    public WriteableMapPropertyRepository(ListenersFactory listenersFactory, PropertyRepository parent, Map<String, String> values) {
        super(listenersFactory, parent);
        this.values = values;
    }

    @Override
    protected final Set<String> _keySet() {
        return values.keySet();
    }

    @Override
    protected final String _get(String key) {
        return values.get(key);
    }

    @Override
    protected final void _set(String key, String value) {
        values.put(key, value);
    }

    @Override
    protected final void _remove(String key) {
        values.remove(key);
    }
}
