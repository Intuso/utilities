package com.intuso.utilities.properties.api;

import com.intuso.utilities.listener.ManagedCollection;
import com.intuso.utilities.listener.ManagedCollectionFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 01/04/14
 * Time: 08:31
 * To change this template use File | Settings | File Templates.
 */
public class TestManagedCollectionFactory implements ManagedCollectionFactory {
    @Override
    public <LISTENER> ManagedCollection<LISTENER> create() {
        return new ManagedCollection<LISTENER>(new CopyOnWriteArrayList<LISTENER>());
    }
}
