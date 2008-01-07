/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import static ee.ioc.cs.vsle.synthesize.CodeGenerator.*;

/**
 * This class is responsible for the whole planning
 * 
 * @author pavelg
 * 
 */
public class DepthFirstPlanner implements IPlanner {

    private int maxDepth = s_maxDepth;
    private final boolean isSubtaskRepetitionAllowed = s_isSubtaskRepetitionAllowed;
    private final boolean isIncremental = s_isIncremental;
    
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
    public ArrayList<Rel> invokePlaning( Problem problem, boolean computeAll ) {
        long startTime = System.currentTimeMillis();

        ArrayList<Rel> algorithm = new ArrayList<Rel>();

        // manage axioms
        Collection<Var> flattened = new HashSet<Var>();
        for ( Iterator<Rel> axiomIter = problem.getAxioms().iterator(); axiomIter.hasNext(); ) {
            Rel rel = axiomIter.next();
            //do not overwrite values of variables that come via args of compute() or as inputs of independent subtasks
            unfoldVarsToSet( rel.getOutputs(), flattened );
            
            if( !problem.getAssumptions().containsAll( flattened ) ) {
                algorithm.add( rel );
            }
            axiomIter.remove();
            flattened.clear();
        }

        problem.getFoundVars().addAll( problem.getKnownVars() );

        if ( problem.getRelsWithSubtasks().isEmpty() && linearForwardSearch( problem, algorithm, problem.getGoals(), computeAll ) ) {
            if ( RuntimeProperties.isLogInfoEnabled() )
                db.p( "Problem solved without subtasks" );
        } else if ( !problem.getRelsWithSubtasks().isEmpty() ) {
            subtaskPlanning( problem, algorithm, computeAll );
        } else {
            if ( RuntimeProperties.isLogInfoEnabled() )
                db.p( "Problem not solved" );
        }

        if ( RuntimeProperties.isLogInfoEnabled() )
            db.p( "Planning time: " + ( System.currentTimeMillis() - startTime ) + "ms." );

        return algorithm;
    }

    /**
     * Linear forward search algorithm
     * 
     * @param p
     * @param algorithm
     * @param targetVars
     * @param computeAll
     * @return
     */
    private boolean linearForwardSearch( Problem p, List<Rel> algorithm, Set<Var> targetVars, boolean computeAll ) {

        /*
         * while iterating through hashset, items cant be removed from/added to
         * that set. Theyre collected into these sets and added/removedall
         * together after iteration is finished
         */
        Set<Var> newVars = new LinkedHashSet<Var>();
        Set<Var> relOutputs = new LinkedHashSet<Var>();
        Set<Var> removableVars = new LinkedHashSet<Var>();
        // backup goals for later optimization
        Set<Var> allTargetVars = new LinkedHashSet<Var>( targetVars );

        boolean changed = true;

        if ( isLinearLoggingOn() )
            db.p( "------Starting linear planning with (sub)goals: " + targetVars + "--------" );

        if ( isLinearLoggingOn() )
            db.p( "Algorithm " + algorithm );

        int counter = 1;

        while ( ( !computeAll && changed && !targetVars.isEmpty() ) || ( changed && computeAll ) ) {

            if ( isLinearLoggingOn() )
                db.p( "----Iteration " + counter + " ----" );

            counter++;
            changed = false;
            // remove targets if they're already known
            for ( Iterator<Var> targetIter = targetVars.iterator(); targetIter.hasNext(); ) {
                Var targetVar = targetIter.next();
                if ( p.getFoundVars().contains( targetVar ) ) {
                    targetIter.remove();
                }
            }
            // iterate through all knownvars
            if ( isLinearLoggingOn() )
                db.p( "Known:" + p.getKnownVars() );

            for ( Var var : p.getKnownVars() ) {

                if ( isLinearLoggingOn() )
                    db.p( "Current Known: " + var );

                // Check the relations of all components
                for ( Rel rel : var.getRels() ) {
                    if ( isLinearLoggingOn() )
                        db.p( "And its rel: " + rel );
                    if ( p.getAllRels().contains( rel ) ) {
                        if ( !var.getField().isConstant() ) {
                            rel.removeUnknownInput( var );
                        }

                        if ( isLinearLoggingOn() )
                            db.p( "problem contains it " + rel + " unknownInputs: " + rel.getUnknownInputCount() );

                        removableVars.add( var );

                        if ( rel.getUnknownInputCount() == 0 && rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {

                            if ( isLinearLoggingOn() )
                                db.p( "rel is ready to be used " + rel );

                            boolean relIsNeeded = false;

                            if ( isLinearLoggingOn() )
                                db.p( "tema outputsid " + rel.getOutputs() );

                            for ( Var relVar : rel.getOutputs() ) {

                                if ( !p.getFoundVars().contains( relVar ) ) {
                                    relIsNeeded = true;
                                }
                            }

                            if ( rel.getOutputs().isEmpty() ) {
                                relIsNeeded = true;
                            }
                            if ( isLinearLoggingOn() )
                                db.p( "relIsNeeded " + relIsNeeded );

                            if ( relIsNeeded ) {

                                if ( isLinearLoggingOn() )
                                    db.p( "ja vajati " + rel );

                                if ( !rel.getOutputs().isEmpty() ) {
                                    relOutputs.clear();
                                    unfoldVarsToSet( rel.getOutputs(), relOutputs );
                                    newVars.addAll( relOutputs );
                                    p.getFoundVars().addAll( relOutputs );
                                }
                                algorithm.add( rel );
                                if ( isLinearLoggingOn() )
                                    db.p( "algorithm " + algorithm );
                            }

                            p.getAllRels().remove( rel );
                            changed = true;
                        }
                    }
                }
            }

            if ( isLinearLoggingOn() )
                db.p( "foundvars " + p.getFoundVars() );

            p.getKnownVars().addAll( newVars );
            p.getKnownVars().removeAll( removableVars );
            newVars.clear();
        }
        if ( isLinearLoggingOn() )
            db.p( "algorithm " + algorithm );

        if ( !computeAll ) {
            Optimizer.optimize( p, algorithm, new HashSet<Var>( allTargetVars ) );

            if ( isLinearLoggingOn() )
                db.p( "optimized algorithm " + algorithm );
        }

        if ( isLinearLoggingOn() )
            db.p( "\n---!!!Finished linear planning!!!---\n" );

        return targetVars.isEmpty() || p.getFoundVars().containsAll( allTargetVars );
    }

    private void subtaskPlanning( Problem problem, ArrayList<Rel> algorithm, boolean computeAll ) {

        if ( isSubtaskLoggingOn() )
            db.p( "!!!--------- Starting Planning With Subtasks ---------!!!" );

        final int maxDepthBackup = maxDepth;
        boolean incremental = isIncremental;
        
        if( incremental ) {
            int incrementalDepth = 0;
            
            while( incrementalDepth < ( isSubtaskRepetitionAllowed ? maxDepthBackup : problem.getRelsWithSubtasks().size() ) ) {
                if( isSubtaskLoggingOn() )
                    db.p( "Incremental dfs, with max depth " + ( incrementalDepth + 1 ) 
                            + " and " + problem.getRelsWithSubtasks().size() + " subtasks to solve" );
                maxDepth = incrementalDepth++;
                subtaskPlanningImpl( problem, algorithm, new LinkedHashSet<Rel>(), 0, new ArrayList<SubtaskRel>(), computeAll );
                if( isSubtaskLoggingOn() )
                    db.p( "Unsolved subtask left: " + problem.getRelsWithSubtasks().size() );
            }
            if( isSubtaskLoggingOn() )
                db.p( "Fininshed incremental dfs" );
            if( linearForwardSearch( problem, algorithm, problem.getGoals(), computeAll ) ) {
                if( isSubtaskLoggingOn() )
                    db.p( "All solved" ); 
            } else {
                if( isSubtaskLoggingOn() )
                    db.p( "Not solved" ); 
            }
            
        } else {
            if ( !isSubtaskRepetitionAllowed ) {
                maxDepth = problem.getRelsWithSubtasks().size() - 1;
            }

            if ( isSubtaskLoggingOn() )
                db.p( "maxDepth: " + ( maxDepth + 1 ) );

            subtaskPlanningImpl( problem, algorithm, new LinkedHashSet<Rel>(), 0, new ArrayList<SubtaskRel>(), computeAll );

            linearForwardSearch( problem, algorithm, problem.getGoals(), computeAll );
        }

        maxDepth = maxDepthBackup;
    }

    /**
     * Goal-driven recursive (depth-first, exhaustive) search with backtracking
     * 
     * @param problem
     * @param algorithm
     * @param subtaskRelsInPath
     * @param depth
     * @param computeAll
     */
    private void subtaskPlanningImpl( Problem problem, List<Rel> algorithm, Set<Rel> subtaskRelsInPath, int depth,
            List<SubtaskRel> indSubtasks, boolean computeAll ) {

        Set<Rel> relsWithSubtasks = new LinkedHashSet<Rel>( problem.getRelsWithSubtasks() );

        Set<Rel> relsWithSubtasksToRemove = new LinkedHashSet<Rel>();
        
        // start building Maximal Linear Branch (MLB)
        MLB: while ( !relsWithSubtasks.isEmpty() ) {

            if ( isSubtaskLoggingOn() ) {
                String print = p( depth ) + "Starting new MLB with: ";
                for ( Rel rel : relsWithSubtasks ) {
                    print += "\n" + p( depth ) + "  " + rel.getParentObjectName() + " : " + rel.getDeclaration();
                }
//                 print += "\n" + p( depth ) + " All remaining rels in problem:";
//                for ( Rel rel : problem.getAllRels() ) {
//                    print += "\n" + p( depth ) + " " + rel.getParentObjectName() + " : " + rel.getDeclaration();
//                }
//                print += "\n" + p( depth ) + "All found variables: ";
//                for ( Var var : problem.getFoundVars() ) {
//                    print += "\n" + p( depth ) + " " + var.toString();
//                }
                db.p( print );
            }

            // definitely know here that subtasks will be used after
            linearForwardSearch( problem, algorithm, new LinkedHashSet<Var>( 0 ), true );

            // or children
            OR: for ( Iterator<Rel> subtaskRelIterator = relsWithSubtasks.iterator(); subtaskRelIterator.hasNext(); ) {

                Rel subtaskRel = subtaskRelIterator.next();

                if ( isSubtaskLoggingOn() )
                    db.p( p( depth ) + "OR: depth: " + ( depth + 1 ) + " rel - " + subtaskRel.getParentObjectName() + " : "
                            + subtaskRel.getDeclaration() );
                if ( ( subtaskRel.getUnknownInputCount() > 0 ) || problem.getFoundVars().containsAll( subtaskRel.getOutputs() ) ) {

                    if ( isSubtaskLoggingOn() ) {
                        db.p( p( depth ) + "skipped" );
                        if ( subtaskRel.getUnknownInputCount() > 0 ) {
                            db.p( p( depth ) + "because unknown inputs count = " + subtaskRel.getUnknownInputCount() + " "
                                    + subtaskRel.printUnknownInputs() );
                        } else if ( problem.getFoundVars().containsAll( subtaskRel.getOutputs() ) ) {
                            db.p( p( depth ) + "because all outputs in FoundVars" );
                        }
                    }
                    continue OR;
                }

                Set<Rel> newPath = new LinkedHashSet<Rel>();

                if ( !isSubtaskRepetitionAllowed && subtaskRelsInPath.contains( subtaskRel ) ) {
                    if ( isSubtaskLoggingOn() )
                        db.p( p( depth ) + "This rel with subtasks is already in use, path: " + newPath );
                    continue;
                } else if ( !isSubtaskRepetitionAllowed ) {
                    if ( isSubtaskLoggingOn() )
                        db.p( p( depth ) + "This rel with subtasks can be used, path: " + newPath );
                    newPath.addAll( subtaskRelsInPath );
                    newPath.add( subtaskRel );
                }

                // this is true if all subtasks are solvable
                boolean allSolved = true;
                // and children
                AND: for ( SubtaskRel subtask : subtaskRel.getSubtasks() ) {
                    if ( isSubtaskLoggingOn() )
                        db.p( p( depth ) + "AND: subtask - " + subtask );

                    if ( subtask.isIndependent() ) {
                        if ( isSubtaskLoggingOn() )
                            db.p( "Independent!!!" );

                        SubtaskRel initialSubtask;

                        int ind;

                        if ( ( ind = indSubtasks.indexOf( subtask ) ) > -1 ) {
                            initialSubtask = indSubtasks.get( ind );

                            if ( subtask != initialSubtask ) {
                                if ( subtask.isSolvable() == null )
                                    subtask.getAlgorithm().addAll( initialSubtask.getAlgorithm() );
                                subtask.setSolvable( initialSubtask.isSolvable() );
                                subtask = initialSubtask;
                            }
                        } else {
                            indSubtasks.add( subtask );
                        }

                        if ( subtask.isSolvable() == null ) {
                            if ( isSubtaskLoggingOn() )
                                db.p( "Not solved yet" );
                            // independent subtask is solved only once
                            Problem context = subtask.getContext();
                            if ( RuntimeProperties.isLogInfoEnabled() ) {
                                db.p( "Start solving independent subtask " + subtask.getDeclaration() );
                            }
                            ArrayList<Rel> alg = invokePlaning( context, computeAll );
                            boolean solved = context.getFoundVars().containsAll( context.getGoals() );
                            if ( solved ) {
                                subtask.setSolvable( Boolean.TRUE );
                                subtask.getAlgorithm().addAll( alg );
                                if ( RuntimeProperties.isLogInfoEnabled() ) {
                                    db.p( "Solved " + subtask.getDeclaration() );
                                }
                            } else {
                                subtask.setSolvable( Boolean.FALSE );
                                if ( RuntimeProperties.isLogInfoEnabled() ) {
                                    db.p( "Unable to solve " + subtask.getDeclaration() );
                                }
                            }
                            allSolved &= solved;
                        } else if ( subtask.isSolvable() == Boolean.TRUE ) {
                            if ( isSubtaskLoggingOn() )
                                db.p( "Already solved" );
                            allSolved &= true;
                        } else {
                            if ( isSubtaskLoggingOn() )
                                db.p( "Not solvable" );
                            allSolved &= false;
                        }
                        if ( isSubtaskLoggingOn() )
                            db.p( "End of independent subtask " + subtask );
                        
                        if ( !allSolved ) {
                            continue OR;
                        }
                    } else {
                        // lets clone the environment
                        Problem problemNew = problem.getCopy();
                        // we need fresh cloned subtask instance
                        SubtaskRel subtaskNew = problemNew.getSubtask( subtask );

                        prepareSubtask( problemNew, subtaskNew );

                        Set<Var> goals = new LinkedHashSet<Var>();
                        unfoldVarsToSet( subtaskNew.getOutputs(), goals );

                        boolean solved = linearForwardSearch( problemNew, subtaskNew.getAlgorithm(),
                        // never optimize here
                                goals, true );

                        if ( solved ) {
                            if ( isSubtaskLoggingOn() )
                                db.p( p( depth ) + "SOLVED subtask: " + subtaskNew );
                            subtask.getAlgorithm().addAll( subtaskNew.getAlgorithm() );
                            allSolved &= solved;
                            continue AND;
                        } else if ( !solved && ( depth == maxDepth ) ) {
                            if ( isSubtaskLoggingOn() )
                                db.p( p( depth ) + "NOT SOLVED and cannot go any deeper, subtask: " + subtaskNew );
                            continue OR;
                        }

                        if ( isSubtaskLoggingOn() )
                            db.p( p( depth ) + "Recursing deeper" );

                        List<Rel> newAlg = new ArrayList<Rel>( subtaskNew.getAlgorithm() );

                        subtaskPlanningImpl( problemNew, newAlg, newPath, depth + 1, indSubtasks, computeAll );

                        if ( isSubtaskLoggingOn() )
                            db.p( p( depth ) + "Back to depth " + ( depth + 1 ) );

                        solved = linearForwardSearch( problemNew, newAlg,
                        // always optimize here in order to get rid of
                        // unnecessary subtask instances
                                goals, computeAll );

                        if ( isSubtaskLoggingOn() )
                            db.p( p( depth ) + ( solved ? "" : "NOT" ) + " SOLVED subtask: " + subtaskNew );

                        allSolved &= solved;

                        // if at least one subtask is not solvable, try another
                        // branch
                        if ( !allSolved ) {
                            continue OR;
                        }
                        // copy algorithm from cloned subtask to old
                        subtask.getAlgorithm().addAll( newAlg );
                    }

                }

                if ( allSolved ) {
                    algorithm.add( subtaskRel );

                    Set<Var> newVars = new LinkedHashSet<Var>();

                    unfoldVarsToSet( subtaskRel.getOutputs(), newVars );
//                    System.err.println( "newVars " + newVars.size() + " " + newVars + 
//                            "\nproblem.getKnownVars() " + problem.getKnownVars().size() + " " + problem.getKnownVars() + 
//                            "\nproblem.getFoundVars() " + problem.getFoundVars().size() + " " + problem.getFoundVars() );
                    problem.getKnownVars().addAll( newVars );
                    problem.getFoundVars().addAll( newVars );

                    subtaskRelIterator.remove();

                    if ( isSubtaskLoggingOn() ) {
                        db.p( p( depth ) + "SOLVED ALL SUBTASKS for " + subtaskRel.getParentObjectName() + " : "
                                + subtaskRel.getDeclaration() );
                        db.p( p( depth ) + "Updating the problem graph and continuing building new MLB" );
                    }

                    //this is used for incremental dfs
                    if( depth == 0 ) {
                        relsWithSubtasksToRemove.add( subtaskRel );
                    }
                    
                    continue MLB;

                } else if ( !isSubtaskRepetitionAllowed ) {
                    if ( isSubtaskLoggingOn() )
                        db.p( p( depth ) + "NOT SOLVED ALL subtasks, removing from path " + subtaskRel.getParentObjectName()
                                + " : " + subtaskRel.getDeclaration() );
                    newPath.remove( subtaskRel );
                }
            }

            // exit loop because there are no more rels with subtasks to be
            // applied
            // (i.e. no more rels can introduce new variables into the
            // algorithm)
            if ( isSubtaskLoggingOn() ) {
                db.p( p( depth ) + "No more MLB can be constructed" );
            }
            break MLB;
        }
        
        //incremental dfs, remove solved subtasks
        if( depth == 0 ) {
            problem.getRelsWithSubtasks().removeAll( relsWithSubtasksToRemove );
        }
    }

    private void prepareSubtask( Problem problem, Rel subtask ) {
        // [x->y] assume x is known
        Set<Var> flatVars = new LinkedHashSet<Var>();
        unfoldVarsToSet( subtask.getInputs(), flatVars );
        problem.getKnownVars().addAll( flatVars );
        problem.getFoundVars().addAll( flatVars );
    }

    private static volatile int s_maxDepth = 2;// 0..2, i.e. 3
    private static volatile boolean s_isSubtaskRepetitionAllowed = false;
    private static volatile boolean s_isIncremental = false;
    
    private String p( int depth ) {
        String s = "\t";
        for ( int i = 0; i < depth; i++ ) {
            s += s;
        }
        return s;
    }

    private static boolean linearLoggingOn = false;
    private static boolean subtaskLoggingOn = false;

    public Component getCustomOptionComponent() {
        final JSpinner spinner = new JSpinner( new SpinnerNumberModel( s_maxDepth + 1, 1, 100, 1 ) );
        spinner.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer) ( (JSpinner) e.getSource() ).getValue();
                s_maxDepth = value - 1;

                if ( RuntimeProperties.isLogDebugEnabled() )
                    db.p( "maxDepth " + ( s_maxDepth + 1 ) );
            }
        } );
        final JPanel panel = new JPanel() {
            public void setEnabled( boolean b ) {
                super.setEnabled( b );
                spinner.setEnabled( b );
            }
        };
        final JLabel lblMaxDepth = new JLabel( "Max Depth: " );
        panel.add( lblMaxDepth );
        panel.add( spinner );

        final JCheckBox chboxRepeat = new JCheckBox( "Allow subtask recursive repetition", s_isSubtaskRepetitionAllowed );

        chboxRepeat.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                if( s_isSubtaskRepetitionAllowed == chboxRepeat.isSelected() )
                    return;
                
                s_isSubtaskRepetitionAllowed = chboxRepeat.isSelected();
                panel.setEnabled( s_isSubtaskRepetitionAllowed );
                lblMaxDepth.setEnabled( s_isSubtaskRepetitionAllowed );
                
                if ( RuntimeProperties.isLogDebugEnabled() )
                    db.p( "m_isSubtaskRepetitionAllowed " + s_isSubtaskRepetitionAllowed );
            }
        } );

        chboxRepeat.setSelected( s_isSubtaskRepetitionAllowed );
        
        final JCheckBox chboxIncremental = new JCheckBox( "Incremental" );

        chboxIncremental.addChangeListener( new ChangeListener() {

            public void stateChanged( ChangeEvent e ) {
                
                if( s_isIncremental == chboxIncremental.isSelected() )
                    return;
                
                s_isIncremental = chboxIncremental.isSelected();
                
//                if( isIncremental) {
//                    chboxRepeat.setSelected( false );
//                }
//                chboxRepeat.setEnabled( !isIncremental );
                
                if ( RuntimeProperties.isLogDebugEnabled() )
                    db.p( "isIncremental " + s_isIncremental );
            }
        } );
        
        chboxIncremental.setSelected( s_isIncremental );
               
        JPanel container1 = new JPanel( new GridLayout( 4, 0 ) );
        container1.setBorder( BorderFactory.createTitledBorder( "Planning settings" ) );
        container1.add( chboxIncremental );
        container1.add( chboxRepeat );
        container1.add( panel );

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
        this.subtaskLoggingOn = subtaskLoggingOn;
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
        this.linearLoggingOn = linearLoggingOn;
    }

    /**
     * @return the linearLoggingOn
     */
    private boolean isLinearLoggingOn() {
        return linearLoggingOn || RuntimeProperties.isLogDebugEnabled();
    }
}
