/**
 * 
 */
package ee.ioc.cs.vsle.ccl;

import ee.ioc.cs.vsle.event.*;

/**
 * @author pavelg
 *
 */
public class CompileEvent extends BaseEvent {

	private static Dispatcher s_dispatcher;

	private static Object s_lock = new Object();

	private String m_fileName;
	
	static {
		init();
	}

	/**
	 * @param originator
	 */
	public CompileEvent( Object originator, String fileName ) {
		super(originator);
		
		m_fileName = fileName;
	}

	/* (non-Javadoc)
	 * @see ee.ioc.cs.vsle.event.BaseEvent#getDispatcher()
	 */
	@Override
	protected EventDispatcher getDispatcher() {
		return s_dispatcher;
	}

	private static void init() {

		if (s_dispatcher == null) {
			synchronized (s_lock) {
				if (s_dispatcher == null) {
					s_dispatcher = new Dispatcher();

				}
			}
		}
	}

	public static void registerListener(Listener listener) {
		s_dispatcher.register(listener);
	}

	public static void unregisterListener(Listener listener) {
		s_dispatcher.unregister(listener);
	}

	private static class Dispatcher extends EventDispatcher {

		public void callListenerOnEvent(BaseEventListener obj, BaseEvent evt) {

			Listener listener = (Listener) obj;

			listener.onCompileEvent((CompileEvent) evt);
		}
	}

	////////////////////////////////////////////////////////////////////////////
	// Interface Listener
	////////////////////////////////////////////////////////////////////////////

	/**
	 * Interface <code>Listener</code>
	 *
	 */
	public interface Listener extends BaseEventListener {

		public void onCompileEvent(CompileEvent event);
	}

	public String getFileName() {
		return m_fileName;
	}

	public void setFileName(String name) {
		m_fileName = name;
	}
}
