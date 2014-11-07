package ee.ioc.cs.vsle.classeditor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;

import javax.swing.JPanel;

import ee.ioc.cs.vsle.editor.State;
import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.vclass.Canvas;
import ee.ioc.cs.vsle.vclass.Connection;
import ee.ioc.cs.vsle.vclass.GObj;
import ee.ioc.cs.vsle.vclass.Port;
import ee.ioc.cs.vsle.vclass.RelObj;
import ee.ioc.cs.vsle.vclass.VPackage;
import ee.ioc.cs.vsle.vclass.Canvas.DrawingArea;
import ee.ioc.cs.vsle.vclass.Canvas.MoveEdit;

public class ClassCanvas extends Canvas{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1290323197462938186L;
	public MouseOps mListener; 
    private boolean drawOpenPorts = true;    
    public IconPalette iconPalette;
	public BoundingBox boundingBox;
    

    public ClassCanvas( VPackage _package, String workingDir ) {    	
    	 super(workingDir);
    	 vPackage = _package;
         m_canvasTitle = vPackage.getName();
         initialize();
    }
	
    
    @Override
    protected void initialize() {
    	super.initialize();
        mListener = new MouseOps( this );     
       //DrawingArea 
     //  drawingArea =  getDrawingArea();
       drawingArea.addMouseListener( mListener );
     //   super.drawingArea.add
        drawingArea.addMouseMotionListener( mListener );
    }
    
    @Override
    public DrawingArea getDrawingArea() {
    	drawingArea = new DrawingArea();
    	drawingArea.init();
        return  (DrawingArea) drawingArea;
    }

    public class DrawingArea extends Canvas.DrawingArea {
       
		private static final long serialVersionUID = 7866172800421330602L;

		@Override        
        protected void paintComponent( Graphics g ) {
			super.paintComponent(g);
		        Graphics2D g2 = (Graphics2D) g;
		        
		        //     hide or show BoundingBox
		        iconPalette.boundingbox.setEnabled( !isBBPresent() );
		        
		        // coordinates
		          int rectX = Math.min( mListener.startX, mouseX );
		          int rectY = Math.min( mListener.startY, mouseY );
		          int width = Math.abs( mouseX - mListener.startX );
		          int height = Math.abs( mouseY - mListener.startY );
		        
		        // Draw Shapes
		                
		         if ( mListener.state.equals( State.drawArc1 ) ) {
		             g.drawRect( mListener.startX, mListener.startY, mListener.arcWidth, mListener.arcHeight );
		             g.drawLine( mListener.startX + mListener.arcWidth / 2, mListener.startY + mListener.arcHeight / 2, mouseX,
		                     mouseY );
		         } else if ( mListener.state.equals( State.drawArc2 ) ) {
		             if ( mListener.fill ) {
		                 g2.fillArc( mListener.startX, mListener.startY, mListener.arcWidth, mListener.arcHeight,
		                         mListener.arcStartAngle, mListener.arcAngle );

		             } else {
		                 g2.drawArc( mListener.startX, mListener.startY, mListener.arcWidth, mListener.arcHeight,
		                         mListener.arcStartAngle, mListener.arcAngle );
		             }

			
		}	
		         if ( !mListener.mouseState.equals( "released" ) ) {

		             if ( mListener.state.equals( State.dragBox ) 
		                     || mListener.state.equals( State.boundingbox )) {
		                    g2.setColor( Color.gray );
		                    g2.drawRect( rectX, rectY, width, height );
		                } else {
		                    
		                    int red = mListener.color.getRed();
		                    int green = mListener.color.getGreen();
		                    int blue = mListener.color.getBlue();

		                    int alpha = mListener.getTransparency();
		                    g2.setColor( new Color( red, green, blue, alpha ) );

		                    if ( mListener.lineType > 0 ) {
		                        g2.setStroke( new BasicStroke( mListener.strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND,
		                                50, new float[] { mListener.lineType, mListener.lineType }, 0 ) );
		                    } else {
		                        g2.setStroke( new BasicStroke( mListener.strokeWidth ) );
		                    }
		            
		                    if ( mListener.state.equals( State.drawRect ) ) {
		                        g2.setColor( mListener.color );
		                        g2.drawRect( rectX, rectY, width, height );
		                    } else if ( mListener.state.equals( State.drawFilledRect ) ) {
		                        g2.setColor( mListener.color );
		                        g2.fillRect( rectX, rectY, width, height );    
		                 } else if ( mListener.state.equals( State.drawLine ) ) {
		                     g2.drawLine( mListener.startX, mListener.startY, mouseX, mouseY );                        
		                    } else if ( mListener.state.equals( State.drawOval ) ) {
		                        g2.setColor( mListener.color );
		                        g2.drawOval( rectX, rectY, width, height );
		                    } else if ( mListener.state.equals( State.drawFilledOval ) ) {
		                        g2.setColor( mListener.color );
		                        g2.fillOval( rectX, rectY, width, height );
		                 } else if ( mListener.state.equals( State.drawArc ) ) {
		                     g.drawRect( rectX, rectY, width, height );
		                 } else if ( mListener.state.equals( State.drawFilledArc ) ) {
		                     g.drawRect( rectX, rectY, width, height );
		        }        

		                }
		         }
		}
    }
    public void setDrawOpenPorts(boolean drawOpenPorts) {
		
		this.drawOpenPorts = drawOpenPorts;		
		for (GObj obj : scheme.getObjectList()) {
		    obj.setDrawOpenPorts( drawOpenPorts );
        }
		drawingArea.repaint();
	}
    
    public boolean isDrawOpenPorts() {
		return drawOpenPorts;
	}
    
    public BoundingBox getBoundingBox() {
		return boundingBox;
	}
    
    public boolean isBBPresent() {
        boolean isBbPresent = false;
        for (GObj obj : getObjectList()) {
            for (Shape shape : obj.getShapes()) {
				if (shape instanceof BoundingBox) {
					isBbPresent = true;
					boundingBox = new BoundingBox(obj.getX(),obj.getY(), shape.getWidth(), shape.getHeight());					
					break;
				}
			}
        }
        return isBbPresent;
    }
    
    public boolean isBBTop() {
        boolean isBbTop = false;
        for (GObj obj : getObjectList()) {
            for (Shape shape : obj.getShapes()) {
				if (shape instanceof BoundingBox) {
				   if(getObjectList().indexOf(obj) == getObjectList().size() - 1)
					   isBbTop = true;
					}			
					break;
				}
			}
         return isBbTop;
    }
    
    /**
     * Sets actionInProgress. Actions that consist of more than one atomic step
     * that cannot be interleaved with other actions should set this property
     * and unset it after completion. For example, consider this scenario:
     * <ol>
     * <li>a new object is created</li>
     * <li>a new connection is connected to the new object</li>
     * <li>before connecting a second object the addition of the object is
     * undone</li>
     * </ol>
     * This is a case when undo-redo should be disabled until either the
     * connection is cancelled or the second end is connected.
     * 
     * @param newValue the actionInProgress value
     */
    public void setActionInProgress( boolean newValue ) {
        if ( newValue != actionInProgress ) {
            actionInProgress = newValue;
            ClassEditor editor = ClassEditor.getInstance();
            editor.deleteAction.setEnabled( !newValue );
            editor.refreshUndoRedo();
        }
    }
    
    /**
     * Starts adding a new relation object.
     * 
     * @param port the first port of the relation object
     */
    protected void startAddingRelObject( Port port ) {    	
    	super.startAddingRelObject(port);
        setActionInProgress( true );
    }

    protected void cancelAddingObject() {
    	super.cancelAddingObject();
        setActionInProgress( false );
    }

}
