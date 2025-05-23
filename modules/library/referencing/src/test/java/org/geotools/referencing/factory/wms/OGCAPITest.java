/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2023, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.referencing.factory.wms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.geotools.api.metadata.citation.Citation;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.crs.GeographicCRS;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.factory.AbstractAuthorityFactory;
import org.geotools.referencing.factory.CachedCRSAuthorityDecorator;
import org.geotools.referencing.factory.IdentifiedObjectFinder;
import org.junit.Before;
import org.junit.Test;

public class OGCAPITest {

    private WebCRSFactory factory;

    /** Initializes the factory to test. */
    @Before
    public void setUp() {
        factory = new OGCAPICRSFactory();
    }

    @Test
    public void testAuthority() {
        final Citation authority = factory.getAuthority();
        assertTrue(Citations.identifierMatches(authority, "OGC"));
        assertFalse(Citations.identifierMatches(authority, "CRS"));
        assertFalse(Citations.identifierMatches(authority, "EPSG"));
        assertFalse(Citations.identifierMatches(authority, "AUTO"));
        assertFalse(Citations.identifierMatches(authority, "AUTO2"));
    }

    /** Tests the CRS:84 code. */
    @Test
    public void testCRS84() throws FactoryException {
        GeographicCRS crs = factory.createGeographicCRS("OGC:84");
        assertSame(crs, factory.createGeographicCRS("84"));
        assertSame(crs, factory.createGeographicCRS("CRS84"));
        assertSame(crs, factory.createGeographicCRS("OGC:CRS84"));
        assertSame(crs, factory.createGeographicCRS("ogc : crs84"));
        assertNotSame(crs, factory.createGeographicCRS("OGC:83"));
        assertNotEquals(DefaultGeographicCRS.WGS84, crs);
        assertTrue(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));
    }

    @Test
    public void testCRS83() throws FactoryException {
        GeographicCRS crs = factory.createGeographicCRS("OGC:83");
        assertSame(crs, factory.createGeographicCRS("83"));
        assertSame(crs, factory.createGeographicCRS("CRS83"));
        assertSame(crs, factory.createGeographicCRS("OGC:CRS83"));
        assertNotSame(crs, factory.createGeographicCRS("OGC:84"));
        assertFalse(CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, crs));
    }

    /** Tests the {@link IdentifiedObjectFinder#find} method. */
    @Test
    public void testFind() throws FactoryException {
        final GeographicCRS CRS84 = factory.createGeographicCRS("OGC:84");
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);
        assertTrue("Newly created finder should default to full scan.", finder.isFullScanAllowed());

        finder.setFullScanAllowed(false);
        assertSame(
                "Should find without the need for scan, since we can use the CRS:84 identifier.",
                CRS84,
                finder.find(CRS84));

        finder.setFullScanAllowed(true);
        assertSame(
                "Allowing scanning should not make any difference for this CRS84 instance.", CRS84, finder.find(CRS84));

        assertNotSame("Required condition for next test.", CRS84, DefaultGeographicCRS.WGS84);
        assertNotEquals("Required condition for next test.", CRS84, DefaultGeographicCRS.WGS84);
        assertTrue("Required condition for next test.", CRS.equalsIgnoreMetadata(CRS84, DefaultGeographicCRS.WGS84));

        finder.setFullScanAllowed(false);
        assertNull(
                "Should not find WGS84 without a full scan, since it doesn't contains the CRS:84 identifier.",
                finder.find(DefaultGeographicCRS.WGS84));

        finder.setFullScanAllowed(true);
        assertSame(
                "A full scan should allow us to find WGS84, since it is equals ignoring metadata to CRS:84.",
                CRS84,
                finder.find(DefaultGeographicCRS.WGS84));

        finder.setFullScanAllowed(false);
        assertNull("The scan result should not be cached.", finder.find(DefaultGeographicCRS.WGS84));

        // --------------------------------------------------
        // Same test than above, using a CRS created from WKT
        // --------------------------------------------------

        String wkt = "GEOGCS[\"WGS 84\",\n"
                + "  DATUM[\"WGS84\",\n"
                + "    SPHEROID[\"WGS 84\", 6378137.0, 298.257223563]],\n"
                + "  PRIMEM[\"Greenwich\", 0.0],\n"
                + "  UNIT[\"degree\", 0.017453292519943295]]";
        CoordinateReferenceSystem search = CRS.parseWKT(wkt);
        assertNotEquals("Required condition for next test.", CRS84, search);
        assertTrue("Required condition for next test.", CRS.equalsIgnoreMetadata(CRS84, search));

        finder.setFullScanAllowed(false);
        assertNull(
                "Should not find WGS84 without a full scan, since it doesn't contains the CRS:84 identifier.",
                finder.find(search));

        finder.setFullScanAllowed(true);
        assertSame(
                "A full scan should allow us to find WGS84, since it is equals ignoring metadata to CRS:84.",
                CRS84,
                finder.find(search));

        assertEquals("OGC:84", finder.findIdentifier(search));
    }

    /**
     * Tests the {@link IdentifiedObjectFinder#find} method through a buffered authority factory. The objects found are
     * expected to be cached.
     */
    @Test
    public void testBufferedFind() throws FactoryException {
        final AbstractAuthorityFactory factory = new CachedCRSAuthorityDecorator(this.factory);
        final GeographicCRS CRS84 = factory.createGeographicCRS("OGC:84");
        final IdentifiedObjectFinder finder = factory.getIdentifiedObjectFinder(CoordinateReferenceSystem.class);

        finder.setFullScanAllowed(false);
        assertSame(
                "Should find without the need for scan, since we can use the CRS:84 identifier.",
                CRS84,
                finder.find(CRS84));

        finder.setFullScanAllowed(false);
        assertNull(
                "Should not find WGS84 without a full scan, since it doesn't contains the CRS:84 identifier.",
                finder.find(DefaultGeographicCRS.WGS84));

        finder.setFullScanAllowed(true);
        assertSame(
                "A full scan should allow us to find WGS84, since it is equals ignoring metadata to CRS:84.",
                CRS84,
                finder.find(DefaultGeographicCRS.WGS84));

        finder.setFullScanAllowed(false);
        assertSame(
                "At the contrary of testFind(), the scan result should be cached.",
                CRS84,
                finder.find(DefaultGeographicCRS.WGS84));

        assertEquals("OGC:84", finder.findIdentifier(DefaultGeographicCRS.WGS84));
    }
}
