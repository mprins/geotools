/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2017, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.mysql;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.geotools.jdbc.JDBCTestSetup;
import org.geotools.jdbc.JDBCTestSupport;
import org.junit.Test;

/**
 * Tests that enhandedSpatialSupport flag is automatically and properly set based on identified MySQL version.
 *
 * @author Justin Deoliveira, The Open Planning Project
 */
public class MySQLDataStoreAutoEnhandedSpatialSupportTest extends JDBCTestSupport {
    @Override
    protected JDBCTestSetup createTestSetup() {
        return new MySQLTestSetup();
    }

    @Test
    public void testEnhancedSpatialSupportDetection() throws Exception {
        boolean isMySQL56 = MySQLDataStoreFactory.isMySqlVersion56OrAbove(dataStore);
        boolean isMySQL80 = MySQLDataStoreFactory.isMySqlVersion80OrAbove(dataStore);
        if (isMySQL56) {
            assertTrue(((MySQLDialectBasic) dialect).getUsePreciseSpatialOps());
        } else if (isMySQL80) {
            assertTrue(((MySQLDialectBasic) dialect).getUsePreciseSpatialOps());
        } else {
            assertFalse(((MySQLDialectBasic) dialect).getUsePreciseSpatialOps());
        }
    }
}
