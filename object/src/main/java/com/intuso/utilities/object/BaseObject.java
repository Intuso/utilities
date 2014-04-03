package com.intuso.utilities.object;

import com.intuso.utilities.listener.ListenerRegistration;
import com.intuso.utilities.listener.Listeners;
import com.intuso.utilities.listener.ListenersFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: tomc
 * Date: 26/05/12
 * Time: 09:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseObject<DATA extends Data<CHILD_DATA>, CHILD_DATA extends Data<?>,
            CHILD_OBJECT extends BaseObject<? extends CHILD_DATA, ?, ?, E>, E extends Exception> {

    public final static String PATH_SEPARATOR = "/";

    private final DATA data;
    private final Map<String, CHILD_OBJECT> children;
    private final Map<String, ListenerRegistration> childListeners;
    private final Listeners<ObjectListener<? super CHILD_OBJECT>> listeners;
    private final GeneralListener generalListener = new GeneralListener();
    private final AncestorListener ancestorListener = new AncestorListener();

    public BaseObject(ListenersFactory listenersFactory, DATA data) {
        assert data != null;
        this.data = data;
        this.listeners = listenersFactory.create();
        children = new TreeMap<String, CHILD_OBJECT>();
        childListeners = new HashMap<String, ListenerRegistration>();
    }

    protected void createChildren(ObjectFactory<CHILD_DATA, ? extends CHILD_OBJECT, ? extends E> factory) throws E {
        for(CHILD_DATA childData : data.getChildData().values())
            if(children.get(childData.getId()) == null) {
                CHILD_OBJECT child = factory.create(childData);
                children.put(childData.getId(), child);
                childListeners.put(child.getId(), child.addChildListener(ancestorListener));
                generalListener.childObjectAdded(child.getId(), child);
            }
    }

    public DATA getData() {
        return data;
    }

    public final ListenerRegistration addChildListener(ObjectListener<? super CHILD_OBJECT> listener) {
        return listeners.addListener(listener);
    }

    public final ListenerRegistration addChildListener(ObjectListener<? super CHILD_OBJECT> listener, boolean callForExistingChildren, boolean callForExistingAncestors) {
        ListenerRegistration result = listeners.addListener(listener);
        if(callForExistingChildren) {
            for(CHILD_OBJECT child : getChildren()) {
                listener.childObjectAdded(child.getId(), child);
                if(callForExistingAncestors)
                    notifyListener(listener, child);
            }
        }
        return result;
    }

    private void notifyListener(ObjectListener<? super CHILD_OBJECT> listener, BaseObject<?, ?, ?, ?> child) {
        for(BaseObject<?, ?, ?, ?> object : child.getChildren()) {
            listener.ancestorObjectAdded(object.getId(), object);
            notifyListener(listener, object);
        }
    }

    public final String getId() {
        return data.getId();
    }

    protected void addChild(CHILD_OBJECT child) {
        if(child != null) {
            if(children.get(child.getId()) != null || data.getChildData(child.getId()) != null)
                throw new RuntimeException("A child/child-data with id=\"" + child.getId() + "\" already exists. You must remove the existing one before adding one of the same id");
            children.put(child.getId(), child);
            data.addChildData(child.getData());
            childListeners.put(child.getId(), child.addChildListener(ancestorListener));
            generalListener.childObjectAdded(child.getId(), child);
        }
    }

    public CHILD_OBJECT removeChild(String id) {
        CHILD_OBJECT child = getChild(id);
        if(child != null) {
            children.remove(id);
            data.removeChildData(id);
            childListeners.remove(id).removeListener();
            generalListener.childObjectRemoved(child.getId(), child);
        }
        return child;
    }

    public Collection<CHILD_OBJECT> getChildren() {
        return children.values();
    }

    public final CHILD_OBJECT getChild(String id) {
        return children.get(id);
    }

    private class GeneralListener implements ObjectListener<CHILD_OBJECT> {
        @Override
        public void childObjectAdded(String childId, CHILD_OBJECT child) {
            for(ObjectListener<? super CHILD_OBJECT> listener : listeners)
                listener.childObjectAdded(childId, child);
            ancestorObjectAdded(childId, child);
        }

        @Override
        public void childObjectRemoved(String childId, CHILD_OBJECT child) {
            for(ObjectListener<? super CHILD_OBJECT> listener : listeners)
                listener.childObjectRemoved(childId, child);
            ancestorObjectRemoved(childId, child);
        }

        @Override
        public void ancestorObjectAdded(String ancestorPath, BaseObject<?, ?, ?, ?> ancestor) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(ObjectListener<? super CHILD_OBJECT> listener : listeners)
                listener.ancestorObjectAdded(newAncestorPath, ancestor);
        }

        @Override
        public void ancestorObjectRemoved(String ancestorPath, BaseObject<?, ?, ?, ?> ancestor) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(ObjectListener<? super CHILD_OBJECT> listener : listeners)
                listener.ancestorObjectRemoved(newAncestorPath, ancestor);
        }
    }

    private class AncestorListener implements ObjectListener<BaseObject<?, ?, ?, ?>> {
        @Override
        public void childObjectAdded(String childId, BaseObject<?, ?, ?, ?> child) {
            // do nothing for child added
        }

        @Override
        public void childObjectRemoved(String childId, BaseObject<?, ?, ?, ?> child) {
            // do nothing for child removed
        }

        @Override
        public void ancestorObjectAdded(String ancestorPath, BaseObject<?, ?, ?, ?> ancestor) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(ObjectListener<? super CHILD_OBJECT> listener : listeners)
                listener.ancestorObjectAdded(newAncestorPath, ancestor);
        }

        @Override
        public void ancestorObjectRemoved(String ancestorPath, BaseObject<?, ?, ?, ?> ancestor) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(ObjectListener<? super CHILD_OBJECT> listener : listeners)
                listener.ancestorObjectRemoved(newAncestorPath, ancestor);
        }
    }
}