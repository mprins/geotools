/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.referencing;

import org.geotools.api.metadata.Identifier;

/**
 * Identifier used for reference systems.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as#01-111">ISO 19115</A>
 * @author Ely Conn (Leica Geosystems Geospatial Imaging, LLC)
 * @since GeoAPI 2.1
 */
public interface ReferenceIdentifier extends Identifier {
    /**
     * Key for the <code>{@value}</code> property to be given to the
     * {@linkplain org.geotools.api.referencing.ObjectFactory CRS factory} <code>createFoo(&hellip;)</code> methods.
     * This is used for setting the value to be returned by {@link #getCodeSpace}.
     *
     * @see #getCodeSpace
     */
    String CODESPACE_KEY = "codespace";

    /**
     * Key for the <code>{@value}</code> property to be given to the
     * {@linkplain org.geotools.api.referencing.ObjectFactory CRS factory} <code>createFoo(&hellip;)</code> methods.
     * This is used for setting the value to be returned by {@link #getVersion}.
     *
     * @see #getVersion
     */
    String VERSION_KEY = "version";

    /**
     * Name or identifier of the person or organization responsible for namespace.
     *
     * @return The identifier code space.
     */
    String getCodeSpace();

    /**
     * Version identifier for the namespace, as specified by the code authority. When appropriate, the edition is
     * identified by the effective date, coded using ISO 8601 date format.
     *
     * @return The version for the namespace (for example the version of the underlying EPSG database).
     */
    String getVersion();
}
