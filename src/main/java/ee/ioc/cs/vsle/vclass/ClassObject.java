package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import ee.ioc.cs.vsle.graphics.BoundingBox;
import ee.ioc.cs.vsle.graphics.ShapeGroup;
import ee.ioc.cs.vsle.iconeditor.ClassFieldsTableModel;
import ee.ioc.cs.vsle.classeditor.ClassFieldTable;
import ee.ioc.cs.vsle.classeditor.IconPort;
import ee.ioc.cs.vsle.vclass.PackageClass.ComponentType;

public class ClassObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7267146419156048873L;
	
	
	// Class properties.
		public String className;
		public String classDescription;
		public String classIcon;
		public BoundingBox boundingbox;
		public ComponentType componentType;	
		
		public ArrayList<ClassField> fields;// = new ArrayList<ClassField>();  

		private ClassFieldTable dbrClassFields = new ClassFieldTable();
		
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

		public String getClassDescription() {
			return classDescription;
		}

		public void setClassDescription(String classDescription) {
			this.classDescription = classDescription;
		}

		public String getClassIcon() {
			return classIcon;
		}

		public void setClassIcon(String classIcon) {
			this.classIcon = classIcon;
		}

		public ComponentType getComponentType() {
			return componentType;
		}

		public void setComponentType(ComponentType componentType) {
			this.componentType = componentType;
		}

		public void setClassName(String className) {
			this.className = className;
		}

		public ClassObject(String className,String classDescription,String classIcon,ComponentType componentType){
			this.className = className;
			this.classDescription = classDescription;
			this.classIcon = classIcon;
			this.componentType = componentType;
		}
		
		
		public ClassObject() {
			this.shapeList = new ShapeGroup();
			this.ports = new ArrayList<IconPort>();
			this.fields = new ArrayList<ClassField>();
		}


		public ClassFieldTable getDbrClassFields() {
			return dbrClassFields;
		}

		public void setDbrClassFields(ClassFieldTable dbrClassFields) {
			this.dbrClassFields = dbrClassFields;
		}

		public void setClassFields( Collection<ClassField> cFields){
		 		 

	      this.dbrClassFields = new ClassFieldTable();		
	      	  
		  this.fields = new ArrayList<ClassField>(cFields);
		  for ( int i = 0; i < fields.size(); i++ ) {
			 Object[] row = { ( fields.get( i ) ).getName(), ( fields.get( i ) ).getType(), ( fields.get( i ) ).getValue(),
					 ( fields.get( i ) ).isGoal?"Goal": ( fields.get( i ) ).isInput?"Input":"Normal" , ( fields.get( i ) ).getDescription() , fields.get(i).isHidden(), 
							 ( fields.get( i ) ).getKnownGraphics() != null?true:false, ( fields.get( i ) ).getDefaultGraphics()!= null?true:false};
			 
			  dbrClassFields.addRow( row );
			  if(( fields.get( i ) ).getKnownGraphics() != null){
				  dbrClassFields.updateGraphic(false, ( fields.get( i ) ).getKnownGraphics(), i);
			  }
			  if(( fields.get( i ) ).getDefaultGraphics() != null){
				  dbrClassFields.updateGraphic(true, ( fields.get( i ) ).getDefaultGraphics(), i);
			  }
		  }		  
		   
		}
		

		public ClassFieldTable removeClassFieldsGraphics(){
		 		 
		 if(this.fields != null){	
				     
			 for ( int i = 0; i < fields.size(); i++ ) {					
				  dbrClassFields.removeGraphic(false, i);
				  dbrClassFields.removeGraphic(true, i);			
			 }		  
		 }		 
		  dbrClassFields.setRowCount( 0 );
		  return dbrClassFields;		  
		}
		
		
		public boolean validateBasicProperties(){
			boolean valid = false;
			if(className == null && classDescription != null && componentType != null){
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
		
		public ClassFieldTable getClassFieldModel() {
			  return dbrClassFields;
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
