package org.freeplane.plugin.latex;

import java.net.URL;
import java.util.Hashtable;

import org.freeplane.features.format.FormatController;
import org.freeplane.features.mode.Controller;
import org.freeplane.features.mode.ModeController;
import org.freeplane.features.mode.mindmapmode.MModeController;
import org.freeplane.features.text.TextController;
import org.freeplane.features.text.mindmapmode.ConditionalContentTransformer;
import org.freeplane.main.mindmapmode.stylemode.SModeController;
import org.freeplane.main.osgi.IModeControllerExtensionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String PREFERENCES_RESOURCE = "preferences.xml";
	static final String TOGGLE_PARSE_LATEX = "parse_latex";

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(final BundleContext context) throws Exception {
		registerMindMapModeExtension(context);
	}

	private void registerMindMapModeExtension(final BundleContext context) {
		final Hashtable<String, String[]> props = new Hashtable<String, String[]>();
		props.put("mode", new String[] { MModeController.MODENAME, SModeController.MODENAME });
		context.registerService(IModeControllerExtensionProvider.class.getName(),
		    new IModeControllerExtensionProvider() {
			    public void installExtension(final ModeController modeController) {
					//LattexNodeHook -> Menu insert
					final LatexNodeHook nodeHook = new LatexNodeHook();
					modeController.getExtension(TextController.class).addTextTransformer(//
							new ConditionalContentTransformer(new LatexRenderer(), Activator.TOGGLE_PARSE_LATEX));
					modeController.getController().getExtension(FormatController.class).addPatternFormat(new LatexFormat());
					modeController.getController().getExtension(FormatController.class).addPatternFormat(new UnparsedLatexFormat());
					if (modeController.getModeName().equals("MindMap")) {
						modeController.addAction(new InsertLatexAction(nodeHook));
						modeController.addAction(new EditLatexAction(nodeHook));
						modeController.addAction(new DeleteLatexAction(nodeHook));
						addPreferencesToOptionPanel();
					}
			    }

				private void addPreferencesToOptionPanel() {
					final URL preferences = this.getClass().getResource(PREFERENCES_RESOURCE);
					if (preferences == null)
						throw new RuntimeException("cannot open preferences");
					final Controller controller = Controller.getCurrentController();
					MModeController modeController = (MModeController) controller.getModeController();
					modeController.getOptionPanelBuilder().load(preferences);
				}
		    }, props);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext context) throws Exception {
	}
}
