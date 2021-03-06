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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author pavelg
 *
 */
public class EventQueue extends Thread {

	private static final Logger logger = LoggerFactory.getLogger(EventQueue.class);

	private int m_eventsAdded       = 0;
	private int m_eventsConsumed    = 0;

	private long m_prevEventTime = System.currentTimeMillis();
	private long m_maxTimeDelta  = 1000;    //1000 ms
	private long m_startTime     = System.currentTimeMillis();

	/** Field <code>firstUsedCell</code> */
	private EventCell m_firstUsedCell = null;

	/** Field <code>lastUsedCell</code> */
	private EventCell m_lastUsedCell = null;

	/** Field <code>totalSize</code> */
	private int m_totalSize = 0;

	/** Field <code>usedSize</code> */
	private int m_usedSize = 0;

	/** Field <code>m_running</code> */
	private volatile boolean m_running = false;
	
	/**
	 * Method <code>getStartTime</code>
	 *
	 *
	 * @return the value of <code>long</code> type
	 *
	 */
	public final long getStartTime()
	{
		return m_startTime;
	}

	/**
	 * Method <code>getOtherEventsAdded</code>
	 *
	 *
	 * @return the value of <code>int</code> type
	 *
	 */
	public final int getOtherEventsAdded()
	{
		return m_eventsAdded;
	}

	/**
	 * Method <code>getOtherEventsConsumed</code>
	 *
	 *
	 * @return the value of <code>int</code> type
	 *
	 */
	public final int getOtherEventsConsumed()
	{
		return m_eventsConsumed;
	}

	/**
	 * Method <code>resetInternalCounters</code>
	 *
	 *
	 */
	public final synchronized void resetInternalCounters()
	{

		m_eventsAdded       = 0;
		m_eventsConsumed    = 0;

		m_startTime = System.currentTimeMillis();
	}

	/**
	 * Method <code>getDump</code>
	 *
	 *
	 * @return the value of <code>String</code> type
	 *
	 */
	public String getDump()
	{

		String dump = "";

		dump += "added = " + getOtherEventsAdded() + ", ";
		dump += "consumed = " + getOtherEventsConsumed() + ", ";

		return dump;
	}

	/**
	 * Method <code>shutdown</code>
	 *
	 *
	 */
	public void shutdown()
	{
		m_running = false;
	}

	/**
	 * Method <code>isRunning</code>
	 *
	 *
	 * @return the value of <code>boolean</code> type
	 *
	 */
	public boolean isRunning()
	{
		return m_running;
	}

	/**
	   * Adds a new event to the queue
	   */
	public synchronized void addEvent( BaseEvent evt ) throws EventQueueException
	{

		m_eventsAdded++;

		if ( m_usedSize >= m_totalSize )
		{
			if ( m_firstUsedCell == null )
			{
				m_firstUsedCell         = m_lastUsedCell = new EventCell();
				m_firstUsedCell.nextEl  = m_firstUsedCell;
				m_totalSize             = m_usedSize = 1;
				m_firstUsedCell.evtData = evt;

				notifyAll();

				return;
			}

			EventCell afterLast = m_lastUsedCell.nextEl;

			m_lastUsedCell.nextEl        = new EventCell();
			m_lastUsedCell.nextEl.nextEl = afterLast;

			m_totalSize++;
		}

		m_lastUsedCell = m_lastUsedCell.nextEl;

		if ( m_lastUsedCell.evtData != null )
		{
			EventQueueException ex = new EventQueueException(
				"Non-null evtData in lastUsedCell when adding new event!" );

			throw ( ex );
		}

		m_usedSize++;

		m_lastUsedCell.evtData = evt;

		notifyAll();
	}

	/**
	 * Gets a next event and sends it to receiver
	 */
	private BaseEvent consumeEvent() throws EventQueueException
	{

		// Consumed event cells are deleted then
		// queue threshold falls shorts of given value.
		final double queueThreshold = 0.9;    // 90% occupancy
		final int    queueMinLimit  = 100;

		BaseEvent evt            = null;

		synchronized ( this )
		{

			if ( m_usedSize == 0 )
			{
				return null;
			}

			evt = m_firstUsedCell.evtData;

			if ( evt == null )
			{

				// SHOULD NEVER HAPPEN !!!
				EventQueueException ex = new EventQueueException(
				"Non-zero usedSize but null evtData in firstUsedCell when consuming event!" );

				throw ( ex );
			}

			long m_newEventTime = System.currentTimeMillis();

			if ( ( m_prevEventTime - m_newEventTime ) > m_maxTimeDelta )
			{
				logger.info( "THE SYSTEM TIME IS JUMPED BACK : prev event time = "
						+ new Date( m_prevEventTime )
						+ ", current event time = "
						+ new Date( m_newEventTime ) );    //evt.getTimeStamp() ) );
			}

			m_prevEventTime       = m_newEventTime;
			m_firstUsedCell.evtData = null;
			m_firstUsedCell         = m_firstUsedCell.nextEl;

			// If event queue is longer than 100 cells and
			// too many cells are empty, then proceed with cleanup.
			if ( ( m_usedSize > queueMinLimit )
					&& ( ( 1.0 * m_usedSize / m_totalSize ) < queueThreshold ) )
			{

				EventCell t = m_lastUsedCell.nextEl;

				m_lastUsedCell.nextEl = t.nextEl;
				t.nextEl            = null;

				m_totalSize--;
			}

			m_usedSize--;
		}

		try
		{
			if ( evt.isValid() )
			{
				evt.dispatch();
			}

			evt.processed();
		}
		catch ( Exception ex )
		{
			logger.info( "Cannot dispatch the event: " + evt );
		}

		m_eventsConsumed++;

		return evt;
	}

	/**
	 * Processes all event in the queue
	 */
	public void run()
	{

		Thread.currentThread().setName( "EventQueue" );

		m_running = true;

		int evtCnt            = 0;
		int evtCntBeforeYield = 7;      //31;

		while ( m_running )
		{
			BaseEvent evt = null;

			try
			{
				evt = consumeEvent();
			}
			catch ( EventQueueException ex )
			{
				logger.error( "Ignored by event queue: ", ex );
			}

			if ( evt == null )
			{
				while ( m_usedSize == 0 )
				{
					synchronized ( this )
					{
						try
						{
							wait();
						}
						catch ( InterruptedException ex ) {}
					}
				}

				continue;
			}
			if ( ( ( ++evtCnt ) & evtCntBeforeYield ) == 0 )
			{
				yield();
			}
		}

		cleanup();
	}

	/**
	 * An queue element. Contains of an event and pointer to the next element in the queue
	 */
	private static class EventCell
	{

		/** Field <code>evtData</code> */
		private BaseEvent evtData = null;

		/** Field <code>nextEl</code> */
		private EventCell nextEl = null;
	}

	/**
	 * Stops the thread and clears the queue.
	 * Leaves only first empty element, other objects are subject
	 * to next garbage collection iteration.
	 */
	private void cleanup()
	{

		// do cleanup for ordinary (other) events
		EventCell p = m_firstUsedCell;

		while ( ( p != null ) && ( p.nextEl != null ) )
		{
			EventCell r = p.nextEl;

			p.nextEl  = null;
			p.evtData = null;
			p         = r;
		}

		m_lastUsedCell = m_firstUsedCell;

	}
}
