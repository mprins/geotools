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
package org.geotools.gml2.bindings;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.geotools.gml2.GML;
import org.geotools.xsd.Binding;
import org.junit.Test;
import org.locationtech.jts.geom.MultiLineString;
import org.w3c.dom.Document;

public class GMLMultiLineStringPropertyTypeBinding2Test extends GMLTestSupport {
    @Test
    public void testType() {
        assertEquals(
                MultiLineString.class, binding(GML.MultiLineStringPropertyType).getType());
    }

    @Test
    public void testExecutionMode() {
        assertEquals(Binding.OVERRIDE, binding(GML.MultiLineStringPropertyType).getExecutionMode());
    }

    @Test
    public void testParse() throws Exception {
        GML2MockData.multiLineStringProperty(document, document);

        MultiLineString mp = (MultiLineString) parse();
        assertNotNull(mp);
    }

    @Test
    public void testEncode() throws Exception {
        Document doc = encode(GML2MockData.multiLineString(), GML.multiLineStringProperty);

        assertEquals(
                1,
                doc.getElementsByTagNameNS(GML.NAMESPACE, GML.MultiLineString.getLocalPart())
                        .getLength());
    }
}
