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
package org.freeplane.core.resources.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import org.freeplane.core.util.TextUtils;

import com.jgoodies.forms.builder.DefaultFormBuilder;

/** implementation of <remind> properties. */
public class RemindValueProperty extends PropertyBean implements IPropertyControl {
	public static final String DON_T_TOUCH_VALUE = "";
	protected static final int DON_T_TOUCH_VALUE_INT = 2;
	static public final String FALSE_VALUE = "false";
	protected static final int FALSE_VALUE_INT = 1;
	static public final String TRUE_VALUE = "true";
	protected static final int TRUE_VALUE_INT = 0;
	JButton mButton = new JButton();
	int state = 0;

    /**
     */
    public RemindValueProperty(final String name) {
		super(name);
		mButton.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				setState((getState() + 1) % 3);
				firePropertyChangeEvent();
			}
		});
	}

	private int getState() {
		return state;
	}

	@Override
	public String getValue() {
		switch (state) {
			case TRUE_VALUE_INT:
				return TRUE_VALUE;
			case FALSE_VALUE_INT:
				return FALSE_VALUE;
			case DON_T_TOUCH_VALUE_INT:
				return DON_T_TOUCH_VALUE;
		}
		return null;
	}

	public void layout(final DefaultFormBuilder builder) {
		layout(builder, mButton);
	}

	public void setEnabled(final boolean pEnabled) {
		mButton.setEnabled(pEnabled);
	}

	/**
	 *
	 */
	protected void setState(final int newState) {
        state = newState;
        String[] strings;
        strings = new String[3];
        strings[RemindValueProperty.TRUE_VALUE_INT] = TextUtils.getText("OptionalDontShowMeAgainDialog.ok")
            .replaceFirst("&", "");
        strings[RemindValueProperty.FALSE_VALUE_INT] = TextUtils.getText("OptionalDontShowMeAgainDialog.cancel")
            .replaceFirst("&", "");
        strings[RemindValueProperty.DON_T_TOUCH_VALUE_INT] = TextUtils.getText("OptionPanel.ask").replaceFirst("&",
            "");
        mButton.setText(strings[state]);
	}

	@Override
	public void setValue(final String value) {
		if (value == null
		        || !(value.toLowerCase().equals(TRUE_VALUE) || value.toLowerCase().equals(FALSE_VALUE) || value
		            .toLowerCase().equals(DON_T_TOUCH_VALUE))) {
			throw new IllegalArgumentException("Cannot set a boolean to " + value);
		}
		setState(transformString(value));
	}

	private int transformString(final String string) {
		if (string == null) {
			return RemindValueProperty.DON_T_TOUCH_VALUE_INT;
		}
		if (string.toLowerCase().equals(TRUE_VALUE)) {
			return RemindValueProperty.TRUE_VALUE_INT;
		}
		if (string.toLowerCase().equals(FALSE_VALUE)) {
			return RemindValueProperty.FALSE_VALUE_INT;
		}
		return RemindValueProperty.DON_T_TOUCH_VALUE_INT;
	}
}
