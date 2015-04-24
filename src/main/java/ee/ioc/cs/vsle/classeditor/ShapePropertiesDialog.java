package ee.ioc.cs.vsle.classeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.naming.SizeLimitExceededException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.multi.MultiListUI;

import ee.ioc.cs.vsle.common.ops.State;
import ee.ioc.cs.vsle.editor.PaletteBase;
import ee.ioc.cs.vsle.graphics.Arc;
import ee.ioc.cs.vsle.graphics.Line;
import ee.ioc.cs.vsle.graphics.Oval;
import ee.ioc.cs.vsle.graphics.Rect;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.vclass.ClassObject;
import ee.ioc.cs.vsle.vclass.GObj;

public class ShapePropertiesDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 1L;

    private JButton bttnOk = new JButton( "OK" );
    private JButton bttnCancel = new JButton( "Cancel" );
    private JButton colors = createColorButton(); 
    
    private ClassEditor editor;
    private GObj obj;
    private Shape shape;
    private Color col;
    
 // Labels
    private final JLabel lblTitle = new JLabel();
 	private final JLabel lblWidth = new JLabel("Width:");
 	private final JLabel lblHeight = new JLabel("Height:");
 	private final JLabel lblFixed = new JLabel("Fixed:");
 	private final JLabel lblX = new JLabel("x:");
 	private final JLabel lblY = new JLabel("y:");
 	private final JLabel lblStartX = new JLabel("Start x:");
 	private final JLabel lblStartY = new JLabel("Start y:");
 	private final JLabel lblEndX = new JLabel("End x:");
 	private final JLabel lblEndY = new JLabel("End y:");
 	private final JLabel lblColour = new JLabel("Current colour:");
 	private final JLabel lblColourPick = new JLabel("Change colour:");
 	private final JLabel lblLinetype = new JLabel("Linetype:");
 	//private final JLabel lblFilled = new JLabel("Filled:");
 	private final JLabel lblTransparency = new JLabel("Transparency:");
 	private final JLabel lblStroke = new JLabel("Stroke:");
 	private final JLabel lblArcAngle = new JLabel("Arc Angle:");
 	private final JLabel lblStartAngle = new JLabel("Start Angle:");
 	private String label; //document onchange fire src
 	
 // Fields
 	private final JTextField fldWidth = new JTextField();
 	private final JTextField fldHeight = new JTextField();
 	private final JFormattedTextField fldX = new JFormattedTextField(); 
 	private final JFormattedTextField fldY = new JFormattedTextField();
 	private final JFormattedTextField fldLinetype = new JFormattedTextField();
 	private final JFormattedTextField fldStroke = new JFormattedTextField();
 	private final JFormattedTextField fldTransparency = new JFormattedTextField();
 	private final JTextField fldStartX = new JTextField(); 
 	private final JTextField fldStartY = new JTextField();
 	private final JTextField fldEndX = new JTextField(); 
 	private final JTextField fldEndY = new JTextField();
 	private final JFormattedTextField fldArcAngle = new JFormattedTextField();
 	private final JFormattedTextField fldStartAngle = new JFormattedTextField();
 	private final JTextField fldColour = new JTextField();
 	
 	
	private final JPanel pnlLabels = new JPanel();
	private final JPanel pnlFields = new JPanel();
	private final JPanel pnlProps = new JPanel();
	private final JPanel pnlErrors = new JPanel();
	private final JPanel pnlTitle = new JPanel();
	private final JPanel pnlMain = new JPanel( new BorderLayout() );

    private JCheckBox c_Fixed = new JCheckBox();    
    
    ShapePropertiesDialog( ClassEditor editor, GObj obj ) {
        super( editor );
        setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        this.setModal( true );
        this.editor = editor;
        this.obj = obj;
        
        initValues();

        setLocationRelativeTo( editor );
        setResizable(false);
        pack();
    }
    
    private JButton createColorButton() {

    	ImageIcon icon = FileFuncs.getImageIcon("images/colorchooser.gif", false);
        JButton button = new JButton(icon);

        button.setSelectedIcon(icon);
        button.setToolTipText("Select colour");

        Insets BUTTON_BORDER = new Insets(2, 2, 2, 2);
        button.setMargin(BUTTON_BORDER);

        button.addActionListener(this);
        //button.addMouseListener(getButtonMouseListener());
        return button;
    }

    private void initGUI(int type) {
    	/**
    	 * types 1 - standard (rect, oval)
    	 * 		 2 - line
    	 * 		 3 - arc
    	 */
               
        JPanel pnlButtons = new JPanel();
        setTitle( "Edit Shape" );
        
        pnlLabels.setPreferredSize(new Dimension(100, 210));
        pnlLabels.setLayout(new GridLayout(12, 1));
        pnlLabels.setAlignmentX(RIGHT_ALIGNMENT);       
        if(type != 2){
        	pnlLabels.add(lblWidth);
        	pnlLabels.add(lblHeight);
        	pnlLabels.add(lblX);
            pnlLabels.add(lblY);
        } else {        	
        	pnlLabels.add(lblStartX);
        	pnlLabels.add(lblStartY);
        	pnlLabels.add(lblEndX);
            pnlLabels.add(lblEndY);
            pnlLabels.add(lblWidth);
        	pnlLabels.add(lblHeight);
        }
        if(type == 3){
        	pnlLabels.add(lblArcAngle);
        	pnlLabels.add(lblStartAngle);
        }
        pnlLabels.add(lblFixed);      
        pnlLabels.add(lblColour);
        pnlLabels.add(lblColourPick);
        pnlLabels.add(lblLinetype);
        pnlLabels.add(lblTransparency);
        pnlLabels.add(lblStroke);
        
        pnlFields.setPreferredSize(new Dimension(70, 210));
        pnlFields.setLayout(new GridLayout(12, 1));        
        if(type != 2){
        	pnlFields.add(fldWidth);
        	pnlFields.add(fldHeight);
        	pnlFields.add(fldX);
            pnlFields.add(fldY);
        } else {
        	pnlFields.add(fldStartX);
        	fldStartX.getDocument().addDocumentListener(new onChangeListner("width") );
        	pnlFields.add(fldStartY);
        	fldStartY.getDocument().addDocumentListener(new onChangeListner("height") );
        	pnlFields.add(fldEndX);        	        	
        	fldEndX.getDocument().addDocumentListener(new onChangeListner("width") );        	
        	pnlFields.add(fldEndY);
        	fldEndY.getDocument().addDocumentListener(new onChangeListner("height") );
        	pnlFields.add(fldWidth);
        	fldWidth.getDocument().addDocumentListener(new onChangeListner("x") );
        	pnlFields.add(fldHeight);
        	fldHeight.getDocument().addDocumentListener(new onChangeListner("y") );
        }
        if(type == 3){
        	pnlFields.add(fldArcAngle);
        	pnlFields.add(fldStartAngle);
        }
        pnlFields.add(c_Fixed);      
        pnlFields.add(fldColour);
        
        pnlFields.add(colors);

        pnlFields.add(fldLinetype);
        pnlFields.add(fldTransparency);
        pnlFields.add(fldStroke);
        
       
                        
        pnlButtons.add( bttnOk );
        pnlButtons.add( bttnCancel );

        bttnCancel.addActionListener( this );
        bttnOk.addActionListener( this );

        //addSeparator();
        pnlTitle.add(lblTitle);
        
       pnlErrors.setPreferredSize(new Dimension(170, 50));
        
        pnlProps.setLayout(new BorderLayout());
     //  pnlProps.setPreferredSize(new Dimension(160, 300));
		pnlProps.setBorder(new EmptyBorder(5, 5, 5, 5) );
		pnlProps.add(pnlTitle, BorderLayout.NORTH);
		pnlProps.add(pnlLabels,BorderLayout.WEST);
		pnlProps.add(pnlFields,BorderLayout.EAST);		
		pnlProps.add(pnlErrors, BorderLayout.SOUTH);
        
        pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5) );
        pnlMain.add(pnlProps, BorderLayout.NORTH );
        pnlMain.add(pnlButtons, BorderLayout.SOUTH);
        getContentPane().add(pnlMain);
       }
    
    private void initValues(){
    	if(obj.getX() != 0) fldX.setValue(obj.getX());
    	if(obj.getY() != 0) fldY.setValue(obj.getY());
    	if(obj.getWidth() != 0) fldWidth.setText(obj.getWidth()+"");
    	if(obj.getHeight() != 0) fldHeight.setText(obj.getHeight()+"");    	
    	if(obj.getShapes().size() > 0){
    		for(Shape s: obj.getShapes()){
    			this.shape = s;
    			if(s instanceof Rect){
    				if(s.isFilled()){
    					lblTitle.setText("Filled Rectangle");
    				} else lblTitle.setText("Rectangle");
    				initGUI(1);
    			}
    			if(s instanceof Oval){
    				if(s.isFilled()){
    					lblTitle.setText("Filled Oval");
    				} else lblTitle.setText("Oval");
    				initGUI(1);
    			}
    			if(s instanceof Line){
    				lblTitle.setText("Line");
    				//((Line) shape).setStringCoords(obj);
    				
    				if(obj.getWidth() == 0) fldWidth.setText(obj.getWidth()+""); // width or height can be 0 for Lines
    				if(obj.getHeight() == 0) fldHeight.setText(obj.getHeight()+"");
    				fldStartX.setText((obj.getX() + ((Line) shape).getStartX())+"");
    				fldStartY.setText((obj.getY() + ((Line) shape).getStartY())+"");

    				fldEndX.setText((obj.getX() + ((Line) shape).getEndX())+"");
    				fldEndY.setText((obj.getY() + ((Line) shape).getEndY())+"");
    					
    				initGUI(2);
    			}
    			if(s instanceof Arc){
    				if(s.isFilled()){
    					lblTitle.setText("Filled Arc");
    				} else lblTitle.setText("Arc");
    				fldArcAngle.setValue(((Arc)shape).getArcAngle());
    				fldStartAngle.setValue(((Arc)shape).getStartAngle());
    				initGUI(3);
    			}
    			fldColour.setText("#" + Integer.toHexString(shape.getColor().getRed()) + Integer.toHexString(shape.getColor().getGreen()) + Integer.toHexString(shape.getColor().getBlue()));
    			fldColour.setEditable(false);
    			fldTransparency.setValue(shape.getTransparency());
    			c_Fixed.setSelected(shape.isFixed());
    		    fldLinetype.setValue(s.getLineType());
    		    fldStroke.setValue(s.getStrokeWidth());
    		}
    	}    	
    }
   
    private boolean validateInput(){
    	boolean res = true;
    	Integer v;
    	if(((Number)fldTransparency.getValue()).intValue()  > 255){
    		addErrorPanel("'Transparency'value must be between 0 and 255");
    		res = false;
    	}
    	if(!((v = tryParseNull(fldWidth.getText())) != null && v > 0) || 
    			!(( v = tryParseNull(fldHeight.getText())) != null && v > 0)){    			   		
    		addErrorPanel("Negative or non numeric values not allowed");
    		res = false;
    	}
    	
    	//System.out.println("Validate = "+res+"; w = "+ tryParse(fldWidth.getText()));
    	return res;
    }
    
    private boolean validateCoords(){
    	boolean res = true;
    	Integer v;    	
    	if(!((v = tryParseNull(fldX.getText())) != null && v > 0) || 
    			!(( v = tryParseNull(fldY.getText())) != null && v > 0)){    			   		
    		addErrorPanel("Negative or non numeric values not allowed");
    		res = false;
    	}
    	
    	//System.out.println("Validate = "+res+"; w = "+ tryParse(fldWidth.getText()));
    	return res;
    }
    
    
    private boolean validateLineInput(){
    	boolean res = true;
    	Integer v;
    	if(!((v = tryParseNull(fldStartX.getText())) != null && v > 0) ||
    			!((v = tryParseNull(fldStartY.getText())) != null && v > 0) ||
    			!((v = tryParseNull(fldEndX.getText())) != null && v > 0) ||
    			!(( v = tryParseNull(fldEndY.getText())) != null && v > 0)){    			   		
    		addErrorPanel("Negative or non numeric values not allowed");
    		res = false;
    	}
    	return res;
    }
    
    private boolean validateArcInput(){
    	boolean res = true;
    	if(fldArcAngle.getValue() == null || fldStartAngle.getValue() == null){
    		addErrorPanel("'Start Angle' and 'Arc Angle' are mandatory for Arc Shape");
    		return false;
    	}
    	int arc = ((Number)fldArcAngle.getValue()).intValue();
    	if(arc < -360 || arc > 360){
    		addErrorPanel("Arc Angle value must be between -360 and 360");
    		return false;
    	}
    	arc = ((Number)fldStartAngle.getValue()).intValue();
    	if(arc < 0 || arc > 360){
    		addErrorPanel("Start Angle value must be between 0 and 360");
    		return false;
    	}
    	return res;
    }
        
    /**
     * Action listener.
     * @param evt ActionEvent - action event.
     */
    public void actionPerformed( ActionEvent evt ) {
        if ( evt.getSource() == bttnCancel ) {
            dispose();
        } 
        if ( evt.getSource() == colors ) {
        	 col = JColorChooser.showDialog(ClassEditor.getInstance(), "Choose Color",
                     Color.black);
        	 if(col == null){
        		 col = Color.BLACK;
        	 }
        	 fldColour.setText("#" + Integer.toHexString(col.getRed()) + Integer.toHexString(col.getGreen()) + Integer.toHexString(col.getBlue()));
        } 
        else if ( evt.getSource() == bttnOk && validateInput()) {
        	
        	ClassCanvas canvas = editor.getCurrentCanvas();
        	obj.setWidth(tryParse(fldWidth.getText()));
        	shape.setWidth(obj.getWidth());
        	obj.setHeight(tryParse(fldHeight.getText()));
        	shape.setHeight(obj.getHeight());
        	obj.setX(((Number)fldX.getValue()).intValue());
        	obj.setY(((Number)fldY.getValue()).intValue());
        	shape.setLineType(((Number)fldLinetype.getValue()).floatValue());
        	shape.setStrokeWidth(((Number)fldStroke.getValue()).floatValue());        	
        	shape.setFixed(c_Fixed.isSelected());
        	if(col == null) col = shape.getColor();
        	shape.setColor( Shape.createColorWithAlpha( col, ((Number)fldTransparency.getValue()).intValue() ) );        	
        	
        	if(shape instanceof Line){
        		if(validateLineInput()){
        		int x1 = tryParse(fldStartX.getText()) ;
        		int x2 = tryParse(fldEndX.getText());
        		int y1 = tryParse(fldStartY.getText()) ;
        		int y2 = tryParse(fldEndY.getText());

        		((Line)shape).setStartX(x1>x2?obj.getWidth():0);
        		((Line)shape).setEndX(x1<x2?obj.getWidth():0);
        		((Line)shape).setStartY(0);
        		((Line)shape).setEndY(obj.getHeight());
        		
        		obj.setX(Math.min(x1, x2));
            	obj.setY(Math.min(y1, y2));
        		} else return;
         	}
        	if(shape instanceof Arc){
        		if(validateArcInput()){
        			((Arc)shape).setArcAngle(((Number)fldArcAngle.getValue()).intValue());
        			((Arc)shape).setStartAngle(((Number)fldStartAngle.getValue()).intValue());
        		} else return;
        	}
            canvas.drawingArea.repaint();
            editor.repaint();
            dispose();
         }                   
    }

    /* safe parser*/
    private int tryParse(String s){
    	if(s==""){
    		return 1;
    	}
    	try {
    		int i = Integer.parseInt(s);
    		return i;
    	} catch (Exception e) {
			return 1;
		}  	
    }
    
    /* Null parser*/
    private Integer tryParseNull(String s){
    	if(s==""){
    		return null;
    	}
    	try {
    		int i = Integer.parseInt(s);
    		return i;
    	} catch (Exception e) {
			return null;
		}  	
    }
    
    
    protected void updateLabel (String name) {    	
    	if(validateInput() && validateLineInput()){    		
    		clearAll();
    		System.out.println("Text Width = "+ fldWidth.getText() + " int width = " + tryParse(fldWidth.getText()));	
    	int x1 = tryParse(fldStartX.getText()) ;
		int x2 = tryParse(fldEndX.getText());
		
    		if(name == "width" && !fldWidth.hasFocus()){
    			
    			fldWidth.setText(Math.max(x1, x2) - Math.min(x1, x2)+"");
    		}
    		if(name == "height" && !fldHeight.hasFocus()){
    			fldHeight.setText((int) Math.abs(tryParse(fldStartY.getText()) - tryParse(fldEndY.getText()))+"");
    		}
    		if(name == "x" && fldWidth.hasFocus()){    	
    			if(x1<x2) fldEndX.setText((int) (x1 + tryParse(fldWidth.getText())) + "");
    			else fldEndX.setText((int) (x1 - tryParse(fldWidth.getText())) + "");  				
    		}
    		if(name == "y" && fldHeight.hasFocus()){
    			fldEndY.setText((int) Math.abs(tryParse(fldStartY.getText()) + tryParse(fldHeight.getText())) + "");
    		}    		
    	} 
    }
    
    private void addErrorPanel(String errorMessage){
    	
    	JTextArea msg = new JTextArea(errorMessage);
    	msg.setPreferredSize(new Dimension(150,50));
    	msg.setWrapStyleWord(true);
    	msg.setLineWrap(true);
    	msg.setEditable(false);
    	msg.setFont( new Font("Arial", Font.BOLD, 12));
    	msg.setForeground(Color.RED);
    	pnlErrors.removeAll();
    	pnlErrors.add(msg);
    	pnlErrors.revalidate();
    	getContentPane().repaint();
    }
	
    private void clearAll(){
    	pnlErrors.removeAll();
    	pnlErrors.revalidate();
    	getContentPane().repaint();
    }
    
	private class onChangeListner implements DocumentListener{
		  String label;
		  public void changedUpdate(DocumentEvent e) {
		      //updateLabel(label);
		  }
		  public void removeUpdate(DocumentEvent e) {
			  updateLabel(label);			  
		  }
		  public void insertUpdate(DocumentEvent e) {
			 updateLabel(label);
		  }
		  public onChangeListner(String label){
			  this.label = label;			  
		  }
	}
	
}
/**
 * if(((Line)shape).getStringX1() != null && ((Line)shape).getStringX1() != ""){
    					fldStartX.setText(((Line)shape).getStringX1());
    				} else {
    					fldStartX.setText(obj.getX()+"");
    				}    	
    				if(((Line)shape).getStringY1() != null && ((Line)shape).getStringY1() != ""){
    					fldStartY.setText(((Line)shape).getStringY1());
    				} else {
    					fldStartY.setText(obj.getY()+"");
    				}  
    				if(((Line)shape).getStringX2() != null && ((Line)shape).getStringX2() != ""){
    					fldEndX.setText(((Line)shape).getStringX2());
    				} else {
    					fldEndX.setText(((Line)shape).getEndX()+"");
    				}  
    				if(((Line)shape).getStringY2() != null && ((Line)shape).getStringY2() != ""){
    					fldEndY.setText(((Line)shape).getStringY2());
    				} else {
    					fldEndY.setText(((Line)shape).getEndY()+"");
    				}      
    				*/
