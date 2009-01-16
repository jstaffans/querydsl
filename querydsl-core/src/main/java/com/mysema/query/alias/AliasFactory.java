/*
 * Copyright (c) 2008 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.query.alias;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import com.mysema.query.grammar.types.Expr;
import com.mysema.query.grammar.types.Path;
import com.mysema.query.grammar.types.PathMetadata;
import com.mysema.query.grammar.types.Path.PEntity;
import com.mysema.query.util.FactoryMap;

/**
 * AliasFactory provides
 *
 * @author tiwe
 * @version $Id$
 */
public class AliasFactory {
    
    private final ThreadLocal<WeakIdentityHashMap<Object, Expr<?>>> bindings = new ThreadLocal<WeakIdentityHashMap<Object, Expr<?>>>() {
        @Override
        protected WeakIdentityHashMap<Object, Expr<?>> initialValue() {
                return new WeakIdentityHashMap<Object, Expr<?>>();
        }
    };
    
    private final ThreadLocal<Expr<?>> current = new ThreadLocal<Expr<?>>();
    
    // caches top level paths (class/var as key)
    private FactoryMap<PEntity<?>> pathCache = new FactoryMap<PEntity<?>>(){
        public <A> PEntity<A> create(Class<A> cl, String var){
            return new Path.PEntity<A>(cl, cl.getSimpleName(), PathMetadata.forVariable(var));
        }
    };
    
    // cahces top level proxies (class/var as key)
    private FactoryMap<ManagedObject> proxyCache = new FactoryMap<ManagedObject>(){
        public ManagedObject create(Class<?> cl, Expr<?> path){
            return (ManagedObject) createProxy(cl, path);
        }
    };
    
    public <A> A createAliasForProp(Class<A> cl, Object parent, Expr<?> path){        
        A proxy = createProxy(cl, path);    
        return proxy;
    }
        
    @SuppressWarnings("unchecked")
    public <A> A createAliasForVar(Class<A> cl, String var){    
        Expr<?> path = pathCache.get(cl,var);
        A proxy = (A) proxyCache.get(cl,path);        
        return proxy;
    }
    
    @SuppressWarnings("unchecked")
    private <A> A createProxy(Class<A> cl, Expr<?> path) {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(AliasFactory.class.getClassLoader());
        if (cl.isInterface()){
            enhancer.setInterfaces(new Class[]{cl,ManagedObject.class});
        }else{
            enhancer.setSuperclass(cl);    
            enhancer.setInterfaces(new Class[]{ManagedObject.class});
        }         
        // creates one handler per proxy
        MethodInterceptor handler = new PropertyAccessInvocationHandler(path,this);
        enhancer.setCallback(handler);
        A rv = (A)enhancer.create();
        bindings.get().put(rv, path);
        return rv;
    }
    
    @SuppressWarnings("unchecked")
    public <A extends Expr<?>> A getCurrent() {
        return (A) current.get();
    }

    public boolean hasCurrent() {
        return current.get() != null;
    }
    
    public Expr<?> pathForAlias(Object key){
        return bindings.get().get(key);
    }

    public void setCurrent(Expr<?> path){
        current.set(path);
    }
    
}
