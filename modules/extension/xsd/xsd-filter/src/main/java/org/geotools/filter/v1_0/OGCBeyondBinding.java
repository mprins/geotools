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
package org.geotools.filter.v1_0;

import javax.xml.namespace.QName;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.filter.spatial.Beyond;
import org.geotools.xsd.AbstractComplexBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;
import org.locationtech.jts.geom.GeometryFactory;

/**
 * Binding object for the element http://www.opengis.net/ogc:Beyond.
 *
 * <p>
 *
 * <pre>
 *         <code>
 *  &lt;xsd:element name="Beyond" substitutionGroup="ogc:spatialOps" type="ogc:DistanceBufferType"/&gt;
 *
 *          </code>
 *         </pre>
 *
 * @generated
 */
public class OGCBeyondBinding extends AbstractComplexBinding {
    FilterFactory filterFactory;
    GeometryFactory geometryFactory;

    public OGCBeyondBinding(FilterFactory filterFactory, GeometryFactory geometryFactory) {
        this.filterFactory = filterFactory;
        this.geometryFactory = geometryFactory;
    }

    /** @generated */
    @Override
    public QName getTarget() {
        return OGC.Beyond;
    }

    /**
     *
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    @Override
    public Class getType() {
        return Beyond.class;
    }

    @Override
    public int getExecutionMode() {
        return AFTER;
    }

    /**
     *
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     *
     * @generated modifiable
     */
    @Override
    public Object parse(ElementInstance instance, Node node, Object value) throws Exception {
        Expression[] operands = OGCUtils.spatial(node, filterFactory, geometryFactory);
        DistanceUnits distance = node.getChildValue(DistanceUnits.class);
        return filterFactory.beyond(operands[0], operands[1], distance.getDistance(), distance.getUnits());
    }
}
