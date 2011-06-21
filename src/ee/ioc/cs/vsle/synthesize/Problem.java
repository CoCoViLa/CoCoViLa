package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;

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
		return ("Vars: " + ( RuntimeProperties.isLogDebugEnabled() ? printVarsDetails( allVars.values() ) : allVars.keySet() ) 
		        + "\n Rels: " + printRels( allRels ) 
		        + ( !axioms.isEmpty() ? "\n Axioms: " + printRels( axioms ) : "")
		        + ( !relWithSubtasks.isEmpty() ? "\n Subtasks: " + printRels( relWithSubtasks ) : "" )
		        + ( !getCurrentContext().getKnownVars().isEmpty() ? "\n Known: " + getCurrentContext().getKnownVars() : "" ) );
	}
	
	private String printRels( Collection<Rel> rels ) {
	    StringBuilder sb = new StringBuilder();
	    for ( Rel rel : rels ) {
	        if( sb.length() > 0 ) sb.append( "\n" );
            sb.append( rel.getDeclaration() );
        }
	    return sb.toString();
	}
	
	private String printVarsDetails( Collection<Var> vars) {
	    StringBuilder sb = new StringBuilder();
        for ( Var var : vars ) {
            if( sb.length() > 0 ) sb.append( "\n" );
            sb.append( "Name = ").append( var.getFullName() );
            if( !var.getRels().isEmpty() ) {
                sb.append( ", Rels: \n" ).append( printRels( var.getRels() ) );
            }
            if( var.getField().isAlias() && !var.getChildVars().isEmpty() ) {
                sb.append( "\nChild vars: " ).append( var.getChildVars() );
            }
        }
        return sb.toString();
	}
	
}
