package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.ccl.CompileException;
import ee.ioc.cs.vsle.ccl.CCL;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.synthesize.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import javax.swing.*;

/**
 */
public class ProgramRunner {
	private Object genObject;

	private ObjectList objects;
	private ClassList classList;
    private List<Var> assumptions = new ArrayList<Var>(); 
    private Object[] arguments;
    private String mainClassName = new String();
    private ArrayList relations;
    private VPackage vPackage;
    
	public ProgramRunner( ArrayList relations, ObjectList objs, VPackage pack ) {
		objects = GroupUnfolder.unfold( objs );
		this.relations = relations;
		this.vPackage = pack;
	}
	
	public String getSpec() {
		ISpecGenerator sgen = SpecGenFactory.getInstance().getCurrentSpecGen();

        return sgen.generateSpec( objects, relations, vPackage );
	}
	
	private Object[] getArguments() throws Exception {
    	if( assumptions.isEmpty() ) {
    		return new Object[0];
    	}
    	
    	if( arguments == null ) {
    		ProgramAssumptionsDialog ass = new ProgramAssumptionsDialog( null, mainClassName, assumptions );
    		
    		if( ass.isOK )
    		{
    			arguments = ass.getArgs();
    		} else {
    			throw new Exception( "Assumptions undefined" );
    		}
    	}
    	
    	return arguments;
    }
	
	public boolean compileAndRun( String genCode )
    {
    	arguments = null;
    	
    	Synthesizer.makeProgram( genCode, classList, mainClassName );
    	
        ArrayList<String> watchFields = watchableFields( objects );
        
        try {
        	return compileAndRun( watchFields, getArguments() ) != null;
        } catch ( CompileException ce ) {
            ErrorWindow.showErrorMessage(
                    "Compilation failed:\n " + ce.excDesc );
        } catch ( Exception ce ) {
            ErrorWindow.showErrorMessage( ce.getMessage() );
        }
        
        return false;
    }
	
	public String invoke( String invokeText )
    {
    	ArrayList<String> watchFields = watchableFields( objects );

        if ( genObject != null ) {
            if ( !invokeText.equals( "" ) ) {
                int k = Integer.parseInt( invokeText );

                for ( int i = 0; i < k; i++ ) {
                    try {
						return run( watchFields, getArguments() );
					} catch (Exception e) {
						ErrorWindow.showErrorMessage( e.getMessage() );
					}
                }
            } else {
                try {
					return run( watchFields, getArguments() );
				} catch (Exception e) {
					ErrorWindow.showErrorMessage( e.getMessage() );
				}
            }
            
            runPropagate();
        }
        return "";
    }
       
	public String invokeNew( String invokeText ) {
		arguments = null;
		return invoke( invokeText );
	}
	
    public String compute()
    {
    	return compute( getSpec(), true );
    }
    
    public String compute( String fullSpec, boolean computeAll ) {
    	
        try {
            mainClassName = SpecParser.getClassName( fullSpec );
            
            if ( RuntimeProperties.isLogInfoEnabled() )
    			db.p( "Computing " + mainClassName );
            
            classList = SpecParser.parseSpecification( fullSpec );
            assumptions.clear();
            return Synthesizer.makeProgramText( fullSpec, computeAll, classList, mainClassName, assumptions );
        } catch ( UnknownVariableException uve ) {

            db.p( "Fatal error: variable " + uve.excDesc + " not declared" );
            ErrorWindow.showErrorMessage(
                    "Fatal error: variable " + uve.excDesc + " not declared" );

        } catch ( LineErrorException lee ) {
            db.p( "Fatal error on line " + lee.excDesc );
            ErrorWindow.showErrorMessage(
                    "Syntax error on line '" + lee.excDesc + "'" );

        } catch ( EquationException ee ) {
            ErrorWindow.showErrorMessage( ee.excDesc );

        } catch ( MutualDeclarationException lee ) {
            db.p(
                    "Mutual recursion in specifications, between classes "
                    + lee.excDesc );
            ErrorWindow.showErrorMessage(
                    "Mutual recursion in specifications, classes " + lee.excDesc );

        } catch ( SpecParseException spe ) {
            db.p( spe.excDesc );
            ErrorWindow.showErrorMessage( spe.excDesc );

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
        
        return null;
    }
    
    private ArrayList<String> watchableFields( ObjectList objects ) {
        ClassField field;
        GObj obj;

        objects = GroupUnfolder.unfold( objects );
        ArrayList<String> watchFields = new ArrayList<String>();

        for ( int i = 0; i < objects.size(); i++ ) {
            obj = ( GObj ) objects.get( i );
            for ( int j = 0; j < obj.fields.size(); j++ ) {
                field = ( ClassField ) obj.fields.get( j );
                if ( field.isWatched() ) {
                    watchFields.add( obj.name + "." + field.getName() );
                }
            }
        }
        return watchFields;
    }
    
	private static HashSet<Var> foundVars = new HashSet<Var>();

	public static void clearFoundVars() {
		foundVars.clear();
	}

	public static void addFoundVar(Var var) {
		if (isFoundVar(var)) {
			return;
		}
		foundVars.add(var);
	}

	public static void addAllFoundVars(Collection<Var> col) {
		for (Var var : col ) {
			if( !foundVars.contains(var) ) {
				foundVars.add(var);
			}
		}
	}

	public static boolean isFoundVar(Var var) {
		for (Iterator iter = foundVars.iterator(); iter.hasNext();) {
			Var in = (Var) iter.next();
			if (in.toString().equals(var.toString())) {
				return true;
			}
		}
		return false;
	}

	public static void printFoundVars() {
		if (RuntimeProperties.isLogDebugEnabled())
			System.err.println("foundVars: " + foundVars);
	}

	public void runPropagate() {
		try {
			Class clasType;
			Class clas = genObject.getClass();

			Field f, f2;
			Object lastObj;
			GObj obj;
			ClassField field;

			String fullName;
			Var var;
			boolean varIsComputed;
			db.p("runPropagate() foundVars: " + foundVars);
			for (int i = 0; i < objects.size(); i++) {
				obj = (GObj) objects.get(i);
				f = clas.getDeclaredField(obj.name);
				lastObj = f.get(genObject);
				for (int j = 0; j < obj.fields.size(); j++) {
					field = (ClassField) obj.fields.get(j);
					if (!field.getType().equals("alias")) {
						clasType = f.getType();
						f2 = clasType.getDeclaredField(field.getName());
						Class c = f2.getType();
						fullName = obj.name + "." + field.getName();
						varIsComputed = false;
						if (foundVars != null) {
							Iterator allVarsIter = foundVars.iterator();
							while (allVarsIter.hasNext()) {
								var = (Var) allVarsIter.next();

								if (fullName.equals((var.getObject().toString()
										+ "." + var.getField()).substring(5))) {
									varIsComputed = true;
									break;
								}
							}
						}
						if (varIsComputed) {
							
							if (c.toString().equals("int")) {
								
								field.setValue(Integer.toString(f2.getInt(lastObj)));
								
							} else if (c.toString().equals("double")) {
								
								field.setValue(Double.toString(f2.getDouble(lastObj)));
								
							} else if (c.toString().equals("boolean")) {
								
								field.setValue(Boolean.toString(f2.getBoolean(lastObj)));
								
							} else if (c.toString().equals("char")) {
								
								field.setValue(Character.toString(f2.getChar(lastObj)));
								
							} else if (c.toString().equals("float")) {
								
								field.setValue(Float.toString(f2.getFloat(lastObj)));
								
							} else {// it is type object
								Object o = f2.get(lastObj);
								if( o instanceof String[] ) {
									String[] sar = (String[])o;
									String result = "";
									for(int k = 0; k < sar.length; k++ ) {
										result += sar[k] + ClassField.ARRAY_TOKEN;
									}
									field.setValue(result);
								} else {
									field.setValue(o.toString());
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private Object compile() throws CompileException {
		genObject = makeGeneratedObject( mainClassName );
		
		return genObject;
	}
	
	private String compileAndRun( ArrayList<String> watchFields,
			Object[] args ) throws CompileException {
		
		if ( compile() != null ) {
			return run(watchFields, args );
		}
		return null;
	}

	private String run(ArrayList<String> watchFields, Object[] args ) {
		StringBuffer result = new StringBuffer();
		
		try {
			Class clas = genObject.getClass();
			Method method = clas.getMethod("compute", Object[].class);
			db.p( "Running" );
			for (int i = 0; i < args.length; i++) {
				db.p( args[i].getClass() + " " + args[i] );
			}
			method.invoke(genObject, new Object[]{ args } );
			Field f;
			StringTokenizer st;
			Object lastObj;

			for (int i = 0; i < watchFields.size(); i++) {
				lastObj = genObject;
				clas = genObject.getClass();
				st = new StringTokenizer( watchFields.get(i), ".");
				while (st.hasMoreElements()) {
					String s = st.nextToken();

					f = clas.getDeclaredField(s);
					if (st.hasMoreElements()) {
						clas = f.getType();
						lastObj = f.get(lastObj);
					} else {
						Class c = f.getType();

						if (c.toString().equals("int")) {
							result.append(watchFields.get(i)
									+ ": " + f.getInt(lastObj) + "\n");
						} else if (c.toString().equals("double")) {
							result.append(watchFields.get(i)
									+ ": " + f.getDouble(lastObj) + "\n");
						} else if (c.toString().equals("boolean")) {
							result.append(watchFields.get(i)
									+ ": " + f.getBoolean(lastObj) + "\n");
						} else if (c.toString().equals("char")) {
							result.append(watchFields.get(i)
									+ ": " + f.getChar(lastObj) + "\n");
						} else if (c.toString().equals("float")) {
							result.append(watchFields.get(i)
									+ ": " + f.getFloat(lastObj) + "\n");
						} else {
							result.append(watchFields.get(i)
									+ ": " + f.get(lastObj) + "\n");
						}
					}

				}
			}
			result.append("----------------------\n");

		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
		
		return result.toString();
	}

	private Object makeGeneratedObject(String programName) throws CompileException {
		CCL classLoader = new CCL();

		Object inst = null;
		try {
			if (classLoader.compile2(programName)) {				
				Class clas = classLoader.loadClass(programName);
				inst = clas.newInstance();
			}

		} catch (NoClassDefFoundError e) { 
			JOptionPane.showMessageDialog(null, "Class not found:\n" + e.getMessage(),
					"Execution error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(System.err);
		} catch (Exception e) {
			e.printStackTrace(System.err);
			return null;
		}
		return inst;
	}
}
