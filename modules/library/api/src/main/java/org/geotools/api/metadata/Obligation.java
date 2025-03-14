/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2011, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2004-2005, Open Geospatial Consortium Inc.
 *
 *    All Rights Reserved. http://www.opengis.org/legal/
 */
package org.geotools.api.metadata;

import static org.geotools.api.annotation.Specification.ISO_19115;

import java.util.ArrayList;
import java.util.List;
import org.geotools.api.annotation.UML;
import org.geotools.api.util.CodeList;

/**
 * Obligation of the element or entity.
 *
 * @version <A HREF="http://www.opengeospatial.org/standards/as#01-111">ISO 19115</A>
 * @author Martin Desruisseaux (IRD)
 * @since GeoAPI 2.0
 */
@UML(identifier = "MD_ObligationCode", specification = ISO_19115)
public final class Obligation extends CodeList<Obligation> {
    /** Serial number for compatibility with different versions. */
    private static final long serialVersionUID = -2135167450448770693L;

    /** List of all enumerations of this type. Must be declared before any enum declaration. */
    private static final List<Obligation> VALUES = new ArrayList<>(3);

    /** Element is always required. */
    @UML(
            identifier = "mandatory",
            obligation = org.geotools.api.annotation.Obligation.CONDITIONAL,
            specification = ISO_19115)
    public static final Obligation MANDATORY = new Obligation("MANDATORY");

    /** Element is not required. */
    @UML(
            identifier = "optional",
            obligation = org.geotools.api.annotation.Obligation.CONDITIONAL,
            specification = ISO_19115)
    public static final Obligation OPTIONAL = new Obligation("OPTIONAL");

    /** Element is required when a specific condition is met. */
    @UML(
            identifier = "conditional",
            obligation = org.geotools.api.annotation.Obligation.CONDITIONAL,
            specification = ISO_19115)
    public static final Obligation CONDITIONAL = new Obligation("CONDITIONAL");

    /**
     * Constructs an enum with the given name. The new enum is automatically added to the list returned by
     * {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private Obligation(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code Obligation}s.
     *
     * @return The list of codes declared in the current JVM.
     */
    public static Obligation[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new Obligation[VALUES.size()]);
        }
    }

    /** Returns the list of enumerations of the same kind than this enum. */
    @Override
    public Obligation[] family() {
        return values();
    }

    /**
     * Returns the obligation that matches the given string, or returns a new one if none match it.
     *
     * @param code The name of the code to fetch or to create.
     * @return A code matching the given name.
     */
    public static Obligation valueOf(String code) {
        return valueOf(Obligation.class, code);
    }
}
