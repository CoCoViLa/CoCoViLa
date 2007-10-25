/**
 * 
 */
package ee.ioc.cs.vsle.editor;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import ee.ioc.cs.vsle.util.*;

/**
 * Class for collecting error messages
 */
public class DiagnosticsCollector {

    private List<String> messages = new ArrayList<String>();

    public void collectDiagnostic( String msg ) {
        if ( RuntimeProperties.isLogDebugEnabled() )
            db.p( msg );

        messages.add( msg );
    }

    public List<String> getMessages() {
        return messages;
    }

    /**
     * @return true, if there were problems, false otherwise.
     */
    public boolean hasProblems() {
        return getMessages().size() > 0;
    }
    
    public interface Diagnosable {
        public DiagnosticsCollector getDiagnostics();
    }
    
    public static boolean promptLoad( Component relative, DiagnosticsCollector collector, String title, String source ) {
        final JDialog dialog = new JDialog( Editor.getInstance() );
        dialog.setModal( true );
        dialog.setLocationRelativeTo( relative );
        dialog.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
        dialog.setTitle( title );

        JLabel topLabel = new JLabel( "The following problems occured " + "while loading the " + source + ":" );
        topLabel.setBorder( new EmptyBorder( 20, 5, 5, 5 ) );

        dialog.add( topLabel, BorderLayout.NORTH );

        JTextArea txt = new JTextArea( 5, 40 );
        txt.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        txt.setLineWrap( true );
        txt.setWrapStyleWord( true );
        txt.setEditable( false );
        for ( String s : collector.getMessages() ) {
            txt.append( " - " );
            txt.append( s );
            txt.append( "\n" );
        }
        txt.setCaretPosition( 0 );
        JScrollPane scrollPane = new JScrollPane( txt, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        dialog.add( scrollPane, BorderLayout.CENTER );

        final JButton btnContinue = new JButton( "Continue" );
        JButton btnCancel = new JButton( "Cancel" );

        final boolean[] result = new boolean[ 1 ];
        result[ 0 ] = false; // close is Cancel

        btnCancel.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.setVisible( false );
            }
        } );

        btnContinue.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.setVisible( false );
                result[ 0 ] = true;
            }
        } );

        JPanel btnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
        btnPanel.add( btnContinue );
        btnPanel.add( btnCancel );
        dialog.add( btnPanel, BorderLayout.SOUTH );
        dialog.pack();
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                btnContinue.requestFocus();
            }
        } );
        dialog.setVisible( true );
        dialog.dispose();

        return result[ 0 ];
    }
}
