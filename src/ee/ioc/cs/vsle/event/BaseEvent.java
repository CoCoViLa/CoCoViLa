/**
 * 
 */
package ee.ioc.cs.vsle.event;

import java.util.EventObject;

/**
 * @author pavelg
 *
 */
abstract public class BaseEvent extends EventObject {

	private static final Object   s_originator = new DefaultOriginator();
	
	private boolean         m_processed      = false;
	private long            m_timeStamp      = 0L;
	
	/** Field <code>instancesMaxNumber</code> */
	private static int s_instancesMaxNumber = 0;

	/** Field <code>instancesCounter</code> */
	private static int s_instancesCounter = 0;
	
	public BaseEvent(Object originator) {
		
		super( ( originator == null )
				   ? s_originator
				   : originator );

			setTimeStamp( System.currentTimeMillis() );

			s_instancesCounter++;
			s_instancesMaxNumber++;
	}

	public void setTimeStamp( long v )
	{
		m_timeStamp = v;
	}
	
	public long getTimeStamp()
	{
		return m_timeStamp;
	}
	
	public static int getInstancesCounter()
	{
		return s_instancesCounter;
	}

	public static int getInstancesMaxNumber()
	{
		return s_instancesMaxNumber;
	}
	
	protected void dispatch()
	{
		getDispatcher().traverseListeners( this );
	}

	/**
	 * This method can be called
	 * in the context of
	 * AWT event dispatching thread only
	 */
	public void dispatchAwtEvent()
	{
		dispatch();
	}
	
	abstract protected EventDispatcher getDispatcher();
	
	public void processed()
	{
		m_processed = true;
	}

	public boolean isProcessed()
	{
		return m_processed;
	}

	public boolean isValid()
	{
		return true;
	}

	public void finalize()
	{
		s_instancesCounter--;
	}
	
	private static class DefaultOriginator
	{
		public String toString()
		{
			return "DefaultOriginator";
		}
	}
}
