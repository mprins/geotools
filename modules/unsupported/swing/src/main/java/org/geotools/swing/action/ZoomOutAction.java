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

package org.geotools.swing.action;

import java.awt.event.ActionEvent;
import org.geotools.swing.MapPane;
import org.geotools.swing.tool.ZoomOutTool;

/**
 * An action for connect a control (probably a JButton) to the ZoomOutTool for ZoomOutg the map with mouse clicks or
 * drags.
 *
 * @author Michael Bedward
 * @since 2.6
 * @version $Id$
 */
public class ZoomOutAction extends MapAction {

    /**
     * Constructor. The associated control will be labelled with an icon.
     *
     * @param mapPane the map pane being serviced by this action
     */
    public ZoomOutAction(MapPane mapPane) {
        this(mapPane, false);
    }

    /**
     * Constructor. The associated control will be labelled with an icon and, optionally, the tool name.
     *
     * @param mapPane the map pane being serviced by this action
     * @param showToolName set to true for the control to display the tool name
     */
    public ZoomOutAction(MapPane mapPane, boolean showToolName) {
        String toolName = showToolName ? ZoomOutTool.TOOL_NAME : null;
        super.init(mapPane, toolName, ZoomOutTool.TOOL_TIP, ZoomOutTool.ICON_IMAGE);
    }

    /**
     * Called when the associated control is activated. Leads to the map pane's cursor tool being set to a new
     * ZoomOutTool object
     *
     * @param ev the event (not used)
     */
    @Override
    public void actionPerformed(ActionEvent ev) {
        getMapPane().setCursorTool(new ZoomOutTool());
    }
}
