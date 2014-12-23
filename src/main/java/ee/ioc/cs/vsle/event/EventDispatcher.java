package ee.ioc.cs.vsle.event;

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
