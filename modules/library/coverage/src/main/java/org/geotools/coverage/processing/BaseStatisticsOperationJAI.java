/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2005-2016, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.coverage.processing;

import it.geosolutions.jaiext.JAIExt;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import javax.media.jai.JAI;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.ParameterBlockJAI;
import javax.media.jai.ROIShape;
import javax.media.jai.StatisticsOpImage;
import javax.media.jai.registry.RenderedRegistryMode;
import org.geotools.api.metadata.spatial.PixelOrientation;
import org.geotools.api.parameter.ParameterDescriptor;
import org.geotools.api.parameter.ParameterValueGroup;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.metadata.iso.citation.Citations;
import org.geotools.parameter.DefaultParameterDescriptor;
import org.geotools.parameter.ImagingParameterDescriptors;
import org.geotools.parameter.ImagingParameters;
import org.geotools.referencing.CRS;
import org.geotools.referencing.operation.transform.ProjectiveTransform;
import org.geotools.util.logging.Logging;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

/**
 * This class is the root class for the Statistics operations based on {@link JAI}'s {@link StatisticsOpImage} like
 * Extrema and Histogram. It provides basic capabilities for management of geospatial parameters like
 * {@link javax.media.jai.ROI}s and subsampling factors.
 *
 * @author Simone Giannecchini
 * @since 2.4.x
 */
public abstract class BaseStatisticsOperationJAI extends OperationJAI {

    /** */
    private static final long serialVersionUID = 6830028735162290160L;

    /** {@link Logger} for this class. */
    public static final Logger LOGGER = Logging.getLogger(BaseStatisticsOperationJAI.class);

    /** The parameter descriptor for the SPATIAL_SUBSAMPLING_X */
    public static final ParameterDescriptor<Double> SPATIAL_SUBSAMPLING_X = new DefaultParameterDescriptor<>(
            Citations.JAI,
            "xPeriod",
            Double.class, // Value class (mandatory)
            null, // Array of valid values
            null, // Default value
            null, // Minimal value
            null, // Maximal value
            null, // Unit of measure
            true);

    /** The parameter descriptor for the SPATIAL_SUBSAMPLING_Y */
    public static final ParameterDescriptor<Double> SPATIAL_SUBSAMPLING_Y = new DefaultParameterDescriptor<>(
            Citations.JAI,
            "yPeriod",
            Double.class, // Value class (mandatory)
            null, // Array of valid values
            null, // Default value
            null, // Minimal value
            null, // Maximal value
            null, // Unit of measure
            true);

    /** The parameter descriptor for the Region Of Interest. */
    public static final ParameterDescriptor<Polygon> ROI = new DefaultParameterDescriptor<>(
            Citations.JAI,
            "roi",
            Polygon.class, // Value class (mandatory)
            null, // Array of valid values
            null, // Default value
            null, // Minimal value
            null, // Maximal value
            null, // Unit of measure
            true);

    private static Set<ParameterDescriptor> REPLACED_DESCRIPTORS;

    static {
        final Set<ParameterDescriptor> replacedDescriptors = new HashSet<>();
        replacedDescriptors.add(SPATIAL_SUBSAMPLING_X);
        replacedDescriptors.add(SPATIAL_SUBSAMPLING_Y);
        replacedDescriptors.add(ROI);
        REPLACED_DESCRIPTORS = Collections.unmodifiableSet(replacedDescriptors);
    }

    /**
     * Constructor for {@link BaseStatisticsOperationJAI}.
     *
     * @param operationDescriptor {@link OperationDescriptor} for the underlying JAI operation.
     */
    public BaseStatisticsOperationJAI(OperationDescriptor operationDescriptor) {
        super(
                operationDescriptor,
                new ImagingParameterDescriptors(
                        getOperationDescriptor(operationDescriptor.getName()), REPLACED_DESCRIPTORS));
    }

    /**
     * Constructor for {@link BaseStatisticsOperationJAI}.
     *
     * @param operationDescriptor {@link OperationDescriptor} for the underlying JAI operation.
     * @param replacements {@link ImagingParameterDescriptors} that should replace the correspondent
     *     {@link ImagingParameters} in order to change the default behavior they have inside JAI.
     */
    public BaseStatisticsOperationJAI(
            OperationDescriptor operationDescriptor, ImagingParameterDescriptors replacements) {
        super(
                operationDescriptor,
                new ImagingParameterDescriptors(
                        ImagingParameterDescriptors.properties(operationDescriptor),
                        operationDescriptor,
                        RenderedRegistryMode.MODE_NAME,
                        ImagingParameterDescriptors.DEFAULT_SOURCE_TYPE_MAP,
                        REPLACED_DESCRIPTORS));
    }
    /**
     * Constructor for {@link BaseStatisticsOperationJAI}.
     *
     * @param name of the underlying JAI operation.
     */
    public BaseStatisticsOperationJAI(String name, OperationDescriptor operationDescriptor) {
        super(
                getOperationDescriptor(JAIExt.getOperationName(name)),
                new ExtendedImagingParameterDescriptors(
                        name, operationDescriptor, new HashSet<>(REPLACED_DESCRIPTORS)));
    }

    /**
     * Constructor for {@link BaseStatisticsOperationJAI}.
     *
     * @param name of the underlying JAI operation.
     */
    public BaseStatisticsOperationJAI(String name) {
        super(
                getOperationDescriptor(name),
                new ImagingParameterDescriptors(getOperationDescriptor(name), new HashSet<>(REPLACED_DESCRIPTORS)));
    }

    /**
     * Copies parameter values from the specified {@link ParameterValueGroup} to the {@link ParameterBlockJAI}
     *
     * @param parameters The {@link ParameterValueGroup} to be copied.
     * @return A copy of the provided {@link ParameterValueGroup} as a JAI block.
     * @see
     *     org.geotools.coverage.processing.OperationJAI#prepareParameters(org.geotools.api.parameter.ParameterValueGroup)
     */
    @Override
    protected ParameterBlockJAI prepareParameters(ParameterValueGroup parameters) {
        // /////////////////////////////////////////////////////////////////////
        //
        // Make a copy of the input parameters.
        //
        // ///////////////////////////////////////////////////////////////////
        final ImagingParameters copy = (ImagingParameters) descriptor.createValue();
        final ParameterBlockJAI block = (ParameterBlockJAI) copy.parameters;
        try {

            // /////////////////////////////////////////////////////////////////////
            //
            //
            // Now transcode the parameters as needed by this operation.
            //
            //
            // ///////////////////////////////////////////////////////////////////
            // XXX make it robust
            final GridCoverage2D source = (GridCoverage2D) parameters
                    .parameter(operation.getSourceNames()[PRIMARY_SOURCE_INDEX])
                    .getValue();
            final AffineTransform gridToWorldTransformCorrected = new AffineTransform(
                    (AffineTransform) source.getGridGeometry().getGridToCRS2D(PixelOrientation.UPPER_LEFT));
            final MathTransform worldToGridTransform;
            try {
                worldToGridTransform = ProjectiveTransform.create(gridToWorldTransformCorrected.createInverse());
            } catch (NoninvertibleTransformException e) {
                // //
                //
                // Something bad happened here, namely the transformation to go
                // from grid to world was not invertible. Let's wrap and
                // propagate the error.
                //
                // //
                final CoverageProcessingException ce = new CoverageProcessingException(e);
                throw ce;
            }

            // //
            //
            // get the original envelope and the crs
            //
            // //
            final CoordinateReferenceSystem crs = source.getCoordinateReferenceSystem2D();
            final ReferencedEnvelope envelope = source.getEnvelope2D();

            // /////////////////////////////////////////////////////////////////////
            //
            // Transcode the xPeriod and yPeriod parameters by applying the
            // WorldToGrid transformation for the source coverage.
            //
            // I am assuming that the supplied values are in the same CRS as the
            // source coverage. We here apply
            //
            // /////////////////////////////////////////////////////////////////////
            final double xPeriod = parameters.parameter("xPeriod").doubleValue();
            final double yPeriod = parameters.parameter("yPeriod").doubleValue();
            if (!Double.isNaN(xPeriod) && !Double.isNaN(yPeriod)) {

                // build the new one that spans over the requested area
                // NOTE:
                //                final Position2D LLC = new Position2D(crs, envelope.x,
                // envelope.y);
                //                LLC.setCoordinateReferenceSystem(crs);
                //                final Position2D URC =
                //                        new Position2D(crs, envelope.x + xPeriod, envelope.y +
                // yPeriod);
                //                URC.setCoordinateReferenceSystem(crs);

                final ReferencedEnvelope shrinkedEnvelope = new ReferencedEnvelope(
                        envelope.getMinX(),
                        envelope.getMinX() + xPeriod,
                        envelope.getMinY(),
                        envelope.getMinY() + yPeriod,
                        crs);

                // transform back into raster space
                final Rectangle2D transformedEnv =
                        CRS.transform(worldToGridTransform, shrinkedEnvelope).toRectangle2D();

                // block settings
                block.setParameter("xPeriod", Integer.valueOf((int) transformedEnv.getWidth()));
                block.setParameter("yPeriod", Integer.valueOf((int) transformedEnv.getHeight()));
            }
            // /////////////////////////////////////////////////////////////////////
            //
            // Transcode the polygon parameter into a roi.
            //
            // I am assuming that the supplied values are in the same
            // CRS as the source coverage. We here apply
            //
            // /////////////////////////////////////////////////////////////////////
            final Object o = parameters.parameter("roi").getValue();
            if (o != null && o instanceof Polygon) {
                final Polygon roiInput = (Polygon) o;
                if (new ReferencedEnvelope(roiInput.getEnvelopeInternal(), source.getCoordinateReferenceSystem2D())
                        .intersects((Envelope) new ReferencedEnvelope(envelope))) {
                    final java.awt.Polygon shapePolygon = convertPolygon(roiInput, worldToGridTransform);

                    block.setParameter("roi", new ROIShape(shapePolygon));
                }
            }

            // Handle JAI-EXT parameters if needed
            handleJAIEXTParams(block, parameters);
            // Returning the parameterBlock
            return block;
        } catch (Exception e) {
            // //
            //
            // Something bad happened here Let's wrap and propagate the error.
            //
            // //
            final CoverageProcessingException ce = new CoverageProcessingException(e);
            throw ce;
        }
    }

    /**
     * Converte a JTS {@link Polygon}, which represents a ROI, into an AWT {@link java.awt.Polygon} by means of the
     * provided {@link MathTransform}.
     *
     * @param roiInput the input ROI as a JTS {@link Polygon}.
     * @param worldToGridTransform the {@link MathTransform} to apply to the input ROI.
     * @return an AWT {@link java.awt.Polygon}.
     * @throws TransformException in case the provided {@link MathTransform} chokes.
     */
    protected static java.awt.Polygon convertPolygon(final Polygon roiInput, MathTransform worldToGridTransform)
            throws TransformException {
        final boolean isIdentity = worldToGridTransform.isIdentity();
        final java.awt.Polygon retValue = new java.awt.Polygon();
        final double[] coords = new double[2];
        final LineString exteriorRing = roiInput.getExteriorRing();
        final CoordinateSequence exteriorRingCS = exteriorRing.getCoordinateSequence();
        final int numCoords = exteriorRingCS.size();
        for (int i = 0; i < numCoords; i++) {
            // get the actual coord
            coords[0] = exteriorRingCS.getX(i);
            coords[1] = exteriorRingCS.getY(i);

            // transform it
            if (!isIdentity) worldToGridTransform.transform(coords, 0, coords, 0, 1);

            // send it back to the returned polygon
            retValue.addPoint((int) (coords[0] + 0.5d), (int) (coords[1] + 0.5d));
        }

        // return the created polygon.
        return retValue;
    }
}
