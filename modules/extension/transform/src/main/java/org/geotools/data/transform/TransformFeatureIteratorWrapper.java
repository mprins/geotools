/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2012 - 2015, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.data.transform;

import java.io.IOException;
import java.util.logging.Logger;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.filter.expression.Expression;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.FeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.util.logging.Logging;

/**
 * A transforming iterator based on a user provided {@link SimpleFeatureIterator}
 *
 * @author Andrea Aime - GeoSolutions
 */
class TransformFeatureIteratorWrapper implements SimpleFeatureIterator {

    static final Logger LOGGER = Logging.getLogger(TransformFeatureIteratorWrapper.class);

    private SimpleFeatureBuilder fb;

    private FeatureIterator<SimpleFeature> wrapped;

    private Transformer transformer;

    private SimpleFeatureType target;

    public TransformFeatureIteratorWrapper(FeatureIterator<SimpleFeature> wrapped, Transformer transformer)
            throws IOException {
        this.transformer = transformer;
        this.target = transformer.getSchema();
        this.wrapped = wrapped;

        // prepare the feature builder
        this.fb = new SimpleFeatureBuilder(target);
    }

    @Override
    public boolean hasNext() {
        return wrapped.hasNext();
    }

    @Override
    public SimpleFeature next() {
        SimpleFeature f = wrapped.next();

        for (AttributeDescriptor ad : target.getAttributeDescriptors()) {
            Expression ex = transformer.getExpression(ad.getLocalName());
            if (ex != null) {
                Object value = ex.evaluate(f, ad.getType().getBinding());
                fb.add(value);
            } else {
                fb.add(null);
            }
        }
        fb.featureUserData(f);

        return fb.buildFeature(transformer.transformFid(f));
    }

    @Override
    public void close() {
        if (wrapped != null) {
            wrapped.close();
        }
        wrapped = null;
    }
}
