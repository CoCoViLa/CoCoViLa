package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.db;
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

    /**
     This method makes a compilable class from problem specification, calling createProblem,
     planner, generates needed classes(_Class_ notation), putting it all together and writing into
     a file.

     void makeProgram(ObjectList objects, ee.ioc.cs.editor.vclass.ConnectionList connections, Hashtable classes) {
     objects = ee.ioc.cs.editor.vclass.GroupUnfolder.unfold(objects);
     String prog = makeProgramText(objects, connections, true);
     generateSubclasses(classes);
     writeFile(prog);
     }
     */

    private static Synthesizer s_instance;
    
    private Synthesizer() {}
    
    public static Synthesizer getInstance() {
    	if( s_instance == null ) {
    		s_instance = new Synthesizer();
    	}
    	return s_instance;
    }
    /**
     * @param progText -
     * @param classes -
     * @param mainClassName -
     */
    public void makeProgram( String progText, ClassList classes, String mainClassName ) {
        generateSubclasses( classes );
        writeFile( progText, mainClassName );
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
    public String makeProgramText( String fileString, boolean computeAll, ClassList classList,
                                   String mainClassName, List<Var> assumptions ) throws SpecParseException {

        Problem problem = null;
        // call the packageParser to create a problem from the specification
        try {
            problem = SpecParser.getInstance().makeProblem( classList );
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
        for ( int i = 0; i < ac.fields.size(); i++ ) {

            field = ac.fields.get( i );
            if ( !( field.getType().equals( "alias" ) || field.getType().equals( "void" ) ) ) {
                if ( field.isSpecField() ) {
                    prog += "    public " + field.getType() + " " + field.getName() + " = new " +
                            field.getType() + "();\n";
                } else if ( isPrimitive( field.getType() ) ) {
                    prog += "    public " + field.getType() + " " + field.getName() + ";\n";
                } else if ( isArray( field.getType() ) ) {
                    prog += "    public " + field.getType() + " " + field.getName() + " ;\n";
                } else {
                    prog += "    public " + field.getType() + " " + field.getName() + " = new " +
                            field.getType() + "();\n";
                }
            }
        }

        prog += "    " + getComputeMethodSignature() + " {\n";
        prog += algorithm;
        prog += "    }";
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
            fileString = matcher.replaceAll( "\n    " + prog );
        }

        createGeneratedInterface();
        createSubtaskInterface();

        return fileString;

    }

    /**
     Generates compilable java classes from the annotated classes that have been used in the specification.
     @param classes List of classes obtained from the ee.ioc.cs.editor.synthesize.SpecParser
     */
    private void generateSubclasses( ClassList classes ) {

        AnnotatedClass pClass;
        String lineString = "", fileString;
        Pattern pattern;
        Matcher matcher;

        // for each class generate new one used in synthesis

        for ( int h = 0; h < classes.size(); h++ ) {
            pClass = ( AnnotatedClass ) classes.get( h );
            if ( !pClass.name.equals( "this" ) ) {
                fileString = "";
                try {
                    BufferedReader in = new BufferedReader( new FileReader( RuntimeProperties.
                            packageDir + File.separator + pClass.name + ".java" ) );

                    while ( ( lineString = in.readLine() ) != null ) {
                        fileString += lineString + "\n";
                    }
                    in.close();

                } catch ( IOException io ) {
                    db.p( io );
                }
                // find the class declaration
                pattern = Pattern.compile( "class[ \t\n]+" + pClass.name 
                		+ "|" + "public class[ \t\n]+" + pClass.name);
                matcher = pattern.matcher( fileString );

                // be sure class is public
                if ( matcher.find() ) {
                    fileString = matcher.replaceAll( "public class " + pClass.name );
                }
                SpecParser specParser = SpecParser.getInstance();
                String declars = "";

                try {
                    ArrayList specLines = specParser.getSpec( fileString, false );

                    while ( !specLines.isEmpty() ) {
                        LineType lt = specParser.getLine( specLines );

                        //if (! (specLines.get(0)).equals("")) {
                        if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                            String[] split = lt.getSpecLine().split( ":", -1 );
                            String[] vs = split[ 1 ].trim().split( " *, *", -1 );
                            String type = split[ 0 ].trim();

                            if ( !type.equals( "void" ) ) {
                                for ( int i = 0; i < vs.length; i++ ) {
                                    if ( isPrimitive( type ) ) {
                                        declars += "    public " + type + " " + vs[ i ] + ";\n";
                                    } else if ( isArray( type ) ) {
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
                        "/\\*@.*specification[ \t\n]+[a-zA-Z_0-9-.]+[ \t\n]*\\{[ \t\n]*(.+)[ \t\n]*\\}[ \t\n]*@\\*/ *",
                        Pattern.DOTALL );
                matcher = pattern.matcher( fileString );
                if ( matcher.find() ) {
                    fileString = matcher.replaceAll( "\n    " + declars );
                }

                writeFile( fileString, pClass.name );
                /*try {
                    PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter(
                            RuntimeProperties.genFileDir + System.getProperty( "file.separator" ) +
                            "" + pClass.name + ".java" ) ) );

                    out.println( fileString );
                    out.close();
                } catch ( Exception e ) {
                    db.p( e );
                }*/
            }
        }
    }

    private String getTypeWithoutArray( String type ) {
        return type.substring( 0, type.length() - 2 );
    }

    private void writeFile( String prog, String mainClassName ) {
        try {
            PrintWriter out = new PrintWriter( new BufferedWriter( new FileWriter(
                    RuntimeProperties.genFileDir + System.getProperty( "file.separator" ) +
                    mainClassName + ".java" ) ) );

            out.println( prog );
            out.close();
        } catch ( Exception e ) {
            db.p( e );
        }
    }

    private boolean isPrimitive( String type ) {
        return ( type.equals( "int" ) || type.equals( "double" ) || type.equals( "float" ) ||
             type.equals( "long" ) || type.equals( "short" ) || type.equals( "boolean" ) ||
             type.equals( "char" ) );
    }

    private boolean isArray( String type ) {
        int length = type.length();

        return ( type.length() >= 2 && type.substring( length - 2, length ).equals( "[]" ) );
    }

    /**
     * @param fileName -
     */
    public void parseFromCommandLine( String fileName ) {
        try {
            SpecParser sp = SpecParser.getInstance();
            
            String file = sp.getStringFromFile( RuntimeProperties.packageDir + fileName );

            String mainClassName = sp.getClassName( file );
            
            ClassList classList = sp.parseSpecification( file );
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
