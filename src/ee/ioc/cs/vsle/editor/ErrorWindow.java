package ee.ioc.cs.vsle.editor;

import java.awt.*;

import java.awt.event.*;

import javax.swing.*;

public class ErrorWindow extends JFrame implements ActionListener {

	JTextArea textArea;
	JPanel errorText;

        private static ErrorWindow instance = new ErrorWindow();

        private ErrorWindow() {
            super( "Error" );

            addComponentListener( new ComponentResizer( ComponentResizer.CARE_FOR_MINIMUM ) );
            
            textArea = new JTextArea();
            textArea.setFont( RuntimeProperties.font );
            JScrollPane areaScrollPane = new JScrollPane( textArea );

            areaScrollPane.setVerticalScrollBarPolicy(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );

            errorText = new JPanel();
            errorText.setLayout( new BorderLayout() );
            errorText.add( areaScrollPane, BorderLayout.CENTER );

            getContentPane().add( errorText );
            textArea.setEditable( false );
            setDefaultCloseOperation(HIDE_ON_CLOSE);
            setSize( 600, 300 );
            validate();

        }

        public void setVisible( boolean value ) {
            if( !value ) {
                this.textArea.setText( "" );
            }
            super.setVisible( value );
        }

        public static void showErrorMessage( String message ) {
            instance.textArea.append( message + "\n\n");

            instance.setVisible( true );
            if( instance.getState() == JFrame.ICONIFIED ) {
                instance.setState( JFrame.NORMAL );
            }
            instance.toFront();
        }

	public void actionPerformed(ActionEvent e) {
	}
}
