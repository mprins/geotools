/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.feature.type;

import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.feature.type.GeometryType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

public class GeometryDescriptorImpl extends AttributeDescriptorImpl implements GeometryDescriptor {

    public GeometryDescriptorImpl(
            GeometryType type, Name name, int min, int max, boolean isNillable, Object defaultValue) {
        super(type, name, min, max, isNillable, defaultValue);
    }

    @Override
    public GeometryType getType() {
        return (GeometryType) super.getType();
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return getType().getCoordinateReferenceSystem();
    }

    @Override
    public String getLocalName() {
        return getName().getLocalPart();
    }
}
