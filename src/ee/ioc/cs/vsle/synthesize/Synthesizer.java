package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.ClassField;
import ee.ioc.cs.vsle.editor.RuntimeProperties;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 This class is responsible for managing the planning and code generation process.
 @author Ando Saabas
 */
public class Synthesizer {

    public static final String GENERATED_INTERFACE_NAME = "IComputable";
    public static final String SUBTASK_INTERFACE_NAME = "Subtask";

    private Synthesizer() {}
      
    /**
    This method makes a compilable class from problem specification, calling createProblem,
    planner, generates needed classes(_Class_ notation), putting it all together and writing into
    a file.
     * @throws SpecParseException 
    */
    public static void makeProgram( String progText, ClassList classes, String mainClassName ) throws SpecParseException {
        generateSubclasses( classes );
        FileFuncs.writeFile( progText, mainClassName, "java", RuntimeProperties.genFileDir );
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
                                   String mainClassName, List<Var> assumptions ) throws SpecParseException {

        Problem problem = null;
        // call the packageParser to create a problem from the specification
        try {
            problem = ProblemCreator.makeProblem( classList );
        } catch ( Exception e ) {
            e.printStackTrace();
            
            problem = new Problem();
        }

        // run the planner on the obtained problem
        ArrayList algorithmList = PlannerFactory.getInstance().getCurrentPlanner().invokePlaning( problem, computeAll );
        String algorithm = CodeGenerator.getInstance().generate( algorithmList, problem.getAssumptions() );

        if( assumptions != null ) {
        	assumptions.addAll( problem.getAssumptions() );
        }
        
        String prog = "";
        
        ClassField field;
        // start building the main source file.
        AnnotatedClass ac = classList.getType( "this" );

        // check all the fields and make declarations accordingly
        for ( int i = 0; i < ac.getFields().size(); i++ ) {

            field = ac.getFields().get( i );
            if ( !( field.getType().equals( "alias" ) || field.getType().equals( "void" ) ) ) {
                if ( field.isSpecField() ) {
                    prog += CodeGenerator.OT_TAB + "public " + field.getType() + " " + field.getName() + " = new " +
                            field.getType() + "();\n";
                } else if ( field.isConstant() ) { 
                	prog += CodeGenerator.OT_TAB + "final public " + field.getType() 
                		 + " " + field.getName() + " = " + field.getValue() + ";\n";
                } else if ( field.isPrimitive() ) {
                    prog += CodeGenerator.OT_TAB + "public " + field.getType() + " " + field.getName() + ";\n";
                } else if ( field.isArray() ) {
                    prog += CodeGenerator.OT_TAB + "public " + field.getType() + " " + field.getName() + ";\n";
                } else {
                    prog += CodeGenerator.OT_TAB + "public " + field.getType() + " " + field.getName() + " = new " +
                            field.getType() + "();\n";
                }
            }
        }

        prog += CodeGenerator.OT_TAB + getComputeMethodSignature() + " {\n";
        prog += algorithm;
        prog += CodeGenerator.OT_TAB + "}";
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile( "class[ \t\n]+" + mainClassName + "|" + "public class[ \t\n]+" +
                                   mainClassName );
        matcher = pattern.matcher( fileString );

        if ( matcher.find() ) {
            fileString = matcher.replaceAll( "public class " + mainClassName + " implements " +
                                             GENERATED_INTERFACE_NAME );
        }

        pattern = Pattern.compile(
                "/\\*@.*specification[ \t\n]+[a-zA-Z_0-9-.]+[ \t\n]*\\{[ \t\n]*(.+)[ \t\n]*\\}[ \t\n]*@\\*/ *",
                Pattern.DOTALL );
        matcher = pattern.matcher( fileString );

        if ( matcher.find() ) {
            fileString = matcher.replaceAll( "\n" + prog );
        }

        createGeneratedInterface();
        createSubtaskInterface();

        CodeGenerator.reset();
        
        return fileString;

    }

    /**
     Generates compilable java classes from the annotated classes that have been used in the specification.
     @param classes List of classes obtained from the ee.ioc.cs.editor.synthesize.SpecParser
     * @throws SpecParseException 
     */
    private static void generateSubclasses( ClassList classes ) throws SpecParseException {

        AnnotatedClass pClass;
        String fileString;
        Pattern pattern;
        Matcher matcher;

        HashSet<String> generated = new HashSet<String>();
        // for each class generate new one used in synthesis

        for ( int h = 0; h < classes.size(); h++ ) {
            pClass = ( AnnotatedClass ) classes.get( h );
            if ( !pClass.getName().equals( "this" ) && !generated.contains( pClass.getName() ) ) {
            	generated.add( pClass.getName() );
                fileString = "";
                try {
                	fileString = SpecParser.getStringFromFile( RuntimeProperties.
                            packageDir + File.separator + pClass.getName() + ".java" );
                	
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

                try {
                    ArrayList<String> specLines = SpecParser.getSpec( fileString, false );

                    while ( !specLines.isEmpty() ) {
                        LineType lt = SpecParser.getLine( specLines );

                        //if (! (specLines.get(0)).equals("")) {
                        if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                            String[] split = lt.getSpecLine().split( ":", -1 );
                            String[] vs = split[ 1 ].trim().split( " *, *", -1 );
                            String type = split[ 0 ].trim();

                            if ( !type.equals( "void" ) ) {
                                for ( int i = 0; i < vs.length; i++ ) {
                                    if ( TypeUtil.isPrimitive( type ) ) {
                                        declars += "    public " + type + " " + vs[ i ] + ";\n";
                                    } else if ( TypeUtil.isArray( type ) ) {
                                        declars += "    public " + type + " " + vs[ i ] + " ;\n";
                                    } else if ( classes.getType( type ) != null ) {
                                        declars += "    public " + type + " " + vs[ i ] +
                                                " = new " + type + "();\n";

                                    } else {
                                        declars += "    public " + type + " " + vs[ i ] + " = new " +
                                                type + "();\n";

                                    }

                                }
                            }
                        }
                        //}
                    }
                } catch ( Exception e ) {
                }

                // find spec
                pattern = Pattern.compile(
                        "/\\*@.*specification[ \t\n]+[a-zA-Z_0-9-.]+[ \t\n]*(super ([ a-zA-Z_0-9-,]+ ))?[ \t\n]*\\{[ \t\n]*(.+)[ \t\n]*\\}[ \t\n]*@\\*/ *",
                        Pattern.DOTALL );
                matcher = pattern.matcher( fileString );
                if ( matcher.find() ) {
                    fileString = matcher.replaceAll( "\n" + declars );
                } else {
                	throw new SpecParseException( "Unable to parse " + pClass.getName() + " specification" );
                }

                FileFuncs.writeFile( fileString, pClass.getName(), "java", RuntimeProperties.genFileDir );
            }
        }
    }

    /**
     * @param fileName -
     */
    public static void parseFromCommandLine( String fileName ) {
        try {
            
            String file = SpecParser.getStringFromFile( RuntimeProperties.packageDir + fileName );

            String mainClassName = SpecParser.getClassName( file );
            
            ClassList classList = SpecParser.parseSpecification( file );
            String prog = makeProgramText( file, true, classList, mainClassName, null ); //changed to true

            makeProgram( prog, classList, mainClassName );
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

    private static void createSubtaskInterface() {
        File file = new File( RuntimeProperties.genFileDir
                              + System.getProperty( "file.separator" )
                              + SUBTASK_INTERFACE_NAME + ".java" );

        if ( !file.exists() ) {
            try {
                FileWriter fw = new FileWriter( file );
                String in = "public interface Subtask {\n"
                            + "\tObject[] run(Object[] in) throws Exception;\n}\n";
                fw.write( in );
                fw.close();
            } catch ( IOException ex ) {
                System.err.println( "Unable to create " + file.getAbsolutePath() );
            }
        }
    }

    private static void createGeneratedInterface() {
        File file = new File( RuntimeProperties.genFileDir
                              + System.getProperty( "file.separator" )
                              + GENERATED_INTERFACE_NAME + ".java" );

        if ( !file.exists() ) {
            try {
                FileWriter fw = new FileWriter( file );
                String in = "public interface IComputable {\n"
                            + "\t" + getComputeMethodSignature() + ";\n}\n";
                fw.write( in );
                fw.close();
            } catch ( IOException ex ) {
                System.err.println( "Unable to create " + file.getAbsolutePath() );
            }
        }
    }
    
    private static String getComputeMethodSignature() {
    	return "public void compute( Object... args )";
    }
}
