package ee.ioc.cs.vsle.synthesize;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ee.ioc.cs.vsle.api.ComputeModelException;
import ee.ioc.cs.vsle.editor.ProgramRunner;
import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.parser.ParsedSpecificationContext;
import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.util.FileFuncs.FileSystemStorage;
import ee.ioc.cs.vsle.util.FileFuncs.GenStorage;
import ee.ioc.cs.vsle.util.TypeUtil;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 This class is responsible for managing the planning and code generation process.
 @author Ando Saabas
 */
public class Synthesizer {

    private static final Logger logger = LoggerFactory.getLogger(Synthesizer.class);

	public static final String RE_SPEC = "/\\*@.*specification[ \t\n]+" +
			"[a-zA-Z_0-9-.]+[ \t\n]*(super ([ a-zA-Z_0-9-,]+ ))?" +
			"[ \t\n]*\\{[ \t\n]*(.+)[ \t\n]*\\}[ \t\n]*@\\*/ *";

    private Synthesizer() {
        // should not be instanciated
    }

    /**
    This method makes a compilable class from problem specification, calling createProblem,
    planner, generates needed classes(_Class_ notation), putting it all together and writing into
    a file.
     * @throws SpecParseException 
    */
    public static void makeProgram( String progText, ClassList classes, String mainClassName, String path, GenStorage storage ) throws SpecParseException {
        generateSubclasses( mainClassName, classes, path, storage );
        storage.writeFile( mainClassName + ".java", progText );
    }

    /** Takes care of steps needed for planning and algorithm extracting, calling problem creator
     * and planner and	returning compilable java source.
     * Creating a problem means parsing the specification(s recursively), unfolding it, and making
     * a flat representation of the specification (essentially a graph). Planning is run on this graph.
     * @param context -
     * @param computeAll -
     * @return String -
     * @throws SpecParseException -
     */
    public static String makeProgramText( ParsedSpecificationContext context, boolean computeAll, ProgramRunner runner ) throws SpecParseException {

        // call the Problem Creator to create a problem from the specification
        Problem problem = new ProblemCreator(context.classList, context.mainClassName).makeProblem();
            
        // run the planner on the obtained problem
        EvaluationAlgorithm algorithm = PlannerFactory.getInstance().getCurrentPlanner().invokePlaning( problem, computeAll );
        
        if( RuntimeProperties.isShowAlgorithm() ) {
        	AlgorithmVisualizer.getInstance().addNewTab( context.mainClassName, algorithm );
        }
        
        CodeGenerator cg = new CodeGenerator( algorithm, problem, context.mainClassName );
        String algorithmCode = cg.generate();

        if (runner != null) {
            runner.addFoundVars( problem.getCurrentContext().getFoundVars() );
            runner.setAssumptions( problem.getAssumptions() );
        }
        
        StringBuilder prog = new StringBuilder();
        
        // start building the main source file.
        AnnotatedClass ac = context.classList.getType( context.mainClassName );

        // check all the fields and make declarations accordingly
        for ( ClassField field : ac.getClassFields() ) {

            String dec = TypeUtil.getDeclaration( field, "public" );
           
        	if( dec != null && dec.length() > 0 ) {
                prog.append( CodeGenerator.OT_TAB ).append( dec );
        	}
            
        }
       
        prog.append( "\n" ).append(  CodeGenerator.OT_TAB )
            .append( CodeGenerator.getComputeMethodSignature( CodeGenerator.COMPUTE_ARG_NAME) )
            .append( " {\n" );
        prog.append( algorithmCode );
        prog.append( CodeGenerator.OT_TAB ).append( "}\n\n" );
        
        prog.append( CodeGenerator.OT_TAB ).append( "public static void main( String[] args ) {\n" )
                .append( CodeGenerator.OT_TAB ).append( CodeGenerator.OT_TAB )
                .append( "new " ).append( context.mainClassName ).append( "().compute();\n" )
                .append( CodeGenerator.OT_TAB ).append( "}\n" );
        
        Pattern pattern;
        Matcher matcher;

        pattern = Pattern.compile("(?:public[ \t\n]+)?class[ \t\n]+" 
        		+ context.mainClassName + "(?:[ \t\n]+extends[ \t\n]+([A-Za-z]\\w*))?");

        String fileString = context.fullRootSpec;
        matcher = pattern.matcher( fileString );

        if ( matcher.find() ) {
            fileString = matcher.replaceAll("public class " + context.mainClassName
            		+ (matcher.start(1) < matcher.end(1) 
            				? " extends " + matcher.group(1) : "")
            		+ " implements " + CodeGenerator.GENERATED_INTERFACE_NAME);
        }

        pattern = Pattern.compile(RE_SPEC, Pattern.DOTALL);
        matcher = pattern.matcher( fileString );

        StringBuilder fsb = new StringBuilder(fileString);
        
        if ( matcher.find() ) {
            fsb.insert( matcher.start(), "\n" ).replace( matcher.start()+1, matcher.end()+1, Matcher.quoteReplacement( prog.toString() ) );
        }

        fsb.insert( 0, "import ee.ioc.cs.vsle.util.*;\nimport ee.ioc.cs.vsle.api.*;\n\n" )
                .append( cg.getIndependentSubtasks() );
        
        return fsb.toString();
    }

    /**
     Generates compilable java classes from the annotated classes that have been used in the specification.
     @throws SpecParseException
      * @param mainClassName
     * @param classes List of classes obtained from the ee.ioc.cs.editor.synthesize.SpecParser
     */
    private static void generateSubclasses(String mainClassName, ClassList classes, String path, GenStorage storage) throws SpecParseException {

    	if( classes == null ) {
    		throw new SpecParseException( "Empty Class list!!!" );
    	}
    	
        String fileString;
        Pattern pattern;
        Matcher matcher;

        // for each class generate new one used in synthesis
        for ( AnnotatedClass pClass : classes ) {
            
            if ( !pClass.getName().equals( mainClassName ) ) {
                
                fileString = FileFuncs.getFileContents(
                        new File(path, pClass.getName() + ".java"));

                    // find the class declaration
                pattern = Pattern.compile( "class[ \t\n]+" + pClass.getName() 
                		+ "|" + "public class[ \t\n]+" + pClass.getName());
                matcher = pattern.matcher( fileString );

                StringBuilder fsb = new StringBuilder( fileString );
                // be sure class is public
                if ( matcher.find() ) {
                    fsb.replace( matcher.start(), matcher.end(), "public class " + pClass.getName() );
                }
                
                fsb.insert( 0, "import ee.ioc.cs.vsle.api.*;\n\n" );
                
                StringBuilder declars = new StringBuilder();

                for ( ClassField field : pClass.getClassFields() ) {
                    declars.append( CodeGenerator.OT_TAB ).append( TypeUtil.getDeclaration( field, "public" ) );
				}

                // find spec
                pattern = Pattern.compile(RE_SPEC, Pattern.DOTALL);
                matcher = pattern.matcher( fsb.toString() );
                if ( matcher.find() ) {
                    int matcherStart = matcher.start();
                    fsb.insert( matcherStart++, "\n" ).delete( matcherStart, matcher.end()+1 ).insert( matcherStart, declars );
                } else {
                	throw new SpecParseException( "Unable to parse " + pClass.getName() + " specification" );
                }

                storage.writeFile( pClass.getName() + ".java", fsb.toString() );
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
                + contextClassName.toLowerCase(), contextClassName, true );
        //create subtask class relation
        SubtaskClassRelation subtaskCR = SubtaskClassRelation
                .createIndependentSubtask( contextClassName + " |- "
                        + Arrays.toString( inputs ) + " -> "
                        + Arrays.toString( outputs ), contextCF );
        Collection<ClassField> varsForSubtask = classList.getType(
                contextClassName ).getFields();
        subtaskCR.addInputs( inputs, varsForSubtask );
        subtaskCR.addOutputs( outputs, varsForSubtask );
        subtask = new ProblemCreator(classList, null).makeIndependentSubtask( subtaskCR );
        //get problem graph
        Problem problemContext = subtask.getContext();
        //construct an algorithm
        EvaluationAlgorithm alg = PlannerFactory.getInstance().getCurrentPlanner()
                .invokePlaning( problemContext, false );
        boolean solved = problemContext.getCurrentContext().getFoundVars()
                .containsAll( problemContext.getCurrentContext().getAllGoals() );
        if ( !solved ) {
            throw new ComputeModelException( contextClassName
                    + " problem is not solvable!" );
        }
        //generate code
        StringBuilder classCode = new StringBuilder();
        String className = CodeGenerator.genIndependentSubtask( subtask, alg,
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
            
            ClassList classList = new SpecParser(path).parseSpecification(file, mainClassName, null);
            String prog = makeProgramText( new ParsedSpecificationContext(file, classList, mainClassName), true, null ); //changed to true

            String outputDir = RuntimeProperties.getGenFileDir();
            makeProgram( prog, classList, mainClassName, path, new FileSystemStorage(outputDir));
        } catch ( UnknownVariableException uve ) {
            logger.error("Fatal error: variable " + uve.getMessage() + " not declared");
        } catch ( LineErrorException lee ) {
            logger.error("Fatal error on line " + lee.getMessage());
        } catch ( MutualDeclarationException lee ) {
            logger.error("Mutual recursion in specifications, between classes " + lee.getMessage());
        } catch ( EquationException ee ) {
            logger.error(ee.getMessage());
        } catch ( SpecParseException spe ) {
            logger.error( null, spe );
        } catch ( Exception ex ) {
            ex.printStackTrace();
        }
    }

}
