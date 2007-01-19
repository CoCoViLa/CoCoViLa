package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.util.*;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import ee.ioc.cs.vsle.vclass.*;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.equations.EquationSolver;
import ee.ioc.cs.vsle.equations.EquationSolver.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

/**
 * This class takes care of parsing the specification and translating it into a graph on which planning can be run.
 * @author Ando Saabas, Pavel Grigorenko
 */
public class SpecParser {

	private SpecParser() {}
	
    public static void main( String[] args ) {

        try {
            //String s = new String( getStringFromFile( args[ 0 ] ) );
            String ss = "class blah /*@ specification blah super ffff, fffdddd, gjjjh { spec } @*/";
            
            ArrayList<String> a = getSpec( ss, false );

            while ( !a.isEmpty() ) {
                if ( !( a.get( 0 ) ).equals( "" ) ) {
                    try {
						db.p( getLine( a ) );
					} catch (SpecParseException e) {
						e.printStackTrace();
					}
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
    	if ( RuntimeProperties.isLogDebugEnabled() ) 
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
    static LineType getLine( ArrayList<String> a ) throws SpecParseException {
        Matcher matcher;
        Pattern pattern;

        while ( ( a.get( 0 ) ).equals( "" ) || a.get( 0 ).trim().startsWith( "//" ) ) {
            a.remove( 0 );
            if ( a.isEmpty() ) {
                return null;
            }
        }
        final String line = a.get( 0 );

        a.remove( 0 );
        if ( line.indexOf( "alias " ) >= 0 ) {
            pattern = Pattern.compile( "alias *(\\(( *[^\\(\\) ]+ *)\\))* *([^= ]+) *= *\\((.*)\\) *" );
            matcher = pattern.matcher( line );
            if ( matcher.find() ) {
            	if( matcher.group( 3 ).indexOf( "." ) > -1 ) {
            		throw new SpecParseException( "Alias " + matcher.group( 3 ) +
                            " cannot be declared with compound name" );
            	}
            	String returnLine = matcher.group( 3 ) 
            						+ ":" + matcher.group( 4 )
            						+ ":" + ( matcher.group( 2 ) == null 
            								|| matcher.group( 2 ).equals("null")? "" : matcher.group( 2 ));
                return new LineType( LineType.TYPE_ALIAS, returnLine, line );
            }
            //allow empty alias declaration e.g. "alias x;"
            pattern = Pattern.compile( "alias *(\\(( *[^\\(\\) ]+ *)\\))* *([^= ]+) *" );
            matcher = pattern.matcher( line );
            if ( matcher.find() ) {
            	if( matcher.group( 3 ).indexOf( "." ) > -1 ) {
            		throw new SpecParseException( "Alias " + matcher.group( 3 ) +
                            " cannot be declared with compound name" );
            	}
            	String returnLine = matcher.group( 3 ) 
            						+ "::" + ( matcher.group( 2 ) == null 
            								|| matcher.group( 2 ).equals("null")? "" : matcher.group( 2 ));
            	return new LineType( LineType.TYPE_ALIAS, returnLine, line );
            }
            
			return new LineType( LineType.TYPE_ERROR, line, line );
			
        } else if ( line.indexOf( "super" ) >= 0 ) {
        	pattern = Pattern.compile( "super#([^ .]+)" );
            matcher = pattern.matcher( line );
            if ( matcher.find() ) {
                String returnLine = matcher.group( 1 );

                return new LineType( LineType.TYPE_SUPERCLASSES, returnLine, line );
            }
			return new LineType( LineType.TYPE_ERROR, line, line );
			
        } else if ( line.trim().startsWith( "const" ) ) {
        	pattern = Pattern.compile( " *([a-zA-Z_$][0-9a-zA-Z_$]*[\\[\\]]*) +([a-zA-Z_$][0-9a-zA-Z_$]*) *= *([a-zA-Z0-9.{}\"]+|new [a-zA-Z0-9.{}\\[\\]]+) *" );
            matcher = pattern.matcher( line );
            if ( matcher.find() ) {
                return new LineType( LineType.TYPE_CONST, matcher.group( 1 ) + ":" + matcher.group( 2 ) + ":" + matcher.group( 3 ), line );
            }
			return new LineType( LineType.TYPE_ERROR, line, line );
			
        } else if ( line.indexOf( "=" ) >= 0 ) { // Extract on solve equations
        	//lets check if it's an alias
        	pattern = Pattern.compile( " *([^= ]+) *= *\\[(.*)\\] *$" );
        	matcher = pattern.matcher( line );
            if ( matcher.find() ) {
            	//TODO here is no check against existance of alias we try to use
            	//"true" means that this is an assignment, not declaration
            	String returnLine = matcher.group( 1 ) + ":" + matcher.group( 2 ) + "::true";
            	
            	return new LineType( LineType.TYPE_ALIAS, returnLine, line );
            }
            
            pattern = Pattern.compile( " *([^= ]+) *= *((\".*\")|(new .*\\(.*\\))|(\\{.*\\})) *$" );
            matcher = pattern.matcher( line );
            if ( matcher.find() ) {
                return new LineType( LineType.TYPE_ASSIGNMENT, matcher.group( 1 ) + ":" + matcher.group( 2 ), line );
            }
			pattern = Pattern.compile( " *([^=]+) *= *([-_0-9a-zA-Z.()\\+\\*/^ ]+) *$" );
			matcher = pattern.matcher( line );
			if ( matcher.find() ) {
			    return new LineType( LineType.TYPE_EQUATION, line, line );
			}
			return new LineType( LineType.TYPE_ERROR, line, line );

        } else if ( line.indexOf( "->" ) >= 0 ) {
            pattern = Pattern.compile( "(.*) *-> *(.+) *\\{(.+)\\}" );
            matcher = pattern.matcher( line );
            if ( matcher.find() ) {
                return new LineType( LineType.TYPE_AXIOM, line, line );
            }
			pattern = Pattern.compile( "(.*) *-> *([ -_a-zA-Z0-9.,]+) *$" );
			matcher = pattern.matcher( line );
			if ( matcher.find() ) {
			    return new LineType( LineType.TYPE_SPECAXIOM, line, line );

			}
			return new LineType( LineType.TYPE_ERROR, line, line );
        }  else {
            pattern = Pattern.compile( "^ *([a-zA-Z_$][0-9a-zA-Z_$]*(\\[\\])*) (([a-zA-Z_$][0-9a-zA-Z_$]* ?, ?)* ?[a-zA-Z_$][0-9a-zA-Z_$]* ?$)" );
            matcher = pattern.matcher( line );
            if ( matcher.find() ) {
                return new LineType( LineType.TYPE_DECLARATION, matcher.group( 1 ) + ":" + matcher.group( 3 ), line );
            }
			return new LineType( LineType.TYPE_ERROR, line, line );
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
                ".*/\\*@.*specification [a-zA-Z_0-9-.]+ ?(super ([ a-zA-Z_0-9-,]+ ))? ?\\{ ?(.+) ?\\} ?@\\*/ ?" );
        matcher = pattern.matcher( fileString );
        if ( matcher.find() ) {
        	String sc = "";
        	if( matcher.group( 2 ) != null ) {
        		sc += "super";
        		String[] superclasses = matcher.group( 2 ).split( "," );
        		for (int i = 0; i < superclasses.length; i++) {
        			String t = superclasses[i].trim();
        			if( t.length() > 0 )
        			sc += "#" + t;
				}
        		sc += ";\n";
        	}
            fileString = sc + matcher.group( 3 );
        }
        return fileString;
    }

    private static ArrayList<String> s_parseErrors = new ArrayList<String>();
    
    public static ClassList parseSpecification( String fullSpec, String path ) throws IOException,
    										SpecParseException, EquationException {
    	s_parseErrors.clear();
    	HashSet<String> hs = new HashSet<String>();
    	return parseSpecificationImpl( refineSpec( fullSpec ), TYPE_THIS, path, hs );
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
    private static ClassList<AnnotatedClass> parseSpecificationImpl( String spec, String className, String path,
                                         HashSet<String> checkedClasses ) throws IOException,
            SpecParseException, EquationException {
        Matcher matcher2;
        Pattern pattern;
        String[] split;
        ArrayList<ClassField> vars = new ArrayList<ClassField>();
        ArrayList<String> subtasks = new ArrayList<String>();
        AnnotatedClass annClass = new AnnotatedClass( className );
        ClassList<AnnotatedClass> classList = new ClassList<AnnotatedClass>();

        ArrayList<String> specLines = getSpec( spec, true );

        try {

            while ( !specLines.isEmpty() ) {
                LineType lt = getLine( specLines );

                if ( lt != null ) {
                	
                	if ( RuntimeProperties.isLogDebugEnabled() ) 
                		db.p( "Parsing: Class " + className + " " + lt );
                	
                	if ( lt.getType() == LineType.TYPE_SUPERCLASSES ) {
                		split = lt.getSpecLine().split( "#", -1 );
                		
                		for (int i = 0; i < split.length; i++) {
							String name = split[i];
							
							File file = new File( path + name + ".java" );

	                        if ( file.exists() && isSpecClass( path, name ) ) {
	                            if ( classList.getType( name ) == null ) {
	                                checkedClasses.add( name );
	                                String s = new String( getStringFromFile( path + name + ".java" ) );

	                                ClassList<AnnotatedClass> superClasses = parseSpecificationImpl( refineSpec( s ), name,
	                                		path, checkedClasses );
	                                checkedClasses.remove( name );
	                                
	                                superClasses.getType( name ).setOnlyForSuperclassGeneration( true );
	                                
	                                for ( AnnotatedClass annCl : superClasses.getSuperClasses() ) {
	                                	annClass.addSuperClass( annCl );
	                                	//annClass.getFields().addAll( annCl.getFields() );
	                                	vars.addAll( annCl.getFields() );
	                                	annClass.getClassRelations().addAll( annCl.getClassRelations() );
	                                	annCl.getFields().clear();
	                                	annCl.getClassRelations().clear();
	                                	
	                                	if( !classList.contains(annCl) ) {
	                                		classList.add( annCl );
	                                	}
									}
	                            }
	                        }
						}
                	} else if ( lt.getType() == LineType.TYPE_ASSIGNMENT ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, lt.getOrigSpecLine() );

                        classRelation.setOutput( split[ 0 ], vars );
                        classRelation.setMethod( split[ 0 ] + " = " + split[ 1 ] );
                        checkAnyType( split[ 0 ], split[ 1 ], vars);
                        annClass.addClassRelation( classRelation );
                        if ( RuntimeProperties.isLogDebugEnabled() ) 
                        	db.p( classRelation );
                        
                    } else if ( lt.getType() == LineType.TYPE_CONST ) {
                    	split = lt.getSpecLine().split( ":", -1 );
                    	String type  = split[ 0 ].trim();
                    	String name  = split[ 1 ].trim();
                    	String value = split[ 2 ].trim();
                    	
                    	if ( varListIncludes( vars, name ) ) {
                    		s_parseErrors.add("Variable " + name +
                                    " declared more than once in class " + className);
                    		
                            throw new SpecParseException( "Variable " + name +
                                    " declared more than once in class " + className );
                        }
                    	
                    	File file = new File( path + type + ".java" );
                    	if( file.exists() && isSpecClass( path, type ) ) {
                    		s_parseErrors.add("Constant " + name + " cannot be of type " + type);
                    		throw new SpecParseException( "Constant " + name +
                                    " cannot be of type " + type );
                    	}
                    	if ( RuntimeProperties.isLogDebugEnabled() ) 
                    		db.p( "---===!!! " + type + " " + name + " = " + value );
                    	
                    	ClassField var = new ClassField( name, type, value, true );

                        vars.add( var );
                    	
                    } else if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        String[] vs = split[ 1 ].trim().split( " *, *", -1 );
                        String type = split[ 0 ].trim();

                        if ( RuntimeProperties.isLogDebugEnabled() ) 
                        	db.p( "Checking existence of " + path + type + ".java" );
                        if ( checkedClasses.contains( type ) ) {
                            throw new MutualDeclarationException( className + " <-> " + type );
                        }
                        File file = new File( path + type + ".java" );
                        boolean specClass = false;

                        // if a file by this name exists in the package directory and it includes a specification, we're gonna check it
                        if ( file.exists() && isSpecClass( path, type ) ) {
                            specClass = true;
                            if ( classList.getType( type ) == null ) {
                                checkedClasses.add( type );
                                String s = new String( getStringFromFile( path + type + ".java" ) );

                                classList.addAll( parseSpecificationImpl( refineSpec( s ), type, path,
                                        checkedClasses ) );
                                checkedClasses.remove( type );
                                specClass = true;
                            }
                        }
                        for ( int i = 0; i < vs.length; i++ ) {
                            if ( varListIncludes( vars, vs[ i ] ) ) {
                            	s_parseErrors.add( "Variable " + vs[ i ] +
                                        " declared more than once in class " + className );
                                throw new SpecParseException( "Variable " + vs[ i ] +
                                        " declared more than once in class " + className );
                            }
                            ClassField var = new ClassField( vs[ i ], type, specClass );

                            vars.add( var );
                        }

                    } else if ( lt.getType() == LineType.TYPE_ALIAS ) {
                        split = lt.getSpecLine().split( ":", -1 );
                        String name = split[ 0 ];
                        
                        Alias a;
                        
                        if( split[ 1 ].trim().equals("") ) {
                        	//if there are no variables on the rhs, mark alias as empty
                        	if ( !varListIncludes( vars, name ) ) {
                        		a = new Alias( name );
                        		if( !split[ 2 ].trim().equals("") )
                        		{
                        			a.setStrictTypeOfVars( split[ 2 ].trim() );
                        		}
                        		vars.add( a );
                        		continue;
                        	}
                        }
                        
                        String[] list = split[ 1 ].trim().split( " *, *", -1 );
                        
                        ClassField var = ClassRelation.getVar( name, vars );
                        
                        if ( var != null && !var.isAlias() ) {
                        	s_parseErrors.add( "Variable " + name +
                                    " declared more than once in class " + className );
                            throw new SpecParseException( "Variable " + name +
                                    " declared more than once in class " + className );
                        } else if ( var != null && var.isAlias() ) {
                        	a = (Alias)var;
                        	if( !a.isEmpty() ) {
                        		throw new SpecParseException( "Alias " + name +
                                        " cannot be overriden, class " + className );
                        	}
                        } else {
                        	//if its an assignment, check if alias has already been declared
                        	if( split.length > 3 && Boolean.parseBoolean( split[ 3 ] ) ) {
                        		try {
                        			if( ( name.indexOf( "." ) == -1 ) && !varListIncludes( vars, name ) ) {
                        				throw new Exception();
                        				
                        			} else if ( name.indexOf( "." ) > -1 ) {
                        				//here we have to dig deeply
                        				int ind = name.indexOf( "." );
                        				
                        				String parent = name.substring( 0, ind );
                        				String leftFromName = name.substring( ind + 1, name.length() );
                        				
                        				ArrayList<ClassField> varsFromClass = vars;
                        				ClassField parentVar = ClassRelation.getVar( parent, varsFromClass );
                        				String parentType = parentVar.getType();
                        				
                        				AnnotatedClass parentClass = classList.getType( parentType );
                        				
                        				while( leftFromName.indexOf( "." ) > -1 ) {
                        					
                        					ind = leftFromName.indexOf( "." );
                        					parent = leftFromName.substring( 0, ind );
                        					leftFromName = leftFromName.substring( ind + 1, leftFromName.length() );
                        					
                        					parentVar = parentClass.getFieldByName( parent );
                        					
                        					parentType = parentVar.getType();
                        					parentClass = classList.getType( parentType );
                        				}
                        				
                        				if( !parentClass.hasField(leftFromName) ) {
                        					throw new Exception( "Variable " + leftFromName + " is not declared in class " + parentClass );
                        				}
                        			}
                        		} catch( Exception e) {
                        			throw new SpecParseException( "Alias " + name +
                        					" is not declared, class " + className + ( e.getMessage() != null ? "\n" + e.getMessage() : "" ) );
                        		}
                        	}
                        	//if everything is ok, create alias
                        	a = new Alias( name );
                        	
                        	if( !split[ 2 ].trim().equals("") )
                    		{
                    			a.setStrictTypeOfVars( split[ 2 ].trim() );
                    		}
                        }

                        a.addAll( list, vars, classList );
                        vars.add( a );
                        ClassRelation classRelation = new ClassRelation( RelType.TYPE_ALIAS, lt.getOrigSpecLine() );

                        classRelation.addInputs( list, vars );
                        classRelation.setMethod( TypeUtil.TYPE_ALIAS );
                        classRelation.setOutput( name, vars );
                        annClass.addClassRelation( classRelation );
                        
                        if ( RuntimeProperties.isLogDebugEnabled() ) 
                        	db.p( classRelation );
                        
                        if ( !a.isWildcard() ) {
                            classRelation = new ClassRelation( RelType.TYPE_ALIAS, lt.getOrigSpecLine() );
                            classRelation.addOutputs( list, vars );
                            classRelation.setMethod( TypeUtil.TYPE_ALIAS );
                            classRelation.setInput( name, vars );
                            annClass.addClassRelation( classRelation );
                            if ( RuntimeProperties.isLogDebugEnabled() ) db.p( classRelation );
                        } 
                        //wildcard flag will be set inside alias
                        //else { a.setWildcard( true ); }

                    } else if ( lt.getType() == LineType.TYPE_EQUATION ) {
                        EquationSolver.solve( lt.getSpecLine() );
                        next: 
                        for ( Relation rel : EquationSolver.getRelations() ) {
                        	if ( RuntimeProperties.isLogDebugEnabled() ) db.p( "equation: " + rel );
                            String[] pieces = rel.getRel().split( ":" );
                            String method = rel.getExp();
                            String out = pieces[ 2 ].trim();
                            
                            //cannot assign new values for constants
                            ClassField tmp = ClassRelation.getVar( checkAliasLength( out, vars, className ), vars );
                            if( tmp != null && tmp.isConstant() ) {
                            	db.p( "Ignoring constant and equation output: " + tmp );
                            	continue;
                            }
                            //if one variable is used on both sides of "=", we cannot use such relation.
                            String[] inputs = pieces[ 1 ].trim().split( " " );
                            for (int j = 0; j < inputs.length; j++) {
								if( inputs[j].equals(out) ) {
									if ( RuntimeProperties.isLogDebugEnabled() ) 
										db.p( " - unable use this equation because variable " 
												+ out + " appears on both sides of =" );
									continue next;
								}
							}
                            
                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_EQUATION, lt.getOrigSpecLine() );

                            classRelation.setOutput( out, vars );

                            //checkAliasLength( inputs, vars, className );
                            for( int i = 0; i < inputs.length; i++ ) {
                            	String initial = inputs[i];
                            	inputs[i] = checkAliasLength( inputs[i], vars, className );
                            	String name = inputs[i];
                            	if( name.startsWith( "*" ) ) {
                            		name = inputs[i].substring( 1 );
                            	}
                            	method = method.replaceAll( "\\$" + initial + "\\$", name );
                        	}
                            method = method.replaceAll( "\\$" + out + "\\$", out );
                            
                            checkAnyType( out, inputs, vars);
                            
                            if ( !inputs[ 0 ].equals( "" ) ) {
                                classRelation.addInputs( inputs, vars );
                            }
                            classRelation.setMethod( method );
                            annClass.addClassRelation( classRelation );
                            if ( RuntimeProperties.isLogDebugEnabled() ) 
                            	db.p( "Equation: " + classRelation );

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
                                "\\[([^\\]\\[]*) *-> *([^\\]\\[]*)\\]", "#" ), lt.getOrigSpecLine() );

                        pattern = Pattern.compile( "(.*) *-> ?(.*)\\{(.*)\\}" );
                        matcher2 = pattern.matcher( lt.getSpecLine() );
                        if ( matcher2.find() ) {

                            String[] outputs = matcher2.group( 2 ).trim().split( " *, *", -1 );

                            if ( !outputs[ 0 ].equals( "" ) ) {
                                if ( outputs[ 0 ].indexOf( "*" ) >= 0 ) {
                                    getWildCards( classList, outputs[ 0 ] );
                                }

                            }
                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_JAVAMETHOD, lt.getOrigSpecLine() );

                            if ( matcher2.group( 2 ).trim().equals( "" ) ) {
                            	s_parseErrors.add("Error in line \n" + lt.getOrigSpecLine() +
                                        "\nin class " + className +
                                        ".\nAn axiom can not have an empty output.");
                                throw new SpecParseException( "Error in line \n" + lt.getOrigSpecLine() +
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
                            ClassRelation classRelation = new ClassRelation( RelType.TYPE_UNIMPLEMENTED, lt.getOrigSpecLine() );
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
                        throw new LineErrorException( lt.getOrigSpecLine() );
                    }
                }
            }
        } catch ( UnknownVariableException uve ) {
        	
            throw new UnknownVariableException( className + "." + uve.excDesc );

        }
        annClass.addVars( vars );
        classList.add( annClass );
        return classList;
    }

    private static void checkAnyType( String output, String input, ArrayList<ClassField> vars ) throws UnknownVariableException {
    	checkAnyType( output, new String[]{ input }, vars );
    }
    
    //TODO - implement _any_!!!
    private static void checkAnyType( String output, String[] inputs, ArrayList<ClassField> vars ) throws UnknownVariableException {
    	ClassField out = ClassRelation.getVar( output, vars );
    	
    	if( out == null || !out.getType().equals(TYPE_ANY) ) {
    		return;
    	}
    	
    	String newType = TYPE_ANY;
    	
    	for (int i = 0; i < inputs.length; i++) {
    		ClassField in = ClassRelation.getVar( inputs[i], vars );
    		
    		if( in == null ) {
    			try {
    				Integer.parseInt( inputs[i] );
    				newType = TYPE_INT;
    				continue;
    			} catch( NumberFormatException ex ) {}
    			
    			try {
    				Double.parseDouble( inputs[i] );
    				newType = TYPE_DOUBLE;
    				continue;
    			} catch( NumberFormatException ex ) {}
    			
    			if( inputs[i] != null && inputs[i].trim().equals("") ) {
    				newType = TYPE_DOUBLE;//TODO - tmp
    				continue;
    			}
    			
    			throw new UnknownVariableException( inputs[i] );
    		}
    		if( i == 0 ) {
    			newType = in.getType();
    			continue;
    		}
    		TypeToken token = TypeToken.getTypeToken( newType );
    		
    		TypeToken tokenIn = TypeToken.getTypeToken( in.getType() );
    		
    		if( token != null && tokenIn != null && token.compareTo( tokenIn ) < 0 ) {
    			newType = in.getType();
    		}
		}
    	
    	out.setType( newType );
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
    			
    			ClassField var = new ClassField( aliasLengthName, TYPE_INT, "" + length, true );
    			
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
        ArrayList<ClassField> fields = new ArrayList<ClassField>();
        String s = new String( getStringFromFile( fileName ) );
        ArrayList<String> specLines = getSpec( s, false );
        String[] split;

        while ( !specLines.isEmpty() ) {
            LineType lt = null;
			try {
				lt = getLine( specLines );
			} catch (SpecParseException e) {
				e.printStackTrace();
			}

            if ( lt != null ) {
                if ( lt.getType() == LineType.TYPE_ASSIGNMENT ) {
                    split = lt.getSpecLine().split( ":", -1 );
                    for ( int i = 0; i < fields.size(); i++ ) {
                        if ( fields.get( i ).getName().equals( split[ 0 ] ) ) {
                            fields.get( i ).setValue( split[ 1 ] );
                        }
                    }
                } else if ( lt.getType() == LineType.TYPE_DECLARATION ) {
                    split = lt.getSpecLine().split( ":", -1 );
                    String[] vs = split[ 1 ].trim().split( " *, *", -1 );
                    String type = split[ 0 ].trim();

                    for ( int i = 0; i < vs.length; i++ ) {
                        ClassField var = new ClassField( vs[ i ], type );

                        fields.add( var );
                    }
                }
            }
        }
        return fields;
    }

    private static boolean isSpecClass( String path, String file ) {
        try {
            BufferedReader in = new BufferedReader( new FileReader( path + file + ".java" ) );
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
