package com.intuso.utilities.object;

import com.intuso.utilities.listener.Listener;
import com.intuso.utilities.listener.Listeners;
import com.intuso.utilities.listener.ListenersFactory;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 01/04/14
 * Time: 08:31
 * To change this template use File | Settings | File Templates.
 */
public class TestListenersFactory implements ListenersFactory {
    @Override
    public <LISTENER extends Listener> Listeners<LISTENER> create() {
        return new Listeners<LISTENER>(new CopyOnWriteArrayList<LISTENER>());
    }
}
