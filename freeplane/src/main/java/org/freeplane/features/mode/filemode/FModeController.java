/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitry Polivaev
 *
 *  This file is modified by Dimitry Polivaev in 2008.
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
package org.freeplane.features.mode.filemode;

import java.io.File;

import org.freeplane.features.map.filemode.FMapController;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.mode.ModeController;

public class FModeController extends ModeController {
	static public final String MODENAME = "File";

	public FModeController(final Controller controller) {
		super(controller);
	}

	@Override
	public String getModeName() {
		return FModeController.MODENAME;
	}

	@Override
	public void startup() {
		final Controller controller = getController();
		controller.getMapViewManager().changeToMode(MODENAME);
		if (controller.getMap() == null) {
			((FMapController) getMapController()).newMap(File.listRoots());
		}
		super.startup();
	}
}
