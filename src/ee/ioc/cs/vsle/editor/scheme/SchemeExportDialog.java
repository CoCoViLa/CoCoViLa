package ee.ioc.cs.vsle.editor.scheme;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.packageparse.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

public class SchemeExportDialog extends JDialog {

    private ISchemeContainer schemeContainer;
    private VPackage selectedPackage;
    private List<Port> ports;
    
    public SchemeExportDialog( ISchemeContainer schemeContainer, List<Port> ports ) {
        super( Editor.getInstance(), "Export scheme", true );
        assert schemeContainer != null;
        assert ports != null;
        this.schemeContainer = schemeContainer;
        this.selectedPackage = schemeContainer.getPackage();
        this.ports = ports;
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
        pack();
        setResizable( false );
        
        updatePreview();
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
        
        centerPane.add( getNameTextField(), GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 3, 1, 0, 0,
                        GridBagConstraints.HORIZONTAL,
                        GridBagConstraints.CENTER ) );

        //Ports
        centerPane.add( new JLabel( "Ports:" ), GuiUtil
                .buildGridBagConstraints( gbc, 2, ++y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.NORTH ) );

        JScrollPane jsListPane = new JScrollPane( getPortsList() );
        jsListPane.setPreferredSize(new Dimension(170, 105));
        
        centerPane.add( jsListPane, GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 3, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.NORTH ) );

        //icon
        centerPane.add( new JLabel( "Toolbar Icon:" ), GuiUtil.buildGridBagConstraints(
                gbc, 0, y += 2, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.WEST ) );
        
        ImageIcon icon = FileFuncs.getImageIcon("images/default.gif", false);
        final JLabel jlblIcon = new JLabel(icon);
        jlblIcon.setBorder( BorderFactory.createLineBorder( Color.black ) );
        
        centerPane.add( jlblIcon, GuiUtil.buildGridBagConstraints(
                gbc, 1, y, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.CENTER ) );
        
        final JLabel jlblIconName = new JLabel( "default.gif" );
        centerPane.add( jlblIconName, GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.WEST ) );
        
        JButton jbtChooseIcon = new JButton( "Change..." );
        centerPane.add( jbtChooseIcon, GuiUtil
                .buildGridBagConstraints( gbc, 5, y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.EAST ) );
        
        jbtChooseIcon.addActionListener( new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent arg0 ) {
                File iconFile = FileFuncs.showFileChooser( selectedPackage.getPath(), 
                        null, null, SchemeExportDialog.this, false );
                
                if( iconFile != null && iconFile.exists() ) {
                    ImageIcon ii = FileFuncs.getImageIcon( iconFile.getAbsolutePath(), true );
                    if( ii.getImageLoadStatus() == MediaTracker.COMPLETE ) {
                        jlblIcon.setIcon( ii );
                        jlblIconName.setText( iconFile.getName() );
                        jlblIconName.setToolTipText( iconFile.getAbsolutePath() );                        
                    }
                }
            }
        });

        //change package
        centerPane
                .add( new JLabel( "Package:" ), GuiUtil
                        .buildGridBagConstraints( gbc, 0, ++y, 1, 1, 0, 0,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.SOUTH ) );
        
        final JLabel jlblPack = new JLabel();
        jlblPack.setText( selectedPackage.getName() );
        jlblPack.setToolTipText( selectedPackage.getPath() );
        
        centerPane.add( jlblPack,
                GuiUtil.buildGridBagConstraints( gbc, 1, y, 3, 1, 0, 0,
                        GridBagConstraints.HORIZONTAL,
                        GridBagConstraints.SOUTHWEST ) );
        
        JButton jbtChangePackage = new JButton( "Change..." );
        jbtChangePackage.addActionListener( new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent e ) {
                File packFile = FileFuncs.showFileChooser( selectedPackage.getPath(), 
                        new File( selectedPackage.getPath() ), 
                        new CustomFileFilter( CustomFileFilter.EXT.XML ), 
                        SchemeExportDialog.this, 
                        false );
                
                if( packFile != null ) {
                    VPackage pack = PackageXmlProcessor.load( packFile );
                    if( pack != null ) {
                        selectedPackage = pack;
                        jlblPack.setText( selectedPackage.getName() );
                        jlblPack.setToolTipText( selectedPackage.getPath() );
                        if( selectedPackage.equals( schemeContainer.getPackage() )) {
                            jcbModifyScheme.setEnabled( true );      
                            jcbModifyScheme.setToolTipText( null );
                        } else {
                            jcbModifyScheme.setSelected( false );
                            jcbModifyScheme.setEnabled( false );
                            jcbModifyScheme.setToolTipText( "Selected package differs from the package of original scheme" );
                        }
                        repaint();
                    }
                }
            }
        });
        
        centerPane
                .add( jbtChangePackage, GuiUtil
                        .buildGridBagConstraints( gbc, 5, y, 1, 1, 0, 0,
                                GridBagConstraints.NONE,
                                GridBagConstraints.EAST ) );
        
        //modify scheme
        jcbModifyScheme = new JCheckBox( "Modify current scheme" );
        centerPane
                .add(jcbModifyScheme, GuiUtil
                        .buildGridBagConstraints( gbc, 0, ++y, 6, 1, 0, 0,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.WEST ) );
        
        return centerPane;
    }

    private JCheckBox jcbModifyScheme;
    private JTextField jtfName;
    
    private JTextField getNameTextField() {
        
        if( jtfName == null ) {
            jtfName = new JTextField( "untitled" );
            
            jtfName.getDocument().addDocumentListener( new DocumentListener() {

                @Override
                public void removeUpdate( DocumentEvent e ) {
                    updatePreview();
                }

                @Override
                public void insertUpdate( DocumentEvent e ) {
                    updatePreview();
                }

                @Override
                public void changedUpdate( DocumentEvent e ) {
                    updatePreview();
                }
            });
        }
        return jtfName;
    }

    public static void main( String[] args ) {
        SchemeContainer container = null;
        VPackage pack;
        if ( ( pack = PackageXmlProcessor
                .load( new File(
                        "/Users/pavelg/workspace/cocovila_packages/Circuit/Circuit.xml" ) ) ) != null ) {

            container = new SchemeContainer( pack, pack.getPath() );

            new SchemeExportDialog( container, new ArrayList<Port>(Arrays.asList( new Port[] {  } ) ) ).setVisible( true );
        }
    }

    JPanel previewPanel;
    Text previewText = new Text( 5, 20, getPreviewPanel().getFont(), Color.red, 255, "" );
    
    private JPanel getPreviewPanel() {
        if( previewPanel == null ) {
            previewPanel = new JPanel() {
                @Override
                protected void paintComponent( Graphics g ) {
                    Graphics2D g2 = (Graphics2D) g;
                    super.paintComponent( g2 );

                    if ( RuntimeProperties.isAntialiasingOn() ) {
                        g2.setRenderingHint(
                                java.awt.RenderingHints.KEY_ANTIALIASING,
                                java.awt.RenderingHints.VALUE_ANTIALIAS_ON );
                    }
                    
                    int portStep = 15;
                    int selectedPortCount = getPortsList().getSelectedIndices().length;
                    int rectMinWidth = Math.max( 60, getFontMetrics( previewText.getFont() ).stringWidth( previewText.getText() ) + 10 );
                    rectMinWidth = Math.max( rectMinWidth, (selectedPortCount+1)*portStep );
                    int rectMaxWidth = previewPanel.getWidth() - 40;
                    int rectWidth = Math.min( rectMinWidth, rectMaxWidth );
                    
                    new Rect( 0, 0, rectWidth, 35, Color.black.getRGB(), false, 3f, 255, 0 ).draw( 20, 20, 1f, 1f, g2 );
                    previewText.draw( 20, 20, 1f, 1f, g2 );
                    
                    int maxPorts = Math.max( rectMaxWidth / portStep, 1 );
                    
                    for( int i = 0; i < selectedPortCount; i++ ) {
                        Port.DEFAULT_OPEN_GRAPHICS.draw( 
                                32 + (i % maxPorts)*portStep, 
                                (i/maxPorts + 1)*portStep + 40, 
                                1f, 1f, g2 );
                    }
                }
            };
        }
        return previewPanel;
    }
    
    private void updatePreview() {
        
        previewText.setText( getNameTextField().getText() );
        getPreviewPanel().repaint();
    }
    
    private JList jlPorts;
    
    private JList getPortsList() {
        
        if( jlPorts == null ) {
//            List<String> model = new ArrayList<String>();
            Set<String> model = new LinkedHashSet<String>();
            
            for( Port port : ports ) {
                String id = port.getObject().getName() + "." + port.getField().getName();
                model.add( id );
            }
//            model.addAll( ports );

            for( GObj obj : schemeContainer.getObjects() ) {
                for( Port port : obj.getPorts() ) {
                    String id = port.getObject().getName() + "." + port.getField().getName();
                    model.add( id );
//                    if( !ports.contains( port ) ) {
//                        model.add( port );
//                    }
                }
            }
            //        System.out.println( "Model: " + model);
            jlPorts = new JList( model.toArray() );

            ListCellRenderer rend = new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent( JList list, Object value,
                        int index, boolean isSelected, boolean cellHasFocus ) {

                    JLabel lbl = (JLabel) super.getListCellRendererComponent( jlPorts, value, index, isSelected, cellHasFocus );
//                    Port p = (Port)value;
//                    lbl.setText( p.getObject().getName() + "." + p.getName() + ( p.getId() != null ? " (" + p.getId() + ")" : "" ) );
                    return lbl;
                }
            };

            jlPorts.setCellRenderer( rend );
            jlPorts.setSelectionInterval( 0, ports.size()-1 );
            //        System.out.println(jlist.getModel().getClass().getName());
            jlPorts.addListSelectionListener( new ListSelectionListener() {

                @Override
                public void valueChanged( ListSelectionEvent e ) {
//                    Object[] values = jlPorts.getSelectedValues();
//                    System.out.println(Arrays.toString( values ));
                    updatePreview();
                }
            });
            jlPorts.setVisibleRowCount( 6 );

        }
        
        return jlPorts;
    }
}
