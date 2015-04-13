package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.ShapeGroup;
import ee.ioc.cs.vsle.iconeditor.ClassFieldsTableModel;
import ee.ioc.cs.vsle.classeditor.IconPort;
import ee.ioc.cs.vsle.vclass.PackageClass.ComponentType;

public class ClassObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7267146419156048873L;
	
	
	// Class properties.
		public static String className;
		public static String classDescription;
		public static String classIcon;
		public BoundingBox boundingbox;
		public static ComponentType componentType;	
		
		public ArrayList<ClassField> fields = new ArrayList<ClassField>();  
				
		public ShapeGroup shapeList;
		public ArrayList<IconPort> ports;

		int maxWidth = 0, maxHeight = 0;

		
		
		
    // Port Drawing Settings		

	    private boolean drawOpenPorts = true;	
	    
		public boolean isDrawOpenPorts() {
			return drawOpenPorts;
		}

		public void setDrawOpenPorts(boolean drawOpenPorts) {
			this.drawOpenPorts = drawOpenPorts;
		}

		public String getClassName() {
			return className;
		}

		public void setClassName(String className) {
			ClassObject.className = className;
		}

		public static String getClassDescription() {
			return classDescription;
		}

		public void setClassDescription(String classDescription) {
			ClassObject.classDescription = classDescription;
		}

		public static String getClassIcon() {
			return classIcon;
		}

		public void setClassIcon(String classIcon) {
			ClassObject.classIcon = classIcon;
		}

		public static ComponentType getComponentType() {
			return componentType;
		}

		public void setComponentType(ComponentType componentType) {
			ClassObject.componentType = componentType;
		}

		public ClassObject(String className,String classDescription,String classIcon,ComponentType componentType){
			ClassObject.className = className;
			ClassObject.classDescription = classDescription;
			ClassObject.classIcon = classIcon;
			ClassObject.componentType = componentType;
		}
		
		
		public ClassObject() {
			this.shapeList = new ShapeGroup();
			this.ports = new ArrayList<IconPort>();
			this.fields = new ArrayList<ClassField>();
		}

		public ClassFieldsTableModel setClassFields( Collection<ClassField> cFields){
		 		 

	      ClassFieldsTableModel dbrClassFields = new ClassFieldsTableModel();		
			
		  this.fields = new ArrayList<ClassField>(cFields);
		  for ( int i = 0; i < fields.size(); i++ ) {
			  String[] row = { ( fields.get( i ) ).getName(), ( fields.get( i ) ).getType(), ( fields.get( i ) ).getValue() };
			  dbrClassFields.addRow( row );
		  }		  
		  return dbrClassFields;		  
		}
		
		public boolean validateBasicProperties(){
			boolean valid = false;
			if(className != null && classDescription != null){
				valid = true;
			}
			return valid;
		}

		public int getMaxWidth() {
			return maxWidth;
		}

		public void setMaxWidth(int maxWidth) {
			this.maxWidth = maxWidth;
		}

		public int getMaxHeight() {
			return maxHeight;
		}

		public void setMaxHeight(int maxHeight) {
			this.maxHeight = maxHeight;
		}
		
		public void setMax(int w, int h) {
			if (w > maxWidth)
				maxWidth = w;
			if (h > maxHeight)
				maxHeight = h;			
		}
		
		/*@TODO Extra Validation
		 * 
		 *  if ( ClassEditor.className == null || ClassEditor.classDescription == null
				  || ClassEditor.getClassIcon() == null
				  || ClassEditor.componentType == null
				  || ( ClassEditor.className != null && ClassEditor.className.trim().length() == 0 )
				  || ( ClassEditor.classDescription != null && ClassEditor.classDescription.trim().length() == 0 )
				  || ( ClassEditor.getClassIcon() != null && ClassEditor.getClassIcon().trim().length() == 0 ) ) {
		 * 
		 * 
		 */

		
}
