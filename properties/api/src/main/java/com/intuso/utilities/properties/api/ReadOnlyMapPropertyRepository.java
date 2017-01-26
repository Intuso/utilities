package com.intuso.utilities.properties.api;

import com.intuso.utilities.listener.ManagedCollectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReadOnlyMapPropertyRepository extends ReadOnlyPropertyRepository {

    private final Map<String, String> values;

    public static ReadOnlyMapPropertyRepository newEmptyRepository(ManagedCollectionFactory managedCollectionFactory) {
        return newEmptyRepository(managedCollectionFactory, null);
    }

    public static ReadOnlyMapPropertyRepository newEmptyRepository(ManagedCollectionFactory managedCollectionFactory, PropertyRepository parent) {
        return new ReadOnlyMapPropertyRepository(managedCollectionFactory, parent, new HashMap<String, String>());
    }

    public ReadOnlyMapPropertyRepository(ManagedCollectionFactory managedCollectionFactory, Map<String, String> values) {
        this(managedCollectionFactory, null, values);
    }

    public ReadOnlyMapPropertyRepository(ManagedCollectionFactory managedCollectionFactory, PropertyRepository parent, Map<String, String> values) {
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
}
