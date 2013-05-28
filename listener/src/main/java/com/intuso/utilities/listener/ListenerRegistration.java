package com.intuso.utilities.listener;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 07/07/12
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class ListenerRegistration<L extends Listener> {

    private Set<? super L> listeners;
    private L listener;

    public ListenerRegistration(Set<? super L> listeners, L listener) {
        this.listeners = listeners;
        this.listener = listener;
        listeners.add(listener);
    }

    public final void removeListener() {
        listeners.remove(listener);
    }
}
