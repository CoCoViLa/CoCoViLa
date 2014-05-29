package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

/**
 */
public class Optimizer {
    
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
		
		if (RuntimeProperties.isLogDebugEnabled())
			db.p( p + "!!!--------- Starting Optimization with targets: " + goals + " ---------!!!");
		
		for (int i = algorithm.size() - 1; i >= 0; i--) {
			if (RuntimeProperties.isLogDebugEnabled())
				db.p( p + "Reguired vars: " + goals );
			
			res = algorithm.get(i);
            rel = res.getRel();
            if (RuntimeProperties.isLogDebugEnabled())
    			db.p( p + "Rel from algorithm: " + rel );
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
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p + "Required");
				
				if( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
					Set<Var> tmpSbtInputs = new LinkedHashSet<Var>();
					for (SubtaskRel subtask : rel.getSubtasks() ) {
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( p + "Optimizing subtask: " + subtask );
						HashSet<Var> subGoals = new HashSet<Var>();
						CodeGenerator.unfoldVarsToSet( subtask.getOutputs(), subGoals );
						// the problem object is required only on the top level
						optimize( null, res.getSubtaskAlgorithm( subtask ), subGoals, incPrefix( p ) );
						if (RuntimeProperties.isLogDebugEnabled()) {
							db.p( p + "Finished optimizing subtask: " + subtask );
							db.p( p + "Required inputs from upper level: " + subGoals );
						}

						tmpSbtInputs.addAll(subGoals);
					}
					goals.addAll(tmpSbtInputs);
				}
				Set<Var> inputs = new LinkedHashSet<Var>();
                CodeGenerator.unfoldVarsToSet(rel.getInputs(), inputs);
				goals.addAll(inputs);
			} else {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p + "Removed");
				removeThese.add(res);
			}
		}
		if (RuntimeProperties.isLogDebugEnabled()) {
			db.p( p + "Initial algorithm: " + algorithm + "\nRels to remove: " + removeThese );
		}
		
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
		if (RuntimeProperties.isLogDebugEnabled())
			db.p( p + "Optimized Algorithm: " + algorithm );
	}
	
	private static String incPrefix( String p ) {
		if( p == null || p.length() == 0 ) {
			return ">";
		}
		return p + p.substring( 0, 1 );
	}
}
