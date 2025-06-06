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
package org.geotools.xsd.impl;

import javax.xml.namespace.QName;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDSchemaContent;
import org.geotools.xsd.ElementInstance;
import org.geotools.xsd.InstanceComponent;
import org.geotools.xsd.Node;
import org.geotools.xsd.ParserDelegate;
import org.picocontainer.MutablePicoContainer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DelegatingHandler implements DocumentHandler, ElementHandler {

    ParserDelegate delegate;
    Handler parent;
    QName elementName;
    NodeImpl parseTree;

    DelegatingHandler(ParserDelegate delegate, QName elementName, Handler parent) {
        this.delegate = delegate;
        this.parent = parent;
        this.elementName = elementName;

        // create a parse tree
        XSDElementDeclaration e = XSDFactory.eINSTANCE.createXSDElementDeclaration();
        e.setTargetNamespace(elementName.getNamespaceURI());
        e.setName(elementName.getLocalPart());

        ElementImpl instance = new ElementImpl(e);
        instance.setName(elementName.getLocalPart());
        instance.setNamespace(elementName.getNamespaceURI());

        parseTree = new NodeImpl(instance);
    }

    @Override
    public void setContext(MutablePicoContainer context) {}

    @Override
    public MutablePicoContainer getContext() {
        return null;
    }

    @Override
    public XSDElementDeclaration getElementDeclaration() {
        return ((ElementInstance) parseTree.getComponent()).getElementDeclaration();
    }

    @Override
    public Handler getParentHandler() {
        return parent;
    }

    @Override
    public Handler createChildHandler(QName name) {
        return new DelegatingHandler(delegate, name, this);
    }

    @Override
    public void startChildHandler(Handler child) {}

    @Override
    public void endChildHandler(Handler child) {}

    @Override
    public InstanceComponent getComponent() {
        return null;
    }

    @Override
    public Node getParseNode() {
        return parseTree;
    }

    @Override
    public XSDSchemaContent getSchemaContent() {
        return null;
    }

    @Override
    public void startDocument() throws SAXException {
        delegate.startDocument();
    }

    @Override
    public void endDocument() throws SAXException {
        delegate.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        delegate.startPrefixMapping(prefix, uri);
    }

    @Override
    public void startElement(QName name, Attributes attributes) throws SAXException {

        if (!(parent instanceof DelegatingHandler)) {
            parent.startChildHandler(this);
        }

        delegate.startElement(name.getNamespaceURI(), name.getLocalPart(), qname(name), attributes);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        delegate.characters(ch, start, length);
    }

    @Override
    public void endElement(QName name) throws SAXException {
        delegate.endElement(name.getNamespaceURI(), name.getLocalPart(), qname(name));
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        delegate.endPrefixMapping(prefix);
    }

    String qname(QName name) {
        return name.getNamespaceURI() != null ? name.getPrefix() + ":" + name.getLocalPart() : name.getLocalPart();
    }
}
