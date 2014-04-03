package com.intuso.utilities.properties.reader.commandline;

import com.google.common.collect.Maps;
import com.intuso.utilities.listener.ListenersFactory;
import com.intuso.utilities.properties.api.PropertyRepository;
import com.intuso.utilities.properties.api.ReadOnlyPropertyRepository;

import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public final class CommandLinePropertyRepository extends ReadOnlyPropertyRepository {

    private final Map<String, String> values = Maps.newHashMap();

    public CommandLinePropertyRepository(ListenersFactory listenersFactory, PropertyRepository parent, String[] args) {
        super(listenersFactory, parent);
        if(args.length % 2 == 1)
            throw new IllegalArgumentException("Odd number of arguments to parse - must be even");

        for(int i = 0; i < args.length; i+=2) {
            if(!args[i].startsWith("-"))
                throw new IllegalArgumentException("Property name must start with \"-\"");
            values.put(args[i].substring(1), args[i + 1]);
        }
    }

    @Override
    public Set<String> _keySet() {
        return values.keySet();
    }

    @Override
    public String _get(String key) {
        return values.get(key);
    }
}
