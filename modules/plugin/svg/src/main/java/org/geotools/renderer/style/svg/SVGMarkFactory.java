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
 */
package org.geotools.renderer.style.svg;

import java.awt.Graphics2D;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import org.apache.batik.gvt.CompositeGraphicsNode;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ShapeNode;
import org.geotools.api.feature.Feature;
import org.geotools.api.filter.expression.Expression;
import org.geotools.geometry.jts.TransformedShape;
import org.geotools.renderer.style.MarkFactory;

public class SVGMarkFactory implements MarkFactory {

    private RenderableSVGCache cache;

    public SVGMarkFactory() {
        this.cache = new RenderableSVGCache();
    }

    public SVGMarkFactory(Map<Key, Object> hints) {
        this.cache = new RenderableSVGCache(hints);
    }

    @Override
    public Shape getShape(Graphics2D graphics, Expression symbolUrl, Feature feature) throws Exception {
        RenderableSVG svg = cache.getRenderableSVG(feature, symbolUrl, "image/svg");
        if (svg == null) {
            return null;
        }

        Shape shape = getShape(svg.node);
        final Rectangle2D bounds = shape.getBounds2D();
        double maxSize = Math.max(bounds.getWidth(), bounds.getHeight());
        // flip and center the shape
        double scaleToOne = 1 / maxSize;
        final AffineTransform at = new AffineTransform(scaleToOne, 0, 0, -scaleToOne, -0.5, 0.5);
        TransformedShape ts = new TransformedShape(shape, at);
        return ts;
    }

    private Shape getShape(GraphicsNode node) {
        if (!node.isVisible()) {
            return null;
        }
        Shape result = null;
        if (node instanceof ShapeNode) {
            result = ((ShapeNode) node).getShape();
        } else if (node instanceof CompositeGraphicsNode) {
            CompositeGraphicsNode composite = (CompositeGraphicsNode) node;
            for (Object object : composite.getChildren()) {
                Shape subShape = getShape((GraphicsNode) object);
                if (subShape != null) {
                    if (result == null) {
                        result = subShape;
                    } else {
                        // accumulate in new GeneralPath, to avoid contaminating the original shape,
                        // in case it was a GeneralPath to start with
                        GeneralPath gp = new GeneralPath();
                        gp.append(result.getPathIterator(new AffineTransform()), false);
                        gp.append(subShape.getPathIterator(new AffineTransform()), false);
                        result = gp;
                    }
                }
            }
        }
        AffineTransform transform = node.getTransform();
        if (result != null && transform != null && !transform.isIdentity()) {
            result = new TransformedShape(result, transform);
        }

        return result;
    }
}
