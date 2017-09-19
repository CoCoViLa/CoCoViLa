/**
 * 
 */
package ee.ioc.cs.vsle.table.event;

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

import javax.swing.event.*;

/**
 * @author pavelg
 *
 */
public class TableEvent extends java.util.EventObject
{
    public static final int HRULES = 1;
    public static final int VRULES = 2;
    public static final int DATA = 3;
    public static final int RENDERER = 4;
    
    private int typeMask;
    private Object value;
    
    public TableEvent( Object source, int mask ) {
        this( source, mask, null );
    }

    public TableEvent( Object source, int mask, Object val ) {
        super( source );
        
        typeMask = mask;
        value = val;
    }
    
    /**
     * @return the typeMask
     */
    public int getType() {
        return typeMask;
    }
    
    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    
    /////////////////////////////////////////////////////TABLE EVENT/////////////////////////////////////////////////////
    
    /** List of listeners */
    private static EventListenerList listenerList = new EventListenerList();
    
    /**
     * @param l
     */
    public static void addTableListener(TableEvent.Listener l) {
        listenerList.add(TableEvent.Listener.class, l);
    }

    /**
     * @param l
     */
    public static void removeTableListener(TableEvent.Listener l) {
        listenerList.remove(TableEvent.Listener.class, l);
    }
    
    /**
     * @param e
     */
    public static void dispatchEvent(TableEvent e) {
        Object[] listeners = listenerList.getListenerList();
        
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TableEvent.Listener.class) {
                ((TableEvent.Listener)listeners[i+1]).tableChanged(e);
            }
        }
    }
    
    /**
    *
    */
   public interface Listener extends java.util.EventListener
   {
       public void tableChanged(TableEvent e);
   }
}
