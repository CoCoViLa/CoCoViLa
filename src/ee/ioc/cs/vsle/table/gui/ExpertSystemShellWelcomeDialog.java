package ee.ioc.cs.vsle.table.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

public class ExpertSystemShellWelcomeDialog extends JDialog {

    public ExpertSystemShellWelcomeDialog() {
        super( Editor.getInstance(), "Expert System Shell", ModalityType.APPLICATION_MODAL );
        
        init();
    }
    
    private void init() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        
        contentPane.add( GuiUtil.addComponentAsFlow( new JLabel( "Welcome to Expert System Shell!" ), FlowLayout.CENTER ) );
        contentPane.add( GuiUtil.addComponentAsFlow( new JLabel( "Choose your action:" ), FlowLayout.CENTER ) );
        
        final JButton jbNewExpTbl = new JButton( "Create new expert table" );
        contentPane.add( GuiUtil.addComponentAsFlow( jbNewExpTbl, FlowLayout.CENTER ) );
        final JButton jbExistExpTbl = new JButton( "Open existing expert table" );
        contentPane.add( GuiUtil.addComponentAsFlow( jbExistExpTbl, FlowLayout.CENTER ) );
        final JButton jbConsultExpTbl = new JButton( "Consult expert table" );
        contentPane.add( GuiUtil.addComponentAsFlow( jbConsultExpTbl, FlowLayout.CENTER ) );
        
        setContentPane( contentPane );
        
        ActionListener lst = new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e ) {
                
                Runnable runnable;
                if( e.getSource() == jbNewExpTbl ) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            TableFrame frame = new TableFrame();
                            frame.setLocationRelativeTo( Editor.getInstance() );
                            frame.setVisible( true );
                            frame.toFront();
                            frame.newTable();
                        }
                    };
                } else if( e.getSource() == jbExistExpTbl ) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            TableFrame frame = new TableFrame();
                            frame.setLocationRelativeTo( Editor.getInstance() );
                            frame.setVisible( true );
                            frame.toFront();
                            frame.openTable();
                        }
                    };
                } else if ( e.getSource() == jbConsultExpTbl ) {
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            new ExpertConsultant( Editor.getInstance(),
                                    Editor.getInstance() != null 
                                        ? Editor.getInstance().getCurrentPackage() != null 
                                            ? Editor.getInstance().getCurrentPackage().getPath() 
                                            : null//yuck
                                        : null ).setVisible( true );
                        }
                    };
                } else return;
                
                jbNewExpTbl.removeActionListener( this );
                jbExistExpTbl.removeActionListener( this );
                jbConsultExpTbl.removeActionListener( this );
                
                dispose();
                
                SwingUtilities.invokeLater( runnable );
            }
        };
        
        jbNewExpTbl.addActionListener( lst );
        jbExistExpTbl.addActionListener( lst );
        jbConsultExpTbl.addActionListener( lst );
        
        pack();
    }
    
    
    /**
     * @param args
     */
    public static void main( String[] args ) {

        try {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
            
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

        } catch ( Exception e ) {
            db.p( "Unable to init default Look And Feel: " + UIManager.getSystemLookAndFeelClassName() );
        }
        
        SwingUtilities.invokeLater( new Runnable() {
            
            @Override
            public void run() {
                ExpertSystemShellWelcomeDialog d = new ExpertSystemShellWelcomeDialog();
                d.setDefaultCloseOperation( DISPOSE_ON_CLOSE );
                d.setVisible( true );
            }
        } );
    }

}
