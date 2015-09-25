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
public class TestObject extends BaseObject<TestData, TestData, TestObject> {

    public final static ObjectFactory<TestData, TestObject> FACTORY
            = new ObjectFactory<TestData, TestObject>() {
        @Override
        public TestObject create(TestData data) {
            return new TestObject(data);
        }
    };

    public TestObject(TestData data) {
        super(new TestListenersFactory(), data);
    }
}
