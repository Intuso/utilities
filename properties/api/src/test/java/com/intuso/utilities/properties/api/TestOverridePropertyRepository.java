package com.intuso.utilities.properties.api;

import org.junit.Test;

import java.util.HashMap;
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

        Map<String, String> originalValues = new HashMap<String, String>();
        originalValues.put("key1", "value1");
        originalValues.put("key2", "value2");
        WriteableMapPropertyRepository originalRepository = new WriteableMapPropertyRepository(new TestManagedCollectionFactory(), originalValues);

        Map<String, String> overridenValues = new HashMap<String, String>();
        overridenValues.put("key1", "overridenValue1");
        overridenValues.put("key3", "overridenValue3");
        WriteableMapPropertyRepository overridenRepository = new WriteableMapPropertyRepository(new TestManagedCollectionFactory(), originalRepository, overridenValues);

        assertEquals(3, overridenRepository.keySet().size());
        assertTrue(overridenRepository.keySet().contains("key1"));
        assertTrue(overridenRepository.keySet().contains("key2"));
        assertTrue(overridenRepository.keySet().contains("key3"));
        assertEquals("overridenValue1", overridenRepository.get("key1"));
        assertEquals("value2", overridenRepository.get("key2"));
        assertEquals("overridenValue3", overridenRepository.get("key3"));
    }
}
