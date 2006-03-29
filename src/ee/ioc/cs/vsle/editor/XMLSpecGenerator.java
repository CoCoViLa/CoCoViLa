/*
 *
 */
package ee.ioc.cs.vsle.editor;

import java.util.ArrayList;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.vclass.*;

public class XMLSpecGenerator implements ISpecGenerator {

	private XMLSpecGenerator() {}
	
    public String generateSpec(ObjectList objects, ArrayList relations,
            VPackage pack) {
        GObj obj;
        ClassField field;
//        String method = "";
        String spec = "";

        StringBuffer s = new StringBuffer();
        s.append("<package name=\"" + pack.getPackageClassName() + "\">\n");

        for (int i = 0; i < objects.size(); i++) {
            obj = (GObj) objects.get(i);
            s.append("<object type=\"" + obj.getClassName() + "\" name=\""
                    + obj.getName() + "\">\n");
            s.append("  <fields>\n");
            for (int j = 0; j < obj.fields.size(); j++) {
                field = (ClassField) obj.fields.get(j);
                if (field.getValue() != null) {
                    s.append("    <field name=\""+ field.getName()
                            + "\" value =\""+ field.getValue() + "\">\n");

                }
            }
            s.append("  </fields>\n");
            s.append("</object>\n\n");
        }
        Connection rel;

        for (int i = 0; i < relations.size(); i++) {
            rel = (Connection) relations.get(i);

            if (rel.endPort.getName().equals("any")) {
                s.append("<rel obj1 =\"" + rel.endPort.getObject().getName() 
                        + "\" port1 =\"" +rel.beginPort.getName() + "\""  
                        + " obj2 =\""  + rel.beginPort.getObject().getName() + "\" port2=\""
                        + rel.beginPort.getName() + "\"/>\n");
            } else if (rel.beginPort.getName().equals("any")) {
                s.append("<rel obj1 =\"" + rel.endPort.getObject().getName() + "\" port1=\""
                        + rel.endPort.getName() + "\" obj2=\""
                        + rel.beginPort.getObject().getName() + "\" port2 =\""
                        + rel.endPort.getName() + "\"/>\n");
            } else {
                s.append("<rel obj1 =\"" + rel.endPort.getObject().getName() + "\" port1=\""
                        + rel.endPort.getName() + "\" "
                        + " obj2=\"" + rel.beginPort.getObject().getName() + "\""
                        + " port2=\"" + rel.beginPort.getName() + "\"/>\n");
            }

        }
        s.append(spec);
        s.append("\n</package>");
        s.append("\n");

        return s.toString();
    }

    public static void init() {
		FactoryStorage.register( new Factory() );
	}
    
    static class Factory implements IFactory {

    	private static ISpecGenerator instance;
    	
		public String getInterfaceInstance() {
			return "\\SPECGEN\\XML";
		}

		public ISpecGenerator getInstance() {
			if( instance == null ) {
				instance = new XMLSpecGenerator();
			}
			return instance;
		}
		
		public String getDescription() {
			return "XML specification";
		}	
	}
}
