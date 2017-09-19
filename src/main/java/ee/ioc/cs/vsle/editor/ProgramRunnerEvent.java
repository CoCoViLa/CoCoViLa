/**
 * 
 */
package ee.ioc.cs.vsle.editor;

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

import ee.ioc.cs.vsle.event.*;

/**
 * @author pavelg
 * 
 */
public class ProgramRunnerEvent extends BaseEvent {

    private static Dispatcher s_dispatcher;

    private static Object s_lock = new Object();

    public static final int COMPUTE_GOAL = 1;
    public static final int COMPUTE_ALL = 1 << 1;
    public static final int COMPILE = 1 << 2;
    public static final int RUN = 1 << 3;
    public static final int PROPAGATE = 1 << 4;
    public static final int RUN_NEW = COMPILE | RUN;
    public static final int DESTROY = 1 << 5;
    public static final int REQUEST_SPEC = 1 << 6;
    public static final int SHOW_VALUES = 1 << 7;
    public static final int SHOW_ALL_VALUES = 1 << 8;

    private long m_id;
    private int m_operation;
    private String m_specText;
    private String m_programText;
    private String m_objectName;
    private boolean m_requestFeedback = false;
    private int m_repeat = 1;

    static {
        init();
    }

    /**
     * @param originator
     */
    public ProgramRunnerEvent( Object originator, long id, int operation ) {
        super( originator );

        m_id = id;
        m_operation = operation;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ee.ioc.cs.vsle.event.BaseEvent#getDispatcher()
     */
    @Override
    protected EventDispatcher getDispatcher() {
        return s_dispatcher;
    }

    private static void init() {

        if ( s_dispatcher == null ) {
            synchronized ( s_lock ) {
                if ( s_dispatcher == null ) {
                    s_dispatcher = new Dispatcher();

                }
            }
        }
    }

    public static void registerListener( Listener listener ) {
        s_dispatcher.register( listener );
    }

    public static void unregisterListener( Listener listener ) {
        s_dispatcher.unregister( listener );
    }

    private static class Dispatcher extends EventDispatcher {

        public void callListenerOnEvent( BaseEventListener obj, BaseEvent evt ) {

            Listener listener = (Listener) obj;

            listener.onProgramRunnerEvent( (ProgramRunnerEvent) evt );
        }
    }

    // //////////////////////////////////////////////////////////////////////////
    // Interface Listener
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Interface <code>Listener</code>
     * 
     */
    public interface Listener extends BaseEventListener {

        public void onProgramRunnerEvent( ProgramRunnerEvent event );
    }

    public long getId() {
        return m_id;
    }

    public int getOperation() {
        return m_operation;
    }

    public String getSpecText() {
        return m_specText;
    }

    public void setSpecText( String text ) {
        this.m_specText = text;
    }

    public boolean isRequestFeedback() {
        return m_requestFeedback;
    }

    public void setRequestFeedback( boolean requestFeedback ) {
        this.m_requestFeedback = requestFeedback;
    }

    public String getProgramText() {
        return m_programText;
    }

    public void setProgramText( String text ) {
        m_programText = text;
    }

    public int getRepeat() {
        return m_repeat;
    }

    public void setRepeat( int repeat ) {
        this.m_repeat = repeat;
    }

    public String getObjectName() {
        return m_objectName;
    }

    public void setObjectName( String name ) {
        m_objectName = name;
    }
}
