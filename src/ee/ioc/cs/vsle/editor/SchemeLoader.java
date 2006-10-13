package ee.ioc.cs.vsle.editor;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.util.ArrayList;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.graphics.Shape;

/**
 * Loads stored schemes from .syn files.
 * 
 * @see ee.ioc.cs.vsle.editor.RuntimeProperties#SCHEME_DTD
 */
public class SchemeLoader {

	private static SAXParser parser;
	private static PackageHandler handler;

	private SchemeLoader() {
		// the SchemeLoader should be used statically
	}

	/**
	 * Creates scheme description from a .syn file.
	 * 
	 * Incorrect syntax in the input or inconcistencies between
	 * the stored scheme and the package description are consired
	 * as errors. In other words this method should return a
	 * valid scheme or no scheme at all.
	 * 
	 * This method is not thread safe.
	 * 
	 * @param file scheme file
	 * @param vp package description
	 * @return scheme description, or null when exceptions occur
	 */
	public static Scheme getScheme(File file, VPackage vp) {
		if (parser == null) {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			
			// Use the validating parser
			factory.setValidating(true);

			try {
				parser = factory.newSAXParser();
			} catch (Exception e) {
				db.p(e);
				return null;
			}
			
			handler = new PackageHandler();
		}
		
		long startParsing = System.currentTimeMillis();
		
		handler.setVPackage(vp);

		Scheme scheme = null;
		try {
			parser.parse(file, handler);

			if (RuntimeProperties.isLogDebugEnabled()) 
				db.p("Scheme parsing completed in "
						+ (System.currentTimeMillis() - startParsing)
						+ "ms.\n" );
			
			scheme = new Scheme(handler.getObjects(), handler.getConnections());
		} catch (Exception e) {
			db.p(e);
		}

		return scheme;
	}


	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================
	static class PackageHandler extends DefaultHandler {
		private ObjectList objects;
		private ConnectionList connections;
		private VPackage vPackage;

		private Connection connection;
		private GObj obj;

		@Override
		public InputSource resolveEntity(String publicId, String systemId) {
			InputSource is = null;
			// order the DTD to be specified externally.
			if (systemId != null && systemId.endsWith("dtd")) {
				is = new InputSource(FileFuncs.getResource(RuntimeProperties.SCHEME_DTD, false).toString());
			}
			return is;
		}

		@Override
		public void error(SAXParseException spe) {
			db.p("\n** Parsing error, line " + spe.getLineNumber() + ", uri "
					+ spe.getSystemId());

			// Use the contained exception, if any
			Exception x = spe;

			if (spe.getException() != null)
				x = spe.getException();

			db.p(x);
		}

		@Override
		public void setDocumentLocator(Locator l) {
			// ignored
		}

		@Override
		public void startDocument() {
			connections = new ConnectionList();
			objects = new ObjectList();
		}

		@Override
		public void endDocument() {
			// ignored
		}

		@Override
		public void startElement(String namespaceURI, String lName,
				String qName, Attributes attrs) throws SAXException {
			String element = qName;

			if (element.equals("object") || element.equals("relobject")) {
				String name = attrs.getValue("name");
				String type = attrs.getValue("type");
				obj = (element.equals("relobject")) ? new RelObj() : new GObj();
				obj.setName(name);
				obj.setClassName(type);
				objects.add(obj);
			} else if (element.equals("scheme")) {
				String type = attrs.getValue("package");
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
				String strict = attrs.getValue("strict");

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
				obj.setStrict(Boolean.valueOf(strict).booleanValue());
			} else if (element.equals("field")) {
				String name = new String(attrs.getValue("name"));
				String type = new String(attrs.getValue("type"));
				String value = attrs.getValue("value");

				ClassField cf = new ClassField(name, type, value);

				obj.fields.add(cf);
			} else if (element.equals("connection")) {
				String obj1 = new String(attrs.getValue("obj1"));
				String port1 = new String(attrs.getValue("port1"));
				String obj2 = new String(attrs.getValue("obj2"));
				String port2 = new String(attrs.getValue("port2"));
				Port beginPort = objects.getPort(obj1, port1);
				Port endPort = objects.getPort(obj2, port2);
				connection = new Connection(beginPort, endPort);
				beginPort.addConnection(connection);
				endPort.addConnection(connection);
				beginPort.setConnected(true);
				endPort.setConnected(true);
				connections.add(connection);
			} else if (element.equals("point")) {
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
				PackageClass pClass = vPackage.getClass(obj.className);
				
				if (pClass != null) {
					// deep clone each separate field
					ClassField field;
					ClassField objField;
					if (pClass.fields.size() != obj.fields.size())
						throw new SAXException("Mismatch between the number of "
								+ "fields in the package description and "
								+ "the scheme file for object " + obj.getName());
						
					for (int i = 0; i < pClass.fields.size(); i++) {
						field = pClass.fields.get(i);
						objField = obj.fields.get(i);
						objField.setKnownGraphics(field.getKnownGraphics());
						objField.setDefaultGraphics(field.getDefaultGraphics());
					}

					obj.ports = new ArrayList<Port>(pClass.ports);
					obj.shapes = new ArrayList<Shape>(pClass.graphics.shapes);

					Shape shape;
					for (int i = 0; i < obj.shapes.size(); i++) {
						shape = obj.shapes.get(i);
						obj.shapes.set(i, shape.clone());
					}

					Port port;
					for (int i = 0; i < obj.ports.size(); i++) {
						port = obj.ports.get(i);
						obj.ports.set(i, port.clone());
						port = obj.ports.get(i);
						port.setObject(obj);

						if (port.isStrict()) {
							obj.strict = true;
						}

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
				} else {
					throw new SAXException("There is no class \"" + obj.getClassName()
							+ "\" in the package \"" + vPackage.getName() + "\"");
				}
			} else if (qName.equals("scheme")) {
				// create proper references to start and endports in all
				// RelObjects
				// Üsna valus häkk
				for (int i = 0; i < objects.size(); i++) {
					obj = objects.get(i);
					if (obj instanceof RelObj) {
						Port port = obj.ports.get(0);
						Connection con = port.getConnections().get(0);
						((RelObj) obj).startPort = con.beginPort;
						// ((RelObj)obj).startPort.obj = con.beginPort.obj;
						port = obj.ports.get(1);
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
	}
}
