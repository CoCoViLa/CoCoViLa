package ee.ioc.cs.vsle.classeditor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.editor.State;
import ee.ioc.cs.vsle.graphics.Arc;
import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.Image;
import ee.ioc.cs.vsle.graphics.Line;
import ee.ioc.cs.vsle.graphics.Oval;
import ee.ioc.cs.vsle.graphics.Rect;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.graphics.Text;
import ee.ioc.cs.vsle.vclass.ClassGraphics;
import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.ObjectList;
import ee.ioc.cs.vsle.vclass.Point;
import ee.ioc.cs.vsle.vclass.Port;

/**
 * Mouse operations on Canvas.
 */
public class MouseOps extends MouseInputAdapter {

    String state = State.selection;
    String mouseState = "";
    int startX, startY;
    boolean mouseOver;

    public int arcWidth, arcHeight;
    public boolean fill = false;
    public float strokeWidth = 1.0f;
    public int transparency = 255;
    public int lineType = 0;
    
    public Color color = Color.black;
    boolean dragged = false;
    public int arcStartAngle;
    public int arcAngle;
    
    private Canvas canvas;
    private Point draggedBreakPoint;
    private GObj draggedObject;
    private int cornerClicked;
    private Port currentPort;

    public MouseOps( Canvas e ) {
        this.canvas = e;
    }
    
    public int getTransparency() {
        return this.transparency;
    }

    public int getLineType() {
        return this.lineType;
    }    

    public void changeObjectColors( Color col ) {
    	ArrayList<GObj> selectedObjs = canvas.getScheme().getObjectList().getSelected();
    	for (GObj gObj : selectedObjs) {
    		for (Shape s : gObj.getShapes()) {
    			s.setColor( col );
    			canvas.drawingArea.repaint();
			}
		}
    } // change object colors
    
    public void setState( String state ) {
    	System.out.println("MouseOps setState " + state);
    	
        if (State.chooseColor.equals(state)) {
            Color col = JColorChooser.showDialog(ClassEditor.getInstance(), "Choose Color",
                    Color.black);

            // col is null when the dialog was cancelled or closed
            if (col != null) {
                this.color = col;
                changeObjectColors(col);
            }

            canvas.iconPalette.resetButtons();
            this.state = State.selection;
        }
        else {
	        if ( canvas.currentCon != null || canvas.getCurrentObj() != null ) {
	            canvas.cancelAdding();
	            if ( currentPort != null ) {
	                currentPort.setSelected( false );
	                currentPort = null;
	            }
	        }
	
	        assert currentPort == null;
	        assert canvas.currentCon == null;
	        assert canvas.getCurrentObj() == null;
	        assert canvas.currentPainter == null;
	
	        this.state = state;
        }
        
        if ( State.addRelation.equals( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
        } else if ( State.selection.equals( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            canvas.iconPalette.resetButtons();
        } else if ( State.isAddRelClass( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
        } else if ( State.isAddObject( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            canvas.startAddingObject();
        }        
    }

    private void openTextEditor( int x, int y ) {
        new TextDialog( ClassEditor.getInstance(), x, y ).setVisible( true );
    } // openTextEditor
    
    
    /**
     * Open the dialog for specifying port properties. Returns port to the
     * location the dialog was opened from. Dialog is modal and aligned
     * to the center of the open application window.
     */
    private void openPortPropertiesDialog() {
        new PortPropertiesDialog( ClassEditor.getInstance(), null ).setVisible( true );
    } // openPortPropertiesDialog    
    
    /**
     * Draws port on the drawing area of the ClassEditor.
     * @param portName - name of the port.
     * @param isAreaConn - port is area connectible or not.
     * @param isStrict - port is strict or not.
     * @param portType - type of port: Integer, String, Object
     */
    public void drawPort( String portName, boolean isAreaConn, boolean isStrict, String portType, boolean isMulti ) {
        ArrayList<Port> ports = new ArrayList<Port>();
        String portConnection = null;
        if (isAreaConn) portConnection = "area";
        Port p = new Port(portName, portType, 0, 0, portConnection, isStrict, isMulti);
        ports.add(p);
        
        GObj obj = new GObj();
        p.setObject(obj);
        obj.setX(canvas.mouseX);
        obj.setY(canvas.mouseY);
        
        obj.setHeight(p.getHeight());
        obj.setWidth(p.getWidth());     
        obj.setName("port");            
        obj.setPorts(ports);
        System.out.println("GObj " + obj.toString());
        
        canvas.addObject(obj);  
        canvas.repaint();
    } // drawPort
    
    public void drawPort( Port p, int xOffset, int yOffset ) {
    	
    	//xOffset = 0; yOffset = 0;
        ArrayList<Port> ports = new ArrayList<Port>();
               
        ports.add(p);
        //p.setX();
        
        GObj obj = new GObj();
        p.setObject(obj);
        obj.setX(p.getX() + xOffset); // 
        obj.setY(p.getY() + yOffset); // 
        p.setX(0);
        p.setY(0);      

        p.setDefaultGraphics(obj.isDrawOpenPorts());
               
        obj.setHeight(p.getHeight());
        obj.setWidth(p.getWidth());     
        obj.setName("port");        
    //    obj.set
        obj.setPorts(ports);       
        
        
        System.out.println("GObj " + obj.toString() + "; x=" + obj.getX() + "; y=" + obj.getY());

        canvas.addObject(obj);
        canvas.repaint();
    } // drawPort    
     
    public void repaintPort( Port p, ClassGraphics graphics, boolean openFlag ) {
    	
    // cleanup graphics code	
    
     if(graphics.getShapes().get(0) != null){ 	
    	 graphics.getShapes().get(0).setX(0);
    	 graphics.getShapes().get(0).setY(0);
     } else return;
   	 
     if(openFlag){            		 
   		 p.setOpenGraphics(graphics);
   	 } else {
   		 p.setClosedGraphics(graphics);
   	 }
        p.setX(0);
        p.setY(0);    
        
              
        canvas.repaint();
    } 
    
    /**
     * Draws text on the drawing area of the IconEditor.
     * @param font Font - font used for drawing the text.
     * @param color Color - font color.
     * @param text String - the actual string of text drawn.
     */
    public void drawText( Font font, Color color, String text, int x, int y ) {
        Text t = new Text( x, y, font, Shape.createColorWithAlpha( color, getTransparency() ), text );
        addShape(t);
        
        canvas.drawingArea.repaint();
    } // drawText   
    
    public void changeTransparency( int transparencyPercentage ) {
    	this.transparency = transparencyPercentage;
    	ArrayList<GObj> selectedObjs = canvas.getScheme().getObjectList().getSelected();
    	for (GObj gObj : selectedObjs) {
    		for (Shape s : gObj.getShapes()) {
    			s.setColor( Shape.createColorWithAlpha( s.getColor(), transparency ) );
    			canvas.drawingArea.repaint();
			}
		}
    }

    /**
     * Change line type of the selected shape(s).
     * @param lineType - selected line type icon name.
     */
    public void changeLineType( int lineType ) {
    	this.lineType = lineType;
    	ArrayList<GObj> selectedObjs = canvas.getScheme().getObjectList().getSelected();
    	for (GObj gObj : selectedObjs) {
    		for (Shape s : gObj.getShapes()) {
    			s.setLineType( lineType );
    			canvas.drawingArea.repaint();
			}
		}
    } // changeLineType

    /**
    * Change the stroke with of the selected shape(s).
    * @param strokeW double - stroke width selected from the spinner.
    */
    public void changeStrokeWidth( float strokeW ) {
    	this.strokeWidth = strokeW;
    	ArrayList<GObj> selectedObjs = canvas.getScheme().getObjectList().getSelected();
    	for (GObj gObj : selectedObjs) {
    		for (Shape s : gObj.getShapes()) {
    			s.setStrokeWidth( strokeWidth );
    			canvas.drawingArea.repaint();
			}
		}    	
    } // changeStrokeWidth    
    
    public void addShape(Shape s) {
    	addShape(s, 0, 0);
    }
    
    public void addShape(Shape s, int xOffset, int yOffset) {

    	
    	/// test values
   /* 	 xOffset = 0; 
    	 yOffset = 0;*/
    	
        System.out.println("1. MouseOps.addShape() " + s.toText());
        System.out.println("2. addShape x " + s.getX() + " y " + s.getY() + " h " + s.getHeight() + " w " + s.getWidth());

        ArrayList<Shape> shapes = new ArrayList<Shape>();
        shapes.add(s);

        GObj obj = new GObj();        
        obj.setX(s.getX() + xOffset);
        obj.setY(s.getY() + yOffset);
        obj.setHeight(s.getHeight());
        obj.setWidth(s.getWidth()); 
        
        if (s instanceof Rect || s instanceof Oval || s instanceof Arc || s instanceof Line
        		|| s instanceof BoundingBox) {
            if (s.getHeight() == 0 || s.getWidth() == 0) {
            	return;
            }        	
        }
        
        if (s instanceof Rect || s instanceof Oval || s instanceof Arc
        		|| s instanceof BoundingBox || s instanceof Text || s instanceof Image) {
	        s.setX(0);
	        s.setY(0);
        }
        else if (s instanceof Line) {
        	int minX = Math.min( ((Line) s).getStartX(), ((Line) s).getEndX() );
        	int minY = Math.min( ((Line) s).getStartY(), ((Line) s).getEndY()  );
        	
        	if (xOffset == 0) obj.setX(minX);
        	if (yOffset == 0) obj.setY(minY);
            
            double k = ((Line) s).getK();

            if (k >= 0) {
	            ((Line) s).setStartX(0);
	            ((Line) s).setStartY(0);
	            ((Line) s).setEndX(s.getWidth());
	            ((Line) s).setEndY(s.getHeight());
            } else {
            	((Line) s).setStartX(s.getWidth());
	            ((Line) s).setStartY(0);
	            ((Line) s).setEndX(0);
	            ((Line) s).setEndY(s.getHeight());
            }
        }        

        System.out.println("OBJECT X,Y,H,W " + obj.getX() + ", " + obj.getY()+ ", " + obj.getHeight()+ ", " + obj.getWidth());
        System.out.println("////////// shape " + s.toText());
  
        if (s instanceof Text) {
//        	obj.setHeight(15);
//            obj.setWidth(50); 
        	obj.setHeight(75);
            obj.setWidth(75); 
        }
        
        obj.setName(s.getClass().getName());
        obj.setShapes(shapes);

        canvas.addObject(obj);
    }
    /**
     * Mouse entered event from the MouseMotionListener. Invoked when the mouse
     * enters a component.
     * 
     * @param e MouseEvent - Mouse event performed.
     */
    @Override
    public void mouseEntered( MouseEvent e ) {
        mouseOver = true;
    }

    /**
     * Mouse exited event from the MouseMotionListener. Invoked when the mouse
     * exits a component.
     * 
     * @param e MouseEvent - Mouse event performed.
     */
    @Override
    public void mouseExited( MouseEvent e ) {
        mouseOver = false;
        canvas.drawingArea.repaint();
    }

    private void openObjectPopupMenu( GObj obj, int x, int y ) {
        ObjectPopupMenu popupMenu = new ObjectPopupMenu( obj, canvas );
        popupMenu.show( canvas, x, y );
    }
    
    private void openPortPopupMenu( IconPort port, int x, int y ) {
        IconPortPopupMenu popupMenu = new IconPortPopupMenu( port, ClassEditor.getInstance() );
        popupMenu.show( canvas, x, y );
    } // openPortPopupMenu    

    /**
     * Mouse clicked event from the MouseListener. Invoked when the mouse button
     * has been clicked (pressed and released) on a component.
     * @param e MouseEvent - Mouse event performed. In the method a distinction
     *                       is made between left and right mouse clicks.
     */
    @Override
    public void mouseClicked( MouseEvent e ) {
    	System.out.println("IconMouseOps mouseClicked: " + state );
        int x, y;
        x = e.getX();
        y = e.getY();

        if ( state.equals( State.drawArc1 ) ) {
            setState( State.drawArc2 );
            double legOpp = startY + arcHeight / 2 - y;
            double legNear = x - ( startX + arcWidth / 2 );
            arcStartAngle = (int) ( Math.atan( legOpp / legNear ) * 180 / Math.PI );
            if ( legNear < 0 )
                arcStartAngle = arcStartAngle + 180;
            if ( legNear > 0 )
                arcStartAngle = arcStartAngle + 360;
            if ( arcStartAngle > 360 )
                arcStartAngle = arcStartAngle - 360;
            return;
        }
        if ( state.equals( State.drawArc2 ) ) {
            Arc arc = new Arc( startX, startY, arcWidth, arcHeight, arcStartAngle, arcAngle, 
                    Shape.createColorWithAlpha( color, getTransparency() ), fill, strokeWidth, lineType );
            addShape( arc );
            setState( State.selection );
        }
        // LISTEN RIGHT MOUSE BUTTON
        if ( SwingUtilities.isRightMouseButton( e ) ) {
            GObj obj = canvas.getObjectList().checkInside( x, y, canvas.getScale() );
            if ( obj != null || canvas.getObjectList().getSelectedCount() > 1 ) {
            	if (obj.getPortList() != null && !obj.getPortList().isEmpty()) {
            		Port port = obj.getPortList().get(0);
            		IconPort p = new IconPort(port.getName(), port.getX(), port.getY(), port.isArea(), port.isStrict(), port.isMulti());
            		p.setType(port.getType());
            		openPortPopupMenu( p, e.getX() + canvas.drawingArea.getX(), e.getY() + canvas.drawingArea.getY() );
            	} else { 
            		openObjectPopupMenu( obj, e.getX() + canvas.drawingArea.getX(), e.getY() + canvas.drawingArea.getY() );
            	}
            }        	
        } // END OF LISTENING RIGHT MOUSE BUTTON
        else {
            if ( state.equals( State.selection ) ) {
                // **********Selecting objects code*********************
                if ( !e.isShiftDown() ) {
                    canvas.getObjectList().clearSelected();
                    canvas.getConnections().clearSelected();
                }
                
                GObj obj = canvas.getObjectList().checkInside(x, y, canvas.getScale());
                
             //   ObjectList testobj = canvas.getObjectList();
                
                if ( obj != null ) {
                    obj.setSelected( true );
                    if (SwingUtilities.isMiddleMouseButton(e)) {
                    	if (ClassEditor.className != null)
                    	canvas.openClassCodeViewer(ClassEditor.className);
                    } else {
                        canvas.setCurrentObj( obj );
                    }
                }

            } else {
                if ( canvas.getCurrentObj() != null ) {
                    canvas.addCurrentObject();
                    setState( State.selection );
                }
            }
        } // END OF LISTENING LEFT MOUSE BUTTON

        canvas.drawingArea.repaint();
    }
    

    @Override
    public void mousePressed( MouseEvent e ) {
    	System.out.println("MouseOps mousePressed " + state);
    	mouseState = "pressed";
        if ( !( state.equals( State.drawArc1 ) || state.equals( State.drawArc2 ) ) ) {
            startX =  Math.round( e.getX() / canvas.getScale() );
            startY =  Math.round( e.getY() / canvas.getScale() );
        }
        
        if ( state.equals( State.selection ) ) {
            GObj obj = null;
            canvas.mouseX = Math.round( e.getX() / canvas.getScale() );
            canvas.mouseY = Math.round( e.getY() / canvas.getScale() );
            Connection con = canvas.getConnectionNearPoint( canvas.mouseX, canvas.mouseY );

            obj = canvas.getObjectList().checkInside(canvas.mouseX, canvas.mouseY, 1);

            if ( obj != null ) {
                if ( e.isShiftDown() ) {
                    obj.setSelected( true );
                } else {
                    if ( !obj.isSelected() ) {
                        canvas.getObjectList().clearSelected();
                        obj.setSelected( true );
                    }
                }
                System.out.println("IconMouseOps: " + SwingUtilities.isLeftMouseButton( e ) );
                if ( SwingUtilities.isLeftMouseButton( e ) ) {
                    setState( State.drag );
                    draggedObject = obj;
                }
                canvas.drawingArea.repaint();
            } else if ( con == null ) {
                cornerClicked = canvas.getObjectList().controlRectContains(
                        canvas.mouseX, canvas.mouseY);
                if ( cornerClicked != 0 ) {
                    setState( State.resize );
                } else {
                    setState( State.dragBox );
                    startX = canvas.mouseX;
                    startY = canvas.mouseY;
                }
            }
        }
        canvas.setActionInProgress( true );
    }

    

    
    @Override
    public void mouseDragged( MouseEvent e ) {
    	System.out.println("MouseOps mouseDragged " + state);
        if ( !SwingUtilities.isLeftMouseButton( e ) ) {
            return;
        }
        int x = Math.round( e.getX() / canvas.getScale() );
        int y = Math.round( e.getY() / canvas.getScale() );

        canvas.setPosInfo( x, y );

        if ( State.drag.equals( state ) ) {
            int moveX, moveY;

            if ( RuntimeProperties.getSnapToGrid() ) {
                GObj obj = draggedObject;
                int step = RuntimeProperties.getGridStep();

                // When snap to grid is on mouse coordinates are calculated
                // as if the mouse jumped from one grid line to the next.
                // The dragged object's top left corner is always at a grid
                // line intersection point. The relative positions of the
                // dragged class and other selected objects should be constant.
                moveX = Math.round( (float) ( obj.getX() + Math.round( (float) x / step ) * step - canvas.mouseX ) / step )
                        * step - obj.getX();
                moveY = Math.round( (float) ( obj.getY() + Math.round( (float) y / step ) * step - canvas.mouseY ) / step )
                        * step - obj.getY();
            } else {
                moveX = x - canvas.mouseX;
                moveY = y - canvas.mouseY;
            }

            // If there are strict ports being moved find the first one that
            // would create a new connection and snap it to the other port.
            ArrayList<GObj> selected = canvas.getObjectList().getSelected();
            SNAP: for ( GObj obj : selected ) {
                if ( obj.isStrict() ) {
                    for ( Port port1 : obj.getPortList() ) {
                        if ( port1.isStrict() ) {
                            Point p1 = obj.toCanvasSpace( port1.getRealCenterX(), port1.getRealCenterY() );
                            Port port2 = canvas.getObjectList().getPort(
                                    p1.x + moveX, p1.y + moveY, obj);

                            if ( port2 != null && !selected.contains( port2.getObject() ) && port1.canBeConnectedTo( port2 )
                                    && ( !port1.isConnectedTo( port2 ) || port1.isStrictConnected() ) ) {

                                Point p2 = port2.getObject().toCanvasSpace( port2.getRealCenterX(), port2.getRealCenterY() );
                                
                                moveX = p2.x - p1.x;

                                moveY = p2.y - p1.y;

                                break SNAP;
                            }
                        }
                    }
                }
            }
         
            canvas.moveObjects( moveX, moveY );
            canvas.mouseX += moveX;
            canvas.mouseY += moveY;
            
            
            canvas.drawingArea.setAutoscrolls(true);
            canvas.areaScrollPane.setAutoscrolls(true);
            
           

            /* Scroll check */            
            
            int compareHeight =  Math.round( canvas.mouseY * canvas.getScale() ); 
            int compareWidth =  Math.round( canvas.mouseX * canvas.getScale() ); 
            
            ObjectList olist = canvas.getObjectList();
            for ( GObj obj : olist) {
            	if ((obj.getX() + obj.getWidth()) > compareWidth) {
            		compareWidth = obj.getX() + obj.getWidth();
            	}
            	if ((obj.getY() + obj.getHeight()) > compareHeight) {
            		compareHeight = obj.getY() + obj.getHeight();
            	}
            }
            if (selected != null && selected.get(0) != null){
            	compareHeight += selected.get(0).getHeight(); 
            	compareWidth += selected.get(0).getWidth();
            }
            System.out.println("Scroll check vertical: " + canvas.areaScrollPane.getViewport().getSize().getHeight() + " < " + compareHeight);
            System.out.println("Scroll check horizontal: " + canvas.areaScrollPane.getViewport().getSize().getWidth() + " < " + compareWidth);
            
            if(canvas.areaScrollPane.getViewport().getSize().getHeight() <  compareHeight || canvas.areaScrollPane.getViewport().getSize().getWidth() <  compareWidth){
                 	//drawAreaSize
            	Dimension drawAreaSize = new Dimension(10+compareWidth, 10+compareHeight);
            	canvas.drawingArea.setPreferredSize(drawAreaSize);
             	canvas.drawAreaSize.setSize(drawAreaSize);
             	
            	System.out.println("Drawing area new size: " + drawAreaSize.getWidth() + " " + drawAreaSize.getHeight());
             	
             	canvas.drawingArea.revalidate();
            }
           
        	
          /*  java.awt.Point vpp = canvas.areaScrollPane.getViewport().getViewPosition();
            vpp.translate(canvas.mouseX , canvas.mouseY);
            canvas.drawingArea.scrollRectToVisible(new Rectangle(vpp, canvas.areaScrollPane.getViewport().getSize()));*/                       
           
            
        } else if ( State.resize.equals( state ) ) {
            // Do not allow resizing of strictly connected objects as it
            // seems to be not very useful and creates problems such as
            // the connected ports could get misplaced and should be
            // disconnected but maybe that is not what the user is expecting.
            // Until this operation is proven necessary and is clearly specified
            // it is better to deny it.
            if ( !draggedObject.isStrictConnected() ) {
                int moveX = x - canvas.mouseX;
                int moveY = y - canvas.mouseY;
                canvas.resizeObjects( moveX, moveY, cornerClicked );
                canvas.mouseX += moveX;
                canvas.mouseY += moveY;
            }
        } else if ( State.dragBox.equals( state ) ) {
            canvas.mouseX = x;
            canvas.mouseY = y;
        } else if ( state.equals( State.drawLine ) ) {
            canvas.mouseX = x;
            canvas.mouseY = y;
        } else if ( state.equals( State.drawArc ) || state.equals( State.drawFilledArc ) ) {
            fill = false;
            if ( state.equals( State.drawFilledArc ) ) {
                fill = true;
            }
            canvas.mouseX = x;
            canvas.mouseY = y;
        } else if ( state.equals( State.drawText ) ) {
            startX = x;
            startY = y;
        } else if ( state.equals( State.addPort ) ) {
            startX = x;
            startY = y;
        } else if ( state.equals( State.insertImage ) ) {
            startX = x;
            startY = y;
        } else if ( state.equals( State.drawRect ) || state.equals( State.drawFilledRect ) || state.equals( State.boundingbox ) ) {
            fill = false;
            if ( state.equals( State.drawFilledRect ) ) {
                fill = true;
            }
            canvas.mouseX = x;
            canvas.mouseY = y;
        } else if ( state.equals( State.drawOval ) || state.equals( State.drawFilledOval ) ) {
            fill = false;
            if ( state.equals( State.drawFilledOval ) ) {
                fill = true;
            }
            canvas.mouseX = x;
            canvas.mouseY = y;
        } else {
            Connection c = canvas.getConnectionNearPoint(x, y);
            if (c != null) {
                canvas.getConnections().clearSelected();
                c.setSelected(true);
                draggedBreakPoint = new Point(x, y);
                c.addBreakPoint(c.indexOf(x, y), draggedBreakPoint);
                setState(State.dragBreakPoint);
            }
        }
        
        //System.out.println("MouseOps mouseDragged canvas.mouseX, canvas.mouseY " + canvas.mouseX + ", " + canvas.mouseY);
        canvas.drawingArea.repaint();
    }

    @Override
    public void mouseMoved( MouseEvent e ) {
    	System.out.println("MouseOps mouseMoved " + state);
        int x = Math.round( e.getX() / canvas.getScale() );
        int y = Math.round( e.getY() / canvas.getScale() );

        canvas.setPosInfo( x, y );
        
        if ( state.equals( State.drawArc2 ) ) {
            double legOpp = startY + arcHeight / 2 - y;
            double legNear = x - ( startX + arcWidth / 2 );
            arcAngle = (int) ( Math.atan( legOpp / legNear ) * 180 / Math.PI ) - arcStartAngle;
            if ( legNear < 0 )
                arcAngle = arcAngle + 180;
            if ( legNear > 0 )
                arcAngle = arcAngle + 360;
            if ( arcAngle > 360 )
                arcAngle = arcAngle - 360;
            if ( arcAngle < 0 ) {
                arcAngle += 360;

            }
        }        
        if ( state.equals( State.drawArc1 ) || state.equals( State.drawArc2 ) )
        	canvas.drawingArea.repaint();
        
        if ( State.isAddObject( state ) && canvas.getCurrentObj() != null ) {
            // if we're adding a new object...

            if ( RuntimeProperties.getSnapToGrid() ) {
                canvas.getCurrentObj().setX( Math.round( x / RuntimeProperties.getGridStep() ) * RuntimeProperties.getGridStep() );
                canvas.getCurrentObj().setY( Math.round( y / RuntimeProperties.getGridStep() ) * RuntimeProperties.getGridStep() );
            } else {
                canvas.getCurrentObj().setY( y );
                canvas.getCurrentObj().setX( x );
            }

            if ( canvas.getCurrentObj().isStrict() )
                updateStrictPortHilight();

            Rectangle rect = new Rectangle( e.getX() - 10, e.getY() - 10, Math.round( canvas.getCurrentObj().getRealWidth()
                    * canvas.getScale() ) + 10, Math.round( ( canvas.getCurrentObj().getRealHeight() * canvas.getScale() ) + 10 ) );

            canvas.drawingArea.scrollRectToVisible( rect );

            if ( e.getX() + canvas.getCurrentObj().getRealWidth() > canvas.drawAreaSize.width ) {

                canvas.drawAreaSize.width = e.getX() + canvas.getCurrentObj().getRealWidth();

                canvas.drawingArea.setPreferredSize( canvas.drawAreaSize );
                canvas.drawingArea.revalidate();
            }

            if ( e.getY() + canvas.getCurrentObj().getRealHeight() > canvas.drawAreaSize.height ) {

                canvas.drawAreaSize.height = e.getY() + canvas.getCurrentObj().getRealHeight();

                canvas.drawingArea.setPreferredSize( canvas.drawAreaSize );
                canvas.drawingArea.revalidate();
            }

            canvas.drawingArea.repaint();
        }

        canvas.mouseX = x;
        canvas.mouseY = y;
    }

    /**
     * Hilights (selects) strict ports of the current object (the object being
     * added or moved) that are about to be strictly connected at the current
     * location.
     */
    private void updateStrictPortHilight() {
        for ( Port port : canvas.getCurrentObj().getPortList() ) {
            port.setSelected( false );

            Port port2 = canvas.getObjectList().getPort(
                    port.getRealCenterX(), port.getRealCenterY());

            if ( port2 != null && port2.isStrict() && !port2.isStrictConnected() && port.canBeConnectedTo( port2 ) ) {

                // Maybe there is a huge strict port which contains more than
                // one of currentObj's ports. In this case only the first
                // port will be connected an the others should not be
                // hilighted.
                boolean ignore = false;
                for ( Port p : canvas.getCurrentObj().getPortList() ) {
                    if (p.isSelected() && canvas.getObjectList().getPort(
                            p.getRealCenterX(), p.getRealCenterY()) == port2) {
                        ignore = true;
                        break;
                    }

                }

                if ( !ignore ) {
                    port.setSelected( true );
                    canvas.getCurrentObj().setX( canvas.getCurrentObj().getX()
                            + ( port2.getRealCenterX() - port.getRealCenterX() ) );
                    canvas.getCurrentObj().setY( canvas.getCurrentObj().getY()
                            + ( port2.getRealCenterY() - port.getRealCenterY() ) );
                }
            }
        }
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
    	System.out.println("MouseOps mouseReleased: " + state);
    	mouseState = "released";

        if ( state.equals( State.drag ) ) {
        	
            if ( !SwingUtilities.isLeftMouseButton( e ) )
                return;
            state = State.selection;
        } else if ( state.equals( State.resize ) ) {
            state = State.selection;
        } else if ( state.equals( State.dragBox ) ) {
            int x1 = Math.min( startX, canvas.mouseX );
            int x2 = Math.max( startX, canvas.mouseX );
            int y1 = Math.min( startY, canvas.mouseY );
            int y2 = Math.max( startY, canvas.mouseY );
            canvas.getObjectList().selectObjectsInsideBox(
                    x1, y1, x2, y2, e.isShiftDown());
            state = State.selection;
            canvas.drawingArea.repaint();
        }

        if ( state != null && state.equals( State.drawRect ) || state.equals( State.drawFilledRect )
                || state.equals( State.boundingbox ) ) {
            final int width = Math.abs( canvas.mouseX - startX );
            final int height = Math.abs( canvas.mouseY - startY );

            if ( state.equals( State.boundingbox ) ) {
                BoundingBox box = new BoundingBox( Math.min( startX, canvas.mouseX ), Math.min( startY, canvas.mouseY ), width, height );
                addShape(box);
                state = State.selection;
            } else {
//	            System.out.println("startX, startY " + startX + ", " + this.startY);
//	            System.out.println("canvas.mouseX, canvas.mouseY " + canvas.mouseX + ", " + canvas.mouseY);
//	            System.out.println(" Math.min( startX, canvas.mouseX ) " +  Math.min( startX, canvas.mouseX ));
//	            System.out.println(" Math.min( startY, canvas.mouseY ) " + Math.min( startY, canvas.mouseY ));
//	            System.out.println("width, height " + width + "," + height);
//	            System.out.println("fill, strokeWidth, lineType " + this.fill + ", " + this.strokeWidth + ", " + this.lineType);
	            Rect rect = new Rect( Math.min( startX, canvas.mouseX ), Math.min( startY, canvas.mouseY ), width, height, 
	                    Shape.createColorWithAlpha( color, getTransparency() ), fill, strokeWidth, lineType );
	            addShape(rect);
            }
            canvas.drawingArea.repaint();
        } else if ( state.equals( State.drawOval ) || state.equals( State.drawFilledOval ) ) {
            int width = Math.abs( canvas.mouseX - startX );
            int height = Math.abs( canvas.mouseY - startY );
            Oval oval = new Oval( Math.min( startX, canvas.mouseX ), Math.min( startY, canvas.mouseY ), width, height, 
                    Shape.createColorWithAlpha( color, getTransparency() ),
                    fill, strokeWidth, lineType );
            addShape(oval);
            canvas.drawingArea.repaint();
        } else if ( state.equals( State.drawArc ) || state.equals( State.drawFilledArc ) ) {
            arcWidth = Math.abs( canvas.mouseX - startX );
            arcHeight = Math.abs( canvas.mouseY - startY );
            setState( State.drawArc1 );
        } else if ( state.equals( State.drawLine ) ) {
            System.out.println("startX, startY " + startX + ", " + this.startY);
            System.out.println("canvas.mouseX, canvas.mouseY " + canvas.mouseX + ", " + canvas.mouseY);
//            System.out.println(" Math.min( startX, canvas.mouseX ) " +  Math.min( startX, canvas.mouseX ));
//            System.out.println(" Math.min( startY, canvas.mouseY ) " + Math.min( startY, canvas.mouseY ));
            System.out.println("fill, strokeWidth, lineType " + this.fill + ", " + this.strokeWidth + ", " + this.lineType);
            
            Line line = new Line( startX, startY, canvas.mouseX, canvas.mouseY, 
                    Shape.createColorWithAlpha( color, getTransparency() ), strokeWidth, lineType );
            addShape( line );
            canvas.drawingArea.repaint();
        } else if ( state.equals( State.resize ) ) {
            state = State.selection;
        } else if ( state.equals( State.freehand ) ) {
        	// TODO
//        	drawDotOnClick( color );
        } else if ( state.equals( State.eraser ) ) {
        	// select obj and delete
        	GObj obj = canvas.getObjectList().checkInside(canvas.mouseX, canvas.mouseY, canvas.getScale()); 
        	if (obj != null) {
        		obj.setSelected(true);
        		canvas.deleteSelectedObjects();
        	}
            canvas.drawingArea.repaint();
        } else if ( state.equals( State.drawText ) ) {
            openTextEditor( canvas.mouseX, canvas.mouseY );
        } else if ( state.equals( State.addPort ) ) {
            openPortPropertiesDialog();
        } else if ( state.equals( State.insertImage ) ) {
            openImageDialog();
        }
        
        List<GObj> selected = canvas.getObjectList().getSelected();
        if ( selected != null && selected.size() > 0 )
            canvas.setStatusBarText( "Selection: " + selected.toString() );

        canvas.setActionInProgress( false );
    }
    
    /**
     * Open the image dialog. Returns
     * image to the location the dialog was opened from. Dialog
     * is modal and aligned to the center of the open application window.
     */
    private void openImageDialog() {
        if ( ClassEditor.getInstance().checkPackage() )
            new ImageDialog( ClassEditor.getInstance(), null ).setVisible( true );
    } // openTextEditor    

    void destroy() {
        canvas = null;
        draggedBreakPoint = null;
        draggedObject = null;
        currentPort = null;
    }
}
