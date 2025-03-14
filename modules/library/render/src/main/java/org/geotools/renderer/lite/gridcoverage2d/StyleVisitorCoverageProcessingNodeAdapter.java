/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.renderer.lite.gridcoverage2d;

import java.util.List;
import org.geotools.api.coverage.grid.GridCoverage;
import org.geotools.api.style.RasterSymbolizer;
import org.geotools.api.style.Style;
import org.geotools.api.style.StyleVisitor;
import org.geotools.api.util.InternationalString;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.util.factory.Hints;

/**
 * This class implements an adapter to allow a {@link CoverageProcessingNode} to feed itself by visiting an SLD
 * {@link Style} .
 *
 * <p>This class can be used to tie together {@link CoverageProcessingNode} s built from a chains as specified by the
 * {@link RasterSymbolizer} SLd element.
 *
 * @author Simone Giannecchini, GeoSolutions
 */
public abstract class StyleVisitorCoverageProcessingNodeAdapter extends StyleVisitorAdapter
        implements StyleVisitor, CoverageProcessingNode {

    /**
     * Allows subclasses to access the {@link CoverageProcessingNode} we are adapting.
     *
     * @return the adaptee
     * @uml.property name="adaptee"
     */
    protected CoverageProcessingNode getAdaptee() {
        return adaptee;
    }

    /** Instance of {@link CoverageProcessingNode} that we are adapting. */
    private final CoverageProcessingNode adaptee;

    /** @param adaptee */
    public StyleVisitorCoverageProcessingNodeAdapter(CoverageProcessingNode adaptee) {
        GridCoverageRendererUtilities.ensureNotNull(adaptee, "CoverageProcessingNode");
        this.adaptee = adaptee;
    }

    /** Default constructor for {@link StyleVisitorCoverageProcessingNodeAdapter} */
    public StyleVisitorCoverageProcessingNodeAdapter(InternationalString name, InternationalString description) {
        this(-1, name, description);
    }

    /**
     * Default constructor that gives users the possibility
     *
     * @param maxSources maximum number of sources allowed for this node.
     */
    public StyleVisitorCoverageProcessingNodeAdapter(
            int maxSources, InternationalString name, InternationalString description) {
        this(maxSources, null, name, description);
    }

    /**
     * Default constructor that gives users the possibility
     *
     * @param maxSources maximum number of sources allowed for this node.
     * @param hints instance of {@link Hints} class to control creation of internal factories. It can be <code>null
     *     </code>.
     */
    public StyleVisitorCoverageProcessingNodeAdapter(
            int maxSources, Hints hints, InternationalString name, InternationalString description) {
        adaptee = new BaseCoverageProcessingNode(maxSources, hints != null ? hints.clone() : null, name, description) {

            @Override
            protected GridCoverage execute() {
                synchronized (StyleVisitorCoverageProcessingNodeAdapter.this) {
                    return StyleVisitorCoverageProcessingNodeAdapter.this.execute();
                }
            }
        };
    }

    /** @see BaseCoverageProcessingNode#execute() */
    protected abstract GridCoverage execute();

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#addSink(org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode)
     */
    @Override
    public void addSink(CoverageProcessingNode sink) {
        adaptee.addSink(sink);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#addSource(org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode)
     */
    @Override
    public boolean addSource(CoverageProcessingNode source) {
        return adaptee.addSource(source);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getOutput()
     */
    @Override
    public GridCoverage getOutput() {
        return adaptee.getOutput();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getSink(int)
     */
    @Override
    public CoverageProcessingNode getSink(int index) {
        return adaptee.getSink(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getSinks()
     */
    @Override
    public List<CoverageProcessingNode> getSinks() {
        return adaptee.getSinks();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getSource(int)
     */
    @Override
    public CoverageProcessingNode getSource(int index) {
        return adaptee.getSource(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getSources()
     */
    @Override
    public List<CoverageProcessingNode> getSources() {
        return adaptee.getSources();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#removeSink(org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode)
     */
    @Override
    public boolean removeSink(CoverageProcessingNode sink) {
        return adaptee.removeSink(sink);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#removeSink(int)
     */
    @Override
    public CoverageProcessingNode removeSink(int index) {
        return adaptee.removeSink(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#removeSource(org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode)
     */
    @Override
    public boolean removeSource(CoverageProcessingNode source) {
        return adaptee.removeSource(source);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#removeSource(int)
     */
    @Override
    public CoverageProcessingNode removeSource(int index) {
        return adaptee.removeSource(index);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#dispose(boolean)
     */
    @Override
    public void dispose(boolean force) {
        adaptee.dispose(force);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getHints()
     */
    @Override
    public Hints getHints() {
        return adaptee.getHints();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getNumberOfSinks()
     */
    @Override
    public int getNumberOfSinks() {
        return adaptee.getNumberOfSinks();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getNumberOfSources()
     */
    @Override
    public int getNumberOfSources() {
        return adaptee.getNumberOfSources();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getDescription()
     */
    @Override
    public InternationalString getDescription() {
        return adaptee.getDescription();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getName()
     */
    @Override
    public InternationalString getName() {
        return adaptee.getName();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return adaptee.toString();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.geotools.renderer.lite.gridcoverage2d.CoverageProcessingNode#getCoverageFactory()
     */
    @Override
    public GridCoverageFactory getCoverageFactory() {
        return adaptee.getCoverageFactory();
    }
}
