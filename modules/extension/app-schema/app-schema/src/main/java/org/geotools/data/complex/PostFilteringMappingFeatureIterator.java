/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
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

package org.geotools.data.complex;

import java.util.NoSuchElementException;
import org.geotools.api.feature.Feature;
import org.geotools.api.filter.Filter;
import org.geotools.feature.FeatureIterator;

/**
 * An extension to {@linkplain org.geotools.data.complex.DataAccessMappingFeatureIterator} where filter is present.
 * Unlike with FilteringMappingFeatureIterator The filter is applied on the complex feature
 *
 * @author Niels Charlier (Curtin University of Technology)
 */
public class PostFilteringMappingFeatureIterator implements IMappingFeatureIterator {

    protected FeatureIterator<Feature> delegate;
    protected Feature next;
    protected Filter filter;
    protected int maxFeatures;
    protected int count = 0;

    public PostFilteringMappingFeatureIterator(
            FeatureIterator<Feature> iterator, Filter filter, int maxFeatures, int offset) {
        this.delegate = iterator;
        this.filter = filter;
        this.maxFeatures = maxFeatures;
        int startIndex = -1;
        while (startIndex < offset) {
            next = getFilteredNext();
            startIndex++;
        }
    }

    @Override
    public void close() {
        delegate.close();
    }

    protected Feature getFilteredNext() {
        while (delegate.hasNext() && count < maxFeatures) {
            Feature feature = delegate.next();
            try {
                if (filter.evaluate(feature)) {
                    return feature;
                }
            } catch (NullPointerException e) {
                // ignore this exception
                // this is to cater the case if the attribute has no value and
                // has been skipped in the delegate DataAccessMappingFeatureIterator
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Feature next() {
        if (next == null) {
            throw new NoSuchElementException();
        }

        count++;
        Feature current = next;
        next = getFilteredNext();
        return current;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
