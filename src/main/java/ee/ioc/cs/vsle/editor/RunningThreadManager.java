/**
 * 
 */
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

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;

/**
 * @author pavelg
 * 
 */
public class RunningThreadManager {

    private static HashMap<Long, TimedThread> threadsByID = new LinkedHashMap<Long, TimedThread>();
    private static ManagerDialog dialog;

    /**
     * @param ProgrammRunner's id
     * @param Thread thr
     */
    public static void addThread( long id, TimedThread thr ) {

        threadsByID.put( id, thr );

        if ( dialog != null ) {
            dialog.onThreadAdded( id );
        }
    }

    /**
     * Stops given thread and removes corresponding panel
     * 
     * @param thread
     */
    @SuppressWarnings( "deprecation" )
    public static void removeThread( long id, boolean terminate ) {

        TimedThread thread = threadsByID.remove( id );

        if ( thread != null ) {

            if ( dialog != null ) {
                dialog.onThreadRemoved( id );
            }

            if ( terminate ) {
                try {
                    thread.stop();
                } catch ( Exception ex ) {
                    // do not print anything here
                }
            }
        }

    }

    /**
     * opens the dialog
     */
    public static void showDialog() {

        if ( dialog != null ) {
            dialog.toFront();
            return;
        }

        dialog = new ManagerDialog();
        for ( Long id : threadsByID.keySet() ) {
            dialog.onThreadAdded( id );
        }
        dialog.setVisible( true );
    }

    private static class ManagerDialog extends JDialog {

        private HashMap<Long, PanelWithTimer> threads = new LinkedHashMap<Long, PanelWithTimer>();

        private JPanel jContentPane = null;
        private JPanel m_mainPanel;
        private JButton jButtonRefresh = null;
        private JButton jButtonKillAll = null;

        /**
         * @param owner
         */
        private ManagerDialog() {
            super( Editor.getInstance(), "Running and unterminated threads" );
            initialize();
        }

        /* (non-Javadoc)
         * @see java.awt.Window#dispose()
         */
        @Override
        public void dispose() {
            super.dispose();
            threads.clear();
            dialog = null;

        }

        /**
         * @return
         */
        private JPanel getMainPanel() {
            if ( m_mainPanel == null ) {
                m_mainPanel = new JPanel();

                m_mainPanel.setLayout( new BoxLayout( m_mainPanel, BoxLayout.Y_AXIS ) );
            }

            return m_mainPanel;
        }

        /**
         * Panel with Timer
         */
        class PanelWithTimer extends JPanel {
            
            Timer timer;
            
            public PanelWithTimer(LayoutManager layout) {
                super(layout);
            }
            
            void setTimer( Timer timer ) {
                if( timer != null ) {
                    this.timer = timer;
                    this.timer.start();
                }
            }
            
            void stopTimer() {
                if( timer != null ) {
                    timer.stop();
                    timer = null;
                }
            }
        }
        
        /**
         * @param id
         * @return
         */
        private PanelWithTimer createThreadPanel( final long id ) {
            final PanelWithTimer panel = new PanelWithTimer( new FlowLayout( FlowLayout.LEFT ) );

            final TimedThread thread = threadsByID.get( id );
            String threadName = thread.getName();
            final String s = threadName + "(alive for %1$tT)";
            final Calendar c = Calendar.getInstance( TimeZone.getTimeZone( "GMT" ) );
            c.setTimeInMillis( 0L );
            final JLabel lbl = new JLabel( String.format( s, c ) );
            panel.add( lbl );

            JButton but = new JButton( "X" );
            panel.add( but );

            ActionListener taskPerformer = new ActionListener() {
                long ms = thread.getElapsedTime();

                public void actionPerformed( ActionEvent evt ) {
                    c.setTimeInMillis( ms += 1000 );
                    lbl.setText( String.format( s, c ) );
                }
            };

            panel.setTimer(new Timer( 1000, taskPerformer ));
            
            panel.setEnabled( true );
            but.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    panel.stopTimer();
                    RunningThreadManager.removeThread( id, true );
                }
            } );

            return panel;
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

        /**
         * @return
         */
        private JButton getJButtonKillAll() {
            if ( jButtonKillAll == null ) {
                jButtonKillAll = new JButton( "Kill All" );
                jButtonKillAll.addActionListener( new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        for ( Long id : threads.keySet() ) {
                            RunningThreadManager.removeThread( id, true );
                        }
                    }
                } );
            }
            return jButtonKillAll;
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

        /**
         * @param id
         */
        public void onThreadAdded( final long id ) {

            SwingUtilities.invokeLater( new Runnable() {

                public void run() {
                    PanelWithTimer panel = createThreadPanel( id );
                    threads.put( id, panel );
                    m_mainPanel.add( panel );
                    validate();
                }
            } );
        }

        /**
         * @param id
         */
        public void onThreadRemoved( final long id ) {

            SwingUtilities.invokeLater( new Runnable() {

                public void run() {
                    PanelWithTimer panel = threads.remove( id );

                    if ( panel != null ) {
                        panel.stopTimer();
                        m_mainPanel.remove( panel );
                        m_mainPanel.validate();
                        m_mainPanel.repaint();
                    }
                }
            } );

        }

    }
}
