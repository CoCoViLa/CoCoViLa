package xxx;

import javax.swing.JOptionPane;

import ee.ioc.cs.vsle.vclass.ISchemeEventListener;
import ee.ioc.cs.vsle.vclass.SchemeEvent;

/**
 * @author andrex
 */
public class TestEventListener implements ISchemeEventListener {

    public void schemeLoaded(SchemeEvent evt) {
        System.err.println("Scheme loaded:" + evt);
    }

    public void schemeClosed(SchemeEvent evt) {
        System.err.println("Scheme closed:" + evt);
    }

    public void objectCreated(SchemeEvent evt) {
        System.err.println("Object created: " + evt.getObject());
        JOptionPane.showMessageDialog(evt.getWindow(), "The number of objects: " + evt.getScheme().objects.size() + ".", "New object created", 
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void objectDeleted(SchemeEvent evt) {
        System.err.println("Object deleted:" + evt);
    }

    public void objectClicked(SchemeEvent evt) {
        System.err.println("Object poked:" + evt);
        //JOptionPane.showMessageDialog(evt.getWindow(), "The object " + evt.getObject() + " was clicked.", "Object clicked",                JOptionPane.INFORMATION_MESSAGE);
        
        //evt.getObject().setHeight(evt.getObject().getHeight() * 2);
        evt.getObject().resize(20, 20, 3);
    }
}
