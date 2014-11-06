package ee.ioc.cs.vsle.common.ops;

import ee.ioc.cs.vsle.util.VMath;
import ee.ioc.cs.vsle.vclass.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Mouse operations on Canvas.
 */
public class MouseOps extends MouseInputAdapter {

    // Remove a dragged breakpoint on mouse button release when it is closer
    // to the line segment between neighbouring points than this threshold.
    private static double BP_REMOVE_THRESHOLD = 200d;

    public String state = State.selection;
    public int startX, startY;
    protected boolean mouseOver;

    private Canvas canvas;
    protected Point draggedBreakPoint;
    private Connection draggedBreakPointConn;
    public GObj draggedObject;
    public int cornerClicked;
    public Port currentPort;

    public MouseOps( Canvas e ) {
        this.canvas = e;
    }

    public void setState( String state ) {
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

        /**
        if ( State.addRelation.equals( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
        } else if ( State.selection.equals( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
            canvas.palette.resetButtons();
        } else if ( State.isAddRelClass( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR ) );
        } else if ( State.isAddObject( state ) ) {
            canvas.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
            canvas.startAddingObject();
        }*/
    }

    /**
     * Mouse entered event from the MouseMotionListener. Invoked when the mouse
     * enters a component.
     * 
     * @param e MouseEvent - Mouse event performed.
     */
    @Override
	public void mouseEntered( MouseEvent e ){
    	
    }

    /**
     * Mouse exited event from the MouseMotionListener. Invoked when the mouse
     * exits a component.
     * 
     * @param e MouseEvent - Mouse event performed.
     */
    @Override
    public void mouseExited( MouseEvent e ) {
    }

  /*  private void openObjectPopupMenu( GObj obj, int x, int y ) {
        ObjectPopupMenu popupMenu = new ObjectPopupMenu( obj, canvas );
        popupMenu.show( canvas, x, y );
    }*/

    @Override
    public void mouseClicked( MouseEvent e ) {
    	//stub
    }

    @Override
    public void mousePressed( MouseEvent e ) {
      //Stub
    }

    @Override
    public void mouseDragged( MouseEvent e ) {
    	//stub
    }

    @Override
    public void mouseMoved( MouseEvent e ) {
        // stub
    }

    /**
     * Hilights the port under the mouse cursor when adding a relation class and
     * the port of the relation class can be connected to the mouseover port.
     * 
     * @param x the X coordinate of the mouse cursor
     * @param y the Y coordinate of the mouse cursor
     */
    private void updateRelClassPortHilight( int x, int y ) {
        Port port = canvas.getObjectList().getPort(x, y);
        RelObj curObj = (RelObj) canvas.getCurrentObj();

        if ( currentPort != null && currentPort != port ) {
            if ( curObj == null || curObj.getStartPort() != currentPort )
                currentPort.setSelected( false );

            currentPort = null;
        }

        if ( port != null && currentPort == null ) {
            if ( curObj != null ) {
                // hilight the second port only if its type is compatible
                // with the first already connected port
                if ( port.canBeConnectedTo( curObj.getStartPort() ) ) {
                    port.setSelected( true );
                    currentPort = port;
                }
            } else {
                port.setSelected( true );
                currentPort = port;
            }
        }
    }

    /**
     * Hilights the port the connection could be attached to.
     * 
     * @see #updateRelClassPortHilight(int, int)
     * @param x the X coordinate of the mouse cursor
     * @param y the Y coordinate of the mouse cursor
     */
    /*
     * The logic for adding connections and relation classes is quite similar,
     * is is possible to generalise and merge these methods?
     */
    private void updateConnectionPortHilight( int x, int y ) {
        Port port = canvas.getObjectList().getPort(x, y);

        if ( currentPort != null && currentPort != port ) {
            if ( canvas.currentCon == null || canvas.currentCon.getBeginPort() != currentPort ) {
                currentPort.setSelected( false );
            }
            currentPort = null;
        }

        if ( port != null && currentPort == null ) {
            if ( canvas.currentCon != null ) {
                // hilight the second port only if its type is compatible
                // with the first already connected port
                Port firstPort = canvas.currentCon.getBeginPort();
                if ( port.canBeConnectedTo( firstPort ) ) {
                    port.setSelected( true );
                    currentPort = port;
                }
            } else if ( port.canBeConnected() ) {
                port.setSelected( true );
                currentPort = port;
            }
        }
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
      // Stub
    }

    private void startBreakPointDrag(Connection con, Point bp) {
        draggedBreakPoint = bp;
        draggedBreakPointConn = con;
        setState(State.dragBreakPoint);
        con.setSelected(true);
        canvas.drawingArea.repaint();
    }

    private void endBreakPointDrag() {
        setState(State.selection);

        // Remove the dragged breakpoint if it is on a straight line
        // or close to another breakpoint or endpoint.
        if (draggedBreakPoint != null && draggedBreakPointConn != null) {
            ArrayList<Point> ps = draggedBreakPointConn.getBreakPoints();
            assert ps != null;
            int n = ps.indexOf(draggedBreakPoint);
            assert n >= 0 && n < ps.size();

            // Find neighbour anchor points, could be breakpoints or ports
            Point p1, p2;
            if (n == 0) {
                p1 = draggedBreakPointConn.getBeginPort().getAbsoluteCenter();
            } else {
                p1 = ps.get(n - 1);
            }
            if (n == ps.size() - 1) {
                p2 = draggedBreakPointConn.getEndPort().getAbsoluteCenter();
            } else {
                p2 = ps.get(n + 1);
            }

            // Remove the dragged breakpoint if it lies on an almost
            // straight line.
            double d = (draggedBreakPoint.x - p1.x) * (p2.y - p1.y)
                     - (draggedBreakPoint.y - p1.y) * (p2.x - p1.x);

            if (Math.abs(d) < BP_REMOVE_THRESHOLD) {
                ps.remove(draggedBreakPoint);
                draggedBreakPoint = null;
            }

            // Remove the breakpoint if it is close to another breakpoint
            if (draggedBreakPoint != null) {
                d = VMath.distanceBetweenPoints(p2, draggedBreakPoint);
                if (d < Connection.NEAR_DISTANCE * 3) {
                    draggedBreakPointConn.removeBreakPoint(n);
                    draggedBreakPoint = null;
                }
            }
            if (draggedBreakPoint != null) {
                d = VMath.distanceBetweenPoints(p1, draggedBreakPoint);
                if (d < Connection.NEAR_DISTANCE * 3) {
                    draggedBreakPointConn.removeBreakPoint(n);
                    draggedBreakPoint = null;
                }
            }

            // a breakpoint was removed, redrawing is needed
            if (draggedBreakPoint == null) {
                canvas.drawingArea.repaint();
            }
        }
        draggedBreakPoint = null;
        draggedBreakPointConn = null;
    }
    
   public void destroy() {
        
        canvas = null;
        draggedBreakPoint = null;
        draggedBreakPointConn = null;
        draggedObject = null;
        currentPort = null;
    }
}
