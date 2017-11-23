package org.freeplane.plugin.latex;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.JEditorPane;

import org.freeplane.core.resources.ResourceController;
import org.freeplane.core.ui.components.JRestrictedSizeScrollPane;
import org.freeplane.core.ui.components.UITools;
import org.freeplane.core.util.TextUtils;
import org.freeplane.features.format.PatternFormat;
import org.freeplane.features.map.NodeModel;
import org.freeplane.features.nodestyle.NodeStyleController;
import org.freeplane.features.text.AbstractContentTransformer;
import org.freeplane.features.text.TextController;
import org.freeplane.features.text.TransformationException;
import org.freeplane.features.text.mindmapmode.EditNodeBase;
import org.freeplane.features.text.mindmapmode.EditNodeBase.IEditControl;
import org.freeplane.features.text.mindmapmode.EditNodeDialog;
import org.freeplane.features.text.mindmapmode.IEditBaseCreator;
import org.freeplane.features.text.mindmapmode.MTextController;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXIcon;

public class LatexRenderer extends AbstractContentTransformer implements IEditBaseCreator {

	private static final String LATEX_EDITOR_FONT_SIZE = "latex_editor_font_size";
	private static final String LATEX_EDITOR_FONT = "latex_editor_font";
	private static final String LATEX_EDITOR_DISABLE = "latex_disable_editor";
	private static final String LATEX = "\\latex";
	private static final String UNPARSED_LATEX = "\\unparsedlatex";
	
	
	public LatexRenderer() {
		super(20);
	}

	public Object transformContent(TextController textController,
			Object content, NodeModel node, Object transformedExtension)
			throws TransformationException {
		return content;
	}
	
	private static boolean checkForLatexPrefix(final String nodeText, final String prefix)
	{
		int startLength = prefix.length() + 1; 
		return nodeText.length() > startLength && nodeText.startsWith(prefix) &&
			 Character.isWhitespace(nodeText.charAt(startLength - 1));
	}
	
	private static enum TargetMode { FOR_ICON, FOR_EDITOR };

	private String getLatexNode(final String nodeText, final String nodeFormat, final TargetMode mode)
	{
		boolean includePrefix = mode == TargetMode.FOR_EDITOR;
		
		if(checkForLatexPrefix(nodeText, LATEX)){
			return includePrefix ? nodeText : nodeText.substring(LATEX.length() + 1);
		}
		else if(LatexFormat.LATEX_FORMAT.equals(nodeFormat)){
			return nodeText;
		} else if(checkForLatexPrefix(nodeText, UNPARSED_LATEX) && mode == TargetMode.FOR_EDITOR) {
			return nodeText;
		} else if(UnparsedLatexFormat.UNPARSED_LATEX_FORMAT.equals(nodeFormat) && mode == TargetMode.FOR_EDITOR) {
			return nodeText;
		} else {
			return null;
		}
	}

	@Override
	public Icon getIcon(TextController textController, Object content,
			NodeModel node, Object transformedExtension) {
		if(transformedExtension == node.getUserObject()){
			String string = content.toString();
			String nodeFormat = textController.getNodeFormat(node);
			if (PatternFormat.IDENTITY_PATTERN.equals(nodeFormat))
				return null;

			final String latext = getLatexNode(string, nodeFormat, TargetMode.FOR_ICON);
			if (latext == null)
				return null;
			final NodeStyleController ncs = NodeStyleController.getController(textController.getModeController());
			final int maxWidth = ncs.getMaxWidth(node).toBaseUnitsRounded();
			TeXText teXt = new TeXText(latext);
			int fontSize = Math.round(ncs.getFontSize(node) * UITools.FONT_SCALE_FACTOR);
			TeXIcon icon = teXt.createTeXIcon(TeXConstants.STYLE_DISPLAY, fontSize, TeXConstants.ALIGN_LEFT, maxWidth);
			return icon;
		}
		return null;
	}

	public EditNodeBase createEditor(NodeModel node,
			IEditControl editControl, String text, boolean editLong) {
		MTextController textController = MTextController.getController();
		if (textController.isTextFormattingDisabled(node)) // Format=Text!
			return null;
		final KeyEvent firstKeyEvent = textController.getEventQueue().getFirstEvent();
		String nodeFormat = textController.getNodeFormat(node);
		final String latexText = getLatexNode(text, nodeFormat, TargetMode.FOR_EDITOR);
		
		// this option has been added to work around bugs in JSyntaxPane with Chinese characters
		if (ResourceController.getResourceController().getBooleanProperty(LATEX_EDITOR_DISABLE))
			return null;
		
		if(latexText != null){
			JEditorPane textEditor = new JEditorPane();
			textEditor.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
			final JRestrictedSizeScrollPane scrollPane = new JRestrictedSizeScrollPane(textEditor);
			scrollPane.setMinimumSize(new Dimension(0, 60));
			final EditNodeDialog editNodeDialog = new LatexEditor(node, latexText, firstKeyEvent, editControl, false, textEditor);
			editNodeDialog.setTitle(TextUtils.getText("latex_editor"));
			textEditor.setContentType("text/latex");

			final String fontName = ResourceController.getResourceController().getProperty(LATEX_EDITOR_FONT);
			final int fontSize = ResourceController.getResourceController().getIntProperty(LATEX_EDITOR_FONT_SIZE);
			textEditor.setFont(new Font(fontName, Font.PLAIN, fontSize));

			return editNodeDialog;
		}
		return null;
	}
}
