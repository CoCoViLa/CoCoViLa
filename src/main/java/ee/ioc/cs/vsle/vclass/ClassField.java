package ee.ioc.cs.vsle.vclass;

import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_ANY;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_VOID;

import java.io.Serializable;
import java.util.*;

import javax.xml.transform.sax.TransformerHandler;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import ee.ioc.cs.vsle.util.*;

/**
 * <p>
 * Title: ee.ioc.cs.editor.vclass.ClassField
 * </p>
 * <p>
 * Description: <description>
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author Ando Saabas
 * @version 1.0
 */
public class ClassField implements Cloneable, Serializable {

	private static final long serialVersionUID = 1L;

	public final static int TYPE_VARIABLE = 1;
	public final static int TYPE_ALIAS = 2;
	public final static int TYPE_EXCEPTION = 3;
	public final static int TYPE_CONSTANT = 4;
	
	private String name;

	protected String type = "";

	//this is a real type assigned to a field with type "ANY"
	protected String anyTypeSubstitution = null;
	
	protected String value;

	protected String description;

	/**
	 * If a field is hidden it mean that it will not be shown in the Object Properties window
	 */
	protected boolean hidden = false;
	
	protected boolean specField = false;

	protected boolean isConstant = false;

	protected ArrayList<ClassField> vars;

	private ClassGraphics knownGraphics;

	private ClassGraphics defaultGraphics;

	protected boolean isInput = false;
	
	protected boolean isGoal = false;
	
	protected boolean isStatic = false;
	
	/**
	 * If a field is hidden it mean that it will not be shown in the Object Properties window
	 */

  private boolean schemeObject;
	
	/**
	 * Class constructor.
	 */
	public ClassField(String name) {
		this(name, "", null, false, false);
	} // ee.ioc.cs.editor.vclass.ClassField

	public ClassField(String name, String type) {
		this(name, type, null, false, false);
	}

	public ClassField(String name, String type, String value) {
		this(name, type, value, false, false);
	}

	public ClassField(String name, String type, String value, String nature, String description, boolean hidden, ClassGraphics knownGraphics,
			ClassGraphics defaultGraphics) {
		this.name = name;
		this.type = type;
		this.description = description;
		this.value = value;
		if(nature.equals("Input")){
			isInput = true;
		} else if (nature.equals("Goal")){
			isGoal = true;
		}
		this.hidden = hidden;
		this.knownGraphics = knownGraphics;
		this.defaultGraphics = defaultGraphics;
	}

	
	public ClassField(String name, String type, String value, boolean isConstant) {
		this(name, type, value, false, isConstant);
	}

    public ClassField(String name, String type, boolean isSpecField) {
    	this(name, type, null, isSpecField, false);
	} // ee.ioc.cs.editor.vclass.ClassField

	private ClassField(String name, String type, String value, boolean isSpecField, boolean isConstant) {
		setName( name );
		setType(type);
		setValue(value);
		this.isConstant = isConstant;
		this.specField = isSpecField;
	}
	
	/**
	 * Checks if we have an ee.ioc.cs.editor.vclass.Alias class or not.
	 * 
	 * @return boolean - indicator indicating whether the class is or is not an
	 *         alias.
	 */
	public boolean isAlias() {
		//return TypeUtil.TYPE_ALIAS.equals( type );
		return false;
	} // isAlias

	/**
	 * <UNCOMMENTED>
	 * 
	 * @return boolean -
	 */
	public boolean isArray() {
		return TypeUtil.isArray( getType() );
	} // isArray

	public String arrayType() {

		if (TypeUtil.isArray(getType())) {
			return TypeUtil.getArrayComponentType(getType());
		}
		return "notArray";
	}

	public boolean isPrimitiveArray() {
		return isPrimitive(arrayType());
	}

	public boolean isPrimOrStringArray() {
		return isPrimitiveOrString(arrayType());
	}

	public boolean isPrimitive(String s) {
		return TypeUtil.isPrimitive(s);
	}

	public boolean isPrimitive() {
		return TypeUtil.isPrimitive(getType());
	}

	public boolean isPrimitiveOrString(String s) {
		return TypeUtil.isPrimitiveOrString(s);
	}

	public boolean isPrimitiveOrString() {
		return TypeUtil.isPrimitiveOrString(type);
	}

	/**
	 * Performs cloning.
	 * 
	 * @return ClassField
	 */
	@Override
	public ClassField clone() {
		try {
			return (ClassField) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	} // clone

	/**
	 * Converts the class into a string and returns the name of it.
	 * NB! FOR DEBUG ONLY
	 * @return String - name of the class.
	 */
	@Override
	public String toString() {
		String _type = getType();
		return (_type != null && type.length() > 0 ? _type + " " : "") + getName();
	} // toString

	/**
	 * <UNCOMMENTED>
	 * 
	 * @return boolean -
	 */
	public boolean isSpecField() {
		return specField;
	} // isSpecField

	public boolean isKnown() {
		return value != null;
	}

	/**
	 * Generates SAX events that are necessary to serialize this object to XML.
	 * @param th the receiver of events
	 * @throws SAXException 
	 */
	public void toXML(TransformerHandler th) throws SAXException {
		AttributesImpl attrs = new AttributesImpl();
		
		attrs.addAttribute("", "", "name", StringUtil.CDATA, getName());
		attrs.addAttribute("", "", "type", StringUtil.CDATA, getType());

		if (isInput && !isGoal) {
			attrs.addAttribute("", "", "nature", StringUtil.CDATA,
					"input");
		} else if (!isInput && isGoal)
			attrs.addAttribute("", "", "nature", StringUtil.CDATA, "goal");

		if (value != null)
			attrs.addAttribute("", "", "value", StringUtil.CDATA, value);

		th.startElement("", "", "field", attrs);
		th.endElement("", "", "field");
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	/**
     * @param description the description to set
     */
    public void setDescription( String description ) {
        this.description = description;
    }

    public ArrayList<ClassField> getVars() {
		//alias
		if( !isAlias() ) {
			throw new IllegalStateException( "ClassField " + getName() + " is not alias, but " + type );
		}
		return vars;
	}
	
	public void setKnownGraphics(ClassGraphics gr) {
		knownGraphics = gr;
	}

	public ClassGraphics getKnownGraphics() {
		return knownGraphics;
	}

	public void setDefaultGraphics(ClassGraphics gr) {
		defaultGraphics = gr;
	}

	public ClassGraphics getDefaultGraphics() {
		return defaultGraphics;
	}

	public boolean isConstant() {
		return isConstant;
	}

	public void setName(String name) {
		if( name == null ) {
			throw new IllegalArgumentException( "Name cannot be null!" );
		}
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if( obj != null && obj instanceof ClassField ) {
			ClassField cf = (ClassField)obj;
			return getName().equals( cf.getName() ) && type.equals( cf.type );
		}
		return false;
	}

	@Override
	public int hashCode() {
		return ( getName() + type ).hashCode();
	}

	public boolean isAny() {
		return TYPE_ANY.equals( getType() );
	}
	
	public String getAnySpecificType() {
		if(!isAny())
			throw new IllegalStateException("Not an ANY type! " + this);
		
		if(!isAnyTypeBound())
			throw new IllegalStateException("ANY is not bound yet! " + this);
		
		return anyTypeSubstitution;
	}
	
	public boolean isAnyTypeBound() {
		return anyTypeSubstitution != null;
	}
	
	public void setAnySpecificType(String type) {
		if(!isAny())
			throw new IllegalStateException("Not an ANY type! " + this);
		
		if(type == null)
			throw new NullPointerException("Provided type cannot be null! " + this);
		
		if(anyTypeSubstitution != null && !anyTypeSubstitution.equals(type) )
			throw new IllegalArgumentException("Cannot assign type " + type + ", ANY is already bound with another type! " + this);
		
		anyTypeSubstitution = type;
	}
	
	public boolean isVoid() {
		return TYPE_VOID.equals( getType() );
	}

	public boolean isGoal() {
		return isGoal;
	}

	public void setGoal(boolean isGoal) {
		this.isGoal = isGoal;
	}

	public boolean isInput() {
		return isInput;
	}

	public void setInput(boolean isInput) {
		this.isInput = isInput;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

    /**
     * @return false
     */
    public boolean isAliasLength() {
        return false;
    }

    /**
     * @return the hidden
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * @param hidden the hidden to set
     */
    public void setHidden( boolean hidden ) {
        this.hidden = hidden;
    }

    public boolean isSchemeObject() {
      return schemeObject;
  }
    
    public void setSchemeObject( boolean schemeObject ) {
      this.schemeObject = schemeObject; 
    }
}
