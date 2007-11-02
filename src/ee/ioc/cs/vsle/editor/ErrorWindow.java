package ee.ioc.cs.vsle.editor;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;

import ee.ioc.cs.vsle.util.db;

public class ErrorWindow extends JFrame {

    JTextArea textArea;
    JPanel errorText;
    JScrollPane areaScrollPane;

    private static ErrorWindow instance = new ErrorWindow();

    private ErrorWindow() {
        super( "Error" );

        addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );
            
        textArea = new JTextArea();
        textArea.setFont( RuntimeProperties.getFont() );
        areaScrollPane = new JScrollPane( textArea );

        areaScrollPane.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        errorText = new JPanel();
        errorText.setLayout( new BorderLayout() );
        errorText.add( areaScrollPane, BorderLayout.CENTER );

        getContentPane().add( errorText );
        textArea.setEditable( false );
        textArea.setWrapStyleWord( true );
        textArea.setLineWrap( true );
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize( 600, 300 );
        validate();
    }

    @Override
    public void dispose() {
        textArea.setText(null);
        super.dispose();
    }

    /**
     * Shows the specified message in a window.
     * This method is thread safe.
     * @param message the message to show
     */
    public static void showErrorMessage(final String message) {
        if (SwingUtilities.isEventDispatchThread()) {
            appendAndShow(message);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
               public void run() {
                   appendAndShow(message);
               }
            });
        }
    }

    /**
     * Appends the specified message to the error window and brings the
     * window to front.
     * @param message the message to append
     */
    static void appendAndShow(String message) {
        assert SwingUtilities.isEventDispatchThread();

        int curLen = instance.textArea.getDocument().getLength();
        int firstErrorIndex = message.indexOf("ERROR");

        instance.textArea.append(message + "\n\n");
        // The window must be set visible before modelToView() call,
        // otherwise the size of the textarea is not computed yet.
        instance.setVisible(true);

        // The compiler generated error messages are often very long but
        // the root cause of the error is described after the first occurence
        // of the string "ERROR". Make it easier for the user to spot errors
        // by scrolling to the hopefully most relevant location.
        JViewport vp = instance.areaScrollPane.getViewport();
        try {
            Rectangle r = instance.textArea.modelToView(curLen 
                    + firstErrorIndex);
            Point p = vp.getViewPosition();
            p.setLocation(0, r.y);
            vp.setViewPosition(p);
            // Hilight the first occurence of "ERROR"
            if (firstErrorIndex > -1)
                instance.textArea.select(curLen + firstErrorIndex,
                        curLen + firstErrorIndex + 5);
        } catch (BadLocationException e) {
            db.p(e);
        }

        if( instance.getState() == Frame.ICONIFIED ) {
            instance.setState( Frame.NORMAL );
        }
        instance.toFront();
    }
}
