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

abstract public class EventDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(EventDispatcher.class);

	private EventContainer m_container;

	public abstract void callListenerOnEvent( BaseEventListener obj, BaseEvent evt );

	public void tryToCallListenerOnEvent( BaseEventListener listener, BaseEvent evt )
	{

		try
		{
			callListenerOnEvent( listener, evt );
		}
		catch ( Exception ex )
		{

			// We do the catch here, so we can recover and send the same
			// event to other listeners.
			logger.error( "Ignored by event queue:  " + ex.getMessage()
								  + "\n    event listener =  " + listener
								  + "\n    event = " + evt );
		}
	}

	public EventDispatcher()
	{
		m_container = new EventContainer();
	}

	public void register( BaseEventListener listener )
	{
//		db.p( "register listener: "
//				+ getClass().getName() + ","
//				+ listener.getClass().getName() );
		
		//assert( listener != null, "listener cannot be null" );
		m_container.add( listener );
	}

	public boolean unregister( BaseEventListener listener )
	{
//		db.p( "unregister listener: "
//				+ getClass().getName() + ","
//				+ listener.getClass().getName() );
			
		return m_container.delete( listener );
	}

	public void traverseListeners( BaseEvent event )
	{
		m_container.traverseAll( event, this );
	}
	
}
