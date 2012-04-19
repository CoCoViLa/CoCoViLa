package ee.ioc.cs.vsle.vclass;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
import ee.ioc.cs.vsle.util.db;

/**
 * The scheme description
 */
public class Scheme implements Serializable, ee.ioc.cs.vsle.api.Scheme, ISpecExtendable {

    private static final long serialVersionUID = 1L;
    private ObjectList objects;
	private ConnectionList connections;
	private ISchemeContainer canvas;
	private String extendedSpec;
	
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

	@Override
    public VPackage getPackage() {
		return canvas.getPackage();
	}

	public ObjectList getObjectList() {
		if (objects == null)
			objects = new ObjectList();

		return objects;
	}

    public GObj getObject(int index) {
        return objects != null ? objects.get(index) : null;
    }

    public ArrayList<GObj> getSelectedObjects() {
        return objects != null ? objects.getSelected() : new ArrayList<GObj>(0);
    }

    public List<SchemeObject> getObjects() {
        return new ArrayList<SchemeObject>(objects);
    }

	public void setObjects(ObjectList objects) {
		this.objects = objects;
	}

	public List<ee.ioc.cs.vsle.api.Connection> getConnections() {
	    return new ArrayList<ee.ioc.cs.vsle.api.Connection>(connections);
	}

	public ConnectionList getConnectionList() {
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
    @Override
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
    @Override
    public void repaint(int x, int y, int width, int height) {
    }

    /*
     * @see ee.ioc.cs.vsle.api.Scheme#getFieldValue(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Object getFieldValue(String objectName, String fieldName) {
        GObj obj = objects.getByName(objectName);
        if (obj == null)
            throw new NoSuchSchemeObjectException(objectName);

        ClassField field = obj.getField(fieldName);
        if (field == null)
            throw new NoSuchClassFieldException(fieldName);

        return field.getValue();
    }

    @Override
    public SchemeObject getObject(String objectName) {
        SchemeObject obj = objects.getByName(objectName);
        if (obj == null)
            throw new NoSuchSchemeObjectException(objectName);
        return obj;
    }
    
    @Override
    public Object queryTable( String tableId, Object[] args ) {
        return TableManager.getTable( getPackage(), tableId ).queryTable( args );
    }

    @Override
    public Object queryTable( String[] inputIds, String tableId, Object[] args ) {
        return TableManager.getTable( getPackage(), tableId ).queryTable( inputIds, args );
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
        
        return new ProgramRunner(canvas).computeModel( context.getName(), inputNames, outputNames, inputValues, true );
    }
    
    @Override
    public Object[] computeModel(
            String context, String[] inputNames,
            String[] outputNames, Object[] inputValues, boolean cacheCompiledModel ) {

        return new ProgramRunner(canvas).computeModel( context, inputNames, outputNames, inputValues, cacheCompiledModel );
    }

    @Override
    public void close() {
        if (SwingUtilities.isEventDispatchThread()) {
            // The following cast should be avoided, but currently
            // ISchemeContainer has no reference to (but actually is)
            // the canvas that should be closed.
            Editor.getInstance().closeSchemeTab((Canvas) canvas);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        close();
                    }
                });
            } catch (InterruptedException e) {
                db.p(e);
            } catch (InvocationTargetException e) {
                db.p(e);
            }
        }
    }

    @Override
    public ee.ioc.cs.vsle.api.Scheme load(InputStream inputStream) {
        final InputStream input = inputStream;
        final Canvas[] c = new Canvas[1];

        if (SwingUtilities.isEventDispatchThread()) {
            c[0] = Editor.getInstance().newSchemeTab(
                    getContainer().getPackage(), input);
        } else {
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        c[0] = Editor.getInstance().newSchemeTab(
                                getContainer().getPackage(), input);
                    }
                });
            } catch (InterruptedException e) {
                db.p(e);
            } catch (InvocationTargetException e) {
                db.p(e);
            }
        }
        return c[0] == null ? null : c[0].getScheme();
    }

    public ISchemeContainer getContainer() {
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

    public boolean saveToFile( File file ) {
        OutputStream output = null;
        try {
            // The stream has to be closed explicitly or the file will
            // remain open probably until the next garbage collection.
            output = new FileOutputStream( file );
            save(output);
            return true;
        } catch ( Exception e ) {
            e.printStackTrace();
        } finally {
            if ( output != null ) {
                try {
                    output.close();
                } catch ( IOException e ) {
                    db.p( e );
                }
                output = null;
            }
        }
        return false;
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

            String spec = getSpecText();
            if( spec != null ) {
                th.startElement( "", "", "extended_spec", null );
                th.startCDATA();
                char[] chs = spec.toCharArray();
                th.characters( chs, 0, chs.length );
                th.endCDATA();
                th.endElement( "", "", "extended_spec" );
            }
            
            th.endElement("", "", "scheme");
            th.endDocument();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSpecText() {
        return extendedSpec;
    }

    @Override
    public String getTitle() {
        return canvas.getPackage().getName();
    }

    @Override
    public void setSpecText(String spec) {
        extendedSpec = spec;
    }
    
    public void destroy() {
        
        if( objects != null ) {
            objects.clear();
            objects = null;
        }
        
        if( connections != null ) {
            connections.clear();
            connections = null;
        }
        
        canvas = null;
    }
}
