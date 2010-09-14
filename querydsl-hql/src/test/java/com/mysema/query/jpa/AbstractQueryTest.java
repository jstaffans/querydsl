/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.query.jpa;

import static org.junit.Assert.assertEquals;

import com.mysema.query.jpa.HQLSerializer;
import com.mysema.query.jpa.HQLSubQuery;
import com.mysema.query.jpa.HQLTemplates;
import com.mysema.query.types.Expression;

public abstract class AbstractQueryTest implements Constants{

    protected QueryHelper query() {
        return new QueryHelper();
    }

    protected HQLSubQuery sub(){
        return new HQLSubQuery();
    }

    protected static void assertToString(String expected, Expression<?> expr) {
        HQLSerializer serializer = new HQLSerializer(HQLTemplates.DEFAULT);
        assertEquals(expected, serializer.handle(expr).toString().replace("\n", " "));
    }

}