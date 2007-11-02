package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.vclass.*;

public class XMLSpecGenerator implements ISpecGenerator {

	private XMLSpecGenerator() {
		// use the factory
	}
	
    public String generateSpec(Scheme scheme, String className) {
        GObj obj;
        String spec = "";

    	ObjectList objects = scheme.getObjects();
    	ConnectionList relations = scheme.getConnections();

        StringBuffer s = new StringBuffer();
        s.append("<package name=\"" + className + "\">\n");

        for (int i = 0; i < objects.size(); i++) {
            obj = objects.get(i);
            s.append("<object type=\"" + obj.getClassName() + "\" name=\""
                    + obj.getName() + "\">\n");
            s.append("  <fields>\n");
            for ( ClassField field : obj.getFields() ) {
                if (field.getValue() != null) {
                    s.append("    <field name=\""+ field.getName()
                            + "\" value =\""+ field.getValue() + "\">\n");

                }
            }
            s.append("  </fields>\n");
            s.append("</object>\n\n");
        }

        for (Connection rel : relations) {
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

	public CustomFileFilter getFileFilter() {
		return new CustomFileFilter( CustomFileFilter.EXT.XML );
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
