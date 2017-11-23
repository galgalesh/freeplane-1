package org.freeplane.view.swing.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;

import org.freeplane.core.ui.components.UITools;
import org.freeplane.features.mode.ModeController;
import org.freeplane.features.nodestyle.ShapeConfigurationModel;

@SuppressWarnings("serial")
abstract public class ShapedMainView extends MainView {
	
	final private ShapeConfigurationModel shapeConfiguration;

	public ShapedMainView(ShapeConfigurationModel shapeConfiguration) {
		super();
		this.shapeConfiguration = shapeConfiguration;
	}

	public ShapeConfigurationModel getShapeConfiguration(){
		return shapeConfiguration;
	}

	@Override
    public
	Point getLeftPoint() {
		final Point in = new Point(0, getHeight() / 2);
		return in;
	}

	@Override
    public
	Point getRightPoint() {
		final Point in = getLeftPoint();
		in.x = getWidth() - 1;
		return in;
	}

	@Override
	public void paintComponent(final Graphics graphics) {
		final Graphics2D g = (Graphics2D) graphics;
		final NodeView nodeView = getNodeView();
		if (nodeView.getModel() == null) {
			return;
		}
		final ModeController modeController = getNodeView().getMap().getModeController();
		final Object renderingHint = modeController.getController().getMapViewManager().setEdgesRenderingHint(g);
		paintBackgound(g);
		paintDragOver(g);
		final Color borderColor = getBorderColor();
		final Color oldColor = g.getColor();
		g.setColor(borderColor);
		final Stroke oldStroke = g.getStroke();
		g.setStroke(UITools.createStroke(getPaintedBorderWidth(), getDash().variant, BasicStroke.JOIN_MITER));
		paintNodeShape(g);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, renderingHint);
		g.setColor(oldColor);
		g.setStroke(oldStroke);
		super.paintComponent(g);
	}
	
	abstract protected void paintNodeShape(final Graphics2D g);

}
