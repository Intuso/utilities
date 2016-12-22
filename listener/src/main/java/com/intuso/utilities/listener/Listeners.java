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
public final class Listeners<LISTENER> implements Iterable<LISTENER> {

    private final List<LISTENER> listeners;

    public Listeners(List<LISTENER> listeners) {
        this.listeners = listeners;
    }

    public ListenerRegistration addListener(LISTENER listener) {
        return new ListenerRegistration(listeners, listener);
    }

    public Iterator<LISTENER> iterator() {
        return listeners.iterator();
    }
}
