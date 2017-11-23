/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file author is Christian Foltin
 *  It is modified by Dimitry Polivaev in 2008.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.plugin.svg;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import org.apache.batik.svggen.SVGGraphics2D;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.features.export.mindmapmode.ExportController;
import org.freeplane.features.map.MapModel;
import org.freeplane.features.mode.Controller;
import org.freeplane.view.swing.map.MapView;

class ExportSvg extends ExportVectorGraphic {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void export(MapModel map, File chosenFile) {
		if (!ExportController.getContoller().checkCurrentMap(map)){
			return;
		}
		try {
			final MapView view = (MapView) Controller.getCurrentController().getMapViewManager().getMapViewComponent();
			if (view == null) {
				return;
			}
			Controller.getCurrentController().getViewController().setWaitingCursor(true);
			final SVGGraphics2D g2d = fillSVGGraphics2D(view);
			final FileOutputStream bos = new FileOutputStream(chosenFile);
			final BufferedOutputStream bufStream = new BufferedOutputStream(bos);
			final OutputStreamWriter osw = new OutputStreamWriter(bufStream, "UTF-8");
			g2d.stream(osw);
			osw.flush();
			bos.flush();
			bos.close();
		}
		catch (final Exception ex) {
			org.freeplane.core.util.LogUtils.warn(ex);
			UITools.errorMessage(ex.getLocalizedMessage());
		}
		finally{
			Controller.getCurrentController().getViewController().setWaitingCursor(false);
		}
	}

}
