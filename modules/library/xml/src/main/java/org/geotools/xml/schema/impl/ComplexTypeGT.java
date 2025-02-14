/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2004-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.xml.schema.impl;

import java.net.URI;
import java.util.Map;
import javax.naming.OperationNotSupportedException;
import org.geotools.xml.PrintHandler;
import org.geotools.xml.schema.Attribute;
import org.geotools.xml.schema.ComplexType;
import org.geotools.xml.schema.Element;
import org.geotools.xml.schema.ElementGrouping;
import org.geotools.xml.schema.ElementValue;
import org.geotools.xml.schema.Schema;
import org.geotools.xml.schema.Type;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Provides ...TODO summary sentence
 *
 * <p>TODO Description
 *
 * <p>Responsibilities:
 *
 * <ul>
 *   <li>
 *   <li>
 * </ul>
 *
 * <p>Example Use:
 *
 * <pre><code>
 * ComplexTypeGT x = new ComplexTypeGT( ... );
 * TODO code example
 * </code></pre>
 *
 * @author dzwiers
 * @since 0.3
 */
public class ComplexTypeGT implements ComplexType {

    private String name = null, id = null, anyAtrNS = null;
    private URI ns;
    private Type parent = null;
    private Attribute[] attrs = null;
    private Element[] elems = null;
    private ElementGrouping child = null;
    private boolean _abstract, mixed, derived;

    /** Construct <code>ComplexTypeGT</code>. */
    public ComplexTypeGT(
            String id,
            String name,
            URI namespace,
            ElementGrouping child,
            Attribute[] attrs,
            Element[] elems,
            boolean mixed,
            Type parent,
            boolean _abstract,
            boolean derived,
            String anyAttributeNS) {
        this.name = name;
        this.id = id;
        this.anyAtrNS = anyAttributeNS;
        this.ns = namespace;
        this.parent = parent;
        this.attrs = attrs;
        this.elems = elems;
        this.child = child;
        this._abstract = _abstract;
        this.mixed = mixed;
        this.derived = derived;
    }

    /**
     * TODO summary sentence for getParent ...
     *
     * @see org.geotools.xml.schema.ComplexType#getParent()
     */
    @Override
    public Type getParent() {
        return parent;
    }

    /**
     * TODO summary sentence for isAbstract ...
     *
     * @see org.geotools.xml.schema.ComplexType#isAbstract()
     */
    @Override
    public boolean isAbstract() {
        return _abstract;
    }

    /**
     * TODO summary sentence for getAnyAttributeNameSpace ...
     *
     * @see org.geotools.xml.schema.ComplexType#getAnyAttributeNameSpace()
     */
    @Override
    public String getAnyAttributeNameSpace() {
        return anyAtrNS;
    }

    /**
     * TODO summary sentence for getAttributes ...
     *
     * @see org.geotools.xml.schema.ComplexType#getAttributes()
     */
    @Override
    public Attribute[] getAttributes() {
        return attrs;
    }

    /**
     * TODO summary sentence for getBlock ...
     *
     * @see org.geotools.xml.schema.ComplexType#getBlock()
     */
    @Override
    public int getBlock() {
        return Schema.NONE;
    }

    /**
     * TODO summary sentence for getChild ...
     *
     * @see org.geotools.xml.schema.ComplexType#getChild()
     */
    @Override
    public ElementGrouping getChild() {
        return child;
    }

    /**
     * TODO summary sentence for getChildElements ...
     *
     * @see org.geotools.xml.schema.ComplexType#getChildElements()
     */
    @Override
    public Element[] getChildElements() {
        return elems;
    }

    /**
     * TODO summary sentence for getFinal ...
     *
     * @see org.geotools.xml.schema.ComplexType#getFinal()
     */
    @Override
    public int getFinal() {
        return Schema.NONE;
    }

    /**
     * TODO summary sentence for getId ...
     *
     * @see org.geotools.xml.schema.ComplexType#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    /**
     * TODO summary sentence for isMixed ...
     *
     * @see org.geotools.xml.schema.ComplexType#isMixed()
     */
    @Override
    public boolean isMixed() {
        return mixed;
    }

    /**
     * TODO summary sentence for isDerived ...
     *
     * @see org.geotools.xml.schema.ComplexType#isDerived()
     */
    @Override
    public boolean isDerived() {
        return derived;
    }

    /**
     * TODO summary sentence for cache ...
     *
     * @see org.geotools.xml.schema.ComplexType#cache(org.geotools.xml.schema.Element, java.util.Map)
     */
    @Override
    public boolean cache(Element element, Map<String, Object> hints) {
        return true;
    }

    /**
     * TODO summary sentence for getValue ...
     *
     * @see org.geotools.xml.schema.Type#getValue(org.geotools.xml.schema.Element,
     *     org.geotools.xml.schema.ElementValue[], org.xml.sax.Attributes, java.util.Map)
     */
    @Override
    public Object getValue(Element element, ElementValue[] value, Attributes attrs1, Map<String, Object> hints)
            throws OperationNotSupportedException, SAXException {
        throw new OperationNotSupportedException();
    }

    /**
     * TODO summary sentence for getName ...
     *
     * @see org.geotools.xml.schema.Type#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * TODO summary sentence for getNamespace ...
     *
     * @see org.geotools.xml.schema.Type#getNamespace()
     */
    @Override
    public URI getNamespace() {
        return ns;
    }

    /**
     * TODO summary sentence for getInstanceType ...
     *
     * @see org.geotools.xml.schema.Type#getInstanceType()
     */
    @Override
    public Class getInstanceType() {
        return null;
    }

    /**
     * TODO summary sentence for canEncode ...
     *
     * @see org.geotools.xml.schema.Type#canEncode(org.geotools.xml.schema.Element, java.lang.Object, java.util.Map)
     */
    @Override
    public boolean canEncode(Element element, Object value, Map<String, Object> hints) {
        return false;
    }

    /**
     * TODO summary sentence for encode ...
     *
     * @see org.geotools.xml.schema.Type#encode(org.geotools.xml.schema.Element, java.lang.Object,
     *     org.geotools.xml.PrintHandler, java.util.Map)
     */
    @Override
    public void encode(Element element, Object value, PrintHandler output, Map<String, Object> hints)
            throws OperationNotSupportedException {
        throw new OperationNotSupportedException();
    }

    /**
     * TODO summary sentence for findChildElement ...
     *
     * @see org.geotools.xml.schema.Type#findChildElement(java.lang.String)
     */
    @Override
    public Element findChildElement(String name1) {
        return getChild() != null ? getChild().findChildElement(name1) : null;
    }
}
