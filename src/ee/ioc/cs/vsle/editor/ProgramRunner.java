package ee.ioc.cs.vsle.editor;

import ee.ioc.cs.vsle.vclass.*;
import ee.ioc.cs.vsle.api.Scheme;
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

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                m_canvas.unregisterRunner( m_id );
            }
        } );

        if ( m_lst != null ) {

            ProgramRunnerEvent.unregisterListener( m_lst );

            m_lst = null;

        }
    }

    private String getSpec() {

        updateFromCanvas();

        return SpecGenFactory.getInstance().getCurrentSpecGen().generateSpec( m_canvas.scheme );
    }

    private Object[] getArguments() throws Exception {
        if ( getAssumptions().isEmpty() ) {
            return new Object[ 0 ];
        }

        if ( arguments == null ) {
            ProgramAssumptionsDialog ass = new ProgramAssumptionsDialog( null, mainClassName, getAssumptions() );

            if ( ass.isOK ) {
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

            if ( classLoader.compile2( mainClassName ) ) {
                Class<?> clas = classLoader.loadClass( mainClassName );
                genObject = clas.newInstance();
            }
            Class<?> pc = classLoader.loadClass( "ee.ioc.cs.vsle.api.ProgramContext" );
            pc.getMethod( "setScheme", Scheme.class ).invoke( null, m_canvas.scheme );

        } catch ( NoClassDefFoundError e ) {
            JOptionPane.showMessageDialog( null, "Class not found:\n" + e.getMessage(), "Execution error",
                    JOptionPane.ERROR_MESSAGE );
            e.printStackTrace( System.err );

        } catch ( CompileException ce ) {
            ErrorWindow.showErrorMessage( "Compilation failed:\n " + ce.excDesc );
        } catch ( SpecParseException e ) {
            ErrorWindow.showErrorMessage( "Compilation failed:\n " + e.excDesc );
        } catch ( Exception ce ) {
            ErrorWindow.showErrorMessage( ce.getMessage() );
            ce.printStackTrace( System.err );
        }

        return null;
    }

    private String compute( String fullSpec, boolean computeAll ) {

        try {
            foundVars.clear();

            mainClassName = SpecParser.getClassName( fullSpec );

            if ( RuntimeProperties.isLogInfoEnabled() )
                db.p( "Computing " + mainClassName );

            Set<String> schemeObjects = new HashSet<String>();

            for ( GObj gObj : m_canvas.objects ) {
                schemeObjects.add( gObj.getName() );
            }

            classList = SpecParser.parseSpecification( fullSpec, mainClassName, schemeObjects, m_canvas.getWorkDir() );
            getAssumptions().clear();

            return Synthesizer.makeProgramText( fullSpec, computeAll, classList, mainClassName, this );

        } catch ( UnknownVariableException uve ) {

            db.p( "Fatal error: variable " + uve.excDesc + " not declared" );
            ErrorWindow.showErrorMessage( "Fatal error: variable " + uve.excDesc + " not declared" );

        } catch ( LineErrorException lee ) {
            db.p( "Fatal error on line " + lee.excDesc );
            ErrorWindow.showErrorMessage( "Syntax error on line '" + lee.excDesc + "'" );

        } catch ( EquationException ee ) {
            ErrorWindow.showErrorMessage( ee.excDesc );

        } catch ( MutualDeclarationException lee ) {
            db.p( "Mutual recursion in specifications, between classes " + lee.excDesc );
            ErrorWindow.showErrorMessage( "Mutual recursion in specifications, classes " + lee.excDesc );

        } catch ( SpecParseException spe ) {
            db.p( spe.excDesc );
            ErrorWindow.showErrorMessage( spe.excDesc );

        } catch ( Exception ex ) {
            ex.printStackTrace();
        }

        return null;
    }

    private Collection<String> watchableFields() {
        ClassField field;
        GObj obj;

        objects = GroupUnfolder.unfold( objects );
        Collection<String> watchFields = new TreeSet<String>();

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

    private Set<Var> foundVars = new TreeSet<Var>( new Comparator<Var>() {

        public int compare( Var v1, Var v2 ) {

            return v1.toString().compareTo( v2.toString() );
        }
    } );

    public void addFoundVars( Collection<Var> col ) {
        foundVars.addAll( col );
    }

    private String printFoundVars() {

        StringBuilder result = new StringBuilder( "----------- Found Vars -----------\n" );

        for ( Var var : foundVars ) {

            if ( var.getField().isAlias() || var.getField().isVoid() ) {
                continue;
            }

            try {
                appendVarStringValue( var.getFullName(), result );
            } catch ( Exception e ) {
            }
        }

        result.append( "----------------------------------\n" );
        result.append( "\n" );

        return result.toString();
    }

    private void showComputedValues( final String varName ) {
        
        final StringBuilder result = new StringBuilder();

        for ( Var var : foundVars ) {

            if ( var.getField().isAlias() || var.getField().isVoid() ) {
                continue;
            }

            String varname;
            
            if( ( varname = var.getFullName() ).startsWith( varName ) ) {
                try {
                    appendVarStringValue( varname, result );
                } catch ( Exception e ) {
                }
            }
        }
        
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JOptionPane.showMessageDialog( m_canvas, result.toString(), varName, JOptionPane.PLAIN_MESSAGE );
            }
        } );
    }
    
    private void appendVarStringValue( String fullName, StringBuilder result ) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {

        Class clas;
        Field f;

        StringTokenizer st = new StringTokenizer( fullName, "." );
        Object obj = genObject;
        clas = genObject.getClass();

        while ( st.hasMoreElements() ) {

            String s = st.nextToken();

            // show values of variables declared in superclasses as well
            f = clas.getField( s );// getDeclarField(s);

            if ( st.hasMoreElements() ) {
                clas = f.getType();
                obj = f.get( obj );
            } else {
                Class c = f.getType();

                result.append( fullName );
                result.append( ": " );

                if ( c.toString().equals( TYPE_INT ) ) {
                    result.append( f.getInt( obj ) );
                } else if ( c.toString().equals( TYPE_LONG ) ) {
                    result.append( f.getLong( obj ) );
                } else if ( c.toString().equals( TYPE_DOUBLE ) ) {
                    result.append( f.getDouble( obj ) );
                } else if ( c.toString().equals( TYPE_BOOLEAN ) ) {
                    result.append( f.getBoolean( obj ) );
                } else if ( c.toString().equals( TYPE_CHAR ) ) {
                    result.append( f.getChar( obj ) );
                } else if ( c.toString().equals( TYPE_FLOAT ) ) {
                    result.append( f.getFloat( obj ) );
                } else if ( c.toString().equals( TYPE_SHORT ) ) {
                    result.append( f.getShort( obj ) );
                } else if ( c.toString().equals( TYPE_BYTE ) ) {
                    result.append( f.getByte( obj ) );
                } else {
                    Object o = f.get( obj );
                    if ( o instanceof Object[] ) {

                        result.append( Arrays.deepToString( (Object[]) o ) );
                    } else {
                        result.append( o );
                    }
                }

                result.append( "\n" );
            }

        }

    }

    private void propagate() {
        try {

            if ( genObject == null || isWorking() )
                return;

            final Class clas = genObject.getClass();

            Field fieldOfGobj, fieldOfCf;
            Object lastObj;

            for ( GObj gObj : objects ) {

                // superclass object is a special case that has no field
                // declaration in the generated code so we have to skip it here
                if ( gObj.isSuperClass() )
                    continue;

                fieldOfGobj = clas.getDeclaredField( gObj.getName() );
                lastObj = fieldOfGobj.get( genObject );

                for ( ClassField cf : gObj.fields ) {

                    if ( cf.isAlias() || cf.isInput() ) {
                        continue;
                    }

                    fieldOfCf = fieldOfGobj.getType().getDeclaredField( cf.getName() );

                    boolean varIsComputed = false;

                    for ( Var var : foundVars ) {

                        if ( var.getFullName().equals( gObj.getName() + "." + cf.getName() ) ) {
                            varIsComputed = true;
                            break;
                        }
                    }

                    if ( varIsComputed ) {

                        String typeOfCf = fieldOfCf.getType().toString();

                        if ( typeOfCf.equals( TYPE_INT ) ) {

                            cf.setValue( Integer.toString( fieldOfCf.getInt( lastObj ) ) );

                        } else if ( typeOfCf.equals( TYPE_DOUBLE ) ) {

                            cf.setValue( Double.toString( fieldOfCf.getDouble( lastObj ) ) );

                        } else if ( typeOfCf.equals( TYPE_BOOLEAN ) ) {

                            cf.setValue( Boolean.toString( fieldOfCf.getBoolean( lastObj ) ) );

                        } else if ( typeOfCf.equals( TYPE_CHAR ) ) {

                            cf.setValue( Character.toString( fieldOfCf.getChar( lastObj ) ) );

                        } else if ( typeOfCf.equals( TYPE_FLOAT ) ) {

                            cf.setValue( Float.toString( fieldOfCf.getFloat( lastObj ) ) );

                        } else if ( typeOfCf.equals( TYPE_LONG ) ) {

                            cf.setValue( Long.toString( fieldOfCf.getLong( lastObj ) ) );

                        } else if ( typeOfCf.equals( TYPE_SHORT ) ) {

                            cf.setValue( Short.toString( fieldOfCf.getShort( lastObj ) ) );

                        } else if ( typeOfCf.equals( TYPE_BYTE ) ) {

                            cf.setValue( Byte.toString( fieldOfCf.getByte( lastObj ) ) );

                        } else {// it is type object
                            Object o = fieldOfCf.get( lastObj );
                            if ( o instanceof String[] ) {
                                String[] sar = (String[]) o;
                                String result = "";
                                for ( int k = 0; k < sar.length; k++ ) {
                                    result += sar[ k ] + ClassField.ARRAY_TOKEN;
                                }
                                cf.setValue( result );
                            } else {
                                cf.setValue( o.toString() );
                            }
                        }
                    }
                }
            }
        } catch ( Exception e ) {
            db.p( "Error propagating value: " + e.getClass().getCanonicalName() + " : " + e.getMessage() );
            if ( RuntimeProperties.isLogDebugEnabled() ) {
                e.printStackTrace( System.err );
            }
        } finally {
            m_canvas.repaint();
        }
    }

    private void run( final boolean sendFeedback, final long id, final boolean doPropagate ) {

        if ( genObject == null )
            return;

        new Thread() {
            public void run() {
                Thread.currentThread().setName( "RunningThread" + System.currentTimeMillis() );

                try {
                    Object[] args = getArguments();
                    for ( int i = 0; i < args.length; i++ ) {
                        db.p( args[ i ].getClass() + " " + args[ i ] );
                    }

                    Class clas = genObject.getClass();
                    Method method = clas.getMethod( "compute", Object[].class );
                    db
                            .p( "Running... ( NB! The thread is alive until the next message --> ) "
                                    + Thread.currentThread().getName() );

                    setWorking( true );

                    RunningThreadKillerDialog.addThread( this );

                    try {
                        method.invoke( genObject, new Object[] { args } );
                    } catch ( InvocationTargetException ex ) {
                        /*
                         * Stacktrace is printed so that there is some feedback
                         * when generated code throws an exception which isn't
                         * caught. Stacktrace is not printed if a thread was
                         * stopped manually. 
                         */
                        if ( !( ex.getCause() instanceof ThreadDeath ) ) {
                            ex.printStackTrace();
                        }
                        
                    }

                    RunningThreadKillerDialog.removeThread( this );

                    setWorking( false );

                    db.p( "--> Finished!!! " + Thread.currentThread().getName() );

                    if ( sendFeedback ) {

                        ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, id,
                                ProgramRunnerFeedbackEvent.TEXT_RESULT, printFoundVars() + printWatchFields() );

                        EventSystem.queueEvent( evt );
                    }

                    if ( doPropagate ) {
                        propagate();
                    }

                } catch ( Exception e ) {
                    ErrorWindow.showErrorMessage( e.getMessage() );
                }

            }
        }.start();

    }

    private String printWatchFields() {

        StringBuilder result = new StringBuilder( "---------- Watch Fields ----------\n" );

        Collection<String> watchFields = watchableFields();

        for ( String wf : watchFields ) {

            try {
                appendVarStringValue( wf, result );
            } catch ( Exception e ) {
            }
        }

        result.append( "----------------------------------\n" );
        result.append( "\n" );

        return result.toString();
    }

    public long getId() {
        return m_id;
    }

    class ProgramRunnerEventListener implements ProgramRunnerEvent.Listener {

        public void onProgramRunnerEvent( ProgramRunnerEvent event ) {

            if ( event.getId() != m_id )
                return;

            int operation = event.getOperation();

            String programSource = null;

            if ( ( operation & ProgramRunnerEvent.REQUEST_SPEC ) > 0 ) {

                ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, event.getId(),
                        ProgramRunnerFeedbackEvent.TEXT_SPECIFICATION, getSpec() );

                EventSystem.queueEvent( evt );

                return;
            }

            if ( ( ( operation & ProgramRunnerEvent.COMPUTE_GOAL ) > 0 ) || ( ( operation & ProgramRunnerEvent.COMPUTE_ALL ) > 0 ) ) {

                String spec = ( event.getSpecText() != null ) ? event.getSpecText() : getSpec();

                programSource = compute( spec, ( ( operation & ProgramRunnerEvent.COMPUTE_ALL ) > 0 ) );

                if ( event.isRequestFeedback() ) {
                    ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, event.getId(),
                            ProgramRunnerFeedbackEvent.TEXT_PROGRAM, programSource );

                    EventSystem.queueEvent( evt );
                }
            }

            if ( ( operation & ProgramRunnerEvent.COMPILE ) > 0 ) {

                compile( event.getProgramText() != null ? event.getProgramText() : programSource );

            }

            boolean isPropagated = false;

            if ( ( operation & ProgramRunnerEvent.RUN ) > 0 ) {

                for ( int i = 0; i < event.getRepeat(); i++ ) {
                    try {
                        run( event.isRequestFeedback(), event.getId(), ( ( operation & ProgramRunnerEvent.PROPAGATE ) > 0 ) );
                        isPropagated = true;
                    } catch ( Exception e ) {
                        ErrorWindow.showErrorMessage( e.getMessage() );
                    }
                }
            }

            if ( !isPropagated && ( operation & ProgramRunnerEvent.PROPAGATE ) > 0 ) {
                propagate();
            }

            if( ( operation & ProgramRunnerEvent.SHOW_VALUES ) > 0 ) {
                showComputedValues( event.getObjectName() );
            }

            if ( ( operation & ProgramRunnerEvent.DESTROY ) > 0 ) {
                destroy();

                ProgramRunnerFeedbackEvent evt = new ProgramRunnerFeedbackEvent( this, event.getId(),
                        ProgramRunnerFeedbackEvent.DISPOSE, null );

                EventSystem.queueEvent( evt );
            }

        }

    }

    public boolean isWorking() {

        synchronized ( s_lock ) {
            return isWorking;
        }
    }

    public void setWorking( boolean isWorking ) {
        synchronized ( s_lock ) {
            this.isWorking = isWorking;
        }
    }

    /**
     * @param assumptions the assumptions to set
     */
    public void setAssumptions( List<Var> assumptions ) {
        this.assumptions = assumptions;
    }

    /**
     * @return the assumptions
     */
    public List<Var> getAssumptions() {
        return assumptions;
    }

}
