package ee.ioc.cs.vsle.editor;

import java.awt.event.*;
import java.awt.*;
import javax.swing.AbstractAction;
import javax.swing.*;


import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

public class KeyOps implements KeyListener {

	Editor editor;

	public KeyOps(Editor editor) {
		this.editor = editor;
	}

	public void keyPressed(KeyEvent e) {
	} // keyPressed

	public void keyTyped(KeyEvent e) {
	} // keyTyped

	public void keyReleased(KeyEvent e) {

		if (e.getKeyCode() == 127) {        // event: delete object(s), key: del
			editor.deleteObjects();
		} else if (e.getKeyCode() == 68) {   // event: clone object(s), key: ctrl+d
			editor.cloneObject();
		} else if (e.getKeyCode() == 71) { // event: group objects, key: ctrl+g
			editor.groupObjects();
		} else if (e.getKeyCode() == 85) { // event: ungroup objects, key: ctrl+u
			editor.ungroupObjects();
		} else if (e.getKeyCode() == 39) { // event: move shape to right, key: right arrow key
			editor.moveObject(1, 0);
		} else if (e.getKeyCode() == 37) { // event: move shape to left, key: left arrow key
			editor.moveObject(-1, 0);
		} else if (e.getKeyCode() == 38) { // event: move shape up, key: up arrow key
			editor.moveObject(0, -1);
		} else if (e.getKeyCode() == 40) { // event: move shape down, key: down arrow key
			editor.moveObject(0, 1);
		} else if (e.getKeyCode() == 72) {// event: hilight object ports, key: ctrl+h
			editor.hilightPorts();
		} else if (e.getKeyCode() == 82) {	// event: open object properties, key: ctrl+r
			editor.openPropertiesDialog();
		} else if (e.getKeyCode() == 27) {	// event: escape key, return to selection
			editor.stopRelationAdding();
		}


	} // keyReleased

}
