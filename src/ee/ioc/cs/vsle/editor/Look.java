package ee.ioc.cs.vsle.editor;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.UIManager.*;

import ee.ioc.cs.vsle.util.*;

/**
 * Look and feel handler.
 */
public class Look {

//	static {
//		try {
//			if( Class.forName( "com.incors.plaf.kunststoff.KunststoffLookAndFeel" ) != null ) {
//				UIManager.installLookAndFeel("Kunststoff",
//					"com.incors.plaf.kunststoff.KunststoffLookAndFeel");
//			}
//		} catch (ClassNotFoundException e) {}
//	}

	private static Look s_instance;

	public static Look getInstance() {

		if (s_instance == null) {
			s_instance = new Look();
		}

		return s_instance;
	}

    /**
     * Updates the L'n'F of the application at run-time.
     * @param lnf the class name of the new Look and Feel
     * @param saveConfig true to save the LnF as default
     */
	public static void changeLookAndFeelForApp(final String lnf,
            final boolean saveConfig) {

	    try {
	        UIManager.setLookAndFeel(lnf);

            for (Window window : SystemUtils.getAllWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
                window.pack();
            }

            if (saveConfig)
                PropertyBox.setProperty(PropertyBox.DEFAULT_LAYOUT, lnf);

        } catch (Exception e) {
	        db.p("Unable to change Look And Feel: " + lnf);
	    }
	}



    public void initDefaultLnF() {

		String lnf = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
				PropertyBox.DEFAULT_LAYOUT);

		try {

			UIManager.setLookAndFeel(lnf);

		} catch (Exception e) {
			db.p("Unable to init default Look And Feel: " + lnf);
		}
	}

	public void createMenuItems(JMenu menu) {

		LookAndFeel laf = UIManager.getLookAndFeel();

		String lafName = (laf != null) ? laf.getName() : "";

		ButtonGroup group = new ButtonGroup();

		for (final LookAndFeelInfo lnfs : UIManager.getInstalledLookAndFeels()) {
			
			JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(lnfs.getName());

			group.add(menuItem);

			if ( lafName.equals( lnfs.getName() ) ) {
				menuItem.setSelected(true);
			}

			menuItem.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					changeLookAndFeelForApp(lnfs.getClassName(), true);
				}
			});

			menu.add(menuItem);
		}

		menu.setToolTipText("Set default Look And Feel");
	}

}
