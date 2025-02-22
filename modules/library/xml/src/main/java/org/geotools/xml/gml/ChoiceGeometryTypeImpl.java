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
package org.geotools.xml.gml;

import java.util.List;
import org.geotools.api.feature.type.GeometryType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.Filter;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * Created for GML generated FeatureTypes. Represents a Choice type.
 *
 * <p>This is temporary and only for use by the parser. It should never be public or in common use.
 *
 * @author Jesse
 */
public class ChoiceGeometryTypeImpl extends ChoiceAttributeTypeImpl implements ChoiceGeometryType, GeometryType {

    private CoordinateReferenceSystem crs;

    public ChoiceGeometryTypeImpl(
            Name name,
            Class[] types,
            Class defaultType,
            boolean nillable,
            int min,
            int max,
            Object defaultValue,
            CoordinateReferenceSystem crs,
            List<Filter> filter) {
        super(name, types, defaultType, nillable, min, max, defaultValue, filter);
        this.crs = crs;
    }

    @Override
    public Object convert(Object obj) {
        GeometryFactory fac = new GeometryFactory();
        if (getBinding() == MultiPolygon.class && obj instanceof Polygon) {
            return fac.createMultiPolygon(new Polygon[] {(Polygon) obj});
        }
        if (getBinding() == MultiPoint.class && obj instanceof Point) {
            return fac.createMultiPoint(new Point[] {(Point) obj});
        }
        if (getBinding() == MultiLineString.class && obj instanceof LineString) {
            return fac.createMultiLineString(new LineString[] {(LineString) obj});
        }
        if (getBinding() == GeometryCollection.class && obj instanceof Geometry) {
            return fac.createGeometryCollection(
                    new org.locationtech.jts.geom.Geometry[] {(org.locationtech.jts.geom.Geometry) obj});
        }
        return obj;
    }

    @Override
    public GeometryType getType() {
        return this;
    }

    @Override
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return crs;
    }
}
