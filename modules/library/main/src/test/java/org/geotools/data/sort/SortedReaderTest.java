package org.geotools.data.sort;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.NoSuchElementException;
import org.geotools.api.data.SimpleFeatureReader;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.sort.SortBy;
import org.geotools.api.filter.sort.SortOrder;
import org.geotools.data.simple.DelegateSimpleFeatureReader;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

public class SortedReaderTest {

    SimpleFeatureReader fr;

    FilterFactory ff;

    SortBy[] peopleAsc;

    SortBy[] peopleDesc;

    SortBy[] fidAsc;

    SortBy[] nullAsc;

    SimpleFeatureType schema;

    DefaultFeatureCollection fc;

    private SortBy[] dateAsc;

    @Before
    public void setup() throws IOException {
        SimpleFeatureTypeBuilder typeBuilder = new SimpleFeatureTypeBuilder();

        typeBuilder.setName("test");
        typeBuilder.setNamespaceURI("test");
        typeBuilder.setCRS(DefaultGeographicCRS.WGS84);
        typeBuilder.add("defaultGeom", Point.class, DefaultGeographicCRS.WGS84);
        typeBuilder.add("PERSONS", Integer.class);
        typeBuilder.add("byte", Byte.class);
        typeBuilder.add("short", Short.class);
        typeBuilder.add("long", Long.class);
        typeBuilder.add("float", Float.class);
        typeBuilder.add("double", Double.class);
        typeBuilder.add("date", Date.class);
        typeBuilder.add("sql_date", java.sql.Date.class);
        typeBuilder.add("sql_time", java.sql.Time.class);
        typeBuilder.add("sql_timestamp", java.sql.Timestamp.class);
        typeBuilder.add("otherGeom", LineString.class);
        typeBuilder.setDefaultGeometry("defaultGeom");

        schema = typeBuilder.buildFeatureType();

        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(schema);

        GeometryFactory gf = new GeometryFactory();
        fc = new DefaultFeatureCollection("test", schema);

        double x = -140;
        double y = 45;
        final int features = 500;
        for (int i = 0; i < features; i++) {
            Point point = gf.createPoint(new Coordinate(x + i, y + i));
            point.setUserData(DefaultGeographicCRS.WGS84);

            builder.add(point);
            builder.add(Integer.valueOf(i));
            builder.add(Byte.valueOf((byte) i));
            builder.add(Short.valueOf((short) i));
            builder.add(Long.valueOf(i));
            builder.add(Float.valueOf(i));
            builder.add(Double.valueOf(i));
            builder.add(new Date());
            builder.add(new java.sql.Date(System.currentTimeMillis()));
            builder.add(new java.sql.Time(System.currentTimeMillis()));
            builder.add(new java.sql.Timestamp(System.currentTimeMillis()));

            LineString line = gf.createLineString(
                    new Coordinate[] {new Coordinate(x + i, y + i), new Coordinate(x + i + 1, y + i + 1)});
            line.setUserData(DefaultGeographicCRS.WGS84);
            builder.add(line);

            fc.add(builder.buildFeature(i + ""));
        }

        // add a feature with a null geometry
        builder.add(null);
        builder.add(Integer.valueOf(-1));
        builder.add(null);
        fc.add(builder.buildFeature((features + 1) + ""));

        fr = new DelegateSimpleFeatureReader(schema, fc.features());

        ff = CommonFactoryFinder.getFilterFactory(null);
        peopleAsc = new SortBy[] {ff.sort("PERSONS", SortOrder.ASCENDING)};
        peopleDesc = new SortBy[] {ff.sort("PERSONS", SortOrder.DESCENDING)};
        dateAsc = new SortBy[] {ff.sort("date", SortOrder.ASCENDING)};
        fidAsc = new SortBy[] {SortBy.NATURAL_ORDER};
        nullAsc = new SortBy[] {ff.sort("null", SortOrder.ASCENDING)};
    }

    @After
    public void tearDown() throws IOException {
        fr.close();
    }

    @Test
    public void testCanSort() {
        assertTrue(SortedFeatureReader.canSort(schema, peopleAsc));
        assertTrue(SortedFeatureReader.canSort(schema, peopleDesc));
        assertTrue(SortedFeatureReader.canSort(schema, fidAsc));
        assertFalse(SortedFeatureReader.canSort(schema, nullAsc));
    }

    @Test
    public void testMemorySort() throws IOException {
        // make it so that we are not going to hit the disk
        try (SimpleFeatureReader sr = new SortedFeatureReader(fr, peopleAsc, 1000)) {
            assertSortedOnPeopleAsc(sr);
        }
    }

    @Test
    public void testFileSortDate() throws IOException {
        // make it so that we are going to hit the disk
        try (SimpleFeatureReader sr = new SortedFeatureReader(fr, dateAsc, 100)) {
            assertSortedOnDateAsc(sr);
        }
    }

    @Test
    public void testFileSortPeople() throws IOException {
        // make it so that we are going to hit the disk
        try (SimpleFeatureReader sr = new SortedFeatureReader(fr, peopleAsc, 5)) {
            assertSortedOnPeopleAsc(sr);
        }
    }

    @Test
    public void testIteratorSortReduce() throws IOException {
        // make it so that we are not going to hit the disk
        try (SimpleFeatureIterator fi = new SortedFeatureIterator(fc.features(), schema, peopleAsc, 1000)) {
            assertSortedOnPeopleAsc(fi);
        }
    }

    @Test
    public void testSortDescending() throws IOException {
        // make it so that we are not going to hit the disk
        try (SimpleFeatureReader sr = new SortedFeatureReader(fr, peopleDesc, 1000)) {
            double prev = -1;
            while (sr.hasNext()) {
                SimpleFeature f = sr.next();
                int curr = (Integer) f.getAttribute("PERSONS");
                if (prev > 0) {
                    assertTrue(curr <= prev);
                }
                prev = curr;
            }
        }
    }

    @Test
    public void testSortNatural() throws IOException {
        // make it so that we are not going to hit the disk
        try (SimpleFeatureReader sr = new SortedFeatureReader(fr, fidAsc, 1000)) {
            String prev = null;
            while (sr.hasNext()) {
                SimpleFeature f = sr.next();
                String id = f.getID();
                if (prev != null) {
                    assertTrue(id.compareTo(prev) >= 0);
                }
                prev = id;
            }
        }
    }

    @Test
    public void testSortNaturalPartialLastPage() throws IOException {
        // make it so that we are not going to hit the disk, but
        // some of the data won't fit in the last page, used to be
        // left in memory and forgotten
        final int PRIME = 173;
        try (SimpleFeatureReader sr = new SortedFeatureReader(fr, fidAsc, PRIME)) {
            String prev = null;
            int count = 0;
            while (sr.hasNext()) {
                SimpleFeature f = sr.next();
                String id = f.getID();
                if (prev != null) {
                    assertTrue(id.compareTo(prev) >= 0);
                }
                prev = id;
                count++;
            }
            assertEquals(fc.size(), count);
        }
    }

    private void assertSortedOnPeopleAsc(SimpleFeatureReader fr)
            throws IllegalArgumentException, NoSuchElementException, IOException {
        double prev = -1;
        while (fr.hasNext()) {
            SimpleFeature f = fr.next();
            int curr = (Integer) f.getAttribute("PERSONS");
            if (prev > 0) {
                assertTrue(curr >= prev);
            }
            prev = curr;
        }
    }

    private void assertSortedOnDateAsc(SimpleFeatureReader fr)
            throws IllegalArgumentException, NoSuchElementException, IOException {
        Date prev = null;
        while (fr.hasNext()) {
            SimpleFeature f = fr.next();
            Date curr = (Date) f.getAttribute("date");
            if (prev != null) {
                assertTrue(prev.compareTo(curr) <= 0);
            }
            prev = curr;
        }
    }

    private void assertSortedOnPeopleAsc(SimpleFeatureIterator fi)
            throws IllegalArgumentException, NoSuchElementException, IOException {
        double prev = -1;
        while (fi.hasNext()) {
            SimpleFeature f = fi.next();
            int curr = (Integer) f.getAttribute("PERSONS");
            if (prev > 0) {
                assertTrue(curr >= prev);
            }
            prev = curr;
        }
    }
}
