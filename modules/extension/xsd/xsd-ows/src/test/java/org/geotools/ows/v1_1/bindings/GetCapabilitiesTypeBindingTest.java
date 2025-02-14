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
package org.geotools.ows.v1_1.bindings;

import static org.junit.Assert.assertEquals;

import net.opengis.ows11.GetCapabilitiesType;
import org.geotools.ows.v1_1.OWS;
import org.geotools.ows.v1_1.OWSTestSupport_1_1;
import org.geotools.xsd.Binding;
import org.junit.Assert;
import org.junit.Test;

public class GetCapabilitiesTypeBindingTest extends OWSTestSupport_1_1 {
    @Test
    public void testType() throws Exception {
        assertEquals(GetCapabilitiesType.class, binding(OWS.GetCapabilitiesType).getType());
    }

    @Test
    public void testExecutionMode() throws Exception {
        assertEquals(Binding.OVERRIDE, binding(OWS.GetCapabilitiesType).getExecutionMode());
    }

    @Test
    public void testParse() throws Exception {
        String xml = "<ows:GetCapabilities xmlns:ows=\"http://www.opengis.net/ows\" version=\"1.1.0\">"
                + "<ows:AcceptVersions>"
                + "<ows:Version>1.0.0</ows:Version>"
                + "</ows:AcceptVersions>"
                + "</ows:GetCapabilities>";

        buildDocument(xml);

        GetCapabilitiesType getCaps = (GetCapabilitiesType) parse();
        Assert.assertNotNull(getCaps);

        Assert.assertEquals(1, getCaps.getAcceptVersions().getVersion().size());
        Assert.assertEquals("1.0.0", getCaps.getAcceptVersions().getVersion().get(0));
    }
}
