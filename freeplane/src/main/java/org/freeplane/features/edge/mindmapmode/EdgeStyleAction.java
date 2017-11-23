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
package org.freeplane.features.edge.mindmapmode;

import java.awt.event.ActionEvent;

import org.freeplane.core.ui.AMultipleNodeAction;
import org.freeplane.core.ui.SelectableAction;
import org.freeplane.features.edge.EdgeController;
import org.freeplane.features.edge.EdgeModel;
import org.freeplane.features.edge.EdgeStyle;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.mode.Controller;

@SelectableAction(checkOnNodeChange = true)
class EdgeStyleAction extends AMultipleNodeAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final private EdgeStyle mStyle;

	public EdgeStyleAction( final EdgeStyle style) {
		super("EdgeStyleAction." + style);
		mStyle = style;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * freeplane.modes.mindmapmode.actions.MultipleNodeAction#actionPerformed
	 * (freeplane.modes.NodeModel)
	 */
	@Override
	protected void actionPerformed(final ActionEvent e, final NodeModel node) {
		((MEdgeController) EdgeController.getController()).setStyle(node, mStyle);
	}

	@Override
	public void setSelected() {
		final NodeModel node = Controller.getCurrentModeController().getMapController().getSelectedNode();
		final EdgeModel model = EdgeModel.getModel(node);
		if (model != null) {
			if (mStyle.equals(model.getStyle())) {
				setSelected(true);
				return;
			}
		}
		setSelected(false);
	}
}
