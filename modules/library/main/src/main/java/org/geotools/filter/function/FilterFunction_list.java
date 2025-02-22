/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.filter.function;

// this code is autogenerated - you shouldnt be modifying it!

import static org.geotools.filter.capability.FunctionNameImpl.parameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.geotools.api.filter.capability.FunctionName;
import org.geotools.api.filter.expression.Expression;
import org.geotools.filter.FunctionExpressionImpl;
import org.geotools.filter.capability.FunctionNameImpl;

/**
 * A simple function that creates a list of values of any number of arguments
 *
 * @author Niels Charlier
 */
public class FilterFunction_list extends FunctionExpressionImpl {

    public static FunctionName NAME =
            new FunctionNameImpl("list", parameter("list", List.class), parameter("item", Object.class, 1, -1));

    public FilterFunction_list() {
        super(NAME);
    }

    @Override
    public Object evaluate(Object feature) {

        List<Object> result = new ArrayList<>();

        for (Expression expr : getParameters()) {
            try {
                Object value = expr.evaluate(feature);
                if (value instanceof Collection) {
                    for (Object item : (Collection) value) {
                        result.add(item);
                    }
                } else {
                    result.add(value);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        return result;
    }
}
