/**
 * 
 */
package ee.ioc.cs.vsle.event;

import ee.ioc.cs.vsle.util.db;

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

		m_eventQueue  = new EventQueue();

		m_eventQueue.start();

		// Wait until the queue gets started
		while ( !m_eventQueue.isRunning() )
		{
			try
			{
				Thread.currentThread().sleep( 50 );
			}
			catch ( InterruptedException ex ) {}
		}

		db.p( "The queue is running" );
	}

	public static void queueEvent( BaseEvent event )
	{
		System.err.println( "queueEvent: " + event + " " + s_instance );
		s_instance.addEventToQueue( event );
	}

	/**
	 * Method <code>addEventToQueue</code>
	 *
	 *
	 * @param <code>event</code> is of <code>FTOevent</code> type
	 *
	 */
	private void addEventToQueue( BaseEvent event )
	{
		if ( m_eventQueue == null )
		{
			return;
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