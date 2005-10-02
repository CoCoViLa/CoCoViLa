package ee.ioc.cs.vsle.editor;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.undo.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UndoRedoDocumentPanel extends JPanel {

    private Document m_document;
    protected UndoManager undo = new UndoManager();

    private JButton m_buttonUndo = new JButton( new ImageIcon( "images/undo.png" ) );
    private JButton m_buttonRedo = new JButton( new ImageIcon( "images/redo.png" ) );

    public UndoRedoDocumentPanel( Document doc ) {
        m_document = doc;

        init();
    }

    private void init() {
        setLayout( new FlowLayout( FlowLayout.LEFT ) );
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
        if ( undo.canUndo() ) {
            m_buttonUndo.setEnabled( true );
        } else {
            m_buttonUndo.setEnabled( false );
        }
    }

    private void updateRedoState() {
        if ( undo.canRedo() ) {
            m_buttonRedo.setEnabled( true );
        } else {
            m_buttonRedo.setEnabled( false );
        }
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
