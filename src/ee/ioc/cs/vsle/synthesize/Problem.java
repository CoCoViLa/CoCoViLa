package ee.ioc.cs.vsle.synthesize;

import java.io.*;
import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

class Problem implements Serializable {

	private Set<Rel> axioms = new HashSet<Rel>();

	private Set<Var> knownVars = new HashSet<Var>();

	private List<Var> assumptions = new ArrayList<Var>();

	private Set<Var> goals = new HashSet<Var>();

	private Map<String, Var> allVars = new HashMap<String, Var>();

	private Set<Rel> allRels = new HashSet<Rel>();

	private Set<Rel> relWithSubtasks = new HashSet<Rel>();

	private Set<SubtaskRel> subtasks = new HashSet<SubtaskRel>();

	private Vector<Rel> subGoal = null;

	private HashSet<Var> foundVars = new HashSet<Var>();

	private Var rootVarThis;
	
	Var getVarByFullName( String field ) {
		
		for (Var var : allVars.values() ) {

			if ( var.getFullName().equals( field ) ) {
				return var;
			}
		}
		return null;
	}

	Problem( Var varThis ) {
		rootVarThis = varThis;
	}
	
	Var getRootVar() {
		return rootVarThis;
	}
	
	void addSubtask(SubtaskRel rel) {
		subtasks.add(rel);
	}

	Map<String, Var> getAllVars() {
		return allVars;
	}

	Set<Rel> getAxioms() {
		return axioms;
	}

	Set<Var> getKnownVars() {
		return knownVars;
	}

	Set<Var> getFoundVars() {
		return foundVars;
	}

	Set<Var> getGoals() {
		return goals;
	}

	Set<Rel> getAllRels() {
		return allRels;
	}

	Set<Rel> getRelsWithSubtasks() {
		return relWithSubtasks;
	}

	void addAxiom(Rel rel) {
		axioms.add(rel);
	}

	void addGoal(Var var) {
		goals.add(var);
	}

	void addRel(Rel rel) {
		allRels.add(rel);
	}

	void addAllRels(HashSet<Rel> set) {
		allRels.addAll(set);
	}

	void addRelWithSubtask(Rel rel) {
		relWithSubtasks.add(rel);
	}

	void addVar(Var var) {
		allVars.put(var.getFullName(), var);
	}

	SubtaskRel getSubtask(SubtaskRel subt) {
		for (Iterator<SubtaskRel> iter = subtasks.iterator(); iter.hasNext();) {
			SubtaskRel subtask = iter.next();
			if (subtask.equals(subt))
				return subtask;
		}
		return null;
	}

	public List<Var> getAssumptions() {
		return assumptions;
	}

	Problem getCopy() {
		
		try {
			return DeepCopy.copy( this );

		} catch (Exception e) {
			
			db.p( "\nUnable to get the copy of Problem, due to: " );
			
			if( RuntimeProperties.isLogDebugEnabled() ) {
				e.printStackTrace();
				db.p( "\nProblem:\n" + this.toString() );
			} else {
				db.p( e.getClass().getName() + " " + e.getMessage() );
			}
			
			return null;
		}
	}

	public String toString() {
		return ("All: " + allVars + "\n Rels: " + allRels + "\n Known: "
				+ knownVars + "\n Targets:" + goals + "\n Axioms:"
				+ axioms + "\n Subtasks:" + relWithSubtasks + "\n subGoal:"
				+ subGoal + "\n");
	}

}
