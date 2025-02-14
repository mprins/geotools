/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.feature.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.AttributeType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.Filter;
import org.geotools.api.util.InternationalString;
import org.geotools.feature.type.FeatureTypeImpl;

/**
 * Implementation fo SimpleFeatureType, subtypes must be atomic and are stored in a list.
 *
 * @author Justin
 * @author Ben Caradoc-Davies, CSIRO Exploration and Mining
 */
public class SimpleFeatureTypeImpl extends FeatureTypeImpl implements SimpleFeatureType {

    // list of types
    volatile List<AttributeType> types = null;

    Map<String, Integer> index;

    Map<String, AttributeDescriptor> descriptors;

    @SuppressWarnings("unchecked")
    public SimpleFeatureTypeImpl(
            Name name,
            List<AttributeDescriptor> schema,
            GeometryDescriptor defaultGeometry,
            boolean isAbstract,
            List<Filter> restrictions,
            AttributeType superType,
            InternationalString description) {
        // Note intentional circumvention of generics type checking;
        // this is only valid if schema is not modified.
        super(name, (List) schema, defaultGeometry, isAbstract, restrictions, superType, description);
        index = buildIndex(this);
        descriptors = buildDescriptorIndex(this);
    }

    /** @see org.geotools.api.feature.simple.SimpleFeatureType#getAttributeDescriptors() */
    @Override
    @SuppressWarnings("unchecked")
    public final List<AttributeDescriptor> getAttributeDescriptors() {
        // Here we circumvent the generics type system. Because we provide the schema and know it is
        // copied into an ArrayList in ComplexTypeImpl, this must work. Ugly, but then so are simple
        // features.
        return (List) getDescriptors();
    }

    @Override
    public List<AttributeType> getTypes() {
        if (types == null) {
            synchronized (this) {
                if (types == null) {
                    ArrayList<AttributeType> temp = new ArrayList<>();
                    for (AttributeDescriptor ad : getAttributeDescriptors()) {
                        temp.add(ad.getType());
                    }
                    types = temp;
                }
            }
        }
        return types;
    }

    @Override
    public AttributeType getType(Name name) {
        AttributeDescriptor attribute = getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    @Override
    public AttributeType getType(String name) {
        AttributeDescriptor attribute = getDescriptor(name);
        if (attribute != null) {
            return attribute.getType();
        }
        return null;
    }

    @Override
    public AttributeType getType(int index) {
        return getTypes().get(index);
    }

    @Override
    public AttributeDescriptor getDescriptor(Name name) {
        return (AttributeDescriptor) super.getDescriptor(name);
    }

    @Override
    public AttributeDescriptor getDescriptor(String name) {
        return descriptors.get(name);
    }

    @Override
    public AttributeDescriptor getDescriptor(int index) {
        return getAttributeDescriptors().get(index);
    }

    @Override
    public int indexOf(Name name) {
        if (name.getNamespaceURI() == null) {
            return indexOf(name.getLocalPart());
        }
        // otherwise do a full scan
        int index = 0;
        for (AttributeDescriptor descriptor : getAttributeDescriptors()) {
            if (descriptor.getName().equals(name)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    @Override
    public int indexOf(String name) {
        Integer idx = index.get(name);
        if (idx != null) {
            return idx.intValue();
        } else {
            return -1;
        }
    }

    @Override
    public int getAttributeCount() {
        return getAttributeDescriptors().size();
    }

    @Override
    public String getTypeName() {
        return getName().getLocalPart();
    }

    /** Builds the name -> position index used by simple features for fast attribute lookup */
    static Map<String, Integer> buildIndex(SimpleFeatureType featureType) {
        // build an index of attribute name to index
        Map<String, Integer> index = new HashMap<>();
        int i = 0;
        for (AttributeDescriptor ad : featureType.getAttributeDescriptors()) {
            index.put(ad.getLocalName(), i++);
        }
        if (featureType.getGeometryDescriptor() != null) {
            index.put(null, index.get(featureType.getGeometryDescriptor().getLocalName()));
        }
        return index;
    }

    /** Builds the name -> descriptor index used by simple features for fast attribute lookup */
    static Map<String, AttributeDescriptor> buildDescriptorIndex(SimpleFeatureType featureType) {
        // build an index of attribute name to index
        Map<String, AttributeDescriptor> index = new HashMap<>();
        for (AttributeDescriptor ad : featureType.getAttributeDescriptors()) {
            index.put(ad.getLocalName(), ad);
        }
        if (featureType.getGeometryDescriptor() != null) {
            index.put(null, featureType.getGeometryDescriptor());
        }
        return index;
    }
}
