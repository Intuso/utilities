package com.intuso.utilities.listener;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: ravnroot
 * Date: 08/07/12
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public class ListenerRegistrationTest {

    private Listeners<TestListener> listeners = new Listeners<TestListener>();
    private TestListener listener = new TestListener();

    @Test
    public void testAddListener() {
        ListenerRegistration lr = listeners.addListener(listener);
        assertNotNull(lr);
        assertEquals(1, listeners.getListeners().size());
    }

    @Test
    public void testRemoveListener() {
        ListenerRegistration lr = listeners.addListener(listener);
        assertNotNull(lr);
        assertEquals(1, listeners.getListeners().size());
        lr.removeListener();
        assertEquals(0, listeners.getListeners().size());
    }

    @Test
    public void testRemoveListenerMultipleTimes() {
        ListenerRegistration lr = listeners.addListener(listener);
        assertNotNull(lr);
        assertEquals(1, listeners.getListeners().size());
        lr.removeListener();
        assertEquals(0, listeners.getListeners().size());
        lr.removeListener();
        lr.removeListener();
        lr.removeListener();
        lr.removeListener();
        assertEquals(0, listeners.getListeners().size());
    }

    @Test
    public void testAddMultipleListener() {
        ListenerRegistration lr1 = listeners.addListener(listener);
        ListenerRegistration lr2 = listeners.addListener(listener);
        assertNotNull(lr1);
        assertNotNull(lr2);
        assertEquals(1, listeners.getListeners().size());
    }

    @Test
    public void testRemoveMultipleListener() {
        ListenerRegistration lr1 = listeners.addListener(listener);
        ListenerRegistration lr2 = listeners.addListener(listener);
        assertNotNull(lr1);
        assertNotNull(lr2);
        assertEquals(1, listeners.getListeners().size());
        lr1.removeListener();
        assertEquals(0, listeners.getListeners().size());
        lr2.removeListener();
        assertEquals(0, listeners.getListeners().size());
    }

    private class TestListener implements Listener {}
}
