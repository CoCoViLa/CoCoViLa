package ee.ioc.cs.vsle.iconeditor;

import ee.ioc.cs.vsle.graphics.*;

import java.awt.event.*;

public class IconKeyOps implements KeyListener {

  IconEditor editor;

  public IconKeyOps(IconEditor editor) {
	this.editor = editor;
  } // IconKeyOps

  public void keyPressed(KeyEvent e) {
  } // keyPressed

  public void keyTyped(KeyEvent e) {
  } // keyTyped

  public void keyReleased(KeyEvent e) {
	if(e.getKeyCode()==127) {        // event: delete object(s), key: del
	  editor.deleteObjects();
	}
	else if(e.getKeyCode()==68) {   // event: clone object(s), key: ctrl+d
	 editor.cloneObject();
   }
   else if(e.getKeyCode()==71) { // event: group objects, key: ctrl+g
	 editor.groupObjects();
   }
   else if(e.getKeyCode()==85) { // event: ungroup objects, key: ctrl+u
	 editor.ungroupObjects();
   }
   else if(e.getKeyCode()==88) { // event: fix shape: key: ctrl+x
	 editor.fixShape();
   }
   else if(e.getKeyCode()==45 && !e.isControlDown()) { // event: move shape backward, key: minus
	 if(editor.shapeList.getSelected().size()==1) {
	   // MOVE OBJECT BACKWARD IN THE LIST
	   // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
	   editor.currentShape = (Shape)editor.shapeList.getSelected().get(0);
	   if(editor.shapeList.indexOf(editor.currentShape)!=0) {
		 editor.shapeList.sendBackward(editor.currentShape, 1);
		 editor.repaint();
	   }
	 }
   } else if(e.getKeyCode()==521 && !e.isControlDown()) { // event: move shape forward, key: plus
	 if(editor.shapeList.getSelected().size()==1) {
	   // MOVE OBJECT FORWARD IN THE LIST
	   // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
	   editor.currentShape = (Shape)editor.shapeList.getSelected().get(0);
	   if(editor.shapeList.indexOf(editor.currentShape)!=editor.shapeList.size()-1){
		 editor.shapeList.bringForward(editor.currentShape, 1);
		 editor.repaint();
	   }
	 }
   } else if(e.getKeyCode()==521 && e.isControlDown()) { // event: move shape to front, key: ctrl + plus
	 if(editor.shapeList.getSelected().size()==1) {
	   // MOVE OBJECT TO THE FRONT IN THE LIST,
	   // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
	   editor.currentShape = (Shape)editor.shapeList.getSelected().get(0);
	   if(editor.shapeList.indexOf(editor.currentShape)!=editor.shapeList.size()-1) {
		 editor.shapeList.bringToFront(editor.currentShape);
		 editor.repaint();
	   }
	 }
   } else if(e.getKeyCode()==45 && e.isControlDown()) { // event: move shape to back, key: ctrl + minus
	 if(editor.shapeList.getSelected().size()==1) {
	   // MOVE OBJECT TO THE BACK IN THE LIST
	   // NOTE THAT THE LIST IS ITERATED IN REVERSE ORDER WHEN REPAINTED
	   editor.currentShape = (Shape)editor.shapeList.getSelected().get(0);
	   if(editor.shapeList.indexOf(editor.currentShape)!=0) {
		 editor.shapeList.sendToBack(editor.currentShape);
		 editor.repaint();
	   }
	 }
   }
   else if(e.getKeyCode()==39) { // event: move shape to right, key: right arrow key
	 editor.moveObject(1,0);
   } else if(e.getKeyCode()==37) { // event: move shape to left, key: left arrow key
	 editor.moveObject(-1,0);
   } else if(e.getKeyCode()==38) { // event: move shape up, key: up arrow key
	 editor.moveObject(0,-1);
   } else if(e.getKeyCode()==40) { // event: move shape down, key: down arrow key
	 editor.moveObject(0,1);
   }


  } // keyReleased

}
