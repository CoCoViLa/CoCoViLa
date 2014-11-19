package ee.ioc.cs.vsle.iconeditor;

import java.util.*;

import ee.ioc.cs.vsle.graphics.*;
import ee.ioc.cs.vsle.vclass.*;

/*
 * Info about class (shapes, ports, descriptions, properties)
 */
public class IconClass {
	ShapeGroup shapeList;
	ArrayList<IconPort> ports;
	ArrayList<ClassField> fields;
	String name;
	String description;
	private String iconName;
	BoundingBox boundingbox;
	int maxWidth = 0, maxHeight = 0;
	private PackageClass.ComponentType componentType = PackageClass.ComponentType.CLASS;
	
	IconClass(){
		this.shapeList = new ShapeGroup();
		this.ports = new ArrayList<IconPort>();
		this.fields = new ArrayList<ClassField>();
		this.name = null;
		this.description = null;
		this.iconName = null;
	}

	/**
	 * @return Returns the description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return Returns the iconName.
	 */
	public String getIconName() {
		return iconName;
	}

	/**
	 * @param iconName The iconName to set.
	 */
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the ports.
	 */
	public ArrayList<IconPort> getPorts() {
		return ports;
	}

	/**
	 * @param ports The ports to set.
	 */
	public void setPorts(ArrayList<IconPort> ports) {
		this.ports = ports;
	}

	/**
	 * @return Returns the shapeList.
	 */
	public ShapeGroup getShapeList() {
		return shapeList;
	}

	/**
	 * @param shapeList The shapeList to set.
	 */
	public void setShapeList(ShapeGroup shapeList) {
		this.shapeList = shapeList;
	}

	/**
	 * @return Returns the boundingbox.
	 */
	public BoundingBox getBoundingbox() {
		return boundingbox;
	}

	/**
	 * @param boundingbox The boundingbox to set.
	 */
	public void setBoundingbox(BoundingBox boundingbox) {
		this.boundingbox = boundingbox;
	}
	
	/**
	 * @return Returns the maxHeight.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}

	/**
	 * @param maxHeight The maxHeight to set.
	 */
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * @return Returns the maxWidth.
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * @param maxWidth The maxWidth to set.
	 */
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}
	
	/*
	 * @param width, height
	 */
	public void setMax(int w, int h) {
		if (w > maxWidth)
			maxWidth = w;
		if (h > maxHeight)
			maxHeight = h;
		
	}
	
	public void shiftPorts(int offsetX, int offsetY) {
		for (int i = 0; i < ports.size(); i++){
			ports.get(i).shift(offsetX, offsetY);
		}
	}

	/**
	 * @return Returns the fields.
	 */
	public ArrayList<ClassField> getFields() {
		return fields;
	}

	/**
	 * @param fields The fields to set.
	 */
	public void setFields(ArrayList<ClassField> fields) {
		this.fields = fields;
	}

    /**
     * @return the componentType
     */
    PackageClass.ComponentType getComponentType() {
        return componentType;
    }

    /**
     * @param componentType the componentType to set
     */
    void setComponentType( PackageClass.ComponentType componentType ) {
        this.componentType = componentType;
    }
}
