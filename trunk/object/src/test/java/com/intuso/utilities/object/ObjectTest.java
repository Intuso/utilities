package com.intuso.utilities.object;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class ObjectTest {

    @Test
    public void testData() {
        TestData data = new TestData("id", "randomValue");
        assertEquals("randomValue", data.getRandomValue());
    }

    @Test
    public void testCreateObject() throws Exception {
        TestData data = new TestData("id", "randomValue");
        TestObject object = new TestObject(data);
        object.createChildren(TestObject.FACTORY);
        assertNotNull(object.getData());
        assertEquals(data, object.getData());
        assertEquals("randomValue", object.getData().getRandomValue());
        assertEquals("id", object.getId());
    }

    @Test
    public void testCreateObjectTree() throws Exception {
        TestData dataAA = new TestData("AA");
        TestData dataA = new TestData("A", dataAA);
        TestData dataB = new TestData("B");
        TestData data = new TestData("root", dataA, dataB);
        TestObject object = new TestObject(data);
        object.createChildren(TestObject.FACTORY);
        assertEquals(data, object.getData());
        assertEquals(2, object.getChildren().size());
        for(BaseObject w : object.getChildren())
            w.createChildren(TestObject.FACTORY);
        assertNotNull(object.getChild("A"));
        assertEquals(dataA, object.getChild("A").getData());
        assertEquals(1, object.getChild("A").getChildren().size());
        assertNotNull(object.getChild("B"));
        assertEquals(dataB, object.getChild("B").getData());
        assertEquals(0, object.getChild("B").getChildren().size());
        assertNotNull(object.getChild("A").getChild("AA"));
        assertEquals(dataAA, object.getChild("A").getChild("AA").getData());
        assertEquals(0, object.getChild("A").getChild("AA").getChildren().size());
    }

    @Test
    public void testAddObjects() throws Exception {
        TestData data = new TestData("root");
        TestData dataA = new TestData("A");
        TestData dataAA = new TestData("AA");
        TestData dataB = new TestData("B");
        TestObject object = new TestObject(data);
        object.createChildren(TestObject.FACTORY);
        TestObject objectA = new TestObject(dataA);
        objectA.createChildren(TestObject.FACTORY);
        TestObject objectAA = new TestObject(dataAA);
        objectAA.createChildren(TestObject.FACTORY);
        TestObject objectB = new TestObject(dataB);
        objectB.createChildren(TestObject.FACTORY);
        objectA.addChild(objectAA);
        object.addChild(objectA);
        object.addChild(objectB);
        assertEquals(data, object.getData());
        assertEquals(2, object.getChildren().size());
        assertNotNull(object.getChild("A"));
        assertEquals(dataA, object.getChild("A").getData());
        assertEquals(1, object.getChild("A").getChildren().size());
        assertNotNull(object.getChild("B"));
        assertEquals(dataB, object.getChild("B").getData());
        assertEquals(0, object.getChild("B").getChildren().size());
        assertNotNull(object.getChild("A").getChild("AA"));
        assertEquals(dataAA, object.getChild("A").getChild("AA").getData());
        assertEquals(0, object.getChild("A").getChild("AA").getChildren().size());
    }

    @Test
    public void testRemoveObject() throws Exception {
        TestData dataAA = new TestData("AA");
        TestData dataA = new TestData("A", dataAA);
        TestData dataB = new TestData("B");
        TestData data = new TestData("root", dataA, dataB);
        TestObject object = new TestObject(data);
        object.createChildren(TestObject.FACTORY);
        assertEquals(data, object.getData());
        assertEquals(2, object.getChildren().size());
        for(BaseObject child : object.getChildren())
            child.createChildren(TestObject.FACTORY);
        assertNotNull(object.getChild("A"));
        assertEquals(dataA, object.getChild("A").getData());
        assertEquals(1, object.getChild("A").getChildren().size());
        assertNotNull(object.getChild("B"));
        assertEquals(dataB, object.getChild("B").getData());
        assertEquals(0, object.getChild("B").getChildren().size());
        assertNotNull(object.getChild("A").getChild("AA"));
        assertEquals(dataAA, object.getChild("A").getChild("AA").getData());
        assertEquals(0, object.getChild("A").getChild("AA").getChildren().size());
        object.getChild("A").removeChild("AA");
        assertEquals(0, object.getChild("A").getChildren().size());
        assertNull(object.getChild("A").getChild("AA"));
    }
}
