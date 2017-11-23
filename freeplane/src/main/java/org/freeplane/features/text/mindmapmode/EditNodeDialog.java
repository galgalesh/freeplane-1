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
package org.freeplane.features.text.mindmapmode;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.RootPaneContainer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;

import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.LabelAndMnemonicSetter;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.spellchecker.mindmapmode.SpellCheckerController;
import org.freeplane.features.ui.IMapViewManager;

/**
 * @author foltin
 */
public class EditNodeDialog extends EditNodeBase {
	private JTextComponent textComponent;
	private final boolean enableSplit;

	private class LongNodeDialog extends EditDialog {

		public LongNodeDialog(final RootPaneContainer frame, final String title, final Color background) {
			super(EditNodeDialog.this, title, frame);
			final IMapViewManager viewController = Controller.getCurrentModeController().getController()
			    .getMapViewManager();
			final JScrollPane editorScrollPane;
			if (textComponent == null) {
				JTextArea textArea = new JTextArea(getText());
				textArea.setLineWrap(true);
				textArea.setWrapStyleWord(true);
				textComponent = textArea;
				editorScrollPane = new JScrollPane(textComponent);
				final SpellCheckerController spellCheckerController = SpellCheckerController.getController();
				spellCheckerController.enableAutoSpell(textComponent, true);
				final Font nodeFont = viewController.getFont(getNode());
				textComponent.setFont(nodeFont);
				final Color nodeTextColor = viewController.getTextColor(getNode());
				textComponent.setForeground(nodeTextColor);
				textComponent.setBackground(background);
				textComponent.setCaretColor(nodeTextColor);
				editorScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
				int preferredHeight = viewController.getComponent(getNode()).getHeight();
				preferredHeight = Math.max(preferredHeight, Integer.parseInt(ResourceController.getResourceController()
				    .getProperty("el__min_default_window_height")));
				preferredHeight = Math.min(preferredHeight, Integer.parseInt(ResourceController.getResourceController()
				    .getProperty("el__max_default_window_height")));
				int preferredWidth = viewController.getComponent(getNode()).getWidth();
				preferredWidth = Math.max(preferredWidth, Integer.parseInt(ResourceController.getResourceController()
				    .getProperty("el__min_default_window_width")));
				preferredWidth = Math.min(preferredWidth, Integer.parseInt(ResourceController.getResourceController()
				    .getProperty("el__max_default_window_width")));
				editorScrollPane.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
			}
			else {
				textComponent.setText(getText());
				final JScrollPane ancestorScrollPane = (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, textComponent);
				if (ancestorScrollPane != null) {
					editorScrollPane = ancestorScrollPane;
				}
				else {
					editorScrollPane = new JScrollPane(textComponent);
				}
			}
			final JPanel panel = new JPanel();
			final JButton okButton = new JButton();
			final JButton cancelButton = new JButton();
			final JButton splitButton = new JButton();
			final JCheckBox enterConfirms = new JCheckBox("", ResourceController.getResourceController()
			    .getBooleanProperty("el__enter_confirms_by_default"));
			LabelAndMnemonicSetter.setLabelAndMnemonic(okButton, TextUtils.getRawText("ok"));
			LabelAndMnemonicSetter.setLabelAndMnemonic(cancelButton, TextUtils.getRawText("cancel"));
			LabelAndMnemonicSetter.setLabelAndMnemonic(splitButton, TextUtils.getRawText("split"));
			LabelAndMnemonicSetter.setLabelAndMnemonic(enterConfirms, TextUtils.getRawText("enter_confirms"));
			okButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					submit();
				}
			});
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					cancel();
				}
			});
			splitButton.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					split();
				}
			});
			enterConfirms.addActionListener(new ActionListener() {
				public void actionPerformed(final ActionEvent e) {
					textComponent.requestFocus();
					ResourceController.getResourceController().setProperty("el__enter_confirms_by_default",
					    Boolean.toString(enterConfirms.isSelected()));
				}
			});
			textComponent.addKeyListener(new KeyListener() {
				public void keyPressed(final KeyEvent e) {
					switch (e.getKeyCode()) {
						case KeyEvent.VK_ESCAPE:
							e.consume();
							confirmedCancel();
							break;
						case KeyEvent.VK_ENTER:
							e.consume();
							if ((e.getModifiers() & InputEvent.SHIFT_MASK) != 0
							        || enterConfirms.isSelected() == ((e.getModifiers() & InputEvent.ALT_MASK) != 0)) {
								insertString("\n");
								break;
							}
							submit();
							break;
						case KeyEvent.VK_TAB:
							e.consume();
							insertString("    ");
							break;
					}
				}

				public void insertString(final String text) {
					try {
						textComponent.getDocument().insertString(textComponent.getCaretPosition(), text, null);
					}
					catch (BadLocationException e) {
						e.printStackTrace();
					}
				}

				public void keyReleased(final KeyEvent e) {
				}

				public void keyTyped(final KeyEvent e) {
				}
			});
			textComponent.addMouseListener(new MouseListener() {
				private void conditionallyShowPopup(final MouseEvent e) {
					if (e.isPopupTrigger()) {
						final Component component = e.getComponent();
						final JPopupMenu popupMenu = createPopupMenu(component);
						popupMenu.show(component, e.getX(), e.getY());
						e.consume();
					}
				}

				public void mouseClicked(final MouseEvent e) {
				}

				public void mouseEntered(final MouseEvent e) {
				}

				public void mouseExited(final MouseEvent e) {
				}

				public void mousePressed(final MouseEvent e) {
					conditionallyShowPopup(e);
				}

				public void mouseReleased(final MouseEvent e) {
					conditionallyShowPopup(e);
				}
			});
			final JPanel buttonPane = new JPanel();
			buttonPane.add(enterConfirms);
			buttonPane.add(okButton);
			buttonPane.add(cancelButton);
			if (enableSplit)
				buttonPane.add(splitButton);
			buttonPane.setMaximumSize(new Dimension(1000, 20));
			if (ResourceController.getResourceController().getBooleanProperty("el__buttons_above")) {
				panel.add(buttonPane);
				panel.add(editorScrollPane);
			}
			else {
				panel.add(editorScrollPane);
				panel.add(buttonPane);
			}
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			getDialog().setContentPane(panel);
		}

		/*
		 * (non-Javadoc)
		 * @see freeplane.view.mindmapview.EditNodeBase.Dialog#cancel()
		 */
		@Override
		protected void cancel() {
			super.cancel();
			getEditControl().cancel();
		}

		@Override
		public Component getMostRecentFocusOwner() {
			if (getDialog().isFocused()) {
				return getFocusOwner();
			}
			else {
				return textComponent;
			}
		}

		/*
		 * (non-Javadoc)
		 * @see freeplane.view.mindmapview.EditNodeBase.Dialog#isChanged()
		 */
		@Override
		protected boolean isChanged() {
			return !getText().equals(textComponent.getText());
		}

		@Override
		public void show() {
			textComponent.requestFocus();
			super.show();
		}

		/*
		 * (non-Javadoc)
		 * @see freeplane.view.mindmapview.EditNodeBase.Dialog#split()
		 */
		@Override
		protected void split() {
			super.split();
			getEditControl().split(textComponent.getText(), textComponent.getCaretPosition());
		}

		/*
		 * (non-Javadoc)
		 * @see freeplane.view.mindmapview.EditNodeBase.Dialog#submit()
		 */
		@Override
		protected void submit() {
			super.submit();
			getEditControl().ok(textComponent.getText());
		}
	}

	/** Private variable to hold the last value of the "Enter confirms" state. */
	final private KeyEvent firstEvent;
	private String title;
	private boolean isModal;

	public EditNodeDialog(final NodeModel node, final String text, final KeyEvent firstEvent,
	                      final IEditControl editControl, boolean enableSplit) {
		super(node, text, editControl);
		this.firstEvent = firstEvent;
		this.enableSplit = enableSplit;
	}

	public EditNodeDialog(NodeModel nodeModel, String text, KeyEvent firstEvent, IEditControl editControl,
	                      boolean enableSplit, JEditorPane textEditor) {
		this(nodeModel, text, firstEvent, editControl, enableSplit);
		textComponent = textEditor;
	}

	public void show(final RootPaneContainer frame) {
		if (title == null) {
			title = TextUtils.getText("edit_long_node");
		}
		final EditDialog dialog = new LongNodeDialog(frame, title, getBackground());
		redispatchKeyEvents(textComponent, firstEvent);
        if (firstEvent == null) {
            textComponent.setCaretPosition(textComponent.getDocument().getLength());
        }
		dialog.getDialog().setModal(isModal);
		dialog.getDialog().pack();
		Controller.getCurrentModeController().getController().getMapViewManager().scrollNodeToVisible(node);
		if (ResourceController.getResourceController().getBooleanProperty("el__position_window_below_node")) {
			UITools.setDialogLocationUnder(dialog.getDialog(), getNode());
		}
		else {
			UITools.setDialogLocationRelativeTo(dialog.getDialog(), getNode());
		}
		dialog.show();
		dialog.getDialog().addComponentListener(new ComponentListener() {
			public void componentShown(final ComponentEvent e) {
			}

			public void componentResized(final ComponentEvent e) {
			}

			public void componentMoved(final ComponentEvent e) {
			}

			public void componentHidden(final ComponentEvent e) {
				dialog.dispose();
			}
		});
	}

	public void setTitle(String title) {
		this.title = title;
	}

	protected void setModal(boolean isModal) {
		this.isModal = isModal;
	}
}
