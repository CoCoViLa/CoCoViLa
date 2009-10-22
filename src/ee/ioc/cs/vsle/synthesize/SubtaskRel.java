/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.vclass.*;

/**
 * @author pavelg
 *
 */
public class SubtaskRel extends Rel {

    private List<Rel> algorithm = new ArrayList<Rel>();
    
    private Problem context;
    private ClassField contextCF;
    
    /** 
     * "true" means that subtask is solvable, 
     * "false" means non-solvable, 
     * "null" - planning has not been performed yet 
     */
    private Boolean isSolvable;
    
	/**
	 * 
	 */
	public SubtaskRel( Var parent, String decl ) {
		super( parent, decl );
	}

    void addRelToAlgorithm(Rel rel) {

        algorithm.add(rel);
    }

    List<Rel> getAlgorithm() {

        return algorithm;
    }

    public boolean isIndependent() {
    	return context != null;
    }

	public Problem getContext() {
		return context;
	}

	public void setContext(Problem context) {
		this.context = context;
	}

	public Boolean isSolvable() {
		return isSolvable;
	}

	public void setSolvable(Boolean isSolvable) {
		this.isSolvable = isSolvable;
	}

	public ClassField getContextCF() {
		return parent.getField();
	}
    
}
