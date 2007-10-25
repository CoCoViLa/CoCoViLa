package ee.ioc.cs.vsle.vclass;

import java.util.*;
import java.io.Serializable;
import ee.ioc.cs.vsle.graphics.Shape;

public class PackageClass implements Serializable {
    private static final long serialVersionUID = 1L;
	public String name;
	public String icon;
	//fields declared in the xml
    private Map<String, ClassField> propFields = new LinkedHashMap<String, ClassField>();
    //fields declared in the specification of the corresponding java class
    private Map<String, ClassField> specFields = new LinkedHashMap<String, ClassField>();
	public ClassGraphics graphics;
	public ArrayList<Port> ports = new ArrayList<Port>();
	public String description;
	public boolean relation = false;
    public String painterName;
    private ClassPainter painterPrototype;
    private int sequence;
    private boolean isStatic; // should the class be static by default?

	public PackageClass(String name) {
		this.name = name;
	}

	public PackageClass() {
		super();
	}

	@Override
	public String toString() {
		return name;
	}

	public void addPort(Port port) {
		ports.add(port);
	}

	public void addGraphics(ClassGraphics gr) {
		graphics = gr;
	}

	/**
	 * Sets the {@code ClassPainter} instance that is used to create
	 * new instances by cloning the first instacne.
	 * @param painter the painter prototype for this visual class
	 */
	public void setPainterPrototype(ClassPainter painter) {
		this.painterPrototype = painter;
	}
	
	/**
	 * Creates and returns a new {@code ClassPainter} instace.
	 * The returned instance is initialized with given arguments.
	 * @param scheme reference to the scheme
	 * @param obj the object the new painter is bound to
	 * @return new {@code ClassPainter} instance; {@code null} if there
	 * is no painter for this class
	 */
	public ClassPainter getPainterFor(Scheme scheme, GObj obj) {
		ClassPainter painter = null;
		if (painterPrototype != null) {
			painter = painterPrototype.clone();
			painter.setVClass(obj);
			painter.setScheme(scheme);
		}
		return painter;
	}

	/**
	 * Creates a new visual class instance from this package class.
	 * A new name must be assigned to each instance. 
	 * 
	 * @return a new visual instance of this package class
	 */
	public GObj getNewInstance() {
		GObj obj = relation ? new RelObj() : new GObj();

		obj.setWidth(graphics.getWidth());
		obj.setHeight(graphics.getHeight());
		obj.setClassName(name);
		obj.setStatic(isStatic);

		// deep clone fields list
        for (ClassField field : specFields.values() ) {
            ClassField newField = field.clone();
            if( propFields.containsValue( field ) ) {
                obj.addField( newField );
            }
            obj.addSpecField( newField );
        }
        
		obj.shapes = new ArrayList<Shape>(graphics.shapes.size());
		for (Shape shape : graphics.shapes)
			obj.shapes.add(shape.clone());

		ArrayList<Port> newPorts = new ArrayList<Port>(ports.size());
		for (Port port : ports) {
			port = port.clone();
			port.setObject(obj);
			newPorts.add(port);

			if (port.x + port.getOpenGraphics().boundX < obj.portOffsetX1) {
				obj.portOffsetX1 = port.x + port.getOpenGraphics().boundX;
			}

			if (port.y + port.getOpenGraphics().boundY < obj.portOffsetY1) {
				obj.portOffsetY1 = port.y + port.getOpenGraphics().boundY;
			}

			if (port.x + port.getOpenGraphics().boundWidth > obj.width
					+ obj.portOffsetX2) {
				obj.portOffsetX2 = Math.max((port.x
						+ port.getOpenGraphics().boundX 
						+ port.getOpenGraphics().boundWidth) - obj.width, 0);
			}

			if (port.y + port.getOpenGraphics().boundHeight > obj.height
					+ obj.portOffsetY2) {
				obj.portOffsetY2 = Math.max((port.y
						+ port.getOpenGraphics().boundY 
						+ port.getOpenGraphics().boundHeight) - obj.height, 0);
			}

			port.setConnections(new ArrayList<Connection>());
		}
		obj.setPorts(newPorts);

		return obj;
	}

	/**
	 * Returns the next serial number for this class.
	 * @return the next serial number
	 */
	public int getNextSerial() {
		return sequence++;
	}

	/**
	 * Checks whether there is a field in this class with the specified
	 * name and type.
	 * @param fieldName the name of the field
	 * @param fieldType the type of the field
	 * @return true, if there is a field with the specified name and exact type,
	 * false otherwise
	 */
	public boolean hasField(String fieldName, String fieldType) {
		if (getFields() == null)
			return false;
		
		for (ClassField f : getFields()) {
			if (f.getName().equals(fieldName) && f.getType().equals(fieldType))
				return true;
		}
		return false;
	}

	/**
     * @param propFields the fields to set
     */
    public void addField( ClassField field ) {
        this.propFields.put( field.getName(), field );
    }

    /**
     * @return the fields
     */
    public Collection<ClassField> getFields() {
        return propFields.values();
    }

    /**
	 * Sets the default static property value for new instances of this class.
	 * @param isStatic default static property value for new objects
	 */
	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

    /**
     * @return the specFields
     */
    public ClassField getSpecField( String name ) {
        return specFields.get( name );
    }

    /**
     * @param specFields the specFields to set
     */
    public void setSpecFields( Collection<ClassField> specFields ) {
        for( ClassField field : specFields ) {
            this.specFields.put( field.getName(), field );
        }
    }
}
