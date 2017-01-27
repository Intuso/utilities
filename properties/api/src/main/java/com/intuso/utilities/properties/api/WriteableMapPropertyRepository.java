package com.intuso.utilities.properties.api;

import com.intuso.utilities.collection.ManagedCollectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WriteableMapPropertyRepository extends PropertyRepository {

    private final Map<String, String> values;

    public static WriteableMapPropertyRepository newEmptyRepository(ManagedCollectionFactory managedCollectionFactory) {
        return newEmptyRepository(managedCollectionFactory, null);
    }

    public static WriteableMapPropertyRepository newEmptyRepository(ManagedCollectionFactory managedCollectionFactory, PropertyRepository parent) {
        return new WriteableMapPropertyRepository(managedCollectionFactory, parent, new HashMap<String, String>());
    }

    public WriteableMapPropertyRepository(ManagedCollectionFactory managedCollectionFactory, Map<String, String> values) {
        this(managedCollectionFactory, null, values);
    }

    public WriteableMapPropertyRepository(ManagedCollectionFactory managedCollectionFactory, PropertyRepository parent, Map<String, String> values) {
        super(managedCollectionFactory, parent);
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
