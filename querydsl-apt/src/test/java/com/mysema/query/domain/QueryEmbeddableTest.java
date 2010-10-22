package com.mysema.query.domain;

import static org.junit.Assert.*;

import org.junit.Test;

import com.mysema.query.annotations.QueryEmbeddable;
import com.mysema.query.annotations.QueryEntity;

public class QueryEmbeddableTest {

    @QueryEntity
    public class Parent {
    
        String parentProperty;
        
        Child child;
        
    }
    
    @QueryEmbeddable
    public class Child {
     
        String childProperty;
        
    }
    
    @Test
    public void test(){
        assertNotNull(QQueryEmbeddableTest_Parent.parent.child.childProperty);
    }
    
}