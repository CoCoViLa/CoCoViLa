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
 * @author Ando Saabas, Pavel Grigorenko
 */
public class SpecParser {

	private SpecParser() {}
	
    public static void main( String[] args ) {

        try {
            String s = new String( getStringFromFile( args[ 0 ] ) );
            ArrayList a = getSpec( s, false );

            while ( !a.isEmpty() ) {
                if ( !( a.get( 0 ) ).equals( "" ) ) {
                    db.p( getLine( a ) );
                } else {
                    a.remove( 0 );

                }
            }
        } catch ( Exception e ) {
            db.p( e );
        }
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
     Return the contents of a file as a String object.
     @param	fileName	name of the file name
     */
    static String getStringFromFile( String fileName ) throws IOException {
        db.p( "Retrieving " + fileName );

        BufferedReader in = new BufferedReader( new FileReader( fileName ) );
        String lineString, fileString = new String();

        while ( ( lineString = in.readLine() ) != null ) {
            fileString += lineString + "\n";
        }
        in.close();
        return fileString;
    }

    /**
     @return ArrayList of lines in specification
     @param	text	Secification text as String
     */
    static ArrayList<String> getSpec( String text, boolean isRefinedSpec ) throws IOException {
    	if( !isRefinedSpec ) {
    		text = refineSpec( text );
    	}
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
    static LineType getLine( ArrayList<String> a ) {
        Matcher matcher2;
        Pattern pattern;

        while ( ( a.get( 0 ) ).equals( "" ) || a.get( 0 ).trim().startsWith( "//" ) ) {
            a.remove( 0 );
            if ( a.isEmpty() ) {
                return null;
            }
        }
        String line = a.get( 0 );

        a.remove( 0 );
        if ( line.indexOf( "alias " ) >= 0 ) {
            pattern = Pattern.compile( "alias ([^= ]+) ?= ?\\((.*)\\) *" );
            matcher2 = pattern.matcher( line );
            if ( matcher2.find() ) {
                String returnLine = matcher2.group( 1 ) + ":" + matcher2.group( 2 );

                return new LineType( LineType.TYPE_ALIAS, returnLine );
            }
			return new LineType( LineType.TYPE_ERROR, line );
        } else if ( line.indexOf( ":=" ) >= 0 ) {
        	pattern = Pattern.compile( "^ *([a-zA-Z_$][0-9a-zA-Z_$]*[\\[\\]]*) +([a-zA-Z_$][0-9a-zA-Z_$]*) *:= *([a-zA-Z0-9.{}\"]+|new [a-zA-Z0-9.{}\\[\\]]+) *$" );
            matcher2 = pattern.matcher( line );
            if ( matcher2.find() ) {
                return new LineType( LineType.TYPE_CONST, matcher2.group( 1 ) + ":" + matcher2.group( 2 ) + ":" + matcher2.group( 3 ) );
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
        }  else {
            pattern = Pattern.compile( "^ *([a-zA-Z_$][0-9a-zA-Z_$]*(\\[\\])*) (([a-zA-Z_$][0-9a-zA-Z_$]* ?, ?)* ?[a-zA-Z_$][0-9a-zA-Z_$]* ?$)" );
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
    private static String refineSpec( String fileString ) throws IOException {
        Matcher matcher;
        Pattern pattern;

        //remove comments before removing line brake \n
        String[] s = fileString.split( "\n" );
        fileString = "";
        for (int i = 0; i < s.length; i++) {
			if( !s[i].trim().startsWith("//") ) {
				fileString += s[i];
			}
		}
			
        // remove unneeded whitespace
        pattern = Pattern.compile( "[ \r\t\n]+" );
        matcher = pattern.matcher( fileString );
        fileString = matcher.replaceAll( " " );

        // find spec
        pattern = Pattern.compile( //"[ ]+(super [ a-zA-Z_0-9-,]+ )? "
                ".*/\\*@.*specification [a-zA-Z_0-9-.]+ ?\\{ ?(.+) ?\\} ?@\\*/ ?" );
        matcher = pattern.matcher( fileString );
        if ( matcher.find() ) {
            fileString = matcher.group( 1 );
        }
        return fileString;
    }

    public static ClassList parseSpecification( String fullSpec ) throws IOException,
    										SpecParseException, EquationException {
    	HashSet<String> hs = new HashSet<String>();
    	return parseSpecificationImpl( refineSpec( fullSpec ), "this", null, hs );
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
    private static ClassList<AnnotatedClass> parseSpecificationImpl( String spec, String className, AnnotatedClass parent,
                                         HashSet<String> checkedClasses ) throws IOException,
            SpecParseException, EquationException {
        Matcher matcher2;
        Pattern pattern;
        String[] split;
        ArrayList<ClassField> vars = new ArrayList<ClassField>();
        ArrayList<String> subtasks = new ArrayList<String>();
        AnnotatedClass annClass = new AnnotatedClass( className, parent );
        ClassList<AnnotatedClass> classList = new ClassList<AnnotatedClass>();

        ArrayList<String> specLines = getSpec( spec, true );

        try {

            while ( !specLines.isEmpty() ) {
                LineType lt = getLine( specLines );

                if ( lt != null ) {
                	db.p( "Parsing: Class " + className + " Line " + lt.getSpecLine() );
                    if ( lt.getType() == LineType.TYPE_ASSIGNMENT ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION );

                        classRelation.setOutput( split[ 0 ], vars );
                        classRelation.setMethod( split[ 0 ] + " = " + split[ 1 ] );
                        annClass.addClassRelation( classRelation );
                        if ( RuntimeProperties.isLogDebugEnabled() ) db.p( classRelation );
                        
                    } else if ( lt.getType() == LineType.TYPE_CONST ) {
                    	split = lt.getSpecLine().split( ":", -1 );
                    	String type  = split[ 0 ].trim();
                    	String name  = split[ 1 ].trim();
                    	String value = split[ 2 ].trim();
                    	
                    	if ( varListIncludes( vars, name ) ) {
                            throw new SpecParseException( "Variable " + name +
                                    " declared more than once in class " + className );
                        }
                    	
                    	File file = new File( RuntimeProperties.packageDir + type + ".java" );
                    	if( file.exists() && isSpecClass( type ) ) {
                    		throw new SpecParseException( "Constant " + name +
                                    " cannot be of type " + type );
                    	}
                    	db.p( "---===!!! " + type + " " + name + " = " + value );
                    	
                    	ClassField var = new ClassField( name, type, value, true );

                        vars.add( var );
                    	
                    } else if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        String[] vs = split[ 1 ].trim().split( " *, *", -1 );
                        String type = split[ 0 ].trim();

                        if ( RuntimeProperties.isLogDebugEnabled() ) db.p( "Checking existence of " +
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
                        if ( varListIncludes( vars, name ) ) {
                            throw new SpecParseException( "Variable " + name +
                                    " declared more than once in class " + className );
                        }
                        Alias a = new Alias( name );

                        a.addAll( list, vars, classList );
                        vars.add( a );
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_ALIAS );

                        classRelation.addInputs( list, vars );
                        classRelation.setMethod( "alias" );
                        classRelation.setOutput( name, vars );
                        annClass.addClassRelation( classRelation );
                        if ( RuntimeProperties.isLogDebugEnabled() ) db.p( classRelation );
                        if ( !list[ 0 ].startsWith( "*" ) ) {
                            classRelation = new ClassRelation( RelType.TYPE_ALIAS );
                            classRelation.addOutputs( list, vars );
                            classRelation.setMethod( "alias" );
                            classRelation.setInput( name, vars );
                            annClass.addClassRelation( classRelation );
                            if ( RuntimeProperties.isLogDebugEnabled() ) db.p( classRelation );
                        } else {
                        	a.setWildcard( true );
                        }

                    } else if ( lt.getType() == LineType.TYPE_EQUATION ) {
                        EquationSolver.solve( lt.getSpecLine() );
                        for ( int i = 0; i < EquationSolver.relations.size(); i++ ) {
                            String result = ( String ) EquationSolver.relations.get( i );
                            String[] pieces = result.split( ":" );

                            //cannot assign new values for constants
                            ClassField tmp = ClassRelation.getVar( pieces[ 2 ].trim(), vars );
                            if( tmp != null && tmp.isConstant() ) {
                            	db.p( "Ignoring constant and equation output: " + tmp );
                            	continue;
                            }
                            
                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION );

                            classRelation.setOutput( pieces[ 2 ].trim(), vars );

                            String[] inputs = pieces[ 1 ].trim().split( " " );

                            //checkAliasLength( inputs, vars, className );
                            
                            if ( !inputs[ 0 ].equals( "" ) ) {
                                classRelation.addInputs( inputs, vars );
                            }
                            classRelation.setMethod( pieces[ 0 ] );
                            annClass.addClassRelation( classRelation );
                            if ( RuntimeProperties.isLogDebugEnabled() ) db.p( "Equation: " +
                                    classRelation );

                        }
                    } else if ( lt.getType() == LineType.TYPE_AXIOM ) {
                        pattern = Pattern.compile( "\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]" );
                        matcher2 = pattern.matcher( lt.getSpecLine() );

                        subtasks.clear();
                        while ( matcher2.find() ) {
                            if ( RuntimeProperties.isLogDebugEnabled() ) db.p( "matching " +
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

                            checkAliasLength( inputs, vars, className );
                            
                            if ( !inputs[ 0 ].equals( "" ) ) {
                                classRelation.addInputs( inputs, vars );
                            }
                            if ( subtasks.size() != 0 ) {
                                classRelation.addSubtasks( subtasks, vars );
                                classRelation.setType( RelType.TYPE_METHOD_WITH_SUBTASK );
                            }
                            classRelation.setMethod( matcher2.group( 3 ).trim() );
                            if ( RuntimeProperties.isLogDebugEnabled() ) db.p( classRelation );
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
                            if ( RuntimeProperties.isLogDebugEnabled() ) db.p( classRelation );
                            annClass.addClassRelation( classRelation );
                        }
                    } else if ( lt.getType() == LineType.TYPE_ERROR ) {
                        throw new LineErrorException( lt.getSpecLine() );
                    }
                }
            }
        } catch ( UnknownVariableException uve ) {
        	
            //uve.printStackTrace( System.out );
            throw new UnknownVariableException( className + "." + uve.excDesc );

        }
        annClass.addVars( vars );
        classList.add( annClass );
        return classList;
    }

    private static void checkAliasLength( String inputs[], ArrayList<ClassField> vars, String className ) 
    				throws UnknownVariableException {
    	for( int i = 0; i < inputs.length; i++ ) {
        	inputs[i] = checkAliasLength( inputs[i], vars, className ) ;
    	}
    }
    
    private static String checkAliasLength( String input, ArrayList<ClassField> vars, String className ) 
    				throws UnknownVariableException {
    	//check if inputs contain <alias>.lenth variable
    	if( input.endsWith( ".length" ) ) {
    		int index = input.lastIndexOf( ".length" );
    		String aliasName = input.substring( 0, index );
    		ClassField field = ClassRelation.getVar( aliasName, vars );
    		if( field != null && field.isAlias() ) {
    			Alias alias = (Alias)field;
    			String aliasLengthName = 
    				( (alias.isWildcard() ? "*" : "" ) + aliasName + "_LENGTH" );
    			if( varListIncludes( vars, aliasLengthName ) ) {
    				return aliasLengthName;
    			}
    			
    			int length = alias.getVars().size();
    			
    			ClassField var = new ClassField( aliasLengthName, "int", "" + length, true );
    			
    			vars.add( var );
    			
    			return aliasLengthName;
    			
    		}
			throw new UnknownVariableException( "Alias " + aliasName +
					" not found in " + className );
    	}
    	return input;
    }

    private static void getWildCards( ClassList classList, String output ) {
        String list[] = output.split( "\\." );
        for ( int i = 0; i < list.length; i++ ) {
            if ( RuntimeProperties.isLogDebugEnabled() ) db.p( list[ i ] );
        }
    }

    /**
     @return list of fields declared in a specification.
     */
    public static ArrayList<ClassField> getFields( String fileName ) throws IOException {
        ArrayList<ClassField> vars = new ArrayList<ClassField>();
        String s = new String( getStringFromFile( fileName ) );
        ArrayList<String> specLines = getSpec( s, false );
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

    private static boolean isSpecClass( String file ) {
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

    private static boolean varListIncludes( ArrayList<ClassField> vars, String varName ) {
        ClassField cf;

        for ( int i = 0; i < vars.size(); i++ ) {
            cf = vars.get( i );
            if ( cf.getName().equals( varName ) ) {
                return true;
            }
        }
        return false;
    }
}
