/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2012 Freeplane team and others
 *
 *  this file is created by Dimitry Polivaev in 2012.
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
package org.freeplane.core.ui.components.html;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.html.HTMLDocument;

public class ScaledHTML extends BasicHTML{

    /**
     * Create an html renderer for the given component and
     * string of html.
     */
    public static View createHTMLView(JLabel c, String html) {
	ScaledEditorKit kit = SynchronousScaledEditorKit.create();
	Document doc = kit.createDefaultDocument(c);
	Object base = c.getClientProperty(documentBaseKey);
	if (base instanceof URL) {
	    ((HTMLDocument)doc).setBase((URL)base);
	}
	Reader r = new StringReader(html);
	try {
	    kit.read(r, doc, 0);
	} catch (Throwable e) {
	}
	ViewFactory f = kit.getViewFactory();
	View hview = f.create(doc.getDefaultRootElement());
	View v = new Renderer(c, f, hview);
	return v;
    }

     public static void updateRenderer(JLabel c, String text) {
    	View value = null;
    	try{
    	View oldValue = (View)c.getClientProperty(propertyKey);
    	if (isHTMLString(text)) {
    		value = ScaledHTML.createHTMLView(c, text);
    	}
    	if (value != oldValue && oldValue != null) {
    		for (int i = 0; i < oldValue.getViewCount(); i++) {
    			oldValue.getView(i).setParent(null);
    		}
    	}
    	}
    	finally{
    		c.putClientProperty(BasicHTML.propertyKey, value);
    	}
    }


    /**
     * Overrides to the default stylesheet.  Should consider
     * just creating a completely fresh stylesheet.
     */
    static final String styleChanges = 
    "p { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }" +
    "body { margin-top: 0; margin-bottom: 0; margin-left: 0; margin-right: 0 }";

	/**
     * Root text view that acts as an HTML renderer.
     */
    public static class Renderer extends View {

        Renderer(JComponent c, ViewFactory f, View v) {
            super(null);
        setSizeRunning = true;   
	    host = c;
	    factory = f;
	    view = v;
	    view.setParent(this);
	    // initially layout to the preferred size
	    setSize(view.getPreferredSpan(X_AXIS), view.getPreferredSpan(Y_AXIS));
        }

        public AttributeSet getAttributes() {
	    return null;
	}

         public float getPreferredSpan(int axis) {
	    if (axis == X_AXIS) {
		// width currently laid out to
		return width;
	    }
	    return view.getPreferredSpan(axis);
        }
         
       public float getMinimumSpan(int axis) {
	    return view.getMinimumSpan(axis);
        }

        public float getMaximumSpan(int axis) {
	    return Integer.MAX_VALUE;
        }

        public void preferenceChanged(View child, boolean width, boolean height) {
        	if(! setSizeRunning){
        		setSize(view.getPreferredSpan(X_AXIS), view.getPreferredSpan(Y_AXIS));
        		host.revalidate();
        		host.repaint();
        	}
        }

        public float getAlignment(int axis) {
	    return view.getAlignment(axis);
        }

        public void paint(Graphics g, Shape allocation) {
	    Rectangle alloc = allocation.getBounds();
	    view.setSize(alloc.width, alloc.height);
	    view.paint(g, allocation);
        }
        
         public void setParent(View parent) {
            throw new Error("Can't set parent on root view");
        }

        public int getViewCount() {
            return 1;
        }
        public View getView(int n) {
            return view;
        }
        public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
	    return view.modelToView(pos, a, b);
        }

	public Shape modelToView(int p0, Position.Bias b0, int p1, 
				 Position.Bias b1, Shape a) throws BadLocationException {
	    return view.modelToView(p0, b0, p1, b1, a);
	}

        public int viewToModel(float x, float y, Shape a, Position.Bias[] bias) {
	    return view.viewToModel(x, y, a, bias);
        }

        public Document getDocument() {
            return view.getDocument();
        }
        
        public int getStartOffset() {
	    return view.getStartOffset();
        }

        public int getEndOffset() {
	    return view.getEndOffset();
        }

        public Element getElement() {
	    return view.getElement();
        }

        public void setSize(float width, float height) {
        	setSizeRunning = true;
        	this.width = (int) width;
        	view.setSize(width, height);
        	setSizeRunning = false;
        }

        public void resetSize() {
        	setSize(0, 0);
        	setSize(view.getPreferredSpan(X_AXIS), view.getPreferredSpan(Y_AXIS));
        }
        
        public Container getContainer() {
            return host;
        }
        
        public ViewFactory getViewFactory() {
	    return factory;
        }

	private int width;
        private View view;
	private ViewFactory factory;
	private JComponent host;
	private boolean setSizeRunning;

    }
}
