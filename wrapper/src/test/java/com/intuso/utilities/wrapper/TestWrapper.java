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
public class TestWrapper extends Wrapper<TestData, TestData, TestWrapper, Exception> {

    public final static WrapperFactory<TestData, TestWrapper, Exception> FACTORY
            = new WrapperFactory<TestData, TestWrapper, Exception>() {
        @Override
        public TestWrapper create(TestData wrappable) throws Exception {
            return new TestWrapper(wrappable);
        }
    };

    public TestWrapper(TestData wrappable) {
        super(wrappable);
    }
}
