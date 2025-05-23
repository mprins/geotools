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
package org.geotools.feature;

import java.util.Collection;
import org.geotools.api.feature.Feature;
import org.geotools.api.feature.GeometryAttribute;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.FeatureType;
import org.geotools.api.feature.type.GeometryType;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.api.geometry.BoundingBox;
import org.geotools.geometry.jts.ReferencedEnvelope;

/**
 * Temptative implementation of Feature.
 *
 * <p>NOTE this is work in progress and at this time not really being used throughout the library.
 *
 * @author jdeolive
 * @author jgarnett
 */
public class FeatureImpl extends ComplexAttributeImpl implements Feature {

    /** Default geometry attribute */
    volatile GeometryAttribute defaultGeometry;

    /**
     * Create a Feature with the following content.
     *
     * @param properties Collectio of Properties (aka Attributes and/or Associations)
     * @param desc Nested descriptor
     * @param id Feature ID
     */
    public FeatureImpl(Collection<Property> properties, AttributeDescriptor desc, FeatureId id) {
        super(properties, desc, id);
    }

    /**
     * Create a Feature with the following content.
     *
     * @param properties Collectio of Properties (aka Attributes and/or Associations)
     * @param type Type of feature to be created
     * @param id Feature ID
     */
    public FeatureImpl(Collection<Property> properties, FeatureType type, FeatureId id) {
        super(properties, type, id);
    }

    @Override
    public FeatureType getType() {
        return (FeatureType) super.getType();
    }

    @Override
    public FeatureId getIdentifier() {
        return (FeatureId) this.id;
    }
    /**
     * Get the total bounds of this feature which is calculated by doing a union of the bounds of each geometry this
     * feature is associated with.
     *
     * @return An Envelope containing the total bounds of this Feature.
     * @task REVISIT: what to return if there are no geometries in the feature? For now we'll return a null envelope,
     *     make this part of interface? (IanS - by OGC standards, all Feature must have geom)
     */
    @Override
    public BoundingBox getBounds() {

        ReferencedEnvelope bounds = new ReferencedEnvelope(getType().getCoordinateReferenceSystem());
        for (Property property : getValue()) {
            if (property instanceof GeometryAttribute) {
                bounds.include(((GeometryAttribute) property).getBounds());
            }
        }

        return bounds;
    }

    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        if (defaultGeometry != null) {
            return defaultGeometry;
        }

        synchronized (this) {
            if (defaultGeometry == null) {
                // look it up from the type
                if (getType().getGeometryDescriptor() == null) {
                    return null;
                }

                GeometryType geometryType = getType().getGeometryDescriptor().getType();

                if (geometryType != null) {
                    for (Property property : getValue()) {
                        if (property instanceof GeometryAttribute) {
                            if (property.getType().equals(geometryType)) {
                                defaultGeometry = (GeometryAttribute) property;
                                break;
                            }
                        }
                    }
                }
            }
        }

        return defaultGeometry;
    }

    // TODO: REVISIT
    // this implementation seems really bad to me or I am missing something:
    // 1- getValue() shouldn't contain the passed in attribute, but the schema should contain its
    // descriptor
    // 2- this.defaultGeometry = defaultGeometry means getValue() will  not contain the argument
    @Override
    public void setDefaultGeometryProperty(GeometryAttribute defaultGeometry) {
        if (!getValue().contains(defaultGeometry)) {
            throw new IllegalArgumentException("specified attribute is not one of: " + getValue());
        }

        synchronized (this) {
            this.defaultGeometry = defaultGeometry;
        }
    }
}
