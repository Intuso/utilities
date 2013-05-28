package com.intuso.utilities.wrapper;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: tomc
 * Date: 26/05/12
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
public class Wrappable<SW extends Wrappable> implements Serializable {
    
    private String id;

    private Map<String, SW> subWrappables;

    protected Wrappable() {}

    public Wrappable(String id) {
        this(id, new HashMap<String, SW>());
    }

    public Wrappable(String id, SW ... subWrappables) {
        this(id, Arrays.asList(subWrappables));
    }

    public Wrappable(String id, List<SW> subWrappables) {
        this.id = id;
        this.subWrappables = new HashMap<String, SW>(subWrappables.size());
        for(SW subWrappable : subWrappables)
            this.subWrappables.put(subWrappable.getId(), subWrappable);
    }
    
    public Wrappable(String id, Map<String, SW> subWrappables) {
        this.id = id;
        this.subWrappables = subWrappables;
    }

    public final String getId() {
        return id;
    }

    public final Map<String, SW> getSubWrappables() {
        return Collections.unmodifiableMap(subWrappables);
    }

    public final SW getSubWrappable(String id) {
        return subWrappables.get(id);
    }

    public final void addWrappable(SW subWrappable) {
        subWrappables.put(subWrappable.getId(), subWrappable);
    }

    public final SW removeWrappable(String id) {
        return subWrappables.remove(id);
    }
}
