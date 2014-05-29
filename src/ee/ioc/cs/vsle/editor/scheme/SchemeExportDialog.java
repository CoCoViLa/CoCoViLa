package ee.ioc.cs.vsle.editor.scheme;

import java.awt.*;
import java.awt.Point;
import java.awt.event.*;
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
    private Set<String> ports;
    private String iconRelativePath;
    private boolean isOk;
    
    //GUI
    private JButton jbtOk;
    private JCheckBox jcbModifyScheme;
    private JCheckBox jcbExportFields;
    private JTextField jtfName;
    private JTextField jtfDescr;
    private JPanel previewPanel;
    private JList jlPorts;
    
    //shapes
    private Text previewText = new Text( 5, 20, getPreviewPanel().getFont(), Color.red, "" );
    private Rect previewRect = new Rect( 0, 0, 35, 35, Color.black, false, 1f, 0 );
    
    /**
     * @param schemeContainer
     * @param ports
     */
    public SchemeExportDialog( ISchemeContainer schemeContainer, Set<String> ports ) {
        super( Editor.getInstance(), "Export scheme", true );
        
        assert schemeContainer != null;
        assert ports != null;
        
        this.schemeContainer = schemeContainer;
        this.selectedPackage = schemeContainer.getPackage();
        this.ports = ports;
        init();
        
        setVisible( true );
    }

    private void init() {
        
        JPanel contentPane = new JPanel( new BorderLayout( 5, 5 ) );
        contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        setContentPane( contentPane );

        JPanel buttonPane = new JPanel(
                new FlowLayout( FlowLayout.CENTER, 5, 5 ) );
        
        final JButton jbtCancel;
        buttonPane.add( jbtOk = new JButton( "OK" ) );
        buttonPane.add( jbtCancel = new JButton( "Cancel" ) );

        ActionListener alst = new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent e ) {
                
                if( e.getSource() == jbtOk ) {
                    isOk = true;
                }
                
                SchemeExportDialog.this.dispose();
                jbtOk.removeActionListener( this );
                jbtCancel.removeActionListener( this );
            }
        };
        
        jbtOk.addActionListener( alst );
        jbtCancel.addActionListener( alst );
        
        contentPane.add( buttonPane, BorderLayout.SOUTH );
        contentPane.add( createCenterPanel(), BorderLayout.CENTER );

        setDefaultCloseOperation( DISPOSE_ON_CLOSE );
        pack();
        setResizable( false );
        
        updatePreview();
        
        getNameTextField().requestFocus();
        getNameTextField().selectAll();
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
                GridBagConstraints.EAST ) );
        
        centerPane.add( getNameTextField(), GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 3, 1, 0, 0,
                        GridBagConstraints.HORIZONTAL,
                        GridBagConstraints.CENTER ) );

        //Ports
        centerPane.add( new JLabel( "Ports:" ), GuiUtil
                .buildGridBagConstraints( gbc, 2, ++y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.NORTHEAST ) );

        JScrollPane jsListPane = new JScrollPane( getPortsList() );
        jsListPane.setPreferredSize(new Dimension(170, 105));
        
        centerPane.add( jsListPane, GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 3, 1, 0, 0,
                        GridBagConstraints.BOTH, GridBagConstraints.NORTH ) );

        //Fields
        jcbExportFields = new JCheckBox( "Export list of fields" );
        centerPane
                .add( jcbExportFields, GuiUtil
                        .buildGridBagConstraints( gbc, 0, y += 2, 2, 1, 0, 0,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.WEST ) );
        
        //Description
        centerPane.add( new JLabel( "Description:" ), GuiUtil.buildGridBagConstraints(
                gbc, 2, y, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.EAST ) );
        
        centerPane.add( getDescrTextField(), GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 3, 1, 0, 0,
                        GridBagConstraints.HORIZONTAL,
                        GridBagConstraints.CENTER ) );
        
        //icon
        centerPane.add( new JLabel( "Toolbar Icon:" ), GuiUtil.buildGridBagConstraints(
                gbc, 0, ++y, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.WEST ) );
        
        final JLabel jlblIcon = new JLabel();
        jlblIcon.setBorder( BorderFactory.createLineBorder( Color.black ) );
        
        centerPane.add( jlblIcon, GuiUtil.buildGridBagConstraints(
                gbc, 1, y, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.CENTER ) );
        
        final JLabel jlblIconName = new JLabel();
        centerPane.add( jlblIconName, GuiUtil
                .buildGridBagConstraints( gbc, 3, y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.WEST ) );
        
        JButton jbtChooseIcon = new JButton( "Change..." );
        centerPane.add( jbtChooseIcon, GuiUtil
                .buildGridBagConstraints( gbc, 5, y, 1, 1, 0, 0,
                        GridBagConstraints.NONE, GridBagConstraints.EAST ) );
        
        final Runnable dropIcon = new Runnable() {
            @Override
            public void run() {
                iconRelativePath = "default.gif";
                ImageIcon icon = FileFuncs.getImageIcon("images/default.gif", false);
                jlblIcon.setIcon( icon );
                jlblIconName.setText( iconRelativePath );
            }
        };
        
        dropIcon.run();
        
        jbtChooseIcon.addActionListener( new ActionListener() {
            
            @Override
            public void actionPerformed( ActionEvent arg0 ) {
                File iconFile = FileFuncs.showFileChooser( selectedPackage.getPath(), 
                        null, null, SchemeExportDialog.this, false );
                
                if( iconFile != null && iconFile.exists() ) {
                    
                    String packageDir = new File( selectedPackage.getPath() ).getParent();

                    if( !iconFile.getAbsolutePath().startsWith( packageDir ) ) {
                        JOptionPane.showMessageDialog( 
                                SchemeExportDialog.this, 
                                "Image should be in the (sub)directory of the package " + selectedPackage.getName(), 
                                "Error", JOptionPane.ERROR_MESSAGE );
                        return;
                    }

                    ImageIcon ii = FileFuncs.getImageIcon( iconFile.getAbsolutePath(), true );
                    if( ii.getImageLoadStatus() == MediaTracker.COMPLETE ) {
                        
                        iconRelativePath = iconFile.getAbsolutePath().substring( packageDir.length()+1 );
                        jlblIcon.setIcon( ii );
                        jlblIconName.setText( iconRelativePath );
                        jlblIconName.setToolTipText( iconFile.getAbsolutePath() ); 
                        checkValidity();
                    } else {
                        JOptionPane.showMessageDialog( 
                                SchemeExportDialog.this, 
                                "Error loading image " + iconFile.getAbsolutePath(), 
                                "Error", JOptionPane.ERROR_MESSAGE );
                        return;
                    }
                    SchemeExportDialog.this.pack();
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
                File oldPackFile = new File( selectedPackage.getPath() );
                File packFile = FileFuncs.showFileChooser( null, 
                        oldPackFile, 
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
                        
                        //check image icon
                        if( iconRelativePath != null 
                                && ! new File(packFile.getParent(), iconRelativePath).exists() ) {
                            dropIcon.run();
                        }
                        
                        checkValidity();
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
        jcbModifyScheme = new JCheckBox( "Replace selected objects with new object in the current scheme" );
        centerPane
                .add(jcbModifyScheme, GuiUtil
                        .buildGridBagConstraints( gbc, 0, ++y, 6, 1, 0, 0,
                                GridBagConstraints.HORIZONTAL,
                                GridBagConstraints.WEST ) );
        
        return centerPane;
    }

    private void checkValidity() {

        boolean isValid = 
            iconRelativePath != null 
                && StringUtil.isJavaIdentifier( getSchemeName() );

        jbtOk.setEnabled( isValid );
    }
    
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

    private JTextField getDescrTextField() {
        
        if( jtfDescr == null ) {
            jtfDescr = new JTextField( "" );
        }
        return jtfDescr;
    }

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
                    
                    previewRect.setWidth( rectWidth );
                    int offset = 20;
                    previewRect.draw( offset, offset, 1f, 1f, g2 );
                    previewText.draw( offset, offset, 1f, 1f, g2 );
                    
                    int maxPorts = Math.max( rectMaxWidth / portStep, 1 );
                    portPoints.clear();
                    
                    for( int i = 0; i < selectedPortCount; i++ ) {
                        
                        Point p = new Point( 12 + (i % maxPorts)*portStep, (i/maxPorts + 1)*portStep + 20 );
                        portPoints.add( p );
                        
                        Port.DEFAULT_OPEN_GRAPHICS.draw( 
                                p.x + offset, 
                                p.y + offset, 
                                1f, 1f, g2 );
                    }
                }
            };
        }
        return previewPanel;
    }
    
    private List<Point> portPoints = new ArrayList<Point>();
    
    public Point[] getPortPoints() {
        return portPoints.toArray( new Point[portPoints.size()] );
    }
    
    public ClassGraphics getClassGraphics() {
        
        ClassGraphics cg = new ClassGraphics();
        cg.setBoundWidth( previewRect.getWidth() );
        cg.setBoundHeight( previewRect.getHeight() );
        cg.addShape( previewText );
        cg.addShape( previewRect );
        return cg;
    }
    
    private void updatePreview() {
        
        previewText.setText( getNameTextField().getText() );
        checkValidity();
        getPreviewPanel().repaint();
    }
    
   
    private JList getPortsList() {
        
        if( jlPorts == null ) {
            Set<String> model = new LinkedHashSet<String>();
            //first list ports with outer connections
            model.addAll( ports );
            //and then put the rest of the ports
            for (GObj obj : schemeContainer.getObjectList()) {
                for( Port port : obj.getPortList() ) {
                    model.add( port.getNameWithObject() );
                }
            }
            jlPorts = new JList( model.toArray() );
            jlPorts.setSelectionInterval( 0, ports.size()-1 );
            jlPorts.addListSelectionListener( new ListSelectionListener() {

                @Override
                public void valueChanged( ListSelectionEvent e ) {
                    updatePreview();
                }
            });
            jlPorts.setVisibleRowCount( 6 );

        }
        
        return jlPorts;
    }
    
    public String getSchemeName() {
        return getNameTextField().getText();
    }
    
    public String[] getSelectedPortNames() {
        
        List<String> portNames = new ArrayList<String>();
        for( Object value : getPortsList().getSelectedValues() ) {
            portNames.add( (String)value );
        }
        
        return portNames.toArray( new String[portNames.size()] );
    }
    
    public String getImageFilename() {
        return iconRelativePath;
    }
    
    public VPackage getSelectedPackage() {
        return selectedPackage;
    }
    
    public boolean shouldModifyCurrentScheme() {
        return jcbModifyScheme.isSelected();
    }

    public boolean isOk() {
        return isOk;
    }
    
    public String getDescription() {
        return getDescrTextField().getText();
    }
    
    public boolean shouldExportFields() {
        return jcbExportFields.isSelected();
    }
}
