package com.intuso.utilities.object;

import com.intuso.utilities.listener.Listener;

/**
 * Created by IntelliJ IDEA.
 * User: tomc
 * Date: 28/05/12
 * Time: 00:16
 * To change this template use File | Settings | File Templates.
 */
public interface ObjectListener<O> extends Listener {
    void childObjectAdded(String childId, O child);
    void childObjectRemoved(String childId, O child);
    void ancestorObjectAdded(String ancestorPath, BaseObject<?, ?, ?> ancestor);
    void ancestorObjectRemoved(String ancestorPath, BaseObject<?, ?, ?> ancestor);
}
