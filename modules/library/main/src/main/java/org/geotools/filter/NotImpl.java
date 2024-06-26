/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2006-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.filter;

import java.util.ArrayList;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterVisitor;
import org.geotools.api.filter.Not;

/** @author jdeolive */
public class NotImpl extends LogicFilterImpl implements Not {

    protected NotImpl(Filter filter) {
        super(new ArrayList<>());
        this.children.add(filter);
    }

    @Override
    public Filter getFilter() {
        return children.get(0);
    }

    public void setFilter(Filter filter) {
        if (children.isEmpty()) {
            children.add(filter);
        } else {
            children.set(0, filter);
        }
    }

    // @Override
    @Override
    public boolean evaluate(Object feature) {
        return !getFilter().evaluate(feature);
    }

    @Override
    public Object accept(FilterVisitor visitor, Object extraData) {
        return visitor.visit(this, extraData);
    }
}
