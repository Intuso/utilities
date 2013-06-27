package com.intuso.utilities.wrapper;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 03/07/12
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public interface WrapperFactory<WBL extends Data<?>, WR extends Wrapper<? extends WBL, ?, ?, ?>,
        E extends Exception> {
    public WR create(WBL wrappable) throws E;
}
