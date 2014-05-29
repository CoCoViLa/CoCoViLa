package ee.ioc.cs.vsle.event;

public class Test {

	Lst lst = new Lst();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.err.println( Thread.currentThread() );
		
		Test tst = new Test();
		
		TestEvent.registerListener( tst.lst );
		
		TestEvent event = new TestEvent( tst );
		EventSystem.queueEvent( event );
		
	}

	private class Lst implements TestEvent.Listener {

		public void onTest(TestEvent event) {
			
			System.err.println( "!!!event: " + event + " " + Thread.currentThread() );
			
			TestEvent.unregisterListener( this );
		}
		
	}
	
}



class TestEvent extends BaseEvent {

	private static Dispatcher  s_dispatcher;
	private static Object      s_lock = new Object();
	
	static
	{
		init();
	}
	
	public TestEvent(Object originator) {
		super(originator);
	}

	@Override
	protected EventDispatcher getDispatcher() {
		return s_dispatcher;
	}
	
	private static void init()
	{

		if ( s_dispatcher == null )
		{
			synchronized ( s_lock )
			{
				if ( s_dispatcher == null )
				{
					s_dispatcher = new Dispatcher();

				}
			}
		}
	}

	/**
	 * Method <code>registerListener</code>
	 *
	 * @param listener is of <code>Listener</code> type
	 */
	public static void registerListener( Listener listener )
	{
		System.err.println( "registerListener: " + listener );
		s_dispatcher.register( listener );
	}

	/**
	 * Method <code>unregisterListener</code>
	 *
	 * @param listener is of <code>Listener</code> type
	 */
	public static void unregisterListener( Listener listener )
	{
		System.err.println( "registerListener: " + listener );
		s_dispatcher.unregister( listener );
	}

	private static class Dispatcher extends EventDispatcher
	{

		/**
		 * Method <code>callListenerOnEvent</code>
		 *
		 * @param obj is of <code>FTOeventListener</code> type
		 * @param evt is of <code>FTOevent</code> type
		 */
		public void callListenerOnEvent( BaseEventListener obj, BaseEvent evt )
		{

			Listener listener = (Listener) obj;

			listener.onTest( (TestEvent) evt );
		}
	}


	////////////////////////////////////////////////////////////////////////////
	// Interface Listener
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Interface <code>Listener</code>
	 *
	 */
	public interface Listener extends BaseEventListener
	{

		/**
		 * Method <code>onTicketEvent</code>
		 * @param event is of <code>FTOticketEvent</code> type
		 */
		public void onTest( TestEvent event );
	}
	
}