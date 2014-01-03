package com.intuso.utilities.properties.reader.file;

import com.google.common.collect.Lists;
import com.intuso.utilities.properties.api.Reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 15:07
 * To change this template use File | Settings | File Templates.
 */
public final class FileReader extends Reader {

    private final List<Entry> entries = Lists.newArrayList();

    public FileReader(String sourceName, int priority, String filePath) throws IOException {
        this(sourceName, priority, new File(filePath));
    }

    public FileReader(String sourceName, int priority, File file) throws IOException {
        super(sourceName, priority);
        Properties fileProps = new Properties();
        fileProps.load(new FileInputStream(file));
        for(String key : fileProps.stringPropertyNames())
            entries.add(new Entry(key, fileProps.getProperty(key)));
    }

    @Override
    public final Iterator<Entry> iterator() {
        return entries.iterator();
    }
}
