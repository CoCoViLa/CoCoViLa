/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static ee.ioc.cs.vsle.synthesize.CodeGenerator.*;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for the whole planning
 * 
 * @author pavelg
 * 
 */
public class DepthFirstPlanner implements IPlanner {

    private static final Logger logger = LoggerFactory.getLogger(DepthFirstPlanner.class);

    private int maxDepth = s_maxDepth;
    private final boolean isSubtaskRepetitionAllowed = s_isSubtaskRepetitionAllowed;
    private final boolean isIncremental = s_isIncremental;
    private final boolean isOptDisabled = s_disableOptimizationInSubtasks;
    private Map<SubtaskRel, EvaluationAlgorithm> indSubtasks = new LinkedHashMap<SubtaskRel, EvaluationAlgorithm>();
    private boolean computeAll;
    private boolean nested;
    
    /**
     * private constructor
     */
    private DepthFirstPlanner() {
    }

    /**
     * @return DepthFirstPlanner singleton
     */
    public static DepthFirstPlanner getInstance() {
        return new DepthFirstPlanner();
    }

    /**
     * @return description of this planner
     */
    public String getDescription() {
        return "Depth First";
    }

    /**
     * This method is the access point to the planning procedure. Initially, it
     * adds all variables from axioms to the set of found vars, then does the
     * linear planning. If lp does not solve the problem and there are subtasks,
     * goal-driven recursive planning with backtracking is invoked. Planning is
     * performed until no new variables are introduced into the algorithm.
     */
    public EvaluationAlgorithm invokePlaning( Problem problem, boolean _computeAll ) {
        long startTime = System.currentTimeMillis();

        computeAll = _computeAll;
        EvaluationAlgorithm algorithm = new EvaluationAlgorithm();

        PlanningContext context = problem.getCurrentContext();
        
        // add all axioms at the beginning of an algorithm
        Collection<Var> flattened = new HashSet<Var>();
        for ( Iterator<Rel> axiomIter = problem.getAxioms().iterator(); axiomIter.hasNext(); ) {
            Rel rel = axiomIter.next();
            
            unfoldVarsToSet( rel.getOutputs(), flattened );
            
            //do not overwrite values of variables that come via args of compute() or as inputs of independent subtasks
            if( !problem.getAssumptions().containsAll( flattened ) 
                    //do not overwrite values of already known variables.
                    //typically this is the case when a value of a variable 
                    //is given in a scheme via a properties window
//                    && !problem.getKnownVars().containsAll( flattened ) 
               ) {
                algorithm.addRel( rel );
            }
            axiomIter.remove();
            context.getKnownVars().addAll( flattened );
            flattened.clear();
        }

        context.getFoundVars().addAll( context.getKnownVars() );

        //remove all known vars with no relations
        for( Iterator<Var> varIter = context.getKnownVars().iterator(); varIter.hasNext(); ) {
            if( varIter.next().getRels().isEmpty() ) {
                varIter.remove();
            }
        }
        
        //start planning
        if ( problem.getRelsWithSubtasks().isEmpty() && linearForwardSearch( context, algorithm, computeAll ) ) {
            if ( isLinearLoggingOn() )
                logger.debug("Problem solved without subtasks");
        } else if ( !problem.getRelsWithSubtasks().isEmpty() && subtaskPlanning( problem, algorithm ) ) {
            if ( isLinearLoggingOn() )
                logger.debug("Problem solved with subtasks");
        } else if (!computeAll) {
            if ( isLinearLoggingOn() )
                logger.debug("Problem not solved");
        }

        if ( !nested ) {
            logger.info("Planning time: " + (System.currentTimeMillis() - startTime) + "ms.");
        }
        return algorithm;
    }

    /**
     * Linear forward search algorithm
     * 
     * @param p
     * @param algorithm
     * @param targetVars
     * @param _computeAll
     * @return
     */
    private boolean linearForwardSearch( PlanningContext context, EvaluationAlgorithm algorithm, boolean _computeAll ) {

        /*
         * while iterating through hashset, items cant be removed from/added to
         * that set. Theyre collected into these sets and added/removedall
         * together after iteration is finished
         */
        Set<Var> newVars = new LinkedHashSet<Var>();
        Set<Var> relOutputs = new LinkedHashSet<Var>();
        Set<Var> removableVars = new LinkedHashSet<Var>();

        boolean changed = true;

        if ( isLinearLoggingOn() )
            logger.debug("------Starting linear planning with (sub)goals: " + context.getRemainingGoals() + "--------");

        if ( isLinearLoggingOn() )
            logger.debug("Algorithm " + algorithm);

        int counter = 1;

        while ( ( !_computeAll && changed && !context.getRemainingGoals().isEmpty() ) 
                || ( changed && _computeAll ) ) {

            if ( isLinearLoggingOn() )
                logger.debug("----Iteration " + counter + " ----");

            counter++;
            changed = false;
            
            // iterate through all knownvars
            if ( isLinearLoggingOn() )
                logger.debug("Known:" + context.getKnownVars());

            for ( Var var : context.getKnownVars() ) {

                if ( isLinearLoggingOn() )
                    logger.debug("Current Known: " + var);

                // Check the relations of all components
                for ( Rel rel : var.getRels() ) {
                    if ( isLinearLoggingOn() )
                        logger.debug("And its rel: " + rel);
                    if ( context.isAvailableRel( rel ) ) {
                        context.removeUnknownInput( rel, var );

                        if ( isLinearLoggingOn() )
                            logger.debug("problem contains it " + rel);

                        removableVars.add( var );

                        if ( context.isRelReadyToUse( rel )
                                && rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {

                            if ( isLinearLoggingOn() )
                                logger.debug("rel is ready to be used " + rel);

                            boolean relIsNeeded = false;

                            if ( isLinearLoggingOn() )
                                logger.debug("its outputs " + rel.getOutputs());

                            for ( Var relVar : rel.getOutputs() ) {

                                if ( !context.getFoundVars().contains( relVar ) ) {
                                    relIsNeeded = true;
                                }
                            }

                            if ( rel.getOutputs().isEmpty() ) {
                                relIsNeeded = true;
                            }
                            if ( isLinearLoggingOn() )
                                logger.debug("relIsNeeded " + relIsNeeded);

                            if ( relIsNeeded ) {

                                if ( isLinearLoggingOn() )
                                    logger.debug("needed rel:  " + rel);

                                if ( !rel.getOutputs().isEmpty() ) {
                                    relOutputs.clear();
                                    unfoldVarsToSet( rel.getOutputs(), relOutputs );
                                    newVars.addAll( relOutputs );
                                    context.getFoundVars().addAll( relOutputs );
                                }
                                algorithm.addRel( rel );
                                if ( isLinearLoggingOn() )
                                    logger.debug("algorithm " + algorithm);
                            }

                            context.removeRel( rel );
                            changed = true;
                        }
                    }
                }
            }

            // remove targets if they have already been found
            for ( Iterator<Var> targetIter = context.getRemainingGoals().iterator(); targetIter.hasNext(); ) {
                Var targetVar = targetIter.next();
                if ( context.getFoundVars().contains( targetVar ) ) {
                    targetIter.remove();
                }
            }
            
            if ( isLinearLoggingOn() )
                logger.debug("foundvars " + context.getFoundVars());

            context.getKnownVars().addAll( newVars );
            context.getKnownVars().removeAll( removableVars );
            newVars.clear();
        }
        if ( isLinearLoggingOn() )
            logger.debug("algorithm " + algorithm);

        if ( !_computeAll ) {
            Optimizer.optimize( context, algorithm );

            if ( isLinearLoggingOn() )
                logger.debug("optimized algorithm " + algorithm);
        }

        if ( isLinearLoggingOn() )
            logger.debug("\n---!!!Finished linear planning!!!---\n");

        return context.getRemainingGoals().isEmpty() 
                || context.getFoundVars().containsAll( context.getAllGoals() );
    }

    private boolean subtaskPlanning( Problem problem, EvaluationAlgorithm algorithm ) {

        if ( isSubtaskLoggingOn() )
            logger.debug("!!!--------- Starting Planning With Subtasks ---------!!!");

        final int maxDepthBackup = maxDepth;
        if( isSubtaskLoggingOn() )
            logger.debug("maxDepthBackup:" + maxDepthBackup + " sbt: " + problem.getRelsWithSubtasks().size());
        PlanningContext context = problem.getCurrentContext();
        
        try {

            Set<Rel> relsWithSubtasks = new LinkedHashSet<Rel>( problem.getRelsWithSubtasks() );
            
            if( isIncremental ) {
                int incrementalDepth = 0;

                while( incrementalDepth <= ( isSubtaskRepetitionAllowed ? maxDepthBackup: problem.getRelsWithSubtasks().size()-1 ) ) {
                    if( isSubtaskLoggingOn() )
                        logger.debug("Incremental dfs, with max depth " + (incrementalDepth + 1)
                                + " and " + problem.getRelsWithSubtasks().size() + " subtasks to solve");
                    
                    maxDepth = incrementalDepth++;
                    
                    //if we need to compute some specific goals, after reaching a certain depth, but not the maximal depth, 
                    //the problem may be solved and there is no need to go any deeper.
                    if( subtaskPlanningImpl( context, relsWithSubtasks, algorithm, new LinkedList<Rel>(), 0 ) ) {
                        if( isSubtaskLoggingOn() )
                            logger.debug("The problem was solved during idfs after some intermediate MLB");
                        return true;
                    }

                    if( isSubtaskLoggingOn() )
                        logger.debug("Unsolved subtask left: " + problem.getRelsWithSubtasks().size());
                }

                if( isSubtaskLoggingOn() )
                    logger.debug("Fininshed incremental dfs");

            } else {
                if ( !isSubtaskRepetitionAllowed ) {
                    maxDepth = problem.getRelsWithSubtasks().size() - 1;
                }

                if ( isSubtaskLoggingOn() )
                    logger.debug("Starting subtask dfs with maxDepth: " + (maxDepth + 1));

                if( subtaskPlanningImpl( context, relsWithSubtasks, algorithm, new LinkedList<Rel>(), 0 ) ) {
                    if( isSubtaskLoggingOn() )
                        logger.debug("The problem was solved during dfs after some intermediate MLB");
                    return true;
                }
            }

        } finally {
            if( isSubtaskLoggingOn() )
                logger.debug("Fininshed dfs");

            maxDepth = maxDepthBackup;
            indSubtasks.clear();
        }

        if ( isSubtaskLoggingOn() )
            logger.debug("Invoking final linear planning");
        
        return linearForwardSearch( context, algorithm, computeAll );
    }

    /**
     * Goal-driven recursive (depth-first, exhaustive) search with backtracking
     * 
     * @param problem
     * @param algorithm
     * @param subtaskRelsInPath
     * @param depth
     */
    private boolean subtaskPlanningImpl( PlanningContext context, Set<Rel> relsWithSubtasks,
            EvaluationAlgorithm algorithm, LinkedList<Rel> subtaskRelsInPath, int depth ) {

        Set<Rel> relsWithSubtasksCopy = new LinkedHashSet<Rel>( relsWithSubtasks );

        Set<Rel> relsWithSubtasksToRemove = new LinkedHashSet<Rel>();
        
        boolean firstMLB = true;
        
        // start building Maximal Linear Branch (MLB)
        MLB: while ( !relsWithSubtasksCopy.isEmpty() ) {

            if ( isSubtaskLoggingOn() ) {
                String print = p( depth ) + "Starting new MLB with: ";
                for ( Rel rel : relsWithSubtasksCopy ) {
                    print += "\n" + p( depth ) + "  " + rel.getParent().getFullName() + " : " + rel.getDeclaration();
                }
                /*
                print += "\n" + p( depth ) + " All remaining rels in problem:";
                for ( Rel rel : problem.getAllRels() ) {
                    print += "\n" + p( depth ) + " " + rel.getParentObjectName() + " : " + rel.getDeclaration();
                }
                print += "\n" + p( depth ) + "All found variables: ";
                for ( Var var : problem.getFoundVars() ) {
                    print += "\n" + p( depth ) + " " + var.toString();
                }
                */
                logger.debug(print);
            }

            //if this is a first attempt to construct an MLB to solve a subtask(i.e. depth>0),
            //do not invoke linear planning because it has already been done
            if( ( depth == 0 ) || !firstMLB ) {
                
                boolean solvedIntermediately = 
                    linearForwardSearch( context, algorithm, true );

                //Having constructed some MLBs the (sub)problem may be solved 
                //and there is no need in wasting precious time planning unnecessary branches
                if( solvedIntermediately 
                        && (    //on the top level optimize only if computing goals
                                ( depth == 0 && !computeAll ) 
                                //otherwise (inside subtasks) always optimize
                                || ( depth != 0 )
                        ) ) {
                    //If the problem is solved, optimize and return
                    if( !isOptDisabled )
                        Optimizer.optimize( context, algorithm );
                    return true;
                }
            } else {
                firstMLB = false;
            }
            
            // or children
            OR: for ( Iterator<Rel> subtaskRelIterator = relsWithSubtasksCopy.iterator(); subtaskRelIterator.hasNext(); ) {

                Rel subtaskRel = subtaskRelIterator.next();

                if ( isSubtaskLoggingOn() )
                    logger.debug(p(depth) + "OR: depth: " + (depth + 1) + " rel - " + subtaskRel.getParent().getFullName() + " : "
                            + subtaskRel.getDeclaration());
                
                if ( subtaskRel.equals( subtaskRelsInPath.peekLast() )
                        || ( !context.isRelReadyToUse(subtaskRel) ) 
                        || context.getFoundVars().containsAll( subtaskRel.getOutputs() )
                        || ( !isSubtaskRepetitionAllowed && subtaskRelsInPath.contains( subtaskRel ) )) {
                    
                    if ( isSubtaskLoggingOn() ) {
                        logger.debug(p(depth) + "skipped");
                        if ( !context.isRelReadyToUse(subtaskRel) ) {
                            logger.debug(p(depth) + "because it has unknown inputs");//TODO print unknown
                        } else if ( context.getFoundVars().containsAll( subtaskRel.getOutputs() ) ) {
                            logger.debug(p(depth) + "because all outputs in FoundVars");
                        } else if( subtaskRel.equals( subtaskRelsInPath.peekLast() ) ) {
                            logger.debug(p(depth) + "because it is nested in itself");
                        } else if( !isSubtaskRepetitionAllowed && subtaskRelsInPath.contains( subtaskRel ) ) {
                            logger.debug(p(depth) + "This rel with subtasks is already in use, path: " + subtaskRelsInPath);
                        }
                    }
                    continue OR;
                }

                LinkedList<Rel> newPath = new LinkedList<Rel>( subtaskRelsInPath );
                newPath.add( subtaskRel );

                PlanningResult result = new PlanningResult( subtaskRel, true );
                
                // this is true if all subtasks are solvable
                boolean allSolved = true;
                // and children
                AND: for ( SubtaskRel subtask : subtaskRel.getSubtasks() ) {
                    if ( isSubtaskLoggingOn() )
                        logger.debug(p(depth) + "AND: subtask - " + subtask);

                    EvaluationAlgorithm sbtAlgorithm = null;
                    
                    //////////////////////INDEPENDENT SUBTASK////////////////////////////////////////
                    if ( subtask.isIndependent() ) {
                        if ( isSubtaskLoggingOn() )
                            logger.debug("Independent!!!");

                        if ( subtask.isSolvable() == null ) {
                            if ( isSubtaskLoggingOn() )
                                logger.debug("Start solving independent subtask " + subtask.getDeclaration());
                            // independent subtask is solved only once
                            Problem problemContext = subtask.getContext();
                            DepthFirstPlanner planner = new DepthFirstPlanner();
                            planner.indSubtasks = indSubtasks;
                            planner.nested = true;
                            sbtAlgorithm = planner.invokePlaning( problemContext, isOptDisabled );
                            PlanningContext indCntx = problemContext.getCurrentContext();
                            boolean solved = indCntx.getFoundVars().containsAll( indCntx.getAllGoals() );
                            if ( solved ) {
                                subtask.setSolvable( Boolean.TRUE );
                                indSubtasks.put( subtask, sbtAlgorithm );
                                if ( isSubtaskLoggingOn() )
                                    logger.debug("Solved " + subtask.getDeclaration());
                            } else {
                                subtask.setSolvable( Boolean.FALSE );
                                if ( RuntimeProperties.isLogInfoEnabled() ) {
                                    logger.debug("Unable to solve " + subtask.getDeclaration());
                                }
                            }
                            allSolved &= solved;
                        } else if ( subtask.isSolvable() == Boolean.TRUE ) {
                            if ( isSubtaskLoggingOn() )
                                logger.debug("Already solved");
                            allSolved &= true;
                            sbtAlgorithm = indSubtasks.get( subtask );
                        } else {
                            if ( isSubtaskLoggingOn() )
                                logger.debug("Not solvable");
                            allSolved &= false;
                        }
                        if ( isSubtaskLoggingOn() )
                            logger.debug("End of independent subtask " + subtask);
                        
                        if ( !allSolved ) {
                            continue OR;
                        }
                        
                        assert sbtAlgorithm != null;
                        
                        result.addSubtaskAlgorithm( subtask, sbtAlgorithm );
                    } 
                    //////////////////////DEPENDENT SUBTASK//////////////////////////////////////
                    else {
                        // lets clone the environment
                        PlanningContext newContext = prepareNewContext( context, subtask );
                        
                        sbtAlgorithm = new EvaluationAlgorithm();
                        
                        //during linear planning, if some goals are found, they are removed from the set "goals" 
                        boolean solved = linearForwardSearch( newContext, sbtAlgorithm,
                                // do not optimize here, because the solution may require additional rels with subtasks
                                true );

                        if ( solved ) {
                            if ( isSubtaskLoggingOn() )
                                logger.debug(p(depth) + "SOLVED subtask: " + subtask);
                            
                            if( !isOptDisabled ) {
                                //if a subtask has been solved, optimize its algorithm
                                Optimizer.optimize( newContext, sbtAlgorithm );
                            }
                            
                            result.addSubtaskAlgorithm( subtask, sbtAlgorithm );
                            allSolved &= solved;
                            continue AND;
                        } else if ( !solved && ( depth == maxDepth ) ) {
                            if ( isSubtaskLoggingOn() )
                                logger.debug(p(depth) + "NOT SOLVED and cannot go any deeper, subtask: " + subtask);
                            continue OR;
                        }

                        if ( isSubtaskLoggingOn() )
                            logger.debug(p(depth) + "Recursing deeper");

                        solved = subtaskPlanningImpl( newContext, relsWithSubtasks, sbtAlgorithm, newPath, depth + 1 );

                        if ( isSubtaskLoggingOn() )
                            logger.debug(p(depth) + "Back to depth " + (depth + 1));

                        //the linear planning has been performed at the end of MLB on the depth+1,
                        //if the problem was solved, there is no need to run linear planning again
                        if( ( solved || ( solved = linearForwardSearch( newContext, sbtAlgorithm, true ) ) ) 
                                && !isOptDisabled ) {
                            // if solved, optimize here with full list of goals in order to get rid of
                            // unnecessary subtask instances and other relations
                            Optimizer.optimize( newContext, sbtAlgorithm );
                        }
                        
                        if ( isSubtaskLoggingOn() )
                            logger.debug(p(depth) + (solved ? "" : "NOT") + " SOLVED subtask: " + subtask);

                        allSolved &= solved;

                        // if at least one subtask is not solvable, try another
                        // branch
                        if ( !allSolved ) {
                            continue OR;
                        }
                        
                        result.addSubtaskAlgorithm( subtask, sbtAlgorithm );
                    }

                }//AND

                if ( allSolved ) {
                    algorithm.add( result );

                    Set<Var> newVars = new LinkedHashSet<Var>();

                    unfoldVarsToSet( subtaskRel.getOutputs(), newVars );
                    
                    context.getKnownVars().addAll( newVars );
                    context.getFoundVars().addAll( newVars );

                    subtaskRelIterator.remove();

                    if ( isSubtaskLoggingOn() ) {
                        logger.debug(p(depth) + "SOLVED ALL SUBTASKS for " + subtaskRel.getParent().getFullName() + " : "
                                + subtaskRel.getDeclaration());
                        logger.debug(p(depth) + "Updating the problem graph and continuing building new MLB");
                    }

                    //this is used for incremental dfs
                    if( depth == 0 ) {
                        relsWithSubtasksToRemove.add( subtaskRel );
                    }
                    
                    continue MLB;

                }
                if ( isSubtaskLoggingOn() )
                    logger.debug(p(depth) + "NOT SOLVED ALL subtasks, removing from path " + subtaskRel.getParent().getFullName()
                            + " : " + subtaskRel.getDeclaration());
                newPath.remove( subtaskRel );
            }//end OR

            // exit loop because there are no more rels with subtasks to be
            // applied
            // (i.e. no more rels can introduce new variables into the
            // algorithm)
            if ( isSubtaskLoggingOn() )
                logger.debug(p(depth) + "No more MLB can be constructed");
            break MLB;
        }
        
        //incremental dfs, remove solved subtasks
        if( depth == 0 ) {
            relsWithSubtasks.removeAll( relsWithSubtasksToRemove );
        }
        
        return false;
    }

    private PlanningContext prepareNewContext( PlanningContext context, Rel subtask ) {
        
        PlanningContext newContext = context.getCopy();
        // [x->y] assume x is known
        Set<Var> allSubtaskInputs = new LinkedHashSet<Var>();
        unfoldVarsToSet( subtask.getInputs(), allSubtaskInputs );
        newContext.getKnownVars().addAll( allSubtaskInputs );
        newContext.getFoundVars().addAll( allSubtaskInputs );
        Set<Var> goals = new LinkedHashSet<Var>();
        unfoldVarsToSet( subtask.getOutputs(), goals );
        newContext.addGoals( goals );
        return newContext;
    }

    private static volatile int s_maxDepth = 2;// 0..2, i.e. 3
    private static volatile int s_maxRept = 1;
    //private static volatile int s_limit = 0;//0 - depth; 1 - repetition; ...
    private static volatile boolean s_isSubtaskRepetitionAllowed = false;
    private static volatile boolean s_isIncremental = false;
    private static volatile boolean s_disableOptimizationInSubtasks = false;
    
    private String p( int depth ) {
        String s = "";
        for ( int i = 0; i < depth; i++ ) {
            s += "\t";
        }
        return s;
    }

    private static boolean linearLoggingOn = false;
    private static boolean subtaskLoggingOn = false;

    public Component getCustomOptionComponent() {
        
        /* DEPTH */
        final JSpinner jspnMaxDepth = new JSpinner( new SpinnerNumberModel( s_maxDepth + 1, 1, 100, 1 ) );
        jspnMaxDepth.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer) ( (JSpinner) e.getSource() ).getValue();
                s_maxDepth = value - 1;

                logger.debug("maxDepth " + (s_maxDepth + 1));
            }
        } );
        final JRadioButton jrbMaxDepth = new JRadioButton( "Depth" );
        final JLabel lblMaxDepth = new JLabel( "Depth: " );
        
        final JPanel jpMaxDepthSub = new JPanel() {
            @Override
            public void setEnabled( boolean b ) {
                super.setEnabled( b );
                jspnMaxDepth.setEnabled( b );
                lblMaxDepth.setEnabled( b );
            }
        };
        jpMaxDepthSub.setBorder( BorderFactory.createEmptyBorder( 0, 15, 0, 0 ) );
        jpMaxDepthSub.add( lblMaxDepth );
        jpMaxDepthSub.add( jspnMaxDepth );
        
        ///
        final JPanel jpMaxDepth = new JPanel() {
            @Override
            public void setEnabled( boolean b ) {
                super.setEnabled( b );
                jrbMaxDepth.setEnabled( b );
                jpMaxDepthSub.setEnabled( b );
            }
        };
        
        jpMaxDepth.setLayout( new BoxLayout( jpMaxDepth, BoxLayout.Y_AXIS ) );
        
        jpMaxDepth.add( GuiUtil.addComponentAsFlow( jrbMaxDepth, FlowLayout.LEFT ) );
        jpMaxDepth.add( GuiUtil.addComponentAsFlow( jpMaxDepthSub, FlowLayout.RIGHT ) );
        
        /* REPETITION */
        final JSpinner jspnMaxRept = new JSpinner( new SpinnerNumberModel( s_maxRept, 1, 100, 1 ) );
        jspnMaxRept.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                s_maxRept = (Integer) ( (JSpinner) e.getSource() ).getValue();

                logger.debug("maxRept " + s_maxRept);
            }
        } );
        
        final JRadioButton jrbMaxRept = new JRadioButton( "Repetition" );
        
        final JLabel lblMaxRept = new JLabel( "Count: " );
        
        final JPanel jpMaxReptSub = new JPanel() {
            @Override
            public void setEnabled( boolean b ) {
                super.setEnabled( b );
                jspnMaxRept.setEnabled( b );
                lblMaxRept.setEnabled( b );
            }
        };
        jpMaxReptSub.setBorder( BorderFactory.createEmptyBorder( 0, 15, 0, 0 ) );
        jpMaxReptSub.add( lblMaxRept );
        jpMaxReptSub.add( jspnMaxRept );
        
        final JPanel jpMaxRept = new JPanel() {
            @Override
            public void setEnabled( boolean b ) {
                super.setEnabled( b );
                jrbMaxRept.setEnabled( b );
                jpMaxReptSub.setEnabled( b );
            }
        };
        jpMaxRept.setLayout( new BoxLayout( jpMaxRept, BoxLayout.Y_AXIS ) );
        
        jpMaxRept.add( GuiUtil.addComponentAsFlow( jrbMaxRept, FlowLayout.LEFT ) );
        jpMaxRept.add( GuiUtil.addComponentAsFlow( jpMaxReptSub, FlowLayout.RIGHT ) );
        
        ///////////////////////////////////////
        final JLabel lbl = new JLabel( "Limit search by:", SwingConstants.LEFT );
        
        final JPanel panel = new JPanel() {
            @Override
            public void setEnabled( boolean b ) {
                super.setEnabled( b );
                lbl.setEnabled( b );
                jpMaxDepth.setEnabled( b );
                jpMaxRept.setEnabled( b );
            }
            @Override
            public void setVisible( boolean b ) {
                super.setVisible( b );
                final JPanel p = this;
//                SwingUtilities.invokeLater( new Runnable() {
//                    public void run() {
                        Window win = SwingUtilities.getWindowAncestor( p );
                        System.err.println(win);
                        if( win != null ) {
                            System.err.println( "packing");
                            win.pack();
                        }
//                    }
//                } );
            }
        };
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.setBorder( BorderFactory.createEmptyBorder( 0, 15, 0, 0 ) );
        panel.add( GuiUtil.addComponentAsFlow( lbl, FlowLayout.LEFT ) );
        panel.add( jpMaxDepth );
        panel.add( jpMaxRept );
        
        ButtonGroup bg = new ButtonGroup();
        bg.add( jrbMaxDepth );
        bg.add( jrbMaxRept );
        
        /* CHECKBOX */
        final JCheckBox chboxRepeat = new JCheckBox( "Allow subtask recursive repetition", s_isSubtaskRepetitionAllowed );

        chboxRepeat.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                if( s_isSubtaskRepetitionAllowed == chboxRepeat.isSelected() )
                    return;
                
                s_isSubtaskRepetitionAllowed = chboxRepeat.isSelected();
                panel.setVisible( s_isSubtaskRepetitionAllowed );
                
                logger.debug("m_isSubtaskRepetitionAllowed " + s_isSubtaskRepetitionAllowed);
            }
        } );

        panel.setVisible( s_isSubtaskRepetitionAllowed );
        
        final JCheckBox chboxIncremental = new JCheckBox( "Incremental", s_isIncremental );
        chboxIncremental.setToolTipText( "Incremental depth-first search" );
        
        chboxIncremental.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                
                if( s_isIncremental == chboxIncremental.isSelected() )
                    return;
                
                s_isIncremental = chboxIncremental.isSelected();

                logger.debug( "isIncremental " + s_isIncremental );
            }
        } );
               
        final JCheckBox chboxOptimize = new JCheckBox( "Disable optimization in subtasks", s_disableOptimizationInSubtasks );
        chboxOptimize.setToolTipText( "Use for debugging purposes" );
        
        chboxOptimize.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                
                if( s_disableOptimizationInSubtasks == chboxOptimize.isSelected() )
                    return;
                
                s_disableOptimizationInSubtasks = chboxOptimize.isSelected();

                logger.debug( "disableOptimizationInSubtasks " + s_disableOptimizationInSubtasks );
            }
        } );
        
        JPanel container1 = new JPanel();
        container1.setLayout( new BoxLayout( container1, BoxLayout.Y_AXIS ) );
        container1.setBorder( BorderFactory.createTitledBorder( "Planning settings" ) );
        container1.add( GuiUtil.addComponentAsFlow( chboxOptimize, FlowLayout.LEFT ) );
        container1.add( GuiUtil.addComponentAsFlow( chboxIncremental, FlowLayout.LEFT ) );
        container1.add( GuiUtil.addComponentAsFlow( chboxRepeat, FlowLayout.LEFT ) );
        container1.add( GuiUtil.addComponentAsFlow( panel, FlowLayout.LEFT ) );

        JPanel container2 = new JPanel( new GridLayout( 2, 0 ) );
        container2.setBorder( BorderFactory.createTitledBorder( "Logging options" ) );

        final JCheckBox linear = new JCheckBox( "Detailed linear planning", isLinearLoggingOn() );
        linear.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setLinearLoggingOn( linear.isSelected() );
            }
        } );
        container2.add( linear );
        final JCheckBox subtask = new JCheckBox( "Detailed subtask planning", isSubtaskLoggingOn() );
        subtask.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setSubtaskLoggingOn( subtask.isSelected() );
            }
        } );
        container2.add( subtask );

        JPanel main = new JPanel();
        main.setLayout( new BoxLayout( main, BoxLayout.Y_AXIS ) );
        main.add( container1 );
        main.add( container2 );

        return main;
    }

    /**
     * @param subtaskLoggingOn the subtaskLoggingOn to set
     */
    private void setSubtaskLoggingOn( boolean subtaskLoggingOn ) {
        DepthFirstPlanner.subtaskLoggingOn = subtaskLoggingOn;
    }

    /**
     * @return the subtaskLoggingOn
     */
    private boolean isSubtaskLoggingOn() {
        return subtaskLoggingOn || RuntimeProperties.isLogDebugEnabled();
    }

    /**
     * @param linearLoggingOn the linearLoggingOn to set
     */
    private void setLinearLoggingOn( boolean linearLoggingOn ) {
        DepthFirstPlanner.linearLoggingOn = linearLoggingOn;
    }

    /**
     * @return the linearLoggingOn
     */
    private boolean isLinearLoggingOn() {
        return linearLoggingOn || RuntimeProperties.isLogDebugEnabled();
    }
}
