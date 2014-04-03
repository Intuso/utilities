package com.intuso.utilities.listener;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public final class Listeners<L extends Listener> implements Iterable<L> {

    private final List<L> listeners;

    public Listeners(List<L> listeners) {
        this.listeners = listeners;
    }

    public ListenerRegistration addListener(L listener) {
        return new ListenerRegistration(listeners, listener);
    }

    public Iterator<L> iterator() {
        return listeners.iterator();
    }
}
