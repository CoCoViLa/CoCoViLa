package ee.ioc.cs.vsle.editor;

import java.io.*;
import java.util.*;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 3.05.2004
 * Time: 17:53:32
 * To change this template use Options | File Templates.
 */
public class SpecGenerator implements ISpecGenerator {
	
	private SpecGenerator() {}
	
	public String generateSpec(ObjectList objects, ArrayList relations, VPackage pack) {
		GObj obj;
		ClassField field;
		File methF = new File( RuntimeProperties.packageDir + pack.name + ".meth" );
		File specF = new File( RuntimeProperties.packageDir + pack.name + ".spec" );
		String method = "";
		String spec  = "";
		try {
			BufferedReader in;
			String lineString;
			
			if( methF.exists() ) {
				in = new BufferedReader(new FileReader(methF));
				lineString = new String();
				
				while ( (lineString = in.readLine()) != null) {
					method += lineString+"\n";
				}
				in.close();
			}
			
			if( specF.exists() ) {
				in = new BufferedReader(new FileReader(specF));
				
				while ( (lineString = in.readLine()) != null) {
					spec += lineString+"\n";
				}
				in.close();
			}
		} catch (IOException ioe)     {
			db.p( ioe.getMessage() );
		}
		
		StringBuffer s = new StringBuffer();
		s.append("public class " + pack.getPackageClassName() + " {");
		s.append("\n    /*@ specification  " + pack.getPackageClassName() + " {\n");
		
		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			s.append(
					"    " + obj.getClassName() + " " + obj.getName() + ";\n");
			for (int j = 0; j < obj.fields.size(); j++) {
				field = (ClassField) obj.fields.get(j);
				if (field.getValue() != null) {
					if (field.getType().equals("String")) {
						s.append("        " + obj.getName() + "." + field.getName()
								+ " = \"" + field.getValue() + "\";\n");
					} else if (field.isPrimitiveArray()) {
						s.append(
								"        " + obj.getName() + "." + field.getName()
								+ " = {");
						String[] split = field.getValue().split( ClassField.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append(split[k]);
							} else
								s.append(", " + split[k]);
						}
						s.append("};\n");
						
					} else if (field.isPrimOrStringArray()) {
						s.append(
								"        " + obj.getName() + "." + field.getName()
								+ " = {");
						String[] split = field.getValue().split( ClassField.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append("\"" + split[k] + "\"");
							} else
								s.append(", \"" + split[k] + "\"");
						}
						s.append("};\n");
					} else {
						s.append(
								"        " + obj.getName() + "." + field.getName() + " = "
								+ field.getValue() + ";\n");
					}
				}
			}
		}
		Connection rel;
		
		for (int i = 0; i < relations.size(); i++) {
			rel = (Connection) relations.get(i);
			
			
			
			if (rel.endPort.getName().equals("any")) {
				s.append(
						"    " + rel.endPort.getObject().getName() + "." + rel.beginPort.getName()
						+ " = " + rel.beginPort.getObject().getName() + "."
						+ rel.beginPort.getName() + ";\n");
			}  else if  (rel.beginPort.getName().equals("any")) {
				s.append(
						"    " + rel.endPort.getObject().getName() + "." + rel.endPort.getName()
						+ " = " + rel.beginPort.getObject().getName() + "."
						+ rel.endPort.getName() + ";\n");
			} else   {
				s.append(
						"    " + rel.endPort.getObject().getName() + "." + rel.endPort.getName()
						+ " = " + rel.beginPort.getObject().getName() + "."
						+ rel.beginPort.getName() + ";\n");
			}
			
		}
		s.append(spec);
		s.append("    }@*/\n");
		s.append("\t"+method);
		s.append("\n}");
		
		return s.toString();
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
	
}

