package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.iconeditor.Spinner;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 10.1.2004
 * Time: 14:09:17
 * To change this template use Options | File Templates.
 */
public class OptionsDialog extends JDialog {

	// Labels.
	private static final JLabel lblGenFilesDir = new JLabel("Generated files:");
	private static final JLabel lblPaletteFile = new JLabel("Palette file:");
	private static final JLabel lblDebugInfo = new JLabel("Debug info:");
	private static final JLabel lblDfltLayout = new JLabel("Default layout:");
	private static final JLabel lblAntiAlias = new JLabel("Anti aliasing:");
	private static final JLabel lblShowGrid = new JLabel("Show grid:");
	private static final JLabel lblGridStep = new JLabel("Grid step:");
	private static final JLabel lblNudgeStep = new JLabel("Nudge step:");

	// Text Fields.
	private static final JTextField tfGenFilesDir = new JTextField();
	private static final JTextField tfPaletteFile = new JTextField();

	// Checkboxes.
	private static final JCheckBox chbDebugInfo = new JCheckBox();
	private static final JCheckBox chbAntiAlias = new JCheckBox();
	private static final JCheckBox chbShowGrid = new JCheckBox();

	// Spinners.
	Spinner spinnerGridStep = new Spinner(1, 100, 1, 1);
	Spinner spinnerNudgeStep = new Spinner(1, 100, 1, 1);

	// Comboboxes.
	public static final JComboBox cbDfltLayout = new JComboBox();

	// Buttons
	private static final JButton bttnSave = new JButton("Save");
	private static final JButton bttnCancel = new JButton("Cancel");
	private static final JButton bttnCustomLook = new JButton("...");

	/**
	 * Class constructor.
	 */
	public OptionsDialog() {
		initialize();
	}

	/**
	 * Initialize dialog. Set layout, call methods
	 * for retrieving application parameters from the application.properties file.
	 */
	private void initialize() {

		// Specify smaller font for title and buttons.
		Font f = new Font("Arial", Font.BOLD, 11);

		TitledBorder mainBorder = BorderFactory.createTitledBorder("Program settings");
		mainBorder.setTitleFont(f);

		// See documentation at the "docs" folder for panel layout.
		JPanel pnlMain = new JPanel(); // Dialog main panel holding all other panels.
		JPanel pnlSettings = new JPanel(); // Panel for labels and fields.
		JPanel pnlLabels = new JPanel(); // Panel for field labels.
		JPanel pnlFields = new JPanel(); // Panel for fields.
		JPanel pnlBttns = new JPanel(); // Panel for buttons.
		JPanel pnlLayout = new JPanel(); // Panel for layout combo box and custom layout dialog button.

		pnlMain.setBorder(mainBorder);

		pnlLayout.setLayout(new BorderLayout());
		pnlLayout.add(cbDfltLayout, BorderLayout.CENTER);
		pnlLayout.add(bttnCustomLook, BorderLayout.EAST);

		// Set field labels' panel size.
		pnlLabels.setPreferredSize(new Dimension(100, 180));
		pnlLabels.setMaximumSize(pnlLabels.getPreferredSize());
		pnlLabels.setMinimumSize(pnlLabels.getPreferredSize());

		// Set field labels on their panel.
		pnlLabels.setLayout(new GridLayout(8, 0));
		pnlLabels.add(lblGenFilesDir);
		pnlLabels.add(lblPaletteFile);
		pnlLabels.add(lblDfltLayout);
		pnlLabels.add(lblDebugInfo);
		pnlLabels.add(lblAntiAlias);
		pnlLabels.add(lblShowGrid);
		pnlLabels.add(lblGridStep);
		pnlLabels.add(lblNudgeStep);

		// Set fields' panel size.
		pnlFields.setPreferredSize(new Dimension(200, 180));
		pnlFields.setMaximumSize(pnlFields.getPreferredSize());
		pnlFields.setMinimumSize(pnlFields.getPreferredSize());

		spinnerGridStep.setPreferredSize(new Dimension(40, 18));
		spinnerGridStep.setMaximumSize(spinnerGridStep.getPreferredSize());
		spinnerGridStep.setBorder(BorderFactory.createEtchedBorder());

		spinnerNudgeStep.setPreferredSize(new Dimension(48, 18));
		spinnerNudgeStep.setMaximumSize(spinnerNudgeStep.getPreferredSize());
		spinnerNudgeStep.setBorder(BorderFactory.createEtchedBorder());

		FlowLayout fl = new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);

		JPanel pnlGridSpinner = new JPanel();
		pnlGridSpinner.setLayout(fl);
		pnlGridSpinner.add(spinnerGridStep);

		JPanel pnlNudgeSpinner = new JPanel();
		pnlNudgeSpinner.setLayout(fl);
		pnlNudgeSpinner.add(spinnerNudgeStep);

		// Set fields to their panel.
		pnlFields.setLayout(new GridLayout(8, 0));
		pnlFields.add(tfGenFilesDir);
		pnlFields.add(tfPaletteFile);
		pnlFields.add(pnlLayout);
		pnlFields.add(chbDebugInfo);
		pnlFields.add(chbAntiAlias);
		pnlFields.add(chbShowGrid);
		pnlFields.add(pnlGridSpinner);
		pnlFields.add(pnlNudgeSpinner);

		// Set settings panel size.
		pnlSettings.setPreferredSize(new Dimension(300, 180));
		pnlSettings.setMaximumSize(pnlSettings.getPreferredSize());
		pnlSettings.setMinimumSize(pnlSettings.getPreferredSize());

		// Add labels and fields to the main panel.
		pnlSettings.setLayout(new BorderLayout());
		pnlSettings.add(pnlLabels, BorderLayout.WEST);
		pnlSettings.add(pnlFields, BorderLayout.EAST);

		// Add items to the layout choices combobox.
		if (cbDfltLayout.getItemCount() == 0) {
			cbDfltLayout.addItem(Look.LOOK_CUSTOM);
			cbDfltLayout.addItem(Look.LOOK_METAL);
			cbDfltLayout.addItem(Look.LOOK_MOTIF);
			cbDfltLayout.addItem(Look.LOOK_WINDOWS);
		}

		// Specify smaller font for buttons.
		bttnSave.setFont(f);
		bttnCancel.setFont(f);

		// Add buttons.
		pnlBttns.add(bttnSave);
		pnlBttns.add(bttnCancel);

		// Add all panels to the main panel.
		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlSettings, BorderLayout.NORTH);
		pnlMain.add(pnlBttns, BorderLayout.SOUTH);

		getContentPane().add(pnlMain);

		setSize(new Dimension(320, 280));
		setResizable(false);
		setModal(true);

		initializeSettings();

		/*
		 * ADD ACTION LISTENERS AS ANONYMOUS CLASSES.
		 */

		bttnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (isVisible()) saveSettings();
			}
		});

		/*
		 * Closes the dialog window.
		 */
		bttnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				closeDialog();
			}
		});

		/*
		 * Opens the custom look definition dialog.
		 */
		bttnCustomLook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openCustomLookDialog();
			}
		});

		/*
		 * Changes layout.
		 */
		cbDfltLayout.addItemListener(
			new ItemListener() {
				public void itemStateChanged(final ItemEvent event) {
					if (event.getSource() == cbDfltLayout
						&& event.getStateChange() == ItemEvent.SELECTED) {
						if (cbDfltLayout.getSelectedItem().toString() != null &&
							cbDfltLayout.getSelectedItem().toString().equalsIgnoreCase(Look.LOOK_CUSTOM) &&
							RuntimeProperties.customLayout == null) {
							openCustomLookDialog();
						} else {
							Look.changeLayout(cbDfltLayout.getSelectedItem().toString());
						}
					}
				}
			});

	} // End of initialize();

	/**
	 * Opens a new dialog where the user can specify a new path of the custom
	 * application look and feel module.
	 */
	private void openCustomLookDialog() {
		CustomLookDialog cd = new CustomLookDialog();
		cd.setVisible(true);
	} // openCustomLookDialog

	/**
	 * Initialize dialog with stored settings from the properties file.
	 */
	private void initializeSettings() {
		String sGenFilesDir = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GENERATED_FILES_DIR);
		String sPaletteFile = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.PALETTE_FILE);
		String sDfltLayout = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEFAULT_LAYOUT);
		int iDebugOutput = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEBUG_INFO));
		int iAntiAliasing = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.ANTI_ALIASING));
		int iShowGrid = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID));

		String sGridStep = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GRID_STEP);
		String sNudgeStep = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.NUDGE_STEP);

		tfGenFilesDir.setText(sGenFilesDir);
		tfPaletteFile.setText(sPaletteFile);
		cbDfltLayout.setSelectedItem(sDfltLayout);
		spinnerGridStep.getModel().setValue(sGridStep);
		spinnerNudgeStep.getModel().setValue(sNudgeStep);

		// Initialize debug output checkbox.
		if (iDebugOutput < 1) {
			chbDebugInfo.setSelected(false);
		} else {
			chbDebugInfo.setSelected(true);
		}

		// Initialize antialiasing checkbox.
		if (iAntiAliasing < 1) {
			chbAntiAlias.setSelected(false);
		} else {
			chbAntiAlias.setSelected(true);
		}

		// Initialize show grid checkbox.
		if (iShowGrid < 1) {
			chbShowGrid.setSelected(false);
		} else {
			chbShowGrid.setSelected(true);
		}
	} // initializeSettings

	/**
	 * Store settings.
	 */
	private void saveSettings() {
		String sGenFilesDir = tfGenFilesDir.getText();
		String sPaletteFile = tfPaletteFile.getText();
		String sDfltLayout = String.valueOf(cbDfltLayout.getSelectedItem());
		String sDebugOutput = "0";
		String sAntiAliasing = "0";
		String sShowGrid = "0";

		if (chbDebugInfo.isSelected()) {
			sDebugOutput = "1";
		}
		if (chbAntiAlias.isSelected()) {
			sAntiAliasing = "1";
		}
		if (chbShowGrid.isSelected()) {
			sShowGrid = "1";
		}

		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.GENERATED_FILES_DIR, sGenFilesDir);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.PALETTE_FILE, sPaletteFile);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.DEFAULT_LAYOUT, sDfltLayout);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.DEBUG_INFO, sDebugOutput);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.ANTI_ALIASING, sAntiAliasing);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.SHOW_GRID, sShowGrid);
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.GRID_STEP, spinnerGridStep.getModel().getValue().toString());
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.NUDGE_STEP, spinnerNudgeStep.getModel().getValue().toString());

		RuntimeProperties.isAntialiasingOn = chbAntiAlias.isSelected();
		RuntimeProperties.gridStep = Integer.parseInt(spinnerGridStep.getModel().getValue().toString());
		RuntimeProperties.nudgeStep = Integer.parseInt(spinnerNudgeStep.getModel().getValue().toString());
		RuntimeProperties.genFileDir = sGenFilesDir;
		closeDialog();
	} // saveSettings

	/**
	 * Close the dialog window.
	 */
	private void closeDialog() {
		setVisible(false);
	}

	/**
	 * Main method for class unit-testing.
	 * @param args - command line arguments.
	 */
	public static void main(String[] args) {
		OptionsDialog o = new OptionsDialog();

		o.setVisible(true);
	}

}
