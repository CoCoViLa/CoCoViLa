/**
 * 
 */
package ee.ioc.cs.vsle.event;

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

import java.awt.GraphicsEnvironment;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ee.ioc.cs.vsle.editor.Editor;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pavelg
 *
 */
public class EventSystem {

	private static final Logger logger = LoggerFactory.getLogger(EventSystem.class);

	private static EventSystem   s_instance = new EventSystem();

	private EventQueue           m_eventQueue;

	public static EventQueue getQueueForDebugging()
	{

		if ( s_instance == null )
		{
			return null;
		}

		return s_instance.m_eventQueue;
	}

	private void startQueue() {
		
		if( m_eventQueue == null ) {
			m_eventQueue  = new EventQueue();

			m_eventQueue.start();
		}
	}
	
    // Thread.stop() is really bad because it can leave objects in an
    // inconsistent state:
    // http://java.sun.com/javase/6/docs/technotes/guides/concurrency/threadPrimitiveDeprecation.html
    // We should maybe implement something safer but until then there
    // is no need to bother users with cryptic warning messages.
    @SuppressWarnings("deprecation")
    private void restartQueue() {
		logger.info( "The queue is restarting" );
		System.gc();
		m_eventQueue.stop();
		shutdown();
		startQueue();
	}
	
	/**
	 * Method <code>shutdown</code>
	 *
	 *
	 */
	public void shutdown()
	{

		if ( m_eventQueue != null )
		{
			m_eventQueue.shutdown();

			m_eventQueue = null;
		}
	}

	/**
	 * Method <code>getInstance</code>
	 *
	 *
	 * @return the value of <code>FTOeventSystem</code> type
	 *
	 */
	public static EventSystem getInstance()
	{
		return s_instance;
	}

	/**
	 * Constructor <code>EventSystem</code>
	 *
	 *
	 */
	private EventSystem()
	{

		startQueue();

		// Wait until the queue gets started
		while ( !m_eventQueue.isRunning() )
		{
			try
			{
				Thread.sleep(50);
			}
			catch ( InterruptedException ex ) {}
		}

		logger.debug( "The queue is running" );
		
		MemoryWarningSystem.getInstance().addListener(new MemoryWarningSystem.Listener() {
			public void memoryUsageLow(long usedMemory, long maxMemory) {
				logger.info("Memory usage low!!!");
				restartQueue();
				displayWarning();
			}
		});
		
		MemoryWarningSystem.setPercentageUsageThreshold( 0.95 );
	}

    void displayWarning() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }

        final String title = "Warning: Application memory resources exhausted";
        final String message = 
                  "The processing thread and job queue was destroyed because it ran\n"
                + "out of memory.\n\n"

                + "You need to rerun the last task to get the results.\n\n"

                + "Seeing this message means that the application may have been left\n"
                + "in an inconsistent state and this may result in unexpected behaviour.\n\n"

                + "To avoid the problem in the future more resources could be given to\n"
                + "the application (-Xmx). This can also mean the package needs to be fixed\n"
                + "or the task should be refined.";

        if (SwingUtilities.isEventDispatchThread()) {
            JOptionPane.showMessageDialog(Editor.getInstance(), message,
                    title, JOptionPane.WARNING_MESSAGE);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JOptionPane.showMessageDialog(Editor.getInstance(), message,
                            title, JOptionPane.WARNING_MESSAGE);
                }
            });
        }
    }

	public static void queueEvent( BaseEvent event )
	{
		s_instance.addEventToQueue( event );
	}

	/**
	 * Method <code>addEventToQueue</code>
	 *
	 * @param event is of <code>BaseEvent</code> type
	 */
	private void addEventToQueue( BaseEvent event )
	{
		if ( m_eventQueue == null ) {
			return;
		} else if( !m_eventQueue.isAlive() ) {
			//restart the queue if for example OutOfMemory error happens
			restartQueue();
		}
		
		try
		{
			m_eventQueue.addEvent( event );
		}
		catch ( EventQueueException ex )
		{
			logger.error( "Cannot add an event to the queue: " + ex.getMessage() );
		}
	}
}
