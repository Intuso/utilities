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
 * Time: 12:10
 * To change this template use File | Settings | File Templates.
 */
public class WriteableMapPropertyRepositoryTest {

    @Test
    public void testMapRepository() {

        Map<String, String> values = Maps.newHashMap();
        values.put("key1", "value1");
        values.put("key2", "value2");

        WriteableMapPropertyRepository propertyRepository = new WriteableMapPropertyRepository(new TestListenersFactory(), values);

        assertEquals(2, propertyRepository.keySet().size());
        assertTrue(propertyRepository.keySet().contains("key1"));
        assertTrue(propertyRepository.keySet().contains("key2"));
        assertEquals("value1", propertyRepository.get("key1"));
        assertEquals("value2", propertyRepository.get("key2"));
    }
}
