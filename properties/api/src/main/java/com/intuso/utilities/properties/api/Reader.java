package com.intuso.utilities.properties.api;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 03/01/14
 * Time: 12:18
 * To change this template use File | Settings | File Templates.
 */
public abstract class Reader implements Iterable<Reader.Entry> {

    private final String sourceName;
    private final int priority;

    protected Reader(String sourceName, int priority) {
        this.sourceName = sourceName;
        this.priority = priority;
    }

    public final String getSourceName() {
        return sourceName;
    }

    public final int getPriority() {
        return priority;
    }

    public static final class Entry {

        private final String key;
        private final String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public final String getKey() {
            return key;
        }

        public final String getValue() {
            return value;
        }
    }
}
