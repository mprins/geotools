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

package org.geotools.data.gen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.api.feature.GeometryAttribute;
import org.geotools.api.feature.IllegalAttributeException;
import org.geotools.api.feature.Property;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.AttributeDescriptor;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.feature.type.Name;
import org.geotools.api.feature.type.PropertyDescriptor;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.api.geometry.BoundingBox;
import org.geotools.feature.AttributeImpl;
import org.geotools.feature.GeometryAttributeImpl;
import org.geotools.feature.NameImpl;

/**
 * @author Christian Mueller
 *     <p>Decorator Class for Simple Feature objects having pregeneralized geometries
 *     <p>This feature object is read only, modifying calls result in a {@link UnsupportedOperationException}
 *     <p>The special thing is that a generalized geometry is returned.
 */
public class PreGeneralizedSimpleFeature implements SimpleFeature {

    SimpleFeature feature;

    SimpleFeatureType featureTyp;

    SimpleFeatureType returnedFeatureType;

    String geomPropertyName, backendGeomPropertyName;

    Name nameBackendGeomProperty;

    Map<Object, Object> userData;

    int[] indexMapping;

    public PreGeneralizedSimpleFeature(
            SimpleFeatureType featureTyp,
            SimpleFeatureType returnedFeatureType,
            int[] indexMapping,
            SimpleFeature feature,
            String geomPropertyName,
            String backendGeomPropertyName) {

        this.feature = feature;
        this.geomPropertyName = geomPropertyName;
        this.backendGeomPropertyName = backendGeomPropertyName;
        this.featureTyp = featureTyp;
        this.returnedFeatureType = returnedFeatureType;
        this.indexMapping = indexMapping;
        this.nameBackendGeomProperty = new NameImpl(backendGeomPropertyName);
    }

    private String getBackendAttributeName(String attrName) {
        if (geomPropertyName.equals(attrName)) return backendGeomPropertyName;
        else return attrName;
    }

    private Name getNameBackendAttribute(Name name) {
        if (geomPropertyName.equals(name.getLocalPart())) return nameBackendGeomProperty;
        else return name;
    }

    private Property createProperty(String name) {
        Object value = name.equals(geomPropertyName)
                ? feature.getAttribute(backendGeomPropertyName)
                : feature.getAttribute(name);
        AttributeDescriptor attrDescr = featureTyp.getDescriptor(name);
        if (attrDescr == null) return null;
        if (attrDescr instanceof GeometryDescriptor)
            return new GeometryAttributeImpl(value, (GeometryDescriptor) attrDescr, null);
        else return new AttributeImpl(value, attrDescr, null);
    }

    private UnsupportedOperationException unsupported() {
        return new UnsupportedOperationException("Cannot modify a pregeneralized feature");
    }

    @Override
    public Object getAttribute(String attrName) {
        return feature.getAttribute(getBackendAttributeName(attrName));
    }

    @Override
    public Object getAttribute(Name name) {
        return feature.getAttribute(getNameBackendAttribute(name));
    }

    @Override
    public Object getAttribute(int index) throws IndexOutOfBoundsException {
        return feature.getAttribute(indexMapping == null ? index : indexMapping[index]);
    }

    @Override
    public int getAttributeCount() {
        return feature.getAttributeCount();
    }

    @Override
    public List<Object> getAttributes() {
        return feature.getAttributes();
    }

    @Override
    public Object getDefaultGeometry() {
        return feature.getAttribute(backendGeomPropertyName);
    }

    @Override
    public SimpleFeatureType getFeatureType() {
        return returnedFeatureType;
    }

    @Override
    public String getID() {
        return feature.getID();
    }

    @Override
    public SimpleFeatureType getType() {
        return returnedFeatureType;
    }

    @Override
    public void setAttribute(String arg0, Object arg1) {
        throw unsupported();
    }

    @Override
    public void setAttribute(Name arg0, Object arg1) {
        throw unsupported();
    }

    @Override
    public void setAttribute(int arg0, Object arg1) throws IndexOutOfBoundsException {
        throw unsupported();
    }

    @Override
    public void setAttributes(List<Object> arg0) {
        throw unsupported();
    }

    @Override
    public void setAttributes(Object[] arg0) {
        throw unsupported();
    }

    @Override
    public void setDefaultGeometry(Object arg0) {
        throw unsupported();
    }

    @Override
    public BoundingBox getBounds() {
        return feature.getBounds();
    }

    @Override
    public GeometryAttribute getDefaultGeometryProperty() {
        Object value = feature.getAttribute(backendGeomPropertyName);
        GeometryAttribute attr = new GeometryAttributeImpl(value, featureTyp.getGeometryDescriptor(), null);
        return attr;
    }

    @Override
    public FeatureId getIdentifier() {
        return feature.getIdentifier();
    }

    @Override
    public void setDefaultGeometryProperty(GeometryAttribute arg0) {
        throw unsupported();
    }

    @Override
    public Collection<Property> getProperties() {
        List<Property> result = new ArrayList<>();
        for (PropertyDescriptor descr : featureTyp.getDescriptors()) {
            result.add(createProperty(descr.getName().getLocalPart()));
        }
        return result;
    }

    @Override
    public Collection<Property> getProperties(Name name) {
        return getProperties(name.getLocalPart());
    }

    @Override
    public Collection<Property> getProperties(String name) {
        Property p = createProperty(name);
        if (p == null) return Collections.emptyList();
        else return Collections.singletonList(p);
    }

    @Override
    public Property getProperty(Name name) {
        return createProperty(name.getLocalPart());
    }

    @Override
    public Property getProperty(String name) {
        return createProperty(name);
    }

    @Override
    public Collection<? extends Property> getValue() {
        return getProperties();
    }

    @Override
    public void setValue(Collection<Property> arg0) {
        throw unsupported();
    }

    @Override
    public void validate() throws IllegalAttributeException {
        feature.validate();
    }

    @Override
    public AttributeDescriptor getDescriptor() {
        return null;
    }

    @Override
    public Name getName() {
        return null;
    }

    @Override
    public Map<Object, Object> getUserData() {
        if (userData == null) userData = new HashMap<>();
        return userData;
    }

    @Override
    public boolean isNillable() {
        return false;
    }

    @Override
    public void setValue(Object arg0) {
        throw unsupported();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (!(obj instanceof PreGeneralizedSimpleFeature)) {
            return false;
        }

        PreGeneralizedSimpleFeature feat = (PreGeneralizedSimpleFeature) obj;
        if (feat.geomPropertyName.equals(this.geomPropertyName) == false) return false;
        if (feat.backendGeomPropertyName.equals(this.backendGeomPropertyName) == false) return false;
        if (feat.featureTyp.equals(this.featureTyp) == false) return false;
        if (feat.feature.equals(this.feature) == false) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return featureTyp.hashCode()
                * geomPropertyName.hashCode()
                * backendGeomPropertyName.hashCode()
                * feature.hashCode();
    }

    @Override
    public String toString() {
        return "PregeneralizedFeature of " + feature.toString();
    }
}
