package ee.ioc.cs.vsle.synthesize;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ee.ioc.cs.vsle.vclass.ClassField;

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
	private ClassList superClasses = new ClassList();
	//relations declared in the specification of this class and in specifications of superclasses
	private Collection<ClassRelation> classRelations = new LinkedHashSet<ClassRelation>();
	//fields declared in this annotated class
	private Map<String, ClassField> classFields = new LinkedHashMap<String, ClassField>();
	//all fields declared here and in superclasses
	private Map<String, ClassField> allFields = new LinkedHashMap<String, ClassField>();
	
	/**
	 * Class constructor.
	 * @param name String
	 */ 
	public AnnotatedClass(String name) {
		this.name = name;
	} // ee.ioc.cs.editor.synthesize.AnnotatedClass

	/**
	 * Adds a new field to the ArrayList of fields.
	 * @param field ClassField - a field to be appended to the list of fields.
	 */ 
	 public void addField(ClassField field) {
		String name = field.getName();
     if (hasField(name)) {
       throw new SpecParseException( "Variable " + name + " declared more than once in class " + getName() );
     }
     classFields.put(name, field);
     allFields.put(name, field);
	} // addField

//	/**
//	 * Adds a list of variables to the ArrayList of fields.
//	 * @param v ArrayList - list of variables to be appended to the list of fields.
//	 */
//	void addFields(Collection<ClassField> v) {
//		classFields.addAll(v);
//		allFields.addAll(v);
//	} // addVars

	/**
	 * Adds a new class relation to the list of class relations.
	 * @param classRelation ClassRelation - a class relation to be added to the list of class relations.
	 */
	public void addClassRelation(ClassRelation classRelation) {
		classRelations.add(classRelation);
	} // addClassRelation

	@Override
	public boolean equals( Object o ) {
		if( o != null && o instanceof AnnotatedClass )
		{
			return name.equals( ((AnnotatedClass)o).name );
		}
		return false;
	}
	
	@Override
    public int hashCode() {
        return name.hashCode();
    }
	
	/**
	 * Converts the ee.ioc.cs.editor.synthesize.AnnotatedClass to a string, returning the name of the class.
	 * @return String - a name of the class.
	 */
	public String toString() {
		return name;
	} // toString

	public boolean hasField(String fieldName) {
		return allFields.containsKey(fieldName);
	}

	public ClassField getFieldByName(String fieldName) {
		return allFields.get(fieldName);
	}

	public String getName() {
		return name;
	}

	/*
	 * need to keep relations up-to-date (i.e. not cache rels from superclasses because some relations
	 * may be added into superclasses later
	 */
	public Collection<ClassRelation> getClassRelations() {
	    Collection<ClassRelation> relations = new LinkedHashSet<ClassRelation>();
	    for ( AnnotatedClass superclass : superClasses ) {
	        relations.addAll( superclass.getClassRelations() );
        }
	    //the order does matter! rels from superclasses have to go first
	    relations.addAll( classRelations );
		return relations;
	}

	public Collection<ClassField> getFields() {
		return allFields.values();
	}

	public Collection<ClassField> getClassFields() {
		return classFields.values();
	}
	
	public void addSuperClass( AnnotatedClass clas ) {
		Set<Entry<String, ClassField>> parentClassField = clas.allFields.entrySet();//TODO: refactor to not check every time field name
		for (Entry<String, ClassField> entry : parentClassField) {
			String name = entry.getKey();
			if(CodeGenerator.SPEC_OBJECT_NAME.equals(name))
				continue;
			//make a copy of the field as in case of alias, its state might be altered
			//and other classes with the same superclass might see the altered state, lets avoid this.
			allFields.put(name, entry.getValue().clone());
		}
		
		superClasses.add( clas );
	}

	public ClassList getSuperClasses() {
		return superClasses;
	}

	public ClassList getAllSuperClasses() {
	    ClassList allSuperClasses = new ClassList();
	    allSuperClasses.addAll( superClasses );
	    for ( AnnotatedClass superclass : superClasses ) {
	        allSuperClasses.addAll( superclass.getAllSuperClasses() );
        }
        return allSuperClasses;
    }
}
