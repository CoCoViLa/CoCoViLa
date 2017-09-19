package ee.ioc.cs.vsle.editor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;

public class ErrorWindow extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(ErrorWindow.class);

    JTextArea textArea;
    JPanel errorText;
    JScrollPane areaScrollPane;

    private FontChangeEvent.Listener fontListener = new FontChangeEvent.Listener() {

        @Override
        public void fontChanged( FontChangeEvent e ) {
            if( e.getElement() == RuntimeProperties.Fonts.ERRORS ) {
                textArea.setFont( e.getFont() );
            }
        }
    };
    
    private static ErrorWindow instance = new ErrorWindow();

    private ErrorWindow() {
        super( "Error" );

        addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );
            
        textArea = new JTextArea();
        textArea.setFont( RuntimeProperties.getFont( RuntimeProperties.Fonts.ERRORS ) );
        areaScrollPane = new JScrollPane( textArea );

        FontChangeEvent.addFontChangeListener( fontListener );
        
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
        FontChangeEvent.removeFontChangeListener( fontListener );
        fontListener = null;
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
     * window to front.  If the message is null the string "null\n\n" is
     * appended.
     * @param message the message to append
     */
    static void appendAndShow(String message) {
        assert SwingUtilities.isEventDispatchThread();

        int oldLen = instance.textArea.getDocument().getLength();
        instance.textArea.append(message + "\n\n");
        // The window must be set visible before modelToView() call,
        // otherwise the size of the textarea is not computed yet.
        instance.setVisible(true);

        int firstErrorIndex = (message == null) ? -1 : message.indexOf("ERROR");

        if( firstErrorIndex > -1 ) {
        	// The compiler generated error messages are often very long but
        	// the root cause of the error is described after the first occurence
        	// of the string "ERROR". Make it easier for the user to spot errors
        	// by scrolling to the hopefully most relevant location.
        	JViewport vp = instance.areaScrollPane.getViewport();
        	try {
        		Rectangle r = instance.textArea.modelToView(oldLen 
        				+ firstErrorIndex);
        		Point p = vp.getViewPosition();
        		p.setLocation(0, r.y);
        		vp.setViewPosition(p);
        		// Hilight the first occurence of "ERROR"
        		instance.textArea.select(oldLen + firstErrorIndex,
        				oldLen + firstErrorIndex + 5);
        	} catch (BadLocationException e) {
            logger.error(null, e);
        	}
        }
        
        if( instance.getState() == Frame.ICONIFIED ) {
            instance.setState( Frame.NORMAL );
        }
        instance.toFront();
    }
}
