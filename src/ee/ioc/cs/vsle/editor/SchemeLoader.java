package ee.ioc.cs.vsle.editor;

import java.io.*;

import javax.swing.*;
import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * Loads stored schemes from .syn files. Instances of this scheme loader
 * should be safely reusable as long as it is not used in parallel.
 *
 * This loader tries to solve minor mismatches between the package
 * description and saved scheme. For example, new fields are quietly
 * accepted. On the other hand, warnings are generated in case of missing
 * fields of classes. The user should be notified about any warnings generated.
 * 
 * @see ee.ioc.cs.vsle.editor.RuntimeProperties#SCHEME_DTD
 */
public class SchemeLoader implements DiagnosticsCollector.Diagnosable {

	private SAXParser parser;
	private PackageHandler handler;
	private VPackage vpackage;
	private DiagnosticsCollector collector = new DiagnosticsCollector();
	private boolean schemeLoaded;
	private boolean schemeLoadingCancelled;
	private String schemePath;
	
	/**
	 * Sets the package description
	 * @param vpackage package description
	 */
	public SchemeLoader(VPackage vpackage) {
		this.vpackage = vpackage;
	}

    /**
     * Indicates whether the scheme was successfully loaded.
     * The method returns the value returned by the load() method.
     * @return true, when load() returned true, false otherwise
     */
    public boolean isSchemeLoaded() {
        return schemeLoaded;
    }

    /**
     * Indicates if loading of a scheme was canceled by a user
     * @return true, if loading was canceled; false otherwise
     */
    public boolean isSchemeLoadingCancelled() {
        return schemeLoadingCancelled;
    }
    
    /**
	 * Reads in the scheme description from a .syn file.
	 * The setPackage() method must be called with a non-null argument
	 * before attempting to load any schemes. The results can be asked
	 * with get*() methods after this method returns.
	 * 
	 * @param file scheme file
	 * @return true, if there were no fatal errors, false otherwise
	 */
	public boolean load(File file) {
	    if (!checkInitLoad()) {
	        return false;
	    }

	    // Give a meaningful error message in case of empty files because
	    // it has been possible to generate empty .syn files from Scheme
	    // Editor. Named pipes etc also have zero length, ignore these.
	    if (file.isFile() && file.length() == 0L) {
	        collector.collectDiagnostic("The file " + file.getName() +
	                " is empty!", true);

	        return false;
	    }

	    schemePath = file.getAbsolutePath();
	    
	    InputStream input = null;
	    try {
	        input = new FileInputStream(file);
	        schemeLoaded = parse(input);
	    } catch (FileNotFoundException e) {
	        collector.collectDiagnostic(e.getMessage());
	        return false;
	    } finally {
	        // The stream must be explicitly closed, otherwise it is not
	        // possible to delete the file on Windows.
	        if (input != null) {
	            try {
	                input.close();
	            } catch (IOException e) {
	                db.p(e);
	            }
	            input = null;
	        }
	    }
	    return schemeLoaded;
	}

    public boolean load(InputStream input) {
        schemeLoaded = checkInitLoad() && parse(input);
        return schemeLoaded;
    }

    /**
     * Preparations for loading a scheme.
     * This method makes sure that a package is set and the parser is
     * available.
     * @return true on success, false otherwise
     */
    boolean checkInitLoad() {
        if (vpackage == null) {
            throw new IllegalStateException("Package must be set to a "
                    + "non-null value!");
        }

        if (parser == null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();

            // Use the validating parser
            factory.setValidating(true);

            try {
                parser = factory.newSAXParser();
            } catch (Exception e) {
                db.p(e);
                return false;
            }
            handler = new PackageHandler();
        }

        handler.setVPackage(vpackage);

        return true;
    }

    /**
     * Parses the scheme XML.
     * The method checkInitLoad() must be called at least once before
     * calling this method.
     * @param input the stream that produces the scheme XML to parse
     * @return true on success, false otherwise
     */
    boolean parse(InputStream input) {
        long startParsing = System.currentTimeMillis();

        try {
            parser.parse(input, handler);

            if (RuntimeProperties.isLogDebugEnabled()) {
                    db.p("Scheme parsing completed in "
                            + (System.currentTimeMillis() - startParsing)
                            + "ms.\n" );
            }
        } catch (SchemeLoaderException e) {
            collector.collectDiagnostic(e.getMessage());
            schemeLoadingCancelled = !e.isShowErrorMessage();
            return false;
        } catch (Exception e) {
            collector.collectDiagnostic(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Collects messages
     * 
     * @param msg
     */
    void collectDiagnostic(String msg) {
        collector.collectDiagnostic(msg);
    }

    /**
     * Collects messages.
     * Some messages may be fatal.
     * 
     * @param msg
     * @param isFatal
     */
    public void collectDiagnostic(String msg, boolean isFatal) {
        collector.collectDiagnostic(msg, isFatal);
    }

	public ObjectList getObjectList() {
		if (handler != null)
			return handler.getObjects();

		return null;
	}

	public ConnectionList getConnectionList() {
		if (handler != null)
			return handler.getConnections();

		return null;
	}

	/**
	 * Returns the list of diagnostic messages generated.
	 * @return diagnostic messages
	 */
	public DiagnosticsCollector getDiagnostics() {
	    return collector;
	}
	
	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================
	class PackageHandler extends DefaultHandler {

	    private static final String CONNECTION = "connection";
        private static final String WATCH = "watch";
        private static final String GOAL = "goal";
        private static final String INPUT = "input";
        private static final String NATURE = "nature";
        private static final String VALUE = "value";
        private static final String NAME = "name";
        private static final String FIELD = "field";
        private static final String RELPROPERTIES = "relproperties";
        private static final String PROPERTIES = "properties";
        private static final String SUPERCLASS = "superclass";
        private static final String PACKAGE = "package";
        private static final String SCHEME = "scheme";
        private static final String TYPE = "type";
        private static final String RELOBJECT = "relobject";
        private static final String OBJECT = "object";
        private static final String EXT_SPEC = "extended_spec";
        
        private ObjectList objects;
		private ConnectionList connections;
		private VPackage vPackage;
		private String superClass;

		private Connection connection;
		private GObj obj;
		private PackageClass pclass;
		private boolean ignoreCurrent;
		private boolean readingExtSpec;
		private String schemeExtSpec;
		
		@Override
		public InputSource resolveEntity(String publicId, String systemId) {
			InputSource is = null;
			// order the DTD to be specified externally.
			if (systemId != null && systemId.endsWith("dtd")) {
				is = new InputSource(FileFuncs.getResource(
						RuntimeProperties.SCHEME_DTD, false).toString());
			}
			return is;
		}

		@Override
		public void error(SAXParseException spe) throws SAXParseException {
			String msg = "Parsing error, line " + spe.getLineNumber()
				+ ", uri " + spe.getSystemId();
			db.p(msg);
			collectDiagnostic(msg + "\n" + spe.getMessage());

			// Use the contained exception, if any
			Exception x = spe;

			if (spe.getException() != null)
				x = spe.getException();

			db.p(x);

			throw spe; // One error is enough, abort.
		}

		@Override
		public void setDocumentLocator(Locator l) {
			// ignored
		}

		@Override
		public void startDocument() {
			connections = new ConnectionList();
			objects = new ObjectList();

			// the parser may be reused
			superClass = null;
			ignoreCurrent = false;
			pclass = null;
			obj = null;
		}

		@Override
		public void endDocument() {
			if (superClass != null 
					&& objects.getByName(superClass) == null) {
			    collectDiagnostic("Superclass " + superClass 
						+ " not found.");
				superClass = null;
			}
		}

		@Override
		public void startElement(String namespaceURI, String lName,
				String qName, Attributes attrs) throws SAXException {

			// skip to the end of current broken entry
			if (ignoreCurrent)
				return;

			String element = qName;

			if (element.equals(OBJECT) || element.equals(RELOBJECT)) {
				String name = attrs.getValue(NAME);

				// catch duplicate names
				if (objects.getByName(name) != null) {
				    collectDiagnostic("Duplicate class name: " + name
							+ ". Discarding second instance.");
					ignoreCurrent = true;
					return;
				}

				String type = attrs.getValue(TYPE);
				pclass = vPackage.getClass(type);
				
				if (pclass == null) {
				    collectDiagnostic("The type " + type + " not found in "
							+ "the package. Discarding class " + name + ".");
					ignoreCurrent = true;
					return;
				}

				obj = pclass.getNewInstance();
				
				obj.setName(name);
				obj.setClassName(type);
				obj.setStatic( Boolean.parseBoolean( attrs.getValue("static") ) );
				objects.add(obj);
			} else if (element.equals(SCHEME)) {
				String type = attrs.getValue(PACKAGE);

				superClass = attrs.getValue(SUPERCLASS);
				
                if ( !type.equals( vPackage.getName() ) ) {
                    
                    String msg = "Scheme was built with the package \""
                            + type + "\", not \"" + vPackage.getName() + "\"."
                            + "\nContinue loading scheme using current package?"
                            + "\nSaving it with override previous package with the current one.";
                    
                    int res = JOptionPane.showConfirmDialog( null, msg,
                            "Package mismatch", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE );

                    if( res != JOptionPane.YES_OPTION) {
                        
                        throw new SchemeLoaderException(
                                "Scheme was built with package \"" + type
                                + "\", not \"" + vPackage.getName(), false );
                    }
                }
            } else if (element.equals(PROPERTIES)) {
                String x = attrs.getValue("x");
                String y = attrs.getValue("y");
                String xsize = attrs.getValue("xsize");
                String ysize = attrs.getValue("ysize");
                String angle = attrs.getValue("angle");
                // width and height are deprecated and should be ignored here

                obj.setX(Integer.parseInt(x));
                obj.setY(Integer.parseInt(y));
                obj.setXsize(Float.parseFloat(xsize));
                obj.setYsize(Float.parseFloat(ysize));

                if (angle != null) {
                    obj.setAngle(Double.valueOf(angle).doubleValue());
                }
            } else if (element.equals(RELPROPERTIES)) {
                // Values of properties x, y, endX, endY, angle, width, height,
                // xsize, ysize are calculated from connection ports.
                // The property "strict" is not used at all for relclasses?
                // All these attributes should be ignored here.
            } else if (element.equals(FIELD)) {
				String name = new String(attrs.getValue(NAME));
				String type = new String(attrs.getValue(TYPE));
				String value = attrs.getValue(VALUE);

				if (!pclass.hasField(name, type)) {
				    collectDiagnostic("The class " + obj.getName()
							+ " has saved field " + name + "(" + type + ")"
							+ " = " + value
							+ " but there is no corresponding field in the"
							+ " package. Discarding field value.");
					return;
				}
				ClassField cf = obj.getField( name );
				cf.setValue( value );
				
				String nature = attrs.getValue(NATURE);
				if (INPUT.equals(nature))
					cf.setInput(true);
				else if (GOAL.equals(nature))
					cf.setGoal(true);

				cf.setWatched(Boolean.parseBoolean(attrs.getValue(WATCH)));

			} else if (element.equals(CONNECTION)) {
				String obj1 = new String(attrs.getValue("obj1"));
				String port1 = new String(attrs.getValue("port1"));
				String obj2 = new String(attrs.getValue("obj2"));
				String port2 = new String(attrs.getValue("port2"));
				Port beginPort = objects.getPort(obj1, port1);
				Port endPort = objects.getPort(obj2, port2);

				if (beginPort == null || endPort == null) {
				    collectDiagnostic("Discarding connection "
							+ obj1 + "." + port1 + " = " 
							+ obj2 + "." + port2
							+ " because of missing object(s) or port(s): "
							+ (beginPort == null ? (obj1 + "." + port1) : "")
							+ (beginPort == null && endPort == null ? ", " : "")
							+ (endPort == null ? obj2 + "." + port2 : "")
							+ ".");
					return;
				}

				connection = new Connection(beginPort, endPort);
				connections.add(connection);
			} else if (element.equals("point")) {
				// quietly ignore breakpoints if the connection is not valid
				if (connection == null)
					return;

				String x = new String(attrs.getValue("x"));
				String y = new String(attrs.getValue("y"));
				connection.addBreakPoint(new Point(Integer.parseInt(x),
						Integer.parseInt(y)));
			} else if ( EXT_SPEC.equals( qName ) ) {
			    readingExtSpec = true;
			}

		}

		@Override
		public void endElement( String namespaceURI, String sName, String qName ) {

            if ( qName.equals( OBJECT ) || qName.equals( RELOBJECT ) ) {

                if ( ignoreCurrent ) {
                    ignoreCurrent = false;
                    return;
                }

                if ( superClass != null && superClass.equals( obj.getName() ) ) {
                    obj.setSuperClass( true );
                }
                obj = null;
            } else if ( CONNECTION.equals( qName ) ) {
                connection = null;
            } else if ( EXT_SPEC.equals( qName ) ) {
                readingExtSpec = false;
            } else if ( qName.equals( SCHEME ) ) {
                // create proper references to start and endports in all
                // RelObjects
                // Ysna valus h2kk
                for ( int i = 0; i < objects.size(); i++ ) {
                    obj = objects.get( i );
                    if ( obj instanceof RelObj ) {
                        Port port = obj.getPorts().get( 0 );
                        Connection con = port.getConnections().get( 0 );
                        ( (RelObj) obj ).setStartPort( con.getBeginPort() );
                        // ((RelObj)obj).startPort.obj = con.beginPort.obj;
                        port = obj.getPorts().get( 1 );
                        con = port.getConnections().get( 0 );
                        ( (RelObj) obj ).setEndPort( con.getEndPort() );

                    }
                }
                objects.updateRelObjs();
            }
        }

		@Override
        public void characters( char buf[], int offset, int len ) {
            if ( readingExtSpec ) {
                String spec = new String( buf, offset, len );
                if ( obj != null ) {
                    obj.setSpecText( spec );
                } else {
                    schemeExtSpec = spec;
                }
            }
        }

		@Override
		public void ignorableWhitespace(char buf[], int offset, int len) {
			// Purposely ignore it.
		}

		@Override
		public void processingInstruction(String target, String data) {
			// Purposely ignore it.
		}

		public void setVPackage(VPackage vp) {
			vPackage = vp;
		}

		public ConnectionList getConnections() {
			return connections;
		}

		public ObjectList getObjects() {
			return objects;
		}

		public String getSchemeExtSpec() {
		    return schemeExtSpec;
		}
	}
	
	private class SchemeLoaderException extends RuntimeException {

	    private static final long serialVersionUID = 1L;
	    private boolean showErrorMessage;

	    public SchemeLoaderException(String message, boolean showErrorMessage) {
	        super(message);
	        this.showErrorMessage = showErrorMessage;
	    }

        /**
         * @return the showErrorMessage
         */
        public boolean isShowErrorMessage() {
            return showErrorMessage;
        }
	}

    /**
     * @return the schemePath
     */
    public String getSchemePath() {
        return schemePath;
    }

    public Scheme getScheme(ISchemeContainer canvas) {
        Scheme scheme = new Scheme(canvas, getObjectList(), getConnectionList());
        if ( handler != null )
            scheme.setSpecText(handler.getSchemeExtSpec());
        return scheme;
    }
}
