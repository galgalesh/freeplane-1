/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Dimitry Polivaev
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
package org.freeplane.features.link;

import org.freeplane.core.util.TextUtils;
import org.freeplane.features.filter.condition.ConditionFactory;

/**
 * @author Dimitry Polivaev
 * Mar 7, 2009
 */
public class ConnectorLabelEqualsCondition extends ConnectorLabelCondition {
	public static final String NAME = "connector_label_equals";

	public ConnectorLabelEqualsCondition(final String text, final boolean matchCase,
			final boolean matchApproximately) {
		super(text, matchCase, matchApproximately);
	}

	@Override
	protected boolean checkLink(final ConnectorModel connector) {
		final String middleLabel = connector.getMiddleLabel();
		if (equals(middleLabel)) {
			return true;
		}
		final String sourceLabel = connector.getSourceLabel();
		if (equals(sourceLabel)) {
			return true;
		}
		final String targetLabel = connector.getTargetLabel();
		if (equals(targetLabel)) {
			return true;
		}
		return false;
	}

	private boolean equals(final String middleLabel) {
		if (middleLabel == null) {
			return false;
		}
		return getStringMatchingStrategy().matches(getText(), middleLabel, false, matchCase());
		
//		if (matchCase()) {
//			return middleLabel.equals(getText());
//		}
//		return middleLabel.toLowerCase().equals(getText());
	}

	@Override
	protected String createDescription() {
		final String condition = TextUtils.getText(LinkConditionController.CONNECTOR_LABEL);
		final String simpleCondition = TextUtils.getText(ConditionFactory.FILTER_IS_EQUAL_TO);
		return ConditionFactory.createDescription(condition, simpleCondition, getText(), matchCase(), matchApproximately());
	}

	@Override
	protected String getName() {
		return NAME;
	}
}
