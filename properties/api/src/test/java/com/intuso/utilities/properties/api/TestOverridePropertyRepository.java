package com.intuso.utilities.properties.api;

import com.google.common.collect.Maps;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 25/01/14
 * Time: 12:13
 * To change this template use File | Settings | File Templates.
 */
public class TestOverridePropertyRepository {

    @Test
    public void testOverrideValues() {

        Map<String, String> originalValues = Maps.newHashMap();
        originalValues.put("key1", "value1");
        originalValues.put("key2", "value2");
        WriteableMapPropertyRepository originalRepository = new WriteableMapPropertyRepository(new TestListenersFactory(), originalValues);

        Map<String, String> overridenValues = Maps.newHashMap();
        overridenValues.put("key1", "overridenValue1");
        overridenValues.put("key3", "overridenValue3");
        WriteableMapPropertyRepository overridenRepository = new WriteableMapPropertyRepository(new TestListenersFactory(), originalRepository, overridenValues);

        assertEquals(3, overridenRepository.keySet().size());
        assertTrue(overridenRepository.keySet().contains("key1"));
        assertTrue(overridenRepository.keySet().contains("key2"));
        assertTrue(overridenRepository.keySet().contains("key3"));
        assertEquals("overridenValue1", overridenRepository.get("key1"));
        assertEquals("value2", overridenRepository.get("key2"));
        assertEquals("overridenValue3", overridenRepository.get("key3"));
    }
}
