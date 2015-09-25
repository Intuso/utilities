package com.intuso.utilities.properties.api;

import com.intuso.utilities.listener.ListenersFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReadOnlyMapPropertyRepository extends ReadOnlyPropertyRepository {

    private final Map<String, String> values;

    public static ReadOnlyMapPropertyRepository newEmptyRepository(ListenersFactory listenersFactory) {
        return newEmptyRepository(listenersFactory, null);
    }

    public static ReadOnlyMapPropertyRepository newEmptyRepository(ListenersFactory listenersFactory, PropertyRepository parent) {
        return new ReadOnlyMapPropertyRepository(listenersFactory, parent, new HashMap<String, String>());
    }

    public ReadOnlyMapPropertyRepository(ListenersFactory listenersFactory, Map<String, String> values) {
        this(listenersFactory, null, values);
    }

    public ReadOnlyMapPropertyRepository(ListenersFactory listenersFactory, PropertyRepository parent, Map<String, String> values) {
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
}
