package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.Cursor;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Mouse operations on Canvas.
 */
class MouseOps extends MouseInputAdapter {

    String state = State.selection;
    int startX, startY;
    boolean mouseOver;

    private Canvas canvas;
    private Point draggedBreakPoint;
    private GObj draggedObject;
    private int cornerClicked;
    private Port currentPort;

    public MouseOps( Canvas e ) {
        this.canvas = e;
    }

    public void setState( String state ) {
        if ( canvas.currentCon != null || canvas.currentObj != null ) {
            canvas.cancelAdding();
            if ( currentPort != null ) {
                currentPort.setSelected( false );
                currentPort = null;
            }
        }

        assert currentPort == null;
        assert canvas.currentCon == null;
        assert canvas.currentObj == null;
        assert canvas.currentPainter == null;

        this.state = state;

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
        }
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

    @Override
    public void mouseClicked( MouseEvent e ) {
        int x, y;

        // ignore mouse clicks while dragging
        if ( State.drag.equals( state ) || State.dragBreakPoint.equals( state ) )
            return;

        x = Math.round( e.getX() / canvas.getScale() );
        y = Math.round( e.getY() / canvas.getScale() );

        canvas.mouseX = x;
        canvas.mouseY = y;

        if ( SwingUtilities.isRightMouseButton( e ) ) {
            if ( State.selection.equals( state ) ) {
                Connection relation = canvas.getConnectionNearPoint( x, y );
                if ( relation != null ) {
                    ConnectionPopupMenu popupMenu = new ConnectionPopupMenu( relation, canvas, x, y );
                    popupMenu.show( canvas, e.getX() + canvas.drawingArea.getX(), e.getY() + canvas.drawingArea.getY() );
                } else {
                    GObj obj = canvas.getObjects().checkInside( x, y );
                    if ( obj != null || canvas.getObjects().getSelectedCount() > 1 ) {
                        openObjectPopupMenu( obj, e.getX() + canvas.drawingArea.getX(), e.getY() + canvas.drawingArea.getY() );
                    }
                }
            } else if ( State.addRelation.equals( state ) ) {
                // remove last breakpoint or stop adding the relation
                // when only the first breakpoints is left
                if ( canvas.currentCon != null ) {
                    if ( canvas.currentCon.getBreakpointCount() > 0 )
                        canvas.currentCon.removeBreakPoint();
                    else
                        canvas.mListener.setState( State.selection );
                }
            } else if ( State.isAddRelClass( state ) ) {
                if ( canvas.currentObj != null )
                    canvas.mListener.setState( State.selection );
            } else {
                canvas.mListener.setState( State.selection );
            }
            // **********End of RIGHT mouse button
            // controls**********************************************
        } else {
            if ( state.equals( State.addRelation ) ) {
                // **********Relation adding code**************************
                Port port = canvas.getObjects().getPort( x, y );
                if ( port != null ) {
                    if ( canvas.currentCon == null ) {
                        if ( port.canBeConnected() )
                            canvas.startAddingConnection( port );
                    } else {
                        Port firstPort = canvas.currentCon.beginPort;
                        if ( port.canBeConnectedTo( firstPort ) ) {
                            if ( port == firstPort ) {
                                // Connecting a port to itself does not make
                                // any sense, so do not allow it.
                                canvas.cancelAdding();
                            } else {
                                canvas.addCurrentConnection( port );
                            }
                        }
                    }
                } else {
                    // double click on the background cancels relation adding,
                    // one click on the background adds a new breakpoint.
                    if ( canvas.currentCon != null ) {
                        if ( e.getClickCount() == 2 )
                            canvas.cancelAdding();
                        else
                            canvas.currentCon.addBreakPoint( new Point( x, y ) );
                    }
                }

            } else if ( state.equals( State.selection ) ) {
                // **********Selecting objects code*********************

                if ( !e.isShiftDown() ) {
                    canvas.getObjects().clearSelected();
                    canvas.getConnections().clearSelected();
                }

                Connection con = canvas.getConnectionNearPoint( x, y );

                if ( con != null ) {
                    con.setSelected( true );
                } else {
                    GObj obj = canvas.getObjects().checkInside( x, y );

                    if ( obj != null )
                        obj.setSelected( true );
                }

            } else {
                if ( State.isAddRelClass( state ) ) {
                    Port port = canvas.getObjects().getPort( x, y );

                    if ( port != null ) {
                        PackageClass obj = canvas.getPackage().getClass( State.getClassName( state ) );

                        // Relation classes must have exactly 2 ports. Although
                        // package parser should have already catched this an
                        // additional check does not hurt.
                        if ( obj.ports.size() != 2 )
                            return;

                        if ( canvas.currentObj == null ) {
                            if ( port.canBeConnectedTo( obj.ports.get( 0 ) ) )
                                canvas.startAddingRelObject( port );
                        } else {
                            if ( port.canBeConnectedTo( obj.ports.get( 1 ) ) )
                                canvas.addCurrentObject( port );
                        }
                    }
                } else if ( canvas.currentObj != null ) {
                    canvas.addCurrentObject();
                    setState( State.selection );
                }
            }
        }
        canvas.drawingArea.repaint();
    }

    @Override
    public void mousePressed( MouseEvent e ) {
        if ( state.equals( State.selection ) ) {
            GObj obj = null;
            canvas.mouseX = Math.round( e.getX() / canvas.getScale() );
            canvas.mouseY = Math.round( e.getY() / canvas.getScale() );
            Connection con = canvas.getConnectionNearPoint( canvas.mouseX, canvas.mouseY );

            if ( con != null ) {
                draggedBreakPoint = con.breakPointContains( canvas.mouseX, canvas.mouseY );

                if ( draggedBreakPoint != null ) {
                    setState( State.dragBreakPoint );
                    con.setSelected( true );
                    canvas.drawingArea.repaint();
                } else {
                    obj = canvas.getObjects().checkInside( canvas.mouseX, canvas.mouseY );
                }
            } else {
                obj = canvas.getObjects().checkInside( canvas.mouseX, canvas.mouseY );
            }

            if ( obj != null ) {
                if ( e.isShiftDown() ) {
                    obj.setSelected( true );
                } else {
                    if ( !obj.isSelected() ) {
                        canvas.getObjects().clearSelected();
                        obj.setSelected( true );
                    }
                }
                if ( SwingUtilities.isLeftMouseButton( e ) ) {
                    setState( State.drag );
                    draggedObject = obj;
                }
                canvas.drawingArea.repaint();
            } else if ( con == null ) {
                cornerClicked = canvas.getObjects().controlRectContains( canvas.mouseX, canvas.mouseY );
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

        if ( !SwingUtilities.isLeftMouseButton( e ) ) {
            return;
        }
        int x = Math.round( e.getX() / canvas.getScale() );
        int y = Math.round( e.getY() / canvas.getScale() );

        canvas.setPosInfo( x, y );

        if ( State.dragBreakPoint.equals( state ) ) {
            if ( RuntimeProperties.getSnapToGrid() ) {
                draggedBreakPoint.x = Math.round( (float) x / RuntimeProperties.getGridStep() ) * RuntimeProperties.getGridStep();
                draggedBreakPoint.y = Math.round( (float) y / RuntimeProperties.getGridStep() ) * RuntimeProperties.getGridStep();
            } else {
                draggedBreakPoint.x = x;
                draggedBreakPoint.y = y;
            }
        } else if ( State.drag.equals( state ) ) {
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
            ArrayList<GObj> selected = canvas.getObjects().getSelected();
            SNAP: for ( GObj obj : selected ) {
                if ( obj.isStrict() ) {
                    for ( Port port1 : obj.getPorts() ) {
                        if ( port1.isStrict() ) {
                            Point p1 = obj.toCanvasSpace( port1.getRealCenterX(), port1.getRealCenterY() );
                            Port port2 = canvas.getObjects().getPort( p1.x + moveX, p1.y + moveY, obj );

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
        }
        canvas.drawingArea.repaint();
    }

    @Override
    public void mouseMoved( MouseEvent e ) {
        int x = Math.round( e.getX() / canvas.getScale() );
        int y = Math.round( e.getY() / canvas.getScale() );

        canvas.setPosInfo( x, y );

        // Check if port needs to be hilighted because of mouseover.
        // A repaint() is always necessary when adding connections and the
        // mouse has moved because the disconnected end has to follow the mouse.
        if ( state.equals( State.addRelation ) ) {
            updateConnectionPortHilight( x, y );
            canvas.drawingArea.repaint();
        } else if ( State.isAddRelClass( state ) ) {
            updateRelClassPortHilight( x, y );
            canvas.drawingArea.repaint();
        } else if ( canvas.currentObj != null ) {
            // if we're adding a new object...

            if ( RuntimeProperties.getSnapToGrid() ) {
                canvas.currentObj.x = Math.round( x / RuntimeProperties.getGridStep() ) * RuntimeProperties.getGridStep();
                canvas.currentObj.y = Math.round( y / RuntimeProperties.getGridStep() ) * RuntimeProperties.getGridStep();
            } else {
                canvas.currentObj.y = y;
                canvas.currentObj.x = x;
            }

            if ( canvas.currentObj.isStrict() )
                updateStrictPortHilight();

            Rectangle rect = new Rectangle( e.getX() - 10, e.getY() - 10, Math.round( canvas.currentObj.getRealWidth()
                    * canvas.getScale() ) + 10, Math.round( ( canvas.currentObj.getRealHeight() * canvas.getScale() ) + 10 ) );

            canvas.drawingArea.scrollRectToVisible( rect );

            if ( e.getX() + canvas.currentObj.getRealWidth() > canvas.drawAreaSize.width ) {

                canvas.drawAreaSize.width = e.getX() + canvas.currentObj.getRealWidth();

                canvas.drawingArea.setPreferredSize( canvas.drawAreaSize );
                canvas.drawingArea.revalidate();
            }

            if ( e.getY() + canvas.currentObj.getRealHeight() > canvas.drawAreaSize.height ) {

                canvas.drawAreaSize.height = e.getY() + canvas.currentObj.getRealHeight();

                canvas.drawingArea.setPreferredSize( canvas.drawAreaSize );
                canvas.drawingArea.revalidate();
            }

            canvas.drawingArea.repaint();
        }

        canvas.mouseX = x;
        canvas.mouseY = y;
    }

    /**
     * Hilights the port under the mouse cursor when adding a relation class and
     * the port of the relation class can be connected to the mouseover port.
     * 
     * @param x the X coordinate of the mouse cursor
     * @param y the Y coordinate of the mouse cursor
     */
    private void updateRelClassPortHilight( int x, int y ) {
        Port port = canvas.getObjects().getPort( x, y );
        RelObj curObj = (RelObj) canvas.currentObj;

        if ( currentPort != null && currentPort != port ) {
            if ( curObj == null || curObj.startPort != currentPort )
                currentPort.setSelected( false );

            currentPort = null;
        }

        if ( port != null && currentPort == null ) {
            if ( curObj != null ) {
                // hilight the second port only if its type is compatible
                // with the first already connected port
                if ( port.canBeConnectedTo( curObj.startPort ) ) {
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
        Port port = canvas.getObjects().getPort( x, y );

        if ( currentPort != null && currentPort != port ) {
            if ( canvas.currentCon == null || canvas.currentCon.beginPort != currentPort ) {
                currentPort.setSelected( false );
            }
            currentPort = null;
        }

        if ( port != null && currentPort == null ) {
            if ( canvas.currentCon != null ) {
                // hilight the second port only if its type is compatible
                // with the first already connected port
                Port firstPort = canvas.currentCon.beginPort;
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
        for ( Port port : canvas.currentObj.getPorts() ) {
            port.setSelected( false );

            Port port2 = canvas.getObjects().getPort( port.getRealCenterX(), port.getRealCenterY() );

            if ( port2 != null && port2.isStrict() && !port2.isStrictConnected() && port.canBeConnectedTo( port2 ) ) {

                // Maybe there is a huge strict port which contains more than
                // one of currentObj's ports. In this case only the first
                // port will be connected an the others should not be
                // hilighted.
                boolean ignore = false;
                for ( Port p : canvas.currentObj.getPorts() ) {
                    if ( p.isSelected() && canvas.getObjects().getPort( p.getRealCenterX(), p.getRealCenterY() ) == port2 ) {
                        ignore = true;
                        break;
                    }

                }

                if ( !ignore ) {
                    port.setSelected( true );
                    canvas.currentObj.x += port2.getRealCenterX() - port.getRealCenterX();
                    canvas.currentObj.y += port2.getRealCenterY() - port.getRealCenterY();
                }
            }
        }
    }

    @Override
    public void mouseReleased( MouseEvent e ) {
        if ( state.equals( State.dragBreakPoint ) ) {
            state = State.selection;
        } else if ( state.equals( State.drag ) ) {
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
            canvas.getObjects().selectObjectsInsideBox( x1, y1, x2, y2 );
            state = State.selection;
            canvas.drawingArea.repaint();
        }

        List<GObj> selected = canvas.getObjects().getSelected();
        if ( selected != null && selected.size() > 0 )
            canvas.posInfo.setText( "Selection: " + selected.toString() );

        canvas.setActionInProgress( false );
    }
}
