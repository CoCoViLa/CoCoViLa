package ee.ioc.cs.vsle.synthesize;

import java.util.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

/**
 */
public class Optimizer {
    
    private static Optimizer s_optimizer = null;
    
    private Optimizer() {}
    
    public static Optimizer getInstance()
    {
        if(s_optimizer == null)
        {
            s_optimizer = new Optimizer();
        }
        return s_optimizer;
    }
	/**
	 Takes an algorithm and optimizes it to only calculate the variables that are targets.
	 @return an algorithm for calculating the target variables
	 @param algorithm an unoptimized algorithm
	 @param targets the variables which the algorithm has to calculate (other branches are removed)
	 */   
    public void optimize(List<Rel> algorithm, Set<Var> targets) {
    	optimize( algorithm, targets, "" );
    }
    
	private void optimize(List<Rel> algorithm, Set<Var> targets, String p ) {
		Set<Var> stuff = targets;
		Rel rel;
		Var relVar;
		ArrayList<Rel> removeThese = new ArrayList<Rel>();
		
		if (RuntimeProperties.isLogDebugEnabled())
			db.p( p + "!!!--------- Starting Optimization with targets: " + targets + " ---------!!!");
		
		for (int i = algorithm.size() - 1; i >= 0; i--) {
			if (RuntimeProperties.isLogDebugEnabled())
				db.p( p + "Reguired vars: " + stuff );
            rel = algorithm.get(i);
            if (RuntimeProperties.isLogDebugEnabled())
    			db.p( p + "Rel from algorithm: " + rel );
			boolean relIsNeeded = false;

			for (int j = 0; j < rel.getOutputs().size(); j++) {
				relVar = rel.getOutputs().get(j);
				if (stuff.contains(relVar)) {
					relIsNeeded = true;
					stuff.remove( relVar );
				}
			}

			if ( relIsNeeded ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p + "Required");
				
				if( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
					HashSet<Var> tmpSbtInputs = new HashSet<Var>();
					for (Rel subtask : rel.getSubtasks() ) {
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( p + "Optimizing subtask: " + subtask );
						HashSet<Var> subGoals = new HashSet<Var>( subtask.getOutputs() );
						optimize( subtask.getAlgorithm(), subGoals, incPrefix( p ) );
						if (RuntimeProperties.isLogDebugEnabled()) {
							db.p( p + "Finished optimizing subtask: " + subtask );
							db.p( p + "Required inputs from upper level: " + subGoals );
						}

						tmpSbtInputs.addAll(subGoals);
					}
					stuff.addAll(tmpSbtInputs);
				} 
				stuff.addAll(rel.getInputs());
			} else {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p + "Removed");
				removeThese.add(rel);
			}
		}
		if (RuntimeProperties.isLogDebugEnabled()) {
			db.p( p + "Initial algorithm: " + algorithm + "\nRels to remove: " + removeThese );
		}
		//algorithm.removeAll(removeThese);
		for (Rel relToRemove : removeThese) {
			if( algorithm.indexOf( relToRemove ) > -1 ) {
				algorithm.remove( relToRemove);
			}
		}
		if (RuntimeProperties.isLogDebugEnabled())
			db.p( p + "Optimized Algorithm: " + algorithm );
	}
	
	private String incPrefix( String p ) {
		if( p == null || p.length() == 0 ) {
			return ">";
		}
		return p + p.substring( 0, 1 );
	}
}
