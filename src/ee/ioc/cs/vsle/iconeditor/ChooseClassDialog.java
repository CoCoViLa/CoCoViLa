package ee.ioc.cs.vsle.iconeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;
/*
 * Displays a list of available classes
 */
public class ChooseClassDialog extends JDialog {
	
	
	private static final long serialVersionUID = 1L;
	private static final JButton bttnOk = new JButton("OK");
	private static final JButton bttnCancel = new JButton("Cancel");
	private static final JPanel bttnPanel = new JPanel();
	private static final JScrollPane scrollPane = new JScrollPane();
	private static JList jl = new JList();
	ArrayList pc;
	String selectedValue;
	
	
	public ChooseClassDialog(ArrayList pc) {
		
		this.setTitle("Import Class");
		this.pc = pc;
		bttnPanel.add(bttnOk);
		bttnPanel.add(bttnCancel);
		
		/* Let the user choose only one class at the time */
		DefaultListSelectionModel dlsm = new DefaultListSelectionModel();
		dlsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dlsm.setLeadAnchorNotificationEnabled(false);

		jl.setListData(pc.toArray());
		
		jl.setSelectionModel(dlsm);
		
		scrollPane.getViewport().setView(jl);
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane,BorderLayout.CENTER);

		getContentPane().add(bttnPanel,BorderLayout.SOUTH);
		
		setSize(new Dimension(250,250));
		
		setResizable(false);
		setModal(true);
		
		bttnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					selectedValue = jl.getSelectedValue().toString();
				} catch (NullPointerException e) {
					selectedValue = null;
				}
				setVisible(false);
				
			}
		});
		
		bttnCancel.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent evt) {
				setVisible(false);
			  }
		});

	}
	
	/* Refreshes list items */ 
	public void newJList(ArrayList pc) {
		jl.setListData(pc.toArray());
	}
	public String getSelectedValue(){
		return selectedValue;
	}

}
