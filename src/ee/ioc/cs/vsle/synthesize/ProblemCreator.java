package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;
import static ee.ioc.cs.vsle.util.TypeUtil.*;

public class ProblemCreator {

	private ProblemCreator() {}
	
	static Problem makeProblem( ClassList classes ) throws SpecParseException {
		
	    long start = System.currentTimeMillis();
	    
		Problem problem = new Problem( new Var( new ClassField( TYPE_THIS, TYPE_THIS ), null ) );
		
    	makeProblemImpl( classes, problem.getRootVar(), problem, new HashMap<SubtaskClassRelation, SubtaskRel>(), new HashMap<String, Integer>() );
    	
    	if ( RuntimeProperties.isLogInfoEnabled() )
            db.p( "Problem created in: " + ( System.currentTimeMillis() - start ) + "ms." );
    	
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
    private static void makeProblemImpl( ClassList classes, Var parent, Problem problem, Map<SubtaskClassRelation, SubtaskRel> indpSubtasks, 
    		Map<String, Integer> visitedClasses ) throws
            SpecParseException {

    	List<Alias> aliases = new ArrayList<Alias>();
    	List<Var> aliasLengths = new ArrayList<Var>();
    	
    	AnnotatedClass ac = classes.getType( parent.getType() );
    	
        for ( ClassField cf : ac.getFields() ) {
        	
        	if ( cf.isAlias() ) {
                aliases.add( (Alias)cf );
                continue;
            }
        	
        	Var var = new Var( cf, parent );
            
            problem.addVar( var );
            
            String type;
            
            if ( classes.getType( type = cf.getType() ) != null ) {
            	
            	if( RuntimeProperties.isRecursiveSpecsAllowed() ) {
            		int lastDepth;

            		if( !visitedClasses.containsKey( type ) ) {
            			visitedClasses.put( type, ( lastDepth = RuntimeProperties.getMaxRecursiveDeclarationDepth() ) - 1 );
            		} else if( visitedClasses.get( type ) <= 0 ) {
            			continue;
            		} else {
            			lastDepth = visitedClasses.get(type);
            			visitedClasses.put( type, lastDepth -1 );
            		}

            		makeProblemImpl( classes, var, problem, indpSubtasks, visitedClasses );

            		visitedClasses.put( type, lastDepth );
            		
            	} else {
            		makeProblemImpl( classes, var, problem, indpSubtasks, visitedClasses );
            	}
            	
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
        	createAliasVar( alias, ac, classes, problem, parent );
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

        	String obj = parent.getFullNameForConcat();
        	
        	//check mutual declaration
        	if( RuntimeProperties.isRecursiveSpecsAllowed() 
        			&& ( checkRecursiveSpecRelationImpl( classRelation.getInputs(), visitedClasses, problem, obj ) 
        	    			|| checkRecursiveSpecRelationImpl( classRelation.getOutputs(), visitedClasses, problem, obj ) ) ) {
        		continue;
        	}
        	
            Rel rel = new Rel( parent, classRelation.getSpecLine() );
            Set<Rel> relSet = null;
            
            boolean isAliasRel = false;

            /* 
             * If we have a relation alias = alias, we rewrite it into new relations, ie we create
             * a relation for each component of the alias structure.
             * 
             * Else if the relation is x = y where both vars have common spec type, 
             * make their sub-components equal.
             */
            if ( classRelation.getInputs().size() == 1 && classRelation.getOutputs().size() == 1 ) {

                //if we have a relation alias = *.sth, we need to find out what it actually is
                if ( ( classRelation.getType() == RelType.TYPE_ALIAS ) && isAliasWildcardInput( classRelation ) ) {
                	relSet = makeAliasWildcardRel( ac, classes, classRelation, problem, parent );
                	rel = null;
                	isAliasRel = true;
                }
                //the following is for x = y, not x -> y relation
                else if ( ( classRelation.getType() == RelType.TYPE_EQUATION ) ) {
                	
                    Var inpVar = problem.getAllVars().get( obj + classRelation.getInput().getName() );
                    Var outpVar = problem.getAllVars().get( obj + classRelation.getOutput().getName() );
                    
                    if( inpVar == null ) {
                    	throw new UnknownVariableException( obj + classRelation.getInput().getName() );
                    }
                    
                    if( outpVar == null ) {
                    	throw new UnknownVariableException( obj + classRelation.getOutput().getName() );
                    }
                    
                    isAliasRel = checkVarEquality( inpVar, outpVar, classes, classRelation, parent, problem );
                }
            }

            if ( !isAliasRel ) {
            	rel = makeRel( new Rel( parent, classRelation.getSpecLine() ), classRelation, problem, parent );
            	
                if ( classRelation.getSubtasks().size() > 0 ) {

                    for ( SubtaskClassRelation subtask : classRelation.getSubtasks() ) {
                    	
                        SubtaskRel subtaskRel; 
                        
                        if( subtask.isIndependent() ) {
                        	
                        	if( indpSubtasks.containsKey( subtask ) ) {
                        	    
                        		subtaskRel = indpSubtasks.get( subtask );
                        	} else {
                        	    
                        		subtaskRel = makeIndependentSubtask( classes, indpSubtasks, subtask );
                        	}
                        } else {
                        	subtaskRel = new SubtaskRel( parent, subtask.getSpecLine() );
                        	
                        	makeRel( subtaskRel, subtask, problem, parent );
                        }
                        
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

    /**
     * @param classes
     * @param indpSubtasks
     * @param subtask
     * @return
     * @throws UnknownVariableException
     * @throws SpecParseException
     */
    private static SubtaskRel makeIndependentSubtask( ClassList classes, Map<SubtaskClassRelation, SubtaskRel> indpSubtasks,
            SubtaskClassRelation subtask ) throws UnknownVariableException, SpecParseException {
        SubtaskRel subtaskRel;
        ClassField context = subtask.getContext();

        ClassList newClassList = new ClassList();
        newClassList.addAll( classes );
        //remove annotated class for current context...
        newClassList.remove( classes.getType( TYPE_THIS ) );
        //[current context can be either THIS of the main scheme class or another independent subtask]
        newClassList.remove( classes.getType( AnnotatedClass.INDEPENDENT_SUBTASK ) );
        //...and create a new one as new root context
        AnnotatedClass newAnnClass = new AnnotatedClass( AnnotatedClass.INDEPENDENT_SUBTASK );
        newAnnClass.addField( context );
        ClassRelation newCR = new ClassRelation( RelType.TYPE_UNIMPLEMENTED, subtask.getSpecLine() );

        List<ClassField> empty = new ArrayList<ClassField>();

        for( ClassField input : subtask.getInputs() ) {
        	newCR.addInput( context.getName() + "." + input.getName(), empty );
        }

        for( ClassField output : subtask.getOutputs() ) {
        	newCR.addOutput( context.getName() + "." + output.getName(), empty );
        }

        newAnnClass.addClassRelation( newCR );
        newClassList.add(newAnnClass);
        Problem contextProblem = 
        	new Problem( new Var( new ClassField( TYPE_THIS, AnnotatedClass.INDEPENDENT_SUBTASK ), null ) );

        makeProblemImpl( newClassList, contextProblem.getRootVar(), contextProblem, indpSubtasks, new HashMap<String, Integer>() );

        Var par = contextProblem.getVarByFullName(context.getName());

        subtaskRel = new SubtaskRel( par, subtask.getSpecLine() );

        makeRel( subtaskRel, subtask, contextProblem, par );

        subtaskRel.setContextCF( context );
        subtaskRel.setContext( contextProblem );
        
        indpSubtasks.put( subtask, subtaskRel );
        return subtaskRel;
    }
    
    /**
     * check mutual declaration
     * @param classRelation
     * @param visitedClasses
     * @param problem
     * @param obj
     * @return
     */
    private static boolean checkRecursiveSpecRelationImpl( Collection<ClassField> fields, Map<String, Integer> visitedClasses, Problem problem, String obj ) {
    	for( ClassField outp : fields ) {
    		if( outp.isSpecField() && visitedClasses.get(outp.getType()) == 0 ) {
    			return true;
    		} else if( problem.getAllVars().get( obj + outp.getName() ) == null && outp.getName().indexOf( "." ) > -1 ) {
    			Var par = problem.getAllVars().get( obj + outp.getName().substring( 0, outp.getName().lastIndexOf( ".") ) );
    			
    			if( par != null && par.getField().isSpecField() && visitedClasses.get(par.getType()) == 0 ) {
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
    
    /** 
     * If we have a relation alias = alias, we rewrite it into new relations, ie we create
     * a relation for each component of the alias structure.
     * 
     * Else if the relation is x = y where both vars have common spec type, 
     * make their sub-components equal.
     * 
     * @param inpVar
     * @param outpVar
     * @param classes
     * @param classRelation
     * @param parent
     * @param problem
     * @return
     * @throws SpecParseException
     */
    private static boolean checkVarEquality( Var inpVar, Var outpVar, ClassList classes, ClassRelation classRelation, Var parent, Problem problem ) throws SpecParseException {
        
        if ( inpVar.getField().isAlias() && outpVar.getField().isAlias() ) {

            if ( !( ( Alias ) inpVar.getField() ).equalsByTypes( ( Alias ) outpVar.
                    getField() ) ) {
                throw new AliasException( "Differently typed aliases connected: " 
                        + parent.getFullNameForConcat() + inpVar.getField().getName() + " and " + parent.getFullNameForConcat() + outpVar.getField().getName() );
            }
            
            Var inpChildVar, outpChildVar;
            for ( int i = 0; i < inpVar.getChildVars().size(); i++ ) {
                inpChildVar = inpVar.getChildVars().get( i );
                outpChildVar = outpVar.getChildVars().get( i );

                if( !checkVarEquality( inpChildVar, outpChildVar, classes, classRelation, parent, problem ) ) {
                    
                    Rel rel = new Rel( parent, inpVar.getFullName() + " = " 
                            + outpVar.getFullName() + ", derived from: " + classRelation.getSpecLine() );
                    rel.setType( RelType.TYPE_EQUATION );

                    rel.addInput( inpChildVar );
                    rel.addOutput( outpChildVar );
                    inpChildVar.addRel( rel );
                    problem.addRel( rel );

                    checkSpecClassVarBinding( inpChildVar, outpChildVar, classes, classRelation, parent, problem );
                }
            }
            
            return true;
        } else {
            checkSpecClassVarBinding( inpVar, outpVar, classes, classRelation, parent, problem );
        }
        
        return false;
    }
    
    /**
     * check if both vars have spec class types, find common spec class and make all sub-components to be equal
     * 
     * @throws SpecParseException 
     */
    private static void checkSpecClassVarBinding( Var inpVar, Var outpVar, ClassList classes, ClassRelation classRelation, Var parent, Problem problem ) throws SpecParseException {
        
        if( inpVar.getField().isSpecField() && outpVar.getField().isSpecField() ) {
            
            String typeIn = inpVar.getType();
            String typeOut = outpVar.getType();
            
            ClassList commonTypes = getCommonTypes( classes, typeIn, typeOut );
            
            if( commonTypes.isEmpty() ) {
                throw new SpecParseException( "Incorrect equality, types " + typeIn + " and " + typeOut 
                        + " do not have common superclasses: " + outpVar.getFullName() + " = " 
                        + inpVar.getFullName() + ", derived from: " + classRelation.getSpecLine() );
            }
            
            Set<String> fields = new HashSet<String>();
            fields.add( AnnotatedClass.SPEC_OBJECT_NAME );
            
            for ( AnnotatedClass commonType : commonTypes ) {
                
                for ( ClassField field : commonType.getFields() ) {
                    if( !fields.contains( field.getName() ) ) {
                        fields.add( field.getName() );
                        
                        Var inpVarSub = problem.getAllVars().get( inpVar.getFullNameForConcat() + field.getName() );
                        Var outpVarSub = problem.getAllVars().get( outpVar.getFullNameForConcat() + field.getName() );
                        
                        Rel rel = new Rel( parent, outpVarSub.getFullName() + " = " 
                                + inpVarSub.getFullName() + ", derived from: " + classRelation.getSpecLine() );
                        rel.setType( RelType.TYPE_EQUATION );

                        rel.addInput( inpVarSub );
                        rel.addOutput( outpVarSub );
                        inpVarSub.addRel( rel );
                        problem.addRel( rel );
                        
                        //recursively check this equality
                        checkVarEquality( inpVarSub, outpVarSub, classes, classRelation, parent, problem );
                    }
                }
            }
        }
    }
    
    /**
     * Derives common types for given two types.
     * As multiple inheritance of specifications is allowed, 
     * more that one common type may be derived.
     * 
     * @param classes
     * @param type1
     * @param type2
     * @return
     */
    private static ClassList getCommonTypes( ClassList classes, String type1, String type2 ) {
        
        ClassList types = new ClassList();
        
        if( type1.equals( type2 ) ) {
            types.add( classes.getType( type1 ) );
        }
        else {
            AnnotatedClass ac1 = classes.getType( type1 );
            AnnotatedClass ac2 = classes.getType( type2 );
            
            if( ac2.getSuperClasses().contains( ac1 ) ) {
                types.add( ac1 );
            }
            else if( ac1.getSuperClasses().contains( ac2 ) ) {
                types.add( ac2 );
            }
            else {
                
                for ( AnnotatedClass super1 : ac1.getSuperClasses() ) {
                    if( ac2.getSuperClasses().contains( super1 ) ) {
                        types.add( super1 );
                    }
                }
            }
        }
        
        return types;
    }
    
    /**
     * Creates an alias
     * 
     * @param alias
     * @param ac
     * @param classes
     * @param problem
     * @param parent
     * @throws AliasException
     */
    private static void createAliasVar( Alias alias, AnnotatedClass ac, ClassList classes, Problem problem, Var parent ) throws AliasException {
    	
    	Var var;
    	//corresponding Var may have already been initialized
    	if( ( var = problem.getAllVars().get( parent.getFullNameForConcat() + alias.getName() ) ) == null ) {
    		var = new Var( alias, parent );
    	}
    	
    	if( alias.isWildcard() ) {
    		rewriteWildcardAliasVar( var, ac, classes, problem );
    	} else {
    		for ( ClassField childField : alias.getVars() ) {
    			Var childVar = problem.getAllVars().get( parent.getFullNameForConcat() + childField.getName() );
    			
    			if( childVar != null ) {
    				var.addVar( childVar );
    			}
			}
    	}
        
        problem.addVar( var );
        
        //if alias has no elements it is always computable
        if( var.getChildVars().isEmpty() && alias.isInitialized() ) {
            problem.getKnownVars().add( var );
        }
    }
    
    /**
     * Fills an alias with corresponding variables that match the wildcard
     * 
     * @param aliasVar
     * @param ac
     * @param classes
     * @param problem
     * @throws AliasException
     */
    private static void rewriteWildcardAliasVar( Var aliasVar, AnnotatedClass ac, ClassList classes, Problem problem ) throws AliasException {
    	
    	Alias alias = (Alias)aliasVar.getField();
    	String wildcardVar = alias.getWildcardVar();
    	
    	for ( ClassField clf : ac.getFields() ) {
    		//in the following AnnotatedClass we look for vars that match wildcard
    		AnnotatedClass anc = classes.getType( clf.getType() );
    		if ( anc != null ) {
    			//this field matches
    			ClassField field = anc.getFieldByName( wildcardVar );
    			if ( field != null ) {

    				if( alias.acceptsType( field.getType() ) ) {
    					
    					String absoluteName = aliasVar.getParent().getFullNameForConcat() + clf.getName() + "." + field.getName();
    					
    					Var var = problem.getAllVars().get( absoluteName );
    					
    					if( var != null ) {
    						aliasVar.addVar( var );
    						alias.addVar( var.getField() );
    					}
    				}
    			}
    		}
    	}
    }

    /**
     * checks whether a given relation has a wildcard alias as its input
     * 
     * @param classRelation
     * @return
     */
    private static boolean isAliasWildcardInput( ClassRelation classRelation ) {
    	
        return classRelation.getInput().getName().startsWith( "*." );
    }
    
    /**
     * creates Rel for alias x = ( *.wildcardVar );
     */
    private static Set<Rel> makeAliasWildcardRel( AnnotatedClass ac, ClassList classes, ClassRelation classRelation,
    		Problem problem, Var parentObj ) throws UnknownVariableException {
    	
        Var alias = problem.getAllVars().get( parentObj.getFullNameForConcat() + classRelation.getOutput().getName() );
    	
    	if( alias == null ) {
            throw new UnknownVariableException( parentObj.getFullNameForConcat() + classRelation.getOutput().getName() );
    	}
    	
    	Rel relAliasOutp = new Rel( parentObj, classRelation.getSpecLine() );
    	relAliasOutp.setMethod( classRelation.getMethod() );
    	relAliasOutp.setType( classRelation.getType() );
    	relAliasOutp.addInputs( alias.getChildVars() );
    	relAliasOutp.addOutput( alias );
    	alias.addRel(relAliasOutp);
    	for ( Var childVar : alias.getChildVars() ) {
    		childVar.addRel(relAliasOutp);
		}
    	
    	Rel relAliasInp = new Rel( parentObj, classRelation.getSpecLine() );
    	relAliasInp.setMethod( classRelation.getMethod() );
    	relAliasInp.setType( classRelation.getType() );
    	relAliasInp.addOutputs( alias.getChildVars() );
    	relAliasInp.addInput( alias );
    	alias.addRel(relAliasInp);
    	
    	Set<Rel> relset = new LinkedHashSet<Rel>();
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

       String parentObj = parentVar.getFullNameForConcat();
       
       for ( ClassField input : classRelation.getInputs() ) {
           //if we deal with equation and one variable is used on both sides of "=", we cannot use it.
           if( classRelation.getType() == RelType.TYPE_EQUATION && classRelation.getOutputs().contains( input ) ) {
				return null;
			}
           
           String varName = parentObj + input.getName();
           if ( problem.getAllVars().containsKey( varName ) ) {
               var = problem.getAllVars().get( varName );
               var.addRel( rel );
               rel.addInput( var );
           } else {
               throw new UnknownVariableException( parentObj + varName );
           }
       }
       
       for ( ClassField output : classRelation.getOutputs() ) {
           String varName = parentObj + output.getName();
           if ( problem.getAllVars().containsKey( varName ) ) {
               var = problem.getAllVars().get( varName );
               rel.addOutput( var );
           } else {
               throw new UnknownVariableException( parentObj + varName );
           }
       }
       
       for ( ClassField exception : classRelation.getExceptions() ) {
           Var ex = new Var( exception, null );
           rel.getExceptions().add( ex );
       }

       rel.setMethod( classRelation.getMethod() );
       
       rel.setType( classRelation.getType() );
       
       return rel;
   }
   
   /**
    * @deprecated
    * @param classRelation
    * @return
    */
   private static String checkIfRightWildcard( ClassRelation classRelation ) {
       String s = classRelation.getOutput().getName();
       if ( s.startsWith( "*." ) )
           return s.substring( 2 );
       return null;
   }
   
   /**
    * creates set of Rels for x -> *.y { impl }; - does not work currently
    * @deprecated
    */
   private static Set<Rel> makeRightWildcardRel( AnnotatedClass ac, ClassList classes, ClassRelation classRelation,
		   Problem problem, Var parentVar, String wildcardVar ) throws
		   UnknownVariableException {
	   
	   //temporaly disable
	   if( true ) {
		   throw new UnknownVariableException( "*." + wildcardVar );
	   }
	   ClassField clf;
	   Set<Rel> set = new LinkedHashSet<Rel>();
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
      Collection<Var> vars = new HashSet<Var>();
      Collection<Var> flattened = new HashSet<Var>();
      
      {//Assumptions
          for ( ClassField cf : classRelation.getInputs() ) {
              String varName = obj + cf.getName();
              if ( problem.getAllVars().containsKey( varName ) ) {
                  vars.add( problem.getAllVars().get( varName ) );
              } else {
                  throw new UnknownVariableException( cf.getName() );
              }
          }

          CodeGenerator.unfoldVarsToSet( vars, flattened );

          problem.getKnownVars().addAll( flattened );
          problem.getFoundVars().addAll( flattened );
          problem.getAssumptions().addAll( flattened );
      }
      
      {//Goals
          vars.clear();
          flattened.clear();
          
          for ( ClassField cf : classRelation.getOutputs() ) {
              String varName = obj + cf.getName();
              if ( problem.getAllVars().containsKey( varName ) ) {
                  vars.add( problem.getAllVars().get( varName ) );
              } else {
                  throw new UnknownVariableException( cf.getName() );
              }
          }
          
          CodeGenerator.unfoldVarsToSet( vars, flattened );
          
          problem.getGoals().addAll( flattened );
      }

  }
}
