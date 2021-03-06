/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2019, Open Source Geospatial Foundation (OSGeo)
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
 *
 */

package org.geotools.wcs.bindings;

import javax.xml.namespace.QName;
import org.geotools.wcs.WCS;
import org.geotools.xsd.AbstractComplexBinding;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.Node;

/**
 * Binding object for the type http://www.opengis.net/wcs:ServiceType.
 *
 * <p>
 *
 * <pre>
 *  <code>
 *  &lt;complexType name="ServiceType"&gt;
 *      &lt;annotation&gt;
 *          &lt;documentation&gt;A minimal, human readable rescription of the service. &lt;/documentation&gt;
 *      &lt;/annotation&gt;
 *      &lt;complexContent&gt;
 *          &lt;extension base="wcs:AbstractDescriptionType"&gt;
 *              &lt;sequence&gt;
 *                  &lt;element maxOccurs="unbounded" minOccurs="0" ref="wcs:keywords"/&gt;
 *                  &lt;element minOccurs="0" name="responsibleParty" type="wcs:ResponsiblePartyType"/&gt;
 *                  &lt;element name="fees" type="gml:CodeListType"&gt;
 *                      &lt;annotation&gt;
 *                          &lt;documentation&gt;A text string identifying any fees imposed by the service provider. The keyword NONE shall be used to mean no fees. &lt;/documentation&gt;
 *                      &lt;/annotation&gt;
 *                  &lt;/element&gt;
 *                  &lt;element maxOccurs="unbounded" name="accessConstraints" type="gml:CodeListType"&gt;
 *                      &lt;annotation&gt;
 *                          &lt;documentation&gt;A text string identifying any access constraints imposed by the service provider. The keyword NONE shall be used to mean no access constraints are imposed. &lt;/documentation&gt;
 *                      &lt;/annotation&gt;
 *                  &lt;/element&gt;
 *              &lt;/sequence&gt;
 *              &lt;attribute fixed="1.0.0" name="version" type="string" use="optional"/&gt;
 *              &lt;attribute name="updateSequence" type="string" use="optional"&gt;
 *                  &lt;annotation&gt;
 *                      &lt;documentation&gt;Service metadata (Capabilities) document version, having values that are "increased" whenever any change is made in service metadata document. Values are selected by each server, and are always opaque to clients. When supported by server, server shall return this attribute. &lt;/documentation&gt;
 *                  &lt;/annotation&gt;
 *              &lt;/attribute&gt;
 *          &lt;/extension&gt;
 *      &lt;/complexContent&gt;
 *  &lt;/complexType&gt;
 *
 *   </code>
 *  </pre>
 *
 * @generated
 */
public class ServiceTypeBinding extends AbstractComplexBinding {

    /** @generated */
    @Override
    public QName getTarget() {
        return WCS.ServiceType;
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
        return null;
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

        // TODO: implement and remove call to super
        return super.parse(instance, node, value);
    }
}
