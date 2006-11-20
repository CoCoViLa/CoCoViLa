package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.ccl.*;
import ee.ioc.cs.vsle.event.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.synthesize.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

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
		
		m_canvas.registerRunner( m_id );
		
		updateFromCanvas();
	}
	
	private void updateFromCanvas() {
		objects = GroupUnfolder.unfold( m_canvas.objects );
	}
	
	
	public void destroy() {
		
		m_canvas.unregisterRunner( m_id );
		
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
		printFoundVars();
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

			if( genObject == null || isWorking() ) return;

			final Class clas = genObject.getClass();

			Field fieldOfGobj, fieldOfCf;
			Object lastObj;

			db.p("runPropagate() foundVars: " + foundVars);

			for ( GObj gObj : objects ) {

				fieldOfGobj = clas.getDeclaredField(gObj.getName());
				lastObj = fieldOfGobj.get(genObject);

				for ( ClassField cf : gObj.fields ) {

					if ( cf.isAlias() || cf.isInput() ) {
						continue;
					}

					fieldOfCf = fieldOfGobj.getType().getDeclaredField(cf.getName());

					boolean varIsComputed = false;

					for ( Var var : foundVars ) {

						if ( var.toString().equals( gObj.getName() + "." + cf.getName() ) ) {
							varIsComputed = true;
							break;
						}
					}

					if ( varIsComputed ) {

						String typeOfCf = fieldOfCf.getType().toString();

						if (typeOfCf.equals(TYPE_INT)) {

							cf.setValue(Integer.toString(fieldOfCf.getInt(lastObj)));

						} else if (typeOfCf.equals(TYPE_DOUBLE)) {

							cf.setValue(Double.toString(fieldOfCf.getDouble(lastObj)));

						} else if (typeOfCf.equals(TYPE_BOOLEAN)) {

							cf.setValue(Boolean.toString(fieldOfCf.getBoolean(lastObj)));

						} else if (typeOfCf.equals(TYPE_CHAR)) {

							cf.setValue(Character.toString(fieldOfCf.getChar(lastObj)));

						} else if (typeOfCf.equals(TYPE_FLOAT)) {

							cf.setValue(Float.toString(fieldOfCf.getFloat(lastObj)));

						} else if (typeOfCf.equals(TYPE_LONG)) {

							cf.setValue(Long.toString(fieldOfCf.getLong(lastObj)));

						} else if (typeOfCf.equals(TYPE_SHORT)) {

							cf.setValue(Short.toString(fieldOfCf.getShort(lastObj)));

						} else if (typeOfCf.equals(TYPE_BYTE)) {

							cf.setValue(Byte.toString(fieldOfCf.getByte(lastObj)));

						} else {// it is type object
							Object o = fieldOfCf.get(lastObj);
							if( o instanceof String[] ) {
								String[] sar = (String[])o;
								String result = "";
								for(int k = 0; k < sar.length; k++ ) {
									result += sar[k] + ClassField.ARRAY_TOKEN;
								}
								cf.setValue(result);
							} else {
								cf.setValue(o.toString());
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
						/* Stacktrace is printed so that there is some feedback when generated
						 * code throws an exception which isn't caught. */
						ex.printStackTrace();
					} 
					
					RunningThreadKillerDialog.removeThread( this );
					
					setWorking( false );
					
					db.p( "--> Finished!!! " + Thread.currentThread().getName() );

					if( sendFeedback ) {
						
						ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, id,
								ProgramRunnerFeedbackEvent.TEXT_RESULT, printWatchFields() );
						
						EventSystem.queueEvent( evt );
					}
					
					if( doPropagate ) {
						propagate();
					}

				} catch (Exception e) {
					ErrorWindow.showErrorMessage( e.getMessage() );
				}
				
			}
		}.start();

	}

	private String printWatchFields() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {

		StringBuffer result = new StringBuffer();

		Object obj; 
		Class clas;
		Field f;
		StringTokenizer st;

		ArrayList<String> watchFields = watchableFields();

		for ( String wf : watchFields ) {

			st = new StringTokenizer( wf, ".");
			obj = genObject;
			clas = genObject.getClass();

			while (st.hasMoreElements()) {

				String s = st.nextToken();

				f = clas.getDeclaredField(s);
				if (st.hasMoreElements()) {
					clas = f.getType();
					obj = f.get(obj);
				} else {
					Class c = f.getType();

					if (c.toString().equals(TYPE_INT)) {
						result.append(wf + ": " + f.getInt(obj) + "\n");
					} else if (c.toString().equals(TYPE_LONG)) {
						result.append(wf + ": " + f.getLong(obj) + "\n");
					} else if (c.toString().equals(TYPE_DOUBLE)) {
						result.append(wf + ": " + f.getDouble(obj) + "\n");
					} else if (c.toString().equals(TYPE_BOOLEAN)) {
						result.append(wf + ": " + f.getBoolean(obj) + "\n");
					} else if (c.toString().equals(TYPE_CHAR)) {
						result.append(wf + ": " + f.getChar(obj) + "\n");
					} else if (c.toString().equals(TYPE_FLOAT)) {
						result.append(wf + ": " + f.getFloat(obj) + "\n");
					} else if (c.toString().equals(TYPE_SHORT)) {
						result.append(wf + ": " + f.getShort(obj) + "\n");
					} else if (c.toString().equals(TYPE_BYTE)) {
						result.append(wf + ": " + f.getByte(obj) + "\n");
					} else {
						result.append(wf + ": " + f.get(obj) + "\n");
					}
				}

			}
		}

		result.append("----------------------\n");

		return result.toString();
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
				
				ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, event.getId(),
						ProgramRunnerFeedbackEvent.DISPOSE, null );
				
				EventSystem.queueEvent( evt );
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
