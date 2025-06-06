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

package org.geotools.feature.type;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.geotools.api.feature.type.AttributeType;
import org.geotools.api.feature.type.Name;
import org.geotools.api.filter.Filter;
import org.geotools.api.util.InternationalString;

/**
 * A replacement for {@link AttributeTypeImpl} with lazy evaluation of super type, so types can be defined in any order.
 * Note that type equality is defined by name, so do not allow different types with the same name to be put in any
 * Collection.
 *
 * <p>Inspired by {@link AttributeTypeImpl} and {@link PropertyTypeImpl}.
 *
 * @author Ben Caradoc-Davies (CSIRO Earth Science and Resource Engineering)
 * @see AttributeTypeImpl
 * @see PropertyTypeImpl
 */
public abstract class AbstractLazyAttributeTypeImpl implements AttributeType {

    private final Name name;

    private final Class<?> binding;

    private final boolean identified;

    private final boolean isAbstract;

    private final List<Filter> restrictions;

    private final InternationalString description;

    private final Map<Object, Object> userData;

    private AttributeType superType;

    /** Constructor arguments have the same meaning as in {@link AttributeTypeImpl}. */
    public AbstractLazyAttributeTypeImpl(
            Name name,
            Class<?> binding,
            boolean identified,
            boolean isAbstract,
            List<Filter> restrictions,
            InternationalString description) {
        if (name == null) {
            throw new NullPointerException("Type has no name");
        }
        if (binding == null) {
            throw new NullPointerException("Type has no binding");
        }
        this.name = name;
        this.binding = binding;
        this.identified = identified;
        this.isAbstract = isAbstract;
        if (restrictions == null) {
            this.restrictions = Collections.emptyList();
        } else {
            this.restrictions = Collections.unmodifiableList(new ArrayList<>(restrictions));
        }
        this.description = description;
        this.userData = new HashMap<>();
    }

    /**
     * Subclasses must override this method to return the super type of this type or null if none. This method will only
     * be called once at most.
     *
     * @return super type or null
     */
    public abstract AttributeType buildSuper();

    /** @see org.geotools.api.feature.type.AttributeType#isIdentified() */
    @Override
    public boolean isIdentified() {
        return identified;
    }

    /** @see org.geotools.api.feature.type.AttributeType#getSuper() */
    @Override
    public AttributeType getSuper() {
        if (superType == null) {
            superType = buildSuper();
        }
        return superType;
    }

    /** @see org.geotools.api.feature.type.PropertyType#getName() */
    @Override
    public Name getName() {
        return name;
    }

    /** @see org.geotools.api.feature.type.PropertyType#getBinding() */
    @Override
    public Class<?> getBinding() {
        return binding;
    }

    /** @see org.geotools.api.feature.type.PropertyType#isAbstract() */
    @Override
    public boolean isAbstract() {
        return isAbstract;
    }

    /** @see org.geotools.api.feature.type.PropertyType#getRestrictions() */
    @Override
    public List<Filter> getRestrictions() {
        return restrictions;
    }

    /** @see org.geotools.api.feature.type.PropertyType#getDescription() */
    @Override
    public InternationalString getDescription() {
        return description;
    }

    /** @see org.geotools.api.feature.type.PropertyType#getUserData() */
    @Override
    public Map<Object, Object> getUserData() {
        return userData;
    }

    /**
     * Equality by name. Yes, this may be a surprise to some client code, but how else do you define equality in the
     * face of cyclic type definitions, without breaking encapsulation to analyse the full graph of types?
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AttributeType)) {
            return false;
        } else {
            return name.equals(((AttributeType) other).getName());
        }
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "LazyAttributeType: " + getName();
    }
}
