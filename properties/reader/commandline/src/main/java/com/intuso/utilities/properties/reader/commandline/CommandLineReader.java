package com.intuso.utilities.properties.reader.commandline;

import com.google.common.collect.Lists;
import com.intuso.utilities.properties.api.Reader;

import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public final class CommandLineReader extends Reader {

    public final List<Entry> entries = Lists.newArrayList();

    public CommandLineReader(String sourceName, int priority, String[] args) {
        super(sourceName, priority);

        if(args.length % 2 == 1)
            throw new IllegalArgumentException("Odd number of arguments to parse - must be even");

        for(int i = 0; i < args.length; i+=2) {
            if(!args[i].startsWith("-"))
                throw new IllegalArgumentException("Property name must start with \"-\"");
            entries.add(new Entry(args[i].substring(1), args[i + 1]));
        }
    }

    @Override
    public final Iterator<Entry> iterator() {
        return entries.iterator();
    }
}
