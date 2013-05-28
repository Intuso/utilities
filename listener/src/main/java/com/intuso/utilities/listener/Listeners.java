package com.intuso.utilities.listener;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public final class Listeners<L extends Listener> implements Iterable<L> {

    private Set<L> listeners = new HashSet<L>();

    public <RL extends L> ListenerRegistration<RL> addListener(RL listener) {
        return new ListenerRegistration<RL>(listeners, listener);
    }

    public Set<L> getListeners() {
        // return as a new set so that listeners can remove themselves as they're called without causing a
        // concurrent modification exception
        return new HashSet<L>(listeners);
    }

    @Override

    public Iterator<L> iterator() {
        return getListeners().iterator();
    }
}
