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

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class Optimizer {

  private static final Logger logger = LoggerFactory.getLogger(Optimizer.class);

  private Optimizer() {}

	/**
	 Takes an algorithm and optimizes it to only calculate the variables that are goals.
	 @param algorithm an unoptimized algorithm
	 @param goals the variables which the algorithm has to calculate (other branches are removed)
	 */   
    public static void optimize( PlanningContext context, EvaluationAlgorithm algorithm ) {
    	optimize( context, algorithm, new HashSet<Var>( context.getAllGoals() ), "" );
    }
    
	private static void optimize( PlanningContext context, EvaluationAlgorithm algorithm, Set<Var> goals, String p ) {
		Rel rel;
		PlanningResult res;
		EvaluationAlgorithm removeThese = new EvaluationAlgorithm();
		
		logger.debug(p + "!!!--------- Starting Optimization with targets: " + goals + " ---------!!!");
		
		for (int i = algorithm.size() - 1; i >= 0; i--) {
			logger.debug( p + "Reguired vars: " + goals );
			
			res = algorithm.get(i);
            rel = res.getRel();
			logger.debug( p + "Rel from algorithm: " + rel );
			boolean relIsNeeded = false;

			Set<Var> outputs = new LinkedHashSet<Var>();
			CodeGenerator.unfoldVarsToSet(rel.getOutputs(), outputs);
			
			for ( Var relVar : outputs ) {
				if (goals.contains(relVar)) {
					relIsNeeded = true;
					goals.remove( relVar );
				}
			}

			if ( relIsNeeded ) {
				logger.debug( p + "Required");
				
				if( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
					Set<Var> tmpSbtInputs = new LinkedHashSet<Var>();
					for (SubtaskRel subtask : rel.getSubtasks() ) {
						logger.debug( p + "Optimizing subtask: " + subtask );
						HashSet<Var> subGoals = new HashSet<Var>();
						CodeGenerator.unfoldVarsToSet( subtask.getOutputs(), subGoals );
						// the problem object is required only on the top level
						optimize( null, res.getSubtaskAlgorithm( subtask ), subGoals, incPrefix( p ) );
						logger.debug(p + "Finished optimizing subtask: " + subtask);
						logger.debug( p + "Required inputs from upper level: " + subGoals);

						tmpSbtInputs.addAll(subGoals);
					}
					goals.addAll(tmpSbtInputs);
				}
				Set<Var> inputs = new LinkedHashSet<Var>();
                CodeGenerator.unfoldVarsToSet(rel.getInputs(), inputs);
				goals.addAll(inputs);
			} else {
				logger.debug( p + "Removed");
				removeThese.add(res);
			}
		}
		logger.debug( p + "Initial algorithm: " + algorithm + "\nRels to remove: " + removeThese );

		//remove unneeded relations
		for (PlanningResult resToRemove : removeThese) {
			if( algorithm.indexOf( resToRemove ) > -1 ) {
				algorithm.remove( resToRemove);
			}
			
			if( context != null ) {
			    /* 
			     * Do not keep vars in Found set if a relation that introduces 
			     * those vars has been removed, otherwise the propagation procedure
			     * may overwrite values of such variables.
			     */
			    Set<Var> outputs = new LinkedHashSet<Var>();
	            CodeGenerator.unfoldVarsToSet(resToRemove.getRel().getOutputs(), outputs);
			    context.getFoundVars().removeAll( outputs );
			}
		}
		logger.debug( p + "Optimized Algorithm: " + algorithm );
	}
	
	private static String incPrefix( String p ) {
		if( p == null || p.length() == 0 ) {
			return ">";
		}
		return p + p.substring( 0, 1 );
	}
}
