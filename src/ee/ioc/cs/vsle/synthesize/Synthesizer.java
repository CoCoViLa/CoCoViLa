package ee.ioc.cs.vsle.synthesize;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import ee.ioc.cs.vsle.api.*;
import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.util.FileFuncs.*;
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

    private Synthesizer() {}
      
    /**
    This method makes a compilable class from problem specification, calling createProblem,
    planner, generates needed classes(_Class_ notation), putting it all together and writing into
    a file.
     * @throws SpecParseException 
    */
    public static void makeProgram( String progText, ClassList classes, String mainClassName, String path, GenStorage storage ) throws SpecParseException {
        generateSubclasses( classes, path, storage );
        storage.writeFile( mainClassName + ".java", progText.getBytes() );
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

        problem = new ProblemCreator(classList).makeProblem();
            
        // run the planner on the obtained problem
        ArrayList<Rel> algorithmList = PlannerFactory.getInstance().getCurrentPlanner().invokePlaning( problem, computeAll );
        if( RuntimeProperties.isShowAlgorithm() ) {
        	AlgorithmVisualizer.getInstance().addNewTab( mainClassName, algorithmList );
        }
        CodeGenerator cg = new CodeGenerator( algorithmList, problem, mainClassName );
        String algorithm = cg.generate();

        if (runner != null) {
            runner.addFoundVars( problem.getFoundVars() );
            runner.setAssumptions( problem.getAssumptions() );
        }
        
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
       
        prog.append( "\n" ).append(  CodeGenerator.OT_TAB )
            .append( CodeGenerator.getComputeMethodSignature( CodeGenerator.COMPUTE_ARG_NAME) )
            .append( " {\n" );
        prog.append( algorithm );
        prog.append( CodeGenerator.OT_TAB ).append( "}\n\n" );
        
        prog.append( CodeGenerator.OT_TAB ).append( "public static void main( String[] args ) {\n" )
                .append( CodeGenerator.OT_TAB ).append( CodeGenerator.OT_TAB )
                .append( "new " ).append( mainClassName ).append( "().compute();\n" )
                .append( CodeGenerator.OT_TAB ).append( "}\n" );
        
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("(?:public[ \t\n]+)?class[ \t\n]+" 
        		+ mainClassName + "(?:[ \t\n]+extends[ \t\n]+([A-Za-z]\\w*))?");
        
        matcher = pattern.matcher( fileString );

        if ( matcher.find() ) {
            fileString = matcher.replaceAll("public class " + mainClassName
            		+ (matcher.start(1) < matcher.end(1) 
            				? " extends " + matcher.group(1) : "")
            		+ " implements " + CodeGenerator.GENERATED_INTERFACE_NAME);
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
    private static void generateSubclasses( ClassList classes, String path, GenStorage storage ) throws SpecParseException {

    	if( classes == null ) {
    		throw new SpecParseException( "Empty Class list!!!" );
    	}
    	
        String fileString;
        Pattern pattern;
        Matcher matcher;

        // for each class generate new one used in synthesis
        for ( AnnotatedClass pClass : classes ) {
            
            if ( !pClass.getName().equals( TYPE_THIS ) ) {
                
                fileString = FileFuncs.getFileContents(
                        new File(path, pClass.getName() + ".java"));

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

                storage.writeFile( pClass.getName() + ".java", fileString.getBytes() );
            }
        }
    }

    /**
     * Parses and tries to solve a given computational problem
     * 
     * @param contextClassName
     * @param path
     * @param inputs
     * @param outputs
     * @param classList
     * @param result
     * @return
     * @throws IOException
     * @throws SpecParseException
     */
    public static String computeIndependentModel( String contextClassName,
            String path, String[] inputs, String[] outputs,
            ClassList classList, StringBuilder result ) throws IOException,
            SpecParseException {
        
        //create an instance of current subtask relation
        SubtaskRel subtask;
        //parse context specification
        SpecParser.parseSpecClass( contextClassName, path, classList );
        //create context classfield
        ClassField contextCF = new ClassField( "_"
                + contextClassName.toLowerCase(), contextClassName );
        //create subtask class relation
        SubtaskClassRelation subtaskCR = SubtaskClassRelation
                .createIndependentSubtask( contextClassName + " |- "
                        + Arrays.toString( inputs ) + " -> "
                        + Arrays.toString( outputs ), contextCF );
        Collection<ClassField> varsForSubtask = classList.getType(
                contextClassName ).getFields();
        subtaskCR.addInputs( inputs, varsForSubtask );
        subtaskCR.addOutputs( outputs, varsForSubtask );
        subtask = new ProblemCreator(classList).makeIndependentSubtask( subtaskCR );
        //get problem graph
        Problem context = subtask.getContext();
        //construct an algorithm
        ArrayList<Rel> alg = PlannerFactory.getInstance().getCurrentPlanner()
                .invokePlaning( context, false );
        boolean solved = context.getFoundVars()
                .containsAll( context.getGoals() );
        if ( solved ) {
            subtask.getAlgorithm().addAll( alg );
        } else {
            throw new ComputeModelException( contextClassName
                    + " problem is not solvable!" );
        }
        //generate code
        StringBuilder classCode = new StringBuilder();
        String className = CodeGenerator.genIndependentSubtask( subtask,
                classCode );
        result.append( "import ee.ioc.cs.vsle.util.*;\n" ).append(
                "import ee.ioc.cs.vsle.api.*;\n\npublic " ).append( classCode );

        return className;
    }
    
    /**
     * @param fileName -
     */
    public static void parseFromCommandLine( String path, String fileName ) {
        try {
            
            String file = FileFuncs.getFileContents(new File(path, fileName));

            String mainClassName = SpecParser.getClassName( file );
            
            ClassList classList = SpecParser.parseSpecification(file, mainClassName, null, path);
            String prog = makeProgramText( file, true, classList, mainClassName, null ); //changed to true

            String outputDir = RuntimeProperties.getGenFileDir();
            makeProgram( prog, classList, mainClassName, path,
                    new FileSystemStorage(outputDir));
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

}
