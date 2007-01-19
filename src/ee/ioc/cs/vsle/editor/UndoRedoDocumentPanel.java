package ee.ioc.cs.vsle.editor;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.*;

import ee.ioc.cs.vsle.util.*;

import java.awt.*;
import java.awt.event.*;

public class UndoRedoDocumentPanel extends JPanel {

    private Document m_document;
    protected UndoManager undo = new UndoManager();

    private JButton m_buttonUndo = new JButton( FileFuncs.getImageIcon( "images/undo.png", false ) );
    private JButton m_buttonRedo = new JButton( FileFuncs.getImageIcon( "images/redo.png", false ) );

    public UndoRedoDocumentPanel( Document doc ) {
        m_document = doc;

        init();
    }

    private void init() {
        setLayout( new FlowLayout( FlowLayout.LEFT ) );
        setOpaque(false);
        add( m_buttonUndo );
        add( m_buttonRedo );

        m_document.addUndoableEditListener( new MyUndoableEditListener() );

        m_buttonUndo.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    undo.undo();
                } catch ( CannotUndoException ex ) {}
                updateUndoState();
                updateRedoState();
            }
        } );

        m_buttonRedo.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    undo.redo();
                } catch ( CannotRedoException ex ) {}
                updateRedoState();
                updateUndoState();
            }
        } );

    }

    private void updateUndoState() {
    	m_buttonUndo.setEnabled( undo.canUndo() );
    }

    private void updateRedoState() {
    	m_buttonRedo.setEnabled( undo.canRedo() );
    }

    private class MyUndoableEditListener implements UndoableEditListener {
    	private int hack = 0;
    	
        public void undoableEditHappened( UndoableEditEvent e ) {
        	if( hack++ != 0 )
        		undo.addEdit( e.getEdit() );
            
            updateRedoState();
            updateUndoState();
        }
    }

}
