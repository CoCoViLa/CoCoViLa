package ee.ioc.cs.vsle.editor;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.vclass.*;

public class XMLSpecGenerator implements ISpecGenerator {

	private XMLSpecGenerator() {
		// use the factory
	}
	
    public String generateSpec(Scheme scheme, String className) {
        GObj obj;
        String spec = "";

    	ObjectList objects = scheme.getObjectList();
    	ConnectionList relations = scheme.getConnectionList();

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
            if (rel.getEndPort().getName().equals("any")) {
                s.append("<rel obj1 =\"" + rel.getEndPort().getObject().getName() 
                        + "\" port1 =\"" +rel.getBeginPort().getName() + "\""  
                        + " obj2 =\""  + rel.getBeginPort().getObject().getName() + "\" port2=\""
                        + rel.getBeginPort().getName() + "\"/>\n");
            } else if (rel.getBeginPort().getName().equals("any")) {
                s.append("<rel obj1 =\"" + rel.getEndPort().getObject().getName() + "\" port1=\""
                        + rel.getEndPort().getName() + "\" obj2=\""
                        + rel.getBeginPort().getObject().getName() + "\" port2 =\""
                        + rel.getEndPort().getName() + "\"/>\n");
            } else {
                s.append("<rel obj1 =\"" + rel.getEndPort().getObject().getName() + "\" port1=\""
                        + rel.getEndPort().getName() + "\" "
                        + " obj2=\"" + rel.getBeginPort().getObject().getName() + "\""
                        + " port2=\"" + rel.getBeginPort().getName() + "\"/>\n");
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
    
    static class Factory implements IFactory<XMLSpecGenerator> {

    	private static XMLSpecGenerator instance;
    	
    @Override
		public XMLSpecGenerator getInstance() {
			if( instance == null ) {
				instance = new XMLSpecGenerator();
			}
			return instance;
		}
		
		@Override
		public String getDescription() {
			return "XML specification";
		}

    @Override
    public Class<ISpecGenerator> getInterfaceClass() {
      return ISpecGenerator.class;
    }	
	}
}
