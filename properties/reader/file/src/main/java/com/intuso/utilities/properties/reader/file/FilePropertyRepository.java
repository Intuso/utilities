package com.intuso.utilities.properties.reader.file;

import com.intuso.utilities.listener.ManagedCollectionFactory;
import com.intuso.utilities.properties.api.PropertyRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public final class FilePropertyRepository extends PropertyRepository {

    private final File file;
    private final Set<String> keys = new HashSet<String>();
    private final Properties properties = new Properties();

    public FilePropertyRepository(ManagedCollectionFactory managedCollectionFactory, PropertyRepository parent, String filePath) throws IOException {
        this(managedCollectionFactory, parent, new File(filePath));
    }

    public FilePropertyRepository(ManagedCollectionFactory managedCollectionFactory, PropertyRepository parent, File file) throws IOException {
        super(managedCollectionFactory, parent);
        this.file = file;
        properties.load(new FileInputStream(file));
        for(Map.Entry<Object, Object> entry : properties.entrySet())
            if(entry.getKey() instanceof String && entry.getValue() instanceof String)
                keys.add((String) entry.getKey());
    }

    @Override
    public Set<String> _keySet() {
        return keys;
    }

    @Override
    public String _get(String key) {
        // only return things that we know are String values
        return keys.contains(key) ? (String)properties.get(key) : null;
    }

    @Override
    protected void _set(String key, String value) {
        properties.put(key, value);
        keys.add(key);
        persist();
    }

    @Override
    protected void _remove(String key) {
        properties.remove(key);
        keys.remove(key);
        persist();
    }

    private void persist() {
        try {
            properties.store(new FileOutputStream(file), "");
        } catch (IOException e) {
            throw new RuntimeException("Failed to persist properties to file", e);
        }
    }
}
