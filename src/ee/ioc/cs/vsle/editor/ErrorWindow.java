package ee.ioc.cs.vsle.editor;

import java.awt.Font;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ErrorWindow extends JFrame implements ActionListener {

	JTextArea textArea;
	JPanel errorText;

        private static ErrorWindow instance = new ErrorWindow();

        private ErrorWindow() {
            super( "Error" );

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
