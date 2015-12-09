package com.intuso.utilities.object;

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
public class Data<CHILD_DATA extends Data> implements Serializable {
    
    private String id;

    private Map<String, CHILD_DATA> childData;

    protected Data() {}

    public Data(String id) {
        this(id, new HashMap<String, CHILD_DATA>());
    }

    public Data(String id, CHILD_DATA... childData) {
        this(id, Arrays.asList(childData));
    }

    public Data(String id, List<CHILD_DATA> childData) {
        this.id = id;
        this.childData = new HashMap<String, CHILD_DATA>(childData.size());
        for(CHILD_DATA sd : childData)
            this.childData.put(sd.getId(), sd);
    }
    
    public Data(String id, Map<String, CHILD_DATA> childData) {
        this.id = id;
        this.childData = childData;
    }

    public final String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public final Map<String, CHILD_DATA> getChildData() {
        return Collections.unmodifiableMap(childData);
    }

    public void setChildData(Map<String, CHILD_DATA> childData) {
        this.childData = childData;
    }

    public final CHILD_DATA getChildData(String id) {
        return childData == null ? null : childData.get(id);
    }

    public final void addChildData(CHILD_DATA childData) {
        if(this.childData == null)
            this.childData = new HashMap<>();
        this.childData.put(childData.getId(), childData);
    }

    public final CHILD_DATA removeChildData(String id) {
        return childData.remove(id);
    }
}
