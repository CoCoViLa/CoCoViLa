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

import java.awt.Window;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.UIManager.*;

import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Look and feel handler.
 */
public class Look {

    private static final Logger logger = LoggerFactory.getLogger(Look.class);

    private static Look s_instance;

    public static Look getInstance() {

        if ( s_instance == null ) {
            s_instance = new Look();
        }

        return s_instance;
    }

    /**
     * Updates the L'n'F of the application at run-time.
     * 
     * @param lnf the class name of the new Look and Feel
     * @param saveConfig true to save the LnF as default
     */
    public static void changeLookAndFeelForApp( final String lnf, final boolean saveConfig ) {

        try {
            UIManager.setLookAndFeel( lnf );

            for ( Window window : Window.getWindows() ) {
                SwingUtilities.updateComponentTreeUI( window );
                window.pack();
            }

            if ( saveConfig )
                RuntimeProperties.setLnf( lnf );
            
        } catch ( Exception e ) {
            logger.debug("Unable to change Look And Feel: " + lnf);
        }
    }

    public void initDefaultLnF() {

        try {

            UIManager.setLookAndFeel( RuntimeProperties.getLnf() );

        } catch ( Exception e ) {
            logger.debug("Unable to init default Look And Feel: " + RuntimeProperties.getLnf());
        }
    }

    public void createMenuItems( JMenu menu ) {

        LookAndFeel laf = UIManager.getLookAndFeel();

        String lafName = ( laf != null ) ? laf.getName() : "";

        ButtonGroup group = new ButtonGroup();

        for ( final LookAndFeelInfo lnfs : UIManager.getInstalledLookAndFeels() ) {

            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem( lnfs.getName() );

            group.add( menuItem );

            if ( lafName.equals( lnfs.getName() ) ) {
                menuItem.setSelected( true );
            }

            menuItem.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    changeLookAndFeelForApp( lnfs.getClassName(), true );
                }
            } );

            menu.add( menuItem );
        }

        menu.setToolTipText( "Set default Look And Feel" );
    }

}
