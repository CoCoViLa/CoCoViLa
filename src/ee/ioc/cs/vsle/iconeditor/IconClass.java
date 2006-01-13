package ee.ioc.cs.vsle.iconeditor;

import java.util.ArrayList;

import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.ShapeGroup;

/*
 * Info about class (shapes, ports, descriptions, properties)
 */
public class IconClass {
	ShapeGroup shapeList;
	ArrayList ports;
	ArrayList fields;
	String name;
	String description;
	String iconName;
	Boolean isRelation;
	BoundingBox boundingbox;
	int maxWidth = 0, maxHeight = 0;
	
	
	IconClass(){
		this.shapeList = new ShapeGroup(new ArrayList());
		this.ports = new ArrayList();
		this.fields = new ArrayList();
		this.name = null;
		this.description = null;
		this.iconName = null;
		this.isRelation = false;
			
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
	 * @return Returns the isRelation.
	 */
	public Boolean getIsRelation() {
		return isRelation;
	}

	/**
	 * @param isRelation The isRelation to set.
	 */
	public void setIsRelation(Boolean isRelation) {
		this.isRelation = isRelation;
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
	public ArrayList getPorts() {
		return ports;
	}

	/**
	 * @param ports The ports to set.
	 */
	public void setPorts(ArrayList ports) {
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
			((IconPort)ports.get(i)).shift(offsetX, offsetY);
		}
	}

	/**
	 * @return Returns the fields.
	 */
	public ArrayList getFields() {
		return fields;
	}

	/**
	 * @param fields The fields to set.
	 */
	public void setFields(ArrayList fields) {
		this.fields = fields;
	}
}
