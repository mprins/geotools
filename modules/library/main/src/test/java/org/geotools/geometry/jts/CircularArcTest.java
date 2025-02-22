/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2014, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.geometry.jts;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.locationtech.jts.algorithm.Orientation;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

public class CircularArcTest {

    static final Coordinate ORIGIN = new Coordinate(0, 0);

    static final int COUNTER_CLOCKWISE = 1;

    static final int COLLINEAR = 0;

    static final int CLOCKWISE = -1;

    @BeforeClass
    public static void setupBaseSegmentsQuadrant() {
        // we want to run the test at a higher precision
        CircularArc.setBaseSegmentsQuadrant(32);
    }

    @AfterClass
    public static void resetBaseSegmentsQuadrant() {
        // we want to run the test at a higher precision
        CircularArc.setBaseSegmentsQuadrant(12);
    }

    Envelope envelopeFrom(CircularArc arc, double... otherPoints) {
        Envelope env = new Envelope();
        // add the control points
        env.expandToInclude(arc.controlPoints[0], arc.controlPoints[1]);
        env.expandToInclude(arc.controlPoints[2], arc.controlPoints[3]);
        env.expandToInclude(arc.controlPoints[4], arc.controlPoints[5]);
        if (otherPoints != null) {
            // add the other points
            for (int i = 0; i < otherPoints.length; ) {
                env.expandToInclude(otherPoints[i++], otherPoints[i++]);
            }
        }

        return env;
    }

    static void assertCoordinateEquals(Coordinate expected, Coordinate actual) {
        if (expected == null) {
            assertNull(actual);
        } else {
            assertEquals(expected.x, actual.x, Circle.EPS);
            assertEquals(expected.y, actual.y, Circle.EPS);
        }
    }

    @Test
    public void testCollinear() {
        CircularArc arc = new CircularArc(0, 0, 0, 10, 0, 20);
        assertEquals(CircularArc.COLLINEARS, arc.getRadius(), 0d);
        assertCoordinateEquals(null, arc.getCenter());
        double[] linearized = arc.linearize(0);
        assertArrayEquals(new double[] {0, 0, 0, 10, 0, 20}, linearized, 0d);
        assertEquals(envelopeFrom(arc), arc.getEnvelope());
    }

    @Test
    public void testSamePoints() {
        CircularArc arc = new CircularArc(0, 0, 0, 0, 0, 0);
        assertEquals(0, arc.getRadius(), 0d);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        double[] linearized = arc.linearize(0);
        assertArrayEquals(new double[] {0, 0, 0, 0, 0, 0}, linearized, 0d);
        assertEquals(envelopeFrom(arc), arc.getEnvelope());
        assertEquals(0, arc.getEnvelope().getArea(), 0d);
    }

    @Test
    public void testMinuscule() {
        Circle circle = new Circle(100);
        CircularArc arc = circle.getCircularArc(0, CircularArc.HALF_PI / 128, CircularArc.HALF_PI / 64);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize with a large tolerance, we should get back just the control points
        assertArrayEquals(arc.getControlPoints(), arc.linearize(10), 0d);
        assertEquals(envelopeFrom(arc), arc.getEnvelope());
    }

    @Test
    public void testMatchingSequence() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        CircularArc arc = circle.getCircularArc(0, CircularArc.HALF_PI / 32, CircularArc.HALF_PI / 16);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize with a large tolerance, we should get back just the control points
        assertArrayEquals(arc.getControlPoints(), arc.linearize(10), 0d);
    }

    @Test
    public void testOutsideSequence() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        double halfStep = CircularArc.HALF_PI / 64;
        CircularArc arc = circle.getCircularArc(halfStep, halfStep * 3, halfStep * 5);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize, we should get back the control points, plus the regular points in the middle
        double[] expected = circle.samplePoints(halfStep, halfStep * 2, halfStep * 3, halfStep * 4, halfStep * 5);
        assertArrayEquals(expected, arc.linearize(0.1), Circle.EPS);
    }

    @Test
    public void testOutsideSequenceClockwise() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        double halfStep = CircularArc.HALF_PI / 64;
        CircularArc arc = circle.getCircularArc(halfStep * 5, halfStep * 3, halfStep);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize, we should get back the control points, plus the regular points in the middle
        double[] expected = circle.samplePoints(halfStep * 5, halfStep * 4, halfStep * 3, halfStep * 2, halfStep);
        assertArrayEquals(expected, arc.linearize(0.1), Circle.EPS);
        assertEquals(envelopeFrom(arc), arc.getEnvelope());
    }

    @Test
    public void testStartMatchSequence() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        double halfStep = CircularArc.HALF_PI / 64;
        CircularArc arc = circle.getCircularArc(0, halfStep * 3, halfStep * 5);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize, we should get back the control points, plus the regular points in the middle
        double[] expected = circle.samplePoints(0, halfStep * 2, halfStep * 3, halfStep * 4, halfStep * 5);
        assertArrayEquals(expected, arc.linearize(0.2), Circle.EPS);
    }

    @Test
    public void testMidMatchSequence() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        double halfStep = CircularArc.HALF_PI / 64;
        CircularArc arc = circle.getCircularArc(halfStep, halfStep * 2, halfStep * 5);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize, we should get back the control points, plus the regular points in the middle
        double[] expected = circle.samplePoints(halfStep, halfStep * 2, halfStep * 4, halfStep * 5);
        assertArrayEquals(expected, arc.linearize(0.2), Circle.EPS);
    }

    @Test
    public void testEndMatchSequence() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        double halfStep = CircularArc.HALF_PI / 64;
        CircularArc arc = circle.getCircularArc(halfStep, halfStep * 3, halfStep * 4);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize, we should get back only control points, plus the regular points in the middle
        double[] expected = circle.samplePoints(halfStep, halfStep * 3, halfStep * 4);
        assertArrayEquals(expected, arc.linearize(10), Circle.EPS);
    }

    @Test
    public void testMatchTolerance() {
        Circle circle = new Circle(100);
        CircularArc arc = circle.getCircularArc(0, Math.PI / 2, Math.PI);
        // test with subsequently smaller tolerances, but avoid going too low or
        // numerical issues will byte us, this one stops roughly at 2.4e-7
        double tolerance = 1;
        for (int i = 0; i < 12; i++) {
            double[] linearized = arc.linearize(tolerance);
            // System.out.println(tolerance + " --> " + linearized.length);
            assertTrue(linearized.length >= 64);
            circle.assertTolerance(linearized, tolerance);
            tolerance /= 4;
        }
    }

    @Test
    public void testMatchToleranceClockwise() {
        Circle circle = new Circle(100);
        CircularArc arc = circle.getCircularArc(Math.PI, Math.PI / 2, 0);
        // test with subsequently smaller tolerances, but avoid going too low or
        // numerical issues will byte us, this one stops roughly at 2.4e-7
        double tolerance = 1;
        for (int i = 0; i < 12; i++) {
            double[] linearized = arc.linearize(tolerance);
            // System.out.println(tolerance + " --> " + linearized.length);
            assertTrue(linearized.length >= 64);
            circle.assertTolerance(linearized, tolerance);
            tolerance /= 4;
        }
    }

    @Test
    public void testCrossPIPI() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        double step = CircularArc.HALF_PI / 32;
        CircularArc arc = circle.getCircularArc(-step * 2, step, step * 2);
        assertEquals(100, arc.getRadius(), 1e-9);
        assertCoordinateEquals(ORIGIN, arc.getCenter());
        // linearize, we should get back the control points, plus the regular points in the middle
        double[] expected = circle.samplePoints(-step * 2, -step, 0, step, step * 2);
        assertArrayEquals(expected, arc.linearize(0.2), Circle.EPS);
        assertEquals(envelopeFrom(arc, 100, 0), arc.getEnvelope());
    }

    @Test
    public void testFullCircle() {
        Circle circle = new Circle(100);
        // create control points that will match exactly the points the algo should generate
        CircularArc arc = circle.getCircularArc(0, Math.PI, 0);
        assertEquals(envelopeFrom(arc, 100, 0, 0, 100, -100, 0, 0, -100), arc.getEnvelope());
    }

    @Test
    public void testOrientations() {
        Circle circle = new Circle(100);
        // half circle up
        assertEquals(COUNTER_CLOCKWISE, getOrientationIndex(getLinearizedArc(circle, 0, Math.PI / 2, Math.PI)));
        assertEquals(CLOCKWISE, getOrientationIndex(getLinearizedArc(circle, Math.PI, Math.PI / 2, 0)));
        assertEquals(CLOCKWISE, getOrientationIndex(getLinearizedArc(circle, -Math.PI, Math.PI / 2, 0)));
        // half circle down
        assertEquals(COUNTER_CLOCKWISE, getOrientationIndex(getLinearizedArc(circle, Math.PI, Math.PI * 3 / 2, 0)));
        assertEquals(CLOCKWISE, getOrientationIndex(getLinearizedArc(circle, 0, Math.PI * 3 / 2, Math.PI)));
        assertEquals(CLOCKWISE, getOrientationIndex(getLinearizedArc(circle, 0, -Math.PI / 2, -Math.PI)));
        // end between start and mid, wrapping
        assertEquals(
                COUNTER_CLOCKWISE,
                getOrientationIndex(getLinearizedArc(circle, Math.PI / 2, Math.PI / 4, Math.PI * 3 / 8)));
        assertEquals(
                CLOCKWISE, getOrientationIndex(getLinearizedArc(circle, Math.PI * 3 / 8, Math.PI / 4, Math.PI / 2)));
    }

    private int getOrientationIndex(LineString ls) {
        return Orientation.index(ls.getCoordinateN(0), ls.getCoordinateN(1), ls.getCoordinateN(2));
    }

    private LineString getLinearizedArc(Circle c, double startAngle, double midAngle, double endAngle) {
        CircularArc arc = c.getCircularArc(startAngle, midAngle, endAngle);
        double[] linearized = arc.linearize(Double.MAX_VALUE);
        Coordinate[] coords = new Coordinate[linearized.length / 2];
        for (int i = 0; i < coords.length; i++) {
            coords[i] = new Coordinate(linearized[i * 2], linearized[i * 2 + 1]);
        }
        CoordinateArraySequence cs = new CoordinateArraySequence(coords);
        return new LineString(cs, new GeometryFactory());
    }

    @Test
    public void testGeot7333() {
        // Correctly rounded center and radius computed with an arbitrary precision library
        Coordinate CENTER = new Coordinate(3.85765333609684929e+07, 2.54452296045322483e+06);
        double RADIUS = 3.99598457401221629e-01;

        // We test 3 permutations of the control points to cover all branches in the center
        // computation routine.
        CircularArc arc1 = new CircularArc(
                3.8576532966E7,
                2544522.8998000007,
                3.8576533116106E7,
                2544523.27624,
                3.85765334913E7,
                2544523.338199999);
        assertCoordinateEquals(CENTER, arc1.getCenter());
        assertEquals(RADIUS, arc1.getRadius(), 5e-9);

        CircularArc arc2 = new CircularArc(
                3.8576532966E7,
                2544522.8998000007,
                3.85765334913E7,
                2544523.338199999,
                3.8576533116106E7,
                2544523.27624);
        assertCoordinateEquals(CENTER, arc2.getCenter());
        assertEquals(RADIUS, arc2.getRadius(), 5e-9);

        CircularArc arc3 = new CircularArc(
                3.8576533116106E7,
                2544523.27624,
                3.8576532966E7,
                2544522.8998000007,
                3.85765334913E7,
                2544523.338199999);
        assertCoordinateEquals(CENTER, arc3.getCenter());
        assertEquals(RADIUS, arc3.getRadius(), 5e-9);
    }
}
