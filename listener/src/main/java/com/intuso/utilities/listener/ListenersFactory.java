package com.intuso.utilities.listener;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 01/04/14
 * Time: 08:18
 * To change this template use File | Settings | File Templates.
 */
public interface ListenersFactory {
    <LISTENER> Listeners<LISTENER> create();
}
