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

import java.awt.*;

import javax.swing.event.*;

public class FontChangeEvent extends java.util.EventObject
{
    public static final int HRULES = 1;
    public static final int VRULES = 2;
    public static final int DATA = 3;
    public static final int RENDERER = 4;
    
    private Font font;
    private RuntimeProperties.Fonts element;
    
    public FontChangeEvent( Object source, RuntimeProperties.Fonts element, Font font ) {
        super( source );
        
        this.font = font;
        this.element = element;
    }
    
    /**
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    /**
     * @return the element
     */
    public RuntimeProperties.Fonts getElement() {
        return element;
    }

    /** List of listeners */
    private static EventListenerList listenerList = new EventListenerList();
    
    /**
     * @param l
     */
    public static void addFontChangeListener(FontChangeEvent.Listener l) {
        listenerList.add(FontChangeEvent.Listener.class, l);
    }

    /**
     * @param l
     */
    public static void removeFontChangeListener(FontChangeEvent.Listener l) {
        listenerList.remove(FontChangeEvent.Listener.class, l);
    }
    
    /**
     * @param e
     */
    public static void dispatchEvent(FontChangeEvent e) {
        Object[] listeners = listenerList.getListenerList();
        
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==FontChangeEvent.Listener.class) {
                ((FontChangeEvent.Listener)listeners[i+1]).fontChanged(e);
            }
        }
    }
    
    /**
    *
    */
   public interface Listener extends java.util.EventListener
   {
       public void fontChanged(FontChangeEvent e);
   }
}
