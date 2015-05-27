package ee.ioc.cs.vsle.synthesize;

import static ee.ioc.cs.vsle.util.TypeUtil.TYPE_THIS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ee.ioc.cs.vsle.editor.RuntimeProperties;
import ee.ioc.cs.vsle.util.TypeUtil;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.AliasLength;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProblemCreator {

    private static final Logger logger = LoggerFactory.getLogger(ProblemCreator.class);

    private Map<SubtaskClassRelation, SubtaskRel> indpSubtasks;
    private Map<String, Var> aliasLengths = new HashMap<String, Var>();
    private Map<String, Var> aliasElements = new HashMap<String, Var>();
    private List<Var> parentVars = new ArrayList<Var>();
    //"any" class fields that need to be bound with a concrete type 
    private Map<String, List<Map<String, String>>> unboundAnyTypeVars = new LinkedHashMap<String, List<Map<String, String>>>();
    //the list of classes that exist in the problem setting.
    private ClassList classes;
    private static int aliasCounter = 0;
    private static final Pattern PATTERN_ALIAS_ELEMENT_ACCESS = Pattern
            .compile( "([^\\* ]+)\\.(\\*|[0-9]+)(\\.(\\(([a-zA-Z0-9_]+)\\))?([^\\.() ]+))?$");
    private final String rootClassName;

    ProblemCreator( ClassList classes ) {
        this(classes, null);
    }

    ProblemCreator( ClassList classes, String rootClassName ) {
        assert classes != null;
        this.classes = classes;
        this.rootClassName = rootClassName;
        indpSubtasks = new HashMap<SubtaskClassRelation, SubtaskRel>();
    }
    
    ProblemCreator( ClassList classes, String rootClassName, Map<SubtaskClassRelation, SubtaskRel> indpSubtasks) {
        this(classes, rootClassName);
        assert indpSubtasks != null;
        this.indpSubtasks = indpSubtasks;
    }

    Problem makeProblem() throws SpecParseException {

        long start = System.currentTimeMillis();
        
        Problem problem = new Problem( new Var( new ClassField( TYPE_THIS, rootClassName ), null ) );
        
        makeProblemImpl( problem.getRootVar(), problem, /*new HashMap<String, Integer>(),*/ true );
        
        logger.info( "Problem created in: " + ( System.currentTimeMillis() - start ) + "ms." );
        
        logger.debug( problem.toString() );
        
        return problem;
    }
    
    /**
     Creates the problem - a graph-like data structure on which planning can be applied. The method is recursively
     applied to dig through the class tree.
     @return    problem which can be given to the planner
     @param type the type of object which is currently being added to the problem.
     @param caller caller, or the "parent" of the current object. The objects name will be caller + . + obj.name
     @param problem the problem itself (needed because of recursion).
     */
    private void makeProblemImpl( Var parent, Problem problem,
            /*Map<String, Integer> visitedClasses,*/ boolean root ) throws
            SpecParseException {

        parentVars.add(parent);

        List<Alias> aliases = new ArrayList<Alias>();
        
        AnnotatedClass ac = classes.getType( parent.getType() );
        
        for ( ClassField cf : ac.getFields() ) {
            
            if ( cf.isAlias() ) {
                aliases.add( (Alias)cf );
                continue;
            }
            
            Var var = new Var( cf, parent );
            
            problem.addVar( var );
            
            if( cf.isConstant() || cf.isSchemeObject() ) {
              problem.getCurrentContext().getKnownVars().add( var );
            }
            //String type;
            
            if ( classes.getType( /*type =*/ cf.getType() ) != null ) {
                /*
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

                    makeProblemImpl( var, problem, visitedClasses, false );

                    visitedClasses.put( type, lastDepth );
                    
                } else {*/
                    makeProblemImpl( var, problem, /*visitedClasses,*/ false );
                //}
                
                continue;
            }
            
            if( cf.isAliasLength() && cf.getValue() == null ) {
                //this denotes alias.length constant and the value needs to be assigned
                Alias alias = ((AliasLength)cf).getAlias();
                String aliasFullName = parent.getFullNameForConcat() + alias.getName();
                aliasLengths.put( aliasFullName, var );
            }
        }

        //make aliases when all other vars have been created
        for ( Alias alias : aliases) {
            createAliasVar( alias, ac, problem, parent );
        }

        if( root ) {
            //initialize <alias>.length property
            for ( String alias : aliasLengths.keySet() ) {
                Var lengthVar = aliasLengths.get( alias );
                Var aliasVar = problem.getVar( alias );
                String meth = lengthVar.getFullName() + " = " + aliasVar.getChildVars().size();
                ClassRelation cr = new ClassRelation( RelType.TYPE_EQUATION, meth );
                cr.addOutput( lengthVar.getFullName(), ac.getFields() );
                cr.setMethod( meth );
                ac.addClassRelation( cr );
            }
            
            
//            for (Var parentVar : parentVars) {
//                makeFromClassRelations(parentVar, problem);
//            }
            //backward traversal is needed in order to override 
            //default values of variables given in specifications 
            //TODO but this may be temporary solution, 
            //see axiom application in DepthFirstPlanner.invokePlanning()
            for ( int i = parentVars.size() - 1; i >= 0; i-- ) {
                makeFromClassRelations( parentVars.get( i ), problem );
            }
        }
    }

    /**
     * @param classes
     * @param parent
     * @param problem
     * @param visitedClasses
     * @param ac
     * @throws UnknownVariableException
     * @throws SpecParseException
     */
    private void makeFromClassRelations(Var parent,
            Problem problem//, Map<String, Integer> visitedClasses,
            ) throws UnknownVariableException,
            SpecParseException {
        
        AnnotatedClass ac = classes.getType( parent.getType() );
        
        for ( ClassRelation classRelation : ac.getClassRelations() ) {

            //check mutual declaration
            /*
            String obj = parent.getFullNameForConcat();
            if( RuntimeProperties.isRecursiveSpecsAllowed() 
                    && ( checkRecursiveSpecRelationImpl( classRelation.getInputs(), visitedClasses, problem, obj ) 
                            || checkRecursiveSpecRelationImpl( classRelation.getOutputs(), visitedClasses, problem, obj ) ) ) {
                continue;
            }
            */
            Rel rel = new Rel( parent, classRelation.getSpecLine() );
            Set<Rel> relSet = null;
            
            boolean isAliasRel = false;

            //Substitutions appear in the case of alias element access and are used
            //during the code generation, see Rel.CodeEmitter.emitEquation()
            //For instance, a.1 is replaced by x in the equation y=a.1
            //EquationSolver produces a method string that is directly used by Rel.CodeEmitter. 
            //Variable name a.1 has to be replaced by x in the method string to make it y=x;
            Map<String, String> varSubstitutions = new LinkedHashMap<String, String>();
            
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
                    
                    Var inpVar = checkVarExistance( problem, parent, classRelation.getInput(), varSubstitutions, true );
                    Var outpVar = checkVarExistance( problem, parent, classRelation.getOutput(), varSubstitutions, false );
                    
                    isAliasRel = checkVarEquality( inpVar, outpVar, classes, classRelation, parent, problem );
                }
            }

            if ( !isAliasRel ) {
                rel = makeRel( new Rel( parent, classRelation.getSpecLine() ), classRelation, problem, parent, varSubstitutions );
                
                if ( classRelation.getSubtasks().size() > 0 ) {

                    for ( SubtaskClassRelation subtask : classRelation.getSubtasks() ) {
                        
                        SubtaskRel subtaskRel; 
                        
                        if( subtask.isIndependent() ) {
                            
                            if( indpSubtasks.containsKey( subtask ) ) {
                                
                                subtaskRel = indpSubtasks.get( subtask );
                            } else {
                                
                                subtaskRel = makeIndependentSubtask( subtask );
                            }
                        } else {
                            subtaskRel = new SubtaskRel( parent, subtask.getSpecLine() );
                            
                            makeRel( subtaskRel, subtask, problem, parent, varSubstitutions );
                        }
                        
                        rel.addSubtask( subtaskRel );
                        
                        problem.addSubtask( subtaskRel );
                    }
                }
            }

            if ( rel != null ) {
                // if it is not a "real" relation (type 7), we just set the result as target, and inputs as known variables
                if ( rel.getType() == RelType.TYPE_UNIMPLEMENTED ) {
                    setTargets( problem, rel );
                } 
                // if class relation doesnt have inputs, its an axiom
                else if ( classRelation.getInputs().isEmpty() &&
                        rel.getSubtaskCount() == 0 ) { 
                    problem.addAxiom( rel );
                }
                else {
                    problem.addRel( rel );

                    if( rel.getSubtaskCount() > 0 ) {
                        problem.addRelWithSubtask( rel );
                    }

                } 
            } else if( relSet != null ) {
                for ( Rel _rel : relSet ) {
                    problem.addRel( _rel );
                }
            }

        }
    }

    /**
     * Creates an instance of an independent subtask
     * 
     * @param classes
     * @param indpSubtasks
     * @param subtask
     * @return
     * @throws UnknownVariableException
     * @throws SpecParseException
     */
    SubtaskRel makeIndependentSubtask(SubtaskClassRelation subtask)
            throws UnknownVariableException, SpecParseException {
        SubtaskRel subtaskRel;
        ClassField context = subtask.getContext();

        //construct a new goal
        ClassRelation newCR = new ClassRelation( RelType.TYPE_UNIMPLEMENTED, subtask.getSpecLine() );

        List<ClassField> empty = new ArrayList<ClassField>();

        for( ClassField input : subtask.getInputs() ) {
            newCR.addInput( context.getName() + "." + input.getName(), empty );
        }

        for( ClassField output : subtask.getOutputs() ) {
            newCR.addOutput( context.getName() + "." + output.getName(), empty );
        }

        //create a new annotated class as a new root context
        AnnotatedClass newAnnClass = new AnnotatedClass(
                CodeGenerator.INDEPENDENT_SUBTASK );
        newAnnClass.addField( context );
        newAnnClass.addClassRelation( newCR );
        
        //build a new list of classes
        ClassList newClassList = new ClassList();
        newClassList.add(newAnnClass);
        for ( AnnotatedClass ac : classes ) {
            //do not add annotated class for current context...
            if(ac.getName().equals(rootClassName)
                  //[current context can be either THIS of the main scheme class 
                    //or another independent subtask]
                    || CodeGenerator.INDEPENDENT_SUBTASK.equals(ac.getName())) {
                continue;
            }
            newClassList.add( ac );
        }
        
        Problem contextProblem = 
            new Problem( new Var( new ClassField( TYPE_THIS, CodeGenerator.INDEPENDENT_SUBTASK ), null ) );

        new ProblemCreator(newClassList, contextProblem.getRootVar().getType(), indpSubtasks).makeProblemImpl(
                contextProblem.getRootVar(), contextProblem,
                //new HashMap<String, Integer>(), 
                true);

        Var par = contextProblem.getVarByFullName(context.getName());

        subtaskRel = new SubtaskRel( par, subtask.getSpecLine() );

        makeRel( subtaskRel, subtask, contextProblem, par, null );

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
    @SuppressWarnings( "unused" )
    private static boolean checkRecursiveSpecRelationImpl( Collection<ClassField> fields, Map<String, Integer> visitedClasses, Problem problem, String obj ) {
        for( ClassField outp : fields ) {
            if( outp.isSpecField() && visitedClasses.get(outp.getType()) == 0 ) {
                return true;
            } else if( problem.getVar( obj + outp.getName() ) == null && outp.getName().indexOf( "." ) > -1 ) {
                Var par = problem.getVar( obj + outp.getName().substring( 0, outp.getName().lastIndexOf( ".") ) );
                
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
        }
        
        checkSpecClassVarBinding( inpVar, outpVar, classes, classRelation, parent, problem );
        
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
            fields.add( CodeGenerator.SPEC_OBJECT_NAME );
            
            for ( AnnotatedClass commonType : commonTypes ) {
                
                for ( ClassField field : commonType.getFields() ) {
                    if( !fields.contains( field.getName() ) ) {
                        fields.add( field.getName() );
                        
                        Var inpVarSub = problem.getVar( inpVar.getFullNameForConcat() + field.getName() );
                        Var outpVarSub = problem.getVar( outpVar.getFullNameForConcat() + field.getName() );
                        
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
            
            if( ac2.getAllSuperClasses().contains( ac1 ) ) {
                types.add( ac1 );
            }
            else if( ac1.getAllSuperClasses().contains( ac2 ) ) {
                types.add( ac2 );
            }
            else {
                ClassList allAC2superclasses = ac2.getAllSuperClasses();
                for ( AnnotatedClass super1 : ac1.getAllSuperClasses() ) {
                    if( allAC2superclasses.contains( super1 ) ) {
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
     * @throws SpecParseException 
     */
    private void createAliasVar(Alias alias, AnnotatedClass ac,
            Problem problem, Var parent)
            throws SpecParseException {

        Var var;
        //corresponding Var may have already been initialized
        if( ( var = problem.getVar( parent.getFullNameForConcat() + alias.getName() ) ) == null ) {
            var = new Var( alias, parent );
        } else {
            if (!var.getField().isAlias()
                    || ((Alias) var.getField()).isInitialized()
                    || !alias.isInitialized()) {
                throw new AliasException(
                        "Only uninitialized alias declaration can be swapped with corresponding initialized alias! " + var.getFullName());
            }
            Alias newAlias = new Alias(alias.getName(), alias.getVarType());
            newAlias.setInitialized(true);
            newAlias.getVars().addAll(alias.getVars());
            //replace existing var with a new one 
            //(because we have to make changes in the underlying class field)
            var.setField( newAlias );
        }

        problem.addVar( var );
        
        if( alias.isWildcard() ) {
            rewriteWildcardAliasVar( var, ac, problem );
        } else {
            for ( ClassField childField : alias.getVars() ) {
                Var childVar = checkVarExistance( problem, parent, childField, null, false ); 
                
                if( childVar != null ) {
                    var.addVar( childVar );
                }
            }
        }
        
        //if an alias has been initialized with an empty list of elements it is always computable
        if( var.getChildVars().isEmpty() && alias.isInitialized() ) {
            problem.getCurrentContext().getKnownVars().add( var );
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
    private void rewriteWildcardAliasVar( Var aliasVar, AnnotatedClass ac, Problem problem ) throws AliasException {
        
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
                        
                        Var var = problem.getVar( absoluteName );
                        
                        if( var != null ) {
                            aliasVar.addVar( var );
                            //TODO a field may be overwritten in case of multiports
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
        
        Var alias = problem.getVar( parentObj.getFullNameForConcat() + classRelation.getOutput().getName() );
        
        if( alias == null ) {
            throw new UnknownVariableException( parentObj.getFullNameForConcat() + classRelation.getOutput().getName() );
        }
        
        Rel relAliasOutp = new Rel( parentObj, classRelation.getSpecLine() );
        relAliasOutp.setMethod( classRelation.getMethod() );
        relAliasOutp.setType( classRelation.getType() );
        relAliasOutp.addInputs( alias.getChildVars() );
        relAliasOutp.addOutput( alias );
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

   private Rel makeRel( Rel rel, ClassRelation classRelation, Problem problem, Var parentVar, Map<String, String> substitutions ) throws
           SpecParseException {

       for ( ClassField input : classRelation.getInputs() ) {
           //if we deal with equation and one variable is used on both sides of "=", we cannot use it.
           if( classRelation.getType() == RelType.TYPE_EQUATION && classRelation.getOutputs().contains( input ) ) {
                return null;
            }
           
           Var var = checkVarExistance( problem, parentVar, input, substitutions, true );
           var.addRel( rel );
           rel.addInput( var );
       }
       
       for ( ClassField output : classRelation.getOutputs() ) {
           Var var = checkVarExistance( problem, parentVar, output, substitutions, false );
           rel.addOutput( var );
       }
       
       for ( ClassField exception : classRelation.getExceptions() ) {
           Var ex = new Var( exception, null );
           rel.getExceptions().add( ex );
       }

       rel.setMethod( classRelation.getMethod() );
       
       rel.setType( classRelation.getType() );
       
       rel.addSubstitutions( substitutions );
       
       return rel;
   }

    private Var checkVarExistance(Problem problem, Var parentVar,
            ClassField field, Map<String, String> substitutions, boolean checkAnyType) throws SpecParseException {
        
        final String varName = parentVar.getFullNameForConcat() + field.getName();
        final Var var = problem.getVar(varName);
        
        if (var == null) {
            // --------alias element access--------
            if (aliasElements.containsKey(varName)) {
                Var aliasEl = aliasElements.get(varName); 
                if(substitutions != null)
                    substitutions.put( field.getName(), aliasEl.getFullName() );
                return aliasEl;
            }
            
            Matcher matcher = PATTERN_ALIAS_ELEMENT_ACCESS.matcher(varName);
            if (matcher.find()) {
                String aliasVarName = matcher.group(1);
                String element = matcher.group(2);
                String type = matcher.group(5);
                String subelement = matcher.group(6);
                Var aliasEl =  getAliasElementVar(problem, varName, aliasVarName,
                        element, subelement, parentVar, type);
                if(substitutions != null)
                    substitutions.put( field.getName(), aliasEl.getFullName() );
                return aliasEl;
            }
            // ------end of alias element access------

            throw new UnknownVariableException(varName);
            
        } 

        checkAny( var, field, substitutions, checkAnyType );

        return var;
    }

    /**
     * @param var
     * @param varName
     * @param field
     * @param substitutions
     * @param checkAnyType
     */
    private void checkAny( final Var var, 
                           final ClassField field, 
                           Map<String, String> substitutions,
                           boolean checkAnyType ) {
        
        final String varName = var.getFullName();
        ClassField varField = var.getField(); 
        if(varField.isAny()) {
            String type = null;
            if(varField.isAnyTypeBound()) {
                type = varField.getAnySpecificType();
            }
            else if(field.isAnyTypeBound()) {
                type = field.getAnySpecificType();
                varField.setAnySpecificType( type );
            } else if(checkAnyType) {
                //specific type is still unknown, lets try a bit later
                List<Map<String, String>> listOfPostponedSubstitutions = unboundAnyTypeVars.get( varName );
                if(listOfPostponedSubstitutions == null) {
                    listOfPostponedSubstitutions = new ArrayList<Map<String, String>>();
                    unboundAnyTypeVars.put( varName, listOfPostponedSubstitutions );
                }
                if(!listOfPostponedSubstitutions.contains( substitutions ))
                    listOfPostponedSubstitutions.add(substitutions);
            }
            
            if(type != null) {
                String substitution = CodeGeneratorUtil.getAnyTypeSubstitution(varName, type);
                if(checkAnyType) {
                    substitutions.put(varName, substitution);
                }
                //finally, lets check if there are any substitutions waiting for bound type of this var
                List<Map<String, String>> listOfPostponedSubstitutions = unboundAnyTypeVars.remove( varName );
                if(listOfPostponedSubstitutions != null) {
                    for ( Map<String, String> substs : listOfPostponedSubstitutions ) {
                        if(substs.containsKey( varName ))
                            throw new IllegalStateException( "'any' var " + varName + " already in substitution list");
                        substs.put( varName, substitution );
                    }
                }
            }
        }
    }

    /**
     * @param problem
     * @param varName
     * @param aliasVarName
     * @param element
     * @param subelement
     * @throws AliasException
     * @throws UnknownVariableException
     */
    private Var getAliasElementVar(Problem problem, String varName,
            String aliasVarName, String element, String subelement, Var parent, String type)
            throws AliasException, UnknownVariableException {
        
        Var aliasVar = problem.getVar(aliasVarName);

        if(aliasVar==null) {
            throw new UnknownVariableException(aliasVarName);
        }
        
        if (!((Alias) aliasVar.getField()).isInitialized()) {
            throw new AliasException( aliasVar.getFullName() + " is not initialized!" );
        }

        if ("*".equals(element)) {
            if (subelement != null) {
                // construct new alias
                Alias newAlias = new Alias( "derived_" + aliasCounter++, type );
                newAlias.setInitialized( true );
                Var newAliasVar = new Var( newAlias, parent );
                problem.addVar( newAliasVar );
                
                Rel aliasInputRel = new Rel(parent, "(" + varName + ") " + newAlias.getName() + " ->");
                aliasInputRel.addInput( newAliasVar );
                newAliasVar.addRel( aliasInputRel );
                aliasInputRel.setMethod( TypeUtil.TYPE_ALIAS );
                aliasInputRel.setType( RelType.TYPE_ALIAS );
                
                Rel aliasOutputRel = new Rel(parent, "-> " + "(" + varName + ") " + newAlias.getName());
                aliasOutputRel.addOutput( newAliasVar );
                aliasOutputRel.setMethod( TypeUtil.TYPE_ALIAS );
                aliasOutputRel.setType( RelType.TYPE_ALIAS );
                
                int i = 0;
                for ( Var elementVar : aliasVar.getChildVars() ) {
                    Var var = getVarFromSpecOrAlias( problem, varName, aliasVarName + "." + i++, subelement, elementVar );
                    newAlias.addVar( var.getField() );
                    newAliasVar.addVar( var );
                    
                    aliasInputRel.addOutput( var );
                    
                    aliasOutputRel.addInput( var );
                    var.addRel( aliasOutputRel );
                }
                
                problem.addRel( aliasInputRel );
                problem.addRel( aliasOutputRel );
                
                if( newAliasVar.getChildVars().isEmpty() ) {
                    problem.getCurrentContext().getKnownVars().add( newAliasVar );
                }
                
                aliasElements.put(varName, newAliasVar);
                return newAliasVar;
            }
            throw new AliasException(
                    varName + " is incorrect, wildcard should be defined with an element!");
        }
        
        Var var = getVarFromAliasByElemNr(aliasVar, aliasVarName, element);
        
        if (subelement != null) {
            var = getVarFromSpecOrAlias( problem, varName, aliasVarName + "." + element, subelement, var );
        }
        aliasElements.put(varName, var);
        return var;
    }

    private Var getVarFromAliasByElemNr(Var aliasVar, String aliasVarName,
            String element) throws AliasException {
        int elemNr = Integer.parseInt(element);
        List<Var> childVars = aliasVar.getChildVars();
        int size = childVars.size();
        if (elemNr >= size) {
            throw new AliasException(aliasVarName + "." + element
                    + " is out of bounds, " + aliasVar.getFullName()
                    + " contains " + size
                    + (size == 1 ? " element" : " elements"));
        }
        return childVars.get(elemNr);
    }
    
    /**
     * @param problem
     * @param varName
     * @param aliasElement
     * @param subelement
     * @param var
     * @return
     * @throws UnknownVariableException
     * @throws AliasException
     */
    private Var getVarFromSpecOrAlias( Problem problem, String varName,
            String aliasElement, String subelement, Var var )
            throws UnknownVariableException, AliasException {
        
        if (var.getField().isSpecField()) {
            String subelementName = var.getFullNameForConcat() + subelement;
            if(problem.containsVar(subelementName)) {
                var = problem.getVar(subelementName);
            } else {
                throw new UnknownVariableException(subelementName);
            }
        } else if(var.getField().isAlias()) { 
            boolean found = false;
            if (subelement.matches("^\\d+$")) {
                //if subelement is a number, extract the corresponding var
                if( (var = getVarFromAliasByElemNr(var, varName, subelement) ) != null )
                    found = true;
            } else {
                for (Var childVar : var.getChildVars()) {
                    if (subelement.equals(childVar.getName())) {
                        var = childVar;
                        found = true;
                        break;
                    }
                }
            }
            if(!found)
                throw new AliasException("Alias " 
                        + var.getFullName()
                        + " does not contain an element "
                        + subelement + " specified in " + varName);
        } else {
            throw new AliasException(
                    "Type " + var.getType()
                    + " of " + var.getFullName()
                    + " derived from " 
                    + aliasElement
                    + " is not a metaclass or an alias!");
        }
        return var;
    }

   /**
    * @deprecated
    * @param classRelation
    * @return
    */
   @SuppressWarnings( "unused" )
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
   @SuppressWarnings( "unused" )
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
  private static void setTargets( Problem problem, Rel goal ) throws
          UnknownVariableException {
      
      {//Assumptions
          Collection<Var> flattened = new HashSet<Var>();
          CodeGenerator.unfoldVarsToSet( goal.getInputs(), flattened );
          problem.getCurrentContext().getKnownVars().addAll( flattened );
          problem.getAssumptions().addAll( flattened );
      }
      
      {//Goals
          Set<Var> goals = new LinkedHashSet<Var>();
          CodeGenerator.unfoldVarsToSet( goal.getOutputs(), goals );
          problem.getCurrentContext().addGoals( goals );
      }

  }
}
