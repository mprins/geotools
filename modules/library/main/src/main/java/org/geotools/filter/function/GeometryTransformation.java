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

import org.geotools.api.filter.expression.Function;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * This interface can be implemented by geometry transformation functions that whish to be used in the {@link } geometry
 * property.
 *
 * <p>It gives the renderer a hint of what area should be queried given a certain rendering area
 *
 * @author Andrea Aime - GeoSolutions
 */
public interface GeometryTransformation extends Function {
    /** Returns a query envelope given a certain */
    ReferencedEnvelope invert(ReferencedEnvelope renderingEnvelope);
}
