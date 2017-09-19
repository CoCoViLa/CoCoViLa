/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
