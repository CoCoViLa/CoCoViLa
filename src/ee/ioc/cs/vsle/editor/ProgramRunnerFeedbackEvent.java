package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.event.*;

public class ProgramRunnerFeedbackEvent extends BaseEvent {

	private static Dispatcher s_dispatcher;

	private static Object s_lock = new Object();

	public static final int TEXT_SPECIFICATION = 0;
	public static final int TEXT_PROGRAM = 1;
	public static final int TEXT_RESULT = 2;
	public static final int DISPOSE = 3;
	
	private long m_id;
	private int m_type;
	private String m_text;
	
	static {
		init();
	}

	/**
	 * @param originator
	 */
	public ProgramRunnerFeedbackEvent( Object originator, long id, int type, String text ) {
		super(originator);
		
		m_id = id;
		m_type = type;
		m_text = text;
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

			listener.onProgramRunnerFeedbackEvent((ProgramRunnerFeedbackEvent) evt);
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

		public void onProgramRunnerFeedbackEvent(ProgramRunnerFeedbackEvent event);
	}

	public long getId() {
		return m_id;
	}

	public int getType() {
		return m_type;
	}

	public String getText() {
		return m_text;
	}

}
