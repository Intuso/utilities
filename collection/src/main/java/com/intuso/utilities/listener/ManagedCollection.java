package com.intuso.utilities.listener;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 08/07/12
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public final class ManagedCollection<LISTENER> implements Iterable<LISTENER> {

    private final List<LISTENER> listeners;

    public ManagedCollection(List<LISTENER> listeners) {
        this.listeners = listeners;
    }

    public MemberRegistration add(LISTENER listener) {
        return new MemberRegistration(listeners, listener);
    }

    public Iterator<LISTENER> iterator() {
        return listeners.iterator();
    }
}
