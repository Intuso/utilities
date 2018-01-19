package com.intuso.utilities.properties.api;

import com.intuso.utilities.collection.ManagedCollection;
import com.intuso.utilities.collection.ManagedCollectionFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 01/04/14
 * Time: 08:31
 * To change this template use File | Settings | File Templates.
 */
public class TestManagedCollectionFactory implements ManagedCollectionFactory {

    @Override
    public <LISTENER> ManagedCollection<LISTENER> createSet() {
        return new ManagedCollection<>(Collections.synchronizedSet(new HashSet<LISTENER>()));
    }

    @Override
    public <LISTENER> ManagedCollection<LISTENER> createList() {
        return new ManagedCollection<>(Collections.synchronizedList(new LinkedList<LISTENER>()));
    }
}
