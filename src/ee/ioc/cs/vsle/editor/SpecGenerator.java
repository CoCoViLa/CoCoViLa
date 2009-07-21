package ee.ioc.cs.vsle.editor;

import java.util.*;

import ee.ioc.cs.vsle.factoryStorage.*;
import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

/**
 * Created by IntelliJ IDEA.
 * User: Ando
 * Date: 3.05.2004
 * Time: 17:53:32
 */
public class SpecGenerator implements ISpecGenerator {
	
	private static final String OFFSET1 = CodeGenerator.OT_TAB;
	private static final String OFFSET2 = OFFSET1 + OFFSET1;
	private static final String OFFSET3 = OFFSET2 + OFFSET1;
	
	private SpecGenerator() {
		// use the factory to get instances
	}
	
	public String generateSpec(Scheme scheme, String className) {
	    StringBuilder goalAxiom = null;
	    StringBuilder inputs = null;
	    
    	ObjectList objects = scheme.getObjects();
    	ConnectionList relations = scheme.getConnections();

    	GObj superClass = scheme.getSuperClass();
    	
    	StringBuilder s = new StringBuilder();
		
		// construct class and spec declarations
		s.append("public class " ).append( className);
		if (superClass != null) {
			s.append(" extends ");
			s.append(superClass.getClassName());
		}
		s.append(" {\n").append( OFFSET1 ).append("/*@ specification  ").append(className);
		
		if (superClass != null) {
			s.append(" super ");
			s.append(superClass.getClassName());
		}
		s.append(" {\n");

		Hashtable<String, List<String>> multiRels = new Hashtable<String, List<String>>();
		
		GObj obj;
		for (int i = 0; i < objects.size(); i++) {
			obj = objects.get(i);

			if (!obj.isSuperClass()) {
				s.append( OFFSET2 ).append( ( obj.isStatic() ? "static " : "" ) 
						).append( obj.getClassName() ).append( " " ).append( obj.getName() ).append( ";\n");
			}

			for ( ClassField field : obj.getFields() ) {
			    
			    if( field.isGoal() || field.isInput() ) {
			        
			        String var = "";
                    if (!obj.isSuperClass()) {
                        var += obj.getName() + ".";
                    }
                    var += field.getName() ;
                    
                    if( field.isGoal() ) {
                    	if( goalAxiom == null ) {
                    		goalAxiom = new StringBuilder( "-> " ).append( var );
                    	} else {
                    		goalAxiom.append( ", " ).append( var );
                    	}

                    	continue;
                    }
                    
                    if( inputs == null ) {
                    	inputs = new StringBuilder( var );
                	} else {
                		inputs.append( ", " ).append( var );
                	}
			    }
			    
				if ( field.getValue() != null ) {
					appendSpecFieldLHS(obj, field, s);

					if (field.getType().equals(TYPE_STRING)) {
						s.append("\"" ).append( field.getValue() ).append( "\";\n");
					} else if (field.isPrimitiveArray()) {
						s.append("{");
						String[] split = field.getValue().split( TypeUtil.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append(split[k]);
							} else
								s.append(", " ).append( split[k]);
						}
						s.append("};\n");
						
					} else if (field.isPrimOrStringArray()) {
						s.append("{");
						String[] split = field.getValue().split( TypeUtil.ARRAY_TOKEN );
						for (int k = 0; k < split.length; k++) {
							if (k == 0) {
								s.append("\"" ).append( split[k] ).append( "\"");
							} else
								s.append(", \"" ).append( split[k] ).append( "\"");
						}
						s.append("};\n");
					} else {
						s.append(field.getValue() ).append( ";\n");
					}
				}
			}
			
			//not connected multiport is an empty array and can be used in computations
			for(Port port : obj.getPorts()) {
			    if(port.isMulti()) {
			        String multiport = ( port.getObject().isSuperClass() ? ""
	                        : port.getObject().getName() + "." ) + port.getName();
			        multiRels.put( multiport, new ArrayList<String>() );
			    }
			        
			}
		}
		
		for (Connection rel : relations) {
			if (rel.getEndPort().getName().equals("any")) {
				s.append(
						OFFSET2 ).append( rel.getEndPort().getObject().getName() ).append( "." ).append( rel.getBeginPort().getName()
						).append( " = " ).append( rel.getBeginPort().getObject().getName() ).append( "."
						).append( rel.getBeginPort().getName() ).append( ";\n");
				
			}  else if  (rel.getBeginPort().getName().equals("any")) {
				s.append(
						OFFSET2 ).append( rel.getEndPort().getObject().getName() ).append( "." ).append( rel.getEndPort().getName()
						).append( " = " ).append( rel.getBeginPort().getObject().getName() ).append( "."
						).append( rel.getEndPort().getName() ).append( ";\n");

			} else if( rel.getBeginPort().isMulti() || rel.getEndPort().isMulti() ) {
				
				Port multi = rel.getBeginPort().isMulti() ? rel.getBeginPort() : rel.getEndPort();
				Port simple = !rel.getBeginPort().isMulti() ? rel.getBeginPort() : rel.getEndPort();
				
				String multiport = ( multi.getObject().isSuperClass() ? ""
                        : multi.getObject().getName() + "." ) + multi.getName();
				String port = simple.getObject().getName() + "." + simple.getName();
				
				List<String> list = multiRels.get( multiport );
				if( list == null ) {
					list = new ArrayList<String>(); 
	                multiRels.put( multiport, list );
				} 
				list.add( port );
			} else {
				String startObjName = 
				    TypeUtil.TYPE_THIS.equalsIgnoreCase( rel.getBeginPort().getName() ) 
				        ? rel.getBeginPort().getObject().getName()
				        : ( rel.getBeginPort().getObject().isSuperClass() ) 
						    ? rel.getBeginPort().getName()
							: rel.getBeginPort().getObject().getName() + "." + rel.getBeginPort().getName();
										
				String endObjName = 
				    TypeUtil.TYPE_THIS.equalsIgnoreCase( rel.getEndPort().getName() ) 
                        ? rel.getEndPort().getObject().getName()
                        : ( rel.getEndPort().getObject().isSuperClass() )
							? rel.getEndPort().getName()
							: rel.getEndPort().getObject().getName() + "." + rel.getEndPort().getName();
				
				s.append( OFFSET2 ).append( endObjName ).append( " = " ).append( startObjName ).append( ";\n" );
			}
			
		}
		
		for (String multiport : multiRels.keySet() ) {
			List<String> portlist = multiRels.get( multiport );
			String portarray = "";
			for (int i = 0, len = portlist.size(); i < len; i++) {
				String port = portlist.get( i );
				if( i != len - 1 ) {
					portarray += port + ", ";
				} else {
					portarray += port;
				}
			}
			s.append( OFFSET2 ).append( multiport ).append( " = [ " ).append( portarray ).append( " ];\n");
		}
		
		if( goalAxiom != null ) {
			
			s.append( OFFSET2 );
			
			if( inputs != null ) {
				s.append( inputs ).append( " " );
			}
			
		    s.append( goalAxiom ).append( ";\n" );
		}
		
		s.append( OFFSET1 ).append("}@*/\n}\n");
		
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
			StringBuilder buf) {
		buf.append(OFFSET3);

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

