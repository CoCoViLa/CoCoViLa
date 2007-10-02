package ee.ioc.cs.vsle.synthesize;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

/**
 This class is responsible for managing the planning and code generation process.
 @author Ando Saabas
 */
public class Synthesizer {

	public static final String RE_SPEC = "/\\*@.*specification[ \t\n]+" +
			"[a-zA-Z_0-9-.]+[ \t\n]*(super ([ a-zA-Z_0-9-,]+ ))?" +
			"[ \t\n]*\\{[ \t\n]*(.+)[ \t\n]*\\}[ \t\n]*@\\*/ *";

    public static final String GENERATED_INTERFACE_NAME = "IComputable";
    public static final String SUBTASK_INTERFACE_NAME = "Subtask";

    private Synthesizer() {}
      
    /**
    This method makes a compilable class from problem specification, calling createProblem,
    planner, generates needed classes(_Class_ notation), putting it all together and writing into
    a file.
     * @throws SpecParseException 
    */
    public static void makeProgram( String progText, ClassList classes, String mainClassName, String path ) throws SpecParseException {
        generateSubclasses( classes, path );
        FileFuncs.writeFile( progText, mainClassName, "java", RuntimeProperties.genFileDir, false );
    }

    /** Takes care of steps needed for planning and algorithm extracting, calling problem creator
     * and planner and	returning compilable java source.
     * Creating a problem means parsing the specification(s recursively), unfolding it, and making
     * a flat representation of the specification (essentially a graph). Planning is run on this graph.
     * @param fileString -
     * @param computeAll -
     * @param classList -
     * @param mainClassName -
     * @return String -
     * @throws SpecParseException -
     */
    public static String makeProgramText( String fileString, boolean computeAll, ClassList classList,
                                   String mainClassName, ProgramRunner runner ) throws SpecParseException {

        Problem problem = null;
        // call the packageParser to create a problem from the specification

        try {
            problem = ProblemCreator.makeProblem( classList );
        } catch ( Exception e ) {
            e.printStackTrace();
            
            problem = new Problem( null );
        }

        // run the planner on the obtained problem
        ArrayList<Rel> algorithmList = PlannerFactory.getInstance().getCurrentPlanner().invokePlaning( problem, computeAll );
        if( RuntimeProperties.showAlgorithm ) {
        	AlgorithmVisualizer.getInstance().addNewTab( mainClassName, algorithmList );
        }
        CodeGenerator cg = new CodeGenerator( algorithmList, problem, mainClassName );
        String algorithm = cg.generate();

        runner.addFoundVars( problem.getFoundVars() );
        runner.setAssumptions( problem.getAssumptions() );
        
        StringBuilder prog = new StringBuilder();
        
        // start building the main source file.
        AnnotatedClass ac = classList.getType( TYPE_THIS );

        // check all the fields and make declarations accordingly
        for ( ClassField field : ac.getClassFields() ) {

//        	if( AnnotatedClass.SPEC_OBJECT_NAME.equals( field.getName() ) && ac.getSuperClasses().size() > 0 )
//        		continue;//TODO - remove?
        	
            String dec = TypeUtil.getDeclaration( field, "public" );
           
        	if( dec != null && dec.length() > 0 ) {
                prog.append( CodeGenerator.OT_TAB ).append( dec );
        	}
            
        }
       
        prog.append( "\n" ).append(  CodeGenerator.OT_TAB ).append( getComputeMethodSignature() ).append( " {\n" );
        prog.append( algorithm );
        prog.append( CodeGenerator.OT_TAB ).append( "}\n" );
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("(?:public[ \t\n]+)?class[ \t\n]+" 
        		+ mainClassName + "(?:[ \t\n]+extends[ \t\n]+([A-Za-z]\\w*))?");
        
        matcher = pattern.matcher( fileString );

        if ( matcher.find() ) {
            fileString = matcher.replaceAll("public class " + mainClassName
            		+ (matcher.start(1) < matcher.end(1) 
            				? " extends " + matcher.group(1) : "")
            		+ " implements " + GENERATED_INTERFACE_NAME);
        }

        pattern = Pattern.compile(RE_SPEC, Pattern.DOTALL);
        matcher = pattern.matcher( fileString );

        if ( matcher.find() ) {
            fileString = matcher.replaceAll( "\n" + prog.toString() );
        }

        fileString = "import ee.ioc.cs.vsle.util.*;\nimport ee.ioc.cs.vsle.api.*;\n\n" + fileString 
        			 + "\n" + cg.getIndependentSubtasks();
        
        return fileString;

    }

    /**
     Generates compilable java classes from the annotated classes that have been used in the specification.
     @param classes List of classes obtained from the ee.ioc.cs.editor.synthesize.SpecParser
     * @throws SpecParseException 
     */
    private static void generateSubclasses( ClassList classes, String path ) throws SpecParseException {

    	if( classes == null ) {
    		throw new SpecParseException( "Empty Class list!!!" );
    	}
    	
        AnnotatedClass pClass;
        String fileString;
        Pattern pattern;
        Matcher matcher;

        // for each class generate new one used in synthesis
        for ( int h = 0; h < classes.size(); h++ ) {
            pClass = ( AnnotatedClass ) classes.get( h );
            if ( !pClass.getName().equals( TYPE_THIS ) ) {
                fileString = "";
                try {
                	fileString = SpecParser.getStringFromFile( path + pClass.getName() + ".java" );
                	
                } catch ( IOException io ) {
                    db.p( io );
                }
                // find the class declaration
                pattern = Pattern.compile( "class[ \t\n]+" + pClass.getName() 
                		+ "|" + "public class[ \t\n]+" + pClass.getName());
                matcher = pattern.matcher( fileString );

                // be sure class is public
                if ( matcher.find() ) {
                    fileString = matcher.replaceAll( "public class " + pClass.getName() );
                }
                
                String declars = "";

                for ( ClassField field : pClass.getClassFields() ) {
                	//TODO - remove?
//                	if( AnnotatedClass.SPEC_OBJECT_NAME.equals( field.getName() ) && pClass.getSuperClasses().size() > 0 )
//                		continue;//do not understand why should we skip adding SPEC_OBJECT_NAME if class has superclasses 
                	declars += CodeGenerator.OT_TAB + TypeUtil.getDeclaration( field, "public" );
				}

                // find spec
                pattern = Pattern.compile(RE_SPEC, Pattern.DOTALL);
                matcher = pattern.matcher( fileString );
                if ( matcher.find() ) {
                    fileString = matcher.replaceAll( "\n" + declars );
                } else {
                	throw new SpecParseException( "Unable to parse " + pClass.getName() + " specification" );
                }

                fileString = "import ee.ioc.cs.vsle.api.*;\n\n" + fileString;
               
                FileFuncs.writeFile( fileString, pClass.getName(), "java", RuntimeProperties.genFileDir, false );
            }
        }
    }

    /**
     * @param fileName -
     */
    public static void parseFromCommandLine( String path, String fileName ) {
        try {
            
            String file = SpecParser.getStringFromFile( path + fileName );

            String mainClassName = SpecParser.getClassName( file );
            
            ClassList classList = SpecParser.parseSpecification( path, mainClassName, null, file );
            String prog = makeProgramText( file, true, classList, mainClassName, null ); //changed to true

            makeProgram( prog, classList, mainClassName, path );
        } catch ( UnknownVariableException uve ) {
            db.p( "Fatal error: variable " + uve.excDesc + " not declared" );
        } catch ( LineErrorException lee ) {
            db.p( "Fatal error on line " + lee.excDesc );
        } catch ( MutualDeclarationException lee ) {
            db.p( "Mutual recursion in specifications, between classes " + lee.excDesc );
        } catch ( EquationException ee ) {
            db.p( ee.excDesc );
        } catch ( SpecParseException spe ) {
            db.p( spe );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }
   
    public static String getRunMethodSignature() {
        return "public Object[] run(Object[] in) throws Exception";
    }
    
    public static String getComputeMethodSignature() {
    	return "public void compute( Object... args )";
    }   
}
