package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

public class ProblemCreator {

	private ProblemCreator() {}
	
	static Problem makeProblem( ClassList classes ) throws SpecParseException{
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
    private static Problem makeProblemImpl( ClassList classes, String type, String caller, Problem problem ) throws
            SpecParseException {
        // ee.ioc.cs.editor.util.db.p("CLASSES: "+classes);
        // ee.ioc.cs.editor.util.db.p("TYPE: "+type);
        AnnotatedClass ac = classes.getType( type );
        ClassField cf = null;
        ClassRelation classRelation;
        Var var, var1, var2;
        Rel rel;
        HashSet<Rel> relSet = new HashSet<Rel>();

        for ( int j = 0; j < ac.getFields().size(); j++ ) {
        	if( ac.isOnlyForSuperclassGeneration() ) continue;
        	
            cf = ac.getFields().get( j );
            if ( classes.getType( cf.getType() ) != null ) {
                problem = makeProblemImpl( classes, cf.getType(), caller + "." + cf.getName(), problem );
            }
            if ( cf.isAlias() ) {
                cf = rewriteWildcardAlias( (Alias)cf, ac, classes );
            } else if( cf.isConstant() && cf.getName().startsWith( "*" ) ) {
            	//this denotes for alias.length constant
            	Alias alias = (Alias)ac.getFieldByName( cf.getName().substring( 0, cf.getName().length() - 7 ).substring( 1 ) );
            	cf.setName( cf.getName().substring( 1 ) );
            	cf.setValue( "" + alias.getVars().size() );
            }
            var = new Var( cf, caller );
            
            problem.addVar( var );
            if( cf.isConstant() ) {
            	problem.getKnownVars().add( var );
            	problem.getFoundVars().add( var );
            }
        }

        for ( int j = 0; j < ac.getClassRelations().size(); j++ ) {
            classRelation = ac.getClassRelations().get( j );
            cf = null;
            String obj = caller;

            rel = new Rel();
            boolean isAliasRel = false;

            /* If we have a relation alias = alias, we rewrite it into new relations, ie we create
             a relation for each component of the alias structure*/
            if ( classRelation.getInputs().size() == 1 && classRelation.getOutputs().size() == 1 &&
                 ( classRelation.getType() == RelType.TYPE_ALIAS || classRelation.getType() == RelType.TYPE_EQUATION ) ) {
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

                //the following is for x = y, not x -> y relation where both x and y are aliases
                if ( ( classRelation.getType() == RelType.TYPE_EQUATION ) 
                		&& problem.getAllVars().containsKey( obj + "." + cf1.getName() ) ) {
                	
                    Var v1 = problem.getAllVars().get( obj + "." + cf1.getName() );
                    Var v2 = problem.getAllVars().get( obj + "." + cf2.getName() );

                    if ( v1.getField().isAlias() && v2.getField().isAlias() ) {
//                        if ( RuntimeProperties.isLogDebugEnabled() )
//                            db.p( ( ( Alias ) v1.getField() ).getAliasType() + " " +
//                                  ( ( Alias ) v2.getField() ).getAliasType() );
                        if ( !( ( Alias ) v1.getField() ).equalsByTypes( ( Alias ) v2.
                                getField() ) ) {
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
                            rel.setType( RelType.TYPE_SUBTASK );

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
                } else {
                	rel = makeRel( classRelation, problem, obj );
                }
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
            if ( classRelation.getType() == RelType.TYPE_UNIMPLEMENTED ) {
                setTargets( problem, classRelation, obj );
            } else if ( rel != null && classRelation.getInputs().isEmpty() &&
                        rel.getSubtaskCounter() == 0 ) { // if class relation doesnt have inputs, its an axiom
                problem.addAxiom( rel );
            }
            else if ( rel != null ) {
            	problem.addRel( rel );
            	
            	if( rel.getSubtaskCounter() > 0 ) {
            		problem.addRelWithSubtask( rel );
            	}
            	
            } else {
            	problem.addAllRels( relSet );
            }

        }
        //System.out.println(problem);
        return problem;
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
    
    private static ClassField rewriteWildcardAlias( Alias cf, AnnotatedClass ac, ClassList classes ) throws AliasException {
        if ( cf.isWildcard() ) {
            String wildcardVar = cf.getWildcardVar();
            
            ClassField clf;
            
            for ( int i = 0; i < ac.getFields().size(); i++ ) {
                clf = ac.getFields().get( i );
                AnnotatedClass anc = classes.getType( clf.getType() );
                if ( anc != null ) {
                	ClassField field = anc.getFieldByName( wildcardVar );
                    if ( field != null ) {
                    	
                    	if( !cf.isStrictType() || cf.getVarType().equals( field.getType() ) ) {
                    		ClassField cf2 = field.clone();
                    		cf2.setName( clf.getName() + "." + wildcardVar );
                    		//TODO check for wildcards on deeper levels
                    		if( cf2.isAlias() ) {
                    			for (int j = 0; j < cf2.getVars().size(); j++) {
                    				ClassField cfFrom2 = cf2.getVars().get(j);
                    				cfFrom2 = cfFrom2.clone();
                    				cfFrom2.setName( //clf.getName() + "." + 
                    									cfFrom2.getName() );
                    				cf2.getVars().set(j, cfFrom2);
                    			}
                    		}
//                    		ClassField cf2 = new ClassField(
//                        			clf.getName() + "." + wildcardVar,
//                        			type );
                        	
                        	cf.addVar( cf2 );
                    	}
                    }
                }
            }
            return cf;
        }
		return cf;
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

   private static Rel makeRel( ClassRelation classRelation, Problem problem, String obj ) throws
           UnknownVariableException {
       Var var;
       Rel rel = new Rel();

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
              problem.addTarget( var );
          } else {
              throw new UnknownVariableException( cf.getName() );
          }
      }

  }
}
