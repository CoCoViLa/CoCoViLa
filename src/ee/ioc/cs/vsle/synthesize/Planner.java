package ee.ioc.cs.vsle.synthesize;

import java.util.Iterator;
import java.util.HashSet;
import ee.ioc.cs.vsle.util.db;
import ee.ioc.cs.vsle.editor.ProgramRunner;
import java.util.ArrayList;

/**
 *
 * @author Pavel Grigorenko
 * @version 1.0
 */
public class Planner {

    private static Planner s_planner = null;

    private Problem m_problem = null;

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

        //manage axioms
        for ( Iterator axiomIter = m_problem.getAxioms().iterator(); axiomIter
                                   .hasNext(); ) {
            Rel rel = ( Rel ) axiomIter.next();
            m_problem.getKnownVars().addAll( rel.getOutputs() );
            m_problem.algorithm.add( rel );
            axiomIter.remove();
        }

        //invoke linear planning
		 db.p(m_problem.toString());
        if ( linearForwardSearch( m_problem, computeAll, false, false ) &&
             !computeAll ) {

            return m_problem.algorithm;

        //invoke planning with subtasks
        } else if ( !m_problem.getSubtaskRels().isEmpty() ) {

            db.p( "Subtasks solved: " + subgoalBackwardSearch( m_problem, computeAll ) );

            if ( !m_problem.getTargetVars().isEmpty() ) {
                linearForwardSearch( m_problem, computeAll, false, false );
            }
        }

        return m_problem.algorithm;
    }


    private boolean linearForwardSearch( Problem problem, boolean computeAll,
                                         boolean isSubtask, boolean goingBackward ) {

        /*
         * while iterating through hashset, items cant be removed from/added to
         * that set. Theyre collected into these sets and added/removedall
         * together after iteration is finished
         */
        ArrayList algorithm;
        HashSet allTargetVarsBackup = null;
        HashSet allTargetVars;
        HashSet newVars = new HashSet();
        HashSet removableVars = new HashSet();

        if ( isSubtask ) {
            algorithm = ( ArrayList ) problem.getSubGoal().getAlgorithm();

            if ( !goingBackward ) {
                problem.getKnownVars().addAll( problem.getSubGoal().getInputs() );
            }

            allTargetVarsBackup = new HashSet( problem.getTargetVars() );

            problem.getTargetVars().clear();

            problem.getTargetVars().addAll( problem.getSubGoal().getOutputs() );

            for ( int i = 0; i < problem.getSubGoal().getInputs().size(); i++ ) {
                Var subtaskInput = ( Var ) problem.getSubGoal().getInputs().get( i );
                for ( Iterator iter = problem.getAllRels().iterator(); iter.hasNext(); ) {
                    Rel rel = ( Rel ) iter.next();
                    if ( rel.getOutputs().get( 0 ) == subtaskInput ) {
                        iter.remove();
                    }
                }
            }
            for ( int i = 0; i < problem.getSubGoal().getOutputs().size(); i++ ) {
                Var subtaskOutput = ( Var ) problem.getSubGoal().getOutputs().get( i );
                for ( Iterator iter = problem.getAllRels().iterator(); iter.hasNext(); ) {
                    Rel rel = ( Rel ) iter.next();
                    if ( rel.type == RelType.equation && rel.getInputs().get( 0 ) == subtaskOutput ) {
                        iter.remove();
                    }
                }
            }
        }
        else {
            algorithm = problem.algorithm;
        }

        allTargetVars = new HashSet( problem.getTargetVars() );
        HashSet foundVars = new HashSet(problem.getKnownVars());

        boolean changed = true;
        boolean foundSubGoal = false;

        db.p( "------Starting linear planning"
              + ((isSubtask)? " with subgoal " + problem.getSubGoal(): "")
              + "--------" );
        int counter = 1;

        while ( ( ( !computeAll && changed && !problem.getTargetVars().isEmpty() )
                  || ( changed && computeAll ) )
                && ( !foundSubGoal ) ) {
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
            db.p( "Known:" + problem.getKnownVars() );
            for ( Iterator knownVarsIter = problem.getKnownVars().iterator();
                                           knownVarsIter.hasNext(); ) {
                Var var = ( Var ) knownVarsIter.next();
                db.p( "Current Known: " + var );
                // Check the relations of all components
                for ( Iterator relIter = var.getRels().iterator(); relIter
                                         .hasNext(); ) {
                    Rel rel = ( Rel ) relIter.next();
                    db.p( "And its rel: " + rel );
                    if ( problem.containsRel( rel ) ) {
                        rel.unknownInputs--;
                        db.p( "problem contains it " + rel + " unknownInputs: " + rel.unknownInputs );
                        removableVars.add( var );
                        if ( rel.unknownInputs == 0 && rel.type != RelType.method_with_subtask ) {
                            db.p( "rel is ready to be used " + rel );

                            boolean relIsNeeded = false;

                            for ( int i = 0; i < rel.getOutputs().size(); i++ ) {
                                db.p( "tema outputsid " + rel.getOutputs() );
                                Var relVar = ( Var ) rel.getOutputs().get( i );
                                if ( !foundVars.contains( relVar ) ) {
                                    relIsNeeded = true;
                                }
                                if ( isSubtask && problem.getTargetVars().contains( relVar ) ) {
                                    foundSubGoal = true;
                                }
                            }

                            if ( rel.getOutputs().isEmpty() ) {
                                relIsNeeded = true;
                            }
                            if ( relIsNeeded ) {
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

            db.p( "foundvars " + foundVars );
            problem.getKnownVars().addAll( newVars );
            problem.getKnownVars().removeAll( removableVars );
            newVars.clear();
        }

        if ( !computeAll /*&& !isSubtask*/ ) {
            algorithm = Optimizer.getInstance().optimize( algorithm,
                    allTargetVars );
        }

        if( isSubtask ) {
            problem.getTargetVars().addAll( allTargetVarsBackup );
        }
        db.p( "algorithm" + algorithm.toString() + "\n" );
        ProgramRunner.addAllFoundVars(foundVars);

        return ( isSubtask )
                ? foundSubGoal
                : problem.getTargetVars().isEmpty();
    }

    private boolean subgoalBackwardSearch( Problem problem,
                                           boolean computeAll ) {
        int counter = 1;
        boolean isSolved = false;
        ProblemTree tree = new ProblemTree( problem );
        db.p( "tree: " + tree );
        start: do {
            HashSet nodeSet = tree.breadthWalkthrough( tree.currentDepth );
            for ( Iterator iter = nodeSet.iterator(); iter.hasNext(); ) {
                db.p( "!!!--------Subplanning-------!!! " + counter++ );
                ProblemTreeNode node = (ProblemTreeNode) iter.next();
                Problem pr = node.getProblem();

                db.p( "Problem: " + pr.toString() );
                if ( linearForwardSearch( pr, computeAll, true, false ) ) {
                    pr.addToAlgorithm( pr.getSubGoal().getParentRel() );
                    pr.getKnownVars().addAll( pr.getSubGoal().getParentRel().getOutputs() );

                    isSolved = true;

                    while( pr.currentDepth > 1 ) {
                        pr.currentDepth--;
                        if( linearForwardSearch( pr, computeAll, true, true ) ) {
                            pr.addToAlgorithm( pr.getSubGoal().getParentRel() );
                            pr.getKnownVars().addAll( pr.getSubGoal().getParentRel().getOutputs() );
                            isSolved = true;
                        } else {
                            isSolved = false;
                        }
                    }

                    if( isSolved ) {
                        m_problem = pr;
                        continue start;
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

        private HashSet subtasks = new HashSet();

        private ProblemTree( Problem rootProblem ) {
            root = new ProblemTreeNode( null, rootProblem, null, currentDepth );
            currentDepth++;

            for ( Iterator iter = rootProblem.getSubtaskRels().iterator(); iter
                                  .hasNext(); ) {
                Rel relWithSubtask = ( Rel ) iter.next();
                for ( Iterator subtaskIter = relWithSubtask.getSubtasks()
                                             .iterator(); subtaskIter.hasNext(); ) {
                    Rel subtask = ( Rel ) subtaskIter.next();

                    rootProblem.addSubtask( subtask );
                    subtask.setParentRel( relWithSubtask );

//                    String subtaskString = subtask.toString();
                    this.subtasks.add( subtask.toString() );

                    size++;
                }
            }
            for ( Iterator iter = rootProblem.getSubtasks().iterator(); iter
                                  .hasNext(); ) {
                Rel subtask = ( Rel ) iter.next();
                Problem problemCopy = root.getProblem().getCopy();
                //fuck
                problemCopy.setSubGoal( problemCopy.getSubtaskByString( subtask.toString() ),
                                        currentDepth );
//                problemCopy.addKnown(subtask.getInputs());
                root.addChild( new ProblemTreeNode( root, problemCopy, subtask
                        .toString(), currentDepth ) );
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
            db.p( "increaseDepth " + depth );
            if ( depth > size || currentDepth == size )
                return false;
            if ( top.getDepth() < currentDepth ) {
                while ( top.hasNextChild() ) {
                    increaseDepth( ( ProblemTreeNode ) top.getNextChild(),
                                   depth ); //recursion
                }
            } else {
                for ( Iterator iter = subtasks.iterator(); iter.hasNext(); ) {
                    String rel = ( String ) iter.next();
                    if ( !top.subtasksInPath.contains( rel ) ) {
                        Problem problemCopy = top.getProblem().getCopy();
                        Rel subtask = problemCopy.getSubtaskByString( rel );
                        problemCopy.setSubGoal( subtask, depth );
//                        problemCopy.addKnown(subtask.getInputs());
//                        problemCopy.addTarget(subtask.);
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
            HashSet nodeSet = new HashSet();
            if ( size != 0 )
                breadthWalkthrough( root, depth, nodeSet );
            return nodeSet; //.iterator();
        }

        //      do not use this directly
        void breadthWalkthrough( ProblemTreeNode top, int depth, HashSet set ) {
            if ( depth > size )
                throw new IllegalStateException(
                        "Depth exceeds size of the tree" );
            if ( top.getDepth() < depth ) {
                while ( top.hasNextChild() ) {
                    breadthWalkthrough( ( ProblemTreeNode ) top.getNextChild(),
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

        HashSet children = new HashSet();

        HashSet subtasksInPath = new HashSet();

        Iterator childIterator = null;

        private ProblemTreeNode( ProblemTreeNode parent, Problem problem,
                                 String subtask, int depth ) {
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
            return ( ProblemTreeNode ) childIterator.next();
        }

        private boolean hasNextChild() {
            if ( childIterator == null )
                childIterator = children.iterator();

            if ( childIterator.hasNext() ) {
                return true;
            } else {
                childIterator = null;
                return false;
            }
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

//
//public class PlannerOld {
//    private ArrayList algorithm;
//
//    //"m_" - class members
//    private HashSet m_knownVars;
//    private HashSet m_targetVars;
//    private HashSet m_axioms;
//    private HashSet m_allRels;
//    private HashSet m_allVars;
//    private HashSet m_foundVars;
//    private HashSet m_subtaskRels; // = new HashSet();
//    private boolean m_computeAll = false;
//    private PlannerOld planner;
//
//    /**
//         @param problem the specification unfolded as a graph.
//         @param computeAll set to true, if we try to find everything that can be computed on the problem graph.
//         @param alg - algorithm
//     */
//    public PlannerOld(Problem problem, boolean computeAll, ArrayList alg) {
//        if (alg == null) {
//            algorithm = new ArrayList();
//        }
//        else {
//            algorithm = alg;
//        }
//        this.m_computeAll = computeAll;
//        this.m_knownVars = problem.knownVars;
//        this.m_targetVars = problem.targetVars;
//        this.m_axioms = problem.axioms;
//        this.m_allRels = problem.allRels;
//        this.m_allVars = new HashSet(problem.allVars.values());
//        db.p("All vars: " + m_allVars);
//        Iterator allVarsIter = m_allVars.iterator();
//        while (allVarsIter.hasNext()) {
//            Var var = (Var) allVarsIter.next();
//            db.p("Var: " + " \"" + var.field.type + "\" " + var +
//                 " and its Rels: ");
//            Iterator relIter = var.rels.iterator();
//            while (relIter.hasNext()) {
//                Rel rel = (Rel) relIter.next();
//                db.p(rel + " type " + rel.type);
//            }
//        }
//        Iterator allSubRelIter = problem.subtaskRels.iterator();
//        while (allSubRelIter.hasNext()) {
//            Rel rel = ( Rel ) allSubRelIter.next();
//            System.err.println("---!!!" + rel + " type " + rel.type);
//        }
//        start_planning();
//    }
//
////    public PlannerOld(HashSet allRels, HashSet subtaskRels, HashSet foundVars,
////                   HashSet knownVars, HashSet targetVars) {
////        this.m_allRels = allRels;
////        this.m_knownVars = knownVars;
////        this.m_targetVars = targetVars;
////        this.m_foundVars = foundVars;
////        this.m_subtaskRels = subtaskRels;
////    }
//
//
//    /** Does the linear planning.
//     @return true if problem is solved, otherwise false.
//     */
//
//    boolean lin_planner() {
//
//        /* while iterating through hashset, items cant be removed from/added to that set.
//         Theyre collected into these sets and added/removedall together after iteration
//         is finished*/
//        HashSet newVars = new HashSet();
//        HashSet removableVars = new HashSet();
//        HashSet removableTargets = new HashSet();
//        HashSet removableAxioms = new HashSet();
//        HashSet allTargetVars = new HashSet(m_targetVars);
//        // the set of variables we know in the graph.
//        m_foundVars = new HashSet(m_knownVars);
//
////        m_foundVars.addAll(m_knownVars); //r - problem.
//
////    ArrayList algorithm = new ArrayList();
//
//        Iterator knownVarsIter;
//        Iterator relIter;
//        Iterator targetIter;
//        Iterator axiomIter;
//
//        // String algorithm ="";
//
//        Var var, targetVar, relVar;
//        Rel rel;
//        // Iterate through all components/variables
//        boolean changed = true;
//
////        targetIter = m_targetVars.iterator(); //r - problem.
////        while (targetIter.hasNext()) {
////            targets.add(targetIter.next());
////        }
//
////    Iterator allVarsIter = problem.allVars.values().iterator();
//
////    while (allVarsIter.hasNext()) {
////        var = (Var) allVarsIter.next();
////    }
//        db.p("All Axioms:" + m_axioms);
//        db.p("------Starting linear planning--------");
//        m_subtaskRels = new HashSet();
//        axiomIter = m_axioms.iterator(); //r - problem.
//        while (axiomIter.hasNext()) {
//            rel = (Rel) axiomIter.next();
//            if (rel.subtaskFlag == 0) {
//                m_knownVars.addAll(rel.outputs); //r - problem.addKnown(rel.outputs);
//                algorithm.add(rel);
//                removableAxioms.add(rel);
//                m_foundVars.addAll(rel.outputs);
//            }
//            else {
//                m_subtaskRels.add(rel);
//                removableAxioms.add(rel);
//            }
//        }
//        relIter = m_allRels.iterator();
//        while (relIter.hasNext()) {
//            rel = (Rel) relIter.next();
//            if (rel.subtaskFlag > 0 && rel.inputs.size() > 0) {
//                m_subtaskRels.add(rel);
//            }
//        }
//        m_axioms.removeAll(removableAxioms);
//        db.p("All Axioms:" + m_axioms);
//        db.p("All Rels:" + m_allRels);
//        db.p("Subtask Rels: " + m_subtaskRels);
//        db.p("All targets: " + m_targetVars);
//        // ee.ioc.cs.editor.util.db.p("alustan probleem on:"+problem);
//        int counter = 1;
//
//        while ( (!m_computeAll && changed && !m_targetVars.isEmpty()) ||
//               (changed && m_computeAll)) {
//            db.p("----Iteration " + counter + "----");
//            counter++;
//            changed = false;
//            targetIter = m_targetVars.iterator();
//            while (targetIter.hasNext()) {
//                targetVar = (Var) targetIter.next();
//                if (m_knownVars.contains(targetVar)) {
//                    removableTargets.add(targetVar);
//                }
//            }
//            m_targetVars.removeAll(removableTargets);
//            db.p("Known:" + m_knownVars);
//            knownVarsIter = m_knownVars.iterator();
//            while (knownVarsIter.hasNext()) {
//                var = (Var) knownVarsIter.next();
//                // Check the relations of all components
//                relIter = var.rels.iterator();
//                while (relIter.hasNext()) {
//                    rel = (Rel) relIter.next();
//                    if (m_allRels.contains(rel)) {
//                        rel.unknownInputs--;
//                        removableVars.add(var);
//                        if (rel.unknownInputs == 0) {
//                            db.p("rel on see " + rel);
//
//                            boolean relIsNeeded = false;
//
//                            for (int i = 0; i < rel.outputs.size(); i++) {
//                                db.p("tema outputsid " + rel.outputs);
//                                relVar = (Var) rel.outputs.get(i);
//                                if (!m_foundVars.contains(relVar)) {
//                                    relIsNeeded = true;
//                                }
//                            }
//                            if (rel.outputs.isEmpty()) {
//                                relIsNeeded = true;
//                            }
//                            if (relIsNeeded && rel.subtaskFlag < 1) {
//                                db.p("ja vajati " + rel);
//                                if (!rel.outputs.isEmpty()) {
//                                    newVars.addAll(rel.outputs);
//                                    m_foundVars.addAll(rel.outputs);
//                                }
//                                algorithm.add(rel);
//                            }
////                            if (rel.subtaskFlag > 0) {
////                                db.p("subtaskid: " + rel.subtasks);
////                            }
//
//                            m_allRels.remove(rel);
//                            changed = true;
//                        }
//                    }
//                }
//            }
//
//            // relIter = var.rels.iterator();
//
//            // problem.allRels.contains(rel)) {
//
//            db.p("foundvars " + m_foundVars);
//            m_knownVars.addAll(newVars);
//            m_knownVars.removeAll(removableVars);
//            newVars.clear();
//        }
//        db.p("Targets left: " + m_targetVars);
//        boolean solved = m_targetVars.isEmpty();
//        if (solved) {
//            db.p("Problem was solved");
//        }
//        else {
//            db.p("Problem not solved, unknown vars: " + m_targetVars);
//        }
//
//        if (!m_computeAll) {
//            algorithm = Optimizer.getInstance().optimize(algorithm, allTargetVars);
//        }
//        db.p("algorithm" + algorithm.toString() + "\n");
//                ProgramRunner.foundVars = m_foundVars;
//        return solved;
//
//    }
//
//    /** Does the planning with subtasks.
//     @return true if problem is solved, otherwise false.
//     */
//
//    public boolean sub_planning() {
//        HashSet newVars = new HashSet();
//        HashSet removableVars = new HashSet();
//        HashSet removableRels = new HashSet();
//        HashSet removableSubRels = new HashSet();
//        HashSet subTaskInputs = new HashSet();
//        db.p("-------Sub Planning-------");
//        db.p("m_knownVars " + m_knownVars);
//        db.p("m_targetVars " + m_targetVars);
//        db.p("m_axioms " + m_axioms);
//        db.p("m_allRels " + m_allRels);
//        db.p("m_allVars " + m_allVars);
//        db.p("m_foundVars " + m_foundVars);
//
//        db.p("Left Rels: ");
//        Iterator allRelIter = m_allRels.iterator();
//        while (allRelIter.hasNext()) {
//            Rel rel = (Rel) allRelIter.next();
//            db.p("Rel: " + rel + " & Var unknownInputs: " + rel.unknownInputs);
//        }
//////////////////////////////////////////////////////////////////////////////////
////        m_allRels.addAll(m_subtaskRels);
//
//        boolean changed = true;
//        int counter = 0;
//        while (changed) {
//            changed = false;
//            db.p("-------Iteration " + ++counter + "-------");
//            Iterator subIter = m_subtaskRels.iterator();
//            while (subIter.hasNext()) {
//                Rel rel = (Rel) subIter.next();
//                for (int i = 0; i < rel.subtasks.size(); i++) {
//                    Rel subRel = (Rel) rel.subtasks.get(i);
//                    db.p("subRel " + subRel);
//                    m_knownVars.addAll(subRel.inputs);
//                    db.p("m_knownVars " + m_knownVars);
//                    subRel.unknownInputs -= subRel.inputs.size();
//                    ////////////////////////////////////////////////////////////////
//                    //remove rels with outputs of subTask inputs
//                    subTaskInputs.addAll(subRel.inputs);
//                    allRelIter = m_allRels.iterator();
//                    while (allRelIter.hasNext()) {
//                        Rel trel = (Rel) allRelIter.next();
//                        db.p("trel: " + trel);
//                        for (int k = 0; k < trel.outputs.size(); k++) {
//                            Var outv = (Var) trel.outputs.get(k);
//                            if (subTaskInputs.contains(outv)) {
//                                trel.unknownInputs = 0;
//                                m_foundVars.add(outv);
//                                removableRels.add(trel);
//                            }
//                        }
//                    }
//                    m_allRels.removeAll(removableRels);
//                    removableRels.clear();
//                    ////////////////////////////////////////////////////////////////
//                    db.p("subTaskInputs " + subTaskInputs);
//                    Iterator knownVarIter = m_knownVars.iterator();
//                    while (knownVarIter.hasNext()) {
//                        Var var = (Var) knownVarIter.next();
//                        db.p("var: " + var);
//                        Iterator relIter = var.rels.iterator();
//                        while (relIter.hasNext()) {
//                            Rel subVarRel = (Rel) relIter.next();
//                            db.p("subVarRel " + subVarRel);
//                            if (m_allRels.contains(subVarRel)) {
//                                subVarRel.unknownInputs--;
//                                removableVars.add(var);
//                                if (subVarRel.unknownInputs == 0) {
//
//                                    boolean relIsNeeded = false;
//                                    for (int j = 0; j < subVarRel.outputs.size();
//                                         j++) {
//                                        Var subVarRelOutVar = (Var) subVarRel.
//                                            outputs.get(j);
//                                        if (!m_foundVars.contains(
//                                            subVarRelOutVar)) {
//                                            relIsNeeded = true;
//                                        }
//                                    }
////                                    if (!rel.inAlgorithm) {
////                                            rel.inAlgorithm = true;
////                                            algorithm.add("<subtask>");
////                                        }
//
//                                    if (relIsNeeded) {
//                                        if (!rel.inAlgorithm) {
//                                            rel.inAlgorithm = true;
//                                            algorithm.add("<subtask>");
//                                            algorithm.add(subRel);
//                                        }
//                                        m_foundVars.addAll(subVarRel.outputs);
//                                        newVars.addAll(subVarRel.outputs);
//                                        db.p("subVarRel in alg: " + subVarRel);
//                                        algorithm.add(subVarRel);
//                                    }
//                                    m_allRels.remove(subVarRel);
//                                    changed = true;
//                                }
//                            }
//                        }
//                    }
//                    if (m_foundVars.contains(subRel.outputs.get(0))) {
//                        if (rel.inAlgorithm) {
//                            algorithm.add("</subtask>");
//                        }
//                        algorithm.add(rel);
//                        newVars.addAll(rel.outputs);
//                        m_foundVars.addAll(rel.outputs);
//                        removableSubRels.add(rel);
//                        allRelIter = m_allRels.iterator();
//                        while (allRelIter.hasNext()) {
//                            Rel trel = (Rel) allRelIter.next();
//                            if (trel.outputs.contains(rel.outputs.get(0))) {
//                                removableRels.add(trel);
//                            }
//                        }
//                        m_allRels.removeAll(removableRels);
//
//                    }
//
//                    db.p("m_foundVars" + m_foundVars);
//                    m_knownVars.addAll(newVars);
//                    m_knownVars.removeAll(removableVars);
//                    newVars.clear();
//
//                }
////            if(!m_foundVars.containsAll(m_targetVars)) {
////                sub_planning();
////            }
////                if(rel.
//            }
//            m_subtaskRels.removeAll(removableSubRels);
//            db.p("algorithm" + algorithm.toString() + "\n");
//        }
////        db.p("Left Rels: ");
////        allRelIter = m_allRels.iterator();
////        while (allRelIter.hasNext()) {
////            Rel rel = (Rel) allRelIter.next();
////            db.p("Rel: " + rel + " & Var unknownInputs: " + rel.unknownInputs);
////        }
/////////////////////////////////////////////
////        HashSet unknown = new HashSet();
////
////        allRelIter = m_allRels.iterator();
////        while (allRelIter.hasNext()) {
////            Rel rel = (Rel) allRelIter.next();
////            if (rel.unknownInputs > 0) {
////                for (int i = 0; i < rel.inputs.size(); i++) {
////                    Var var = (Var) rel.inputs.get(i);
////                    unknown.add(var);
////                }
////            }
////        }
////        unknown.removeAll(m_foundVars);
////        unknown.removeAll(subTaskInputs);
////        ///////////////////////////////
////        db.p("Unknown: " + unknown);
//
//        db.p("--------End of SubPlanning------------");
//        db.p(algorithm.toString());
//
//        db.p("m_targetVars " + m_targetVars);
//        db.p("m_axioms " + m_axioms);
//        db.p("m_allRels " + m_allRels);
//        db.p("m_allVars " + m_allVars);
//        db.p("m_knownVars " + m_knownVars);
//        db.p("m_foundVars " + m_foundVars);
////        m_knownVars.clear();
//        removableVars.clear();
//        Iterator targetIter = m_targetVars.iterator();
//        while (targetIter.hasNext()) {
//            Var targetVar = (Var) targetIter.next();
//            if (m_foundVars.contains(targetVar)) {
//                removableVars.add(targetVar);
//            }
//        }
//        m_targetVars.removeAll(removableVars);
//        return m_targetVars.isEmpty();
//
//    }
//
//    public void start_planning() {
//        if (!lin_planner()) {
////            if (!sub_planning()) {
////                lin_planner();
////            }
//        }
//    }
//
//    public String getAlgorithm() {
//        StringBuffer alg = new StringBuffer();
//        for (int i = 0; i < algorithm.size(); i++) {
//            alg.append("        ");
//            alg.append(algorithm.get(i)).toString();
//            alg.append(";\n");
//        }
//        db.p(alg.toString());
//        return alg.toString();
//    }
//
//    public ArrayList getAlgorithmL() {
//        return algorithm;
//    }
//
//}
