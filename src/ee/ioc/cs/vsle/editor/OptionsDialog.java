package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.util.PropertyBox;
import ee.ioc.cs.vsle.iconeditor.Spinner;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

import javax.swing.JLabel;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
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
          private static final JLabel lblShowGrid = new JLabel("Show Grid:");
          private static final JLabel lblGridStep = new JLabel("Grid Step:");

        // Text Fields.
        private static final JTextField tfGenFilesDir = new JTextField();
        private static final JTextField tfPaletteFile = new JTextField();

        // Checkboxes.
        private static final JCheckBox chbDebugInfo = new JCheckBox();
        private static final JCheckBox chbAntiAlias = new JCheckBox();
          private static final JCheckBox chbShowGrid = new JCheckBox();

          // Spinners.
          Spinner spinnerGridStep = new Spinner(1, 100, 1);

        // Comboboxes.
        private static final JComboBox cbDfltLayout = new JComboBox();

        // Buttons
        private static final JButton bttnSave = new JButton("Save");
        private static final JButton bttnCancel = new JButton("Cancel");

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

                TitledBorder mainBorder = BorderFactory.createTitledBorder(
                  "Program settings");

                mainBorder.setTitleFont(f);

                // See documentation at the "docs" folder for panel layout.
                JPanel pnlMain = new JPanel(); // Dialog main panel holding all other panels.
                JPanel pnlSettings = new JPanel(); // Panel for labels and fields.
                JPanel pnlLabels = new JPanel(); // Panel for field labels.
                JPanel pnlFields = new JPanel(); // Panel for fields.
                JPanel pnlBttns = new JPanel(); // Panel for buttons.

                pnlMain.setBorder(mainBorder);

                // Set field labels' panel size.
                pnlLabels.setPreferredSize(new Dimension(100, 160));
                pnlLabels.setMaximumSize(pnlLabels.getPreferredSize());
                pnlLabels.setMinimumSize(pnlLabels.getPreferredSize());

                // Set field labels on their panel.
                pnlLabels.setLayout(new GridLayout(7, 0));
                pnlLabels.add(lblGenFilesDir);
                pnlLabels.add(lblPaletteFile);
                pnlLabels.add(lblDfltLayout);
                pnlLabels.add(lblDebugInfo);
                pnlLabels.add(lblAntiAlias);
                    pnlLabels.add(lblShowGrid);
                    pnlLabels.add(lblGridStep);

                // Set fields' panel size.
                pnlFields.setPreferredSize(new Dimension(200, 160));
                pnlFields.setMaximumSize(pnlFields.getPreferredSize());
                pnlFields.setMinimumSize(pnlFields.getPreferredSize());

                    spinnerGridStep.setPreferredSize(new Dimension(40, 18));
                    spinnerGridStep.setMaximumSize(spinnerGridStep.getPreferredSize());
                    spinnerGridStep.setBorder(BorderFactory.createEtchedBorder());

                    JPanel pnlGridSpinner = new JPanel();
                    FlowLayout fl = new FlowLayout();
                    fl.setAlignment(FlowLayout.LEFT);
                    pnlGridSpinner.setLayout(fl);
                    pnlGridSpinner.add(spinnerGridStep);

                // Set fields to their panel.
                pnlFields.setLayout(new GridLayout(7, 0));
                pnlFields.add(tfGenFilesDir);
                pnlFields.add(tfPaletteFile);
                pnlFields.add(cbDfltLayout);
                pnlFields.add(chbDebugInfo);
                pnlFields.add(chbAntiAlias);
                    pnlFields.add(chbShowGrid);
                    pnlFields.add(pnlGridSpinner);

                // Set settings panel size.
                pnlSettings.setPreferredSize(new Dimension(300, 160));
                pnlSettings.setMaximumSize(pnlSettings.getPreferredSize());
                pnlSettings.setMinimumSize(pnlSettings.getPreferredSize());

                // Add labels and fields to the main panel.
                pnlSettings.setLayout(new BorderLayout());
                pnlSettings.add(pnlLabels, BorderLayout.WEST);
                pnlSettings.add(pnlFields, BorderLayout.EAST);

                // Add items to the layout choices combobox.
                if (cbDfltLayout.getItemCount() == 0) {
                        cbDfltLayout.addItem(Look.LOOK_3D);
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

                setSize(new Dimension(320, 240));
                setResizable(false);
                setModal(true);

                initializeSettings();

                /*
                 * ADD ACTION LISTENERS AS ANONYMOUS CLASSES.
                 */

                bttnSave.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                               if(isVisible()) saveSettings();
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
                 * Changes layout.
                 */
                cbDfltLayout.addItemListener(
                  new ItemListener() {
                        public void itemStateChanged(final ItemEvent event) {
                                if (event.getSource() == cbDfltLayout
                                  && event.getStateChange() == ItemEvent.SELECTED) {
                                        Look.changeLayout(cbDfltLayout.getSelectedItem().toString());
                                }
                        }
                });

        } // End of initialize();

        /**
         * Initialize dialog with stored settings from the properties file.
         */
        private void initializeSettings() {
                String sGenFilesDir = PropertyBox.getProperty(
                  PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.GENERATED_FILES_DIR);
                String sPaletteFile = PropertyBox.getProperty(
                  PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.PALETTE_FILE);
                String sDfltLayout = PropertyBox.getProperty(
                  PropertyBox.APP_PROPS_FILE_NAME, PropertyBox.DEFAULT_LAYOUT);
                int iDebugOutput = Integer.parseInt(
                  PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
                  PropertyBox.DEBUG_INFO));

                int iAntiAliasing = Integer.parseInt(
                  PropertyBox.getProperty(PropertyBox.APP_PROPS_FILE_NAME,
                  PropertyBox.ANTI_ALIASING));

                    int iShowGrid = Integer.parseInt(PropertyBox.getProperty(PropertyBox.
                APP_PROPS_FILE_NAME, PropertyBox.SHOW_GRID));

                   String sGridStep = PropertyBox.getProperty(PropertyBox.
                APP_PROPS_FILE_NAME, PropertyBox.GRID_STEP);

                tfGenFilesDir.setText(sGenFilesDir);
                tfPaletteFile.setText(sPaletteFile);
                cbDfltLayout.setSelectedItem(sDfltLayout);
                    spinnerGridStep.getModel().setValue(sGridStep);

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
        }

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

                RuntimeProperties.isAntialiasingOn = chbAntiAlias.isSelected();
				 RuntimeProperties.gridStep = Integer.parseInt(spinnerGridStep.getModel().getValue().toString());
                closeDialog();
        }

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
