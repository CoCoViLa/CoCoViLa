package ee.ioc.cs.vsle.editor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Application Options dialog
 */
public class OptionsDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Labels.
    private static final JLabel lblGenFilesDir = new JLabel( "Generated files:" );
    private static final JLabel lblCompClasspath = new JLabel( "Compilation classpath:" );
    private static final JLabel lblDebugInfo = new JLabel( "Debug info:" );
    private static final JLabel lblAntiAlias = new JLabel( "Anti aliasing:" );
    private static final JLabel lblShowGrid = new JLabel( "Show grid:" );
    private static final JLabel lblGridStep = new JLabel( "Grid step:" );
    private static final JLabel lblSnapToGrid = new JLabel( "Snap to grid:" );
    private static final JLabel lblNudgeStep = new JLabel( "Nudge step:" );
    private static final JLabel lblSyntaxColor = new JLabel( "Syntax Highlighting:" );
    private static final JLabel lblDefaultZoom = new JLabel( "Default Zoom:" );
    private static final JLabel lblDumpGen = new JLabel("Dump generated files:");
    private static final JLabel lblDefaultEditor = new JLabel("Editor:");

    // Text Fields.
    private static final JTextField tfGenFilesDir = new JTextField( 40 );
    private static final JTextField tfCompClasspath = new JTextField( 40 );
    private static final JTextField tfDefaultEditor = new JTextField(40);

    // Checkboxes.
    private static final JCheckBox chbDebugInfo = new JCheckBox();
    private static final JCheckBox chbAntiAlias = new JCheckBox();
    private static final JCheckBox chbShowGrid = new JCheckBox();
    private static final JCheckBox chbSnapToGrid = new JCheckBox();
    private static final JCheckBox chbSyntaxColor = new JCheckBox();
    private static final JCheckBox chbDumpGen = new JCheckBox();

    // Spinners.
    private JSpinner spinnerGridStep = new JSpinner( new SpinnerNumberModel( 1, 1, 100, 1 ) );
    private JSpinner spinnerNudgeStep = new JSpinner( new SpinnerNumberModel( 1, 1, 100, 1 ) );

    // Comboboxes.
    public static JComboBox cbDfltZoom;

    // Buttons
    private static final JButton bttnSave = new JButton( "Save" );
    private static final JButton bttnCancel = new JButton( "Cancel" );

    /**
     * Class constructor.
     */
    public OptionsDialog( JFrame parent ) {
        super( parent, "CoCoViLa - Settings" );
        initialize();
    }

    /**
     * Initialize dialog. Set layout, call methods for retrieving application
     * parameters from the application.properties file.
     */
    private void initialize() {

        if ( cbDfltZoom == null )
            cbDfltZoom = Palette.getZoomComboBox( RuntimeProperties.getZoomFactor() );

        setLocationByPlatform( true );
        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        setMinimumSize(RuntimeProperties.WINDOW_MIN_DIM);
        setModal(true);

        
        // Settings panel layout

        JPanel pnlSettings = new JPanel(new GridBagLayout());

        GridBagConstraints cSettings = new GridBagConstraints();
        GridBagConstraints cLabels = new GridBagConstraints();

        cLabels.anchor = GridBagConstraints.LINE_START;
        cLabels.insets = new Insets(8, 0, 8, 8);

        cSettings.anchor = GridBagConstraints.LINE_START;
        cSettings.gridwidth = GridBagConstraints.REMAINDER;
        cSettings.weightx = 1;

        cSettings.fill = GridBagConstraints.HORIZONTAL; // for txtfields

        // genFilesDir
        pnlSettings.add(lblGenFilesDir, cLabels);
        pnlSettings.add(tfGenFilesDir, cSettings);

        // compClasspath
        pnlSettings.add(lblCompClasspath, cLabels);
        pnlSettings.add(tfCompClasspath, cSettings);

        cSettings.fill = GridBagConstraints.NONE; // do not strech checkboxes

        // dumpGen
        pnlSettings.add(lblDumpGen, cLabels);
        pnlSettings.add(chbDumpGen, cSettings);

        // debugInfo
        pnlSettings.add(lblDebugInfo, cLabels);
        pnlSettings.add(chbDebugInfo, cSettings);

        // antiAlias
        pnlSettings.add(lblAntiAlias, cLabels);
        pnlSettings.add(chbAntiAlias, cSettings);

        // showGrid
        pnlSettings.add(lblShowGrid, cLabels);
        pnlSettings.add(chbShowGrid, cSettings);

        // snapToGrid
        pnlSettings.add(lblSnapToGrid, cLabels);
        pnlSettings.add(chbSnapToGrid, cSettings);
        
        // gridStep
        pnlSettings.add(lblGridStep, cLabels);
        pnlSettings.add(spinnerGridStep, cSettings);

        // nudgeStep
        pnlSettings.add(lblNudgeStep, cLabels);
        pnlSettings.add(spinnerNudgeStep, cSettings);

        // syntaxColor
        pnlSettings.add(lblSyntaxColor, cLabels);
        pnlSettings.add(chbSyntaxColor, cSettings);

        // defaultZoom
        pnlSettings.add(lblDefaultZoom, cLabels);
        pnlSettings.add(cbDfltZoom, cSettings);

        // defaultEditor
        pnlSettings.add(lblDefaultEditor, cLabels);
        pnlSettings.add(tfDefaultEditor, cSettings);

        // Window layout

        // Buttons are added first, this way they are not covered by the
        // settings panel when the window is too small to fit everyting.
        JPanel pnlBttn = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 2, 5, 2);
        pnlBttn.add(bttnSave, c);
        pnlBttn.add(bttnCancel, c);
        add(pnlBttn, BorderLayout.PAGE_END);

        // Wrap the settings gridbag into a JPanel with BorderLayout that
        // respects the insets created by the titled border
        JPanel pnlSettingsTitle = new JPanel(new BorderLayout());
        pnlSettingsTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Program settings"),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        pnlSettingsTitle.add(pnlSettings, BorderLayout.PAGE_START);
        add(pnlSettingsTitle, BorderLayout.PAGE_START);

        pack();
        initializeSettings();

        /*
         * ADD ACTION LISTENERS AS ANONYMOUS CLASSES.
         */

        bttnSave.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                if ( isVisible() )
                    saveSettings();
            }
        } );

        /*
         * Closes the dialog window.
         */
        bttnCancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent evt ) {
                closeDialog();
            }
        } );

    } // End of initialize();

    /**
     * Initialize dialog with stored settings from the properties file.
     */
    private void initializeSettings() {

        tfGenFilesDir.setText( RuntimeProperties.getGenFileDir() );
        tfCompClasspath.setText( RuntimeProperties.getCompilationClasspath() );
        spinnerGridStep.getModel().setValue( Integer.valueOf(RuntimeProperties.getGridStep()) );
        spinnerNudgeStep.getModel().setValue( Integer.valueOf(RuntimeProperties.getNudgeStep()) );

        // Initialize debug output checkbox.
        if ( RuntimeProperties.getDebugInfo() < 1 ) {
            chbDebugInfo.setSelected( false );
        } else {
            chbDebugInfo.setSelected( true );
        }

        // Initialize antialiasing checkbox.
        chbAntiAlias.setSelected( RuntimeProperties.isAntialiasingOn() );

        // Initialize show grid checkbox.
        chbShowGrid.setSelected( RuntimeProperties.isShowGrid() );
        chbSnapToGrid.setSelected( RuntimeProperties.getSnapToGrid() );
        chbSyntaxColor.setSelected( RuntimeProperties.isSyntaxHighlightingOn() );
        chbDumpGen.setSelected(RuntimeProperties.isDumpGenerated());
        tfDefaultEditor.setText(RuntimeProperties.getDefaultEditor());
    } // initializeSettings

    /**
     * Store settings.
     */
    void saveSettings() {

        RuntimeProperties.setGenFileDir( tfGenFilesDir.getText() );
        RuntimeProperties.setCompilationClasspath( tfCompClasspath.getText() );
        RuntimeProperties.setDebugInfo( chbDebugInfo.isSelected() ? 1 : 0 );
        RuntimeProperties.setAntialiasingOn( chbAntiAlias.isSelected() );
        RuntimeProperties.setShowGrid( chbShowGrid.isSelected() );
        RuntimeProperties.setGridStep( ((Integer) spinnerGridStep.getModel().getValue()).intValue() );
        RuntimeProperties.setSnapToGrid( chbSnapToGrid.isSelected() );
        RuntimeProperties.setNudgeStep( ((Integer) spinnerNudgeStep.getModel().getValue()).intValue() );
        RuntimeProperties.setZoomFactor( Palette.ZOOM_LEVELS[ cbDfltZoom.getSelectedIndex() ] );
        RuntimeProperties.setSyntaxHighlightingOn( chbSyntaxColor.isSelected() );
        RuntimeProperties.setDumpGenerated(chbDumpGen.isSelected());
        RuntimeProperties.setDefaultEditor(tfDefaultEditor.getText());
        RuntimeProperties.save();

        closeDialog();
    } // saveSettings

    /**
     * Close the dialog window.
     */
    void closeDialog() {
        dispose();
    }
}
