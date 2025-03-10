/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.temporal.reference;

import java.util.Collection;
import java.util.Set;
import org.geotools.api.metadata.extent.Extent;
import org.geotools.api.referencing.ReferenceIdentifier;
import org.geotools.api.temporal.TemporalReferenceSystem;
import org.geotools.api.util.GenericName;
import org.geotools.api.util.InternationalString;
import org.geotools.util.Utilities;

/** @author Mehdi Sidhoum (Geomatys) */
public class DefaultTemporalReferenceSystem implements TemporalReferenceSystem {

    /** This is a name that uniquely identifies the temporal reference system. */
    private ReferenceIdentifier name;

    private Extent domainOfValidity;
    private Extent validArea;
    private InternationalString scope;

    /**
     * Creates a new instance of TemporalReferenceSystem by passing a ReferenceIdentifier name and a domain of validity.
     */
    public DefaultTemporalReferenceSystem(ReferenceIdentifier name, Extent domainOfValidity) {
        this.name = name;
        this.domainOfValidity = domainOfValidity;
    }

    @Override
    public ReferenceIdentifier getName() {
        return name;
    }

    @Override
    public Extent getDomainOfValidity() {
        return domainOfValidity;
    }

    @Override
    public InternationalString getScope() {
        return scope;
    }

    @Override
    public Collection<GenericName> getAlias() {
        return null;
    }

    @Override
    public Set<ReferenceIdentifier> getIdentifiers() {
        return null;
    }

    @Override
    public InternationalString getRemarks() {
        return null;
    }

    @Override
    public String toWKT() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /** This is a name that uniquely identifies the temporal reference system. */
    public void setName(ReferenceIdentifier name) {
        this.name = name;
    }

    public void setDomainOfValidity(Extent domainOfValidity) {
        this.domainOfValidity = domainOfValidity;
    }

    public void setValidArea(Extent validArea) {
        this.validArea = validArea;
    }

    public void setScope(InternationalString scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof DefaultTemporalReferenceSystem) {
            final DefaultTemporalReferenceSystem that = (DefaultTemporalReferenceSystem) object;

            return Utilities.equals(this.domainOfValidity, that.domainOfValidity)
                    && Utilities.equals(this.name, that.name)
                    && Utilities.equals(this.scope, that.scope)
                    && Utilities.equals(this.validArea, that.validArea);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.domainOfValidity != null ? this.domainOfValidity.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + (this.scope != null ? this.scope.hashCode() : 0);
        hash = 37 * hash + (this.validArea != null ? this.validArea.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("TemporalReferenceSystem:").append('\n');
        if (name != null) {
            s.append("name:").append(name).append('\n');
        }
        if (domainOfValidity != null) {
            s.append("domainOfValidity:").append(domainOfValidity).append('\n');
        }
        return s.toString();
    }
}
