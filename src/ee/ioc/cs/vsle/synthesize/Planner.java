package ee.ioc.cs.vsle.synthesize;

import java.util.*;
import ee.ioc.cs.vsle.util.db;

public class Planner {
    private ArrayList algorithm;

    //"m_" - class members
    private HashSet m_knownVars;
    private HashSet m_targetVars;
    private HashSet m_axioms;
    private HashSet m_allRels;
    private HashSet m_allVars;
    private HashSet m_foundVars;
    private HashSet m_subtaskRels; // = new HashSet();
    private boolean m_computeAll = false;
    private Planner planner;

    /** @link dependency */
    /*# Problem lnkProblem; */

    public Planner(Problem problem, boolean computeAll, ArrayList alg) {
        if (alg == null) {
            algorithm = new ArrayList();
        }
        else {
            algorithm = alg;
        }
        this.m_computeAll = computeAll;
        this.m_knownVars = problem.knownVars;
        this.m_targetVars = problem.targetVars;
        this.m_axioms = problem.axioms;
        this.m_allRels = problem.allRels;
        this.m_allVars = new HashSet(problem.allVars.values());
        db.p("All vars: " + m_allVars);
        Iterator allVarsIter = m_allVars.iterator();
        while (allVarsIter.hasNext()) {
            Var var = (Var) allVarsIter.next();
            db.p("Var: " + " \"" + var.field.type + "\" " + var +
                 " and its Rels: ");
            Iterator relIter = var.rels.iterator();
            while (relIter.hasNext()) {
                Rel rel = (Rel) relIter.next();
                db.p(rel + " type " + rel.type);
            }
        }
        start_planning();
    }

//    public Planner(HashSet allRels, HashSet subtaskRels, HashSet foundVars,
//                   HashSet knownVars, HashSet targetVars) {
//        this.m_allRels = allRels;
//        this.m_knownVars = knownVars;
//        this.m_targetVars = targetVars;
//        this.m_foundVars = foundVars;
//        this.m_subtaskRels = subtaskRels;
//    }

    boolean lin_planner() {

        /* while iterating through hashset, items cant be removed from/added to that set.
         Theyre collected into these sets and added/removedall together after iteration
         is finished*/
        HashSet newVars = new HashSet();
        HashSet removableVars = new HashSet();
        HashSet removableTargets = new HashSet();
        HashSet removableAxioms = new HashSet();
        HashSet allTargetVars = new HashSet(m_targetVars);
        // the set of variables we know in the graph.
        m_foundVars = new HashSet(m_knownVars);

//        m_foundVars.addAll(m_knownVars); //r - problem.

//    ArrayList algorithm = new ArrayList();

        Iterator knownVarsIter;
        Iterator relIter;
        Iterator targetIter;
        Iterator axiomIter;

        // String algorithm ="";

        Var var, targetVar, relVar;
        Rel rel;
        // Iterate through all components/variables
        boolean changed = true;

//        targetIter = m_targetVars.iterator(); //r - problem.
//        while (targetIter.hasNext()) {
//            targets.add(targetIter.next());
//        }

//    Iterator allVarsIter = problem.allVars.values().iterator();

//    while (allVarsIter.hasNext()) {
//        var = (Var) allVarsIter.next();
//    }
        db.p("All Axioms:" + m_axioms);
        db.p("------Starting linear planning--------");
        m_subtaskRels = new HashSet();
        axiomIter = m_axioms.iterator(); //r - problem.
        while (axiomIter.hasNext()) {
            rel = (Rel) axiomIter.next();
            if (rel.subtaskFlag == 0) {
                m_knownVars.addAll(rel.outputs); //r - problem.addKnown(rel.outputs);
                algorithm.add(rel);
                removableAxioms.add(rel);
                m_foundVars.addAll(rel.outputs);
            }
            else {
                m_subtaskRels.add(rel);
                removableAxioms.add(rel);
            }
        }
        relIter= m_allRels.iterator();
        while(relIter.hasNext()) {
            rel = (Rel)relIter.next();
            if(rel.subtaskFlag > 0 && rel.inputs.size() > 0) {
                m_subtaskRels.add(rel);
            }
        }
        m_axioms.removeAll(removableAxioms);
        db.p("All Axioms:" + m_axioms);
        db.p("All Rels:" + m_allRels);
        db.p("Subtask Rels: " + m_subtaskRels);
        db.p("All targets: " + m_targetVars);
        // ee.ioc.cs.editor.util.db.p("alustan probleem on:"+problem);
        int counter = 1;

        while ( (!m_computeAll && changed && !m_targetVars.isEmpty()) ||
               (changed && m_computeAll)) {
            db.p("----Iteration " + counter + "----");
            counter++;
            changed = false;
            targetIter = m_targetVars.iterator();
            while (targetIter.hasNext()) {
                targetVar = (Var) targetIter.next();
                if (m_knownVars.contains(targetVar)) {
                    removableTargets.add(targetVar);
                }
            }
            m_targetVars.removeAll(removableTargets);
            db.p("Known:" + m_knownVars);
            knownVarsIter = m_knownVars.iterator();
            while (knownVarsIter.hasNext()) {
                var = (Var) knownVarsIter.next();
                // Check the relations of all components
                relIter = var.rels.iterator();
                while (relIter.hasNext()) {
                    rel = (Rel) relIter.next();
                    if (m_allRels.contains(rel)) {
                        rel.flag--;
                        removableVars.add(var);
                        if (rel.flag == 0) {
                            db.p("rel on see " + rel);

                            boolean relIsNeeded = false;

                            for (int i = 0; i < rel.outputs.size(); i++) {
                                db.p("tema outputsid " + rel.outputs);
                                relVar = (Var) rel.outputs.get(i);
                                if (!m_foundVars.contains(relVar)) {
                                    relIsNeeded = true;
                                }
                            }
                            if (rel.outputs.isEmpty()) {
                                relIsNeeded = true;
                            }
                            if (relIsNeeded && rel.subtaskFlag < 1) {
                                db.p("ja vajati " + rel);
                                if (!rel.outputs.isEmpty()) {
                                    newVars.addAll(rel.outputs);
                                    m_foundVars.addAll(rel.outputs);
                                }
                                algorithm.add(rel);
                            }
//                            if (rel.subtaskFlag > 0) {
//                                db.p("subtaskid: " + rel.subtasks);
//                            }

                            m_allRels.remove(rel);
                            changed = true;
                        }
                    }
                }
            }

            // relIter = var.rels.iterator();

            // problem.allRels.contains(rel)) {

            db.p("foundvars " + m_foundVars);
            m_knownVars.addAll(newVars);
            m_knownVars.removeAll(removableVars);
            newVars.clear();
        }
        db.p("Targets left: " + m_targetVars);
        boolean solved = m_targetVars.isEmpty();
        if (solved) {
            db.p("Problem was solved");
        }
        else {
            db.p("Problem not solved, unknown vars: " + m_targetVars);
        }

        if (!m_computeAll) {
			Optimizer optimizer = new Optimizer();
            algorithm = optimizer.optimize(algorithm, allTargetVars);
        }

        return solved;

    }

    public boolean sub_planning() {
        HashSet newVars = new HashSet();
        HashSet removableVars = new HashSet();
        HashSet removableRels = new HashSet();
        HashSet removableSubRels = new HashSet();
        HashSet subTaskInputs = new HashSet();
        db.p("-------Sub Planning-------");
        db.p("m_knownVars " + m_knownVars);
        db.p("m_targetVars " + m_targetVars);
        db.p("m_axioms " + m_axioms);
        db.p("m_allRels " + m_allRels);
        db.p("m_allVars " + m_allVars);
        db.p("m_foundVars " + m_foundVars);

        db.p("Left Rels: ");
        Iterator allRelIter = m_allRels.iterator();
        while (allRelIter.hasNext()) {
            Rel rel = (Rel) allRelIter.next();
            db.p("Rel: " + rel + " & Var flag: " + rel.flag);
        }
////////////////////////////////////////////////////////////////////////////////
//        m_allRels.addAll(m_subtaskRels);

        boolean changed = true;
        int counter = 0;
        while (changed) {
            changed = false;
            db.p("-------Iteration " + ++counter + "-------");
            Iterator subIter = m_subtaskRels.iterator();
            while (subIter.hasNext()) {
                Rel rel = (Rel) subIter.next();
                for (int i = 0; i < rel.subtasks.size(); i++) {
                    Rel subRel = (Rel) rel.subtasks.get(i);
                    db.p("subRel " + subRel);
                    m_knownVars.addAll(subRel.inputs);
                    db.p("m_knownVars " + m_knownVars);
                    subRel.flag -= subRel.inputs.size();
                    ////////////////////////////////////////////////////////////////
                    //remove rels with outputs of subTask inputs
                    subTaskInputs.addAll(subRel.inputs);
                    allRelIter = m_allRels.iterator();
                    while (allRelIter.hasNext()) {
                        Rel trel = (Rel) allRelIter.next();
                        db.p("trel: " + trel);
                        for (int k = 0; k < trel.outputs.size(); k++) {
                            Var outv = (Var) trel.outputs.get(k);
                            if (subTaskInputs.contains(outv)) {
                                trel.flag = 0;
                                m_foundVars.add(outv);
                                removableRels.add(trel);
                            }
                        }
                    }
                    m_allRels.removeAll(removableRels);
                    removableRels.clear();
                    ////////////////////////////////////////////////////////////////
                    db.p("subTaskInputs " + subTaskInputs);
                    Iterator knownVarIter = m_knownVars.iterator();
                    while (knownVarIter.hasNext()) {
                        Var var = (Var) knownVarIter.next();
                        db.p("var: " + var);
                        Iterator relIter = var.rels.iterator();
                        while (relIter.hasNext()) {
                            Rel subVarRel = (Rel) relIter.next();
                            db.p("subVarRel " + subVarRel);
                            if (m_allRels.contains(subVarRel)) {
                                subVarRel.flag--;
                                removableVars.add(var);
                                if (subVarRel.flag == 0) {

                                    boolean relIsNeeded = false;
                                    for (int j = 0; j < subVarRel.outputs.size();
                                         j++) {
                                        Var subVarRelOutVar = (Var) subVarRel.
                                            outputs.get(j);
                                        if (!m_foundVars.contains(
                                            subVarRelOutVar)) {
                                            relIsNeeded = true;
                                        }
                                    }
                                    if (relIsNeeded) {
                                        if (!rel.inAlgorithm) {
                                            rel.inAlgorithm = true;
                                            algorithm.add("<subtask>");
                                        }
                                        m_foundVars.addAll(subVarRel.outputs);
                                        newVars.addAll(subVarRel.outputs);
                                        db.p("subVarRel in alg: " + subVarRel);
                                        algorithm.add(subVarRel);
                                    }
                                    m_allRels.remove(subVarRel);
                                    changed = true;
                                }
                            }
                        }
                    }
                    if (m_foundVars.contains(subRel.outputs.get(0))) {
                        algorithm.add(rel);
                        algorithm.add("</subtask>");
                        newVars.addAll(rel.outputs);
                        m_foundVars.addAll(rel.outputs);
                        removableSubRels.add(rel);
                        allRelIter = m_allRels.iterator();
                        while (allRelIter.hasNext()) {
                            Rel trel = (Rel) allRelIter.next();
                            if (trel.outputs.contains(rel.outputs.get(0))) {
                                removableRels.add(trel);
                            }
                        }
                        m_allRels.removeAll(removableRels);

                    }

                    db.p("m_foundVars" + m_foundVars);
                    m_knownVars.addAll(newVars);
                    m_knownVars.removeAll(removableVars);
                    newVars.clear();

                }
//            if(!m_foundVars.containsAll(m_targetVars)) {
//                sub_planning();
//            }
//                if(rel.
            }
            m_subtaskRels.removeAll(removableSubRels);
            db.p("algorithm" + algorithm.toString() + "\n");
        }
//        db.p("Left Rels: ");
//        allRelIter = m_allRels.iterator();
//        while (allRelIter.hasNext()) {
//            Rel rel = (Rel) allRelIter.next();
//            db.p("Rel: " + rel + " & Var flag: " + rel.flag);
//        }
///////////////////////////////////////////
//        HashSet unknown = new HashSet();
//
//        allRelIter = m_allRels.iterator();
//        while (allRelIter.hasNext()) {
//            Rel rel = (Rel) allRelIter.next();
//            if (rel.flag > 0) {
//                for (int i = 0; i < rel.inputs.size(); i++) {
//                    Var var = (Var) rel.inputs.get(i);
//                    unknown.add(var);
//                }
//            }
//        }
//        unknown.removeAll(m_foundVars);
//        unknown.removeAll(subTaskInputs);
//        ///////////////////////////////
//        db.p("Unknown: " + unknown);

        db.p("--------End of SubPlanning------------");
        db.p(algorithm.toString());

        db.p("m_targetVars " + m_targetVars);
        db.p("m_axioms " + m_axioms);
        db.p("m_allRels " + m_allRels);
        db.p("m_allVars " + m_allVars);
        db.p("m_knownVars " + m_knownVars);
        db.p("m_foundVars " + m_foundVars);
//        m_knownVars.clear();
        removableVars.clear();
        Iterator targetIter = m_targetVars.iterator();
        while (targetIter.hasNext()) {
            Var targetVar = (Var) targetIter.next();
            if (m_foundVars.contains(targetVar)) {
                removableVars.add(targetVar);
            }
        }
        m_targetVars.removeAll(removableVars);
        return m_targetVars.isEmpty();

    }

    public void start_planning() {
        if (!lin_planner()) {
            if (!sub_planning()) {
                lin_planner();
            }
        }
    }

    public String getAlgorithm() {
        StringBuffer alg = new StringBuffer();
        for (int i = 0; i < algorithm.size(); i++) {
            alg.append("        ");
            alg.append(algorithm.get(i)).toString();
            alg.append(";\n");
        }
        db.p(alg.toString());
        return alg.toString();
    }
}
