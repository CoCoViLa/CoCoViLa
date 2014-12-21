package ee.ioc.cs.vsle.editor;

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
