/**
 * 
 */
package ee.ioc.cs.vsle.event;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class EventSystem {
	
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
	
	private void restartQueue() {
		if ( RuntimeProperties.isLogInfoEnabled() ) 
			db.p( "The queue is restarting" );
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
			m_eventQueue.shotdown();

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
	 * Constructor <code>FTOeventSystem</code>
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

		if ( RuntimeProperties.isLogDebugEnabled() ) 
			db.p( "The queue is running" );
		
		MemoryWarningSystem.getInstance().addListener(new MemoryWarningSystem.Listener() {
			public void memoryUsageLow(long usedMemory, long maxMemory) {
				if ( RuntimeProperties.isLogInfoEnabled() ) 
					db.p("Memory usage low!!!");
				restartQueue();
			}
		});
		
		MemoryWarningSystem.setPercentageUsageThreshold( 0.6 );
	}

	public static void queueEvent( BaseEvent event )
	{
		s_instance.addEventToQueue( event );
	}

	/**
	 * Method <code>addEventToQueue</code>
	 *
	 * @param event is of <code>FTOevent</code> type
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
			db.p( "Cannot add an event to the queue: " + ex.getMessage() );
		}
	}
}