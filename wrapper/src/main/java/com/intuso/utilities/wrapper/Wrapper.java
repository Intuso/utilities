package com.intuso.utilities.wrapper;

import com.intuso.utilities.listener.ListenerRegistration;
import com.intuso.utilities.listener.Listeners;

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
public abstract class Wrapper<WBL extends Data<SWBL>, SWBL extends Data<?>,
            SWR extends Wrapper<? extends SWBL, ?, ?, E>, E extends Exception> {

    public final static String PATH_SEPARATOR = "/";

    private final WBL data;
    private final Map<String, SWR> wrappers;
    private final Map<String, ListenerRegistration> childListeners;
    private final Listeners<WrapperListener<? super SWR>> listeners = new Listeners<WrapperListener<? super SWR>>();
    private final GeneralListener generalListener = new GeneralListener();
    private final AncestorListener ancestorListener = new AncestorListener();

    public Wrapper(WBL data) {
        assert data != null;
        this.data = data;
        wrappers = new TreeMap<String, SWR>();
        childListeners = new HashMap<String, ListenerRegistration>();
    }

    protected void unwrapChildren(WrapperFactory<SWBL, ? extends SWR, ? extends E> factory) throws E {
        for(SWBL subWrappable : data.getChildData().values())
            if(wrappers.get(subWrappable.getId()) == null) {
                SWR subWrapper = factory.create(subWrappable);
                wrappers.put(subWrappable.getId(), subWrapper);
                childListeners.put(subWrapper.getId(), subWrapper.addWrapperListener(ancestorListener));
                generalListener.childWrapperAdded(subWrapper.getId(), subWrapper);
            }
    }

    public WBL getData() {
        return data;
    }

    public final ListenerRegistration addWrapperListener(WrapperListener<? super SWR> listener) {
        return listeners.addListener(listener);
    }

    public final String getId() {
        return data.getId();
    }

    protected void addWrapper(SWR wrapper) {
        if(wrapper != null) {
            if(wrappers.get(wrapper.getId()) != null || data.getChildData(wrapper.getId()) != null)
                throw new RuntimeException("A wrapper/wrappable with id=\"" + wrapper.getId() + "\" already exists. You must remove the existing one before adding one of the same id");
            wrappers.put(wrapper.getId(), wrapper);
            data.addChildData(wrapper.getData());
            childListeners.put(wrapper.getId(), wrapper.addWrapperListener(ancestorListener));
            generalListener.childWrapperAdded(wrapper.getId(), wrapper);
        }
    }

    public SWR removeWrapper(String id) {
        SWR wrapper = getWrapper(id);
        if(wrapper != null) {
            wrappers.remove(id);
            data.removeChildData(id);
            childListeners.remove(id).removeListener();
            generalListener.childWrapperRemoved(wrapper.getId(), wrapper);
        }
        return wrapper;
    }

    public Collection<SWR> getWrappers() {
        return wrappers.values();
    }

    public final SWR getWrapper(String id) {
        return wrappers.get(id);
    }

    private class GeneralListener implements WrapperListener<SWR> {
        @Override
        public void childWrapperAdded(String childId, SWR wrapper) {
            for(WrapperListener<? super SWR> listener : listeners)
                listener.childWrapperAdded(childId, wrapper);
            ancestorAdded(childId, wrapper);
        }

        @Override
        public void childWrapperRemoved(String childId, SWR wrapper) {
            for(WrapperListener<? super SWR> listener : listeners)
                listener.childWrapperRemoved(childId, wrapper);
            ancestorRemoved(childId, wrapper);
        }

        @Override
        public void ancestorAdded(String ancestorPath, Wrapper<?, ?, ?, ?> wrapper) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(WrapperListener<? super SWR> listener : listeners)
                listener.ancestorAdded(newAncestorPath, wrapper);
        }

        @Override
        public void ancestorRemoved(String ancestorPath, Wrapper<?, ?, ?, ?> wrapper) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(WrapperListener<? super SWR> listener : listeners)
                listener.ancestorRemoved(newAncestorPath, wrapper);
        }
    }

    private class AncestorListener implements WrapperListener<Wrapper<?, ?, ?, ?>> {
        @Override
        public void childWrapperAdded(String childId, Wrapper<?, ?, ?, ?> wrapper) {
            // do nothing for child added
        }

        @Override
        public void childWrapperRemoved(String childId, Wrapper<?, ?, ?, ?> wrapper) {
            // do nothing for child removed
        }

        @Override
        public void ancestorAdded(String ancestorPath, Wrapper<?, ?, ?, ?> wrapper) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(WrapperListener<? super SWR> listener : listeners)
                listener.ancestorAdded(newAncestorPath, wrapper);
        }

        @Override
        public void ancestorRemoved(String ancestorPath, Wrapper<?, ?, ?, ?> wrapper) {
            String newAncestorPath = getId() + PATH_SEPARATOR + ancestorPath;
            for(WrapperListener<? super SWR> listener : listeners)
                listener.ancestorRemoved(newAncestorPath, wrapper);
        }
    }
}