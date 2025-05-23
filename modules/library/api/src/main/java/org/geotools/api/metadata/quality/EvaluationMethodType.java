/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2004-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.metadata.quality;

import static org.geotools.api.annotation.Obligation.CONDITIONAL;
import static org.geotools.api.annotation.Specification.ISO_19115;

import java.util.ArrayList;
import java.util.List;
import org.geotools.api.annotation.UML;
import org.geotools.api.util.CodeList;

/**
 * Type of method for evaluating an identified data quality measure.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as#01-111">ISO 19115</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 2.0
 */
@UML(identifier = "DQ_EvaluationMethodTypeCode", specification = ISO_19115)
public final class EvaluationMethodType extends CodeList<EvaluationMethodType> {
    /** Serial number for compatibility with different versions. */
    private static final long serialVersionUID = -2481257874205996202L;

    /** List of all enumerations of this type. Must be declared before any enum declaration. */
    private static final List<EvaluationMethodType> VALUES = new ArrayList<>(3);

    /**
     * Method of evaluating the quality of a dataset based on inspection of items within the dataset, where all data
     * required is internal to the dataset being evaluated.
     */
    @UML(identifier = "directInternal", obligation = CONDITIONAL, specification = ISO_19115)
    public static final EvaluationMethodType DIRECT_INTERNAL = new EvaluationMethodType("DIRECT_INTERNAL");

    /**
     * Method of evaluating the quality of a dataset based on inspection of items within the dataset, where reference
     * data external to the dataset being evaluated is required.
     */
    @UML(identifier = "directExternal", obligation = CONDITIONAL, specification = ISO_19115)
    public static final EvaluationMethodType DIRECT_EXTERNAL = new EvaluationMethodType("DIRECT_EXTERNAL");

    /** Method of evaluating the quality of a dataset based on external knowledge. */
    @UML(identifier = "indirect", obligation = CONDITIONAL, specification = ISO_19115)
    public static final EvaluationMethodType INDIRECT = new EvaluationMethodType("INDIRECT");

    /**
     * Constructs an enum with the given name. The new enum is automatically added to the list returned by
     * {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private EvaluationMethodType(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code EvaluationMethodType}s.
     *
     * @return The list of codes declared in the current JVM.
     */
    public static EvaluationMethodType[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new EvaluationMethodType[VALUES.size()]);
        }
    }

    /** Returns the list of enumerations of the same kind than this enum. */
    @Override
    public EvaluationMethodType[] family() {
        return values();
    }

    /**
     * Returns the evaluation method type that matches the given string, or returns a new one if none match it.
     *
     * @param code The name of the code to fetch or to create.
     * @return A code matching the given name.
     */
    public static EvaluationMethodType valueOf(String code) {
        return valueOf(EvaluationMethodType.class, code);
    }
}
