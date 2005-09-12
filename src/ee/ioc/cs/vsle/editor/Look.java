package ee.ioc.cs.vsle.editor;

import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import ee.ioc.cs.vsle.iconeditor.IconEditor;

/**
 * Look and feel handler.
 *
 * Created by IntelliJ IDEA.
 * User: AASMAAUL
 * Date: 13.01.2004
 * Time: 13:01:50
 * To change this template use Options | File Templates.
 */
public class Look {

	private static Editor ed;
	private static IconEditor ied;

	// LAYOUT
	public static final String LOOK_CUSTOM = "Custom";
	public static final String LOOK_METAL = "Metal";
	public static final String LOOK_MOTIF = "Motif";
	public static final String LOOK_WINDOWS = "Windows";

	public void setGUI(Editor e) {
		ed = e;
	}

	public void setGUI(IconEditor e) {
		ied = e;
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
		if (ed != null) {
			SwingUtilities.updateComponentTreeUI(ed);
		}
		if (ied != null) {
			SwingUtilities.updateComponentTreeUI(ied);
		}
	}

}
