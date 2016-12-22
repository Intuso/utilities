package com.intuso.utilities.listener;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 07/07/12
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public final class ListenerRegistration {

    private List<?> listeners;
    private Object listener;

    <LISTENER> ListenerRegistration(List<? super LISTENER> listeners, LISTENER listener) {
        this.listeners = listeners;
        this.listener = listener;
        listeners.add(listener);
    }

    public final void removeListener() {
        listeners.remove(listener);
    }
}
