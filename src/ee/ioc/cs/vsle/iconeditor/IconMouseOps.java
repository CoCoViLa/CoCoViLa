package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.vclass.Point;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.iconeditor.*;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JFileChooser;

import java.io.FileOutputStream;
import java.awt.Cursor;
import javax.swing.JOptionPane;
import java.io.File;
import java.awt.Graphics;
import javax.swing.SwingUtilities;
import javax.swing.JColorChooser;
import javax.swing.UIManager;
import java.awt.Rectangle;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 6.1.2004
 * Time: 23:18:00
 * To change this template use Options | File Templates.
 */

class IconMouseOps
    extends MouseInputAdapter
    implements ActionListener {

  IconEditor editor;
  int shapeCount;
  int relCount;
  Point draggedBreakPoint;
  String state = "";

  public int startX, startY;
  public boolean fill = false;
  public double strokeWidth = 1.0;
  public double transparency = 0.0;
  public Color color = Color.black;
  boolean dragged = false;
  int cornerClicked;

  /**
   * Class constructor.
   * @param e IconEditor - IconEditor reference.
   */
  public IconMouseOps(IconEditor e) {
    this.editor = e;
  }

  public double getTransparency() {
    return this.transparency;
  }

  /**
   * Set the state of the application. The states have been
   * defined in the State class. States are used for defining
   * active actions (drawing, text typing, dragging elements, resizing, ...)
   * @param state String - state of the application.
   */
  public void setState(String state) {
    this.state = state;
  } // setState

  /**
   * Open the text editing dialog. Returns
   * text to the location the dialog was opened from. Dialog
   * is modal and aligned to the center of the open application window.
   */
  private void openTextEditor() {
    new TextDialog(editor);
  } // openTextEditor

  /**
   * Open the dialog for specifying port properties. Returns port to the
   * location the dialog was opened from. Dialog is modal and aligned
   * to the center of the open application window.
   */
  private void openPortPropertiesDialog() {
   PortPropertiesDialog pd = new PortPropertiesDialog(editor,null);
   pd.setModal(true);
  } // openPortPropertiesDialog

  /**
   * Draws text on the drawing area of the IconEditor.
   * @param font Font - font used for drawing the text.
   * @param color Color - font color.
   * @param text String - the actual string of text drawn.
   */
  public void drawText(Font font, Color color, String text) {
   Shape shape = editor.getSelectedShape();
   if(shape!=null && shape instanceof Text) {
     shape.setColor(color);
     shape.setFont(font);
     shape.setText(text);
   } else {
     Text t = new Text(editor.mouseX,editor.mouseY,font,color,getTransparency(),text);
     editor.shapeList.add(t);
   }
   editor.repaint();
  } // drawText

  public void changeTransparency(double transparencyPercentage) {
    this.transparency = transparencyPercentage;
    if(editor.shapeList!=null&&editor.shapeList.size()>0) {
      for(int i=0;i<editor.shapeList.size();i++) {
        Shape s = (Shape)editor.shapeList.get(i);
        if(s.isSelected()) {
          s.setTransparency(transparency);
        }
      }
      editor.repaint();
    }
  }

  public void changeStrokeWidth(double strokeW) {
    this.strokeWidth = strokeW;
    if(editor.shapeList!=null&&editor.shapeList.size()>0) {
      for(int i=0;i<editor.shapeList.size();i++) {
        Shape s = (Shape)editor.shapeList.get(i);
        if(s.isSelected()) {
          s.setStrokeWidth(strokeWidth);
        }
      }
      editor.repaint();
    }
  }

  /**
   * Draws port on the drawing area of the IconEditor.
   * @param portName - name of the port.
   * @param isAreaConn - port is area connectible or not.
   * @param isStrict - port is strict or not.
   */
  public void drawPort(String portName, boolean isAreaConn, boolean isStrict) {
   IconPort p = new IconPort(portName,editor.mouseX,editor.mouseY,isAreaConn,isStrict);
   editor.ports.add(p);
   editor.repaint();
  } // drawPort

  /**
   * Opens new popup menu for specifying port properties.
   * @param port IconPort - port whose properties are to be set.
   * @param x int - x coordinate of the port.
   * @param y int - y coordinate of the port.
   */
  private void openPortPopupMenu(IconPort port, int x, int y) {
    IconPortPopupMenu popupMenu = new IconPortPopupMenu(port,editor);
    popupMenu.show(editor.getContentPane(), x, y);
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
  private void openShapePopupMenu(int x, int y) {
    if(editor.shapeList!=null && editor.shapeList.size()>0 &&
       editor.shapeList.getSelected().size()>0) {
      editor.currentShape = editor.checkInside(x, y);
      ShapePopupMenu popupMenu = new ShapePopupMenu(editor.mListener,editor);
      popupMenu.show(editor.getContentPane(), x, y);

      // Enable or disable grouping menu items.
      if (editor.shapeList.getSelected().size() < 2) {
        popupMenu.enableDisableMenuItem(popupMenu.itemGroup, false);
        if(editor.currentShape!=null) {
          if (editor.currentShape.getName()!=null && editor.currentShape.getName().startsWith("GROUP")) {
            popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, true);
          }
          else {
            popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, false);
          }
        }

        // Enable or disable order changing menu items.
        if(editor.shapeList.indexOf(editor.currentShape)==editor.shapeList.size()-1) {
          popupMenu.enableDisableMenuItem(popupMenu.itemForward,false);
          popupMenu.enableDisableMenuItem(popupMenu.itemToFront,false);
        } else {
          popupMenu.enableDisableMenuItem(popupMenu.itemForward,true);
          popupMenu.enableDisableMenuItem(popupMenu.itemToFront,true);
        }

        if(editor.shapeList.indexOf(editor.currentShape)==0) {
          popupMenu.enableDisableMenuItem(popupMenu.itemBackward,false);
          popupMenu.enableDisableMenuItem(popupMenu.itemToBack,false);
        } else {
          popupMenu.enableDisableMenuItem(popupMenu.itemBackward,true);
          popupMenu.enableDisableMenuItem(popupMenu.itemToBack,true);
        }

      }
      else {
        popupMenu.enableDisableMenuItem(popupMenu.itemBackward,false);
        popupMenu.enableDisableMenuItem(popupMenu.itemForward,false);
        popupMenu.enableDisableMenuItem(popupMenu.itemToFront,false);
        popupMenu.enableDisableMenuItem(popupMenu.itemToBack,false);
        popupMenu.enableDisableMenuItem(popupMenu.itemGroup, true);
        popupMenu.enableDisableMenuItem(popupMenu.itemUngroup, false);
      }
    }
  } // openShapePopupMenu

  /**
   * Add shape to the shape list.
   * @param shape - Shape to be added to the shape list.
   */
  void addShape(Shape shape) {
    editor.shapeList.add(shape);
    editor.currentShape = null;
  } // addShape

  /**
   * Draws a freeform line on the drawing area of the Icon Editor.
   * Continues drawing until the mouse is released resulting a sequence
   * of lines to represent the hand movement while the mouse was dragged around.
   * @param col Color - line color. Black by default if not chosen otherwise from the color chooser.
   */
  public void drawLine(Color col) {
    Line line = new Line(startX, startY, editor.mouseX, editor.mouseY, col.getRGB(), strokeWidth, getTransparency());

    editor.mouseX = startX;
    editor.mouseY = startY;
    editor.shapeList.add(line);
    editor.repaint();
    dragged = true;
  } // drawLine

  /**
   * Draw dots on the drawing area of the IconEditor.
   * @param col Color - dot color. Black by default if not chosen otherwise from the color chooser.
   */
  public void drawDot(Color col) {
    Dot dot = new Dot(startX, startY, col.getRGB(), strokeWidth, getTransparency());
    editor.shapeList.add(dot);
    editor.repaint();
  } // drawDot

  /**
   * Draw a single dot on the drawing area of the IconEditor on a mouse click
   * if the free line tool was selected. Calls the drawDot method for actually
   * drawing the dot.
   * @param col Color - dot color.
   */
  public void drawDotOnClick(Color col) {
    if (!dragged) {
      startX = editor.mouseX;
      startY = editor.mouseY;
      drawDot(col);
    }
    dragged = false;
  } // drawDotOnClick

  public void popupMenuListener(int x, int y) {
    // Check if clicked on a port, else open shape popup menu.
    boolean portMenuOpened = false;
    if(editor.ports!=null && editor.ports.size()>0) {
      for(int i=0;i<editor.ports.size();i++) {
        IconPort p = (IconPort)editor.ports.get(i);
        if(p.isInside(x,y)) {
          portMenuOpened = true;
          openPortPopupMenu(p,x,y);
        }
      }
    }

    // If not clicked on a port, open a shape popup menu.
    if(!portMenuOpened) {
      openShapePopupMenu(x, y);
    }
  }

  /**
   * Mouse clicked event from the MouseListener. Invoked when the mouse button
   * has been clicked (pressed and released) on a component.
   * @param e MouseEvent - Mouse event performed. In the method a distinction
   *                       is made between left and right mouse clicks.
   */
  public void mouseClicked(MouseEvent e) {
    int x, y;
    x = e.getX();
    y = e.getY();

    // LISTEN RIGHT MOUSE BUTTON
    if (SwingUtilities.isRightMouseButton(e)) {
      popupMenuListener(x,y);
    } // END OF LISTENING RIGHT MOUSE BUTTON
    else {
      // SELECT SHAPES
      if (state.equals(State.selection)) {

        if(e.getClickCount()==2) {
          Shape shape = editor.shapeList.checkInside(x, y);
          if(shape!=null) {
            if(shape instanceof Text) {
              TextDialog td = new TextDialog(editor);
              td.setText(shape.getText());
              td.setFont(shape.getFont());
              td.setColor(shape.getColor());
            }
          }
        }

        boolean portSelected = false;

        // Check if clicked inside a port.
        portSelected = selectPort();

        if(!portSelected) {
		   Shape shape = editor.checkInside(x, y);

          if (shape == null) {
            editor.shapeList.clearSelected();
          } else {
            if (!e.isShiftDown()) {
              editor.shapeList.clearSelected();
              shape.setSelected(true);
            }
          }
		  // Display selected shape dimensions on the mouse position label under the icon editor's drawing area.
		  if(editor.shapeList!=null && editor.shapeList.size()>0 && editor.shapeList.getSelected().size()==1) {
			Shape s = (Shape)editor.shapeList.getSelected().get(0);
			if(!(s instanceof Text)) {
			  String text = "W:" + s.width + ", H:" + s.height;
			  editor.posInfo.setText(text);
			}
          }
        } else {
          editor.selectAllObjects(false);
        }

      } // END OF SELECTING SHAPES
      else {
        if (editor.currentShape != null) {
          addShape(editor.currentShape);
          state = State.selection;
          Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
          editor.setCursor(cursor);
        }

      }

    } // END OF LISTENING LEFT MOUSE BUTTON

    editor.repaint();
  }

  /**
   * Check if the mouse was clicked on a port.
   * If, then return a boolean value that the
   * implementing methods can use for not selecting
   * the underlying shapes etc.
   * @return boolean - mouse was clicked on a port or not.
   */
  public boolean selectPort() {
    boolean portSelected = false;
    if (editor.ports != null && editor.ports.size() > 0) {
      for (int i = 0; i < editor.ports.size(); i++) {
        IconPort p = (IconPort) editor.ports.get(i);
        if (p.isInside(editor.mouseX, editor.mouseY)) {
          p.setSelected(true);
          state = State.drag;
          portSelected = true;
        } else {
          p.setSelected(false);
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
  public void mousePressed(MouseEvent e) {
    editor.mouseX = e.getX();
    editor.mouseY = e.getY();
    startX = e.getX();
    startY = e.getY();

    if (state.equals(State.selection)) {

        Shape shape = null;
        boolean portSelected = false;

        // Check if we have clicked inside a port.
        portSelected = selectPort();

        if(!portSelected) {
          shape = editor.checkInside(editor.mouseX, editor.mouseY);

          if (shape != null) {
            if (e.isShiftDown()) {
              shape.setSelected(true);
            } else {
              if (!shape.isSelected()) {
                editor.shapeList.clearSelected();
                shape.setSelected(true);
              }
            }
            cornerClicked = shape.controlRectContains(editor.mouseX, editor.mouseY);

            if (cornerClicked != 0) {
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
          editor.selectAllObjects(false);
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
  public void mouseDragged(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    Shape shape;

    Cursor cursor;

    if(state.equals(State.drag)) {
      cursor = new Cursor(Cursor.DEFAULT_CURSOR);
    } else {
      cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
    }

    editor.setCursor(cursor);


    if (state.equals(State.drag)) {

      if(editor.ports!=null && editor.ports.size()>0) {
        for(int i=0;i<editor.ports.size();i++) {
          IconPort p = (IconPort)editor.ports.get(i);
          if(p.isSelected()) {
            p.setPosition(x,y);
          }
        }
      }

      ArrayList selectedShapes = editor.shapeList.getSelected();

      for (int i = 0; i < selectedShapes.size(); i++) {
        shape = (Shape) selectedShapes.get(i);
		if(shape instanceof ShapeGroup) {
		  shape.setPosition(x - editor.mouseX, y - editor.mouseY);
        } else {
		  shape.setPosition(shape.getX() + (x - editor.mouseX),
							shape.getY() + (y - editor.mouseY));
		}
      }

      editor.mouseX = x;
      editor.mouseY = y;
      editor.repaint();
    }
    else
    if (state.equals(State.dragBox)) {
      editor.mouseX = x;
      editor.mouseY = y;
      editor.repaint();
    }
    else
    if (state.equals(State.drawLine)) {
      editor.repaint();
      Graphics g = editor.drawingArea.getGraphics();
      g.setColor(color);
      g.drawLine(editor.mouseX,editor.mouseY,x,y);
      editor.mouseX = x;
      editor.mouseY = y;
    }
    else
    if (state.equals(State.drawArc) || state.equals(State.drawFilledArc)) {
      fill = false;
      if (state.equals(State.drawFilledArc)) {
        fill = true;
      }
      editor.repaint();
      Graphics g = editor.drawingArea.getGraphics();
      g.setColor(color);
      final int width = Math.abs(editor.mouseX - x);
      final int height = Math.abs(editor.mouseY - y);
      g.drawArc(Math.min(x,editor.mouseX),Math.min(y,editor.mouseY),width,height,0,180);
      editor.mouseX = x;
      editor.mouseY = y;
    }
    else
    if (state.equals(State.drawText)) {
      startX = x;
      startY = y;
    }
    else
    if (state.equals(State.addPort)) {
      startX = x;
      startY = y;
    }
    else
    if (state.equals(State.drawRect) || state.equals(State.drawFilledRect) || state.equals(State.boundingbox)) {
      fill = false;
      if (state.equals(State.drawFilledRect)) {
        fill = true;
      }
      editor.repaint();
      Graphics g = editor.drawingArea.getGraphics();
      g.setColor(color);
      final int width = Math.abs(editor.mouseX - x);
      final int height = Math.abs(editor.mouseY - y);

      g.drawRect(Math.min(x,editor.mouseX),Math.min(y,editor.mouseY),width,height);
      editor.mouseX = x;
      editor.mouseY = y;

    }
    else
    if (state.equals(State.drawOval) || state.equals(State.drawFilledOval)) {
      fill = false;
      if (state.equals(State.drawFilledOval)) {
        fill = true;
      }
      editor.repaint();
      Graphics g = editor.drawingArea.getGraphics();
      g.setColor(color);
      final int width = Math.abs(editor.mouseX - x);
      final int height = Math.abs(editor.mouseY - y);
      g.drawOval(Math.min(x,editor.mouseX),Math.min(y,editor.mouseY),width,height);
      editor.mouseX = x;
      editor.mouseY = y;
    }
    else
    if (state.equals(State.freehand)) {
      startX = x;
      startY = y;
      drawLine(color);
    }
    else
    if (state.equals(State.eraser)) {
      startX = x;
      startY = y;
      editor.shapeList.eraseShape(startX, startY);
	  for(int i=0;i<editor.ports.size();i++) {
		IconPort port = (IconPort)editor.ports.get(i);
	    if(port.isInside(x,y)) {
		  editor.ports.remove(i);
        }
      }
      editor.repaint();
    }
    else
    if(state.equals(State.resize)) {
      int width = x-editor.mouseX;
      editor.mouseX = x;
      int height = y-editor.mouseY;
      editor.mouseY = y;
      editor.currentShape.resize(width,height,cornerClicked);
      editor.repaint();
      editor.posInfo.setText("W: "+editor.currentShape.width+", H: "+editor.currentShape.height);
    }
  }

  /**
   * Mouse moved event from the MouseMotionListener. Invoked when the mouse cursor has
   * been moved onto a component but no buttons have been pushed.
   * @param e MouseEvent - Mouse event performed. In the method a distinction
   *                       is made between right and left mouse clicks.
   */
  public void mouseMoved(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();

    //update mouse position in info label
    editor.posInfo.setText(Integer.toString(x) + ", " + Integer.toString(y));

    // LISTEN LEFT MOUSE BUTTON
    if (SwingUtilities.isLeftMouseButton(e)) {
      //update mouse position on info label
      editor.posInfo.setText(Integer.toString(x) + ", " + Integer.toString(y));
      // editor.currentShape = editor.shapeList.checkInside(x, y);
      if (editor.currentShape != null) {
        editor.currentShape.x = x;
        editor.currentShape.y = y;

        Rectangle rect = new Rectangle(x - 10, y - 10,
                                       editor.currentShape.getRealWidth() + 10,
                                       editor.currentShape.getRealHeight() + 10);
        editor.drawingArea.scrollRectToVisible(rect);
        if (x + editor.currentShape.getRealWidth() > editor.drawAreaSize.width) {
          editor.drawAreaSize.width = x + editor.currentShape.getRealWidth();
          editor.drawingArea.setPreferredSize(editor.drawAreaSize);
          editor.drawingArea.setPreferredSize(editor.drawAreaSize);
        }

        if (y + editor.currentShape.getRealHeight() >
            editor.drawAreaSize.height) {
          editor.drawAreaSize.height = y + editor.currentShape.getRealHeight();
          editor.drawingArea.setPreferredSize(editor.drawAreaSize);
          editor.drawingArea.revalidate();
        }
        editor.repaint();
      }
    }
  }

  /**
   * Mouse released event from the MouseListener. Invoked when a mouse button has been
   * released on a component.
   * @param e MouseEvent - Mouse event performed. In the method a distinction
   *                       is made between different states of the application.
   */
  public void mouseReleased(MouseEvent e) {
    Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
    editor.setCursor(cursor);

    if(SwingUtilities.isLeftMouseButton(e)) {
    if (state.equals(State.drag)) {
      state = State.selection;
    }
    if (state.equals(State.dragBox) || state.equals(State.selection)) {
      editor.selectShapesInsideBox(startX, startY, editor.mouseX, editor.mouseY);
      state = State.selection;
      editor.repaint();
    }
    if (editor.shapeList.getSelected() != null && editor.shapeList.getSelected().size() > 0) {
      String selShapes = editor.shapeList.getSelected().toString();
      if (selShapes != null) {
        selShapes = selShapes.replaceAll("null ", " ");
      }
    }

    if (state.equals(State.dragBreakPoint)) {
      state = State.selection;
    }

    if (state!=null && state.equals(State.drawRect) || state.equals(State.drawFilledRect) || state.equals(State.boundingbox)) {
      final int width = Math.abs(editor.mouseX - startX);
      final int height = Math.abs(editor.mouseY - startY);

      if(state.equals(State.boundingbox)) {
        BoundingBox box = new BoundingBox(Math.min(startX, editor.mouseX),
                             Math.min(startY, editor.mouseY), width, height);
        editor.boundingbox = box;
        // ONLY ONE BOUNDING BOX IS ALLOWED.
        editor.shapeList.add(box);
        editor.palette.boundingbox.setEnabled(false);
        state = State.selection;

      } else {
        Rect rect = new Rect(Math.min(startX, editor.mouseX),
                             Math.min(startY, editor.mouseY), width,
                             height, color.getRGB(), fill,
                             strokeWidth, getTransparency());
        editor.shapeList.add(rect);
      }
	  editor.repaint();
    } else

    if (state.equals(State.drawOval) || state.equals(State.drawFilledOval)) {
      int width = Math.abs(editor.mouseX - startX);
      int height = Math.abs(editor.mouseY - startY);
      Oval oval = new Oval(Math.min(startX,editor.mouseX),Math.min(startY,editor.mouseY), width,
                           height, color.getRGB(), fill,
                           strokeWidth, getTransparency());
      editor.shapeList.add(oval);
      // editor.repaint();
    } else

    if (state.equals(State.drawArc) || state.equals(State.drawFilledArc)) {
      int width = Math.abs(editor.mouseX - startX);
      int height = Math.abs(editor.mouseY - startY);
      Arc arc = new Arc(Math.min(startX,editor.mouseX),Math.min(startY,editor.mouseY), width, height, 0, 180, color.getRGB(), fill, strokeWidth, getTransparency());
      editor.shapeList.add(arc);
      // editor.repaint();
    }
    else

    if (state.equals(State.drawLine)) {
      Line line = new Line(startX, startY, editor.mouseX, editor.mouseY,
                           color.getRGB(), strokeWidth, getTransparency());
      editor.shapeList.add(line);
      // editor.repaint();
    } else

    if (state.equals(State.resize)) {
      state = State.selection;
    } else

    if (state.equals(State.freehand)) {
      drawDotOnClick(color);
    } else

    if (state.equals(State.eraser)) {
      editor.shapeList.eraseShape(editor.mouseX, editor.mouseY);
      editor.repaint();
    } else

    if (state.equals(State.drawText)) {
      openTextEditor();
    } else

    if (state.equals(State.addPort)) {
      openPortPropertiesDialog();
    }

    }

  }

  /**
   * Mouse entered event from the MouseMotionListener. Invoked when the mouse enters a component.
   * @param e MouseEvent - Mouse event performed.
   */
  public void mouseEntered(MouseEvent e) {
    Cursor cursor = new Cursor(Cursor.DEFAULT_CURSOR);
    editor.setCursor(cursor);
  }

  /**
   * Mouse exited event from the MouseMotionListener. Invoked when the mouse exits a component.
   * @param e MouseEvent - Mouse event performed.
   */
  public void mouseExited(MouseEvent e) {
    Cursor cursor = new Cursor(Cursor.HAND_CURSOR);
    editor.setCursor(cursor);
  }

  /**
   * Change colors of selected object(s).
   * @param col Color - color for selected object(s).
   */
  public void changeObjectColors(Color col) {
	state = State.selection;
    if(editor.shapeList!=null && editor.shapeList.size()>0) {
      for(int i=0;i<editor.shapeList.size();i++) {
        Shape s = (Shape)editor.shapeList.get(i);
        if(s!=null && s.isSelected()) {
          s.setColor(col);
          editor.repaint();
        }
      }
    }
  } // change object colors

  //*********************************************************************
   //	end of mouse control functions
   //*********************************************************************

    public void actionPerformed(ActionEvent e) {

      // JmenuItem chosen
      if (e.getSource().getClass().getName() == "javax.swing.JMenuItem"
          || e.getSource().getClass().getName() == "javax.swing.JCheckBoxMenuItem") {
        if (e.getActionCommand().equals(Menu.SAVE_SCHEME)) {
		  editor.saveScheme();
        }
        else if (e.getActionCommand().equals(Menu.CLEAR_ALL)) {
          editor.clearObjects();
        }
        else if (e.getActionCommand().equals(Menu.GRID)) {
          boolean isGridVisible = editor.drawingArea.isGridVisible();
          if(isGridVisible) {
            isGridVisible = false;
          } else {
            isGridVisible = true;
          }
          editor.drawingArea.setGridVisible(isGridVisible);
        }
		else if (e.getActionCommand().equals(Menu.CLASS_PROPERTIES)) {
		  ClassPropertiesDialog c1 = new ClassPropertiesDialog();
		  c1.setEmptyValuesValid(true);
		  c1.setVisible(true);
		}
        else if(e.getActionCommand().equals(Menu.CLONE)) {
          editor.cloneObject();
        }
        else if(e.getActionCommand().equals(Menu.SELECT_ALL)) {
          editor.selectAllObjects(true);
        }
        else if (e.getActionCommand().equals(Menu.LOAD_SCHEME)) {
		  editor.loadScheme();
        }
        else if (e.getActionCommand().equals(Menu.PRINT)) {
          editor.print();
        }
        else if (e.getActionCommand().equals(Menu.EXIT)) {
          editor.exitApplication();
        }
        else if (e.getActionCommand().equals(Menu.DOCS)) {
          String documentationUrl = editor.getSystemDocUrl();
          if (documentationUrl != null && documentationUrl.trim().length() > 0) {
            editor.openInBrowser(documentationUrl);
          }
          else {
            editor.showInfoDialog("Missing information",
                                  "No documentation URL defined in properties.");
          }
        }
        else if (e.getActionCommand().equals(Menu.LICENSE)) {
          new LicenseDialog(editor,null);
        }
        else if (e.getActionCommand().equals(Menu.ABOUT)) {
          new AboutDialog(editor,null);
        }
        else if (e.getActionCommand().equals(Menu.OBJECT_DELETE)) {
          editor.deleteObjects();
        }
        else if (e.getActionCommand().equals(Menu.BACKWARD)) {
          // MOVE OBJECT BACKWARD IN THE LIST
          // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
          editor.shapeList.sendBackward(editor.currentShape, 1);
          editor.repaint();
        }
        else if (e.getActionCommand().equals(Menu.FORWARD)) {
          // MOVE OBJECT FORWARD IN THE LIST
          // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
          editor.shapeList.bringForward(editor.currentShape, 1);
          editor.repaint();
        }
        else if (e.getActionCommand().equals(Menu.TOFRONT)) {
          // MOVE OBJECT TO THE FRONT IN THE LIST,
          // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
          editor.shapeList.bringToFront(editor.currentShape);
          editor.repaint();
        }
        else if (e.getActionCommand().equals(Menu.TOBACK)) {
          // MOVE OBJECT TO THE BACK IN THE LIST
          // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
          editor.shapeList.sendToBack(editor.currentShape);
          editor.repaint();
        }
        else if (e.getActionCommand().equals(Menu.PROPERTIES)) {
          /*
           ObjectPropertiesEditor prop = new ObjectPropertiesEditor(editor.currentObj);
                     prop.pack();
                     prop.setVisible(true); */
        }
        else if (e.getActionCommand().equals(Menu.GROUP)) {
          editor.groupObjects();
        }
		else if (e.getActionCommand().equals(Menu.FIXED)) {
		  editor.fixShape();
        }
        else if (e.getActionCommand().equals(Menu.UNGROUP)) {
          editor.ungroupObjects();
        }
        else if (e.getActionCommand().equals(Menu.SETTINGS)) {
          editor.openOptionsDialog();
        }
        else if (e.getActionCommand().equals(Menu.CLEAR_ALL)) {
          editor.clearObjects();
        }
        else if (e.getActionCommand().equals(Look.LOOK_WINDOWS)) {
          try {
            Look.changeLayout(Look.LOOK_WINDOWS);
          }
          catch (Exception uie) {
            uie.printStackTrace();
          }
        }
        else if (e.getActionCommand().equals(Look.LOOK_METAL)) {
          try {
            Look.changeLayout(Look.LOOK_METAL);
          }
          catch (Exception uie) {
            uie.printStackTrace();
          }
        }
        else if (e.getActionCommand().equals(Look.LOOK_MOTIF)) {
          try {
            Look.changeLayout(Look.LOOK_MOTIF);
          }
          catch (Exception uie) {
            uie.printStackTrace();
          }
        }
        else if (e.getActionCommand().equals(Look.LOOK_CUSTOM)) {
          try {
            Look.changeLayout(Look.LOOK_CUSTOM);
          }
          catch (Exception uie) {
            uie.printStackTrace();
          }
        }
        else if (e.getActionCommand().equals(Menu.EXPORT_CLASS)) {
          editor.exportShapesToXML(); // the class is not a relation
        }
		else if (e.getActionCommand().equals(Menu.EXPORT_TO_PACKAGE)) {
		  editor.exportShapesToPackage(); // append the graphics to a package
		}
		else if (e.getActionCommand().equals(Menu.CREATE_PACKAGE)) {
		  editor.createPackage();
        }

      }

      //Jbutton pressed
      if (e.getSource().getClass().getName() == "javax.swing.JButton") {
        if (e.getActionCommand().equals(State.selection)) {
          editor.mListener.setState(State.selection);
        }
        else if (e.getActionCommand().equals(State.magnifier)) {
          editor.mListener.setState(State.magnifier);
        }
        else if (e.getActionCommand().equals(State.drawLine)) {
          editor.mListener.setState(State.drawLine);
        }
        else if (e.getActionCommand().equals(State.drawArc)) {
          editor.mListener.setState(State.drawArc);
        }
        else if (e.getActionCommand().equals(State.drawFilledArc)) {
          editor.mListener.setState(State.drawFilledArc);
        }
        else if (e.getActionCommand().equals(State.drawRect)) {
          editor.mListener.setState(State.drawRect);
        }
        else if (e.getActionCommand().equals(State.boundingbox)) {
          editor.mListener.setState(State.boundingbox);
        }
        else if (e.getActionCommand().equals(State.drawOval)) {
          editor.mListener.setState(State.drawOval);
        }
        else if (e.getActionCommand().equals(State.drawFilledRect)) {
          editor.mListener.setState(State.drawFilledRect);
        }
        else if (e.getActionCommand().equals(State.drawFilledOval)) {
          editor.mListener.setState(State.drawFilledOval);
        }
        else if (e.getActionCommand().equals(State.chooseColor)) {
          Color col = JColorChooser.showDialog(editor, "Choose Color", Color.black);
          if (col != null) {
            this.color = col;
          }
          changeObjectColors(col);
        }
        else if (e.getActionCommand().equals(State.freehand)) {
          editor.mListener.setState(State.freehand);
        }
        else if (e.getActionCommand().equals(State.eraser)) {
          editor.mListener.setState(State.eraser);
        }
        else if (e.getActionCommand().equals(State.drawText)) {
          editor.mListener.setState(State.drawText);
        }
        else if (e.getActionCommand().equals(State.addPort)) {
          editor.mListener.setState(State.addPort);
        }
      }
	  editor.drawingArea.grabFocus();
    }
}
