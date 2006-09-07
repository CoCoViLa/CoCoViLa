package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.ccl.*;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.synthesize.*;

import java.lang.reflect.*;
import java.util.*;

import javax.swing.*;

/**
 */
public class ProgramRunner {
	
	private final static Object s_lock = new Object();
	private boolean isWorking = false;
	
	private long m_id;
	private ProgramRunnerEventListener m_lst = new ProgramRunnerEventListener();
	
	private Object genObject;

	private ObjectList objects;
	private ClassList classList;
    private List<Var> assumptions = new ArrayList<Var>(); 
    private Object[] arguments;
    private String mainClassName = new String();
    private Canvas m_canvas;
    
	public ProgramRunner( Canvas canvas ) {
		
		m_id = System.currentTimeMillis();
		ProgramRunnerEvent.registerListener( m_lst );
		
		m_canvas = canvas;
		
		updateFromCanvas();
	}
	
	private void updateFromCanvas() {
		objects = GroupUnfolder.unfold( m_canvas.objects );
	}
	
	
	public void destroy() {
		
		if( m_lst != null ) {
			
			ProgramRunnerEvent.unregisterListener( m_lst );
			
			m_lst = null;
			
		}
	}
	
	private String getSpec() {
		
		updateFromCanvas();
		
        return SpecGenFactory.getInstance()
        			.getCurrentSpecGen().generateSpec( objects, m_canvas.connections, m_canvas.getCurrentPackage() );
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
    			throw new Exception( "Unable to run the program: assumptions undefined" );
    		}
    	}
    	
    	return arguments;
    }
	
	private String compile( String genCode ) {

		arguments = null;

		try {
			Synthesizer.makeProgram( genCode, classList, mainClassName, m_canvas.getWorkDir() );

			CCL classLoader = new CCL();

			genObject = null;

			if (classLoader.compile2(mainClassName)) {				
				Class clas = classLoader.loadClass(mainClassName);
				genObject = clas.newInstance();
			}
		} catch (NoClassDefFoundError e) { 
			JOptionPane.showMessageDialog(null, "Class not found:\n" + e.getMessage(),
					"Execution error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace(System.err);

		} catch ( CompileException ce ) {
			ErrorWindow.showErrorMessage(
					"Compilation failed:\n " + ce.excDesc );
		} catch (SpecParseException e) {
			ErrorWindow.showErrorMessage(
					"Compilation failed:\n " + e.excDesc );
		} catch ( Exception ce ) {
			ErrorWindow.showErrorMessage( ce.getMessage() );
			ce.printStackTrace( System.err );
		}

		return null;
	}
	   
    private String compute( String fullSpec, boolean computeAll ) {
    	
        try {
        	
            mainClassName = SpecParser.getClassName( fullSpec );
            
            if ( RuntimeProperties.isLogInfoEnabled() )
    			db.p( "Computing " + mainClassName );
            
            classList = SpecParser.parseSpecification( fullSpec, m_canvas.getWorkDir() );
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
    
    private ArrayList<String> watchableFields() {
        ClassField field;
        GObj obj;

        objects = GroupUnfolder.unfold( objects );
        ArrayList<String> watchFields = new ArrayList<String>();

        for ( int i = 0; i < objects.size(); i++ ) {
            obj = objects.get( i );
            for ( int j = 0; j < obj.fields.size(); j++ ) {
                field = obj.fields.get( j );
                if ( field.isWatched() ) {
                    watchFields.add( obj.getName() + "." + field.getName() );
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

	private void propagate() {
		try {
			
			if( genObject == null || isWorking ) return;
			
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
				obj = objects.get(i);
				f = clas.getDeclaredField(obj.getName());
				lastObj = f.get(genObject);
				for (int j = 0; j < obj.fields.size(); j++) {
					field = obj.fields.get(j);
					if ( !field.isAlias() ) {
						clasType = f.getType();
						f2 = clasType.getDeclaredField(field.getName());
						Class c = f2.getType();
						fullName = obj.getName() + "." + field.getName();
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
		} finally {
			m_canvas.repaint();
		}
	}

	private void run( final boolean sendFeedback, final long id, final boolean doPropagate ) {

		if( genObject == null ) return;

		new Thread() {
			public void run()
			{
				Thread.currentThread().setName( "RunningThread" + System.currentTimeMillis() );
				
				StringBuffer result = new StringBuffer();

				try {
					Object[] args = getArguments();
					for (int i = 0; i < args.length; i++) {
						db.p( args[i].getClass() + " " + args[i] );
					}
					
					Class clas = genObject.getClass();
					Method method = clas.getMethod("compute", Object[].class);
					db.p( "Running... ( NB! The thread is alive until the next message --> ) " 
							+ Thread.currentThread().getName() );
					
					setWorking( true );
					
					RunningThreadKillerDialog.addThread( this );
					
					try {
						method.invoke(genObject, new Object[]{ args } );
					} catch( InvocationTargetException ex ) { 
						/* this is empty because we do not need to show ThreadDeath exception's 
						 * stacktrace after killing a thread using RunningThreadKillerDialog*/
					}
					
					RunningThreadKillerDialog.removeThread( this );
					
					setWorking( false );
					
					db.p( "--> Finished!!! " + Thread.currentThread().getName() );

					Field f;
					StringTokenizer st;
					Object lastObj;

					ArrayList<String> watchFields = watchableFields();

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
					ErrorWindow.showErrorMessage( e.getMessage() );
				}
				if( sendFeedback ) {
					
					ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, id,
							ProgramRunnerFeedbackEvent.TEXT_RESULT, result.toString() );
					
					EventSystem.queueEvent( evt );
				}
				
				if( doPropagate ) {
					propagate();
				}
			}
		}.start();

	}
	
	public long getId() {
		return m_id;
	}
	
	class ProgramRunnerEventListener implements ProgramRunnerEvent.Listener {

		public void onProgramRunnerEvent(ProgramRunnerEvent event) {

			if( event.getId() != m_id ) return;
			
			int operation = event.getOperation();
			
			String programSource = null;
			
			if( ( operation & ProgramRunnerEvent.REQUEST_SPEC ) > 0 ) {
				
				ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, event.getId(),
						ProgramRunnerFeedbackEvent.TEXT_SPECIFICATION, getSpec() );
				
				EventSystem.queueEvent( evt );
				
				return;
			}

			if( ( ( operation & ProgramRunnerEvent.COMPUTE_GOAL ) > 0 ) 
					|| ( ( operation & ProgramRunnerEvent.COMPUTE_ALL ) > 0 ) ) {
				
				String spec = ( event.getSpecText() != null )
							    ? event.getSpecText()
							    : getSpec();
							  
				programSource = compute( spec, ( ( operation & ProgramRunnerEvent.COMPUTE_ALL ) > 0 ) );
				
				if( event.isRequestFeedback() ) {
					ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, event.getId(),
							ProgramRunnerFeedbackEvent.TEXT_PROGRAM, programSource );
					
					EventSystem.queueEvent( evt );
				}
			}

			if( ( operation & ProgramRunnerEvent.COMPILE ) > 0 ) {
				
				compile( event.getProgramText() != null ? event.getProgramText() : programSource );
				
			}
			
			boolean isPropagated = false;
			
			if( ( operation & ProgramRunnerEvent.RUN ) > 0 ) {

				for ( int i = 0; i < event.getRepeat(); i++ ) {
					try {
						run( event.isRequestFeedback(), 
								event.getId(),
								( ( operation & ProgramRunnerEvent.PROPAGATE ) > 0 ) );
						isPropagated = true;
					} catch (Exception e) {
						ErrorWindow.showErrorMessage( e.getMessage() );
					}
				}
			}

			if( !isPropagated && ( operation & ProgramRunnerEvent.PROPAGATE ) > 0 ) {
				propagate();
			}

			if( ( operation & ProgramRunnerEvent.DESTROY ) > 0 ) {
				destroy();
			}

		}
		
	}

	public boolean isWorking() {
		
		synchronized( s_lock ) {
			return isWorking;
		}
	}

	public void setWorking( boolean isWorking ) {
		synchronized( s_lock ) {
			this.isWorking = isWorking;
		}
	}

}
