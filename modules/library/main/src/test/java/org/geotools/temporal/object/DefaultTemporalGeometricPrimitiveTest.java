/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.temporal.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Calendar;
import java.util.Date;
import org.geotools.api.temporal.Duration;
import org.geotools.api.temporal.Instant;
import org.geotools.api.temporal.Position;
import org.geotools.api.temporal.TemporalGeometricPrimitive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** @author Mehdi Sidhoum (Geomatys) */
public class DefaultTemporalGeometricPrimitiveTest {

    private TemporalGeometricPrimitive temporalGeomericPrimitive1;
    private TemporalGeometricPrimitive temporalGeomericPrimitive2;
    private Position position1;
    private Position position2;
    private Calendar cal = Calendar.getInstance();

    @Before
    public void setUp() {

        cal.set(1981, 6, 25);
        Date date = cal.getTime();
        Calendar c2 = Calendar.getInstance();
        c2.set(2018, 4, 21);
        position1 = new DefaultPosition(date);
        position2 = new DefaultPosition(c2.getTime());
        temporalGeomericPrimitive1 = new DefaultInstant(position1);
        temporalGeomericPrimitive2 = new DefaultInstant(position2);
    }

    @After
    public void tearDown() {
        position1 = null;
        position2 = null;
        temporalGeomericPrimitive1 = null;
        temporalGeomericPrimitive2 = null;
    }

    /** Test of distance method, of class DefaultTemporalGeometricPrimitive. */
    @Test
    public void testDistance() {

        // calcul Distance with instant objects
        cal.set(2000, 0, 1);
        Position position = new DefaultPosition(cal.getTime());
        TemporalGeometricPrimitive other = new DefaultInstant(position);
        Duration result = temporalGeomericPrimitive1.distance(other);
        assertNotEquals(temporalGeomericPrimitive2.distance(other), result);

        // calcul Distance with instant and period
        cal.set(2009, 1, 1);
        Instant i1 = new DefaultInstant(new DefaultPosition(cal.getTime()));
        cal.set(2012, 1, 1);
        Instant i2 = new DefaultInstant(new DefaultPosition(cal.getTime()));
        other = new DefaultPeriod(i1, i2);
        result = temporalGeomericPrimitive1.distance(other);
        assertNotEquals(temporalGeomericPrimitive2.distance(other), result);

        // calcul Distance between Period objects
        temporalGeomericPrimitive1 = new DefaultPeriod(new DefaultInstant(position1), new DefaultInstant(position2));
        temporalGeomericPrimitive2 = new DefaultPeriod(i1, new DefaultInstant(position2));
        result = temporalGeomericPrimitive1.distance(other);
        assertEquals(temporalGeomericPrimitive2.distance(other), result);
    }

    /** Test of length method, of class DefaultTemporalGeometricPrimitive. */
    @Test
    public void testLength() {
        cal.set(2020, 0, 1);
        temporalGeomericPrimitive1 = new DefaultPeriod(new DefaultInstant(position1), new DefaultInstant(position2));
        temporalGeomericPrimitive2 = new DefaultPeriod(
                new DefaultInstant(position2), new DefaultInstant(new DefaultPosition(cal.getTime())));
        Duration result = temporalGeomericPrimitive1.length();
        assertNotEquals(temporalGeomericPrimitive2.length(), result);
    }

    /** Test comparison of Instants */
    @Test
    public void testCompare() {
        assertEquals(-1, ((DefaultTemporalPrimitive) temporalGeomericPrimitive1).compareTo(temporalGeomericPrimitive2));
        assertEquals(0, ((DefaultTemporalPrimitive) temporalGeomericPrimitive1).compareTo(temporalGeomericPrimitive1));
        assertEquals(0, ((DefaultTemporalPrimitive) temporalGeomericPrimitive2).compareTo(temporalGeomericPrimitive2));
    }
}
