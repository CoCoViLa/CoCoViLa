package ee.ioc.cs.vsle.vclass;

import java.io.Serializable;
import java.util.ArrayList;

import ee.ioc.cs.vsle.synthesize.Var;

/**
 * <p>Title: ee.ioc.cs.editor.vclass.ClassField</p>
 * <p>Description: <description></p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author Ando Saabas
 * @version 1.0
 */
public class ClassField implements Cloneable, Serializable {

	public final static String ARRAY_TOKEN = "%%";
	
	protected String name;
	protected String type;
	protected String value;
	protected String description;
        private Var m_parentVar;
	protected boolean specField = false;
	protected boolean alias = false;
	protected boolean watched = false;
	protected ArrayList<ClassField> vars;
	protected ClassGraphics knownGraphics;
	protected ClassGraphics defaultGraphics;

	/**
	 * Class constructor.
	 */
	public ClassField( String name ) {
            this.name = name;
	} // ee.ioc.cs.editor.vclass.ClassField

	/**
	 * Class constructor
	 * @param name String - name of the class.
	 * @param type String - type of the class.
	 */
	public ClassField(String name, String type) {
		this.name = name;
		this.type = type;
	}

	public ClassField(String name, String type, String value) {
		this.value = value;
		this.name = name;
		this.type = type;
	}

	/**
	 * Class constructor.
	 * @param name String
	 * @param type String
	 * @param b boolean
	 */
	public ClassField(String name, String type, boolean b) {
		this.name = name;
		this.type = type;
		specField = b;
	} // ee.ioc.cs.editor.vclass.ClassField

	public ClassField(String name, String type, String value, String desc) {
		this.name = name;
		this.type = type;
		this.value = value;
		description = desc;
	}

	/**
	 * Checks if we have an ee.ioc.cs.editor.vclass.Alias class or not.
	 * @return boolean - indicator indicating whether the class is or is not an alias.
	 */
	public boolean isAlias() {
		if (type.equals("alias")) {
			return true;
		}
		return false;
	} // isAlias

	/**
	 * <UNCOMMENTED>
	 * @return boolean -
	 */
	public boolean isArray() {
		int length = type.length();

		if (type.substring(length - 2, length).equals("[]")) {
			return true;
		}
		return false;
	} // isArray

	public String arrayType() {
		int length = type.length();

		if (type.substring(length - 2, length).equals("[]")) {
			return type.substring(0, length - 2);
		}
		return "notArray";
	}

	public boolean isPrimitiveArray() {
		if (isPrimitive(arrayType())) {
			return true;
		}
		return false;
	}

	public boolean isPrimOrStringArray() {
		if (isPrimitiveOrString(arrayType())) {
			return true;
		}
		return false;
	}

	public boolean isPrimitive(String s) {
		if (s.equals("int") || s.equals("double") || s.equals("float")
			|| s.equals("long") || s.equals("short") || s.equals("byte")
			|| s.equals("boolean") || s.equals("char")) {
			return true;
		}
		return false;
	}

	public boolean isPrimitive() {
		if (type.equals("int") || type.equals("double") || type.equals("float")
			|| type.equals("long") || type.equals("short") || type.equals("byte")
			|| type.equals("boolean") || type.equals("char")) {
			return true;
		}
		return false;
	}

	public boolean isPrimitiveOrString(String s) {
		return s.equals("String") || isPrimitive(s);
	}

	public boolean isPrimitiveOrString() {
		return type.equals("String") || isPrimitive();
	}

	/**
	 * Performs cloning.
	 * @return Object
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println("Unable to clone.");
			return null;
		}
	} // clone

	/**
	 * <UNCOMMENTED>
	 * @return boolean -
	 */
	public boolean isWatched() {
		return watched;
	} // isWatched

	/**
	 * Converts the class into a string and returns the name of it.
	 * @return String - name of the class.
	 */
	public String toString() {
		return name;
	} // toString

	/**
	 * <UNCOMMENTED>
	 * @return boolean -
	 */
	public boolean isSpecField() {
		return specField;
	} // isSpecField

	public boolean isKnown() {
		if (value == null) {
			return false;
		}
		return true;
	}

	public String toXML() {
		String xml = "<field name=\"" + name + "\" type=\"" + type + "\"";
		if (value != null)
			xml += " value=\"" + value + "\"";
		xml += "/>\n";
		return xml;
	}

        public void setParentVar( Var var ) {
            m_parentVar = var;
        }

        public Var getParentVar() {
            return m_parentVar;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public void setType( String type ) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue( String value ) {
            this.value = value;
        }

        public String getDescription() {
            return description;
        }

        public ArrayList<ClassField> getVars() {
            return vars;
        }

        public void setWatched( boolean value ) {
            this.watched = value;
        }

        public void setKnownGraphics( ClassGraphics gr ) {
            knownGraphics = gr;
        }

        public ClassGraphics getKnownGraphics() {
            return knownGraphics;
        }

        public void setDefaultGraphics( ClassGraphics gr ) {
            defaultGraphics = gr;
        }

        public ClassGraphics getDefaultGraphics() {
            return defaultGraphics;
        }

}
