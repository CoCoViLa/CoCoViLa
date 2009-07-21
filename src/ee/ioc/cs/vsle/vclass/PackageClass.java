package ee.ioc.cs.vsle.vclass;

import java.util.*;
import java.io.Serializable;
import ee.ioc.cs.vsle.graphics.Shape;

public class PackageClass implements Serializable {
    private static final long serialVersionUID = 1L;
	private String name;
	private String icon;
	//fields declared in the xml
    private Map<String, ClassField> propFields = new LinkedHashMap<String, ClassField>();
    //fields declared in the specification of the corresponding java class
    private Map<String, ClassField> specFields = new LinkedHashMap<String, ClassField>();
	private ClassGraphics graphics;
	private ArrayList<Port> ports = new ArrayList<Port>();
	private String description;
	private boolean relation = false;
    private String painterName;
    private ClassPainter painterPrototype;
    private int sequence;
    private boolean isStatic; // should the class be static by default?

	public PackageClass(String name) {
		this.setName( name );
	}

	public PackageClass() {
		super();
	}

	@Override
	public String toString() {
		return getName();
	}

	public void addPort(Port port) {
		getPorts().add(port);
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
		GObj obj = isRelation() ? new RelObj() : new GObj();

		obj.setWidth(graphics.getBoundWidth());
		obj.setHeight(graphics.getBoundHeight());
		obj.setClassName(getName());
		obj.setStatic(isStatic);

		// deep clone fields list
        for (ClassField field : specFields.values() ) {
            ClassField newField = field.clone();
            if( propFields.containsValue( field ) ) {
                obj.addField( newField );
            }
            obj.addSpecField( newField );
        }
        
		obj.setShapes( new ArrayList<Shape>(graphics.getShapes().size()) );
		for (Shape shape : graphics.getShapes())
			obj.getShapes().add(shape.clone());

		ArrayList<Port> newPorts = new ArrayList<Port>(getPorts().size());
		for (Port port : getPorts()) {
			port = port.clone();
			port.setObject(obj);
			newPorts.add(port);

			if (port.getX() + port.getOpenGraphics().getBoundX() < obj.getPortOffsetX1()) {
				obj.setPortOffsetX1( port.getX() + port.getOpenGraphics().getBoundX() );
			}

			if (port.getY() + port.getOpenGraphics().getBoundY() < obj.getPortOffsetY1()) {
				obj.setPortOffsetY1( port.getY() + port.getOpenGraphics().getBoundY() );
			}

			if (port.getX() + port.getOpenGraphics().getBoundWidth() > obj.getWidth()
					+ obj.getPortOffsetX2()) {
				obj.setPortOffsetX2( Math.max((port.getX()
						+ port.getOpenGraphics().getBoundX() 
						+ port.getOpenGraphics().getBoundWidth()) - obj.getWidth(), 0) );
			}

			if (port.getY() + port.getOpenGraphics().getBoundHeight() > obj.getHeight()
					+ obj.getPortOffsetY2()) {
				obj.setPortOffsetY2( Math.max((port.getY()
						+ port.getOpenGraphics().getBoundY() 
						+ port.getOpenGraphics().getBoundHeight()) - obj.getHeight(), 0) );
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
    public ClassField getSpecField( String specName ) {
        return specFields.get( specName );
    }

    /**
     * @param specFields the specFields to set
     */
    public void setSpecFields( Collection<ClassField> specFields ) {
        for( ClassField field : specFields ) {
            addSpecField( field );
        }
    }
    
    public void addSpecField( ClassField field ) {
        this.specFields.put( field.getName(), field );
    }

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

    /**
     * @param name the name to set
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon( String icon ) {
        this.icon = icon;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @return the ports
     */
    public ArrayList<Port> getPorts() {
        return ports;
    }

    /**
     * @param relation the relation to set
     */
    public void setRelation( boolean relation ) {
        this.relation = relation;
    }

    /**
     * @return the relation
     */
    public boolean isRelation() {
        return relation;
    }

    /**
     * @param painterName the painterName to set
     */
    public void setPainterName( String painterName ) {
        this.painterName = painterName;
    }

    /**
     * @return the painterName
     */
    public String getPainterName() {
        return painterName;
    }
}
