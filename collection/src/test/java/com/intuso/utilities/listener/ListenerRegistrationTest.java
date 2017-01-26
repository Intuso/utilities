package com.intuso.utilities.listener;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 08/07/12
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public class ListenerRegistrationTest {

    private ManagedCollection<TestListener> listeners = new ManagedCollection<TestListener>(new ArrayList<TestListener>());
    private TestListener listener = new TestListener();

    @Test
    public void testAddListener() {
        ManagedCollection.Registration lr = listeners.add(listener);
        assertNotNull(lr);
        assertEquals(1, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testRemoveListener() {
        ManagedCollection.Registration lr = listeners.add(listener);
        assertNotNull(lr);
        assertEquals(1, Lists.newArrayList(listeners).size());
        lr.remove();
        assertEquals(0, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testRemoveListenerMultipleTimes() {
        ManagedCollection.Registration lr = listeners.add(listener);
        assertNotNull(lr);
        assertEquals(1, Lists.newArrayList(listeners).size());
        lr.remove();
        assertEquals(0, Lists.newArrayList(listeners).size());
        lr.remove();
        lr.remove();
        lr.remove();
        lr.remove();
        assertEquals(0, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testAddMultipleListener() {
        ManagedCollection.Registration lr1 = listeners.add(listener);
        ManagedCollection.Registration lr2 = listeners.add(listener);
        assertNotNull(lr1);
        assertNotNull(lr2);
        assertEquals(2, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testRemoveMultipleListener() {
        ManagedCollection.Registration lr1 = listeners.add(listener);
        ManagedCollection.Registration lr2 = listeners.add(listener);
        assertNotNull(lr1);
        assertNotNull(lr2);
        assertEquals(2, Lists.newArrayList(listeners).size());
        lr1.remove();
        assertEquals(1, Lists.newArrayList(listeners).size());
        lr2.remove();
        assertEquals(0, Lists.newArrayList(listeners).size());
    }

    private class TestListener {}
}
