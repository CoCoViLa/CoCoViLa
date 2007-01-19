/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.util.*;

/**
 * @author pavelg
 *
 */
public class SubtaskRel extends Rel {


    private List<Rel> algorithm = new ArrayList<Rel>();
    //  parent axiom containing this subtask
    private Rel parentRel = null;
    
	/**
	 * 
	 */
	public SubtaskRel( Rel rel, Var parent, String decl ) {
		super( parent, decl );
		
		parentRel = rel;
	}

    Rel getParentRel() {

        return parentRel;
    }

    void addRelToAlgorithm(Rel rel) {

        algorithm.add(rel);
    }

    List<Rel> getAlgorithm() {

        return algorithm;
    }

}
