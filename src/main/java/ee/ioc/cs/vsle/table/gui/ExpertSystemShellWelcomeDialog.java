package ee.ioc.cs.vsle.table.gui;

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

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpertSystemShellWelcomeDialog extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(ExpertSystemShellWelcomeDialog.class);

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
                                            : RuntimeProperties.getEssLastPath()
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
            logger.error( "Unable to init default Look And Feel: " + UIManager.getSystemLookAndFeelClassName() );
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
