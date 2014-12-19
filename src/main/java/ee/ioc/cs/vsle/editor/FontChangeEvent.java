package ee.ioc.cs.vsle.editor;

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
