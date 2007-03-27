package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;

import java.util.*;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.AnnotatedClass</p>
 * <p>Description:  A representation of a specifcation of a class. Includes information
 * about fields and relations declared.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class AnnotatedClass {

	public static final String SPEC_OBJECT_NAME = "cocovilaSpecObjectName";
	
	private String name;
	private Collection<AnnotatedClass> superClasses = new LinkedHashSet<AnnotatedClass>();
	private Collection<ClassRelation> classRelations = new LinkedHashSet<ClassRelation>();
	private Collection<ClassField> fields = new LinkedHashSet<ClassField>();
	private boolean isOnlyForSuperclassGeneration = false;
	/**
	 * Class constructor.
	 * @param name String
	 */ 
	AnnotatedClass(String name) {
		this.name = name;
	} // ee.ioc.cs.editor.synthesize.AnnotatedClass

	 void addSuperClass( AnnotatedClass clas ) {
		 superClasses.add( clas );
	 }
	/**
	 * Adds a new field to the ArrayList of fields.
	 * @param field ClassField - a field to be appended to the list of fields.
	 */ 
	 void addField(ClassField field) {
		fields.add(field);
	} // addField

	/**
	 * Adds a list of variables to the ArrayList of fields.
	 * @param v ArrayList - list of variables to be appended to the list of fields.
	 */
	void addFields(Collection<ClassField> v) {
		fields.addAll(v);
	} // addVars

	/**
	 * Adds a new class relation to the list of class relations.
	 * @param classRelation ClassRelation - a class relation to be added to the list of class relations.
	 */
	void addClassRelation(ClassRelation classRelation) {
		classRelations.add(classRelation);
	} // addClassRelation

	public boolean equals( Object o ) {
		if( o != null && o instanceof AnnotatedClass )
		{
			return name.equals( ((AnnotatedClass)o).name );
		}
		return false;
	}
	/**
	 * Converts the ee.ioc.cs.editor.synthesize.AnnotatedClass to a string, returning the name of the class.
	 * @return String - a name of the class.
	 */
	public String toString() {
		return name;
	} // toString

	boolean hasField(String fieldName) {
		return getFieldByName(fieldName) != null;
	}

	public ClassField getFieldByName(String fieldName) {
		for ( ClassField f : fields ){
			if (f.getName().equals(fieldName))
				return f;
		}
		return null;
	}

	String getName() {
		return name;
	}

	Collection<ClassRelation> getClassRelations() {
		return classRelations;
	}

	Collection<ClassField> getFields() {
		return fields;
	}

	public Collection<AnnotatedClass> getSuperClasses() {
		return superClasses;
	}

	public boolean isOnlyForSuperclassGeneration() {
		return isOnlyForSuperclassGeneration;
	}

	public void setOnlyForSuperclassGeneration(boolean isOnlyForSuperclassGeneration) {
		this.isOnlyForSuperclassGeneration = isOnlyForSuperclassGeneration;
	}
}

