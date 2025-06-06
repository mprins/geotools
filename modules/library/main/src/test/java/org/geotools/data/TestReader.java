/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2003-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data;

import java.io.IOException;
import java.util.NoSuchElementException;
import org.geotools.api.data.FeatureReader;
import org.geotools.api.feature.IllegalAttributeException;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;

class TestReader implements FeatureReader<SimpleFeatureType, SimpleFeature> {

    /** */
    private SimpleFeatureType type;

    private SimpleFeature feature;

    public TestReader(SimpleFeatureType type, SimpleFeature f) {
        this.type = type;
        this.feature = f;
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return type;
    }

    @Override
    public SimpleFeature next() throws IOException, IllegalAttributeException, NoSuchElementException {
        next = false;
        return feature;
    }

    boolean next = true;

    @Override
    public boolean hasNext() throws IOException {
        return next;
    }

    @Override
    public void close() throws IOException {}
}
