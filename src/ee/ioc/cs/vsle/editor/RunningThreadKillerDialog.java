/**
 * 
 */
package ee.ioc.cs.vsle.editor;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import ee.ioc.cs.vsle.event.*;

/**
 * @author pavelg
 *
 */
public class RunningThreadKillerDialog extends JDialog {

	private static RunningThreadKillerDialog s_instance = new RunningThreadKillerDialog();
	
	private static final long serialVersionUID = 1L;

	private static Vector<Runnable> threads = new Vector<Runnable>();  //  @jve:decl-index=0:
	
	private JPanel jContentPane = null;

	private JList jList = null;

	private JButton jButtonRefresh = null;

	
	public static void addThread( final Runnable thr ) {
		SwingUtilities.invokeLater( new Runnable() {

        	public void run() {
        		threads.add( thr );
        		
        		System.err.println( "add: " + threads );
        		
        		s_instance.jList.setListData( threads.toArray() );
        	}} 
        );
		
//		s_instance.jList.getModel().
	}
	
	public static void removeThread( final Runnable thr ) {
		SwingUtilities.invokeLater( new Runnable() {

        	public void run() {
        		threads.remove( thr );
        		System.err.println( "remove: " + threads );
        		s_instance.jList.setListData( threads.toArray() );
        	}} 
        );
		
//		s_instance.jList.getModel().
	}
	
	/**
	 * This method initializes jList	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJList() {
		if (jList == null) {
			jList = new JList();
			
			jList.setCellRenderer( new DefaultListCellRenderer() {
				
				public Component getListCellRendererComponent( JList list, Object value,
						int index, boolean isSelected, boolean cellHasFocus)
				{
					System.err.println( "gdf" );
					final Thread thr = (Thread)value;

					JPanel panel = new JPanel();

					panel.add( new JLabel( thr.getName() ) );

					JButton but = new JButton( "X" );
					panel.add( but );

					but.addActionListener( new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							thr.stop();

							removeThread( thr );
						}
					} );

					return panel;
				}
			} );
		}
		return jList;
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
					System.out.println("actionPerformed()");
				}
			});
		}
		return jButtonRefresh;
	}

	public static void getInstance() {
		
		s_instance.setVisible( true );
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RunningThreadKillerDialog d = new RunningThreadKillerDialog();
		d.setDefaultCloseOperation( JDialog.EXIT_ON_CLOSE );
		d.setVisible( true );
	}

	/**
	 * @param owner
	 */
	private RunningThreadKillerDialog() {
		super( Editor.getInstance() );
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setDefaultCloseOperation( JDialog.DISPOSE_ON_CLOSE );
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
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
			jContentPane.add(getJList(), BorderLayout.CENTER);
			jContentPane.add(getJButtonRefresh(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

}
