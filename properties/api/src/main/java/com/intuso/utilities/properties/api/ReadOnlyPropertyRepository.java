package com.intuso.utilities.properties.api;

import com.intuso.utilities.listener.ManagedCollectionFactory;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 17/02/14
 * Time: 08:57
 * To change this template use File | Settings | File Templates.
 */
public abstract class ReadOnlyPropertyRepository extends PropertyRepository {

    private final PropertyRepository parent;

    public ReadOnlyPropertyRepository(ManagedCollectionFactory managedCollectionFactory, PropertyRepository parent) {
        super(managedCollectionFactory, parent);
        this.parent = parent;
    }

    @Override
    protected final void _set(String key, String value) {
        if(parent != null)
            parent._set(key, value);
    }

    @Override
    protected final void _remove(String key) {
        if(parent != null)
            parent._remove(key);
    }
}
