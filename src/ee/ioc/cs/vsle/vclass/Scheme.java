package ee.ioc.cs.vsle.vclass;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.helpers.AttributesImpl;

import ee.ioc.cs.vsle.api.*;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.table.*;
import ee.ioc.cs.vsle.util.StringUtil;

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
    }

    /*
     * @see ee.ioc.cs.vsle.api.Scheme#getFieldValue(java.lang.String,
     *      java.lang.String)
     */
    public Object getFieldValue(String objectName, String fieldName) {
        GObj obj = objects.getByName(objectName);
        if (obj == null)
            throw new NoSuchSchemeObjectException(objectName);

        ClassField field = obj.getField(fieldName);
        if (field == null)
            throw new NoSuchClassFieldException(fieldName);

        return field.getValue();
    }

    /*
     * @see ee.ioc.cs.vsle.api.Scheme#getObject(java.lang.String)
     */
    public SchemeObject getObject(String objectName) {
        SchemeObject obj = objects.getByName(objectName);
        if (obj == null)
            throw new NoSuchSchemeObjectException(objectName);
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
    
    @Override
    public Object[] computeModel(
            Class<?> context, String[] inputNames,
            String[] outputNames, Object[] inputValues) {
        
        return new ProgramRunner(canvas).computeModel( context, inputNames, outputNames, inputValues );
    }
    
    @Override
    public Object[] computeModel(
            String context, String[] inputNames,
            String[] outputNames, Object[] inputValues) {

        return new ProgramRunner(canvas).computeModel( context, inputNames, outputNames, inputValues );
    }

    @Override
    public void close() {
        // The following cast should be avoided, but currently ISchemeContainer
        // has no reference to (but actually is) the canvas that should be closed.
        Editor.getInstance().closeSchemeTab((Canvas) canvas);
    }

    @Override
    public ee.ioc.cs.vsle.api.Scheme load(InputStream inputStream) {
        final InputStream input = inputStream;
        final Canvas[] c = new Canvas[1];
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    c[0] = Editor.getInstance().newSchemeTab(
                            getContainer().getPackage(), input);
                }
                
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return c[0] == null ? null : c[0].getScheme();
    }

    ISchemeContainer getContainer() {
        return canvas;
    }

    @Override
    public long run() {
        final ProgramRunner runner = new ProgramRunner(canvas);

        int op = (RuntimeProperties.isComputeGoal() ? ProgramRunnerEvent.COMPUTE_GOAL
                : ProgramRunnerEvent.COMPUTE_ALL)
                | ProgramRunnerEvent.RUN_NEW
                | (RuntimeProperties.isPropagateValues() ? ProgramRunnerEvent.PROPAGATE
                        : 0);

        ProgramRunnerEvent evt = new ProgramRunnerEvent(this, runner.getId(),
                op);

        EventSystem.queueEvent(evt);

        return runner.getId();
    }

    @Override
    public void save(OutputStream outputStream) {
        try {
            StreamResult result = new StreamResult(outputStream);
            SAXTransformerFactory tf = 
                (SAXTransformerFactory) TransformerFactory.newInstance();

            TransformerHandler th = tf.newTransformerHandler();
            Transformer serializer = th.getTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            serializer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    RuntimeProperties.SCHEME_DTD);
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            th.setResult(result);
            th.startDocument();

            AttributesImpl attrs = new AttributesImpl();
            attrs.addAttribute("", "", "package", StringUtil.CDATA,
                    getPackage().getName());

            GObj superClass = getSuperClass();
            if ( superClass != null ) {
                attrs.addAttribute("", "", "superclass", StringUtil.CDATA,
                        superClass.getName());
            }

            th.startElement("", "", "scheme", attrs);

            for (GObj obj : objects) {
                obj.toXML(th);
            }

            for (Connection con : connections) {
                con.toXML(th);
            }

            th.endElement("", "", "scheme");
            th.endDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
