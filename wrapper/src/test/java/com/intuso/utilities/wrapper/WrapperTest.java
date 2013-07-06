package com.intuso.utilities.wrapper;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 13:41
 * To change this template use File | Settings | File Templates.
 */
public class WrapperTest {

    @Test
    public void testWrappable() {
        TestData wrappable = new TestData("id", "randomValue");
        assertEquals("randomValue", wrappable.getRandomValue());
    }

    @Test
    public void testWrapWrappable() throws Exception {
        TestData wrappable = new TestData("id", "randomValue");
        TestWrapper wrapper = new TestWrapper(wrappable);
        wrapper.unwrapChildren(TestWrapper.FACTORY);
        assertNotNull(wrapper.getData());
        assertEquals(wrappable, wrapper.getData());
        assertEquals("randomValue", wrapper.getData().getRandomValue());
        assertEquals("id", wrapper.getId());
    }

    @Test
    public void testWrapWrappableTree() throws Exception {
        TestData wrappableAA = new TestData("AA");
        TestData wrappableA = new TestData("A", wrappableAA);
        TestData wrappableB = new TestData("B");
        TestData wrappable = new TestData("root", wrappableA, wrappableB);
        TestWrapper wrapper = new TestWrapper(wrappable);
        wrapper.unwrapChildren(TestWrapper.FACTORY);
        assertEquals(wrappable, wrapper.getData());
        assertEquals(2, wrapper.getWrappers().size());
        for(Wrapper w : wrapper.getWrappers())
            w.unwrapChildren(TestWrapper.FACTORY);
        assertNotNull(wrapper.getWrapper("A"));
        assertEquals(wrappableA, wrapper.getWrapper("A").getData());
        assertEquals(1, wrapper.getWrapper("A").getWrappers().size());
        assertNotNull(wrapper.getWrapper("B"));
        assertEquals(wrappableB, wrapper.getWrapper("B").getData());
        assertEquals(0, wrapper.getWrapper("B").getWrappers().size());
        assertNotNull(wrapper.getWrapper("A").getWrapper("AA"));
        assertEquals(wrappableAA, wrapper.getWrapper("A").getWrapper("AA").getData());
        assertEquals(0, wrapper.getWrapper("A").getWrapper("AA").getWrappers().size());
    }

    @Test
    public void testAddWrappers() throws Exception {
        TestData wrappable = new TestData("root");
        TestData wrappableA = new TestData("A");
        TestData wrappableAA = new TestData("AA");
        TestData wrappableB = new TestData("B");
        TestWrapper wrapper = new TestWrapper(wrappable);
        wrapper.unwrapChildren(TestWrapper.FACTORY);
        TestWrapper wrapperA = new TestWrapper(wrappableA);
        wrapperA.unwrapChildren(TestWrapper.FACTORY);
        TestWrapper wrapperAA = new TestWrapper(wrappableAA);
        wrapperAA.unwrapChildren(TestWrapper.FACTORY);
        TestWrapper wrapperB = new TestWrapper(wrappableB);
        wrapperB.unwrapChildren(TestWrapper.FACTORY);
        wrapperA.addWrapper(wrapperAA);
        wrapper.addWrapper(wrapperA);
        wrapper.addWrapper(wrapperB);
        assertEquals(wrappable, wrapper.getData());
        assertEquals(2, wrapper.getWrappers().size());
        assertNotNull(wrapper.getWrapper("A"));
        assertEquals(wrappableA, wrapper.getWrapper("A").getData());
        assertEquals(1, wrapper.getWrapper("A").getWrappers().size());
        assertNotNull(wrapper.getWrapper("B"));
        assertEquals(wrappableB, wrapper.getWrapper("B").getData());
        assertEquals(0, wrapper.getWrapper("B").getWrappers().size());
        assertNotNull(wrapper.getWrapper("A").getWrapper("AA"));
        assertEquals(wrappableAA, wrapper.getWrapper("A").getWrapper("AA").getData());
        assertEquals(0, wrapper.getWrapper("A").getWrapper("AA").getWrappers().size());
    }

    @Test
    public void testRemoveWrapper() throws Exception {
        TestData wrappableAA = new TestData("AA");
        TestData wrappableA = new TestData("A", wrappableAA);
        TestData wrappableB = new TestData("B");
        TestData wrappable = new TestData("root", wrappableA, wrappableB);
        TestWrapper wrapper = new TestWrapper(wrappable);
        wrapper.unwrapChildren(TestWrapper.FACTORY);
        assertEquals(wrappable, wrapper.getData());
        assertEquals(2, wrapper.getWrappers().size());
        for(Wrapper w : wrapper.getWrappers())
            w.unwrapChildren(TestWrapper.FACTORY);
        assertNotNull(wrapper.getWrapper("A"));
        assertEquals(wrappableA, wrapper.getWrapper("A").getData());
        assertEquals(1, wrapper.getWrapper("A").getWrappers().size());
        assertNotNull(wrapper.getWrapper("B"));
        assertEquals(wrappableB, wrapper.getWrapper("B").getData());
        assertEquals(0, wrapper.getWrapper("B").getWrappers().size());
        assertNotNull(wrapper.getWrapper("A").getWrapper("AA"));
        assertEquals(wrappableAA, wrapper.getWrapper("A").getWrapper("AA").getData());
        assertEquals(0, wrapper.getWrapper("A").getWrapper("AA").getWrappers().size());
        wrapper.getWrapper("A").removeWrapper("AA");
        assertEquals(0, wrapper.getWrapper("A").getWrappers().size());
        assertNull(wrapper.getWrapper("A").getWrapper("AA"));
    }
}
