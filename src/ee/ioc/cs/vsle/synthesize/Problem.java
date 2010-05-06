package ee.ioc.cs.vsle.synthesize;

import java.util.*;

/**
 * Represents the problem graph
 */
class Problem {

	private Set<Rel> axioms = new LinkedHashSet<Rel>();
	private List<Var> assumptions = new ArrayList<Var>();
	private Map<String, Var> allVars = new LinkedHashMap<String, Var>();
	private Set<Rel> allRels = new LinkedHashSet<Rel>();
	private Set<Rel> relWithSubtasks = new LinkedHashSet<Rel>();
	private Set<SubtaskRel> subtasks = new LinkedHashSet<SubtaskRel>();
	private Var rootVarThis;
	private PlanningContext currentContext;
	
	Problem( Var varThis ) {
		rootVarThis = varThis;
		addVar( varThis );
		currentContext = new PlanningContext();
	}
	
	PlanningContext getCurrentContext() {
	    return currentContext;
	}
	
	Var getRootVar() {
		return rootVarThis;
	}

	void addVar(Var var) {
	    allVars.put(var.getFullName(), var);
	}

	Var getVar(String varName) {
	    return allVars.get( varName );
	}
	
	boolean containsVar(String varName) {
	    return allVars.containsKey( varName );
	}

	Var getVarByFullName( String field ) {

	    for (Var var : allVars.values() ) {

	        if ( var.getFullName().equals( field ) ) {
	            return var;
	        }
	    }
	    return null;
	}

	Set<Rel> getAxioms() {
		return axioms;
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

	void addRel(Rel rel) {
		allRels.add(rel);
		currentContext.addUnknownInputs( rel, rel.getInputs() );
	}

	void addRelWithSubtask(Rel rel) {
		relWithSubtasks.add(rel);
	}

    void addSubtask(SubtaskRel rel) {
        subtasks.add(rel);
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

	@Override
    public String toString() {
		return ("All: " + allVars + "\n Rels: " + allRels 
		        + "\n Axioms:" + axioms + "\n Subtasks:" + relWithSubtasks );
	}
}
