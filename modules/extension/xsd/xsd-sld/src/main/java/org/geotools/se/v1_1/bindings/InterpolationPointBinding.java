/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2010, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.se.v1_1.bindings;

import javax.xml.namespace.QName;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.style.ColorMapEntry;
import org.geotools.api.style.StyleFactory;
import org.geotools.se.v1_1.SE;
import org.geotools.xsd.AbstractComplexBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

/**
 * Binding object for the element http://www.opengis.net/se:InterpolationPoint.
 *
 * <p>
 *
 * <pre>
 *  <code>
 *  &lt;xsd:element name="InterpolationPoint"
 *      substitutionGroup="ogc:expression" type="se:InterpolationPointType"/&gt;
 *
 *   </code>
 * </pre>
 *
 * <pre>
 *       <code>
 *  &lt;xsd:complexType name="InterpolationPointType"&gt;
 *      &lt;xsd:complexContent&gt;
 *          &lt;xsd:extension base="ogc:ExpressionType"&gt;
 *              &lt;xsd:sequence&gt;
 *                  &lt;xsd:element ref="se:Data"/&gt;
 *                  &lt;xsd:element ref="se:Value"/&gt;
 *              &lt;/xsd:sequence&gt;
 *          &lt;/xsd:extension&gt;
 *      &lt;/xsd:complexContent&gt;
 *  &lt;/xsd:complexType&gt;
 *
 *        </code>
 * </pre>
 *
 * @generated
 */
public class InterpolationPointBinding extends AbstractComplexBinding {

    StyleFactory styleFactory;
    FilterFactory filterFactory;

    public InterpolationPointBinding(StyleFactory styleFactory, FilterFactory filterFactory) {
        this.styleFactory = styleFactory;
        this.filterFactory = filterFactory;
    }

    /** @generated */
    @Override
    public QName getTarget() {
        return SE.InterpolationPoint;
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
        return ColorMapEntry.class;
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

        ColorMapEntry entry = styleFactory.createColorMapEntry();

        // &lt;xsd:element ref="se:Data"/&gt;
        entry.setQuantity(filterFactory.literal(node.getChildValue("Data")));

        // &lt;xsd:element ref="se:Value"/&gt;
        entry.setColor((Expression) node.getChildValue("Value"));

        return entry;
    }
}
