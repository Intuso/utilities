package com.intuso.utilities.collection;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: tomc
 * Date: 08/07/12
 * Time: 12:11
 * To change this template use File | Settings | File Templates.
 */
public final class ManagedCollection<T> implements Iterable<T> {

    private final Collection<T> elements;

    public ManagedCollection(Collection<T> elements) {
        this.elements = elements;
    }

    public Registration add(T element) {
        return new Registration(element);
    }

    public Iterator<T> iterator() {
        return elements.iterator();
    }

    /**
     * Created with IntelliJ IDEA.
     * User: tomc
     * Date: 07/07/12
     * Time: 18:30
     * To change this template use File | Settings | File Templates.
     */
    public final class Registration {

        private Object object;

        private Registration(T element) {
            this.object = element;
            elements.add(element);
        }

        public final void remove() {
            elements.remove(object);
        }
    }
}
