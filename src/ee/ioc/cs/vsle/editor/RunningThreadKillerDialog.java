/**
 * 
 */
package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/**
 * @author pavelg
 *
 */
public class RunningThreadKillerDialog extends JDialog {

	private static RunningThreadKillerDialog s_instance = new RunningThreadKillerDialog();
	
	private static final long serialVersionUID = 1L;

	private static HashMap<Thread, JPanel> threads = new HashMap<Thread, JPanel>();  
	
	private JPanel jContentPane = null;

	private JPanel m_mainPanel;

	private JButton jButtonRefresh = null;

	
	public static void addThread( final Thread thr ) {
		SwingUtilities.invokeLater( new Runnable() {

        	public void run() {
        		JPanel panel = createThreadPanel( thr );
        		threads.put( thr, panel );
        		
        		s_instance.m_mainPanel.add( panel );
        		s_instance.validate();
        	}} 
        );
	}
	
	public static void removeThread( final Runnable thr ) {
		SwingUtilities.invokeLater( new Runnable() {

        	public void run() {
        		JPanel panel = threads.remove( thr );
        		
        		if( panel != null )
        		{
        			s_instance.m_mainPanel.remove( panel );
        			s_instance.m_mainPanel.validate();
        			s_instance.m_mainPanel.repaint();
        		}
        	}} 
        );
		
	}
	
	
	private JPanel getMainPanel()
	{
		if( m_mainPanel == null )
		{
			m_mainPanel = new JPanel();
			
//			m_mainPanel.setLayout( new BoxLayout( m_mainPanel, BoxLayout.Y_AXIS ) );
			m_mainPanel.setLayout( new GridLayout( 0, 1 ) );
		}
		
		return m_mainPanel;
	}

	private static JPanel createThreadPanel( final Thread thread )
	{
		JPanel panel = new JPanel();
		
		panel.add( new JLabel( thread.getName() ) );

		JButton but = new JButton( "X" );
		panel.add( but );
		
		panel.setEnabled( true );
		but.addActionListener( new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {
				removeThread( thread );
				try {
					thread.stop();
				} catch( Exception ex ) {}
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
		if (jButtonRefresh == null) {
			jButtonRefresh = new JButton( "Refresh" );
			jButtonRefresh.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					getMainPanel().removeAll();
					
					for (Iterator iter = threads.values().iterator(); iter.hasNext();) {
						JPanel el = (JPanel)iter.next();
						getMainPanel().add( el );
					}
				}
			});
		}
		return jButtonRefresh;
	}

	public static void getInstance() {
		//s_instance.jList.setEnabled( true );
//		s_instance.dispose();
//		
//		s_instance = new RunningThreadKillerDialog();
		
		s_instance.setVisible( true );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RunningThreadKillerDialog d = new RunningThreadKillerDialog();
		d.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );
		d.setVisible( true );
		
		for (int i = 0; i < 5; i++) {
			Thread runn = new Thread() {
				public void run() {
					while ( true ) {
						try {
							Thread.sleep( 2000 );
						} catch (InterruptedException e) {
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
		super( Editor.getInstance(), "Thread killah" );
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setLocationRelativeTo( getParent() );
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			 JScrollPane scrollPane = new JScrollPane(getMainPanel());

			jContentPane.add(scrollPane, BorderLayout.CENTER);
			jContentPane.add(getJButtonRefresh(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

}
