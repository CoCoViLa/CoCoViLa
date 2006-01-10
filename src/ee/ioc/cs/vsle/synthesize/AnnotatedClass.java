package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;

import java.util.ArrayList;

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

	private String name;
	private ArrayList<AnnotatedClass> superClasses = new ArrayList<AnnotatedClass>();
	private ArrayList<ClassRelation> classRelations = new ArrayList<ClassRelation>();
	private ArrayList<ClassField> fields = new ArrayList<ClassField>();
	private boolean isOnlyForSuperclassGeneration = false;
	/**
	 * Class constructor.
	 * @param s String
	 */ AnnotatedClass(String s) {
		name = s;
	} // ee.ioc.cs.editor.synthesize.AnnotatedClass

	 void addSuperClass( AnnotatedClass clas ) {
		 superClasses.add( clas );
	 }
	/**
	 * Adds a new field to the ArrayList of fields.
	 * @param field ClassField - a field to be appended to the list of fields.
	 */ void addField(ClassField field) {
		fields.add(field);
	} // addField

	/**
	 * Adds a list of variables to the ArrayList of fields.
	 * @param v ArrayList - list of variables to be appended to the list of fields.
	 */
	void addVars(ArrayList<ClassField> v) {
		fields.addAll(v);
	} // addVars

	/**
	 * Adds a new class relation to the list of class relations.
	 * @param classRelation ClassRelation - a class relation to be added to the list of class relations.
	 */
	void addClassRelation(ClassRelation classRelation) {
		classRelations.add(classRelation);
	} // addClassRelation

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
		ClassField f;
		for (int j = 0; j < fields.size(); j++){
            f = fields.get(j);
			if (f.getName().equals(fieldName))
				return f;
		}
		return null;
	}

	String getName() {
		return name;
	}

	ArrayList<ClassRelation> getClassRelations() {
		return classRelations;
	}

	ArrayList<ClassField> getFields() {
		return fields;
	}

	public ArrayList<AnnotatedClass> getSuperClasses() {
		return superClasses;
	}

	public boolean isOnlyForSuperclassGeneration() {
		return isOnlyForSuperclassGeneration;
	}

	public void setOnlyForSuperclassGeneration(boolean isOnlyForSuperclassGeneration) {
		this.isOnlyForSuperclassGeneration = isOnlyForSuperclassGeneration;
	}
}

