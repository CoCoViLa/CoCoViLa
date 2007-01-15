package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.iconeditor.Spinner;

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
 */
public class OptionsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	// Labels.
	private static final JLabel lblGenFilesDir = new JLabel("Generated files:");
	private static final JLabel lblCompClasspath = new JLabel("Compilation classpath:");
	private static final JLabel lblPaletteFile = new JLabel("Palette file:");
	private static final JLabel lblDebugInfo = new JLabel("Debug info:");
	private static final JLabel lblAntiAlias = new JLabel("Anti aliasing:");
	private static final JLabel lblShowGrid = new JLabel("Show grid:");
	private static final JLabel lblGridStep = new JLabel("Grid step:");
	private static final JLabel lblNudgeStep = new JLabel("Nudge step:");
	private static final JLabel lblSyntaxColor = new JLabel("Syntax Highlighting:");
	private static final JLabel lblDefaultZoom = new JLabel("Default Zoom:");

	// Text Fields.
	private static final JTextField tfGenFilesDir = new JTextField(40);
	private static final JTextField tfCompClasspath = new JTextField(40);
	private static final JTextField tfPaletteFile = new JTextField(40);

	// Checkboxes.
	private static final JCheckBox chbDebugInfo = new JCheckBox();
	private static final JCheckBox chbAntiAlias = new JCheckBox();
	private static final JCheckBox chbShowGrid = new JCheckBox();
	private static final JCheckBox chbSyntaxColor = new JCheckBox();

	// Spinners.
	Spinner spinnerGridStep = new Spinner(1, 100, 1, 1);
	Spinner spinnerNudgeStep = new Spinner(1, 100, 1, 1);

	// Comboboxes.
	public static JComboBox cbDfltZoom;

	// Buttons
	private static final JButton bttnSave = new JButton("Save");
	private static final JButton bttnCancel = new JButton("Cancel");

	/**
	 * Class constructor.
	 */
	public OptionsDialog( JFrame parent ) {
		super( parent, "Settings");
		initialize();
	}

	/**
	 * Initialize dialog. Set layout, call methods
	 * for retrieving application parameters from the application.properties file.
	 */
	private void initialize() {

		if (cbDfltZoom == null)
			cbDfltZoom = Palette.getZoomComboBox(Palette.getDefaultZoom());

		setLocationByPlatform( true );

		TitledBorder mainBorder = BorderFactory.createTitledBorder("Program settings");

		// See documentation at the "docs" folder for panel layout.
		JPanel pnlMain = new JPanel(); // Dialog main panel holding all other panels.
		JPanel pnlSettings = new JPanel(); // Panel for labels and fields.
		JPanel pnlLabels = new JPanel(); // Panel for field labels.
		JPanel pnlFields = new JPanel(); // Panel for fields.
		JPanel pnlBttns = new JPanel(); // Panel for buttons.

		pnlMain.setBorder(mainBorder);

		// Set field labels' panel size.
		//pnlLabels.setPreferredSize(new Dimension(100, 180));
		pnlLabels.setMaximumSize(pnlLabels.getPreferredSize());
		pnlLabels.setMinimumSize(pnlLabels.getPreferredSize());

		// Set field labels on their panel.
		pnlLabels.setLayout(new GridLayout(12, 1));
		pnlLabels.add(lblGenFilesDir);
		pnlLabels.add(lblCompClasspath);
		pnlLabels.add(lblPaletteFile);
		pnlLabels.add(lblDebugInfo);
		pnlLabels.add(lblAntiAlias);
		pnlLabels.add(lblShowGrid);
		pnlLabels.add(lblGridStep);
		pnlLabels.add(lblNudgeStep);
		pnlLabels.add(lblSyntaxColor);
		pnlLabels.add(lblDefaultZoom);
		pnlLabels.add( new JLabel( "Unterminated threads:") );

		// Set fields' panel size.
		//pnlFields.setPreferredSize(new Dimension(200, 180));
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

		JPanel pnlDefaultZoom = new JPanel();
		pnlDefaultZoom.setLayout(fl);
		pnlDefaultZoom.add(cbDfltZoom);

		// Set fields to their panel.
		pnlFields.setLayout(new GridLayout(12, 1));
		pnlFields.add(tfGenFilesDir);
		pnlFields.add(tfCompClasspath);
		pnlFields.add(tfPaletteFile);
		pnlFields.add(chbDebugInfo);
		pnlFields.add(chbAntiAlias);
		pnlFields.add(chbShowGrid);
		pnlFields.add(pnlGridSpinner);
		pnlFields.add(pnlNudgeSpinner);
		pnlFields.add(chbSyntaxColor);
		pnlFields.add(pnlDefaultZoom);
		JButton threads = new JButton( "View" );
		
		JPanel pan = new JPanel(new FlowLayout(FlowLayout.LEFT) );
		pan.add(threads);
		pnlFields.add(pan);
		threads.addActionListener( new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						OptionsDialog.this.dispose();
						RunningThreadKillerDialog.getInstance();
					}
				});
			}});
		
		// Set settings panel size.
		//pnlSettings.setPreferredSize(new Dimension(300, 180));
		pnlSettings.setMaximumSize(pnlSettings.getPreferredSize());
		pnlSettings.setMinimumSize(pnlSettings.getPreferredSize());

		// Add labels and fields to the main panel.
		pnlSettings.setLayout(new BorderLayout());
		pnlSettings.add(pnlLabels, BorderLayout.WEST);
		pnlSettings.add(pnlFields, BorderLayout.CENTER);

		// Add buttons.
		pnlBttns.add(bttnSave);
		pnlBttns.add(bttnCancel);

		// Add all panels to the main panel.
		pnlMain.setLayout(new BorderLayout());
		pnlMain.add(pnlSettings, BorderLayout.CENTER);
		pnlMain.add(pnlBttns, BorderLayout.SOUTH);

		getContentPane().add(pnlMain);

		//setSize(new Dimension(500, 300));
		setResizable(false);
		setModal(true);
		pack();
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

	} // End of initialize();

	/**
	 * Initialize dialog with stored settings from the properties file.
	 */
	private void initializeSettings() {
		String sGenFilesDir = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GENERATED_FILES_DIR);
		String sCompClpath = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.COMPILATION_CLASSPATH);
		String sPaletteFile = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.PALETTE_FILE);
		String sDfltLayout = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEFAULT_LAYOUT);
		int iDebugOutput = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEBUG_INFO));
		int iAntiAliasing = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.ANTI_ALIASING));
		int iShowGrid = Integer.parseInt(PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID));

		String sGridStep = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GRID_STEP);
		String sNudgeStep = PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.NUDGE_STEP);

		tfGenFilesDir.setText(sGenFilesDir);
		tfCompClasspath.setText(sCompClpath);
		tfPaletteFile.setText(sPaletteFile);
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
		
		chbSyntaxColor.setSelected( RuntimeProperties.isSyntaxHighlightingOn );
	} // initializeSettings

	/**
	 * Store settings.
	 */
	private void saveSettings() {
		String sGenFilesDir = tfGenFilesDir.getText();
		String sCompClpath = tfCompClasspath.getText();
		String sPaletteFile = tfPaletteFile.getText();
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
				PropertyBox.COMPILATION_CLASSPATH, sCompClpath );
		PropertyBox.setProperty(PropertyBox.APP_PROPS_FILE_NAME,
			PropertyBox.PALETTE_FILE, sPaletteFile);
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
		PropertyBox.setProperty(PropertyBox.ZOOM_LEVEL,
			Float.toString(Palette.ZOOM_LEVELS[cbDfltZoom.getSelectedIndex()]));

		RuntimeProperties.isSyntaxHighlightingOn = chbSyntaxColor.isSelected();
		RuntimeProperties.isAntialiasingOn = chbAntiAlias.isSelected();
		RuntimeProperties.gridStep = Integer.parseInt(spinnerGridStep.getModel().getValue().toString());
		RuntimeProperties.debugInfo = Integer.parseInt(sDebugOutput);
		RuntimeProperties.nudgeStep = Integer.parseInt(spinnerNudgeStep.getModel().getValue().toString());
		RuntimeProperties.genFileDir = sGenFilesDir;
		RuntimeProperties.compilationClasspath = sCompClpath;
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
		OptionsDialog o = new OptionsDialog( null );

		o.setVisible(true);
	}

}
