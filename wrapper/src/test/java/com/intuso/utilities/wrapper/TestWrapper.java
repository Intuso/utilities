package com.intuso.utilities.wrapper;

import org.junit.Ignore;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class TestWrapper extends Wrapper<TestWrappable, TestWrappable, TestWrapper, Exception> {

    public final static WrapperFactory<TestWrappable, TestWrapper, Exception> FACTORY
            = new WrapperFactory<TestWrappable, TestWrapper, Exception>() {
        @Override
        public TestWrapper create(TestWrappable wrappable) throws Exception {
            return new TestWrapper(wrappable);
        }
    };

    public TestWrapper(TestWrappable wrappable) {
        super(wrappable);
    }
}
