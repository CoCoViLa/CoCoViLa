package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import ee.ioc.cs.vsle.util.db;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 3.05.2004
 * Time: 17:53:32
 * To change this template use Options | File Templates.
 */
public class SpecGenerator {
	public String generateSpec(ObjectList objects, ArrayList relations, String packname) {
		GObj obj;
		ClassField field;
		String methName = RuntimeProperties.packageDir + packname + ".meth";
		String specName = RuntimeProperties.packageDir + packname + ".spec";
		String method = "";
		String spec  ="";
		try {
			BufferedReader in = new BufferedReader(new FileReader(methName));
			String lineString = new String();

			while ( (lineString = in.readLine()) != null) {
				method += lineString+"\n";
			}
			in.close();

			in = new BufferedReader(new FileReader(specName));

			while ( (lineString = in.readLine()) != null) {
				spec += lineString+"\n";
			}
			in.close();

		} catch (IOException ioe)     {
			//ioe.printStackTrace();
                        db.p("Method file " + methName + " does not exist");
		}




		StringBuffer s = new StringBuffer();
		s.append("public class GeneratedClass {");
		s.append("\n    /*@ specification  GeneratedClass {\n");
		s.append(spec);
		for (int i = 0; i < objects.size(); i++) {
			obj = (GObj) objects.get(i);
			s.append(
				"    " + obj.getClassName() + " " + obj.getName() + ";\n");
			for (int j = 0; j < obj.fields.size(); j++) {
				field = (ClassField) obj.fields.get(j);
				if (field.value != null) {
					if (field.type.equals("String")) {
						s.append("        " + obj.getName() + "." + field.name
							+ " = \"" + field.value + "\";\n");
					} else if (field.isPrimitiveArray()) {
						s.append(
							"        " + obj.getName() + "." + field.name
							+ " = {");
						String[] split = field.value.split("%%");
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append(split[k]);
							} else
								s.append(", " + split[k]);
						}
						s.append("};\n");

					} else if (field.isPrimOrStringArray()) {
						s.append(
							"        " + obj.getName() + "." + field.name
							+ " = {");
						String[] split = field.value.split("%%");
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append("\"" + split[k] + "\"");
							} else
								s.append(", \"" + split[k] + "\"");
						}
						s.append("};\n");
					} else {
						s.append(
							"        " + obj.getName() + "." + field.name + " = "
							+ field.value + ";\n");
					}
				}
			}
		}
			Connection rel;

		for (int i = 0; i < relations.size(); i++) {
			rel = (Connection) relations.get(i);



			if (rel.endPort.getName().equals("any")) {
				s.append(
					"    " + rel.endPort.obj.getName() + "." + rel.beginPort.getName()
					+ " = " + rel.beginPort.obj.getName() + "."
					+ rel.beginPort.getName() + ";\n");
			}  else if  (rel.beginPort.getName().equals("any")) {
				s.append(
					"    " + rel.endPort.obj.getName() + "." + rel.endPort.getName()
					+ " = " + rel.beginPort.obj.getName() + "."
					+ rel.endPort.getName() + ";\n");
			} else   {
				s.append(
					"    " + rel.endPort.obj.getName() + "." + rel.endPort.getName()
					+ " = " + rel.beginPort.obj.getName() + "."
					+ rel.beginPort.getName() + ";\n");
			}

		}
		s.append("    }@*/\n");
		s.append("\t"+method);
		s.append("\n}");

		return s.toString();
	}
}

