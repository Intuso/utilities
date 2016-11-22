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

    private Listeners<TestListener> listeners = new Listeners<TestListener>(new ArrayList<TestListener>());
    private TestListener listener = new TestListener();

    @Test
    public void testAddListener() {
        ListenerRegistration lr = listeners.addListener(listener);
        assertNotNull(lr);
        assertEquals(1, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testRemoveListener() {
        ListenerRegistration lr = listeners.addListener(listener);
        assertNotNull(lr);
        assertEquals(1, Lists.newArrayList(listeners).size());
        lr.removeListener();
        assertEquals(0, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testRemoveListenerMultipleTimes() {
        ListenerRegistration lr = listeners.addListener(listener);
        assertNotNull(lr);
        assertEquals(1, Lists.newArrayList(listeners).size());
        lr.removeListener();
        assertEquals(0, Lists.newArrayList(listeners).size());
        lr.removeListener();
        lr.removeListener();
        lr.removeListener();
        lr.removeListener();
        assertEquals(0, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testAddMultipleListener() {
        ListenerRegistration lr1 = listeners.addListener(listener);
        ListenerRegistration lr2 = listeners.addListener(listener);
        assertNotNull(lr1);
        assertNotNull(lr2);
        assertEquals(2, Lists.newArrayList(listeners).size());
    }

    @Test
    public void testRemoveMultipleListener() {
        ListenerRegistration lr1 = listeners.addListener(listener);
        ListenerRegistration lr2 = listeners.addListener(listener);
        assertNotNull(lr1);
        assertNotNull(lr2);
        assertEquals(2, Lists.newArrayList(listeners).size());
        lr1.removeListener();
        assertEquals(1, Lists.newArrayList(listeners).size());
        lr2.removeListener();
        assertEquals(0, Lists.newArrayList(listeners).size());
    }

    private class TestListener implements Listener {}
}
