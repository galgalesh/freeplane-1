/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2008 Dimitry Polivaev
 *
 *  This file author is Dimitry Polivaev
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
package org.freeplane.view.swing.map.attribute;

import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import org.freeplane.core.ui.LengthUnits;
import org.freeplane.core.util.LogUtils;
import org.freeplane.core.util.Quantity;
import org.freeplane.features.attribute.AttributeController;
import org.freeplane.features.attribute.AttributeRegistry;
import org.freeplane.features.attribute.IAttributeTableModel;
import org.freeplane.features.attribute.NodeAttributeTableModel;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.mode.ModeController;
import org.freeplane.features.text.TextController;
import org.freeplane.view.swing.map.NodeView;

/**
 * @author Dimitry Polivaev
 */
abstract class AttributeTableModelDecoratorAdapter extends AbstractTableModel 
		implements IAttributeTableModel,
        TableModelListener, ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final private AttributeController attributeController;
	private AttributeRegistry attributeRegistry;
	private NodeAttributeTableModel nodeAttributeModel;
	final private TextController textController;

	public AttributeTableModelDecoratorAdapter(final AttributeView attrView) {
		super();
		final ModeController modeController = attrView.getMapView().getModeController();
		attributeController = AttributeController.getController(modeController);
		textController = TextController.getController(modeController);
		setNodeAttributeModel(attrView.getAttributes());
		setAttributeRegistry(attrView.getAttributeRegistry());
		getNodeAttributeModel().getNode();
		addListeners();
	}

	private void addListeners() {
		getNodeAttributeModel().addTableModelListener(this);
		getAttributeRegistry().addChangeListener(this);
	}

	/**
	 * @param view
	 */
	public abstract boolean areAttributesVisible();

	public void editingCanceled() {
	}

	public AttributeController getAttributeController() {
		return attributeController;
	}

	public AttributeRegistry getAttributeRegistry() {
		return attributeRegistry;
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex) {
		return getNodeAttributeModel().getColumnClass(columnIndex);
	}

	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnName(final int columnIndex) {
		return getNodeAttributeModel().getColumnName(columnIndex);
	}

	public Quantity<LengthUnits> getColumnWidth(final int col) {
		return getNodeAttributeModel().getColumnWidth(col);
	}

	public NodeModel getNode() {
		return getNodeAttributeModel().getNode();
	}

	public NodeAttributeTableModel getNodeAttributeModel() {
		return nodeAttributeModel;
	}

	private void removeListeners() {
		getNodeAttributeModel().removeTableModelListener(this);
		getAttributeRegistry().removeChangeListener(this);
	}

	public void setAttributeRegistry(final AttributeRegistry attributeRegistry) {
		this.attributeRegistry = attributeRegistry;
	}

	public void setColumnWidth(final int col, final Quantity<LengthUnits> width) {
		getAttributeController().performSetColumnWidth(getNodeAttributeModel(), col, width);
	}

	public void setNodeAttributeModel(final NodeAttributeTableModel nodeAttributeModel) {
		this.nodeAttributeModel = nodeAttributeModel;
		int rowCount = nodeAttributeModel.getRowCount();
		cacheTransformedValues(0, (rowCount-1));
	}

	private void cacheTransformedValue(int row) {
			try {
				final Object value = nodeAttributeModel.getValueAt(row, 1);
				if (value != null)
					textController.getTransformedText(value.toString(), getNode(), null);
            }
            catch (Exception e) {
            	LogUtils.warn(e);
            }
	}

	public void viewRemoved(NodeView nodeView) {
		removeListeners();
	}
	public void tableChanged(final TableModelEvent e) {
		switch(e.getType()){
		case TableModelEvent.INSERT:
		case TableModelEvent.UPDATE:
			cacheTransformedValues(e.getFirstRow(), e.getLastRow());
		}
		
	}
	private void cacheTransformedValues(int firstRow, int lastRow) {
		for(int row = firstRow; row <= lastRow; row++){
			cacheTransformedValue(row);
		}
	}
}
