package com.intuso.utilities.webserver;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HandlerContainer;
import org.eclipse.jetty.server.HttpChannelState;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandlerContainer;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ScopedHandler;
import org.eclipse.jetty.util.ArrayTernaryTrie;
import org.eclipse.jetty.util.ArrayUtil;
import org.eclipse.jetty.util.Trie;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tomc on 24/05/17.
 *
 * Problem is that ScopedHandler finds the next ScopedHandler which is always the first one of the collection that
 * somewhere in its handler chain has a ScopedHandler. This sets the context in the Request object and alters the path
 * to remove the context. Then, when ContextHandlerCollection.handle() is called, the path is already context aware so
 * everything 404s.
 *
 * Tried:
 * - session/security handler for each sub handler. Problem is that session cookie path is always set to /api since
 *   that's the context that the session resource lives on. Any non /api request can't use that cookie.
 * - wrapping each sub handler with a dummy handler so the top-level ScopedHandler can't find any sub scopes. Causes
 *   initialisation issues/NPE at runtime
 * - wrapping the ContextHandlerCollection with a dummy handler so the top-level ScopedHandler can't find any sub
 *   scopes. Causes initialisation issues/NPE at runtime
 *
 *  This attempt tries to select the right scope based on the contexts, rather than a fixed next scope based on the
 *  first sub-handler to have a ScopedHandler. This impl of doHandle() knows that the target is already context aware
 *  so uses the context decided by doScope() that is cached for each request
 */
@ManagedObject("Context Handler Collection")
public class ScopedContextHandlerCollection extends ScopedHandler
{
    private static final Logger LOG = Log.getLogger(ScopedContextHandlerCollection.class);

    private volatile Handler[] _handlers;

    private final ConcurrentMap<ContextHandler,Handler> _contextBranches = new ConcurrentHashMap<>();
    private volatile Trie<Map.Entry<String,ScopedContextHandlerCollection.Branch[]>> _pathBranches;
    private Class<? extends ContextHandler> _contextClass = ContextHandler.class;

    /* ------------------------------------------------------------ */
    /**
     * Remap the context paths.
     */
    @ManagedOperation("update the mapping of context path to context")
    public void mapContexts() {

        _contextBranches.clear();

        if (getHandlers()==null)
        {
            _pathBranches=new ArrayTernaryTrie<>(false,16);
            return;
        }

        // Create map of contextPath to handler Branch
        Map<String, ScopedContextHandlerCollection.Branch[]> map = new HashMap<>();
        for (Handler handler:getHandlers())
        {
            ScopedContextHandlerCollection.Branch branch=new ScopedContextHandlerCollection.Branch(handler);
            for (String contextPath : branch.getContextPaths())
            {
                ScopedContextHandlerCollection.Branch[] branches=map.get(contextPath);
                map.put(contextPath, ArrayUtil.addToArray(branches, branch, ScopedContextHandlerCollection.Branch.class));
            }

            for (ContextHandler context : branch.getContextHandlers())
                _contextBranches.putIfAbsent(context, branch.getHandler());
        }

        // Sort the branches so those with virtual hosts are considered before those without
        for (Map.Entry<String, ScopedContextHandlerCollection.Branch[]> entry: map.entrySet())
        {
            ScopedContextHandlerCollection.Branch[] branches=entry.getValue();
            ScopedContextHandlerCollection.Branch[] sorted=new ScopedContextHandlerCollection.Branch[branches.length];
            int i=0;
            for (ScopedContextHandlerCollection.Branch branch:branches)
                if (branch.hasVirtualHost())
                    sorted[i++]=branch;
            for (ScopedContextHandlerCollection.Branch branch:branches)
                if (!branch.hasVirtualHost())
                    sorted[i++]=branch;
            entry.setValue(sorted);
        }

        // Loop until we have a big enough trie to hold all the context paths
        int capacity=512;
        Trie<Map.Entry<String, ScopedContextHandlerCollection.Branch[]>> trie;
        loop: while(true)
        {
            trie=new ArrayTernaryTrie<>(false,capacity);
            for (Map.Entry<String, ScopedContextHandlerCollection.Branch[]> entry: map.entrySet())
            {
                if (!trie.put(entry.getKey().substring(1),entry))
                {
                    capacity+=512;
                    continue loop;
                }
            }
            break loop;
        }


        if (LOG.isDebugEnabled())
        {
            for (String ctx : trie.keySet())
                LOG.debug("{}->{}",ctx, Arrays.asList(trie.get(ctx).getValue()));
        }
        _pathBranches=trie;
    }

    public void setHandlers(Handler[] handlers) {

        if (handlers!=null)
        {
            // check for loops
            for (Handler handler:handlers)
                if (handler == this || (handler instanceof HandlerContainer &&
                        Arrays.asList(((HandlerContainer)handler).getChildHandlers()).contains(this)))
                    throw new IllegalStateException("setHandler loop");

            // Set server
            for (Handler handler:handlers)
                if (handler.getServer()!=getServer())
                    handler.setServer(getServer());
        }
        Handler[] old=_handlers;;
        _handlers = handlers;
        updateBeans(old, handlers);

        if (isStarted())
            mapContexts();
    }

    /* ------------------------------------------------------------ */
    @Override
    protected void doStart() throws Exception {
        mapContexts();
        super.doStart();
    }

    @Override
    public void doScope(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Handler handler = findContext(target, baseRequest);
        String contextPath = "";
        String oldContextPath = baseRequest.getContextPath();
        if(handler instanceof ContextHandler) {
            contextPath = ((ContextHandler) handler).getContextPath();
            if(contextPath == null)
                contextPath = "";
            if(!contextPath.startsWith("/"))
                contextPath = "/" + contextPath;
            if(contextPath.endsWith("/"))
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            if(contextPath.length() > 1) {
                String completeContextPath = (oldContextPath == null ? "" : oldContextPath) + contextPath;
                baseRequest.setContextPath(completeContextPath);
            }
        }
        String contextTarget = target.substring(contextPath.length());
        if(handler != null) {
            baseRequest.setAttribute(ScopedContextHandlerCollection.class.getName(), handler);
            final String finalContextPath = contextPath;
            final ContextHandler.StaticContext context = new ContextHandler.StaticContext() {
                @Override
                public String getContextPath() {
                    return finalContextPath;
                }
            };
            request = new HttpServletRequestWrapper(request) {
                @Override
                public ServletContext getServletContext() {
                    return context;
                }
            };
            if(handler instanceof AbstractHandlerContainer) {
                ScopedHandler nextScope = ((AbstractHandlerContainer) handler).getChildHandlerByClass(ScopedHandler.class);
                if (nextScope != null)
                    nextScope.doScope(contextTarget, baseRequest, request, response);
                else if (_outerScope != null)
                    _outerScope.doHandle(contextTarget,baseRequest,request, response);
                else
                    doHandle(contextTarget,baseRequest,request, response);
            } else if(_outerScope != null)
                _outerScope.doHandle(contextTarget, baseRequest, request, response);
            else
                doHandle(contextTarget,baseRequest,request, response);
        } else if(_outerScope != null)
            _outerScope.doHandle(contextTarget, baseRequest, request, response);
        else
            doHandle(contextTarget, baseRequest, request, response);
        baseRequest.setContextPath(oldContextPath);
    }

    /* ------------------------------------------------------------ */
    /*
     * @see org.eclipse.jetty.server.server.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
     */
    @Override
    public void doHandle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Handler handler = (Handler) baseRequest.getAttribute(ScopedContextHandlerCollection.class.getName());
        if(handler != null)
            handler.handle(target, baseRequest, request, response);
    }

    private Handler findContext(String target, Request baseRequest) {

        Handler[] handlers = getHandlers();
        if (handlers==null || handlers.length==0)
            return null;

        HttpChannelState async = baseRequest.getHttpChannelState();
        if (async.isAsync()) {

            ContextHandler context=async.getContextHandler();
            if (context!=null) {

                Handler branch = _contextBranches.get(context);

                if (branch==null)
                    return context;
                else
                    return branch;
            }
        }

        // data structure which maps a request to a context; first-best match wins
        // { context path => [ context ] }
        // }
        if (target.startsWith("/")) {

            int limit = target.length()-1;

            while (limit>=0) {
                // Get best match
                Map.Entry<String, ScopedContextHandlerCollection.Branch[]> branches = _pathBranches.getBest(target,1,limit);

                if (branches==null)
                    break;

                int l=branches.getKey().length();
                if (l==1 || target.length()==l || target.charAt(l)=='/' && branches.getValue().length > 0)
                    return branches.getValue()[0].getHandler();

                limit=l-2;
            }

            return null;
        } else
            return handlers[0];
    }

    /* ------------------------------------------------------------ */
    /** Add a context handler.
     * @param contextPath  The context path to add
     * @param resourceBase the base (root) Resource
     * @return the ContextHandler just added
     */
    public ContextHandler addContext(String contextPath,String resourceBase) {
        try
        {
            ContextHandler context = _contextClass.newInstance();
            context.setContextPath(contextPath);
            context.setResourceBase(resourceBase);
            addHandler(context);
            return context;
        }
        catch (Exception e)
        {
            LOG.debug(e);
            throw new Error(e);
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The class to use to add new Contexts
     */
    public Class<?> getContextClass() {
        return _contextClass;
    }


    /* ------------------------------------------------------------ */
    /**
     * @param contextClass The class to use to add new Contexts
     */
    public void setContextClass(Class<? extends ContextHandler> contextClass) {
        if (contextClass ==null || !(ContextHandler.class.isAssignableFrom(contextClass)))
            throw new IllegalArgumentException();
        _contextClass = contextClass;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the handlers.
     */
    @Override
    public Handler[] getHandlers() {
        return _handlers;
    }

    /* ------------------------------------------------------------ */
    /* Add a handler.
     * This implementation adds the passed handler to the end of the existing collection of handlers.
     * @see org.eclipse.jetty.server.server.HandlerContainer#addHandler(org.eclipse.jetty.server.server.Handler)
     */
    public void addHandler(Handler handler) {
        setHandlers(ArrayUtil.addToArray(getHandlers(), handler, Handler.class));
    }

    /* ------------------------------------------------------------ */
    /* Prepend a handler.
     * This implementation adds the passed handler to the start of the existing collection of handlers.
     * @see org.eclipse.jetty.server.server.HandlerContainer#addHandler(org.eclipse.jetty.server.server.Handler)
     */
    public void prependHandler(Handler handler) {
        setHandlers(ArrayUtil.prependToArray(handler, getHandlers(), Handler.class));
    }

    public void removeHandler(Handler handler) {
        Handler[] handlers = getHandlers();

        if (handlers!=null && handlers.length>0 )
            setHandlers(ArrayUtil.removeFromArray(handlers, handler));
    }

    @Override
    protected void expandChildren(List<Handler> list, Class<?> byClass) {
        if (getHandlers()!=null)
            for (Handler h:getHandlers())
                expandHandler(h, list, byClass);
    }

    @Override
    public void destroy() {
        if (!isStopped())
            throw new IllegalStateException("!STOPPED");
        Handler[] children=getChildHandlers();
        setHandlers(null);
        for (Handler child: children)
            child.destroy();
        super.destroy();
    }

    @Override
    public String toString() {
        Handler[] handlers=getHandlers();
        return super.toString()+(handlers==null?"[]":Arrays.asList(getHandlers()).toString());
    }

    private final static class Branch {

        private final Handler _handler;
        private final ContextHandler[] _contexts;

        Branch(Handler handler) {
            _handler=handler;

            if (handler instanceof ContextHandler)
            {
                _contexts = new ContextHandler[]{(ContextHandler)handler};
            }
            else if (handler instanceof HandlerContainer)
            {
                Handler[] contexts=((HandlerContainer)handler).getChildHandlersByClass(ContextHandler.class);
                _contexts = new ContextHandler[contexts.length];
                System.arraycopy(contexts, 0, _contexts, 0, contexts.length);
            }
            else
                _contexts = new ContextHandler[0];
        }

        Set<String> getContextPaths() {
            Set<String> set = new HashSet<>();
            for (ContextHandler context:_contexts)
                set.add(context.getContextPath());
            return set;
        }

        boolean hasVirtualHost() {
            for (ContextHandler context:_contexts)
                if (context.getVirtualHosts()!=null && context.getVirtualHosts().length>0)
                    return true;
            return false;
        }

        ContextHandler[] getContextHandlers() {
            return _contexts;
        }

        Handler getHandler() {
            return _handler;
        }

        @Override
        public String toString() {
            return String.format("{%s,%s}",_handler,Arrays.asList(_contexts));
        }
    }
}

