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

import java.util.List;
import javax.xml.namespace.QName;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.Or;
import org.geotools.xsd.AbstractComplexBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;
import org.geotools.xsd.filter.FilterParsingUtils;

/**
 * Binding object for the element http://www.opengis.net/ogc:Or.
 *
 * <p>
 *
 * <pre>
 *         <code>
 *  &lt;xsd:element name="Or" substitutionGroup="ogc:logicOps" type="ogc:BinaryLogicOpType"/&gt;
 *
 *          </code>
 *         </pre>
 *
 * @generated
 */
public class OGCOrBinding extends AbstractComplexBinding {
    FilterFactory filterfactory;

    public OGCOrBinding(FilterFactory filterfactory) {
        this.filterfactory = filterfactory;
    }

    /** @generated */
    @Override
    public QName getTarget() {
        return OGC.Or;
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
        return Or.class;
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
        List<Filter> operands = FilterParsingUtils.BinaryLogicOperator_getChildFilters(node, filterfactory);
        return filterfactory.or(operands);
    }
}
