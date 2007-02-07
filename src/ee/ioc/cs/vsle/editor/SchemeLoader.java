package ee.ioc.cs.vsle.editor;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.graphics.Shape;

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
public class SchemeLoader {

	private SAXParser parser;
	private PackageHandler handler;
	private VPackage vpackage;

	/**
	 * Sets the package description
	 * @param vpackage package description
	 */
	public void setVPackage(VPackage vpackage) {
		this.vpackage = vpackage;
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
		if (vpackage == null)
			throw new IllegalStateException("Package must be set to a "
					+ "non-null value!");

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

		long startParsing = System.currentTimeMillis();
		
		try {
			parser.parse(file, handler);

			if (RuntimeProperties.isLogDebugEnabled()) 
				db.p("Scheme parsing completed in "
						+ (System.currentTimeMillis() - startParsing)
						+ "ms.\n" );
		} catch (Exception e) {
			handler.collectDiagnostic(e.getMessage());
			return false;
		}
		return true;
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
	public List<String> getDiagnostics() {
		if (handler == null)
			return null;
		
		List<String> list = handler.getDiagnostics();
		
		if (list == null || list.isEmpty())
			return null;
		
		return list;
	}

	/**
	 * This method should be consulted after loading a scheme to see if 
	 * there might have been gone anything wrong.
	 * Invoking load() method resets this value.
	 * @return true, if there were problems, false otherwise.
	 */
	public boolean hasProblems() {
		return getDiagnostics() != null;
	}
	
	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================
	static class PackageHandler extends DefaultHandler {
		private ObjectList objects;
		private ConnectionList connections;
		private VPackage vPackage;
		private String superClass;
		private List<String> messages;

		private Connection connection;
		private GObj obj;
		private PackageClass pclass;
		private boolean ignoreCurrent;

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
			collectDiagnostic(msg);

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
			messages = null;
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

			if (element.equals("object") || element.equals("relobject")) {
				String name = attrs.getValue("name");

				// catch duplicate names
				if (objects.getByName(name) != null) {
					collectDiagnostic("Duplicate class name: " + name
							+ ". Discarding second instance.");
					ignoreCurrent = true;
					return;
				}

				String type = attrs.getValue("type");
				pclass = vPackage.getClass(type);
				
				if (pclass == null) {
					collectDiagnostic("The type " + type + " not found in "
							+ "the package. Discarding class " + name + ".");
					ignoreCurrent = true;
					return;
				}

				obj = (element.equals("relobject")) ? new RelObj() : new GObj();
				obj.setName(name);
				obj.setClassName(type);
				objects.add(obj);
			} else if (element.equals("scheme")) {
				String type = attrs.getValue("package");

				superClass = attrs.getValue("superclass");
				
				if (!type.equals(vPackage.getName())) {
					throw new SAXException("Scheme was built with package \""
							+ type + "\", load this package first");
				}
			} else if (element.equals("properties")
					|| element.equals("relproperties")) {

				String x = attrs.getValue("x");
				String y = attrs.getValue("y");
				String xsize = attrs.getValue("xsize");
				String ysize = attrs.getValue("ysize");
				String width = attrs.getValue("width");
				String height = attrs.getValue("height");

				obj.setX(Integer.parseInt(x));
				obj.setY(Integer.parseInt(y));

				if (element.equals("relproperties")) {
					String endX = attrs.getValue("endX");
					String endY = attrs.getValue("endY");
					String angle = attrs.getValue("angle");

					RelObj relObj = (RelObj) obj;
					
					relObj.endX = Integer.parseInt(endX);
					relObj.endY = Integer.parseInt(endY);
					relObj.angle = Double.parseDouble(angle);
				}

				obj.setXsize(Float.parseFloat(xsize));
				obj.setYsize(Float.parseFloat(ysize));
				obj.setWidth(Integer.parseInt(width));
				obj.setHeight(Integer.parseInt(height));
			} else if (element.equals("field")) {
				String name = new String(attrs.getValue("name"));
				String type = new String(attrs.getValue("type"));
				String value = attrs.getValue("value");

				if (!pclass.hasField(name, type)) {
					collectDiagnostic("The class " + obj.getName()
							+ " has saved field " + name + "(" + type + ")"
							+ " = " + value
							+ " but there is no corresponding field in the"
							+ " package. Discarding field value.");
					return;
				}
				ClassField cf = new ClassField(name, type, value);
				obj.fields.add(cf);
			} else if (element.equals("connection")) {
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
			}

		}

		@Override
		public void endElement(String namespaceURI, String sName, String qName)
				throws SAXException {

			if (qName.equals("object") || qName.equals("relobject")) {
				
				if (ignoreCurrent) {
					ignoreCurrent = false;
					return;
				}

				PackageClass pClass = vPackage.getClass(obj.className);
				
				if (pClass != null) {
					// deep clone each separate field
					ClassField field;
					ClassField objField;
						
					for (int i = 0; i < pClass.fields.size(); i++) {
						field = pClass.fields.get(i);
						
						// match object by name because the order of
						// the ports might have changed
						objField = obj.getField(field.getName());

						if (objField == null) {
							collectDiagnostic("Missing field created: "
									+ obj.getName() + "." + field.getName());

							objField = new ClassField(field.getName(),
									field.getType(), field.getValue());

							obj.fields.add(objField);
						}
							
						objField.setKnownGraphics(field.getKnownGraphics());
						objField.setDefaultGraphics(field.getDefaultGraphics());
					}

					ArrayList<Port> ports = new ArrayList<Port>(pClass.ports);
					obj.shapes = new ArrayList<Shape>(pClass.graphics.shapes);

					Shape shape;
					for (int i = 0; i < obj.shapes.size(); i++) {
						shape = obj.shapes.get(i);
						obj.shapes.set(i, shape.clone());
					}

					Port port;
					for (int i = 0; i < ports.size(); i++) {
						port = ports.get(i);
						ports.set(i, port.clone());
						port = ports.get(i);
						port.setObject(obj);

						if (port.x + port.getOpenGraphics().boundX < obj.portOffsetX1) {
							obj.portOffsetX1 = port.x 
									+ port.getOpenGraphics().boundX;
						}

						if (port.y + port.getOpenGraphics().boundY < obj.portOffsetY1) {
							obj.portOffsetY1 = port.y
									+ port.getOpenGraphics().boundY;
						}

						if (port.x + port.getOpenGraphics().boundWidth > obj.width
								+ obj.portOffsetX2) {

							obj.portOffsetX2 = Math.max((port.x
									+ port.getOpenGraphics().boundX + port.getOpenGraphics().boundWidth)
									- obj.width, 0);
						}

						if (port.y + port.getOpenGraphics().boundHeight > obj.height
								+ obj.portOffsetY2) {
							
							obj.portOffsetY2 = Math.max((port.y
									+ port.getOpenGraphics().boundY + port.getOpenGraphics().boundHeight)
									- obj.height, 0);
						}

						port.setConnections(new ArrayList<Connection>(port.getConnections()));
					}
					obj.setPorts(ports);

					if (superClass != null 
							&& superClass.equals(obj.getName())) {
						obj.setSuperClass(true);
					}
				} else {
					throw new SAXException("There is no class \"" + obj.getClassName()
							+ "\" in the package \"" + vPackage.getName() + "\"");
				}
			} else if ("connection".equals(qName)) {
				connection = null;
			} else if (qName.equals("scheme")) {
				// create proper references to start and endports in all
				// RelObjects
				// Üsna valus häkk
				for (int i = 0; i < objects.size(); i++) {
					obj = objects.get(i);
					if (obj instanceof RelObj) {
						Port port = obj.getPorts().get(0);
						Connection con = port.getConnections().get(0);
						((RelObj) obj).startPort = con.beginPort;
						// ((RelObj)obj).startPort.obj = con.beginPort.obj;
						port = obj.getPorts().get(1);
						con = port.getConnections().get(0);
						((RelObj) obj).endPort = con.endPort;

					}
				}
			}
		}

		@Override
		public void characters(char buf[], int offset, int len) {
			// ingored
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

		void collectDiagnostic(String msg) {
			if (RuntimeProperties.isLogDebugEnabled())
				db.p(msg);

			if (messages == null)
				messages = new ArrayList<String>();
			messages.add(msg);
		}

		public List<String> getDiagnostics() {
			return messages;
		}
	}
}
