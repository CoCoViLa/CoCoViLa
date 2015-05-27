package ee.ioc.cs.vsle.synthesize;

import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_ANY;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_DOUBLE;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_INT;
import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_THIS;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.equations.EquationSolver;
import ee.ioc.cs.vsle.equations.EquationSolver.Relation;
import ee.ioc.cs.vsle.parser.SpecificationSourceProvider;
import ee.ioc.cs.vsle.table.Table;
import ee.ioc.cs.vsle.util.FileFuncs;
import ee.ioc.cs.vsle.util.TypeToken;
import ee.ioc.cs.vsle.util.TypeUtil;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.AliasLength;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class takes care of parsing the specification and translating it into a
 * graph on which planning can be run.
 * 
 * @author Ando Saabas, Pavel Grigorenko
 */
public class SpecParser {

    private static final Logger logger = LoggerFactory.getLogger(SpecParser.class);

    private static final Pattern PATTERN_SPEC = Pattern.compile(
                    ".*/\\*@.*specification [a-zA-Z_0-9-.]+ ?(super ([ a-zA-Z_0-9-,]+ ))? ?\\{ ?(.+) ?\\} ?@\\*/ ?" );
    private static final Pattern PATTERN_WHITESPACE = Pattern.compile( "[ \r\t\n]+" );
    private static final Pattern PATTERN_DECLARATION = Pattern
                        .compile( "^ *(static)? *([a-zA-Z_][0-9a-zA-Z_]*(([\\.][a-zA-Z_][0-9a-zA-Z_]*)*)[0-9a-zA-Z_$]*(\\[\\])*) (([a-zA-Z_$][0-9a-zA-Z_$]* ?, ?)* ?[a-zA-Z_$][0-9a-zA-Z_$]* ?$)"   );
    private static final Pattern PATTERN_AXIOM_SPEC = Pattern.compile( "(.*) *-> *([ -_a-zA-Z0-9.,]+) *$" );
    private static final Pattern PATTERN_AXIOM_SUBTASK = Pattern.compile( "\\[ *(([a-zA-Z_$][0-9a-zA-Z_$]*) *\\|-)? *(.*) *-> ?(.*)\\]" );
    private static final Pattern PATTERN_AXIOM_SUBTASKS = Pattern.compile( "\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]" );
    private static final Pattern PATTERN_AXIOM_FULL = Pattern.compile( "(.*) *-> *(.+) *\\{(.+)\\}" );
    private static final Pattern PATTERN_EQUATION = Pattern.compile( " *([^=]+) *= *([-_0-9a-zA-Z.()\\+\\*/^ ]+) *$" );
    private static final Pattern PATTERN_ASSIGNMENT = Pattern.compile( " *([^= ]+) *= *((\".*\")|(new .*\\(.*\\))|(\\{.*\\})|(true)|(false)) *$" );
    private static final Pattern PATTERN_CONSTANT = Pattern
                        .compile( " *([a-zA-Z_$][0-9a-zA-Z_$]*[\\[\\]]*) +([a-zA-Z_$][0-9a-zA-Z_$]*) *= *([a-zA-Z0-9.{}\"]+|new [a-zA-Z0-9.{}\\[\\]]+) *" );
    private static final Pattern PATTERN_SUPERCLASSES = Pattern.compile( "super#([^ .]+)" );
    private static final Pattern PATTERN_ALIAS_VARS = Pattern.compile( " *([^= ]+) *= *\\[(.*)\\] *$" );
    private static final Pattern PATTERN_ALIAS_DECLARATION = Pattern.compile( "alias *(\\(( *[^\\(\\) ]+ *)\\))* *([^= ]+) *" );
    private static final Pattern PATTERN_ALIAS_FULL = Pattern.compile( "alias *(\\(( *[^\\(\\) ]+ *)\\))* *([^= ]+) *= *\\((.*)\\) *" );

    private final SpecificationSourceProvider<String> specSourceProvider;
    private final String packagePath;

    private String rootClassName;

    public SpecParser(String packagePath) {
        this.packagePath = packagePath;
        specSourceProvider = new FileSourceProvider();
    }

    public SpecParser(String packagePath, SpecificationSourceProvider specSourceProvider) {
        this.specSourceProvider = specSourceProvider;
        this.packagePath = packagePath;
    }

    public static String getClassName( String spec ) {
        Pattern pattern = Pattern.compile( "class[ \t\n]+([a-zA-Z_0-9-]+)[ \t\n]+" );
        Matcher matcher = pattern.matcher( spec );

        if ( matcher.find() ) {
            return matcher.group( 1 );
        }

        return "";
    }

    /**
     * @return ArrayList of lines in specification
     * @param text Secification text as String
     * @throws SpecParseException 
     */
    static ArrayList<String> getSpec( String text, boolean isRefinedSpec ) {
        if ( !isRefinedSpec ) {
            text = refineSpec( text );
        }
        String[] s = text.trim().split( ";", 0 );
        ArrayList<String> a = new ArrayList<String>();

        for ( int i = 0; i < s.length; i++ ) {
            a.add( s[ i ].trim() );
        }
        return a;

    }

    /**
     * Reads a line from an arraylist of specification lines, removes it from
     * the arraylist and returns the line together with its type information
     * 
     * @return a specification line with its type information
     * @param a arraylist of specification lines
     */
    static LineType getLine( ArrayList<String> a ) throws SpecParseException {
        Matcher matcher;

        while ( ( a.get( 0 ) ).equals( "" ) || a.get( 0 ).trim().startsWith( "//" ) ) {
            a.remove( 0 );
            if ( a.isEmpty() ) {
                return null;
            }
        }
        final String line = a.get( 0 );

        a.remove( 0 );
        if ( line.startsWith( "alias " ) ) {
            matcher = PATTERN_ALIAS_FULL.matcher( line );
            if ( matcher.find() ) {
                if ( matcher.group( 3 ).indexOf( "." ) > -1 ) {
                    throw new SpecParseException( "Alias " + matcher.group( 3 ) + " cannot be declared with compound name" );
                }
                
                LineType.Alias st = new LineType.Alias();
                st.setName( matcher.group( 3 ) );
                String vars = matcher.group( 4 ).trim();
                st.setComponents( vars.length() == 0 ? new String[0] : vars.split( " *, *", -1 ) );
                st.setComponentType( ( matcher.group( 2 ) == null || matcher.group( 2 ).equals( "null" ) ? "" : matcher.group( 2 ).trim() ) );
                
                return new LineType( LineType.TYPE_ALIAS, st, line );
            }
            // allow empty alias declaration e.g. "alias x;"
            matcher = PATTERN_ALIAS_DECLARATION.matcher( line );
            if ( matcher.find() ) {
                if ( matcher.group( 3 ).indexOf( "." ) > -1 ) {
                    throw new SpecParseException( "Alias " + matcher.group( 3 ) + " cannot be declared with compound name" );
                }
                
                LineType.Alias st = new LineType.Alias();
                st.setName( matcher.group( 3 ) );
                st.setDeclaration( true );
                st.setComponentType( ( matcher.group( 2 ) == null || matcher.group( 2 ).equals( "null" ) ? "" : matcher.group( 2 ).trim() ) );
                return new LineType( LineType.TYPE_ALIAS, st, line );
            }

            return new LineType( LineType.TYPE_ERROR, null, line );

        } else if ( line.indexOf( "super" ) >= 0 && (matcher = PATTERN_SUPERCLASSES.matcher( line )).find() ) {
            LineType.Superclasses st = new LineType.Superclasses();
            st.setClassNames( matcher.group( 1 ).split( "#", -1 ) );
            return new LineType( LineType.TYPE_SUPERCLASSES, st, line );

        } else if ( line.trim().startsWith( "const" ) ) {
            matcher = PATTERN_CONSTANT.matcher( line );
            if ( matcher.find() ) {
                LineType.Constant st = new LineType.Constant();
                st.setName( matcher.group( 2 ).trim() );
                st.setType( matcher.group( 1 ).trim() );
                st.setValue( matcher.group( 3 ).trim() );
                return new LineType( LineType.TYPE_CONST, st, line );
            }
            return new LineType( LineType.TYPE_ERROR, null, line );

        } else if ( line.indexOf( "=" ) >= 0 ) { // Extract on solve
                                                    // equations
            // lets check if it's an alias, e.g. x = [a,b,c];
            matcher = PATTERN_ALIAS_VARS.matcher( line );
            if ( matcher.find() ) {
                LineType.Alias st = new LineType.Alias();
                st.setName( matcher.group( 1 ) );
                String vars = matcher.group( 2 ).trim();
                st.setComponents( vars.length() == 0 ? new String[0] : vars.split( " *, *", -1 ) );
                st.setAssignment( true );
                
                return new LineType( LineType.TYPE_ALIAS, st, line );
            }

            matcher = PATTERN_ASSIGNMENT.matcher( line );
            if ( matcher.find() ) {
                LineType.Assignment st = new LineType.Assignment();
                st.setName( matcher.group( 1 ) );
                st.setValue( matcher.group( 2 ) );
                return new LineType( LineType.TYPE_ASSIGNMENT, st, line );
            }
            matcher = PATTERN_EQUATION.matcher( line );
            if ( matcher.find() ) {
                LineType.Equation st = new LineType.Equation();
                st.setEq( line );
                return new LineType( LineType.TYPE_EQUATION, st, line );
            }
            return new LineType( LineType.TYPE_ERROR, null, line );

        } else if ( line.indexOf( "->" ) >= 0 ) {
            //axiom
            matcher = PATTERN_AXIOM_FULL.matcher( line );
            if ( matcher.find() ) {
                LineType.Axiom st = new LineType.Axiom();
                
                //check for subtasks
                //FIXME - this pattern allows subtasks to be anywhere in the axiom, but they need to come before anything else
                Matcher subMatcher = PATTERN_AXIOM_SUBTASKS.matcher( line );

                while ( subMatcher.find() ) {
                    String subtaskString = subMatcher.group( 0 );
                    
                    Matcher singleSubtaskMatcher = PATTERN_AXIOM_SUBTASK.matcher( subtaskString );
                    
                    if( singleSubtaskMatcher.find() ) {
                        String context = singleSubtaskMatcher.group( 2 );
                        String[] subInputs = singleSubtaskMatcher.group( 3 ).trim().split( " *, *", -1 );
                        String[] subOutputs = singleSubtaskMatcher.group( 4 ).trim().split( " *, *", -1 );
                        
                        st.getSubtasks().put( subtaskString, new String[][] { new String[] { context }, subInputs, subOutputs } );
                    } else {
                        return new LineType( LineType.TYPE_ERROR, null, line );
                    }
                }
                
                String newLine = line.replaceAll( "\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]", "#" );

                matcher = PATTERN_AXIOM_FULL.matcher( newLine );
                
                if ( matcher.find() ) {
                    String in = matcher.group( 1 ).trim();
                    st.setInputs( in.length() == 0 ? new String[0] : in.split( " *, *", -1 ) );
                    String out = matcher.group( 2 ).trim();
                    st.setOutputs( out.length() == 0 ? new String[0] : out.split( " *, *", -1 ) );
                    st.setMethod( matcher.group( 3 ).trim() );
                } else {
                    return new LineType( LineType.TYPE_ERROR, null, line );
                }
                
                return new LineType( LineType.TYPE_AXIOM, st, line );
            }
            //specaxiom
            matcher = PATTERN_AXIOM_SPEC.matcher( line );
            
            if ( matcher.find() ) {
                LineType.Axiom st = new LineType.Axiom();
                st.setSpecAxiom( true );
                String in = matcher.group( 1 ).trim();
                st.setInputs( in.length() == 0 ? new String[0] : in.split( " *, *", -1 ) );
                String out = matcher.group( 2 ).trim();
                st.setOutputs( out.length() == 0 ? new String[0] : out.split( " *, *", -1 ) );
                return new LineType( LineType.TYPE_SPECAXIOM, st, line );

            }
            return new LineType( LineType.TYPE_ERROR, null, line );
        } else {
            matcher = PATTERN_DECLARATION.matcher( line );
            if ( matcher.find() ) {
                LineType.Declaration st = new LineType.Declaration();
                st.setStatic( ( matcher.group( 1 ) != null ) );
                st.setType( matcher.group( 2 ).trim() );
                st.setNames( matcher.group( 6 ).trim().split( " *, *", -1 ) );
                return new LineType( LineType.TYPE_DECLARATION, st, line );
            }
            return new LineType( LineType.TYPE_ERROR, null, line );
        }
    }

    /**
     * Extracts the specification from the java file, also removing unnecessary
     * whitespaces
     * 
     * @return specification text
     * @param fileString a (Java) file containing the specification
     * @throws SpecParseException 
     */
    private static String refineSpec( String fileString ) {
        Matcher matcher;

        // remove comments before removing line brake \n
        String[] s = fileString.split( "\n" );
        StringBuilder tmpBuf = new StringBuilder(fileString.length() / 2);
        for ( int i = 0; i < s.length; i++ ) {
            if ( !s[ i ].trim().startsWith( "//" ) ) {
                tmpBuf.append(s[i]);
            }
        }

        // remove unneeded whitespace
        matcher = PATTERN_WHITESPACE.matcher(tmpBuf);
        // This is broken as spaces should not be replaced in,
        // e.g. string literals. Keeping it now for compatibility.
        tmpBuf.replace(0, tmpBuf.length(), matcher.replaceAll(" "));

        // find spec
        matcher = PATTERN_SPEC.matcher(tmpBuf);
        if ( matcher.find() ) {
            StringBuilder sc = new StringBuilder();
            if ( matcher.group( 2 ) != null ) {
                sc.append("super");
                String[] superclasses = matcher.group( 2 ).split( "," );
                for ( int i = 0; i < superclasses.length; i++ ) {
                    String t = superclasses[ i ].trim();
                    if ( t.length() > 0 ) {
                        sc.append("#");
                        sc.append(t);
                    }
                }
                sc.append(";\n");
            }
            return sc.append(matcher.group(3)).toString();
        }
        
        throw new SpecParseException( "Specification parsing error" );
    }

    public ClassList parseSpecification( String fullSpec, String mainClassName, Set<String> schemeObjects ) {

        rootClassName = mainClassName;

        long start = System.currentTimeMillis();
        
        ClassList classes = parseSpecificationImpl( refineSpec( fullSpec ), mainClassName, schemeObjects,
                new LinkedHashSet<String>() );

        logger.info("Specification parsed in: " + (System.currentTimeMillis() - start) + "ms.");
        
        /* ****** SPEC_OBJECT_NAME for scheme spec ? ****** */
        // AnnotatedClass _this = classes.getType( TYPE_THIS );
        //    	
        // String meth = AnnotatedClass.SPEC_OBJECT_NAME + " = " + "\"" +
        // mainClassName + "\"";
        //    	
        // ClassRelation classRelation = new ClassRelation(
        // RelType.TYPE_EQUATION, meth );
        //
        // classRelation.getOutputs().add( _this.getFieldByName(
        // AnnotatedClass.SPEC_OBJECT_NAME ) );
        // classRelation.setMethod( meth );
        // _this.addClassRelation( classRelation );
        /* ****** SPEC_OBJECT_NAME ****** */

        return classes;
    }

    /**
     * A recrusve method that does the actual parsing. It creates a list of
     * annotated classes that carry infomation about the fields and relations in
     * a class specification.
     * 
     * @param spec a specfication to be parsed. If it includes a declaration of
     *                an annotated class, it will be recursively parsed.
     * @param className the name of the class being parsed
     * @param checkedClasses the list of classes that parser has started to
     *                check. Needed to prevent infinite loop in case of mutual
     *                declarations.
     */
    private ClassList parseSpecificationImpl( String spec, String className, Set<String> schemeObjects, Set<String> checkedClasses ) {
        
        AnnotatedClass annClass = new AnnotatedClass( className );

        /* ****** SPEC_OBJECT_NAME ****** */
        ClassField specObjectName = new ClassField( CodeGenerator.SPEC_OBJECT_NAME, "String" );
        annClass.addField( specObjectName );
        /* ****** SPEC_OBJECT_NAME ****** */

        ClassList classList = new ClassList();

        ArrayList<String> specLines = getSpec( spec, true );

        LineType lt = null;
        
        try {

            while ( !specLines.isEmpty() ) {

                if ( ( lt = getLine( specLines ) ) != null ) {

                    if ( RuntimeProperties.isLogDebugEnabled() )
                        logger.info("Parsing: Class " + className + " " + lt);

                    if ( lt.getType() == LineType.TYPE_SUPERCLASSES ) {
                        LineType.Superclasses statement = ( LineType.Superclasses)lt.getStatement();
                        
                        for ( int i = 0; i < statement.getClassNames().length; i++ ) {
                            String name = statement.getClassNames()[ i ];

                            if ( checkSpecClass( className, checkedClasses, classList, name ) ) {

                                AnnotatedClass superClass = classList.getType( name );

                                annClass.addSuperClass( superClass );
                            } else {
                                throw new SpecParseException( "Unable to parse superclass " + name + " of " + className );
                            }

                        }
                    } else if ( lt.getType() == LineType.TYPE_ASSIGNMENT ) {
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, lt.getOrigSpecLine() );
                        LineType.Assignment statement = ( LineType.Assignment)lt.getStatement();
                        classRelation.addOutput( statement.getName(), annClass.getFields() );
                        classRelation.setMethod( statement.getName() + " = " + statement.getValue() );
                        checkAnyType( getVar(statement.getName(), annClass.getFields()), statement.getValue(), annClass, classList );
                        annClass.addClassRelation( classRelation );
                        logger.debug(classRelation.toString());

                    } else if ( lt.getType() == LineType.TYPE_CONST ) {
                        LineType.Constant statement = ( LineType.Constant)lt.getStatement();

                        if ( containsVar( annClass.getFields(), statement.getName() ) ) {
                            throw new SpecParseException( "Variable " + statement.getName() + " declared more than once in class " + className );
                        }

                        if ( isSpecClass( statement.getType() ) ) {
                            throw new SpecParseException( "Constant " + statement.getName() + " cannot be of type " + statement.getType() );
                        }
                        logger.debug( "---===!!! " + statement.getType() + " " + statement.getName() + " = " + statement.getValue() );

                        ClassField var = new ClassField( statement.getName(), statement.getType(), statement.getValue(), true );

                        annClass.addField( var );

                    } else if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                        LineType.Declaration statement = ( LineType.Declaration)lt.getStatement();
                        
                        boolean isStatic = statement.isStatic();

                        boolean specClass = checkSpecClass( className, checkedClasses, classList, statement.getType() );

                        String[] vars = statement.getNames();
                        
                        for ( int i = 0; i < vars.length; i++ ) {
                            if ( containsVar( annClass.getFields(), vars[ i ] ) ) {
                                throw new SpecParseException( "Variable " + vars[ i ] + " declared more than once in class "
                                        + className );
                            }
                            ClassField var = new ClassField( vars[ i ], statement.getType(), specClass );
                            var.setStatic( isStatic );

                            /* ****** SPEC_OBJECT_NAME ****** */
                            // add the following relation only if the object
                            // exists on a given scheme
                            if ( schemeObjects != null && specClass && ( className.equals(rootClassName) )
                                    && schemeObjects.contains( vars[ i ] ) ) {
                                var.setSchemeObject(true);
                                String s = vars[ i ] + "." + CodeGenerator.SPEC_OBJECT_NAME;
                                String meth = s + " = " + "\"" + vars[ i ] + "\"";

                                ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, meth );

                                classRelation.addOutput( s, annClass.getFields() );
                                classRelation.setMethod( meth );
                                annClass.addClassRelation( classRelation );
                            }
                            /* ****** SPEC_OBJECT_NAME ****** */

                            annClass.addField( var );
                        }

                    } else if ( lt.getType() == LineType.TYPE_ALIAS ) {
                        LineType.Alias statement = ( LineType.Alias)lt.getStatement();

                        Alias alias = null;
                        
                        String name = statement.getName();
                        
                        if( statement.isDeclaration() && !containsVar( annClass.getFields(), name ) ) {
                            alias = new Alias( name, statement.getComponentType() );
                            annClass.addField( alias );
                            continue;
                        }

                        ClassField var = getVar( name, annClass.getFields() );

                        if ( var != null && !var.isAlias() ) {
                            throw new SpecParseException( "Variable " + name + " declared more than once in class " + 
                                    className + ", line: " + lt.getOrigSpecLine() );
                        } else if ( var != null && var.isAlias() ) {
                            alias = (Alias) var;
                            if ( alias.isInitialized() ) {
                                throw new SpecParseException( "Alias " + name + " has already been initialized and cannot be overriden, class " + 
                                        className + ", line: " + lt.getOrigSpecLine() );
                            }
                        } else if ( statement.isAssignment() ) {
                            // if its an assignment, check if alias has already
                            // been declared
                            try {
                                if ( ( name.indexOf( "." ) == -1 ) && !containsVar( annClass.getFields(), name ) ) {
                                    throw new UnknownVariableException( "Alias " + name + " not declared", lt.getOrigSpecLine() );

                                } else if ( name.indexOf( "." ) > -1 ) {
                                    // here we have to dig deeply
                                    int ind = name.indexOf( "." );

                                    String parent = name.substring( 0, ind );
                                    String leftFromName = name.substring( ind + 1, name.length() );

                                    ClassField parentVar = getVar( parent, annClass.getFields() );
                                    String parentType = parentVar.getType();

                                    AnnotatedClass parentClass = classList.getType( parentType );

                                    while ( leftFromName.indexOf( "." ) > -1 ) {

                                        ind = leftFromName.indexOf( "." );
                                        parent = leftFromName.substring( 0, ind );
                                        leftFromName = leftFromName.substring( ind + 1, leftFromName.length() );

                                        parentVar = parentClass.getFieldByName( parent );

                                        parentType = parentVar.getType();
                                        parentClass = classList.getType( parentType );
                                    }

                                    if ( !parentClass.hasField( leftFromName ) ) {
                                        throw new UnknownVariableException( "Variable " + leftFromName
                                                + " is not declared in class " + parentClass, lt.getOrigSpecLine() );
                                    }

                                    Alias aliasDeclaration = (Alias) parentClass.getFieldByName( leftFromName );

                                    if( aliasDeclaration.isInitialized() ) {
                                        throw new SpecParseException( "Alias " + aliasDeclaration.getName() + 
                                                " has already been initialized and cannot be overriden, class " + 
                                                className + ", line: " + lt.getOrigSpecLine() );
                                    }
                                    
                                    // if everything is ok, create alias
                                    alias = new Alias( name, aliasDeclaration.getVarType() );

                                }
                            } catch ( Exception e ) {
                                throw new SpecParseException( "Alias " + name + " is not declared, class " + className
                                        + ( e.getMessage() != null ? "\n" + e.getMessage() : "" ) );
                            }
                        } else {
                            //empty declaration is not allowed, e.g. "alias x = ();".
                            // note, however, it is possible to declare empty alias using two lines, e.g. "alias x; x = [];"
                            if(statement.getComponents() == null || statement.getComponents().length == 0) {
                                throw new SpecParseException( "Alias " + name + " does not bind any variables, line: " + lt.getOrigSpecLine() );
                            }
                            alias = new Alias( name, statement.getComponentType() );
                        }

                        String[] vars = statement.getComponents();
                        
                        alias.addAll( vars, annClass.getFields(), classList );
                        if (!containsVar( annClass.getFields(), name )) {
                          annClass.addField(alias);
                        }
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_ALIAS, lt.getOrigSpecLine() );

                        classRelation.addInputs( vars, annClass.getFields() );
                        classRelation.setMethod( TypeUtil.TYPE_ALIAS );
                        classRelation.addOutput( name, annClass.getFields() );
                        annClass.addClassRelation( classRelation );

                        logger.debug( classRelation.toString() );

                        if ( !alias.isWildcard() ) {
                            classRelation = new ClassRelation( RelType.TYPE_ALIAS, lt.getOrigSpecLine() );
                            classRelation.addOutputs( vars, annClass.getFields() );
                            classRelation.setMethod( TypeUtil.TYPE_ALIAS );
                            classRelation.addInput( name, annClass.getFields() );
                            annClass.addClassRelation( classRelation );
                            logger.debug( classRelation.toString() );
                        }

                        alias.setInitialized( true );

                    } else if ( lt.getType() == LineType.TYPE_EQUATION ) {
                        LineType.Equation statement = ( LineType.Equation)lt.getStatement();
                        EquationSolver solver = new EquationSolver();
                        solver.solve( statement.getEq() );
                        next: for ( Relation rel : solver.getRelations() ) {
                            logger.debug( "equation: " + rel );
                            String[] pieces = rel.getRel().split( ":" );
                            String method = rel.getExp();
                            String out = pieces[ 2 ].trim();

                            // cannot assign new values for constants
                            ClassField tmp = getVar( checkAliasLength( out, annClass, className ), annClass.getFields() );
                            if ( tmp != null && ( tmp.isConstant() || tmp.isAliasLength() ) ) {
                                logger.info("Ignoring constant as equation output: " + tmp);
                                continue;
                            }
                            // if one variable is used on both sides of "=", we
                            // cannot use such relation.
                            String[] inputs = pieces[ 1 ].trim().split( " " );
                            for ( int j = 0; j < inputs.length; j++ ) {
                                if ( inputs[ j ].equals( out ) ) {
                                    logger.debug( " - unable use this equation because variable " + out
                                                + " appears on both sides of =" );
                                    continue next;
                                }
                            }

                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, lt.getOrigSpecLine() );

                            ClassField output = getVarWithType( out, annClass, classList );
                            classRelation.addOutput( output );
                            
                            // checkAliasLength( inputs, annClass.getFields(), className );
                            for ( int i = 0; i < inputs.length; i++ ) {
                                String initial = inputs[ i ];
                                inputs[ i ] = checkAliasLength( inputs[ i ], annClass, className );
                                String name = inputs[ i ];
                                if ( name.startsWith( "*" ) ) {
                                    name = inputs[ i ].substring( 1 );
                                }
                                method = method.replaceAll( "\\$" + initial + "\\$", name );
                            }
                            method = method.replaceAll( "\\$" + out + "\\$", out );

                            checkAnyType( output, inputs, annClass, classList );

                            if ( !inputs[ 0 ].equals( "" ) ) {
                                classRelation.addInputs( inputs, annClass.getFields() );
                            }
                            classRelation.setMethod( method );
                            annClass.addClassRelation( classRelation );
                            logger.debug( "Equation: " + classRelation );

                        }
                    } else if ( lt.getType() == LineType.TYPE_AXIOM ) {
                        LineType.Axiom statement = ( LineType.Axiom)lt.getStatement();

                        if ( statement.getOutputs().length > 0 ) {
                            if ( statement.getOutputs()[ 0 ].indexOf( "*" ) >= 0 ) {
                                getWildCards( classList, statement.getOutputs()[ 0 ] );
                            }

                        }
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_JAVAMETHOD, lt.getOrigSpecLine() );

                        if ( statement.getOutputs().length == 0 ) {
                            throw new SpecParseException( "Error in line \n" + lt.getOrigSpecLine() + "\nin class "
                                    + className + ".\nAn axiom can not have an empty output." );
                        }

                        classRelation.addOutputs( statement.getOutputs(), annClass.getFields() );

                        classRelation.setMethod( statement.getMethod() );

                        if( Table.TABLE_KEYWORD.equals( classRelation.getMethod() ) ) {
                            classRelation.getExceptions().clear();
                            classRelation.getExceptions().add( new ClassField( "java.lang.Exception", "exception" ) );
                        }

                        checkAliasLength( statement.getInputs(), annClass, className );

                        classRelation.addInputs( statement.getInputs(), annClass.getFields() );
                        
                        if ( statement.getSubtasks().size() != 0 ) {

                            for ( String subtaskString : statement.getSubtasks().keySet() ) {

                                String[][] stuff = statement.getSubtasks().get( subtaskString );

                                Collection<ClassField> varsForSubtask = annClass.getFields();
                                SubtaskClassRelation subtask;

                                String context = stuff[0][0];
                                // this denotes independant subtask,
                                // have to make sure that this class has
                                // already been parsed
                                if ( context != null ) {
                                    if ( !checkSpecClass( className, checkedClasses, classList, context ) ) {
                                        throw new SpecParseException(
                                                "Unable to parse independent subtask's context specification "
                                                + subtaskString );
                                    }
                                    varsForSubtask = classList.getType( context ).getFields();

                                    ClassField contextCF = new ClassField( "_" + context.toLowerCase(), context, true );

                                    subtask = SubtaskClassRelation.createIndependentSubtask( subtaskString, contextCF );
                                } else {
                                    subtask = SubtaskClassRelation.createDependentSubtask( subtaskString );
                                }

                                for ( int j = 0; j < stuff[2].length; j++ ) {
                                    subtask.addOutput( stuff[2][ j ], varsForSubtask );
                                }

                                if ( !stuff[1][ 0 ].equals( "" ) ) {
                                    subtask.addInputs( stuff[1], varsForSubtask );
                                }
                                classRelation.addSubtask( subtask );
                            }
                            classRelation.setType( RelType.TYPE_METHOD_WITH_SUBTASK );
                        }

                        logger.debug( classRelation.toString() );

                        annClass.addClassRelation( classRelation );

                    } else if ( lt.getType() == LineType.TYPE_SPECAXIOM ) {
                        LineType.Axiom statement = ( LineType.Axiom)lt.getStatement();
                        
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_UNIMPLEMENTED, lt.getOrigSpecLine() );

                        classRelation.addOutputs( statement.getOutputs(), annClass.getFields() );

                        classRelation.addInputs( statement.getInputs(), annClass.getFields() );

                        annClass.addClassRelation( classRelation );

                        logger.debug( classRelation.toString() );
                        
                    } else if ( lt.getType() == LineType.TYPE_ERROR ) {
                        throw new LineErrorException( lt.getOrigSpecLine() );
                    }
                }
            }
        } catch ( UnknownVariableException uve ) {

            String line = uve.getLine() != null ? uve.getLine() : lt != null ? lt.getOrigSpecLine() : null;
            throw new UnknownVariableException(className + "." + uve.getMessage(), line);

        }
        classList.add( annClass );
        return classList;
    }

    /**
     * Parses a given specification class and fills a list of classes
     * 
     * @param className
     * @param path
     * @param classList
     * @throws IOException
     * @throws SpecParseException
     */
    public static void parseSpecClass(String className, String path, ClassList classList) throws SpecParseException {

        new SpecParser(path).checkSpecClass(null, null, classList, className);
    }
    
    /**
     * @param parentClassName
     * @param checkedClasses
     * @param classList
     * @param type
     * @return
     * @throws IOException
     * @throws SpecParseException 
     * @throws SpecParseException
     */
    private boolean checkSpecClass( String parentClassName, Set<String> checkedClasses, ClassList classList, String type ) {

        logger.debug( "Checking existence of " + packagePath + type + ".java" );
        
        if(checkedClasses == null)
            checkedClasses = new LinkedHashSet<String>();
        
        if ( checkedClasses.contains( type ) ) {
        	if( RuntimeProperties.isRecursiveSpecsAllowed() ) {
        		return true;
        	}
            throw new MutualDeclarationException( parentClassName + " <-> " + type );
        } else if ( classList.getType( type ) != null ) {
            // do not need to parse already parsed class again
            return true;
        }

        boolean specClass = false;
        // if a file by this name exists in the package directory and it
        // includes a specification, we're gonna check it
        if ( isSpecClass( type ) ) {
            specClass = true;
            if ( !classList.containsType( type ) ) {
                checkedClasses.add( type );
                String s = specSourceProvider.getSource(type);

                try {
                    classList.addAll( parseSpecificationImpl( refineSpec( s ), type, null, checkedClasses ) );
                } catch ( SpecParseException e ) {
                    throw new SpecParseException("Class \"" + type + "\": " + e.toString(), e);
                }
                checkedClasses.remove( type );
            }
        }
        return specClass;
    }

    private static void checkAnyType( ClassField output, String input, AnnotatedClass parentClass, ClassList classes ) throws UnknownVariableException {
        checkAnyType( output, new String[] { input }, parentClass, classes );
    }

    // TODO - implement _any_!!!
    private static void checkAnyType( ClassField out, String[] inputs, AnnotatedClass parentClass, ClassList classes )
            throws UnknownVariableException {

        if ( out == null 
             || (!out.isAny() && !TYPE_ANY.equals( getVarType(out.getName(), parentClass, classes) ))) {
            return;
        }
        
        Collection<ClassField> vars = parentClass.getFields();
        
        String newType = TYPE_ANY;

        for ( int i = 0; i < inputs.length; i++ ) {
            ClassField in = getVarWithType( inputs[i], parentClass, classes );

            if ( in == null ) {
                try {
                    Integer.parseInt( inputs[ i ] );
                    newType = TYPE_INT;
                    continue;
                } catch ( NumberFormatException ex ) {
                }

                try {
                    Double.parseDouble( inputs[ i ] );
                    newType = TYPE_DOUBLE;
                    continue;
                } catch ( NumberFormatException ex ) {
                }

                if ( inputs[ i ] != null && inputs[ i ].trim().equals( "" ) ) {
                    newType = TYPE_DOUBLE;// TODO - tmp
                    continue;
                }

                throw new UnknownVariableException( inputs[ i ] );
            }
            if(in.isAny()) {
            	newType = in.getAnySpecificType();
            	continue;
            }
            else if ( i == 0 ) {
                newType = in.getType();
                continue;
            }
            TypeToken token = TypeToken.getTypeToken( newType );

            TypeToken tokenIn = TypeToken.getTypeToken( in.getType() );

            if ( token != null && tokenIn != null && token.compareTo( tokenIn ) < 0 ) {
                newType = in.getType();
            }
        }

        if(!TYPE_ANY.equals(newType))
        	out.setAnySpecificType( newType );
    }

    private static String getVarType(String var, AnnotatedClass parentClass, ClassList classes) {
        return getVarWithType( var, parentClass, classes ).getType();
    }
    
    private static ClassField getVarWithType(String var, AnnotatedClass parentClass, ClassList classes) {
        String[] split = var.split( "\\." );
        if(split.length > 1) {
            String type = "";
            for(int i = 0; i < split.length; i++) {
                ClassField cf = parentClass.getFieldByName( split[i] );
                if(cf.isAlias()) {
                  //if it's alias element access, stop the search, ProblemCreater will handle it
                  break;
                }
                type = cf.getType();
                parentClass = classes.getType( type );
            }
            return new ClassField( var, type );
        }
        return parentClass.getFieldByName( var );
    }
    
    private static void checkAliasLength( String inputs[], AnnotatedClass thisClass, String className )
            throws UnknownVariableException {
        for ( int i = 0; i < inputs.length; i++ ) {
            inputs[ i ] = checkAliasLength( inputs[ i ], thisClass, className );
        }
    }

    public static String checkAliasLength( String input, AnnotatedClass thisClass, String className )
            throws UnknownVariableException {
        // check if inputs contain <alias>.lenth variable
        if ( input.endsWith( ".length" ) ) {
            int index = input.lastIndexOf( ".length" );
            String aliasName = input.substring( 0, index );
            ClassField field = getVar( aliasName, thisClass.getFields() );
            if ( field != null && field.isAlias() ) {
                Alias alias = (Alias) field;
                String aliasLengthName = aliasName + "_LENGTH";
                if ( containsVar( thisClass.getFields(), aliasLengthName ) ) {
                    return aliasLengthName;
                }
                int length = alias.getVars().size();
                AliasLength var = new AliasLength( alias, thisClass.getName() );
                thisClass.addField( var );
                //if value cannot be determined here, it will be defined in ProgramCreator
                if(!alias.isWildcard() && alias.isInitialized() ) {
                    String meth = aliasLengthName + " = " + length;
                    ClassRelation cr = new ClassRelation( RelType.TYPE_EQUATION, meth );
                    cr.addOutput( aliasLengthName, thisClass.getFields() );
                    cr.setMethod( meth );
                    thisClass.addClassRelation( cr );
                }
                return aliasLengthName;

            }
            throw new UnknownVariableException( "Alias " + aliasName + " not found in " + className );
        }
        return input;
    }

    private static void getWildCards( ClassList classList, String output ) {
        String list[] = output.split("\\.");
        for ( int i = 0; i < list.length; i++ ) {
            logger.debug( list[ i ] );
        }
    }

    /**
     * @return list of fields declared in a specification.
     * @throws SpecParseException 
     */
    public static Collection<ClassField> getFields( String path, String fileName, String ext ) throws IOException, SpecParseException {
        Map<String, ClassField> fields = new LinkedHashMap<String, ClassField>();
        String s = FileFuncs.getFileContents(new File(path, fileName + ext));
        ArrayList<String> specLines = getSpec( s, false );

        while ( !specLines.isEmpty() ) {
            LineType lt = null;
            try {
                lt = getLine( specLines );
            } catch ( SpecParseException e ) {
                e.printStackTrace();
            }

            if ( lt != null ) {
                if ( lt.getType() == LineType.TYPE_ASSIGNMENT ) {
                    LineType.Assignment statement = ( LineType.Assignment)lt.getStatement();
                    ClassField field;
                    if( ( field = fields.get( statement.getName() ) ) != null ) {
                        field.setValue( statement.getValue() );
                    }
                } else if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                    LineType.Declaration statement = ( LineType.Declaration)lt.getStatement();

                    for ( int i = 0; i < statement.getNames().length; i++ ) {
                        ClassField var = new ClassField( statement.getNames()[ i ], statement.getType() );

                        fields.put( var.getName(), var );
                    }
                } else if ( lt.getType() == LineType.TYPE_ALIAS ) {
                    LineType.Alias statement = ( LineType.Alias)lt.getStatement();
                    Alias alias = new Alias( statement.getName(), statement.getComponentType() );
                    if( statement.getComponents() != null && statement.getComponents().length > 0 ) {
                        try {
                            //TODO - probably some time it will be needed to fill the class list
                            //and this does not work for aliases with wildcards
                            alias.addAll( statement.getComponents(), fields.values(), new ClassList() );
                            //alternative approach is to do next - 
                            //for( String var : list ) {
                            //    ClassField aliasCF = fields.get( var );
                            //    if( aliasCF != null )
                            //        alias.addVar( aliasCF );
                            //}
                        } catch ( UnknownVariableException e ) {
                            logger.info("Line: " + e.getLine() + ", " + e.toString());
                        } catch ( AliasException e ) {
                        }
                    }
                    fields.put( statement.getName(), alias );
                } else if ( lt.getType() == LineType.TYPE_SUPERCLASSES ) {
                    LineType.Superclasses statement = ( LineType.Superclasses)lt.getStatement();

                    for ( String name : statement.getClassNames() ) {
                        for( ClassField var : getFields( path, name, ext ) ) {
                            fields.put( var.getName(), var );
                        }
                    }
                }
            }
        }
        return fields.values();
    }

    private boolean isSpecClass( String type ) {

        String source = specSourceProvider.getSource(type);
        return source != null && source.matches( "(?s).*specification +" + type + ".*" );
    }

    private static boolean containsVar( Collection<ClassField> vars, String varName ) {

        return getVar( varName, vars ) != null;
    }

    /**
     * @param varName String
     * @param varList ArrayList
     * @return ClassField
     */
    public static ClassField getVar( String varName, Collection<ClassField> varList ) {

        for ( ClassField var : varList ) {
            if ( var.getName().equals( varName ) ) {
                return var;
            }
        }
        return null;
    } // getVar

    private class FileSourceProvider implements SpecificationSourceProvider<String> {

        @Override
        public String getSource(String spec) {
            String pathname = packagePath + spec + ".java";
            File file = new File(pathname);
            try {
                return FileUtils.readFileToString(file);
            }
            catch(Exception e) {
            }
            return null;
        }
    }
}
