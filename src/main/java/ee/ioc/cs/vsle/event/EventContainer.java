package ee.ioc.cs.vsle.event;

import java.util.Vector;

import ee.ioc.cs.vsle.util.db;

public class EventContainer {
	
	private Vector<BaseEventListener> termListeners;
	
	EventContainer()
	{
		termListeners = new Vector<BaseEventListener>();
	}

	public void add( BaseEventListener lst )
	{
		termListeners.add( lst );
	}

	public void traverseAll( BaseEvent ftoevent, EventDispatcher dispatcher )
	{

		// during notifying lisntener can do unsubscribe
		// use copy of array in for statement
		Vector listenersCopy = (Vector) termListeners.clone();

		for ( int k = 0; k < listenersCopy.size(); k++ )
		{
			Object obj = listenersCopy.elementAt( k );

			dispatcher.tryToCallListenerOnEvent( (BaseEventListener) obj, ftoevent );
		}
	}

	public boolean delete( BaseEventListener listener )
	{
		return termListeners.remove( listener );
	}

	private boolean cleanup()
	{

		String  reason     = "no reason";
		boolean canCleanup = true;

		if ( ( termListeners != null ) && ( canCleanup ) )
		{
			canCleanup = termListeners.size() == 0;

			if ( !canCleanup )
			{
				reason = "termListeners.size() == " + termListeners.size();
			}
		}

		if ( canCleanup )
		{

			if ( termListeners != null )
			{
				termListeners.clear();
			}

		}
		else
		{
			db.p( "cleanup disabled due to " + reason );
		}

		return canCleanup;
	}

}
