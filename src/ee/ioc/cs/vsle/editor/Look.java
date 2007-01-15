package ee.ioc.cs.vsle.editor;

import java.awt.Component;
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

	public static void changeLookAndFeelForComponent(final Component component,
			final String lnf, final boolean saveConfig) {

		// make sure LnF is changed in AWT thread
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {

					UIManager.setLookAndFeel(lnf);

					SwingUtilities.updateComponentTreeUI(component);

					if (saveConfig) {
						PropertyBox.setProperty(
								PropertyBox.APP_PROPS_FILE_NAME,
								PropertyBox.DEFAULT_LAYOUT, lnf);
					}

				} catch (Exception e) {
					db.p("Unable to change Look And Feel: " + lnf);
				}
			}
		});
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

	public void createMenuItems(JMenu menu, final Component root) {

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
					changeLookAndFeelForComponent( root, lnfs.getClassName(),
							true );
				}
			});

			menu.add(menuItem);
		}

		menu.setToolTipText("Set default Look And Feel");
	}

}
