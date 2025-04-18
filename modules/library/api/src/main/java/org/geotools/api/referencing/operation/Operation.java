/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2003-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.referencing.operation;

import org.geotools.api.parameter.ParameterValueGroup;

/**
 * A parameterized mathematical operation on coordinates that transforms or converts coordinates to another coordinate
 * reference system. This coordinate operation thus uses an operation method, usually with associated parameter values.
 *
 * @version <A HREF="http://portal.opengeospatial.org/files/?artifact_id=6716">Abstract specification 2.0</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 1.0
 * @see OperationMethod
 */
public interface Operation extends SingleOperation {
    /**
     * Returns the operation method.
     *
     * @return The operation method.
     */
    OperationMethod getMethod();

    /**
     * Returns the parameter values.
     *
     * @return The parameter values.
     * @rename Added "{@code Parameter}" prefix for more consistency with the return type.
     */
    ParameterValueGroup getParameterValues();
}
