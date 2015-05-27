package ee.ioc.cs.vsle.util;

/*
 * Java Console showing System.out and System.err
 * 
 * Initially taken from
 * http://www.comweb.nl/java/Console/Console.html
 */
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Console extends WindowAdapter implements ActionListener {
    private static final Logger logger = LoggerFactory.getLogger(StreamFollower.class);

    private JFrame frame;
    private JTextArea textArea;
    private Thread sysOutFollower;
    private Thread sysErrFollower;
    private volatile boolean quit;

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

        final PipedInputStream pin = new PipedInputStream();
        try {
            PipedOutputStream pout = new PipedOutputStream( pin );
            System.setOut( new PrintStream( new TeeOutputStream(systemOut, pout), true ) );
        } catch ( java.io.IOException io ) {
            textArea.append( "Couldn't redirect STDOUT to this console\n"
                    + io.getMessage() );
        } catch ( SecurityException se ) {
            textArea.append( "Couldn't redirect STDOUT to this console\n"
                    + se.getMessage() );
        }

        final PipedInputStream pin2 = new PipedInputStream();
        try {
            PipedOutputStream pout2 = new PipedOutputStream( pin2 );
            System.setErr( new PrintStream( new TeeOutputStream(systemErr, pout2), true ) );
        } catch ( java.io.IOException io ) {
            textArea.append( "Couldn't redirect STDERR to this console\n"
                    + io.getMessage() );
        } catch ( SecurityException se ) {
            textArea.append( "Couldn't redirect STDERR to this console\n"
                    + se.getMessage() );
        }

        quit = false; // signals the Threads that they should exit

        sysOutFollower = new StreamFollower(pin, StreamRestorer.SYS_OUT, "Console_out" );
        sysOutFollower.start();
        //
        sysErrFollower = new StreamFollower(pin2, StreamRestorer.SYS_ERR, "Console_err" );
        sysErrFollower.start();
    }

    @Override
    public void windowClosed( WindowEvent evt ) {
        quit = true;
        try {
            sysOutFollower.interrupt();
            sysOutFollower.join(1000);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        try {
            sysErrFollower.interrupt();
            sysErrFollower.join(1000);
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        instance = null;
    }

    @Override
    public void windowClosing( WindowEvent evt ) {
        frame.setVisible( false ); // default behaviour of JFrame	
        frame.dispose();
    }

    @Override
    public void actionPerformed( ActionEvent evt ) {
        textArea.setText( "" );
    }

    private void scrollToBottom() {
        if( textArea != null ) {
            textArea.setCaretPosition(textArea.getDocument().getLength());
        }
    }
    
    public String readLine( PipedInputStream in )
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

    private void appendToConsole(final String s) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                textArea.append(s);
                scrollToBottom();
            }
        });
    }

    private class StreamFollower extends Thread {

        private final PipedInputStream pin;
        private final StreamRestorer restorer;

        StreamFollower(PipedInputStream pin, StreamRestorer restorer, String name) {
            super(name);
            setDaemon(true);
            this.pin = pin;
            this.restorer = restorer;
        }

        @Override
        public void run() {
            try {
                while(!quit) {
                    sleep(1);
                    if (pin.available() != 0) {
                        String input = readLine(pin);
                        appendToConsole(input);
                    }
                }
            }
            catch (InterruptedException ie) {
                //quit
            }
            catch ( Exception e ) {
                appendToConsole("\nConsole reports an Internal error.");
                appendToConsole("The error is: " + e);
            }
            finally {
                try {
                    pin.close();
                } catch (IOException e) {
                    logger.error("Error closing stream", e);
                }
                restorer.restore();
            }
        }
    }

    interface StreamRestorer {
        void restore();
        StreamRestorer SYS_OUT = new StreamRestorer() {

            @Override
            public void restore() {
                System.setOut( systemOut );
            }
        };

        StreamRestorer SYS_ERR = new StreamRestorer() {

            @Override
            public void restore() {
                System.setErr(systemErr );
            }
        };
    }
}