package org.geotools.geometry.jts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.geotools.api.geometry.MismatchedReferenceSystemException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.geometry.GeneralBounds;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultEngineeringCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.crs.DefaultProjectedCRS;
import org.geotools.util.factory.GeoTools;
import org.geotools.util.factory.Hints;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;

public class ReferencedEnvelopeTest {

    @Before
    public void setUp() throws Exception {
        // this is the only thing that actually forces CRS object to give up
        // its configuration, necessary when tests are run by Maven, one JVM for all
        // the tests in this module
        Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.FALSE);
        GeoTools.fireConfigurationChanged();
    }

    @Test
    public void testEverything() {
        ReferencedEnvelope everything = ReferencedEnvelope.EVERYTHING;
        ReferencedEnvelope world = new ReferencedEnvelope(ReferencedEnvelope.EVERYTHING);

        assertSame(everything, ReferencedEnvelope.EVERYTHING);
        assertNotSame(everything, world);
        assertEquals(everything, world);
        assertEquals(world, everything);

        assertFalse("This is not an empty envelope", everything.isEmpty());
        assertTrue("This is a null envelope", everything.isNull());

        Coordinate center = everything.centre();
        assertNotNull(center);

        double area = everything.getArea();
        assertTrue("area=" + area, Double.isInfinite(area));

        area = world.getArea();
        assertTrue("area=" + area, Double.isInfinite(area));

        try {
            everything.setBounds(new ReferencedEnvelope());
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            // ignore
        }
        everything.setToNull();
        everything.translate(1.0, 1.0);

        assertEquals(everything, world);
        assertEquals(world, everything);

        assertEquals(world.getMaximum(0), everything.getMaximum(0), 0.0);
        assertEquals(world.getMaximum(1), everything.getMaximum(1), 0.0);

        assertEquals(world.getMinimum(0), everything.getMinimum(0), 0.0);
        assertEquals(world.getMinimum(1), everything.getMinimum(1), 0.0);

        assertEquals(world.getMedian(0), everything.getMedian(0), 0.0);
        assertEquals(world.getMedian(1), everything.getMedian(0), 0.0);
    }

    @Test
    public void intersection() throws Exception {
        ReferencedEnvelope australia = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
        australia.include(40, 110);
        australia.include(10, 150);

        ReferencedEnvelope newZealand = new ReferencedEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        newZealand.include(50, 165);
        newZealand.include(33, 180);
        try {
            australia.intersection(newZealand);
            fail("Expected a mismatch of CoordinateReferenceSystem");
        } catch (MismatchedReferenceSystemException t) {
            // expected
        }
    }

    @Test
    public void include() throws Exception {
        ReferencedEnvelope australia = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
        australia.include(40, 110);
        australia.include(10, 150);

        ReferencedEnvelope newZealand = new ReferencedEnvelope(DefaultEngineeringCRS.CARTESIAN_2D);
        newZealand.include(50, 165);
        newZealand.include(33, 180);

        try {
            australia.expandToInclude(newZealand);
            fail("Expected a mismatch of CoordinateReferenceSystem");
        } catch (MismatchedReferenceSystemException t) {
            // expected
        }
        try {
            australia.include(newZealand);
            fail("Expected a mismatch of CoordinateReferenceSystem");
        } catch (MismatchedReferenceSystemException t) {
            // expected
        }
    }

    @Test
    public void empty() {
        // ensure empty can grab a default CRS when starting from nothing
        ReferencedEnvelope bbox = new ReferencedEnvelope(); // this is empty
        assertNull(bbox.getCoordinateReferenceSystem());

        ReferencedEnvelope australia = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
        australia.include(40, 110);
        australia.include(10, 150);

        bbox.include(australia);

        assertEquals(australia.getCoordinateReferenceSystem(), bbox.getCoordinateReferenceSystem());
    }

    @Test
    public void testBoundsEquals2D() {
        Rectangle2D bounds = new Rectangle2D.Double(-20.0, -20.0, 40.0, 40.0);

        ReferencedEnvelope env1 = new ReferencedEnvelope(bounds, null);
        ReferencedEnvelope env2 = new ReferencedEnvelope(bounds, null);
        double eps = 1.0e-4d;
        assertTrue(env1.boundsEquals2D(env2, eps));

        bounds = new Rectangle2D.Double(-20.01, -20.01, 40.0, 40.0);
        env2 = new ReferencedEnvelope(bounds, null);

        assertFalse(env1.boundsEquals2D(env2, eps));
    }

    @Test
    public void testFactoryMethod() throws Exception {
        try {
            new ReferencedEnvelope(DefaultGeographicCRS.WGS84_3D);
            fail("ReferencedEnvelope should not be able to represent 3D CRS such as GDA94");
        } catch (Exception expected) {
        }

        ReferencedEnvelope bounds2 = ReferencedEnvelope.create(DefaultGeographicCRS.WGS84_3D);
        assertNotNull(bounds2);
    }

    @Test
    public void testTransformToWGS84() throws Exception {
        String wkt = "GEOGCS[\"GDA94\","
                + " DATUM[\"Geocentric Datum of Australia 1994\","
                + "  SPHEROID[\"GRS 1980\", 6378137.0, 298.257222101, AUTHORITY[\"EPSG\",\"7019\"]],"
                + "  TOWGS84[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0], "
                + " AUTHORITY[\"EPSG\",\"6283\"]], "
                + " PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],"
                + " UNIT[\"degree\", 0.017453292519943295], "
                + " AXIS[\"Geodetic longitude\", EAST], "
                + " AXIS[\"Geodetic latitude\", NORTH], "
                + " AXIS[\"Ellipsoidal height\", UP], "
                + " AUTHORITY[\"EPSG\",\"4939\"]]";

        CoordinateReferenceSystem gda94 = CRS.parseWKT(wkt);

        ReferencedEnvelope bounds = new ReferencedEnvelope(
                130.875825803896, 130.898939990319, -16.4491956225999, -16.4338185791628, DefaultGeographicCRS.WGS84);

        ReferencedEnvelope worldBounds2D = bounds.transform(DefaultGeographicCRS.WGS84, true);
        assertEquals(DefaultGeographicCRS.WGS84, worldBounds2D.getCoordinateReferenceSystem());

        ReferencedEnvelope worldBounds3D = bounds.transform(DefaultGeographicCRS.WGS84_3D, true);
        assertEquals(DefaultGeographicCRS.WGS84_3D, worldBounds3D.getCoordinateReferenceSystem());

        ReferencedEnvelope gda94Bounds3D = bounds.transform(gda94, true);
        assertEquals(gda94, gda94Bounds3D.getCoordinateReferenceSystem());
    }

    @Test
    public void testExpandToIncludeNull() throws Exception {
        ReferencedEnvelope r1 = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
        ReferencedEnvelope r2 = new ReferencedEnvelope(DefaultGeographicCRS.WGS84);
        assertTrue(r1.isNull());
        assertTrue(r2.isNull());
        r1.expandToInclude(r2);
        assertTrue(r1.isNull());
    }

    /** Tests that the conversion of different bound types to ReferencedEnvelope does not lose the emptiness property */
    @Test
    public void testEmptyEnvelopeConversion() throws Exception {
        // conversion of an empty OGC envelope should stay empty
        GeneralBounds ge = new GeneralBounds(new double[] {0, 0}, new double[] {-1, -1});
        assertTrue(ge.isEmpty());
        assertTrue(
                ReferencedEnvelope.create(ge, ge.getCoordinateReferenceSystem()).isEmpty());
        assertTrue(ReferencedEnvelope.reference(ge).isEmpty());

        GeneralBounds bounds = new GeneralBounds(DefaultGeographicCRS.WGS84);
        assertTrue(bounds.isEmpty());
        assertTrue(ReferencedEnvelope.create(bounds, bounds.getCoordinateReferenceSystem())
                .isEmpty());
        assertTrue(ReferencedEnvelope.reference(bounds).isEmpty());

        // conversion of an empty Java Rectangle 2D should stay empty
        Rectangle2D r2d = new Rectangle2D.Double(0, 0, -1, -1);
        assertTrue(r2d.isEmpty());
        assertTrue(ReferencedEnvelope.create(r2d, null).isEmpty());
        assertTrue(ReferencedEnvelope.rect(r2d).isEmpty());
        assertTrue(ReferencedEnvelope.rect(r2d, DefaultGeographicCRS.WGS84).isEmpty());

        // conversion of an empty ReferencedEnvelope should stay empty
        ReferencedEnvelope re = new ReferencedEnvelope();
        assertTrue(re.isEmpty());
        assertTrue(ReferencedEnvelope.create(re).isEmpty());
        assertTrue(
                ReferencedEnvelope.create(re, re.getCoordinateReferenceSystem()).isEmpty());
        assertTrue(ReferencedEnvelope.reference(re).isEmpty());
    }

    @Test
    public void testWrappingEnvelopeConversion() throws Exception {
        GeneralBounds ge = new GeneralBounds(new double[] {160, 20}, new double[] {-160, 40});
        ge.setCoordinateReferenceSystem(DefaultGeographicCRS.WGS84);

        // used to convert to an empty envelope, which is wrong, the above is not empty,
        // it's just spanning the dateline. ReferencedEnvelope cannot represent that case,
        // but we can at least make sure it doesn't convert to an empty envelope and use whole
        // world instead
        ReferencedEnvelope re = ReferencedEnvelope.reference(ge);
        assertEquals(DefaultGeographicCRS.WGS84, re.getCoordinateReferenceSystem());
        assertEquals(-180, re.getMinX(), 0d);
        assertEquals(180, re.getMaxX(), 0d);
        assertEquals(20, re.getMinY(), 0d);
        assertEquals(40, re.getMaxY(), 0d);
    }

    @Test
    public void testCompatibleCRSEnvelopeIntersection() throws Exception {
        ReferencedEnvelope env1 = createEnvelope(
                -2.177465925706197E7,
                8646206.01995729,
                -1.8020943797479007E7,
                1.2385261999043737E7,
                "EPSG:3857",
                false);
        ReferencedEnvelope env2 = createEnvelope(
                -2.1788686706639238E7,
                8621919.786483308,
                -1.801691811921271E7,
                1.2394009693069756E7,
                "EPSG:3857",
                true);
        CoordinateReferenceSystem crs1 = env1.getCoordinateReferenceSystem();
        CoordinateReferenceSystem crs2 = env2.getCoordinateReferenceSystem();
        assertNotSame(
                ((DefaultProjectedCRS) crs1)
                        .getBaseCRS()
                        .getCoordinateSystem()
                        .getAxis(0)
                        .getDirection(),
                ((DefaultProjectedCRS) crs2)
                        .getBaseCRS()
                        .getCoordinateSystem()
                        .getAxis(0)
                        .getDirection());
        assertTrue(CRS.isEquivalent(crs1, crs2));
        ReferencedEnvelope re = env1.intersection(env2);
        assertNotNull(re);
    }

    @Test(expected = MismatchedReferenceSystemException.class)
    public void testIncompatibleCRSEnvelopeOperation() throws Exception {
        ReferencedEnvelope env1 = createEnvelope(
                -2.177465925706197E7,
                8646206.01995729,
                -1.8020943797479007E7,
                1.2385261999043737E7,
                "EPSG:3857",
                false);
        ReferencedEnvelope env2 = createEnvelope(
                -2.1788686706639238E7,
                8621919.786483308,
                -1.801691811921271E7,
                1.2394009693069756E7,
                "EPSG:32632",
                true);
        assertFalse(CRS.isEquivalent(env1.getCoordinateReferenceSystem(), env2.getCoordinateReferenceSystem()));
        // The intersection will throw a MismatchedReferenceSystemException
        env1.expandToInclude(env2);
    }

    private ReferencedEnvelope createEnvelope(
            double minX, double minY, double maxX, double maxY, String epsgCode, boolean longitudeFirst)
            throws FactoryException {
        GeneralBounds ge = new GeneralBounds(new double[] {minX, minY}, new double[] {maxX, maxY});
        CoordinateReferenceSystem crs = CRS.decode(epsgCode, longitudeFirst);
        ge.setCoordinateReferenceSystem(crs);
        return ReferencedEnvelope.reference(ge);
    }

    /**
     * This method tests the different ways of initializing a ReferencedEnvelope in a manner consistent with behavior of
     * {@link Rectangle2D}
     */
    @Test
    public void testRectangle2DBehaviour() throws Exception {
        Rectangle2D rectangle = new Rectangle2D.Double(10.0, 10.0, 40.0, 30.0);
        ReferencedEnvelope envelope =
                ReferencedEnvelope.rect(10.0, 10.0, 40.0, 30.0, DefaultEngineeringCRS.CARTESIAN_2D);
        assertEquals("rect", ReferencedEnvelope.rect(rectangle, DefaultEngineeringCRS.CARTESIAN_2D), envelope);

        assertEquals("x", rectangle.getMinX(), envelope.getMinX(), 0.0);
        assertEquals("height", rectangle.getHeight(), envelope.getHeight(), 0.0);

        Point2D center = new Point2D.Double(50.0, 50.0);
        Point2D outer = new Point2D.Double(85.0, 15.0);
        rectangle.setFrameFromCenter(center, outer);
        envelope.setFrameFromCenter(center, outer);
        assertEquals(
                "setFrameFromCenter", ReferencedEnvelope.rect(rectangle, DefaultEngineeringCRS.CARTESIAN_2D), envelope);

        Point2D lower = new Point2D.Double(10.0, 10.0);
        Point2D upper = new Point2D.Double(40.0, 30.0);

        rectangle.setFrameFromDiagonal(lower, upper);
        envelope.setFrameFromDiagonal(lower, upper);
        assertEquals(
                "setFrameFromDiagonal",
                ReferencedEnvelope.rect(rectangle, DefaultEngineeringCRS.CARTESIAN_2D),
                envelope);
    }

    @After
    public void tearDown() throws Exception {
        Hints.putSystemDefault(Hints.FORCE_LONGITUDE_FIRST_AXIS_ORDER, Boolean.TRUE);
        GeoTools.fireConfigurationChanged();
    }
}
