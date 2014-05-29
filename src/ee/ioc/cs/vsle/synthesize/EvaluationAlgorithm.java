/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.util.*;

/**
 * Evaluation Algorithm is a list of relations wrapped
 * into PlanningResult objects.
 * 
 * @author pavelg
 */
public class EvaluationAlgorithm extends ArrayList<PlanningResult> {

    public void addRel(Rel rel) {
        add(new PlanningResult( rel ));
    }
}
