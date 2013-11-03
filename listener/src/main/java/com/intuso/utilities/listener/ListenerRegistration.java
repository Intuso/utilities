package com.intuso.utilities.listener;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 07/07/12
 * Time: 18:30
 * To change this template use File | Settings | File Templates.
 */
public class ListenerRegistration {

    private List<?> listeners;
    private Listener listener;

    public <L extends Listener> ListenerRegistration(List<? super L> listeners, L listener) {
        this.listeners = listeners;
        this.listener = listener;
        listeners.add(listener);
    }

    public final void removeListener() {
        listeners.remove(listener);
    }
}
