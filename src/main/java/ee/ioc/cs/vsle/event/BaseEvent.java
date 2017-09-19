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
