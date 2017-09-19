package ee.ioc.cs.vsle.iconeditor;

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
	ArrayList<String> pc;
	String selectedValue;
	
	
	public ChooseClassDialog(ArrayList<String> pc) {
		
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

        pack();
	}
	
	/* Refreshes list items */ 
	public void newJList(ArrayList<String> pc) {
		jl.setListData(pc.toArray());
	}
	public String getSelectedValue(){
		return selectedValue;
	}

}
