/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.util.*;

/**
 * This object represents either one step in an algorithm
 * or a tree with algorithms that solve the corresponding subtasks
 * 
 * @author pavelg
 */
public class PlanningResult {

    private Rel rel;
    private Map<SubtaskRel, EvaluationAlgorithm> solutions;
    
    PlanningResult(Rel rel) {
        this.rel = rel;
    }
    
    PlanningResult(Rel rel, boolean hasSubtasks) {
        this.rel = rel;
        if(hasSubtasks)
            solutions = new LinkedHashMap<SubtaskRel, EvaluationAlgorithm>();
    }
    
    /**
     * @return the rel
     */
    public Rel getRel() {
        return rel;
    }
    
    void addSubtaskAlgorithm( SubtaskRel subtask, EvaluationAlgorithm algorithm ) {
        solutions.put( subtask, algorithm );
    }
    
    EvaluationAlgorithm getSubtaskAlgorithm(SubtaskRel subtask) {
        return solutions.get( subtask );
    }

    @Override
    public String toString() {
        return rel.toString();// + ", solutions=" + solutions + "]";
    }
    
    
}
