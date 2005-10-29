package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.db;

/**
 *
 * @author Pavel Grigorenko
 * @version 1.0
 */
public class Planner {

    private static Planner s_planner = null;

    private Problem m_problem = null;
    private ArrayList<Rel> m_algorithm = new ArrayList<Rel>();

    private Planner() {
    }

    public static Planner getInstance() {
        if ( s_planner == null ) {
            s_planner = new Planner();
        }
        return s_planner;
    }

    public ArrayList invokePlaning( Problem problem, boolean computeAll ) {

        m_problem = problem;
        ProgramRunner.clearFoundVars();
        m_algorithm.clear();

//        for( Iterator it = m_problem.getAllVars().values().iterator(); it.hasNext(); ) {
//            Var var = (Var)it.next();
//            System.err.println( "Var: " + var + "\n\tRels: " + var.getRels().size() + "\n" + var.getRels() + "\n");
//        }

        //manage axioms
        for ( Iterator axiomIter = m_problem.getAxioms().iterator(); axiomIter
                                   .hasNext(); ) {
            Rel rel = ( Rel ) axiomIter.next();
            m_problem.getKnownVars().addAll( rel.getOutputs() );
            m_algorithm.add( rel );
            axiomIter.remove();
        }

        //invoke linear planning
        //if ( RuntimeProperties.isDebugEnabled() )
        //    db.p( m_problem.toString() );

        if ( linearForwardSearch( m_problem, m_algorithm, computeAll, false, false ) &&
             !computeAll ) {

            return m_algorithm;

        //invoke planning with subtasks
        } else if ( !m_problem.getSubtaskRels().isEmpty() ) {
            boolean solved = subgoalBackwardSearch( m_problem, computeAll );

            if ( RuntimeProperties.isDebugEnabled() )
                db.p( "Subtasks solved: " + solved );

            if ( !m_problem.getTargetVars().isEmpty() ) {
                linearForwardSearch( m_problem, m_algorithm, computeAll, false, false );
            }
        }
        //ProgramRunner.printFoundVars();
        return m_algorithm;
    }


    private boolean linearForwardSearch( Problem problem, ArrayList<Rel> alg, boolean computeAll,
                                         boolean isSubtask, boolean goingBackward ) {

        /*
         * while iterating through hashset, items cant be removed from/added to
         * that set. Theyre collected into these sets and added/removedall
         * together after iteration is finished
         */
        ArrayList<Rel> algorithm = alg;
        HashSet<Var> allTargetVarsBackup = null;
        HashSet<Var> allTargetVars;
        HashSet<Var> newVars = new HashSet<Var>();
        HashSet<Var> removableVars = new HashSet<Var>();

        if ( isSubtask ) {
            algorithm = ( ArrayList<Rel> ) problem.getSubGoal().getAlgorithm();

            if ( !goingBackward ) {
                for( Iterator it = problem.getSubGoal().getInputs().iterator(); it.hasNext(); ) {
                    Var input = (Var)it.next();

                    problem.getKnownVars().add( input );
                    if ( input.getField().isAlias() ) {
                        for( int i = 0; i < input.getField().getVars().size(); i++ ) {
                            String object = ( input.getObject().equals("this") ) ? "" : input.getObject().substring(5) + ".";
                            Var var = problem.getVarByFullName(object + input.getField().getVars().get(i).toString());

                            if( var != null ) {
                                problem.getKnownVars().add( var );
                            }
                        }
                    }
                }
            }

            allTargetVarsBackup = new HashSet<Var>( problem.getTargetVars() );

            problem.getTargetVars().clear();

            problem.getTargetVars().addAll( problem.getSubGoal().getOutputs() );

            for ( int i = 0; i < problem.getSubGoal().getInputs().size(); i++ ) {
                Var subtaskInput = problem.getSubGoal().getInputs().get( i );
                for ( Iterator iter = problem.getAllRels().iterator(); iter.hasNext(); ) {
                    Rel rel = ( Rel ) iter.next();
                    if ( rel.getOutputs().get( 0 ) == subtaskInput ) {
                        iter.remove();
                    }
                }
            }
            for ( int i = 0; i < problem.getSubGoal().getOutputs().size(); i++ ) {
                Var subtaskOutput = problem.getSubGoal().getOutputs().get( i );
                for ( Iterator iter = problem.getAllRels().iterator(); iter.hasNext(); ) {
                    Rel rel = ( Rel ) iter.next();
                    if ( rel.getType() == RelType.TYPE_EQUATION && rel.getInputs().get( 0 ) == subtaskOutput ) {
                        iter.remove();
                    }
                }
            }
        }

        allTargetVars = new HashSet<Var>( problem.getTargetVars() );
        HashSet<Var> foundVars = new HashSet<Var>(problem.getKnownVars());

        boolean changed = true;
        boolean foundSubGoal = false;

        if ( RuntimeProperties.isDebugEnabled() )
            db.p( "------Starting linear planning"
                  + ( ( isSubtask ) ? " with subgoal " + problem.getSubGoal() : "" )
                  + "--------" );

        int counter = 1;

        while ( ( ( !computeAll && changed && !problem.getTargetVars().isEmpty() )
                  || ( changed && computeAll ) )
                && ( !foundSubGoal ) ) {

            if ( RuntimeProperties.isDebugEnabled() )
                db.p( "----Iteration " + counter + " ----" );

            counter++;
            changed = false;
            //remove targets if they're already known
            for ( Iterator targetIter = problem.getTargetVars().iterator();
                                        targetIter
                                        .hasNext(); ) {
                Var targetVar = ( Var ) targetIter.next();
                if ( problem.getKnownVars().contains( targetVar ) ) {
                    targetIter.remove();
                }
            }
            //iterate through all knownvars
            if ( RuntimeProperties.isDebugEnabled() )
                db.p( "Known:" + problem.getKnownVars() );
            for ( Iterator knownVarsIter = problem.getKnownVars().iterator();
                                           knownVarsIter.hasNext(); ) {
                Var var = ( Var ) knownVarsIter.next();
                if ( RuntimeProperties.isDebugEnabled() )
                    db.p( "Current Known: " + var );
                // Check the relations of all components
                for ( Iterator relIter = var.getRels().iterator(); relIter
                                         .hasNext(); ) {
                    Rel rel = ( Rel ) relIter.next();
                    if ( RuntimeProperties.isDebugEnabled() )
                        db.p( "And its rel: " + rel );
                    if ( problem.getAllRels().contains( rel ) ) {
                        rel.setUnknownInputs( rel.getUnknownInputs() - 1 );

                        if ( RuntimeProperties.isDebugEnabled() )
                            db.p( "problem contains it " + rel + " unknownInputs: " + rel.getUnknownInputs() );

                        removableVars.add( var );

                        if ( rel.getUnknownInputs() == 0 && rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) {

                            if ( RuntimeProperties.isDebugEnabled() )
                                db.p( "rel is ready to be used " + rel );

                            boolean relIsNeeded = false;

                            if ( RuntimeProperties.isDebugEnabled() )
                                    db.p( "tema outputsid " + rel.getOutputs() );

                            for ( int i = 0; i < rel.getOutputs().size(); i++ ) {

                                Var relVar = rel.getOutputs().get( i );
                                if ( !foundVars.contains( relVar ) ) {
                                    relIsNeeded = true;
                                }
                                if ( isSubtask && problem.getTargetVars().contains( relVar ) ) {
                                    foundSubGoal = true;
                                }
                                if ( problem.getFoundVars().contains( relVar ) ) {
                                    relIsNeeded = false;
                                }
                            }

                            if ( rel.getOutputs().isEmpty() ) {
                                relIsNeeded = true;
                            }
                            if ( RuntimeProperties.isDebugEnabled() )
                                db.p( "relIsNeeded " + relIsNeeded + " foundSubGoal " + foundSubGoal);

                            if ( relIsNeeded ) {

                                if ( RuntimeProperties.isDebugEnabled() )
                                    db.p( "ja vajati " + rel );

                                if ( !rel.getOutputs().isEmpty() ) {
                                    newVars.addAll( rel.getOutputs() );
                                    foundVars.addAll( rel.getOutputs() );
                                }
                                algorithm.add( rel );
                            }

                            problem.getAllRels().remove( rel );
                            changed = true;
                        }
                    }
                }
            }

            if ( RuntimeProperties.isDebugEnabled() )
                db.p( "foundvars " + foundVars );

            problem.getKnownVars().addAll( newVars );
            problem.getKnownVars().removeAll( removableVars );
            newVars.clear();
        }
        if ( RuntimeProperties.isDebugEnabled() )
                db.p( "algorithm " + algorithm );

        if ( !computeAll && !isSubtask ) {
            algorithm = Optimizer.getInstance().optimize( algorithm,
                    allTargetVars );
        }

        if ( RuntimeProperties.isDebugEnabled() )
            db.p( "algorithm" + algorithm.toString() + "\n" );

        ProgramRunner.addAllFoundVars(foundVars);
        problem.getFoundVars().addAll(foundVars);

        if( isSubtask ) {
            problem.getTargetVars().addAll( allTargetVarsBackup );

            if( !foundSubGoal ) {
                foundSubGoal = problem.getFoundVars().containsAll(problem.getSubGoal().getOutputs());
            }
        }

        return ( isSubtask )
                ? foundSubGoal
                : problem.getTargetVars().isEmpty();
    }

    private boolean subgoalBackwardSearch( Problem problem,
                                           boolean computeAll ) {
        int counter = 1;
        boolean isSolved = false;
        ProblemTree tree = new ProblemTree( problem );

        if ( RuntimeProperties.isDebugEnabled() )
            db.p( "tree: " + tree );
        //start:
                do {
            HashSet nodeSet = tree.breadthWalkthrough( tree.currentDepth );
            for ( Iterator iter = nodeSet.iterator(); iter.hasNext(); ) {
                if ( RuntimeProperties.isDebugEnabled() )
                    db.p( "!!!--------Subplanning-------!!! " + counter++ );
                ProblemTreeNode node = (ProblemTreeNode) iter.next();
                Problem pr = node.getProblem();

                if ( RuntimeProperties.isDebugEnabled() )
                    db.p( "Problem: " + pr.toString() );
                if( pr.getSubGoal().getParentRel().getUnknownInputs() > 0 ) {
                    continue;
                }
                if ( linearForwardSearch( pr, null, computeAll, true, false ) ) {
                    if ( pr.currentDepth > 1 ) {
                        pr.addToAlgorithm( pr.getSubGoal().getParentRel() );
                    } else {
                        m_algorithm.add( pr.getSubGoal().getParentRel() );
                    }

                    pr.getKnownVars().addAll( pr.getSubGoal().getParentRel().getOutputs() );

                    isSolved = true;

                    while( pr.currentDepth > 1 ) {
                        pr.currentDepth--;
                        if( linearForwardSearch( pr, null, computeAll, true, true ) ) {
                            if(pr.currentDepth > 1) {
                                pr.addToAlgorithm( pr.getSubGoal().getParentRel() );
                            } else {
                                m_algorithm.add( pr.getSubGoal().getParentRel() );
                            }
                            pr.getKnownVars().addAll( pr.getSubGoal().getParentRel().getOutputs() );
                            isSolved = true;
                        } else {
                            isSolved = false;
                        }
                    }

                    if( isSolved ) {
                        m_problem = pr;
//                        continue start;
                    }
                }
            }
        } while ( !isSolved && tree.increaseDepth() );

        return isSolved;

    }

    private class ProblemTree {

        ProblemTreeNode root = null;

        private int size = 0;

        private int currentDepth = 0;

        private HashSet<Rel> subtasks = new HashSet<Rel>();

        private ProblemTree( Problem rootProblem ) {
            root = new ProblemTreeNode( null, rootProblem, null, currentDepth );
            currentDepth++;

            for ( Iterator<Rel> iter = rootProblem.getSubtaskRels().iterator(); iter
                                  .hasNext(); ) {
                Rel relWithSubtask = iter.next();
                for ( Iterator<Rel> subtaskIter = relWithSubtask.getSubtasks()
                                             .iterator(); subtaskIter.hasNext(); ) {
                    Rel subtask = subtaskIter.next();

                    rootProblem.addSubtask( subtask );
                    subtask.setParentRel( relWithSubtask );

                    this.subtasks.add( subtask );

                    size++;
                }
            }
            for ( Iterator iter = rootProblem.getSubtasks().iterator(); iter
                                  .hasNext(); ) {
                Rel subtask = ( Rel ) iter.next();
                Problem problemCopy = root.getProblem().getCopy();
                //fuck
                problemCopy.setSubGoal( problemCopy.getSubtask( subtask ),
                                        currentDepth );
                root.addChild( new ProblemTreeNode( root, problemCopy, subtask, currentDepth ) );
            }
        }

        int getSize() {
            return size;
        }

        ProblemTreeNode getRoot() {
            return root;
        }

        boolean increaseDepth() {
            boolean result = increaseDepth( root, currentDepth + 1 );
            currentDepth++;
            return result;
        }

        //do not use this directly
        boolean increaseDepth( ProblemTreeNode top, int depth ) {
            if ( RuntimeProperties.isDebugEnabled() )
                db.p( "increaseDepth " + depth );
            if ( depth > size || currentDepth == size )
                return false;
            if ( top.getDepth() < currentDepth ) {
                while ( top.hasNextChild() ) {
                    increaseDepth( top.getNextChild(),
                                   depth ); //recursion
                }
            } else {
                for ( Iterator iter = subtasks.iterator(); iter.hasNext(); ) {
                    Rel rel = ( Rel ) iter.next();
                    if ( !top.subtasksInPath.contains( rel ) ) {
                        Problem problemCopy = top.getProblem().getCopy();
                        Rel subtask = problemCopy.getSubtask( rel );
                        problemCopy.setSubGoal( subtask, depth );
                        ProblemTreeNode newNode = new ProblemTreeNode( top,
                                problemCopy, rel, depth );

                        newNode.subtasksInPath.addAll( top.subtasksInPath );
                        top.addChild( newNode );
                    }
                }
            }

            return true;
        }

        HashSet breadthWalkthrough( int depth ) {
            HashSet<ProblemTreeNode> nodeSet = new HashSet<ProblemTreeNode>();
            if ( size != 0 )
                breadthWalkthrough( root, depth, nodeSet );
            return nodeSet; //.iterator();
        }

        //      do not use this directly
        void breadthWalkthrough( ProblemTreeNode top, int depth, HashSet<ProblemTreeNode> set ) {
            if ( depth > size )
                throw new IllegalStateException(
                        "Depth exceeds size of the tree" );
            if ( top.getDepth() < depth ) {
                while ( top.hasNextChild() ) {
                    breadthWalkthrough( top.getNextChild(),
                                        depth, set ); //recursion
                }
            } else {
                set.add( top );
            }
        }

        public String toString() {
            return root.children.toString();
        }

    }


    private class ProblemTreeNode {

        ProblemTreeNode parent = null;

        Problem problem = null;

        final int depth;

        HashSet<ProblemTreeNode> children = new HashSet<ProblemTreeNode>();

        HashSet<Rel> subtasksInPath = new HashSet<Rel>();

        Iterator<ProblemTreeNode> childIterator = null;

        private ProblemTreeNode( ProblemTreeNode parent, Problem problem,
                                 Rel subtask, int depth ) {
            if ( subtask != null )
                subtasksInPath.add( subtask );
            if ( parent != null )
                subtasksInPath.addAll( parent.subtasksInPath );
            this.parent = parent;
            this.problem = problem;
            this.depth = depth;
        }

        private Problem getProblem() {
            return problem;
        }

        int getDepth() {
            return depth;
        }

        private ProblemTreeNode getNextChild() {
            return childIterator.next();
        }

        private boolean hasNextChild() {
            if ( childIterator == null )
                childIterator = children.iterator();

            if ( childIterator.hasNext() ) {
                return true;
            }
			childIterator = null;
			return false;
        }

        private void addChild( ProblemTreeNode node ) {
            children.add( node );
        }

        public String toString() {
            String s = "";
            if ( depth != 0 )
                s += problem.getSubGoal( depth ) + " " + problem.hashCode();
            s += "[ ";
            for ( Iterator childIt = children.iterator(); childIt.hasNext(); ) {
                ProblemTreeNode node = ( ProblemTreeNode ) childIt.next();
                s += node.getProblem().getSubGoal( node.depth ) + " " +
                        node.getProblem().hashCode() + " , ";
            }
            s += " ]";

            return s;
        }

    }
}

