/**
 * 
 */
package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

import sun.net.www.content.image.jpeg;

import com.sun.xml.internal.messaging.saaj.soap.JpegDataContentHandler;

/**
 * @author pavelg
 * 
 */
public class RunningThreadKillerDialog extends JDialog {

    private static RunningThreadKillerDialog s_instance = new RunningThreadKillerDialog();

    private static final long serialVersionUID = 1L;

    private static HashMap<Thread, JPanel> threads = new LinkedHashMap<Thread, JPanel>();

    private JPanel jContentPane = null;

    private JPanel m_mainPanel;

    private JButton jButtonRefresh = null;

    private JButton jButtonKillAll = null;
    
    public static void addThread( final Thread thr ) {
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {
                JPanel panel = createThreadPanel( thr );
                threads.put( thr, panel );

                s_instance.m_mainPanel.add( panel );
                s_instance.validate();
            }
        } );
    }

    public static void removeThread( final Runnable thr ) {
        SwingUtilities.invokeLater( new Runnable() {

            public void run() {
                JPanel panel = threads.remove( thr );

                if ( panel != null ) {
                    s_instance.m_mainPanel.remove( panel );
                    s_instance.m_mainPanel.validate();
                    s_instance.m_mainPanel.repaint();
                }
            }
        } );

    }

    private JPanel getMainPanel() {
        if ( m_mainPanel == null ) {
            m_mainPanel = new JPanel();

             m_mainPanel.setLayout( new BoxLayout( m_mainPanel, BoxLayout.Y_AXIS ) );
//            m_mainPanel.setLayout( new GridLayout( 0, 1 ) );
        }

        return m_mainPanel;
    }

    private static JPanel createThreadPanel( final Thread thread ) {
        JPanel panel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );

        String threadName = thread.getName();
        final String s = threadName + "(alive for %1$tT)";
        final JLabel lbl = new JLabel( threadName );
        panel.add( lbl );

        JButton but = new JButton( "X" );
        panel.add( but );

        ActionListener taskPerformer = new ActionListener() {
        	int secs = 0;
        	
            public void actionPerformed(ActionEvent evt) {
            	lbl.setText( String.format( s, (long)++secs * 1000 ) );
            }
        };
        
        final Timer timer = new Timer( 1000, taskPerformer);
        timer.start();
        
        panel.setEnabled( true );
        but.addActionListener( new ActionListener() {
            
            public void actionPerformed( ActionEvent e ) {
            	timer.stop();
                stopAndRemove( thread );
            }
        } );

        return panel;
    }

    /**
     * Stops given thread and removes corresponding panel
     * @param thread
     */
    @SuppressWarnings("deprecation")
    private static void stopAndRemove( Thread thread ) {
    	removeThread( thread );
        try {
        	
            thread.stop();
        } catch ( Exception ex ) {
        	//do not print anything here
        }
    }
    
    /**
     * This method initializes jButtonRefresh
     * 
     * @return javax.swing.JButton
     */
    private JButton getJButtonRefresh() {
        if ( jButtonRefresh == null ) {
            jButtonRefresh = new JButton( "Refresh" );
            jButtonRefresh.addActionListener( new java.awt.event.ActionListener() {
                public void actionPerformed( java.awt.event.ActionEvent e ) {
                    getMainPanel().removeAll();

                    for ( JPanel panel : threads.values() ) {
                        getMainPanel().add( panel );
                    }
                }
            } );
        }
        return jButtonRefresh;
    }

    private JButton getJButtonKillAll() {
        if ( jButtonKillAll == null ) {
        	jButtonKillAll = new JButton( "Kill All" );
        	jButtonKillAll.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    for ( Thread thread : threads.keySet() ) {
                    	stopAndRemove( thread );
                    }
                }
            } );
        }
        return jButtonKillAll;
    }
    
    public static void getInstance() {
        s_instance.setVisible( true );
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        RunningThreadKillerDialog d = new RunningThreadKillerDialog();
        d.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );
        d.setVisible( true );

        for ( int i = 0; i < 5; i++ ) {
            Thread runn = new Thread() {
                public void run() {
                    while ( true ) {
                        try {
                            Thread.sleep( 2000 );
                        } catch ( InterruptedException e ) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            runn.start();

            RunningThreadKillerDialog.addThread( runn );
        }

    }

    /**
     * @param owner
     */
    private RunningThreadKillerDialog() {
        super( Editor.getInstance(), "Running and unterminated threads" );
        initialize();
    }

    /**
     * This method initializes this
     */
    private void initialize() {
        this.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
        this.setSize( 430, 200 );
        this.setContentPane( getJContentPane() );
        this.setLocationRelativeTo( getParent() );
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if ( jContentPane == null ) {
            jContentPane = new JPanel();
            jContentPane.setLayout( new BorderLayout() );
            JPanel flow = new JPanel( new FlowLayout() );
            flow.add( getMainPanel() );
            JScrollPane scrollPane = new JScrollPane( flow );
            jContentPane.add( scrollPane, BorderLayout.CENTER );
            JPanel btnPanel = new JPanel( new GridLayout( 1, 2 ) );
            btnPanel.add( getJButtonRefresh() );
            btnPanel.add( getJButtonKillAll() );
            jContentPane.add( btnPanel, BorderLayout.SOUTH );
        }
        return jContentPane;
    }

}
