package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.ClassField;
 import java.util.HashSet;

/**
 * <p>Title: ee.ioc.cs.editor.synthesize.Var</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author  Ando Saabas
 * @version 1.0
 */
public class Var {

	HashSet rels = new HashSet();
	public ClassField field;
	public String type;
	public String object;
	public String name;

	/**
	 * <UNCOMMENTED>
	 * @param obj String
	 */void setObj(String obj) {
		object = obj;
	} // setObj

	/**
	 * <UNCOMMENTED>
	 * @param name String
	 */
	void setName(String name) {
		this.name = name;
	} // setName

	/**
	 * <UNCOMMENTED>
	 * @param type String
	 */
	void setType(String type) {
		this.type = type;
	} // setType

	/**
	 * <UNCOMMENTED>
	 * @param field ClassField
	 */
	void setField(ClassField field) {
		this.field = field;
	} // setField

	/**
	 * <UNCOMMENTED>
	 * @param rel Rel
	 */
	void addRel(Rel rel) {
		rels.add(rel);
	} // addRel

	/**
	 * <UNCOMMENTED>
	 * @return String
	 */
	String getName() {
		return name;
	} // getName

	/**
	 * <UNCOMMENTED>
	 * @return String
	 */ String getObj() {
		return object;
	} // getObj

	/**
	 * <UNCOMMENTED>
	 * @return String
	 */ public String toString() {
         if(object.equals("this"))
             return (name);
         else
             return (object + "." + name);
	} // toString

}
