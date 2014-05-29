/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.*;

/**
 * @author pavelg
 *
 */
public class SubtaskRel extends Rel {

    private Problem context;
    
    /** 
     * "true" means that subtask is solvable, 
     * "false" means non-solvable, 
     * "null" - planning has not been performed yet 
     */
    private Boolean isSolvable;
    
	public SubtaskRel( Var parent, String decl ) {
		super( parent, decl );
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
