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
package org.geotools.filter.v2_0.bindings;

import javax.xml.namespace.QName;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.filter.expression.Literal;
import org.geotools.api.filter.spatial.BinarySpatialOperator;
import org.geotools.filter.v2_0.FES;
import org.geotools.gml3.v3_2.GML;
import org.geotools.gml3.v3_2.GMLConfiguration;
import org.geotools.xsd.AbstractComplexBinding;
import org.geotools.xsd.Encoder;
import org.geotools.xsd.EncoderDelegate;
import org.locationtech.jts.geom.Geometry;

/**
 * <pre>
 *  &lt;xsd:complexType name="BinarySpatialOpType">
 *     &lt;xsd:complexContent>
 *        &lt;xsd:extension base="fes:SpatialOpsType">
 *           &lt;xsd:sequence>
 *              &lt;xsd:element ref="fes:ValueReference"/>
 *            &lt;xsd:choice>
 *                 &lt;xsd:element ref="fes:expression"/>
 *                 &lt;xsd:any namespace="##other"/>
 *              &lt;/xsd:choice>
 *           &lt;/xsd:sequence>
 *        &lt;/xsd:extension>
 *     &lt;/xsd:complexContent>
 *  &lt;/xsd:complexType>
 *  <pre>
 * @author Justin Deoliveira, OpenGeo
 *
 */
public class BinarySpatialOpTypeBinding extends AbstractComplexBinding {

    @Override
    public QName getTarget() {
        return FES.BinarySpatialOpType;
    }

    @Override
    public Class getType() {
        return BinarySpatialOperator.class;
    }

    @Override
    public Object getProperty(Object object, QName name) throws Exception {
        Expression e = FESParseEncodeUtil.getProperty((BinarySpatialOperator) object, name);
        if (e instanceof Literal && ((Literal) e).getValue() instanceof Geometry) {
            return (EncoderDelegate) output -> {
                Encoder encoder = new Encoder(new GMLConfiguration());
                encoder.setInline(true);
                encoder.encode(((Literal) e).getValue(), GML.AbstractGeometry, output);
            };
        } else {
            return e;
        }
    }
}
