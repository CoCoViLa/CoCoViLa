package ee.ioc.cs.vsle.classeditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ee.ioc.cs.vsle.common.ops.State;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.editor.PaletteBase;
import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.Image;
import ee.ioc.cs.vsle.graphics.Text;
import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.Port;

/**
 * Class Editor toolbar.
 */
public class IconPalette extends PaletteBase {

	ClassEditor editor;

	protected ClassCanvas canvas;
	
    // Labels
    JLabel lblLineWidth = new JLabel(" Size: ");
    JLabel lblLineType;
    JLabel lblTransparency;
    JLabel lblZoom;

    // Spinners
    Spinner spinnerLineWidth = new Spinner(1, 10, 1,1);
    Spinner spinnerTransparency = new Spinner(1, 255, 1,255);
    Spinner spinnerLineType = new Spinner(0,100,1, 0);

    // Buttons
    JToggleButton selection;
    JToggleButton boundingbox;
    JToggleButton text;
    JToggleButton line;
    JToggleButton arc;
    JToggleButton filledarc;
    JToggleButton rectangle;
    JToggleButton filledrectangle;
    JToggleButton oval;
    JToggleButton filledoval;
    JToggleButton freehand;
    JToggleButton eraser;
    JToggleButton colors;
    JToggleButton addport;
    JToggleButton image;
    JButton props;

//    public IconPalette(IconMouseOps mListener, ClassEditor ed, Canvas canv) {
    public IconPalette(MouseOps mListener, ClassEditor ed, ClassCanvas canv) {    	
        super();
        
        this.canvas = canv;
        
        this.canvas.iconPalette = this;
        
        toolBar = new JToolBar();

        this.editor = ed;

        spinnerLineWidth.setPreferredSize(new Dimension(40, 20));
        spinnerLineWidth.setMaximumSize(spinnerLineWidth.getPreferredSize());
        spinnerLineWidth.setBorder(BorderFactory.createLineBorder(java.awt.Color.white,0));

        spinnerTransparency.setPreferredSize(new Dimension(45, 20));
        spinnerTransparency.setMaximumSize(spinnerTransparency.getPreferredSize());
        spinnerTransparency.setBorder(BorderFactory.createLineBorder(java.awt.Color.white,0));

        spinnerLineType.setPreferredSize(new Dimension(40,20));
        spinnerLineType.setMaximumSize(spinnerLineType.getPreferredSize());
        spinnerLineType.setBorder(BorderFactory.createLineBorder(java.awt.Color.white,0));

        // Action listener added as anonymous class.
        ChangeListener listener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SpinnerModel source = (SpinnerModel) e.getSource();
                try {
                	canvas.mListener.changeStrokeWidth(Float.parseFloat(String.valueOf(source.getValue())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // Action listener added as anonymous class.
        ChangeListener transpListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SpinnerModel source = (SpinnerModel) e.getSource();
                try {
                	canvas.mListener.changeTransparency(Integer.parseInt(String.valueOf(source.getValue())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        // Action listener added as anonymous class.
        ChangeListener lineTypeListener = new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                SpinnerModel source = (SpinnerModel) e.getSource();
                try {
                	canvas.mListener.changeLineType(Integer.parseInt(String.valueOf(source.getValue())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        // add action listeners to spinners.
        spinnerLineWidth.getModel().addChangeListener(listener);
        spinnerTransparency.getModel().addChangeListener(transpListener);
        spinnerLineType.getModel().addChangeListener(lineTypeListener);

        selection = createButton("images/mouse.gif", "Select", State.selection);
        boundingbox = createButton("images/boundingbox.gif", "Bounding Box", State.boundingbox);
        addport = createButton("images/port.gif", "Add Port", State.addPort);
        image = createButton("images/image.png", "Insert Image", State.insertImage);
        text = createButton("images/text.gif", "Text", State.drawText);
        line = createButton("images/line.gif", "Line", State.drawLine);
        arc = createButton("images/arc.gif", "Arc", State.drawArc);
        filledarc = createButton("images/fillarc.gif", "Filled arc", State.drawFilledArc);
        rectangle = createButton("images/rect.gif", "Rectangle", State.drawRect);
        filledrectangle = createButton("images/fillrect.gif", "Filled rectangle", State.drawFilledRect);
        oval = createButton("images/oval.gif", "Oval", State.drawOval);
        filledoval = createButton("images/filloval.gif", "Filled oval", State.drawFilledOval);
//        freehand = createButton("images/freehand.gif", "Freehand drawing", State.freehand);
        eraser = createButton("images/eraser.gif", "Eraser", State.eraser);
        colors = createButton("images/colorchooser.gif", "Color chooser", State.chooseColor);    

        for (JToggleButton b : buttons) {
            toolBar.add(b);
        }
        
        props = createBttnProp("images/prop.gif", "Selected Shape Properties");   
        toolBar.add(props);

        lblLineWidth.setToolTipText("Line width or point size of a selected tool");
        toolBar.add(lblLineWidth);
        toolBar.add(spinnerLineWidth);

        Icon icon = FileFuncs.getImageIcon("images/transparency.gif", false);
        lblTransparency = new JLabel(icon);
        lblTransparency.setToolTipText("Object transparency percentage");

        icon = FileFuncs.getImageIcon("images/zoom.gif", false);
        lblZoom = new JLabel(icon);
        lblZoom.setToolTipText("Object zoom percentage");

        icon = FileFuncs.getImageIcon("images/linetype.gif", false);
        lblLineType = new JLabel(icon);
        lblLineType.setToolTipText("Dash style");

        toolBar.add(lblTransparency);
        toolBar.add(spinnerTransparency);

        toolBar.add(lblZoom);
        toolBar.add(createZoomPanel());

        toolBar.add(lblLineType);
        toolBar.add(spinnerLineType);

        canvas.add(toolBar, BorderLayout.PAGE_START);
        canvas.revalidate();        
    }

    protected JButton createBttnProp(String iconPath, String descr) {

    	ImageIcon icon = FileFuncs.getImageIcon(iconPath, false);
        JButton button = new JButton(icon);
        button.setSelectedIcon(icon);      
        button.setToolTipText(descr);

        // Palette buttons should be smaller, that's what JToolBar is doing
        button.setMargin(BUTTON_BORDER);

        
        button.addMouseListener(getButtonMouseListener());
        
        button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					if(canvas.getObjectList().getSelectedCount() == 1){
						ArrayList<GObj> ol = canvas.getObjectList().getSelected();
						Port port = null;
						for ( GObj o : ol ) {
								  if(o.getPortList() != null && o.getPortList().size() == 1){
									  port = o.getPortList().get(0);									 
								  }
						} if (port != null){
							new PortPropertiesDialog( ClassEditor.getInstance(), port ).setVisible( true );
						} else {
							GObj obj = canvas.getObjectList().getSelected().get(0);
							if(obj.getShapes() != null && obj.getShapes().get(0) instanceof BoundingBox && canvas != null ){     
					            if(canvas.getClassObject() != null)            	 
					            	  new ClassPropertiesDialog( canvas.getClassObject().getDbrClassFields(), true );
					            else  new ClassPropertiesDialog( new ClassFieldTable(), true );   
					            	  canvas.updateBoundingBox();   
							} else if(obj.getShapes() != null && obj.getShapes().get(0) instanceof Text){
								new TextDialog( ClassEditor.getInstance(), obj ).setVisible( true );
							} else if (obj.getShapes() != null && obj.getShapes().get(0) instanceof Image){
								new ImageDialog( ClassEditor.getInstance(), obj).setVisible( true );
							} else  new ShapePropertiesDialog(ClassEditor.getInstance(), obj).setVisible( true ); 						
						}
					}
					else {
						JOptionPane.showMessageDialog( null, "Please select single object", "Object undefined",
								  JOptionPane.INFORMATION_MESSAGE );
					}
				} catch (NullPointerException e) {
					
				}
				
			}
		});
       
        return button;
    }
    
    @Override
    protected ActionListener getZoomListener() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int i = ((JComboBox) e.getSource()).getSelectedIndex();
                editor.getCurrentCanvas().setScale(ZOOM_LEVELS[i]);
            }
        };
    }
    
    @Override
    protected void destroy() {
    	super.destroy();
    }

    @Override
    protected void setState(String state) {
        canvas.mListener.setState(state);
    }
} // end of class
