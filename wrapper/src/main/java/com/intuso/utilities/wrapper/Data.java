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
public class Data<CDATA extends Data> implements Serializable {
    
    private String id;

    private Map<String, CDATA> childData;

    protected Data() {}

    public Data(String id) {
        this(id, new HashMap<String, CDATA>());
    }

    public Data(String id, CDATA... childData) {
        this(id, Arrays.asList(childData));
    }

    public Data(String id, List<CDATA> childData) {
        this.id = id;
        this.childData = new HashMap<String, CDATA>(childData.size());
        for(CDATA sd : childData)
            this.childData.put(sd.getId(), sd);
    }
    
    public Data(String id, Map<String, CDATA> childData) {
        this.id = id;
        this.childData = childData;
    }

    public final String getId() {
        return id;
    }

    public final Map<String, CDATA> getChildData() {
        return Collections.unmodifiableMap(childData);
    }

    public final CDATA getChildData(String id) {
        return childData.get(id);
    }

    public final void addChildData(CDATA childData) {
        this.childData.put(childData.getId(), childData);
    }

    public final CDATA removeChildData(String id) {
        return childData.remove(id);
    }
}
