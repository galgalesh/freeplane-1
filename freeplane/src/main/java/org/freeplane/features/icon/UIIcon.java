/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2009 Tamas Eppel
 *
 *  This file author is Tamas Eppel
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
package org.freeplane.features.icon;

import java.net.URL;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.LengthUnits;
import org.freeplane.core.util.Quantity;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.icon.factory.ImageIconFactory;
import org.freeplane.features.map.NodeModel;

/**
 * Base class for all icons used in FreePlane.
 *
 * @author Tamas Eppel
 *
 */
public class UIIcon implements IIconInformation, Comparable<UIIcon> {
	private static final String DEFAULT_IMAGE_PATH = "/images";
	protected static final String SEPARATOR = "/";
	protected static final String THEME_FOLDER_KEY = "icon.theme.folder";
	protected static final ResourceController RESOURCE_CONTROLLER = ResourceController.getResourceController();
	private static final Pattern parentDirPattern = Pattern.compile(SEPARATOR + "[^" + SEPARATOR + ".]+" + SEPARATOR
	        + "\\.\\." + SEPARATOR);
	private final String name;
	private final String fileName;
	private final String translationKeyLabel;
	private final String shortcutKey;
	private URL resourceURL;

	public UIIcon(final String name, final String fileName) {
		this(name, fileName, "", "?");
	}

	public UIIcon(final String name, final String fileName, final String translationKeyLabel) {
		this(name, fileName, translationKeyLabel, "?");
	}

	public UIIcon(final String name, final String fileName, final String translationKeyLabel, final String shortcutKey) {
		this.name = name;
		this.fileName = fileName;
		this.translationKeyLabel = translationKeyLabel;
		this.shortcutKey = shortcutKey;
	}

	public String getFileName() {
		return fileName;
	}

	/**
	 * @return key for the shortcut in the property file
	 */
	public String getShortcutKey() {
		return shortcutKey;
	}

	public String getTranslationKeyLabel() {
		return translationKeyLabel;
	}
	
	public String getTranslationValueLabel() {
		return TextUtils.getText(translationKeyLabel, "");
	}

	public String getName() {
		return name;
	}

	public Icon getIcon() {
		return ImageIconFactory.getInstance().getImageIcon(this);
	}

	public Icon getIcon(final NodeModel node) {
		final Quantity<LengthUnits> iconHeight = IconController.getController().getIconSize(node);
		return ImageIconFactory.getInstance().getImageIcon(this, iconHeight);
	}

	public KeyStroke getKeyStroke() {
		return null;
	}

	public String getImagePath() {
		return DEFAULT_IMAGE_PATH;
	}

	public URL getUrl() {
		if (resourceURL != null) {
			return resourceURL;
		}
		final String path = getPath();
		resourceURL = RESOURCE_CONTROLLER.getIconResource(path);
		return resourceURL;
	}

	public String getPath() {
		StringBuilder builder = new StringBuilder();
		builder = new StringBuilder();
		builder.append(this.getImagePath());
		builder.append(SEPARATOR);
		builder.append(fileName);
		final String path = parentDirPattern.matcher(builder.toString()).replaceFirst(SEPARATOR);
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((translationKeyLabel == null) ? 0 : translationKeyLabel.hashCode());
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((shortcutKey == null) ? 0 : shortcutKey.hashCode());
		result = prime * result + this.getClass().hashCode();
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final UIIcon other = (UIIcon) obj;
		if (translationKeyLabel == null) {
			if (other.translationKeyLabel != null) {
				return false;
			}
		}
		else if (!translationKeyLabel.equals(other.translationKeyLabel)) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		}
		else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		}
		else if (!name.equals(other.name)) {
			return false;
		}
		if (shortcutKey == null) {
			if (other.shortcutKey != null) {
				return false;
			}
		}
		else if (!shortcutKey.equals(other.shortcutKey)) {
			return false;
		}
		return true;
	}

	public int compareTo(final UIIcon uiIcon) {
		return this.getPath().compareTo(uiIcon.getPath());
	}

	@Override
	public String toString() {
		return name;
	}

}
