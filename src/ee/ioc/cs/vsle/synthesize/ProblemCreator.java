package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class ProblemCreator {

	private ProblemCreator() {}
	
	static Problem makeProblem( ClassList classes ) throws SpecParseException {
		
		Problem problem = new Problem( new Var( new ClassField( TYPE_THIS, TYPE_THIS ), null ) );
		
    	makeProblemImpl( classes, problem.getRootVar(), problem );
    	
    	return problem;
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
    private static void makeProblemImpl( ClassList classes, Var parent, Problem problem ) throws
            SpecParseException {

    	List<Alias> aliases = new ArrayList<Alias>();
    	List<Var> aliasLengths = new ArrayList<Var>();
    	
    	AnnotatedClass ac = classes.getType( parent.getType() );
    	
        for ( ClassField cf : ac.getFields() ) {
        	if( ac.isOnlyForSuperclassGeneration() ) continue;
        	
        	if ( cf.isAlias() ) {
                aliases.add( (Alias)cf );
                continue;
            }
        	
        	Var var = new Var( cf, parent );
            
            problem.addVar( var );
            
            if ( classes.getType( cf.getType() ) != null ) {
            	
                makeProblemImpl( classes, var, problem );
                continue;
            }
            else if( cf.isConstant() && cf.getName().startsWith( "*" ) ) {
            	//this denotes for alias.length constant
            	aliasLengths.add( var );
            }
            
            if( cf.isConstant() ) {
            	problem.getKnownVars().add( var );
            	problem.getFoundVars().add( var );
            }
        }
        
        //make aliases when all other vars have been created
        for ( Alias alias : aliases) {
        	createAlias( alias, ac, classes, problem, parent );
		}
        
        for ( Var length : aliasLengths ) {
        	problem.getAllVars().remove( length.getFullName() );
        	String name = length.getName();
        	Alias alias = (Alias)ac.getFieldByName( name.substring( 0, name.length() - 7 ).substring( 1 ) );
        	length.getField().setName( name.substring( 1 ) );
        	length.getField().setValue( "" + alias.getVars().size() );
        	
            problem.addVar( length );
		}
        
        for ( ClassRelation classRelation : ac.getClassRelations() ) {

            Rel rel = new Rel( parent, classRelation.getSpecLine() );
            HashSet<Rel> relSet = null;
            
            boolean isAliasRel = false;

            /* If we have a relation alias = alias, we rewrite it into new relations, ie we create
             a relation for each component of the alias structure*/
            if ( classRelation.getInputs().size() == 1 && classRelation.getOutputs().size() == 1 &&
                 ( classRelation.getType() == RelType.TYPE_ALIAS || classRelation.getType() == RelType.TYPE_EQUATION ) ) {
                ClassField inpField = classRelation.getInputs().get( 0 );
                ClassField outpField = classRelation.getOutputs().get( 0 );

                //if we have a relation alias = *.sth, we need to find out what it actually is
                if ( isAliasWildcard( classRelation ) ) {
                	relSet = makeAliasWildcard( ac, classes, classRelation, problem, parent );
                	rel = null;
                	isAliasRel = true;
                }
                String obj = parent.getFullNameForConcat();
                //the following is for x = y, not x -> y relation where both x and y are aliases
                if ( ( classRelation.getType() == RelType.TYPE_EQUATION ) 
                		&& problem.getAllVars().containsKey( obj + inpField.getName() ) ) {
                	
                    Var inpVar = problem.getAllVars().get( obj + inpField.getName() );
                    Var outpVar = problem.getAllVars().get( obj + outpField.getName() );

                    if ( inpVar.getField().isAlias() && outpVar.getField().isAlias() ) {

                        if ( !( ( Alias ) inpVar.getField() ).equalsByTypes( ( Alias ) outpVar.
                                getField() ) ) {
                            throw new AliasException( "Differently typed aliases connected: " 
                            		+ obj + inpField.getName() + " and " + obj + outpField.getName() );
                        }
                        isAliasRel = true;
                        Var inpChildVar, outpChildVar;
                        for ( int i = 0; i < inpVar.getChildVars().size(); i++ ) {
                        	inpChildVar = inpVar.getChildVars().get( i );
                        	outpChildVar = outpVar.getChildVars().get( i );

                            rel = new Rel( parent, classRelation.getSpecLine() );
                            rel.setUnknownInputs( classRelation.getInputs().size() );
                            rel.setType( RelType.TYPE_EQUATION );

                            rel.addInput( outpChildVar );
                            rel.addOutput( inpChildVar );
                            outpChildVar.addRel( rel );
                            problem.addRel( rel );
                        }
                    }
                }
            }

            if ( !isAliasRel ) {
            	rel = makeRel( new Rel( parent, classRelation.getSpecLine() ), classRelation, problem, parent );
            	
                if ( classRelation.getSubtasks().size() > 0 ) {

                    for ( ClassRelation subtask : classRelation.getSubtasks() ) {
                    	
                        SubtaskRel subtaskRel = new SubtaskRel( rel, parent, subtask.getSpecLine() );
                        
                        makeRel( subtaskRel, subtask, problem, parent );
                        
                        rel.addSubtask( subtaskRel );
                        
                        problem.addSubtask( subtaskRel );
                    }
                }
            }

            // if it is not a "real" relation (type 7), we just set the result as target, and inputs as known variables
            if ( classRelation.getType() == RelType.TYPE_UNIMPLEMENTED ) {
                setTargets( problem, classRelation, parent );
            } 
            // if class relation doesnt have inputs, its an axiom
            else if ( rel != null && classRelation.getInputs().isEmpty() &&
                        rel.getSubtasks().size() == 0 ) { 
                problem.addAxiom( rel );
                problem.getKnownVars().addAll( rel.getOutputs() );
            }
            else if ( rel != null ) {
            	problem.addRel( rel );
            	
            	if( rel.getSubtasks().size() > 0 ) {
            		problem.addRelWithSubtask( rel );
            	}
            	
            } 
            else if( relSet != null ) {
            	problem.addAllRels( relSet );
            }

        }
    }
    
    private static void createAlias( Alias alias, AnnotatedClass ac, ClassList classes, Problem problem, Var parent ) throws AliasException {
    	
    	Var var = new Var( alias, parent );
    	
    	if( alias.isWildcard() ) {
    		rewriteWildcardAlias( var, ac, classes, problem );
    	} else {
    		for ( ClassField childField : alias.getVars() ) {
    			Var childVar = problem.getAllVars().get( parent.getFullNameForConcat() + childField.getName() );
    			
    			if( childVar != null ) {
    				var.addVar( childVar );
    			}
			}
    	}
        
        problem.addVar( var );
    }
    
    private static void rewriteWildcardAlias( Var aliasVar, AnnotatedClass ac, ClassList classes, Problem problem ) throws AliasException {
    	
    	String wildcardVar = ((Alias)aliasVar.getField()).getWildcardVar();

    	ClassField clf;

    	for ( int i = 0; i < ac.getFields().size(); i++ ) {
    		clf = ac.getFields().get( i );
    		//in the following AnnotatedClass we look for vars that match wildcard
    		AnnotatedClass anc = classes.getType( clf.getType() );
    		if ( anc != null ) {
    			//this field matches
    			ClassField field = anc.getFieldByName( wildcardVar );
    			if ( field != null ) {

    				if( !((Alias)aliasVar.getField()).isStrictType() 
    						|| ((Alias)aliasVar.getField()).getVarType().equals( field.getType() ) ) {
    					
    					String absoluteName = aliasVar.getParent().getFullNameForConcat() + clf + "." + field.getName();
    					
    					Var var = problem.getAllVars().get( absoluteName );
    					
    					if( var != null ) {
    						aliasVar.addVar( var );
    						((Alias)aliasVar.getField()).addVar( var.getField() );
    					}
    				}
    			}
    		}
    	}
    }
    
    private static int getUniqueInputCount( ArrayList<ClassField> inputs ) {
    	
    	HashSet<ClassField> inps = new HashSet<ClassField>();
    	
    	int size = 0;
    	
    	for ( ClassField object : inputs ) {
			if( inps.contains( object ) ) {
				continue;
			}
			size++;
			inps.add( object );
		}
    	
    	return size;
    }

    private static boolean isAliasWildcard( ClassRelation classRelation ) {
    	
    	return classRelation.getInputs().size() == 1 && classRelation.getInputs().get( 0 ).getName().startsWith( "*." );
    }
    
    /**
     * creates Rel for alias x = ( *.wildcardVar );
     */
    private static HashSet<Rel> makeAliasWildcard( AnnotatedClass ac, ClassList classes, ClassRelation classRelation,
    		Problem problem, Var parentObj ) throws UnknownVariableException {
    	
    	Var alias = problem.getAllVars().get( parentObj.getFullNameForConcat() + classRelation.getOutputs().get( 0 ).getName() );
    	
    	if( alias == null ) {
    		throw new UnknownVariableException( parentObj.getFullNameForConcat() + classRelation.getOutputs().get( 0 ).getName() );
    	}
    	
    	Rel relAliasOutp = new Rel( parentObj, classRelation.getSpecLine() );
    	relAliasOutp.setMethod( classRelation.getMethod() );
    	relAliasOutp.setType( classRelation.getType() );
    	relAliasOutp.getInputs().addAll( alias.getChildVars() );
    	relAliasOutp.addOutput( alias );
    	relAliasOutp.setUnknownInputs( relAliasOutp.getInputs().size() );
    	alias.addRel(relAliasOutp);
    	for ( Var childVar : alias.getChildVars() ) {
    		childVar.addRel(relAliasOutp);
		}
    	
    	Rel relAliasInp = new Rel( parentObj, classRelation.getSpecLine() );
    	relAliasInp.setMethod( classRelation.getMethod() );
    	relAliasInp.setType( classRelation.getType() );
    	relAliasInp.getOutputs().addAll( alias.getChildVars() );
    	relAliasInp.addInput( alias );
    	relAliasInp.setUnknownInputs( relAliasInp.getInputs().size() );
    	alias.addRel(relAliasInp);
    	
    	HashSet<Rel> relset = new HashSet<Rel>();
    	relset.add( relAliasOutp );
    	relset.add( relAliasInp );
    	
    	return relset;
    }
    
    /**
    creates a relation that will be included in the problem.
    @param problem that will include relation (its needed to get variable information from it)
    @param classRelation the implementational information about this relation
    @param obj the name of the object where the goal specification was declared.
    */

   private static Rel makeRel( Rel rel, ClassRelation classRelation, Problem problem, Var parentVar ) throws
           UnknownVariableException {
       Var var;

       rel.setMethod( classRelation.getMethod() );
       int constants = 0;
       //fist, count constants
       //second, if we deal with equation and one variable is used on both sides of "=", we cannot use it.
       for (ClassField field : classRelation.getInputs() ) {
			if( field.isConstant() ) {
				constants++;
			}
			if( classRelation.getType() == RelType.TYPE_EQUATION && classRelation.getOutputs().contains( field ) ) {
				return null;
			}
		}
       
       rel.setUnknownInputs( getUniqueInputCount( classRelation.getInputs() ) - constants );
       rel.setType( classRelation.getType() );
       ClassField cf;
       
       for ( int k = 0; k < classRelation.getInputs().size(); k++ ) {
           cf = classRelation.getInputs().get( k );
           String varName = parentVar.getFullNameForConcat() + cf.getName();
           if ( problem.getAllVars().containsKey( varName ) ) {
               var = problem.getAllVars().get( varName );
               var.addRel( rel );
               rel.addInput( var );
           } else {
               throw new UnknownVariableException( varName );
           }
       }
       for ( int k = 0; k < classRelation.getOutputs().size(); k++ ) {
           cf = classRelation.getOutputs().get( k );
           String varName = parentVar.getFullNameForConcat() + cf.getName();
           if ( problem.getAllVars().containsKey( varName ) ) {
               var = problem.getAllVars().get( varName );
               rel.addOutput( var );
           } else {
               throw new UnknownVariableException( varName );
           }
       }
       for ( int k = 0; k < classRelation.getExceptions().size(); k++ ) {
           cf = classRelation.getExceptions().get( k );
           Var ex = new Var( cf, null );
           rel.getExceptions().add( ex );
       }

       return rel;
   }
   
   /**
    * @deprecated
    * @param classRelation
    * @return
    */
   private static String checkIfRightWildcard( ClassRelation classRelation ) {
       String s = classRelation.getOutputs().get( 0 ).getName();
       if ( s.startsWith( "*." ) )
           return s.substring( 2 );
       return null;
   }
   
   /**
    * creates set of Rels for x -> *.y { impl }; - does not work currently
    * @deprecated
    */
   private static HashSet<Rel> makeRightWildcardRel( AnnotatedClass ac, ClassList classes, ClassRelation classRelation,
		   Problem problem, Var parentVar, String wildcardVar ) throws
		   UnknownVariableException {
	   
	   //temporaly disable
	   if( true ) {
		   throw new UnknownVariableException( "*." + wildcardVar );
	   }
	   ClassField clf;
	   HashSet<Rel> set = new HashSet<Rel>();
	   /*for ( int i = 0; i < ac.getFields().size(); i++ ) {
		   clf = ac.getFields().get( i );
		   AnnotatedClass anc = classes.getType( clf.getType() );
		   if ( anc != null ) {
			   if ( anc.hasField( wildcardVar ) ) {
				   
				   Var var;
				   Rel rel = new Rel();
				   
				   rel.setMethod( classRelation.getMethod() );
				   rel.setUnknownInputs( classRelation.getInputs().size() );
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
	   }*/
	   return set;
   }
   
   /**
   In case of a goal specification is included (eg a -> b), the right hand side is added to problem
   targets, left hand side is added to known variables.
   @param problem problem to be changed
   @param classRelation the goal specification is extracted from it.
   @param obj the name of the object where the goal specification was declared.
   */
  private static void setTargets( Problem problem, ClassRelation classRelation, Var parent ) throws
          UnknownVariableException {
	  
      String obj = parent.getFullNameForConcat();
      
      for ( ClassField cf : classRelation.getInputs() ) {
          String varName = obj + cf.getName();
          if ( problem.getAllVars().containsKey( varName ) ) {
        	  Var var = problem.getAllVars().get( varName );
              problem.getKnownVars().add( var );
              problem.getFoundVars().add( var );
              problem.getAssumptions().add( var );
          } else {
              throw new UnknownVariableException( cf.getName() );
          }
      }
      
      for ( ClassField cf : classRelation.getOutputs() ) {
          String varName = obj + cf.getName();
          if ( problem.getAllVars().containsKey( varName ) ) {
        	  Var var = problem.getAllVars().get( varName );
              problem.addGoal( var );
          } else {
              throw new UnknownVariableException( cf.getName() );
          }
      }

  }
}
