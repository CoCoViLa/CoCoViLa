package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

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
    private static final JLabel lblNudgeStep = new JLabel( "Nudge step:" );
    private static final JLabel lblSyntaxColor = new JLabel( "Syntax Highlighting:" );
    private static final JLabel lblDefaultZoom = new JLabel( "Default Zoom:" );

    // Text Fields.
    private static final JTextField tfGenFilesDir = new JTextField( 40 );
    private static final JTextField tfCompClasspath = new JTextField( 40 );

    // Checkboxes.
    private static final JCheckBox chbDebugInfo = new JCheckBox();
    private static final JCheckBox chbAntiAlias = new JCheckBox();
    private static final JCheckBox chbShowGrid = new JCheckBox();
    private static final JCheckBox chbSyntaxColor = new JCheckBox();

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
        super( parent, "Settings" );
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

        TitledBorder mainBorder = BorderFactory.createTitledBorder( "Program settings" );

        // See documentation at the "docs" folder for panel layout.
        JPanel pnlMain = new JPanel(); // Dialog main panel holding all other
                                        // panels.
        JPanel pnlSettings = new JPanel(); // Panel for labels and fields.
        JPanel pnlLabels = new JPanel(); // Panel for field labels.
        JPanel pnlFields = new JPanel(); // Panel for fields.
        JPanel pnlBttns = new JPanel(); // Panel for buttons.

        pnlMain.setBorder( mainBorder );

        // Set field labels' panel size.
        // pnlLabels.setPreferredSize(new Dimension(100, 180));
        pnlLabels.setMaximumSize( pnlLabels.getPreferredSize() );
        pnlLabels.setMinimumSize( pnlLabels.getPreferredSize() );

        // Set field labels on their panel.
        pnlLabels.setLayout( new GridLayout( 12, 1 ) );
        pnlLabels.add( lblGenFilesDir );
        pnlLabels.add( lblCompClasspath );
        pnlLabels.add( lblDebugInfo );
        pnlLabels.add( lblAntiAlias );
        pnlLabels.add( lblShowGrid );
        pnlLabels.add( lblGridStep );
        pnlLabels.add( lblNudgeStep );
        pnlLabels.add( lblSyntaxColor );
        pnlLabels.add( lblDefaultZoom );
        pnlLabels.add( new JLabel( "Unterminated threads:" ) );

        // Set fields' panel size.
        // pnlFields.setPreferredSize(new Dimension(200, 180));
        pnlFields.setMaximumSize( pnlFields.getPreferredSize() );
        pnlFields.setMinimumSize( pnlFields.getPreferredSize() );

        spinnerGridStep.setPreferredSize( new Dimension( 40, 18 ) );
        spinnerGridStep.setMaximumSize( spinnerGridStep.getPreferredSize() );
        spinnerGridStep.setBorder( BorderFactory.createEtchedBorder() );

        spinnerNudgeStep.setPreferredSize( new Dimension( 48, 18 ) );
        spinnerNudgeStep.setMaximumSize( spinnerNudgeStep.getPreferredSize() );
        spinnerNudgeStep.setBorder( BorderFactory.createEtchedBorder() );

        FlowLayout fl = new FlowLayout();
        fl.setAlignment( FlowLayout.LEFT );

        JPanel pnlGridSpinner = new JPanel();
        pnlGridSpinner.setLayout( fl );
        pnlGridSpinner.add( spinnerGridStep );

        JPanel pnlNudgeSpinner = new JPanel();
        pnlNudgeSpinner.setLayout( fl );
        pnlNudgeSpinner.add( spinnerNudgeStep );

        JPanel pnlDefaultZoom = new JPanel();
        pnlDefaultZoom.setLayout( fl );
        pnlDefaultZoom.add( cbDfltZoom );

        // Set fields to their panel.
        pnlFields.setLayout( new GridLayout( 12, 1 ) );
        pnlFields.add( tfGenFilesDir );
        pnlFields.add( tfCompClasspath );
        pnlFields.add( chbDebugInfo );
        pnlFields.add( chbAntiAlias );
        pnlFields.add( chbShowGrid );
        pnlFields.add( pnlGridSpinner );
        pnlFields.add( pnlNudgeSpinner );
        pnlFields.add( chbSyntaxColor );
        pnlFields.add( pnlDefaultZoom );
        JButton threads = new JButton( "View" );

        JPanel pan = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        pan.add( threads );
        pnlFields.add( pan );
        threads.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                closeDialog();
                RunningThreadKillerDialog.getInstance();
            }
        } );

        // Set settings panel size.
        // pnlSettings.setPreferredSize(new Dimension(300, 180));
        pnlSettings.setMaximumSize( pnlSettings.getPreferredSize() );
        pnlSettings.setMinimumSize( pnlSettings.getPreferredSize() );

        // Add labels and fields to the main panel.
        pnlSettings.setLayout( new BorderLayout() );
        pnlSettings.add( pnlLabels, BorderLayout.WEST );
        pnlSettings.add( pnlFields, BorderLayout.CENTER );

        // Add buttons.
        pnlBttns.add( bttnSave );
        pnlBttns.add( bttnCancel );

        // Add all panels to the main panel.
        pnlMain.setLayout( new BorderLayout() );
        pnlMain.add( pnlSettings, BorderLayout.CENTER );
        pnlMain.add( pnlBttns, BorderLayout.SOUTH );

        getContentPane().add( pnlMain );

        // setSize(new Dimension(500, 300));
        setResizable( false );
        setModal( true );
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
        spinnerGridStep.getModel().setValue( RuntimeProperties.getGridStep() );
        spinnerNudgeStep.getModel().setValue( RuntimeProperties.getNudgeStep() );

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

        chbSyntaxColor.setSelected( RuntimeProperties.isSyntaxHighlightingOn() );
    } // initializeSettings

    /**
     * Store settings.
     */
    private void saveSettings() {

        RuntimeProperties.setGenFileDir( tfGenFilesDir.getText() );
        RuntimeProperties.setCompilationClasspath( tfCompClasspath.getText() );
        RuntimeProperties.setDebugInfo( chbDebugInfo.isSelected() ? 1 : 0 );
        RuntimeProperties.setAntialiasingOn( chbAntiAlias.isSelected() );
        RuntimeProperties.setShowGrid( chbShowGrid.isSelected() );
        RuntimeProperties.setGridStep( (Integer) spinnerGridStep.getModel().getValue() );
        RuntimeProperties.setNudgeStep( (Integer) spinnerNudgeStep.getModel().getValue() );
        RuntimeProperties.setZoomFactor( Palette.ZOOM_LEVELS[ cbDfltZoom.getSelectedIndex() ] );
        RuntimeProperties.setSyntaxHighlightingOn( chbSyntaxColor.isSelected() );
        RuntimeProperties.save();

        closeDialog();
    } // saveSettings

    /**
     * Close the dialog window.
     */
    private void closeDialog() {
        dispose();
    }
}
