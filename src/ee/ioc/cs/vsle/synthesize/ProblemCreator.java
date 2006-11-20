package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class ProblemCreator {

	private ProblemCreator() {}
	
	static Problem makeProblem( ClassList classes ) throws SpecParseException{
    	return makeProblemImpl( classes, TYPE_THIS, TYPE_THIS, new Problem() );
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
    private static Problem makeProblemImpl( ClassList classes, String type, String caller, Problem problem ) throws
            SpecParseException {

    	AnnotatedClass ac = classes.getType( type );

        for ( ClassField cf : ac.getFields() ) {
        	if( ac.isOnlyForSuperclassGeneration() ) continue;
        	
            if ( classes.getType( cf.getType() ) != null ) {
                problem = makeProblemImpl( classes, cf.getType(), caller + "." + cf.getName(), problem );
            }
            if ( cf.isAlias() ) {
                createAlias( (Alias)cf, ac, classes, problem, caller );
                continue;
            } else if( cf.isConstant() && cf.getName().startsWith( "*" ) ) {
            	//this denotes for alias.length constant
            	Alias alias = (Alias)ac.getFieldByName( cf.getName().substring( 0, cf.getName().length() - 7 ).substring( 1 ) );
            	cf.setName( cf.getName().substring( 1 ) );
            	cf.setValue( "" + alias.getVars().size() );
            }
            
            Var var = new Var( cf, caller );
            
            problem.addVar( var );
            if( cf.isConstant() ) {
            	problem.getKnownVars().add( var );
            	problem.getFoundVars().add( var );
            }
        }

        for ( ClassRelation classRelation : ac.getClassRelations() ) {

            String obj = caller;

            Rel rel = new Rel();
            HashSet<Rel> relSet = null;
            
            boolean isAliasRel = false;

            /* If we have a relation alias = alias, we rewrite it into new relations, ie we create
             a relation for each component of the alias structure*/
            if ( classRelation.getInputs().size() == 1 && classRelation.getOutputs().size() == 1 &&
                 ( classRelation.getType() == RelType.TYPE_ALIAS || classRelation.getType() == RelType.TYPE_EQUATION ) ) {
                ClassField inpField = classRelation.getInputs().get( 0 );
                ClassField outpField = classRelation.getOutputs().get( 0 );

                //if we have a relation alias = *.sth, we need to find out what it actually is
                if ( inpField.getName().startsWith( "*." ) ) {
                    String s = checkIfAliasWildcard( classRelation );
                    if ( s != null ) {
                        relSet = makeAliasWildcard( ac, classes, classRelation, problem, obj, s );
                        rel = null;
                        isAliasRel = true;
                    }
                }

                //the following is for x = y, not x -> y relation where both x and y are aliases
                if ( ( classRelation.getType() == RelType.TYPE_EQUATION ) 
                		&& problem.getAllVars().containsKey( obj + "." + inpField.getName() ) ) {
                	
                    Var inpVar = problem.getAllVars().get( obj + "." + inpField.getName() );
                    Var outpVar = problem.getAllVars().get( obj + "." + outpField.getName() );

                    if ( inpVar.getField().isAlias() && outpVar.getField().isAlias() ) {

                        if ( !( ( Alias ) inpVar.getField() ).equalsByTypes( ( Alias ) outpVar.
                                getField() ) ) {
                            throw new AliasException( "Differently typed aliases connected: " + obj +
                                    "." + inpField.getName() + " and " + obj + "." + outpField.getName() );
                        }
                        isAliasRel = true;
                        Var inpChildVar, outpChildVar;
                        for ( int i = 0; i < inpVar.getChildVars().size(); i++ ) {
                        	inpChildVar = inpVar.getChildVars().get( i );
                        	outpChildVar = outpVar.getChildVars().get( i );

                            rel = new Rel();
                            rel.setUnknownInputs( classRelation.getInputs().size() );
                            rel.setObj( obj );
                            rel.setType( RelType.TYPE_ALIAS );

                            rel.addInput( outpChildVar );
                            rel.addOutput( inpChildVar );
                            outpChildVar.addRel( rel );
                            problem.addRel( rel );
                        }
                    }
                }
            }

            if ( !isAliasRel ) {
            	
//                String s = checkIfRightWildcard( classRelation );
//                if ( s != null ) {
//                    relSet = makeRightWildcardRel( ac, classes, classRelation, problem, obj, s );
//                    rel = null;
//                } else {
                	rel = makeRel( new Rel(), classRelation, problem, obj );
//                }
                if ( classRelation.getSubtasks().size() > 0 ) {

                    for ( ClassRelation subtask : classRelation.getSubtasks() ) {
                    	
                        SubtaskRel subtaskRel = new SubtaskRel( rel );
                        
                        makeRel( subtaskRel, subtask, problem, obj );
                        
                        rel.addSubtask( subtaskRel );
                        
                        problem.addSubtask( subtaskRel );
//                        else {
//                            for ( Rel r : relSet ) {
//                                r.addSubtask( subtaskRel );
//                            }
//                        }
                    }
                }
            }

            // if it is not a "real" relation (type 7), we just set the result as target, and inputs as known variables
            if ( classRelation.getType() == RelType.TYPE_UNIMPLEMENTED ) {
                setTargets( problem, classRelation, obj );
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
        //System.out.println(problem);
        return problem;
    }
    
    private static void createAlias( Alias alias, AnnotatedClass ac, ClassList classes, Problem problem, String object ) throws AliasException {
    	
    	Var var = new Var( alias, object );
    	
    	if( alias.isWildcard() ) {
    		rewriteWildcardAlias( var, ac, classes, problem );
    	} else {
    		for ( ClassField childField : alias.getVars() ) {
    			Var childVar = problem.getAllVars().get( object + "." + childField.getName() );
    			
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
    					
    					String absoluteName = aliasVar.getObject() + "." + clf + "." + field.getName();
    					
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

    private static String checkIfAliasWildcard( ClassRelation classRelation ) {
        String s = classRelation.getInputs().get( 0 ).getName();
        if ( s.startsWith( "*." ) )
            return s.substring( 2 );
        return null;
    }
    
    /**
     * creates Rel for alias x = ( *.wildcardVar );
     */
    private static HashSet<Rel> makeAliasWildcard( AnnotatedClass ac, ClassList classes, ClassRelation classRelation,
    		Problem problem, String obj, String wildcardVar ) throws
    		UnknownVariableException {
    	HashSet<Rel> relset = new HashSet<Rel>();
    	Rel rel = new Rel();
    	rel.setMethod( classRelation.getMethod() );
    	rel.setObj( obj );
    	rel.setType( classRelation.getType() );
    	
    	ClassField clf;
        
    	for ( int i = 0; i < ac.getFields().size(); i++ ) {
    		clf = ac.getFields().get( i );
    		AnnotatedClass anc = classes.getType( clf.getType() );
    		if ( anc != null ) {
    			if ( anc.hasField( wildcardVar ) ) {
    				String varName = obj + "." + clf.getName() + "." + wildcardVar;
    				Var var;
    				if ( problem.getAllVars().containsKey( varName ) ) {
    					var = problem.getAllVars().get( varName );
    					var.addRel( rel );
    					rel.addInput( var );
    				} else {
    					throw new UnknownVariableException( varName );
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
    	for ( int i = 0; i < ac.getFields().size(); i++ ) {
    		clf = ac.getFields().get( i );
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

   private static Rel makeRel( Rel rel, ClassRelation classRelation, Problem problem, String obj ) throws
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
		   Problem problem, String obj, String wildcardVar ) throws
		   UnknownVariableException {
	   
	   //temporaly disable
	   if( true ) {
		   throw new UnknownVariableException( "*." + wildcardVar );
	   }
	   ClassField clf;
	   HashSet<Rel> set = new HashSet<Rel>();
	   for ( int i = 0; i < ac.getFields().size(); i++ ) {
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
	   }
	   return set;
   }
   
   /**
   In case of a goal specification is included (eg a -> b), the right hand side is added to problem
   targets, left hand side is added to known variables.
   @param problem problem to be changed
   @param classRelation the goal specification is extracted from it.
   @param obj the name of the object where the goal specification was declared.
   */
  private static void setTargets( Problem problem, ClassRelation classRelation, String obj ) throws
          UnknownVariableException {
      Var var;
      ClassField cf;

      for ( int k = 0; k < classRelation.getInputs().size(); k++ ) {
          cf = classRelation.getInputs().get( k );
          if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
              var = problem.getAllVars().get( obj + "." + cf.getName() );
              problem.getKnownVars().add( var );
              problem.getFoundVars().add( var );
              problem.getAssumptions().add( var );
          } else {
              throw new UnknownVariableException( cf.getName() );
          }
      }
      for ( int k = 0; k < classRelation.getOutputs().size(); k++ ) {
          cf = classRelation.getOutputs().get( k );
          if ( problem.getAllVars().containsKey( obj + "." + cf.getName() ) ) {
              var = problem.getAllVars().get( obj + "." + cf.getName() );
              problem.addGoal( var );
          } else {
              throw new UnknownVariableException( cf.getName() );
          }
      }

  }
}
