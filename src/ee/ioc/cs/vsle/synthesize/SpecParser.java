package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.db;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import ee.ioc.cs.vsle.vclass.*;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.equations.EquationSolver;

/**
 * This class takes care of parsing the specification and translating it into a graph on which planning can be run.
 * @author Ando Saabas
 */
public class SpecParser {

	private final static SpecParser s_instance = new SpecParser();
	
	private SpecParser() {}
	
	public static SpecParser getInstance() {
		return s_instance;
	}
	
    public static void main( String[] args ) {
        SpecParser p = getInstance();

        try {
            String s = new String( p.getStringFromFile( args[ 0 ] ) );
            ArrayList a = p.getSpec( p.refineSpec( s ) );

            while ( !a.isEmpty() ) {
                if ( !( a.get( 0 ) ).equals( "" ) ) {
                    db.p( p.getLine( a ) );
                } else {
                    a.remove( 0 );

                }
            }
        } catch ( Exception e ) {
            db.p( e );
        }
    }

    /**
     Return the contents of a file as a String object.
     @param	fileName	name of the file name
     */
    public String getStringFromFile( String fileName ) throws IOException {
        db.p( "Retrieving " + fileName );

        BufferedReader in = new BufferedReader( new FileReader( fileName ) );
        String lineString, fileString = new String();

        while ( ( lineString = in.readLine() ) != null ) {
            fileString += lineString;
        }
        in.close();
        return fileString;
    }

    /**
     @return ArrayList of lines in specification
     @param	text	Secification text as String
     */
    public ArrayList<String> getSpec( String text ) {
        String[] s = text.trim().split( ";", -1 );
        ArrayList<String> a = new ArrayList<String>();

        for ( int i = 0; i < s.length; i++ ) {
            a.add( s[ i ].trim() );
        }
        return a;

    }

    /**
     Reads a line from an arraylist of specification lines, removes it from the arraylist and returns the line
     together with its type information
     @return	a specification line with its type information
     @param	a	arraylist of specification lines
     */
    public LineType getLine( ArrayList a ) {
        Matcher matcher2;
        Pattern pattern;

        while ( ( a.get( 0 ) ).equals( "" ) || ( ( String ) a.get( 0 ) ).trim().startsWith( "//" ) ) {
            a.remove( 0 );
            if ( a.isEmpty() ) {
                return null;
            }
        }
        String line = ( String ) a.get( 0 );

        a.remove( 0 );
        if ( line.indexOf( "alias " ) >= 0 ) {
            pattern = Pattern.compile( "alias ([^= ]+) ?= ?\\((.*)\\) *" );
            matcher2 = pattern.matcher( line );
            if ( matcher2.find() ) {
                String returnLine = matcher2.group( 1 ) + ":" + matcher2.group( 2 );

                return new LineType( LineType.TYPE_ALIAS, returnLine );
            }
			return new LineType( LineType.TYPE_ERROR, line );
        } else if ( line.indexOf( "=" ) >= 0 ) { // Extract on solve equations
            pattern = Pattern.compile( " *([^= ]+) *= *((\".*\")|(new .*\\(.*\\))|(\\{.*\\})) *$" );
            matcher2 = pattern.matcher( line );
            if ( matcher2.find() ) {
                return new LineType( LineType.TYPE_ASSIGNMENT, matcher2.group( 1 ) + ":" + matcher2.group( 2 ) );
            }
			pattern = Pattern.compile( " *([^=]+) *= *([-_0-9a-zA-Z.()\\+\\*/^ ]+) *$" );
			matcher2 = pattern.matcher( line );
			if ( matcher2.find() ) {
			    return new LineType( LineType.TYPE_EQUATION, line );
			}
			return new LineType( LineType.TYPE_ERROR, line );

        } else if ( line.indexOf( "->" ) >= 0 ) {
            pattern = Pattern.compile( "(.*) *-> *(.+) *\\{(.+)\\}" );
            matcher2 = pattern.matcher( line );
            if ( matcher2.find() ) {
                return new LineType( LineType.TYPE_AXIOM, line );
            }
			pattern = Pattern.compile( "(.*) *-> *([ -_a-zA-Z0-9.,]+) *$" );
			matcher2 = pattern.matcher( line );
			if ( matcher2.find() ) {
			    return new LineType( LineType.TYPE_SPECAXIOM, line );

			}
			return new LineType( LineType.TYPE_ERROR, line );
        } else {
            pattern = Pattern.compile( "^ *([a-zA-Z_$][0-9a-zA-Z_$]*(\\[\\])?) (([a-zA-Z_$][0-9a-zA-Z_$]* ?, ?)* ?[a-zA-Z_$][0-9a-zA-Z_$]* ?$)" );
            matcher2 = pattern.matcher( line );
            if ( matcher2.find() ) {
                return new LineType( LineType.TYPE_DECLARATION, matcher2.group( 1 ) + ":" + matcher2.group( 3 ) );
            }
			return new LineType( LineType.TYPE_ERROR, line );
        }
    }

    /**
     Extracts the specification from the java file, also removing unnecessary whitespaces
     @return	specification text
     @param fileString	a (Java) file containing the specification
     */
    public String refineSpec( String fileString ) throws IOException {
        Matcher matcher;
        Pattern pattern;

        // remove unneeded whitespace
        pattern = Pattern.compile( "[ \r\t\n]+" );
        matcher = pattern.matcher( fileString );
        fileString = matcher.replaceAll( " " );

        // find spec
        pattern = Pattern.compile(
                ".*/\\*@.*specification [a-zA-Z_0-9-.]+ ?\\{ ?(.+) ?\\} ?@\\*/ ?" );
        matcher = pattern.matcher( fileString );
        if ( matcher.find() ) {
            fileString = matcher.group( 1 );
        }
        return fileString;
    }

    Problem makeProblem( ClassList classes ) throws SpecParseException{
    	return makeProblemImpl( classes, "this", "this", new Problem() );
    }
    /**
     Creates the problem - a graph-like data structure on which planning can be applied. The method is recursively
     applied to dig through the class tree.
     @return	problem which can be given to the planner
     @param classes the list of classes that exist in the problem setting.
     @param type the type of object which is currently being added to the problem.
     @param caller caller, or the "parent" of the current object. The objects name will be caller + . + obj.name
     @param problem the problem itself (needed because of recursion).
     */
    private Problem makeProblemImpl( ClassList classes, String type, String caller, Problem problem ) throws
            SpecParseException {
        // ee.ioc.cs.editor.util.db.p("CLASSES: "+classes);
        // ee.ioc.cs.editor.util.db.p("TYPE: "+type);
        AnnotatedClass ac = classes.getType( type );
        ClassField cf = null;
        ClassRelation classRelation;
        Var var, var1, var2;
        Rel rel;
        HashSet<Rel> relSet = new HashSet<Rel>();

        for ( int j = 0; j < ac.fields.size(); j++ ) {
            cf = ac.fields.get( j );
            if ( classes.getType( cf.getType() ) != null ) {
                problem = makeProblemImpl( classes, cf.getType(), caller + "." + cf.getName(), problem );
            }
            if ( cf.getType().equals( "alias" ) ) {
                cf = rewriteWildcardAlias( cf, ac, classes );
            }
            var = new Var();
            var.setObj( caller );
            var.setField( cf );
            var.setName( cf.getName() );
            var.setType( cf.getType() );
            problem.addVar( var );
        }

        for ( int j = 0; j < ac.classRelations.size(); j++ ) {
            classRelation = ac.classRelations.get( j );
            cf = null;
            String obj = caller;

            rel = new Rel();
            boolean isAliasRel = false;

            /* If we have a relation alias = alias, we rewrite it into new relations, ie we create
             a relation for each component of the alias structure*/
            if ( classRelation.getInputs().size() == 1 && classRelation.getOutputs().size() == 1 &&
                 ( classRelation.getType() == 4 || classRelation.getType() == 3 ) ) {
                ClassField cf1 = classRelation.getInputs().get( 0 );
                ClassField cf2 = classRelation.getOutputs().get( 0 );

                //if we have a relation alias = *.sth, we need to find out what it actually is
                if ( cf1.getName().startsWith( "*." ) ) {
                    String s = checkIfAliasWildcard( classRelation );
                    if ( s != null ) {
                        relSet = makeAliasWildcard( ac, classes, classRelation, problem, obj, s );
                        rel = null;
                        isAliasRel = true;
                    }
                }

                if ( problem.getAllVars().containsKey( obj + "." + cf1.getName() ) ) {
                    Var v1 = problem.getAllVars().get( obj + "." + cf1.getName() );
                    Var v2 = problem.getAllVars().get( obj + "." + cf2.getName() );

                    if ( v1.getField().isAlias() && v2.getField().isAlias() ) {
                        if ( RuntimeProperties.isDebugEnabled() )
                            db.p( ( ( Alias ) v1.getField() ).getAliasType() + " " +
                                  ( ( Alias ) v2.getField() ).getAliasType() );
                        if ( !( ( Alias ) v1.getField() ).getAliasType().equals( ( ( Alias ) v2.
                                getField() ).
                                getAliasType() ) ) {
                            throw new AliasException( "Differently typed aliases connected: " + obj +
                                    "." + cf1.getName() + " and " + obj + "." + cf2.getName() );
                        }
                        isAliasRel = true;
                        for ( int i = 0; i < v1.getField().getVars().size(); i++ ) {
                            String s1 = v1.getField().getVars().get( i ).getName();
                            String s2 = v2.getField().getVars().get( i ).getName();

                            var1 = problem.getAllVars().get( v1.getObject() + "." + s1 );
                            var2 = problem.getAllVars().get( v2.getObject() + "." + s2 );
                            rel = new Rel();
                            rel.setUnknownInputs( classRelation.getInputs().size() );
                            rel.setSubtaskFlag( classRelation.getSubtasks().size() );
                            rel.setObj( obj );
                            rel.setType( 5 );

                            rel.addInput( var2 );
                            rel.addOutput( var1 );
                            var2.addRel( rel );
                            problem.addRel( rel );
                        }
                    }
                }
            }

            if ( !isAliasRel ) {
                String s = checkIfRightWildcard( classRelation );
                if ( s != null ) {
                    relSet = makeRightWildcardRel( ac, classes, classRelation, problem, obj, s );
                    rel = null;
                } else
                    rel = makeRel( classRelation, problem, obj );
                if ( classRelation.getSubtasks().size() > 0 ) {
                    Rel subtaskRel = new Rel();

                    for ( int l = 0; l < classRelation.getSubtasks().size(); l++ ) {
                        ClassRelation subtask = classRelation.getSubtasks().get( l );
                        subtaskRel = makeRel( subtask, problem, obj );
                        if ( rel != null ) {
                            rel.addSubtask( subtaskRel );
                        } else {
                            Iterator varsIter = relSet.iterator();
                            while ( varsIter.hasNext() ) {
                                Rel r = ( Rel ) varsIter.next();
                                r.addSubtask( subtaskRel );
                            }
                        }
                    }
                }
            }

            // if it is not a "real" relation (type 7), we just set the result as target, and inputs as known variables
            if ( classRelation.getType() == 7 ) {
                setTargets( problem, classRelation, obj );
            } else if ( rel != null && classRelation.getInputs().isEmpty() &&
                        rel.getSubtaskCounter() == 0 ) { // if class relation doesnt have inputs, its an axiom
                problem.addAxiom( rel );
            }
            //else if (classRelation.inputs.isEmpty() && rel.subtaskFlag > 0) {
            else if ( rel != null && rel.getSubtaskCounter() > 0 ) {
                problem.addRelWithSubtask( rel );
                problem.addRel( rel );
            } else {
                if ( rel != null && rel.getSubtasks().size() == 0 ) {
                    problem.addRel( rel );
                } else {
                    problem.addAllRels( relSet );
                }
            }

        }
        //System.out.println(problem);
        return problem;
    }

    private ClassField rewriteWildcardAlias( ClassField cf, AnnotatedClass ac, ClassList classes ) {
        if ( cf.getVars().size() == 1 &&
             cf.getVars().get( 0 ).getName().startsWith( "*." ) ) {
            String wildcardVar = cf.getVars().get( 0 ).getName().substring( 2 );
            cf.getVars().clear();
            ClassField clf;
            for ( int i = 0; i < ac.fields.size(); i++ ) {
                clf = ac.fields.get( i );
                AnnotatedClass anc = classes.getType( clf.getType() );
                if ( anc != null ) {
                    if ( anc.hasField( wildcardVar ) ) {
                        ClassField cf2 = new ClassField(
                                clf.getName() + "." + wildcardVar,
                                anc.getFieldByName( wildcardVar ).getType() );

                        cf.getVars().add( cf2 );
                    }
                }
            }
            return cf;
        }
		return cf;
    }

    private void isRightWildcard( ClassRelation classRelation, AnnotatedClass ac, ClassList classes,
                                  String type ) {
        ClassField cf;
        String s = checkIfRightWildcard( classRelation );
        //if the right side of the axiom contains a wildcard, we'll rewrite the axiom

    }

    private String checkIfRightWildcard( ClassRelation classRelation ) {
        String s = classRelation.getOutputs().get( 0 ).getName();
        if ( s.startsWith( "*." ) )
            return s.substring( 2 );
        return null;
    }

    private String checkIfAliasWildcard( ClassRelation classRelation ) {
        String s = classRelation.getInputs().get( 0 ).getName();
        if ( s.startsWith( "*." ) )
            return s.substring( 2 );
        return null;
    }

    /**
     In case of a goal specification is included (eg a -> b), the right hand side is added to problem
     targets, left hand side is added to known variables.
     @param problem problem to be changed
     @param classRelation the goal specification is extracted from it.
     @param obj the name of the object where the goal specification was declared.
     */
    void setTargets( Problem problem, ClassRelation classRelation, String obj ) throws
            UnknownVariableException {
        Var var;
        ClassField cf;

        for ( int k = 0; k < classRelation.getInputs().size(); k++ ) {
            cf = classRelation.getInputs().get( k );
            if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
                var = problem.getAllVars().get( obj + "." + cf.getName() );
                problem.addKnown( var );
            } else {
                throw new UnknownVariableException( cf.getName() );
            }
        }
        for ( int k = 0; k < classRelation.getOutputs().size(); k++ ) {
            cf = classRelation.getOutputs().get( k );
            if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
                var = problem.getAllVars().get( obj + "." + cf.getName() );
                problem.addTarget( var );
            } else {
                throw new UnknownVariableException( cf.getName() );
            }
        }

    }


    HashSet<Rel> makeRightWildcardRel( AnnotatedClass ac, ClassList classes, ClassRelation classRelation,
                                  Problem problem, String obj, String wildcardVar ) throws
            UnknownVariableException {
        ClassField clf;
        HashSet<Rel> set = new HashSet<Rel>();
        for ( int i = 0; i < ac.fields.size(); i++ ) {
            clf = ac.fields.get( i );
            AnnotatedClass anc = classes.getType( clf.getType() );
            if ( anc != null ) {
                if ( anc.hasField( wildcardVar ) ) {

                    Var var;
                    Rel rel = new Rel();

                    rel.setMethod( classRelation.getMethod() );
                    rel.setUnknownInputs( classRelation.getInputs().size() );
                    rel.setSubtaskFlag( classRelation.getSubtasks().size() );
                    rel.setObj( obj );
                    rel.setType( classRelation.getType() );
                    ClassField cf;

                    for ( int k = 0; k < classRelation.getInputs().size(); k++ ) {
                        cf = classRelation.getInputs().get( k );
                        if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
                            var = problem.getAllVars().get( obj + "." + cf.getName() );
                            var.addRel( rel );
                            rel.addInput( var );
                        } else {
                            throw new UnknownVariableException( cf.getName() );
                        }
                    }
                    for ( int k = 0; k < classRelation.getOutputs().size(); k++ ) {
                        if ( k == 0 ) {
                            if ( problem.getAllVars().containsKey( obj + "." + clf.getName() + "." +
                                    wildcardVar ) ) {
                                var = problem.getAllVars().get( obj + "." + clf.getName() +
                                        "." + wildcardVar );
                                rel.addOutput( var );
                            } else {
                                throw new UnknownVariableException( obj + "." + clf.getName() + "." +
                                        wildcardVar );
                            }
                        } else {
                            cf = classRelation.getOutputs().get( k );
                            if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
                                var = problem.getAllVars().get( obj + "." + cf.getName() );
                                rel.addOutput( var );
                            } else {
                                throw new UnknownVariableException( cf.getName() );
                            }
                        }

                    }
                    set.add( rel );
                }
            }
        }
        return set;
    }


    HashSet<Rel> makeAliasWildcard( AnnotatedClass ac, ClassList classes, ClassRelation classRelation,
                               Problem problem, String obj, String wildcardVar ) throws
            UnknownVariableException {
        ClassField clf;
        HashSet<Rel> relset = new HashSet<Rel>();
        Rel rel = new Rel();
        rel.setMethod( classRelation.getMethod() );
        rel.setObj( obj );
        rel.setType( classRelation.getType() );
        for ( int i = 0; i < ac.fields.size(); i++ ) {
            clf = ac.fields.get( i );
            AnnotatedClass anc = classes.getType( clf.getType() );
            if ( anc != null ) {
                if ( anc.hasField( wildcardVar ) ) {
                    Var var;
//                    ClassField cf;
                    if ( problem.getAllVars().containsKey( obj + "." + clf.getName() + "." +
                            wildcardVar ) ) {
                        var = problem.getAllVars().get( obj + "." + clf.getName() + "." +
                                wildcardVar );
                        var.addRel( rel );
                        rel.addInput( var );
                    } else {
                        throw new UnknownVariableException( obj + "." + clf.getName() + "." +
                                wildcardVar );
                    }

                }
            }
        }
        ClassField cf = classRelation.getOutputs().get( 0 );
        if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
            rel.addOutput( problem.getAllVars().get( obj + "." + cf.getName() ) );
        }

        rel.setUnknownInputs( rel.getInputs().size() );

        relset.add( rel );

        rel = new Rel();
        rel.setMethod( classRelation.getMethod() );
        rel.setObj( obj );
        rel.setType( classRelation.getType() );
        for ( int i = 0; i < ac.fields.size(); i++ ) {
            clf = ac.fields.get( i );
            AnnotatedClass anc = classes.getType( clf.getType() );
            if ( anc != null ) {
                if ( anc.hasField( wildcardVar ) ) {
                    Var var;
                    if ( problem.getAllVars().containsKey( obj + "." + clf.getName() + "." +
                            wildcardVar ) ) {
                        var = problem.getAllVars().get( obj + "." + clf.getName() + "." +
                                wildcardVar );
                        //var.addRel( rel );//
                        rel.addOutput( var );
                    } else {
                        throw new UnknownVariableException( obj + "." + clf.getName() + "." +
                                wildcardVar );
                    }

                }
            }
        }
        cf = classRelation.getOutputs().get( 0 );
        if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
            Var varWithWildcard = problem.getAllVars().get( obj + "." + cf.getName() );
            rel.addInput( varWithWildcard );
            varWithWildcard.addRel( rel );
        }

        rel.setUnknownInputs( rel.getInputs().size() );

        relset.add( rel );
        return relset;
    }


    /**
     creates a relation that will be included in the problem.
     @param problem that will include relation (its needed to get variable information from it)
     @param classRelation the implementational information about this relation
     @param obj the name of the object where the goal specification was declared.
     */

    Rel makeRel( ClassRelation classRelation, Problem problem, String obj ) throws
            UnknownVariableException {
        Var var;
        Rel rel = new Rel();

        rel.setMethod( classRelation.getMethod() );
        rel.setUnknownInputs( classRelation.getInputs().size() );
        rel.setSubtaskFlag( classRelation.getSubtasks().size() );
        rel.setObj( obj );
        rel.setType( classRelation.getType() );
        ClassField cf;

        for ( int k = 0; k < classRelation.getInputs().size(); k++ ) {
            cf = classRelation.getInputs().get( k );
            if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
                var = problem.getAllVars().get( obj + "." + cf.getName() );
                var.addRel( rel );
                rel.addInput( var );
            } else {
                throw new UnknownVariableException( cf.getName() );
            }
        }
        for ( int k = 0; k < classRelation.getOutputs().size(); k++ ) {
            cf = classRelation.getOutputs().get( k );
            if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
                var = problem.getAllVars().get( obj + "." + cf.getName() );
                rel.addOutput( var );
            } else {
                throw new UnknownVariableException( cf.getName() );
            }
        }
        for ( int k = 0; k < classRelation.getExceptions().size(); k++ ) {
            cf = classRelation.getExceptions().get( k );
            Var ex = new Var();
            ex.setName( cf.getType() );
            rel.getExceptions().add( ex );
        }

        return rel;
    }

    public ClassList parseSpecification( String spec ) throws IOException,
    										SpecParseException, EquationException {
    	HashSet<String> hs = new HashSet<String>();
    	return parseSpecificationImpl( spec, "this", null, hs );
    }
    
    /**
     A recrusve method that does the actual parsing. It creates a list of annotated classes that
     carry infomation about the fields and relations in a class specification.
     @param spec a specfication to be parsed. If it includes a declaration of an annotated class, it will be
     recursively parsed.
     @param	className the name of the class being parsed
     @param	parent
     @param	checkedClasses the list of classes that parser has started to check. Needed to prevent infinite loop
     in case of mutual declarations.
     */
    private ClassList<AnnotatedClass> parseSpecificationImpl( String spec, String className, AnnotatedClass parent,
                                         HashSet<String> checkedClasses ) throws IOException,
            SpecParseException, EquationException {
        Matcher matcher2;
        Pattern pattern;
        String[] split;
        ArrayList<ClassField> vars = new ArrayList<ClassField>();
        ArrayList<String> subtasks = new ArrayList<String>();
        AnnotatedClass annClass = new AnnotatedClass( className, parent );
        ClassList<AnnotatedClass> classList = new ClassList<AnnotatedClass>();

        ArrayList specLines = getSpec( spec );

        try {

            while ( !specLines.isEmpty() ) {
                LineType lt = getLine( specLines );

                if ( lt != null ) {
                    if ( lt.getType() == LineType.TYPE_ASSIGNMENT ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION );

                        classRelation.setOutput( split[ 0 ], vars );
                        classRelation.setMethod( split[ 0 ] + " = " + split[ 1 ] );
                        annClass.addClassRelation( classRelation );
                        if ( RuntimeProperties.isDebugEnabled() ) db.p( classRelation );

                    } else if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        String[] vs = split[ 1 ].trim().split( " *, *", -1 );
                        String type = split[ 0 ].trim();

                        if ( RuntimeProperties.isDebugEnabled() ) db.p( "Checking existence of " +
                                RuntimeProperties.packageDir + type + ".java" );
                        if ( checkedClasses.contains( type ) ) {
                            throw new MutualDeclarationException( className + " <-> " + type );
                        }
                        File file = new File( RuntimeProperties.packageDir + type + ".java" );
                        boolean specClass = false;

                        // if a file by this name exists in the package directory and it includes a specification, we're gonna check it
                        if ( file.exists() && isSpecClass( type ) ) {
                            specClass = true;
                            if ( classList.getType( type ) == null ) {
                                checkedClasses.add( type );
                                String s = new String( getStringFromFile( RuntimeProperties.
                                        packageDir + type + ".java" ) );

                                classList.addAll( parseSpecificationImpl( refineSpec( s ), type,
                                        annClass, checkedClasses ) );
                                checkedClasses.remove( type );
                                specClass = true;
                            }
                        }
                        for ( int i = 0; i < vs.length; i++ ) {
                            if ( varListIncludes( vars, vs[ i ] ) ) {
                                throw new SpecParseException( "Variable " + vs[ i ] +
                                        " declared more than once in class " + className );
                            }
                            ClassField var = new ClassField( vs[ i ], type, specClass );

                            vars.add( var );
                        }

                    } else if ( lt.getType() == LineType.TYPE_ALIAS ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        String[] list = split[ 1 ].trim().split( " *, *", -1 );
                        String name = split[ 0 ];
                        Alias a = new Alias( name );

                        a.addAll( list, vars, classList );
                        vars.add( a );
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_ALIAS );

                        classRelation.addInputs( list, vars );
                        classRelation.setMethod( "alias" );
                        classRelation.setOutput( name, vars );
                        annClass.addClassRelation( classRelation );
                        if ( RuntimeProperties.isDebugEnabled() ) db.p( classRelation );
                        if ( !list[ 0 ].startsWith( "*" ) ) {
                            classRelation = new ClassRelation( RelType.TYPE_ALIAS );
                            classRelation.addOutputs( list, vars );
                            classRelation.setMethod( "alias" );
                            classRelation.setInput( name, vars );
                            annClass.addClassRelation( classRelation );
                            if ( RuntimeProperties.isDebugEnabled() ) db.p( classRelation );
                        }

                    } else if ( lt.getType() == LineType.TYPE_EQUATION ) {
                        EquationSolver.solve( lt.getSpecLine() );
                        for ( int i = 0; i < EquationSolver.relations.size(); i++ ) {
                            String result = ( String ) EquationSolver.relations.get( i );
                            String[] pieces = result.split( ":" );
                            // if its actually alias
                            /* if (getVar(pieces[2].trim(), vars).isAlias()) {
                             String[] inputs = pieces[1].trim().split(" ");
                             if (geVar(inputs[0].trim(), vars).isAlias()) {
                             if () {
                             }
                             }
                             } else {*/

                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION );

                            classRelation.setOutput( pieces[ 2 ].trim(), vars );

                            String[] inputs = pieces[ 1 ].trim().split( " " );

                            if ( !inputs[ 0 ].equals( "" ) ) {
                                classRelation.addInputs( inputs, vars );
                            }
                            classRelation.setMethod( pieces[ 0 ] );
                            annClass.addClassRelation( classRelation );
                            if ( RuntimeProperties.isDebugEnabled() ) db.p( "Equation: " +
                                    classRelation );

                        }
                    } else if ( lt.getType() == LineType.TYPE_AXIOM ) {
                        pattern = Pattern.compile( "\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]" );
                        matcher2 = pattern.matcher( lt.getSpecLine() );

                        subtasks.clear();
                        while ( matcher2.find() ) {
                            if ( RuntimeProperties.isDebugEnabled() ) db.p( "matching " +
                                    matcher2.group( 0 ) );
                            subtasks.add( matcher2.group( 0 ) );
                        }
                        lt = new LineType( lt.getType(), lt.getSpecLine().replaceAll(
                                "\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]", "#" ) );

                        pattern = Pattern.compile( "(.*) *-> ?(.*)\\{(.*)\\}" );
                        matcher2 = pattern.matcher( lt.getSpecLine() );
                        if ( matcher2.find() ) {

                            String[] outputs = matcher2.group( 2 ).trim().split( " *, *", -1 );

                            if ( !outputs[ 0 ].equals( "" ) ) {
                                if ( outputs[ 0 ].indexOf( "*" ) >= 0 ) {
                                    getWildCards( classList, outputs[ 0 ] );
                                }

                            }
                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_JAVAMETHOD );

                            if ( matcher2.group( 2 ).trim().equals( "" ) ) {
                                throw new SpecParseException( "Error in line \n" + lt.getSpecLine() +
                                        "\nin class " + className +
                                        ".\nAn axiom can not have an empty output." );
                            }
                            //String[] outputs = matcher2.group(2).trim().split(" *, *", -1);

                            if ( !outputs[ 0 ].equals( "" ) ) {
                                classRelation.addOutputs( outputs, vars );
                            }

                            String[] inputs = matcher2.group( 1 ).trim().split( " *, *", -1 );

                            if ( !inputs[ 0 ].equals( "" ) ) {
                                classRelation.addInputs( inputs, vars );
                            }
                            if ( subtasks.size() != 0 ) {
                                classRelation.addSubtasks( subtasks, vars );
                                classRelation.setType( RelType.TYPE_METHOD_WITH_SUBTASK );
                            }
                            classRelation.setMethod( matcher2.group( 3 ).trim() );
                            if ( RuntimeProperties.isDebugEnabled() ) db.p( classRelation );
                            annClass.addClassRelation( classRelation );
                        }

                    } else if ( lt.getType() == LineType.TYPE_SPECAXIOM ) {
                        pattern = Pattern.compile( "(.*) *-> *([-_a-zA-Z0-9.,]+) *$" );
                        matcher2 = pattern.matcher( lt.getSpecLine() );
                        if ( matcher2.find() ) {
                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_UNIMPLEMENTED );
                            String[] outputs = matcher2.group( 2 ).trim().split( " *, *", -1 );

                            if ( !outputs[ 0 ].equals( "" ) ) {
                                classRelation.addOutputs( outputs, vars );
                            }

                            String[] inputs = matcher2.group( 1 ).trim().split( " *, *", -1 );

                            if ( !inputs[ 0 ].equals( "" ) ) {
                                classRelation.addInputs( inputs, vars );
                            }
                            if ( RuntimeProperties.isDebugEnabled() ) db.p( classRelation );
                            annClass.addClassRelation( classRelation );
                        }
                    } else if ( lt.getType() == LineType.TYPE_ERROR ) {
                        throw new LineErrorException( lt.getSpecLine() );
                    }
                }
            }
        } catch ( UnknownVariableException uve ) {
            uve.printStackTrace();
            throw new UnknownVariableException( className + "." + uve.excDesc );

        }
        annClass.addVars( vars );
        classList.add( annClass );
        return classList;
    }

    private void getWildCards( ClassList classList, String output ) {
        String list[] = output.split( "\\." );
        for ( int i = 0; i < list.length; i++ ) {
            if ( RuntimeProperties.isDebugEnabled() ) db.p( list[ i ] );
        }

    }

    /**
     @return list of fields declared in a specification.
     */
    public ArrayList getFields( String fileName ) throws IOException {
        ArrayList<ClassField> vars = new ArrayList<ClassField>();
        String s = new String( getStringFromFile( fileName ) );
        ArrayList specLines = getSpec( refineSpec( s ) );
        String[] split;

        while ( !specLines.isEmpty() ) {
            LineType lt = getLine( specLines );

            if ( lt != null ) {
                if ( lt.getType() == LineType.TYPE_ASSIGNMENT ) {
                    split = lt.getSpecLine().split( ":", -1 );
                    for ( int i = 0; i < vars.size(); i++ ) {
                        if ( vars.get( i ).getName().equals( split[ 0 ] ) ) {
                            vars.get( i ).setValue( split[ 1 ] );
                        }
                    }
                } else if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                    split = lt.getSpecLine().split( ":", -1 );
                    String[] vs = split[ 1 ].trim().split( " *, *", -1 );
                    String type = split[ 0 ].trim();

                    for ( int i = 0; i < vs.length; i++ ) {
                        ClassField var = new ClassField( vs[ i ], type );

                        vars.add( var );
                    }
                }
            }
        }
        return vars;
    }

    boolean isSpecClass( String file ) {
        try {
            BufferedReader in = new BufferedReader( new FileReader( RuntimeProperties.packageDir +
                    file + ".java" ) );
            String lineString, fileString = new String();

            while ( ( lineString = in.readLine() ) != null ) {
                fileString += lineString;
            }
            in.close();
            if ( fileString.matches( ".*specification +" + file + ".*" ) ) {

                return true;
            }
        } catch ( IOException ioe ) {
            db.p( ioe );
        }
        return false;
    }

    boolean varListIncludes( ArrayList vars, String varName ) {
        ClassField cf;

        for ( int i = 0; i < vars.size(); i++ ) {
            cf = ( ClassField ) vars.get( i );
            if ( cf.getName().equals( varName ) ) {
                return true;
            }
        }
        return false;
    }
}
