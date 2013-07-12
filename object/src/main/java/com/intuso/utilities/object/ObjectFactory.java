package com.intuso.utilities.object;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 03/07/12
 * Time: 22:17
 * To change this template use File | Settings | File Templates.
 */
public interface ObjectFactory<DATA extends Data<?>, O extends Object<? extends DATA, ?, ?, ?>,
        E extends Exception> {
    public O create(DATA data) throws E;
}
