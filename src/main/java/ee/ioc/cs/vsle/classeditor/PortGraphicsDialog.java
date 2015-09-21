package ee.ioc.cs.vsle.classeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ee.ioc.cs.vsle.graphics.Rect;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.packageparse.PackageXmlProcessor;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.vclass.Canvas.DrawingArea;

public class PortGraphicsDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private static final JButton bttnOk = new JButton("OK");
	private static final JButton bttnView = new JButton("View");
	private static final JButton bttnCancel = new JButton("Cancel");
	private static final JPanel bttnPanel = new JPanel();
	private static final JScrollPane scrollPane = new JScrollPane();
	private static JList jl = new JList();
	private JLabel picture = new JLabel();
	private static final JScrollPane previewPane = new JScrollPane();
	File file;
	private VPackage pkg;
	
	
	
	//= new JScrollPane( drawingArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
    //        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
	//
	//private DrawingArea da;
    private PopupCanvas cc;// = new PopupCanvas();
       
	ArrayList<String> pc;
	String selectedValue;
	
	
	 private JSplitPane splitPane;
	   
	
	public PortGraphicsDialog(ArrayList<String> pc, String title, JRootPane parent, PopupCanvas cc, File file, boolean openFlag ) { //,  Graphics g 
			
			setTitle(title);
		    this.pc = pc;
		    this.file = file;
		    this.cc = cc;
		    
			bttnPanel.add(bttnOk);
		//	bttnPanel.add(bttnView);
			bttnPanel.add(bttnCancel);

			jl.setListData(pc.toArray());
			scrollPane.getViewport().setView(jl);		
			
			previewPane.getViewport().setView(picture);
			
			Dimension minimumSize = new Dimension(400, 350);
		    scrollPane.setMinimumSize(minimumSize);
		    previewPane.setMinimumSize(minimumSize);
			
		    
		    pkg = PackageXmlProcessor.loadWOValidation(file); // load previews ONCE
			/*Shape shape =  new Rect(2, 2, 20, 13, new Color(65485), true, (float) 1.0, (float)0.0);
			 ArrayList<Shape> shapes = new ArrayList<Shape>();
		        shapes.add(shape);
			GObj obj = new GObj();
			obj.setX(2);
			obj.setY(2);
			obj.setShapes(shapes);
			
			//cc.drawAreaSize.setSize(minimumSize);
			
			cc.addObject(obj);
			cc.repaint();*/
			//cc.setSize(new Dimension(100, 250));
			//cc.setMaximumSize(new Dimension(100, 250));
			
		//	previewPane.getViewport().setView((Component)obj);
			previewPane.add(cc);
			previewPane.getViewport().setView(cc);
			//previewPane.getComponents();
			
	        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
	        		scrollPane,  previewPane);
	        splitPane.setDividerLocation(130);	 
	        splitPane.setOneTouchExpandable(true);
	        splitPane.setContinuousLayout(true);	        	  	      
	        
	    	getContentPane().setLayout(new BorderLayout());
			setModal(true);

	        getContentPane().add(splitPane, BorderLayout.CENTER);
	        getContentPane().add(bttnPanel,BorderLayout.SOUTH);
	        setMinimumSize(new Dimension(400, 250));
	        setLocationRelativeTo(parent);
	     
	     
	        
	        jl.addListSelectionListener(new ListSelectionListener(){
	        
	          public void valueChanged(ListSelectionEvent e) {	 
	        		if(jl != null && jl.getSelectedValue() != null){  
	        			updateLabel(jl.getSelectedValue().toString());
	        		} else selectedValue = null; 
	        		/*catch (NullPointerException ex) {
	        			selectedValue = null;
				}*/
	          }
	        });
	        
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
			
			bttnView.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					try {
						selectedValue = jl.getSelectedValue().toString();
						updateLabel(selectedValue);
					} catch (NullPointerException e) {
						selectedValue = null;
					}
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
	
	
	 protected void updateLabel (String name) {
		 
		 cc.clearObjects();
		 
		 try {					
			  if ( pkg != null ) {
				  PackageClass pClass = pkg.getClass(name);	
				  if ( pClass!= null && pClass.getGraphics() != null && pClass.getGraphics().getShapes() != null){
					    GObj obj = new GObj();		
					 //   Shapes TODO resize!
						obj.setShapes(pClass.getGraphics().getShapes());
						obj.setX(20);
						obj.setY(20);
						cc.addObject(obj);						
						cc.repaint();
					  
				  } else {
					  picture.setText("  Image not found for " + name);
				  }
			  }

		  } catch ( Exception exc ) {
			  exc.printStackTrace();
		  }           		 
	    }

}
