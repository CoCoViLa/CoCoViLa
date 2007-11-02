package ee.ioc.cs.vsle.editor;

import java.util.*;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 3.05.2004
 * Time: 17:53:32
 */
public class SpecGenerator implements ISpecGenerator {
	
	private SpecGenerator() {
		// use the factory to get instances
	}
	
	public String generateSpec(Scheme scheme, String className) {
    	ObjectList objects = scheme.getObjects();
    	ConnectionList relations = scheme.getConnections();

    	GObj superClass = scheme.getSuperClass();
    	
		StringBuffer s = new StringBuffer();
		
		// construct class and spec declarations
		s.append("public class " + className);
		if (superClass != null) {
			s.append(" extends ");
			s.append(superClass.getClassName());
		}
		s.append(" {");

		s.append("\n    /*@ specification  ");
		s.append(className);
		
		if (superClass != null) {
			s.append(" super ");
			s.append(superClass.getClassName());
		}
		s.append(" {\n");

		GObj obj;
		for (int i = 0; i < objects.size(); i++) {
			obj = objects.get(i);

			if (!obj.isSuperClass()) {
				s.append("    " + ( obj.isStatic() ? "static " : "" ) 
						+ obj.getClassName() + " " + obj.getName() + ";\n");
			}

			for ( ClassField field : obj.getFields() ) {
				if ( ( field.getValue() != null ) && !field.isGoal() ) {
					appendSpecFieldLHS(obj, field, s);

					if (field.getType().equals(TYPE_STRING)) {
						s.append("\"" + field.getValue() + "\";\n");
					} else if (field.isPrimitiveArray()) {
						s.append("{");
						String[] split = field.getValue().split( ClassField.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append(split[k]);
							} else
								s.append(", " + split[k]);
						}
						s.append("};\n");
						
					} else if (field.isPrimOrStringArray()) {
						s.append("{");
						String[] split = field.getValue().split( ClassField.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append("\"" + split[k] + "\"");
							} else
								s.append(", \"" + split[k] + "\"");
						}
						s.append("};\n");
					} else {
						s.append(field.getValue() + ";\n");
					}
				}
			}
		}
		
		Hashtable<String, String> multiRels = new Hashtable<String, String>();
		
		for (Connection rel : relations) {
			if (rel.endPort.getName().equals("any")) {
				s.append(
						CodeGenerator.OT_TAB + rel.endPort.getObject().getName() + "." + rel.beginPort.getName()
						+ " = " + rel.beginPort.getObject().getName() + "."
						+ rel.beginPort.getName() + ";\n");
				
			}  else if  (rel.beginPort.getName().equals("any")) {
				s.append(
						CodeGenerator.OT_TAB + rel.endPort.getObject().getName() + "." + rel.endPort.getName()
						+ " = " + rel.beginPort.getObject().getName() + "."
						+ rel.endPort.getName() + ";\n");

			} else if( rel.beginPort.isMulti() || rel.endPort.isMulti() ) {
				
				Port multi = rel.beginPort.isMulti() ? rel.beginPort : rel.endPort;
				Port simple = !rel.beginPort.isMulti() ? rel.beginPort : rel.endPort;
				
				String multiport = multi.getObject().getName() + "." + multi.getName();
				String port = simple.getObject().getName() + "." + simple.getName();
				String list = multiRels.get( multiport );
				
				if( list == null ) {
					list = port;
				} else {
					list += " " + port;
				}
				multiRels.put( multiport, list );
			} else {
				String startObjName = ( rel.beginPort.getObject().isSuperClass() ) 
										? rel.beginPort.getName()
										: rel.beginPort.getObject().getName() + "." + rel.beginPort.getName();
										
				String endObjName = ( rel.endPort.getObject().isSuperClass() )
										? rel.endPort.getName()
										: rel.endPort.getObject().getName() + "." + rel.endPort.getName();
				
				s.append( CodeGenerator.OT_TAB + endObjName + " = " + startObjName + ";\n" );
			}
			
		}
		
		for (String multiport : multiRels.keySet() ) {
			String portlist = multiRels.get( multiport );
			String[] ports = portlist.split( " " );
			String portarray = "";
			for (int i = 0; i < ports.length; i++) {
				String port = ports[i];
				if( i != ports.length - 1 ) {
					portarray += port + ", ";
				} else {
					portarray += port;
				}
			}
			s.append( CodeGenerator.OT_TAB + multiport + " = [ " + portarray + " ];\n");
		}
		
		s.append("    }@*/\n}\n");
		
		return s.toString();
	}

	/**
	 * Generates the left hand side of a specification assignment and
	 * appends it to the buffer.
	 * @param obj the owner of the field
	 * @param field the field
	 * @param buf accumulator
	 */
	private void appendSpecFieldLHS(GObj obj, ClassField field,
			StringBuffer buf) {
		buf.append("        ");

		if (!obj.isSuperClass()) {
			buf.append(obj.getName());
			buf.append(".");
		}

		buf.append(field.getName());
		buf.append(" = ");
	}

	public static void init() {
		FactoryStorage.register( new Factory() );
	}
	
	static class Factory implements IFactory {
		
		private static ISpecGenerator instance;
		
		public String getInterfaceInstance() {
			return SpecGenFactory.s_prefix + "\\SSP";
		}

		public ISpecGenerator getInstance() {
			if( instance == null ) {
				instance = new SpecGenerator();
			}
			return instance;
		}
		
		public String getDescription() {
			return "SSP specification";
		}		
	}

	public CustomFileFilter getFileFilter() {
		return new CustomFileFilter( CustomFileFilter.EXT.JAVA );
	}
	
}

