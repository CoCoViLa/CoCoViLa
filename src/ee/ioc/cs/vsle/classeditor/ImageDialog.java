package ee.ioc.cs.vsle.classeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.graphics.Image;
import ee.ioc.cs.vsle.vclass.Canvas;

public class ImageDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JButton bttnOk = new JButton( "OK" );
    private JButton bttnCancel = new JButton( "Cancel" );

    private ClassEditor editor;

    private Image image;

    private String fullPath;
    private String relativePath;
    
    private JTextField m_jtfImagePath;

    private JButton m_jbtImageSelect;

    private JCheckBox m_jcbAllowResize;
    
    ImageDialog( ClassEditor editor, Image image ) {
        super( editor );
        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        this.setModal( true );
        this.editor = editor;
        
        if ( image != null ) {
            setTitle( "Edit Images" );
            this.image = image;
        } else {
            setTitle( "Insert Image" );
        }

        initGUI();
        
        setSize( new Dimension( 350, 250 ) );
//        setResizable( false );
        pack();
        setLocationRelativeTo( editor );
    }

    private void initGUI() {
        
        JPanel pnlMain = new JPanel( new BorderLayout() );
        JPanel pnlButtons = new JPanel();
        
        m_jcbAllowResize = new JCheckBox( "Allow resizing", false );
        m_jtfImagePath = new JTextField( 30 );
        m_jbtImageSelect = new JButton( "Browse..." );
        m_jbtImageSelect.setMargin( new Insets( 0, 0, 0, 0 ) );
        
        JPanel topFlow = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        topFlow.add( m_jtfImagePath );
        topFlow.add( m_jbtImageSelect );
        JPanel topFlow2 = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
        topFlow2.add( m_jcbAllowResize );
        
        Box top = Box.createVerticalBox();
        top.setBorder( BorderFactory.createTitledBorder( "NB! Set image path relative to the package" ) );
        top.add( topFlow );
        top.add( topFlow2 );
        
        pnlMain.add( top, BorderLayout.NORTH );
        
        pnlButtons.add( bttnOk );
        pnlButtons.add( bttnCancel );
        
        pnlMain.add( pnlButtons, BorderLayout.SOUTH );
        
        getContentPane().add( pnlMain );
        
        m_jbtImageSelect.addActionListener( this );
        bttnCancel.addActionListener( this );
        bttnOk.addActionListener( this );
    }
    
    /**
     * Action listener.
     * @param evt ActionEvent - action event.
     */
    public void actionPerformed( ActionEvent evt ) {
        if ( evt.getSource() == bttnCancel ) {
            dispose();
        } else if ( evt.getSource() == bttnOk ) {
        	ClassCanvas canvas = editor.getCurrentCanvas();
            if( fullPath != null && fullPath.length() != 0 
                    && relativePath != null && relativePath.length() != 0 ) {
            	
            	image = new Image( canvas.mouseX, canvas.mouseY, fullPath, relativePath, !m_jcbAllowResize.isSelected() );
            }
            
            if( image != null ) {
            	canvas.mListener.addShape(image);
            	canvas.drawingArea.repaint();
                editor.repaint();
            }
            dispose();
        } else if ( evt.getSource() == m_jbtImageSelect ) {
            JFileChooser fc = new JFileChooser( RuntimeProperties.getLastPath() );
            
            javax.swing.filechooser.FileFilter filter = new javax.swing.filechooser.FileFilter() {
                
                String[] formats = new String[] { ".png", ".gif" };
                
                @Override
                public String getDescription() {
                    return "Images";
                }

                @Override
                public boolean accept( java.io.File f ) {
                    
                    if( f.isDirectory() ) {
                        return true;
                    }
                    
                    for( String format : formats ) {
                        if( f.getName().toLowerCase().endsWith( format ) ) {
                            return true;
                        }
                    }
                    
                    return false;
                }
            };
            fc.setFileFilter( filter );
            
            fc.setDialogTitle( "Choose image" );

            int returnVal = fc.showOpenDialog( editor );
            
            if ( returnVal == JFileChooser.APPROVE_OPTION ) {
                File file = fc.getSelectedFile();

                String packageDir = editor.getCurrentPackage().getPackageDir();
                
                if( !file.getAbsolutePath().startsWith( packageDir ) ) {
                    JOptionPane.showMessageDialog( editor, "Path is not relative to the package", "Error", JOptionPane.ERROR_MESSAGE );
                    m_jtfImagePath.setText( "" );
                    return;
                }
                
                fullPath = file.getAbsolutePath();
                relativePath = file.getAbsolutePath().substring( packageDir.length() );
                
                m_jtfImagePath.setText( relativePath );
                
                RuntimeProperties.setLastPath( file.getAbsolutePath() );
            }
        }
    }

}
