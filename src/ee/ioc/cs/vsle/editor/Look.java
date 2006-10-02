package ee.ioc.cs.vsle.editor;

import java.awt.Component;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;

/**
 * Look and feel handler.
 *
 * User: AASMAAUL
 * Date: 13.01.2004
 * Time: 13:01:50
 */
public class Look {

	private static Component component;

	// LAYOUT
	public static final String LOOK_CUSTOM = "Custom";
	public static final String LOOK_METAL = "Metal";
	public static final String LOOK_MOTIF = "Motif";
	public static final String LOOK_WINDOWS = "Windows";

	public void setGUI(Component e) {
		Look.component = component;
	}

	/**
	 * Change layout immediately as the layout selection changes.
	 * @param selectedLayout - application layout selected from the menu.
	 */
	public static void changeLayout(String selectedLayout) {
		if (selectedLayout.equalsIgnoreCase(LOOK_WINDOWS)) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} catch (Exception uie) {
			}
		} else if (selectedLayout.equalsIgnoreCase(LOOK_METAL)) {
			try {
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			} catch (Exception uie) {
			}
		} else if (selectedLayout.equalsIgnoreCase(LOOK_MOTIF)) {
			try {
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
			} catch (Exception uie) {
			}
		} else if (selectedLayout.equalsIgnoreCase(LOOK_CUSTOM)) {
			try {
			  if(RuntimeProperties.customLayout!=null && RuntimeProperties.customLayout.trim().length()>0) {
				UIManager.setLookAndFeel(RuntimeProperties.customLayout);
			  } else {
				changeLayout(LOOK_METAL);
              }
			} catch (Exception uie) {
			}
		}
		if (component != null) {
			SwingUtilities.updateComponentTreeUI(component);
		}
	}

}
