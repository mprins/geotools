/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geopkg.wps.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geotools.geopkg.TileMatrix;
import org.geotools.xsd.Binding;
import org.junit.Test;

public class GridtypeBindingTest extends GPKGTestSupport {
    @Test
    public void testType() {
        assertEquals(TileMatrix.class, binding(GPKG.gridtype).getType());
    }

    @Test
    public void testExecutionMode() {
        assertEquals(Binding.OVERRIDE, binding(GPKG.gridtype).getExecutionMode());
    }

    @Test
    public void testParse() throws Exception {
        buildDocument("<grid>"
                + "<zoomlevel>3</zoomlevel>"
                + "<tilewidth>512</tilewidth>"
                + "<tileheight>256</tileheight>"
                + "<matrixwidth>32</matrixwidth>"
                + "<matrixheight>16</matrixheight>"
                + "<pixelxsize>0.05</pixelxsize>"
                + "<pixelysize>0.06</pixelysize>"
                + " </grid>");
        Object result = parse(GPKG.gridtype);
        assertTrue(result instanceof TileMatrix);
        TileMatrix matrix = (TileMatrix) result;
        assertEquals(3, matrix.getZoomLevel().intValue());
        assertEquals(512, matrix.getTileWidth().intValue());
        assertEquals(256, matrix.getTileHeight().intValue());
        assertEquals(32, matrix.getMatrixWidth().intValue());
        assertEquals(16, matrix.getMatrixHeight().intValue());
        assertEquals(0.05, matrix.getXPixelSize(), 0.0);
        assertEquals(0.06, matrix.getYPixelSize(), 0.0);
    }
}
