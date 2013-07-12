package com.intuso.utilities.object;

import org.junit.Ignore;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 13:35
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class TestObject extends Object<TestData, TestData, TestObject, Exception> {

    public final static ObjectFactory<TestData, TestObject, Exception> FACTORY
            = new ObjectFactory<TestData, TestObject, Exception>() {
        @Override
        public TestObject create(TestData data) throws Exception {
            return new TestObject(data);
        }
    };

    public TestObject(TestData data) {
        super(data);
    }
}
