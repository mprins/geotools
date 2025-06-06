/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2014 - 2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.gml3.v3_2.bindings;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geotools.geometry.jts.CircularString;
import org.geotools.geometry.jts.CurvedGeometryFactory;
import org.geotools.geometry.jts.LiteCoordinateSequence;
import org.geotools.gml3.bindings.GML3MockData;
import org.geotools.gml3.v3_2.GML;
import org.geotools.gml3.v3_2.GML32TestSupport;
import org.junit.Test;
import org.locationtech.jts.geom.LineString;
import org.w3c.dom.Document;

public class ArcStringTypeBindingTest extends GML32TestSupport {
    @Test
    public void testParse() throws Exception {
        GML3MockData.arcStringWithPosList(document, document);
        LineString lineString = (LineString) parse();
        assertTrue(lineString instanceof CircularString);
        CircularString cs = (CircularString) lineString;

        double[] controlPoints = cs.getControlPoints();
        assertEquals(1.0, controlPoints[0], 0d);
        assertEquals(1.0, controlPoints[1], 0d);
        assertEquals(2.0, controlPoints[2], 0d);
        assertEquals(2.0, controlPoints[3], 0d);
        assertEquals(3.0, controlPoints[4], 0d);
        assertEquals(1.0, controlPoints[5], 0d);
        assertEquals(5, controlPoints[6], 0d);
        assertEquals(5, controlPoints[7], 0d);
        assertEquals(7, controlPoints[8], 0d);
        assertEquals(3, controlPoints[9], 0d);
    }

    @Test
    public void testEncodeSimple() throws Exception {
        LineString curve = new CurvedGeometryFactory(0.1)
                .createCurvedGeometry(new LiteCoordinateSequence(new double[] {1, 1, 2, 2, 3, 1, 5, 5, 7, 3}));
        Document dom = encode(curve, GML.curveProperty);

        String basePath = "/gml:curveProperty/gml:Curve/gml:segments/gml:ArcString";
        assertThat(dom, hasXPath("count(" + basePath + "[@interpolation='circularArc3Points'])", equalTo("1")));
        assertThat(dom, hasXPath(basePath + "/gml:posList", equalTo("1 1 2 2 3 1 5 5 7 3")));
    }

    @Test
    public void testEncodeCompound() throws Exception {
        // create a compound curve
        CurvedGeometryFactory factory = new CurvedGeometryFactory(0.1);
        LineString curve = factory.createCurvedGeometry(new LiteCoordinateSequence(1, 1, 2, 2, 3, 1, 5, 5, 7, 3));
        LineString straight = factory.createLineString(new LiteCoordinateSequence(7, 3, 10, 15));
        LineString compound = factory.createCurvedGeometry(curve, straight);

        // encode
        Document dom = encode(compound, GML.curveProperty);

        // the curve portion
        String basePath1 = "/gml:curveProperty/gml:Curve/gml:segments/gml:ArcString";
        assertThat(dom, hasXPath("count(" + basePath1 + "[@interpolation='circularArc3Points'])", equalTo("1")));
        assertThat(dom, hasXPath(basePath1 + "/gml:posList", equalTo("1 1 2 2 3 1 5 5 7 3")));

        // the straight portion
        String basePath2 = "/gml:curveProperty/gml:Curve/gml:segments/gml:LineStringSegment";
        assertThat(dom, hasXPath("count(" + basePath2 + "[@interpolation='linear'])", equalTo("1")));
        assertThat(dom, hasXPath(basePath2 + "/gml:posList", equalTo("7 3 10 15")));
    }
}
