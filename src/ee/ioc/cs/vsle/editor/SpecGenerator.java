package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 3.05.2004
 * Time: 17:53:32
 * To change this template use Options | File Templates.
 */
public class SpecGenerator {
	public String generateSpec(ObjectList objects, ArrayList relations) {
		GObj obj;
		ClassField field;
		StringBuffer s = new StringBuffer();
		s.append("public class GeneratedClass {");
		s.append("\n    /*@ specification  GeneratedClass {\n");
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
						String[] split = field.value.split("�");
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
						String[] split = field.value.split("�");
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
			if (rel.endPort.toString().equals("any")) {
				s.append(
					"    " + rel.endPort.obj.toString() + "." + rel.beginPort.toString()
					+ " = " + rel.beginPort.obj.toString() + "."
					+ rel.beginPort.toString() + ";\n");
			}  else if  (rel.beginPort.toString().equals("any")) {
				s.append(
					"    " + rel.endPort.obj.toString() + "." + rel.endPort.toString()
					+ " = " + rel.beginPort.obj.toString() + "."
					+ rel.endPort.toString() + ";\n");
			} else   {
				s.append(
					"    " + rel.endPort.obj.toString() + "." + rel.endPort.toString()
					+ " = " + rel.beginPort.obj.toString() + "."
					+ rel.beginPort.toString() + ";\n");
			}

		}
		s.append("    }@*/\n}");
		return s.toString();
	}
}
