package ee.ioc.cs.vsle.synthesize;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

class Problem {

	HashSet axioms = new HashSet();
	HashSet knownVars = new HashSet();
	HashSet targetVars = new HashSet();
	HashSet removableComponents; // = new HashSet();
	HashSet newComponents; // = new HashSet();
	HashMap allVars = new HashMap();
	HashSet allRels = new HashSet();
	String problemClass;

    /** @link dependency */
    /*# Var lnkVar; */

    /** @link dependency */
    /*# Rel lnkRel; */

	void addAxiom(Rel rel) {
		axioms.add(rel);
	}

	void addKnown(Var var) {
		knownVars.add(var);
	}

	void addKnown(ArrayList vars) {
		knownVars.addAll(vars);
	}

	void addTarget(Var var) {
		targetVars.add(var);
	}

	void addRel(Rel rel) {
		allRels.add(rel);
 	}
	void addAllRels(HashSet set) {
		allRels.addAll(set);
 	}


	void addVar(Var var) {
		allVars.put(var.getObj() + "." + var.getName(), var);
	}

	HashMap getAllVars() {
		return allVars;
	}

	public String toString() {
		return ("All: " + allVars + "\n Rels: " + allRels + "\n Known: " + knownVars + "\n Targets:" + targetVars + "\n Axioms:" + axioms);
	}
}
