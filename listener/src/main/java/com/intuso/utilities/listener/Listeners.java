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

    private List<L> listeners = new ArrayList<L>();
    private List<L> unmodifiableListeners = Collections.unmodifiableList(listeners);

    public ListenerRegistration addListener(L listener) {
        return new ListenerRegistration(listeners, listener);
    }

    public List<L> getListeners() {
        return unmodifiableListeners;
    }

    @Override

    public Iterator<L> iterator() {
        return getListeners().iterator();
    }
}
