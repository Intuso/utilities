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

    public TestObject(TestData data) {
        super(new TestListenersFactory(), data);
    }

    protected void createChildren() {
        for(TestData childData : getData().getChildData().values())
            if(getChild(childData.getId()) == null)
                addChild(new TestObject(childData));
    }
}
