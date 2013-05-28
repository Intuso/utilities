package com.intuso.utilities.wrapper;

import com.intuso.utilities.listener.Listener;

/**
 * Created by IntelliJ IDEA.
 * User: tomc
 * Date: 28/05/12
 * Time: 00:16
 * To change this template use File | Settings | File Templates.
 */
public interface WrapperListener<WR> extends Listener {
    void childWrapperAdded(String childId, WR wrapper);
    void childWrapperRemoved(String childId, WR wrapper);
    void ancestorAdded(String ancestorPath, Wrapper<?, ?, ?, ?> wrapper);
    void ancestorRemoved(String ancestorPath, Wrapper<?, ?, ?, ?> wrapper);
}
