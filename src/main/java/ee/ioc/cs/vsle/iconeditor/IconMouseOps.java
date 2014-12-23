package ee.ioc.cs.vsle.iconeditor;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.editor.Menu;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.graphics.Shape;
import ee.ioc.cs.vsle.vclass.Point;

/**
 * Created by IntelliJ IDEA.
 * Author: Aulo Aasmaa
 * Date: 6.1.2004
 * Time: 23:18:00
 */

class IconMouseOps extends MouseInputAdapter implements ActionListener {

    IconEditor editor;
    int shapeCount;
    int relCount;
    Point draggedBreakPoint;
    String state = "";
    String mouseState = "";

    public int startX, startY;
    public int arcWidth, arcHeight;
    public boolean fill = false;
    public float strokeWidth = 1.0f;
    public int transparency = 255;
    public int lineType = 0;

    public Color color = Color.black;
    boolean dragged = false;
    int cornerClicked;
    public int arcStartAngle;
    public int arcAngle;

    /**
     * Class constructor.
     * @param e IconEditor - IconEditor reference.
     */
    public IconMouseOps( IconEditor e ) {
        this.editor = e;
    }

    public int getTransparency() {
        return this.transparency;
    }

    public int getLineType() {
        return this.lineType;
    }

    /**
     * Set the state of the application. The states have been
     * defined in the State class. States are used for defining
     * active actions (drawing, text typing, dragging elements, resizing, ...)
     * @param state String - state of the application.
     */
    public void setState( String state ) {
        this.state = state;

        if (State.chooseColor.equals(state)) {
            Color col = JColorChooser.showDialog(editor, "Choose Color",
                    Color.black);

            // col is null when the dialog was cancelled or closed
            if (col != null) {
                this.color = col;
                changeObjectColors(col);
            }

            editor.palette.resetButtons();
            this.state = State.selection;
        }
    } // setState

    /**
     * Open the text editing dialog. Returns
     * text to the location the dialog was opened from. Dialog
     * is modal and aligned to the center of the open application window.
     */
    private void openTextEditor( int x, int y ) {
        new TextDialog( editor, x, y ).setVisible( true );
    } // openTextEditor

    /**
     * Open the image dialog. Returns
     * image to the location the dialog was opened from. Dialog
     * is modal and aligned to the center of the open application window.
     */
    private void openImageDialog() {
        if( editor.checkPackage() )
            new ImageDialog( editor, null ).setVisible( true );
    } // openTextEditor
    
    /**
     * Open the dialog for specifying port properties. Returns port to the
     * location the dialog was opened from. Dialog is modal and aligned
     * to the center of the open application window.
     */
    private void openPortPropertiesDialog() {
        new PortPropertiesDialog( editor, null ).setVisible( true );
    } // openPortPropertiesDialog

    /**
     * Draws text on the drawing area of the IconEditor.
     * @param font Font - font used for drawing the text.
     * @param color Color - font color.
     * @param text String - the actual string of text drawn.
     */
    public void drawText( Font font, Color color, String text, int x, int y ) {
        Shape shape = editor.getSelectedShape();
        if ( shape != null && shape instanceof Text ) {
            shape.setColor( color );
            ( (Text) shape ).setFont( font );
            ( (Text) shape ).setText( text );
        } else {
            Text t = new Text( x, y, font, Shape.createColorWithAlpha( color, getTransparency() ), text );
            editor.shapeList.add( t );
        }
        editor.repaint();
    } // drawText

    /**
     * Change transparency of the selected shape(s).
     * @param transparencyPercentage - percentage of the transparency selected from the spinner.
     */
    public void changeTransparency( int transparencyPercentage ) {
        this.transparency = transparencyPercentage;
        if ( editor.shapeList != null && editor.shapeList.size() > 0 ) {
            for ( int i = 0; i < editor.shapeList.size(); i++ ) {
                Shape s = editor.shapeList.get( i );
                if ( s.isSelected() ) {
                    s.setColor( Shape.createColorWithAlpha( s.getColor(), transparency ) );
                }
            }
            editor.repaint();
        }
    }

    /**
     * Change line type of the selected shape(s).
     * @param lineType - selected line type icon name.
     */
    public void changeLineType( int lineType ) {
        this.lineType = lineType;
        if ( editor.shapeList != null && editor.shapeList.size() > 0 ) {
            for ( int i = 0; i < editor.shapeList.size(); i++ ) {
                Shape s = editor.shapeList.get( i );
                if ( s.isSelected() ) {
                    s.setLineType( lineType );
                }
            }
            editor.repaint();
        }
    } // changeLineType

    /**
    * Change the stroke with of the selected shape(s).
    * @param strokeW double - stroke width selected from the spinner.
    */
    public void changeStrokeWidth( float strokeW ) {
        this.strokeWidth = strokeW;
        if ( editor.shapeList != null && editor.shapeList.size() > 0 ) {
            for ( int i = 0; i < editor.shapeList.size(); i++ ) {
                Shape s = editor.shapeList.get( i );
                if ( s.isSelected() ) {
                    s.setStrokeWidth( strokeWidth );
                }
            }
            editor.repaint();
        }
    } // changeStrokeWidth

    /**
     * Draws port on the drawing area of the IconEditor.
     * @param portName - name of the port.
     * @param isAreaConn - port is area connectible or not.
     * @param isStrict - port is strict or not.
     * @param portType - type of port: Integer, String, Object
     */
    public void drawPort( String portName, boolean isAreaConn, boolean isStrict, String portType, boolean isMulti ) {
        IconPort p = new IconPort( portName, startX, startY, isAreaConn, isStrict, isMulti );
        p.setType( portType );
        editor.ports.add( p );
        editor.repaint();
    } // drawPort

    /**
     * Opens new popup menu for specifying port properties.
     * @param port IconPort - port whose properties are to be set.
     * @param x int - x coordinate of the port.
     * @param y int - y coordinate of the port.
     */
    private void openPortPopupMenu( IconPort port, int x, int y ) {
        IconPortPopupMenu popupMenu = new IconPortPopupMenu( port, editor );
        popupMenu.show( editor.getContentPane(), x, y );
    } // openPortPopupMenu

    /**
     * Method for building the Shape Popup Menu, and for enabling and disabling menu items
     * displayed in the menu. Method builds the menu if any of the items
     * displayed are selected. If at least one is selected, a popup menu will
     * appear, and menu items filling the requirements will be enabled. Other
     * menu items will be disabled.
     * @param x int - mouse x coordinate for opening the menu.
     * @param y int - mouse y coordinate for opening the menu.
     */
    private void openShapePopupMenu( int x, int y ) {
        if ( editor.shapeList != null && editor.shapeList.size() > 0 && editor.shapeList.getSelected().size() > 0 ) {
            editor.currentShape = editor.checkInside( x, y );
            ShapePopupMenu popupMenu = new ShapePopupMenu( editor.mListener, editor );
            popupMenu.show( editor.getContentPane(), x, y );

            // Enable or disable grouping menu items.
            if ( editor.shapeList.getSelected().size() < 2 ) {
                popupMenu.enableDisableMenuItem( popupMenu.itemGroup, false );
                if ( editor.currentShape != null ) {
                    if ( editor.currentShape.getName() != null && editor.currentShape.getName().startsWith( "GROUP" ) ) {
                        popupMenu.enableDisableMenuItem( popupMenu.itemUngroup, true );
                    } else {
                        popupMenu.enableDisableMenuItem( popupMenu.itemUngroup, false );
                    }
                }

                // Enable or disable order changing menu items.
                if ( editor.shapeList.indexOf( editor.currentShape ) == editor.shapeList.size() - 1 ) {
                    popupMenu.enableDisableMenuItem( popupMenu.itemForward, false );
                    popupMenu.enableDisableMenuItem( popupMenu.itemToFront, false );
                } else {
                    popupMenu.enableDisableMenuItem( popupMenu.itemForward, true );
                    popupMenu.enableDisableMenuItem( popupMenu.itemToFront, true );
                }

                if ( editor.shapeList.indexOf( editor.currentShape ) == 0 ) {
                    popupMenu.enableDisableMenuItem( popupMenu.itemBackward, false );
                    popupMenu.enableDisableMenuItem( popupMenu.itemToBack, false );
                } else {
                    popupMenu.enableDisableMenuItem( popupMenu.itemBackward, true );
                    popupMenu.enableDisableMenuItem( popupMenu.itemToBack, true );
                }

            } else {
                popupMenu.enableDisableMenuItem( popupMenu.itemBackward, false );
                popupMenu.enableDisableMenuItem( popupMenu.itemForward, false );
                popupMenu.enableDisableMenuItem( popupMenu.itemToFront, false );
                popupMenu.enableDisableMenuItem( popupMenu.itemToBack, false );
                popupMenu.enableDisableMenuItem( popupMenu.itemGroup, true );
                popupMenu.enableDisableMenuItem( popupMenu.itemUngroup, false );
            }
        }
    } // openShapePopupMenu

    /**
     * Add shape to the shape list.
     * @param shape - Shape to be added to the shape list.
     */
    void addShape( Shape shape ) {
        editor.shapeList.add( shape );
        editor.currentShape = null;
    } // addShape

    /**
     * Draws a freeform line on the drawing area of the Icon Editor.
     * Continues drawing until the mouse is released resulting a sequence
     * of lines to represent the hand movement while the mouse was dragged around.
     * @param col Color - line color. Black by default if not chosen otherwise from the color chooser.
     */
    public void drawLine( Color col ) {
        Line line = new Line( startX, startY, editor.mouseX, editor.mouseY, 
                Shape.createColorWithAlpha( col, getTransparency() ), strokeWidth, lineType );

        editor.mouseX = startX;
        editor.mouseY = startY;
        editor.shapeList.add( line );
        editor.repaint();
        dragged = true;
    } // drawLine

    /**
     * Draw dots on the drawing area of the IconEditor.
     * @param col Color - dot color. Black by default if not chosen otherwise from the color chooser.
     */
    public void drawDot( Color col ) {
        Dot dot = new Dot( startX, startY, 
                Shape.createColorWithAlpha( col, getTransparency() ), strokeWidth );
        editor.shapeList.add( dot );
        editor.repaint();
    } // drawDot

    /**
     * Draw a single dot on the drawing area of the IconEditor on a mouse click
     * if the free line tool was selected. Calls the drawDot method for actually
     * drawing the dot.
     * @param col Color - dot color.
     */
    public void drawDotOnClick( Color col ) {
        if ( !dragged ) {
            startX = editor.mouseX;
            startY = editor.mouseY;
            drawDot( col );
        }
        dragged = false;
    } // drawDotOnClick

    public void popupMenuListener( int x, int y ) {
        // Check if clicked on a port, else open shape popup menu.
        boolean portMenuOpened = false;
        if ( editor.ports != null && editor.ports.size() > 0 ) {
            for ( int i = 0; i < editor.ports.size(); i++ ) {
                IconPort p = editor.ports.get( i );
                if ( p.isInside( x, y ) ) {
                    portMenuOpened = true;
                    openPortPopupMenu( p, x, y );
                }
            }
        }

        // If not clicked on a port, open a shape popup menu.
        if ( !portMenuOpened ) {
            openShapePopupMenu( x, y );
        }
    }

    /**
     * Mouse clicked event from the MouseListener. Invoked when the mouse button
     * has been clicked (pressed and released) on a component.
     * @param e MouseEvent - Mouse event performed. In the method a distinction
     *                       is made between left and right mouse clicks.
     */
    @Override
    public void mouseClicked( MouseEvent e ) {
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
            editor.shapeList.add( arc );
            setState( State.selection );
        }
        // LISTEN RIGHT MOUSE BUTTON
        if ( SwingUtilities.isRightMouseButton( e ) ) {
            popupMenuListener( x, y );
        } // END OF LISTENING RIGHT MOUSE BUTTON
        else {
            // SELECT SHAPES
            if ( state.equals( State.selection ) ) {

                if ( e.getClickCount() == 2 ) {
                    Shape shape = editor.shapeList.checkInside( x, y );
                    if ( shape != null ) {
                        if ( shape instanceof Text ) {
                            TextDialog td = new TextDialog( editor, x, y );
                            td.setText( ( (Text) shape ).getText() );
                            td.setFont( ( (Text) shape ).getFont() );
                            td.setColor( shape.getColor() );
                            td.setVisible( true );
                        }
                    }
                }

                boolean portSelected = false;

                // Check if clicked inside a port.
                boolean keepSelection = e.isShiftDown();
                portSelected = selectPort( keepSelection );

                if ( !portSelected ) {
                    Shape shape = editor.checkInside( x, y );

                    if ( shape == null ) {
                        editor.shapeList.clearSelected();
                    } else {
                        if ( !e.isShiftDown() ) {
                            editor.shapeList.clearSelected();
                            shape.setSelected( true );
                        }
                    }
                    // Display selected shape dimensions on the mouse position label under the icon editor's drawing area.
                    if ( editor.shapeList != null && editor.shapeList.size() > 0 && editor.shapeList.getSelected().size() == 1 ) {
                        Shape s = editor.shapeList.getSelected().get( 0 );
                        if ( !( s instanceof Text ) ) {
                            String text = "W:" + s.getWidth() + ", H:" + s.getHeight();
                            editor.posInfo.setText( text );
                        }
                    }
                } else {
                    editor.selectAllObjects( false );
                }

            } // END OF SELECTING SHAPES
            /*
            else {
            	if (editor.currentShape != null) {
            		addShape(editor.currentShape);
            		state = State.selection;
            		Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
            		editor.setCursor(cursor);
            	}

            }*/

        } // END OF LISTENING LEFT MOUSE BUTTON

        editor.repaint();
    }

    /**
     * Check if the mouse was clicked on a port.
     * If, then return a boolean value that the
     * implementing methods can use for not selecting
     * the underlying shapes etc.
     * @param keepSelection - keep the already selected ports selected.
     * @return boolean - mouse was clicked on a port or not.
     */
    public boolean selectPort( boolean keepSelection ) {
        boolean portSelected = false;
        if ( editor.ports != null && editor.ports.size() > 0 ) {
            for ( int i = 0; i < editor.ports.size(); i++ ) {
                IconPort p = editor.ports.get( i );
                if ( p.isInside( editor.mouseX, editor.mouseY ) ) {
                    p.setSelected( true );
                    state = State.drag;
                    portSelected = true;
                } else {
                    if ( !keepSelection ) {
                        p.setSelected( false );
                    }
                }
            }
        }
        return portSelected;
    } // selectPort

    /**
     * Mouse pressed event from the MouseListener. Invoked when a mouse button
     * has been pressed on a component.
     * @param e MouseEvent - Mouse event performed. In the method a distinction
     *                       is made between different states of the application.
     */
    @Override
    public void mousePressed( MouseEvent e ) {
        editor.mouseX = e.getX();
        editor.mouseY = e.getY();
        mouseState = "pressed";
        if ( !( state.equals( State.drawArc1 ) || state.equals( State.drawArc2 ) ) ) {
            startX = e.getX();
            startY = e.getY();
        }

        if ( state.equals( State.selection ) ) {

            Shape shape = null;
            boolean portSelected = false;

            // Check if we have clicked inside a port.
            portSelected = selectPort( true );

            if ( !portSelected ) {
                shape = editor.checkInside( editor.mouseX, editor.mouseY );

                if ( shape != null ) {
                    if ( e.isShiftDown() ) {
                        shape.setSelected( true );
                    } else {
                        if ( !shape.isSelected() ) {
                            editor.shapeList.clearSelected();
                            shape.setSelected( true );
                        }
                    }
                    cornerClicked = shape.controlRectContains( editor.mouseX, editor.mouseY );

                    if ( cornerClicked != 0 ) {
                        state = State.resize;
                        editor.currentShape = shape;
                    } else {
                        state = State.drag;
                        editor.repaint();
                    }

                } else {
                    state = State.dragBox;
                    startX = editor.mouseX;
                    startY = editor.mouseY;
                }
            } else {
                state = State.drag;
                editor.selectAllObjects( false );
            }

        }
    }

    /**
     * Mouse dragged event from the MouseMotionListener. Invoked when a mouse button is
     * pressed on a component and then dragged. MOUSE_DRAGGED events will continue to be
     * delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     *
     * Due to platform-dependent Drag&Drop implementations, MOUSE_DRAGGED events may not be
     * delivered during a native Drag&Drop operation.
     *
     * @param e MouseEvent - Mouse event performed. In the method a distinction
     *                       is made between different states of the application.
     */
    @Override
    public void mouseDragged( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();
        Shape shape;

        Cursor cursor;

        if ( state.equals( State.drag ) ) {
            cursor = new Cursor( Cursor.DEFAULT_CURSOR );
        } else if ( state.equals( State.resize ) ) {
            cursor = getResizeCursor( cornerClicked );
        } else {
            cursor = new Cursor( Cursor.CROSSHAIR_CURSOR );
        }

        editor.setCursor( cursor );

        if ( state.equals( State.drag ) ) {

            if ( editor.ports != null && editor.ports.size() > 0 ) {
                for ( int i = 0; i < editor.ports.size(); i++ ) {
                    IconPort p = editor.ports.get( i );
                    if ( p.isSelected() ) {
                        p.setPosition( p.getX() + ( x - editor.mouseX ), p.getY() + ( y - editor.mouseY ) );
                    }
                }
            }

            ArrayList<Shape> selectedShapes = editor.shapeList.getSelected();

            for ( int i = 0; i < selectedShapes.size(); i++ ) {
                shape = selectedShapes.get( i );
                if ( shape instanceof ShapeGroup ) {
                    shape.setPosition( x - editor.mouseX, y - editor.mouseY );
                } else {
                    shape.setPosition( x - editor.mouseX, y - editor.mouseY );
                }
            }

            editor.mouseX = x;
            editor.mouseY = y;
            editor.repaint();
        } else if ( state.equals( State.dragBox ) ) {
            editor.mouseX = x;
            editor.mouseY = y;
            editor.repaint();
        } else if ( state.equals( State.drawLine ) ) {
            editor.repaint();
            //Graphics g = editor.drawingArea.getGraphics();
            //g.setColor(color);
            //g.drawLine(editor.mouseX, editor.mouseY, x, y);
            editor.mouseX = x;
            editor.mouseY = y;
        } else if ( state.equals( State.drawArc ) || state.equals( State.drawFilledArc ) ) {
            fill = false;
            if ( state.equals( State.drawFilledArc ) ) {
                fill = true;
            }
            editor.repaint();
            //Graphics g = editor.drawingArea.getGraphics();
            //g.setColor(color);
            //final int width = Math.abs(editor.mouseX - x);
            //final int height = Math.abs(editor.mouseY - y);
            //g.drawRect(Math.min(x, editor.mouseX), Math.min(y, editor.mouseY), width, height);
            editor.mouseX = x;
            editor.mouseY = y;
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
            editor.repaint();
            //Graphics g = editor.drawingArea.getGraphics();
            //g.setColor(color);
            //final int width = Math.abs(editor.mouseX - x);
            //final int height = Math.abs(editor.mouseY - y);

            //g.drawRect(Math.min(x, editor.mouseX), Math.min(y, editor.mouseY), width, height);
            editor.mouseX = x;
            editor.mouseY = y;

        } else if ( state.equals( State.drawOval ) || state.equals( State.drawFilledOval ) ) {
            fill = false;
            if ( state.equals( State.drawFilledOval ) ) {
                fill = true;
            }
            editor.repaint();
            //Graphics g = editor.drawingArea.getGraphics();
            //g.setColor(color);
            //final int width = Math.abs(editor.mouseX - x);
            //final int height = Math.abs(editor.mouseY - y);
            //g.drawOval(Math.min(x, editor.mouseX), Math.min(y, editor.mouseY), width, height);
            editor.mouseX = x;
            editor.mouseY = y;
        } else if ( state.equals( State.freehand ) ) {
            startX = x;
            startY = y;
            drawLine( color );
        } else if ( state.equals( State.eraser ) ) {
            startX = x;
            startY = y;
            editor.shapeList.eraseShape( startX, startY );
            for ( int i = 0; i < editor.ports.size(); i++ ) {
                IconPort port = editor.ports.get( i );
                if ( port.isInside( x, y ) ) {
                    editor.ports.remove( i );
                }
            }
            editor.repaint();
        } else if ( state.equals( State.resize ) ) {
            int width = x - editor.mouseX;
            editor.mouseX = x;
            int height = y - editor.mouseY;
            editor.mouseY = y;
            editor.currentShape.resize( width, height, cornerClicked );
            editor.repaint();
            editor.posInfo.setText( "W: " + editor.currentShape.getWidth() + ", H: " + editor.currentShape.getHeight() );
        }
    }

    private Cursor getResizeCursor( int corner ) {

        if ( corner == 1 ) {
            return new Cursor( Cursor.NW_RESIZE_CURSOR );
        } else if ( corner == 2 ) {
            return new Cursor( Cursor.NE_RESIZE_CURSOR );
        } else if ( corner == 3 ) {
            return new Cursor( Cursor.SW_RESIZE_CURSOR );
        } else if ( corner == 4 ) {
            return new Cursor( Cursor.SE_RESIZE_CURSOR );
        } else {
            return new Cursor( Cursor.CROSSHAIR_CURSOR );
        }
    }

    /**
     * Mouse moved event from the MouseMotionListener. Invoked when the mouse cursor has
     * been moved onto a component but no buttons have been pushed.
     * @param e MouseEvent - Mouse event performed. In the method a distinction
     *                       is made between right and left mouse clicks.
     */
    @Override
    public void mouseMoved( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();
        editor.mouseX = x;
        editor.mouseY = y;
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
            //else
            //	arcAngle = (int)(Math.atan(legOpp/legNear)*180/Math.PI) - arcStartAngle;

        }
        //update mouse position in info label
        editor.posInfo.setText( Integer.toString( x ) + ", " + Integer.toString( y ) );
        if ( state.equals( State.drawArc1 ) || state.equals( State.drawArc2 ) )
            editor.repaint();
        // LISTEN LEFT MOUSE BUTTON
        if ( SwingUtilities.isLeftMouseButton( e ) ) {
            //update mouse position on info label
            editor.posInfo.setText( Integer.toString( x ) + ", " + Integer.toString( y ) );
            // editor.currentShape = editor.shapeList.checkInside(x, y);
            if ( editor.currentShape != null ) {
                editor.currentShape.setX( x );
                editor.currentShape.setY( y );

                Rectangle rect = new Rectangle( x - 10, y - 10, editor.currentShape.getRealWidth() + 10, editor.currentShape
                        .getRealHeight() + 10 );
                IconEditor.drawingArea.scrollRectToVisible( rect );
                if ( x + editor.currentShape.getRealWidth() > editor.drawAreaSize.width ) {
                    editor.drawAreaSize.width = x + editor.currentShape.getRealWidth();
                    IconEditor.drawingArea.setPreferredSize( editor.drawAreaSize );
                    IconEditor.drawingArea.setPreferredSize( editor.drawAreaSize );
                }

                if ( y + editor.currentShape.getRealHeight() > editor.drawAreaSize.height ) {
                    editor.drawAreaSize.height = y + editor.currentShape.getRealHeight();
                    IconEditor.drawingArea.setPreferredSize( editor.drawAreaSize );
                    IconEditor.drawingArea.revalidate();
                }
                editor.repaint();
            }
        }

        if ( state.equals( State.selection ) ) {

            int corner;
            if ( editor.currentShape != null && editor.currentShape.isSelected()
                    && ( corner = editor.currentShape.controlRectContains( editor.mouseX, editor.mouseY ) ) > 0 && corner < 5 ) {

                editor.setCursor( getResizeCursor( corner ) );
            } else {
                editor.setCursor( new Cursor( Cursor.DEFAULT_CURSOR ) );
            }
        }
    }

    /**
     * Mouse released event from the MouseListener. Invoked when a mouse button has been
     * released on a component.
     * @param e MouseEvent - Mouse event performed. In the method a distinction
     *                       is made between different states of the application.
     */
    @Override
    public void mouseReleased( MouseEvent e ) {
        int x = e.getX();
        int y = e.getY();
        Cursor cursor = new Cursor( Cursor.DEFAULT_CURSOR );
        editor.setCursor( cursor );

        mouseState = "released";

        if ( SwingUtilities.isLeftMouseButton( e ) ) {
            if ( state.equals( State.drag ) ) {
                state = State.selection;
            }
            if ( state.equals( State.dragBox ) || state.equals( State.selection ) ) {
                int x1 = Math.min( startX, editor.mouseX );
                int x2 = Math.max( startX, editor.mouseX );
                int y1 = Math.min( startY, editor.mouseY );
                int y2 = Math.max( startY, editor.mouseY );
                editor.selectShapesInsideBox( x1, y1, x2, y2 );
                state = State.selection;
                editor.repaint();
            }
            if ( editor.shapeList.getSelected() != null && editor.shapeList.getSelected().size() > 0 ) {
                String selShapes = editor.shapeList.getSelected().toString();
                if ( selShapes != null ) {
                    selShapes = selShapes.replaceAll( "null ", " " );
                }
            }

            if ( state.equals( State.dragBreakPoint ) ) {
                state = State.selection;
            }

            if ( state != null && state.equals( State.drawRect ) || state.equals( State.drawFilledRect )
                    || state.equals( State.boundingbox ) ) {
                final int width = Math.abs( editor.mouseX - startX );
                final int height = Math.abs( editor.mouseY - startY );

                if ( state.equals( State.boundingbox ) ) {
                    BoundingBox box = new BoundingBox( Math.min( startX, editor.mouseX ), Math.min( startY, editor.mouseY ), width, height );
                    editor.boundingbox = box;
                    // ONLY ONE BOUNDING BOX IS ALLOWED.
                    editor.shapeList.add( box );
                    editor.palette.boundingbox.setEnabled( false );
                    state = State.selection;

                } else {
                    Rect rect = new Rect( Math.min( startX, editor.mouseX ), Math.min( startY, editor.mouseY ), width, height,
                            Shape.createColorWithAlpha( color, getTransparency() ), fill, strokeWidth, lineType );
                    editor.shapeList.add( rect );
                }
                editor.repaint();
            } else if ( state.equals( State.drawOval ) || state.equals( State.drawFilledOval ) ) {
                int width = Math.abs( editor.mouseX - startX );
                int height = Math.abs( editor.mouseY - startY );
                Oval oval = new Oval( Math.min( startX, editor.mouseX ), Math.min( startY, editor.mouseY ), width, height, 
                        Shape.createColorWithAlpha( color, getTransparency() ),
                        fill, strokeWidth, lineType );
                editor.shapeList.add( oval );
            } else if ( state.equals( State.drawArc ) || state.equals( State.drawFilledArc ) ) {
                arcWidth = Math.abs( editor.mouseX - startX );
                arcHeight = Math.abs( editor.mouseY - startY );
                setState( State.drawArc1 );
            } else if ( state.equals( State.drawLine ) ) {
                Line line = new Line( startX, startY, editor.mouseX, editor.mouseY, 
                        Shape.createColorWithAlpha( color, getTransparency() ), strokeWidth, lineType );
                editor.shapeList.add( line );
            } else if ( state.equals( State.resize ) ) {
                state = State.selection;
            } else if ( state.equals( State.freehand ) ) {
                drawDotOnClick( color );
            } else if ( state.equals( State.eraser ) ) {
                editor.shapeList.eraseShape( editor.mouseX, editor.mouseY );
                editor.repaint();
            } else if ( state.equals( State.drawText ) ) {
                openTextEditor( x, y );
            } else if ( state.equals( State.addPort ) ) {
                openPortPropertiesDialog();
            } else if ( state.equals( State.insertImage ) ) {
                openImageDialog();
            }

        }
    }

    /**
     * Mouse entered event from the MouseMotionListener. Invoked when the mouse enters a component.
     * @param e MouseEvent - Mouse event performed.
     */
    @Override
    public void mouseEntered( MouseEvent e ) {
        Cursor cursor = new Cursor( Cursor.DEFAULT_CURSOR );
        editor.setCursor( cursor );
    }

    /**
     * Mouse exited event from the MouseMotionListener. Invoked when the mouse exits a component.
     * @param e MouseEvent - Mouse event performed.
     */
    @Override
    public void mouseExited( MouseEvent e ) {
        Cursor cursor = new Cursor( Cursor.HAND_CURSOR );
        editor.setCursor( cursor );
    }

    /**
     * Change colors of selected object(s).
     * @param col Color - color for selected object(s).
     */
    public void changeObjectColors( Color col ) {
        if ( editor.shapeList != null && editor.shapeList.size() > 0 ) {
            for ( int i = 0; i < editor.shapeList.size(); i++ ) {
                Shape s = editor.shapeList.get( i );
                if ( s != null && s.isSelected() ) {
                    s.setColor( col );
                    editor.repaint();
                }
            }
        }
    } // change object colors

    //*********************************************************************
    //	end of mouse control functions
    //*********************************************************************

    public void actionPerformed( ActionEvent e ) {

        // JmenuItem chosen
        if ( e.getSource().getClass().getName() == "javax.swing.JMenuItem"
                || e.getSource().getClass().getName() == "javax.swing.JCheckBoxMenuItem" ) {
            if ( e.getActionCommand().equals( Menu.SAVE_SCHEME ) ) {
                editor.saveScheme();
            } else if ( e.getActionCommand().equals( Menu.CLEAR_ALL ) ) {
                editor.clearObjects();
            } else if ( e.getActionCommand().equals( Menu.GRID ) ) {
                boolean isGridVisible = IconEditor.drawingArea.isGridVisible();
                if ( isGridVisible ) {
                    isGridVisible = false;
                } else {
                    isGridVisible = true;
                }
                IconEditor.drawingArea.setGridVisible( isGridVisible );
            } else if ( e.getActionCommand().equals( Menu.CLASS_PROPERTIES ) ) {
                new ClassPropertiesDialog( editor.getClassFieldModel(), true );
            } else if ( e.getActionCommand().equals( Menu.CLONE ) ) {
                editor.cloneObject();
            } else if ( e.getActionCommand().equals( Menu.SELECT_ALL ) ) {
                editor.selectAllObjects( true );
                editor.selectAllPorts( true );
            } else if ( e.getActionCommand().equals( Menu.LOAD_SCHEME ) ) {
                editor.loadScheme();
            } else if ( e.getActionCommand().equals( Menu.PRINT ) ) {
                editor.print();
            } else if ( e.getActionCommand().equals( Menu.EXIT ) ) {
                editor.exitApplication();
            } else if ( e.getActionCommand().equals( Menu.DOCS ) ) {
                String documentationUrl = RuntimeProperties.getSystemDocUrl();
                if ( documentationUrl != null && documentationUrl.trim().length() > 0 ) {
                    IconEditor.openInBrowser( documentationUrl );
                } else {
                    editor.showInfoDialog( "Missing information", "No documentation URL defined in properties." );
                }
            } else if ( e.getActionCommand().equals( Menu.LICENSE ) ) {
                new LicenseDialog( editor );
            } else if ( e.getActionCommand().equals( Menu.ABOUT ) ) {
                new AboutDialog( editor );
            } else if ( e.getActionCommand().equals( Menu.OBJECT_DELETE ) ) {
                editor.deleteObjects();
            } else if ( e.getActionCommand().equals( Menu.BACKWARD ) ) {
                // MOVE OBJECT BACKWARD IN THE LIST
                // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
                editor.shapeList.sendBackward( editor.currentShape, 1 );
                editor.repaint();
            } else if ( e.getActionCommand().equals( Menu.FORWARD ) ) {
                // MOVE OBJECT FORWARD IN THE LIST
                // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
                editor.shapeList.bringForward( editor.currentShape, 1 );
                editor.repaint();
            } else if ( e.getActionCommand().equals( Menu.TOFRONT ) ) {
                // MOVE OBJECT TO THE FRONT IN THE LIST,
                // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
                editor.shapeList.bringToFront( editor.currentShape );
                editor.repaint();
            } else if ( e.getActionCommand().equals( Menu.TOBACK ) ) {
                // MOVE OBJECT TO THE BACK IN THE LIST
                // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
                editor.shapeList.sendToBack( editor.currentShape );
                editor.repaint();
            } else if ( e.getActionCommand().equals( Menu.PROPERTIES ) ) {
                /*
                 ObjectPropertiesEditor prop = new ObjectPropertiesEditor(editor.currentObj);
                		   prop.pack();
                		   prop.setVisible(true); */
            } else if ( e.getActionCommand().equals( Menu.GROUP ) ) {
                editor.groupObjects();
            } else if ( e.getActionCommand().equals( Menu.FIXED ) ) {
                editor.fixShape();
            } else if ( e.getActionCommand().equals( Menu.UNGROUP ) ) {
                editor.ungroupObjects();
            } else if ( e.getActionCommand().equals( Menu.SETTINGS ) ) {
                editor.openOptionsDialog();
            } else if ( e.getActionCommand().equals( Menu.CLEAR_ALL ) ) {
                editor.clearObjects();
            /* do we need this?
               } else if ( e.getActionCommand().equals( Menu.EXPORT_CLASS ) ) {
                editor.exportShapesToXML(); // the class is not a relation*/
            } else if ( e.getActionCommand().equals( Menu.EXPORT_TO_PACKAGE ) ) {
                editor.exportShapesToPackage(); // append the graphics to a package
            } else if ( e.getActionCommand().equals( Menu.IMPORT_FROM_PACKAGE ) ) {
                editor.loadClass(); // append the graphics to a package
            } else if ( e.getActionCommand().equals( Menu.CREATE_PACKAGE ) ) {
                editor.createPackage();
            } else if ( e.getActionCommand().equals( Menu.SELECT_PACKAGE ) ) {
                editor.selectPackage();
            } else if ( e.getActionCommand().equals( Menu.DELETE_FROM_PACKAGE ) ) {
                editor.deleteClass();
            }
        }
        IconEditor.drawingArea.grabFocus();
    }
} // end of class
