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
package org.geotools.jdbc;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import org.geotools.api.data.FeatureWriter;
import org.geotools.api.data.Query;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.data.Transaction;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.DefaultFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Test;

public abstract class JDBCTransactionOnlineTest extends JDBCTestSupport {
    @Test
    public void testCommit() throws IOException {
        // JDBCFeatureStore fs = (JDBCFeatureStore) dataStore.getFeatureSource(tname("ft1"));

        try (Transaction tx = new DefaultTransaction();
                FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                        dataStore.getFeatureWriterAppend(tname("ft1"), tx)) {
            SimpleFeature feature = writer.next();
            feature.setAttribute(aname("intProperty"), Integer.valueOf(100));
            writer.write();
            writer.close();
            tx.commit();
        }

        SimpleFeatureCollection fc = dataStore.getFeatureSource(tname("ft1")).getFeatures();
        assertEquals(4, fc.size());
    }

    @Test
    public void testNoCommit() throws IOException {
        try (Transaction tx = new DefaultTransaction();
                FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                        dataStore.getFeatureWriterAppend(tname("ft1"), tx)) {
            SimpleFeature feature = writer.next();
            feature.setAttribute(aname("intProperty"), Integer.valueOf(100));
            writer.write();
            tx.rollback();
        }

        SimpleFeatureCollection fc = dataStore.getFeatureSource(tname("ft1")).getFeatures();
        assertEquals(3, fc.size());
    }

    @Test
    public void testConcurrentTransactions() throws IOException {
        try (Transaction tx1 = new DefaultTransaction();
                Transaction tx2 = new DefaultTransaction();
                FeatureWriter<SimpleFeatureType, SimpleFeature> w1 =
                        dataStore.getFeatureWriterAppend(tname("ft1"), tx1);
                FeatureWriter<SimpleFeatureType, SimpleFeature> w2 =
                        dataStore.getFeatureWriterAppend(tname("ft1"), tx2)) {

            SimpleFeature f1 = w1.next();
            SimpleFeature f2 = w2.next();

            f1.setAttribute(aname("intProperty"), Integer.valueOf(100));
            f2.setAttribute(aname("intProperty"), Integer.valueOf(101));

            w1.write();
            w2.write();

            w1.close();
            w2.close();

            tx1.commit();
            tx2.commit();
        }

        SimpleFeatureCollection fc = dataStore.getFeatureSource(tname("ft1")).getFeatures();
        assertEquals(5, fc.size());
    }

    @Test
    public void testSerialTransactions() throws IOException {
        SimpleFeatureStore st = (SimpleFeatureStore) dataStore.getFeatureSource(tname("ft1"));

        SimpleFeatureBuilder b = new SimpleFeatureBuilder(st.getSchema());
        b.set(aname("intProperty"), Integer.valueOf(100));
        SimpleFeature f1 = b.buildFeature(null);
        DefaultFeatureCollection features = new DefaultFeatureCollection(null, null);
        features.add(f1);

        try (Transaction tx1 = new DefaultTransaction()) {
            st.setTransaction(tx1);
            st.addFeatures(features);
            tx1.commit();
        }
        assertEquals(4, dataStore.getFeatureSource(tname("ft1")).getCount(Query.ALL));

        try (Transaction tx2 = new DefaultTransaction()) {
            st.setTransaction(tx2);
            st.addFeatures(features);
            tx2.commit();
        }
        assertEquals(5, dataStore.getFeatureSource(tname("ft1")).getCount(Query.ALL));
    }
}
