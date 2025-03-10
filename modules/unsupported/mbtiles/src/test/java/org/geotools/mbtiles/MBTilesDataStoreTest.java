/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2019, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.mbtiles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.geotools.api.data.DataSourceException;
import org.geotools.api.data.DataStore;
import org.geotools.api.data.DataStoreFinder;
import org.geotools.api.data.FeatureReader;
import org.geotools.api.data.Query;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.referencing.FactoryException;
import org.geotools.data.store.ContentFeatureSource;
import org.geotools.feature.NameImpl;
import org.geotools.jdbc.JDBCDataStoreFactory;
import org.geotools.referencing.CRS;
import org.geotools.util.URLs;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class MBTilesDataStoreTest {

    private static final String DATATYPES = "datatypes";

    // @Rule public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testRasterTiles() throws Exception {
        File file = URLs.urlToFile(this.getClass().getResource("mosaic/world_lakes.mbtiles"));
        assertThrows(DataSourceException.class, () -> new MBTilesDataStore(new MBTilesFile(file)));
    }

    @Test
    public void testPBFNoSchemaFile() throws Exception {
        File file = URLs.urlToFile(this.getClass().getResource("planet.mbtiles"));
        assertThrows(DataSourceException.class, () -> new MBTilesDataStore(new MBTilesFile(file)));
    }

    @Test
    public void testDataTypes() throws IOException, FactoryException {
        File file = URLs.urlToFile(this.getClass().getResource("datatypes.mbtiles"));
        MBTilesDataStore store = new MBTilesDataStore(new MBTilesFile(file));
        assertThat(store.getTypeNames(), arrayContaining("datatypes"));

        ContentFeatureSource fs = store.getFeatureSource(DATATYPES);
        assertNotNull(fs);

        // check the schema
        SimpleFeatureType schema = fs.getSchema();
        assertThat(schema.getTypeName(), equalTo(DATATYPES));

        // the default geometry
        GeometryDescriptor geom = schema.getGeometryDescriptor();
        assertThat(geom.getLocalName(), equalTo("the_geom"));
        assertThat(geom.getCoordinateReferenceSystem(), equalTo(CRS.decode("EPSG:3857", true)));

        // check boolean
        AttributeDescriptor bd = schema.getDescriptor("bool_true");
        assertThat(bd.getType().getBinding(), equalTo(Boolean.class));

        // check number (there is no distinction of subtypes, everything is "Number"
        AttributeDescriptor fd = schema.getDescriptor("float_value");
        assertThat(fd.getType().getBinding(), equalTo(Number.class));

        AttributeDescriptor sd = schema.getDescriptor("string_value");
        assertThat(sd.getType().getBinding(), equalTo(String.class));
    }

    @Test
    public void readSingle() throws IOException, ParseException {
        File file = URLs.urlToFile(this.getClass().getResource("datatypes.mbtiles"));
        MBTilesDataStore store = new MBTilesDataStore(new MBTilesFile(file));
        try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                store.getFeatureReader(new Query("datatypes"), Transaction.AUTO_COMMIT)) {
            assertTrue(reader.hasNext());
            SimpleFeature feature = reader.next();
            assertThat(feature.getAttribute("bool_false"), equalTo(false));
            assertThat(feature.getAttribute("bool_true"), equalTo(true));
            assertThat(((Number) feature.getAttribute("float_value")).doubleValue(), closeTo(1.25, 0.01));
            assertThat(feature.getAttribute("int64_value"), equalTo(123456789012345L));
            assertThat(feature.getAttribute("neg_int_value"), equalTo(-1L));
            assertThat(feature.getAttribute("pos_int_value"), equalTo(1L));
            assertThat(feature.getAttribute("string_value"), equalTo("str"));
            Point expected = (Point) new WKTReader().read("POINT (215246.671651058 6281289.23636264)");
            Point actual = (Point) feature.getDefaultGeometry();
            assertTrue(actual.equalsExact(expected, 0.01));
        }
    }

    @Test
    public void testFactory() throws IOException {
        String namespaceURI = "http://geotools.org/mbtiles";
        File file = URLs.urlToFile(this.getClass().getResource("datatypes.mbtiles"));
        Map<String, Serializable> params = new HashMap<>();
        params.put(MBTilesDataStoreFactory.DBTYPE.key, "mbtiles");
        params.put(MBTilesDataStoreFactory.DATABASE.key, file);
        params.put(JDBCDataStoreFactory.NAMESPACE.key, namespaceURI);
        DataStore store = DataStoreFinder.getDataStore(params);
        assertNotNull(store);
        assertThat(store, Matchers.instanceOf(MBTilesDataStore.class));
        assertThat(store.getTypeNames(), arrayContaining("datatypes"));
        SimpleFeatureType schema = store.getSchema("datatypes");
        NameImpl qualifiedName = new NameImpl(namespaceURI, "datatypes");
        assertThat(schema.getName(), equalTo(qualifiedName));
        SimpleFeatureType schemaFromQualified = store.getSchema(qualifiedName);
        assertThat(schema, equalTo(schemaFromQualified));
    }
}
