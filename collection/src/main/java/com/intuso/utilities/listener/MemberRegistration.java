package com.intuso.utilities.listener;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 07/07/12
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public final class MemberRegistration {

    private Collection<?> collection;
    private Object object;

    <LISTENER> MemberRegistration(Collection<? super LISTENER> collection, LISTENER object) {
        this.collection = collection;
        this.object = object;
        collection.add(object);
    }

    public final void removeListener() {
        collection.remove(object);
    }
}
