package ee.ioc.cs.vsle.util;

/*
 * Java Console showing System.out and System.err
 * 
 * Initially taken from
 * http://www.comweb.nl/java/Console/Console.html
 * 
 * TODO: does not work well for large amount of streaming data
 */
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Console extends WindowAdapter implements WindowListener,
        ActionListener, Runnable {
    private JFrame frame;
    private JTextArea textArea;
    private Thread reader;
    private Thread reader2;
    private boolean quit;

    private final PipedInputStream pin = new PipedInputStream();
    private final PipedInputStream pin2 = new PipedInputStream();
    
    //backup
    private final static PrintStream systemOut = System.out;
    private final static PrintStream systemErr = System.err;
    
    private static Console instance;
    
    public static void show() {
        if( instance == null ) {
            instance = new Console();
        }
        instance.frame.toFront();
    }
    
    private Console() {
        // create all components and add them
        frame = new JFrame( "Java Console" );
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension( ( screenSize.width / 2 ),
                ( screenSize.height / 2 ) );
        int x = ( frameSize.width / 2 );
        int y = ( frameSize.height / 2 );
        frame.setBounds( x, y, frameSize.width, frameSize.height );

        textArea = new JTextArea();
        textArea.setEditable( false );
        JButton button = new JButton( "clear" );

        frame.getContentPane().setLayout( new BorderLayout() );
        frame.getContentPane().add( new JScrollPane( textArea ),
                BorderLayout.CENTER );
        frame.getContentPane().add( button, BorderLayout.SOUTH );
        frame.setVisible( true );

        frame.addWindowListener( this );
        button.addActionListener( this );

        try {
            PipedOutputStream pout = new PipedOutputStream( this.pin );
            System.setOut( new PrintStream( pout, true ) );
        } catch ( java.io.IOException io ) {
            textArea.append( "Couldn't redirect STDOUT to this console\n"
                    + io.getMessage() );
        } catch ( SecurityException se ) {
            textArea.append( "Couldn't redirect STDOUT to this console\n"
                    + se.getMessage() );
        }

        try {
            PipedOutputStream pout2 = new PipedOutputStream( this.pin2 );
            System.setErr( new PrintStream( pout2, true ) );
        } catch ( java.io.IOException io ) {
            textArea.append( "Couldn't redirect STDERR to this console\n"
                    + io.getMessage() );
        } catch ( SecurityException se ) {
            textArea.append( "Couldn't redirect STDERR to this console\n"
                    + se.getMessage() );
        }

        quit = false; // signals the Threads that they should exit

        // Starting two seperate threads to read from the PipedInputStreams				
        //
        reader = new Thread( this, "Console_out" );
        reader.setDaemon( true );
        reader.start();
        //
        reader2 = new Thread( this, "Console_err" );
        reader2.setDaemon( true );
        reader2.start();

    }

    @Override
    public synchronized void windowClosed( WindowEvent evt ) {
        quit = true;
        this.notifyAll(); // stop all threads
        try {
            reader.join( 1000 );
            pin.close();
        } catch ( Exception e ) {
        }
        try {
            reader2.join( 1000 );
            pin2.close();
        } catch ( Exception e ) {
        }
        
        instance = null;
    }

    @Override
    public synchronized void windowClosing( WindowEvent evt ) {
        frame.setVisible( false ); // default behaviour of JFrame	
        frame.dispose();
    }

    @Override
    public synchronized void actionPerformed( ActionEvent evt ) {
        textArea.setText( "" );
    }

    @Override
    public synchronized void run() {
        try {
            while ( Thread.currentThread() == reader ) {
                try {
                    this.wait( 100 );
                } catch ( InterruptedException ie ) {
                }
                if ( quit ) {
                    System.setOut( systemOut );
                    return;
                }
                if ( pin.available() != 0 ) {
                    String input = this.readLine( pin );
                    textArea.append( input );
                    scrollToBottom();
                }
            }

            while ( Thread.currentThread() == reader2 ) {
                try {
                    this.wait( 100 );
                } catch ( InterruptedException ie ) {
                }
                if ( quit ) {
                    System.setErr( systemErr );
                    return;
                }
                if ( pin2.available() != 0 ) {
                    String input = this.readLine( pin2 );
                    textArea.append( input );
                    scrollToBottom();
                }
            }
        } catch ( Exception e ) {
            textArea.append( "\nConsole reports an Internal error." );
            textArea.append( "The error is: " + e );
            scrollToBottom();
        }
    }

    private void scrollToBottom() {
        if( textArea != null )
            textArea.setCaretPosition(textArea.getDocument().getLength());
    }
    
    public synchronized String readLine( PipedInputStream in )
            throws IOException {
        String input = "";
        do {
            int available = in.available();
            if ( available == 0 )
                break;
            byte b[] = new byte[available];
            in.read( b );
            input = input + new String( b, 0, b.length );
        } while ( !input.endsWith( "\n" ) && !input.endsWith( "\r\n" ) && !quit );
        return input;
    }

}