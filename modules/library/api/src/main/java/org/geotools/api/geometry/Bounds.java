/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.geometry;

import java.awt.geom.Rectangle2D;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;

/**
 * A minimum bounding box or rectangle. Regardless of dimension, an {@code Envelope} can be represented without
 * ambiguity as two direct positions (coordinate points). To encode an {@code Envelope}, it is sufficient to encode
 * these two points. This is consistent with all of the data types in this specification, their state is represented by
 * their publicly accessible attributes.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as">ISO 19107</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 * @see org.geotools.api.coverage.grid.GridEnvelope
 */
public interface Bounds {
    /**
     * Returns the envelope coordinate reference system, or {@code null} if unknown. If non-null, it shall be the same
     * as {@linkplain #getLowerCorner lower corner} and {@linkplain #getUpperCorner upper corner} CRS.
     *
     * @return The envelope CRS, or {@code null} if unknown.
     * @since GeoAPI 2.1
     */
    CoordinateReferenceSystem getCoordinateReferenceSystem();

    /**
     * The length of coordinate sequence (the number of entries) in this envelope. Mandatory even when the
     * {@linkplain #getCoordinateReferenceSystem coordinate reference system} is unknown.
     *
     * @return The dimensionality of this envelope.
     * @since GeoAPI 2.0
     */
    int getDimension();

    /**
     * A coordinate position consisting of all the minimal ordinates for each dimension for all points within the
     * {@code Envelope}.
     *
     * @return The lower corner.
     */
    Position getLowerCorner();

    /**
     * A coordinate position consisting of all the maximal ordinates for each dimension for all points within the
     * {@code Envelope}.
     *
     * @return The upper corner.
     */
    Position getUpperCorner();

    /**
     * Returns the minimal ordinate along the specified dimension. This is a shortcut for the following without the cost
     * of creating a temporary {@link Position} object:
     *
     * <blockquote>
     *
     * <code>
     * {@linkplain #getLowerCorner}.{@linkplain Position#getOrdinate getOrdinate}(dimension)
     * </code>
     *
     * </blockquote>
     *
     * @param dimension The dimension for which to obtain the ordinate value.
     * @return The minimal ordinate at the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater than the
     *     {@linkplain #getDimension envelope dimension}.
     * @see Rectangle2D#getMinX
     * @see Rectangle2D#getMinY
     * @since GeoAPI 2.0
     */
    double getMinimum(int dimension) throws IndexOutOfBoundsException;

    /**
     * Returns the maximal ordinate along the specified dimension. This is a shortcut for the following without the cost
     * of creating a temporary {@link Position} object:
     *
     * <blockquote>
     *
     * <code>
     * {@linkplain #getUpperCorner}.{@linkplain Position#getOrdinate getOrdinate}(dimension)
     * </code>
     *
     * </blockquote>
     *
     * @param dimension The dimension for which to obtain the ordinate value.
     * @return The maximal ordinate at the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater than the
     *     {@linkplain #getDimension envelope dimension}.
     * @see Rectangle2D#getMaxX
     * @see Rectangle2D#getMaxY
     * @since GeoAPI 2.0
     */
    double getMaximum(int dimension) throws IndexOutOfBoundsException;

    /**
     * Returns the median ordinate along the specified dimension. The result should be equals (minus rounding error) to:
     *
     * <blockquote>
     *
     * <code>
     * ({@linkplain #getMinimum getMinimum}(dimension) + {@linkplain #getMaximum getMaximum}(dimension)) / 2
     * </code>
     *
     * </blockquote>
     *
     * @param dimension The dimension for which to obtain the ordinate value.
     * @return The median ordinate at the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater than the
     *     {@linkplain #getDimension envelope dimension}.
     * @see Rectangle2D#getCenterX
     * @see Rectangle2D#getCenterY
     * @since GeoAPI 2.2
     */
    double getMedian(int dimension) throws IndexOutOfBoundsException;

    /**
     * Returns the envelope span (typically width or height) along the specified dimension. The result should be equals
     * (minus rounding error) to:
     *
     * <blockquote>
     *
     * <code>
     * {@linkplain #getMaximum getMaximum}(dimension) - {@linkplain #getMinimum getMinimum}(dimension)
     * </code>
     *
     * </blockquote>
     *
     * @param dimension The dimension for which to obtain the ordinate value.
     * @return The span (typically width or height) at the given dimension.
     * @throws IndexOutOfBoundsException If the given index is negative or is equals or greater than the
     *     {@linkplain #getDimension envelope dimension}.
     * @see Rectangle2D#getWidth
     * @see Rectangle2D#getHeight
     * @since GeoAPI 2.2
     */
    double getSpan(int dimension) throws IndexOutOfBoundsException;
}
