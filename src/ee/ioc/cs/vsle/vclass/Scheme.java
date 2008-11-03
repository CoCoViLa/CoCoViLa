package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;

import ee.ioc.cs.vsle.api.SchemeObject;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.table.*;

/**
 * The scheme description
 */
public class Scheme implements Serializable, ee.ioc.cs.vsle.api.Scheme {

    private static final long serialVersionUID = 1L;
    private ObjectList objects;
	private ConnectionList connections;
	private ISchemeContainer canvas;
	
	public Scheme( ISchemeContainer canvas, ObjectList objects,
			ConnectionList connections) {

	    this( canvas );
	    
		this.objects = objects;
		this.connections = connections;
	}

	public Scheme( ISchemeContainer canvas ) {
	    this.canvas = canvas;
	}

	@Override
	public String toString() {
		return "Objects: " + objects + " Connections: " + connections;
	}

	public ee.ioc.cs.vsle.api.Package getPackage() {
		return canvas.getPackage();
	}

	public ObjectList getObjects() {
		if (objects == null)
			objects = new ObjectList();

		return objects;
	}

	public void setObjects(ObjectList objects) {
		this.objects = objects;
	}

	public ConnectionList getConnections() {
		if (connections == null)
			connections = new ConnectionList();

		return connections;
	}

	public void setConnections(ConnectionList connections) {
		this.connections = connections;
	}

	/**
	 * Returns the super class of the scheme if there is one.
	 * @return the super class, null if there is none
	 */
	public GObj getSuperClass() {
		for (GObj obj : objects)
			if (obj.isSuperClass())
				return obj;

		return null;
	}

    /**
     * Repaint the canvas.  This call gives the ClassPainters a chance to
     * update the visuals when, for example, the values of the fields have
     * changed.
     */
    public void repaint() {
        if (canvas == Editor.getInstance().getCurrentCanvas()) {
            canvas.repaint(); // TODO should repaint only the drawingarea
        }
    }

    /*
     * TODO do we need this method?
     * 
     * @see ee.ioc.cs.vsle.api.Scheme#repaint(int, int, int, int)
     */
    public void repaint(int x, int y, int width, int height) {
        Canvas canvas = Editor.getInstance().getCurrentCanvas();
        if (canvas != null)
            canvas.repaint(x, y, width, height); // TODO repaint drawingarea
    }

    /*
     * @see ee.ioc.cs.vsle.api.Scheme#getFieldValue(java.lang.String,
     *      java.lang.String)
     */
    public Object getFieldValue(String objectName, String fieldName) {
        GObj obj = objects.getByName(objectName);
        if (obj == null)
            throw new RuntimeException("No such object: " + objectName);

        ClassField field = obj.getField(fieldName);
        if (field == null)
            throw new RuntimeException("No such field: " + fieldName);

        return field.getValue();
    }

    /*
     * @see ee.ioc.cs.vsle.api.Scheme#getObject(java.lang.String)
     */
    public SchemeObject getObject(String objectName) {
        SchemeObject obj = objects.getByName(objectName);
        if (obj == null)
            throw new RuntimeException("No such object: " + objectName);
        return obj;
    }
    
    /*
     * @see ee.ioc.cs.vsle.api.Scheme#queryTable(java.lang.String, java.lang.Object[])
     */
    public Object queryTable( String tableName, Object[] args ) {
        return TableManager.getTable( getPackage(), tableName ).queryTable( args );
    }

    @Override
    public void rerun() {
        ProgramRunner.rerun( canvas );
    }

    @Override
    public void terminate( long runnerId ) {
        ProgramRunnerEvent event = new ProgramRunnerEvent( canvas, runnerId, ProgramRunnerEvent.DESTROY );
        EventSystem.queueEvent( event );
    }
}
