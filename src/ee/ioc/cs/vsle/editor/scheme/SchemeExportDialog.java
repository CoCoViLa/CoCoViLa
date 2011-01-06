package ee.ioc.cs.vsle.editor.scheme;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.util.*;

public class SchemeExportDialog extends JDialog {

    private ISchemeContainer schemeContainer;

    public SchemeExportDialog( ISchemeContainer schemeContainer ) {
        super( Editor.getInstance(), "Export scheme", true );
        assert schemeContainer != null;
        this.schemeContainer = schemeContainer;
        init();
    }

    private void init() {
        JPanel contentPane = new JPanel( new BorderLayout( 5, 5 ) );
        contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        setContentPane( contentPane );

        JPanel buttonPane = new JPanel(
                new FlowLayout( FlowLayout.CENTER, 5, 5 ) );
        buttonPane.add( new JButton( "OK" ) );
        buttonPane.add( new JButton( "Cancel" ) );

        contentPane.add( buttonPane, BorderLayout.SOUTH );
        contentPane.add( createCenterPanel(), BorderLayout.CENTER );

        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        //        pack();
        setSize( 400, 300 );
    }

    private JPanel createCenterPanel() {
        JPanel centerPane = new JPanel();
        centerPane.setLayout( new GridBagLayout() );

        JPanel previewPane = getPreviewPanel();
        previewPane.setOpaque( true );
        previewPane.setBackground( Color.white );
        previewPane.setBorder( BorderFactory.createLoweredBevelBorder() );
        previewPane.setMinimumSize( new Dimension( 170, 130 ) );
        previewPane.setPreferredSize( previewPane.getMinimumSize() );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets( 5, 5, 5, 5 );

        int y = 0;

        //preview
        centerPane.add( previewPane, GuiUtil.buildGridBagConstraints( gbc, 0,
                y, 2, 3, 5, 5, GridBagConstraints.NONE,
                GridBagConstraints.NORTHWEST ) );

        //Name
        centerPane.add( new JLabel( "Name:" ), GuiUtil.buildGridBagConstraints(
                gbc, 2, y, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.CENTER ) );
        centerPane.add( new JTextField( "some name" ), GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 3, 1, 0, 0,
                        GridBagConstraints.HORIZONTAL,
                        GridBagConstraints.CENTER ) );

        //Ports
        centerPane.add( new JLabel( "Ports:" ), GuiUtil
                .buildGridBagConstraints( gbc, 2, ++y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.NORTH ) );
        centerPane.add( new JList(
                new String[] { "one", "two", "three", "four" } ), GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 3, 1, 0, 0,
                        GridBagConstraints.BOTH, GridBagConstraints.CENTER ) );

        //icon
        centerPane.add( new JLabel( "Icon:" ), GuiUtil.buildGridBagConstraints(
                gbc, 0, y += 2, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.WEST ) );
        centerPane.add( new JLabel( "Image" ), GuiUtil.buildGridBagConstraints(
                gbc, 1, y, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.WEST ) );
        centerPane.add( new JButton( "Choose..." ), GuiUtil
                .buildGridBagConstraints( gbc, 0, y += 1, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.WEST ) );
        centerPane.add( new JLabel( "default.gif" ), GuiUtil
                .buildGridBagConstraints( gbc, 1, y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.WEST ) );
        //modify scheme
        centerPane
                .add( new JCheckBox( "Modify current scheme" ), GuiUtil
                        .buildGridBagConstraints( gbc, 0, ++y, 6, 1, 0, 0,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.SOUTH ) );

        //change package
        centerPane
                .add( new JLabel( "Package:" ), GuiUtil
                        .buildGridBagConstraints( gbc, 0, ++y, 1, 1, 0, 0,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.SOUTH ) );
        centerPane.add( new JLabel( schemeContainer.getPackage().getName() ),
                GuiUtil.buildGridBagConstraints( gbc, 1, y, 3, 1, 0, 0,
                        GridBagConstraints.HORIZONTAL,
                        GridBagConstraints.SOUTHWEST ) );
        centerPane
                .add( new JButton( "Change..." ), GuiUtil
                        .buildGridBagConstraints( gbc, 5, y, 1, 1, 0, 0,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.SOUTH ) );
        return centerPane;
    }

    public static void main( String[] args ) {
        final PackageParser packageLoader = new PackageParser();
        SchemeContainer container = null;
        if ( packageLoader
                .load( new File(
                        "/home/pavelg/workspace/cocovila_packages/Circuit/Circuit.xml" ) ) ) {

            container = new SchemeContainer( packageLoader.getPackage(),
                    packageLoader.getPath() );

            new SchemeExportDialog( container ).setVisible( true );
        }
    }

    Rect rect = new Rect( 0, 0, 60, 35, Color.black.getRGB(), false, 1f, 0, 1f );

    private JPanel getPreviewPanel() {

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent( Graphics g ) {
                Graphics2D g2 = (Graphics2D) g;
                super.paintComponent( g2 );

                if ( RuntimeProperties.isAntialiasingOn() ) {
                    g2.setRenderingHint(
                            java.awt.RenderingHints.KEY_ANTIALIASING,
                            java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
                }
                System.err.println("draw");
                new Rect( 0, 0, 60, 35, Color.black.getRGB(), false, 3f, 100, 0 ).draw( 20, 20, 1f, 1f, g2 );
                new Text( 5, 20, getFont(), Color.red, 100, "name" ).draw( 20, 20, 1f, 1f, g2 );
                //            for ( int i = 0; i < shapeList.size(); i++ ) {
                //                Shape shape = shapeList.get( i );
                //                shape.draw( 0, 0, 1f, 1f, g2 );
                //            }
            }
        };
        
        return panel;
    }
}
