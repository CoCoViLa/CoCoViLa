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
    
//    public void optimize(List<Rel> algorithm, Set<Var> targets) {
//    	
//    }
    
	public void optimize(List<Rel> algorithm, Set<Var> targets) {
		Set<Var> stuff = targets;
		Rel rel;
		Var relVar;
		ArrayList<Rel> removeThese = new ArrayList<Rel>();
		
		if (RuntimeProperties.isDebugEnabled())
			db.p("!!!--------- Starting Optimization with targets: " + targets + " ---------!!!");
		
		for (int i = algorithm.size() - 1; i >= 0; i--) {
			if (RuntimeProperties.isDebugEnabled())
				db.p( "Reguired vars: " + stuff );
            rel = algorithm.get(i);
            if (RuntimeProperties.isDebugEnabled())
    			db.p("Rel from algorithm: " + rel );
			boolean relIsNeeded = false;

			for (int j = 0; j < rel.getOutputs().size(); j++) {
				relVar = rel.getOutputs().get(j);
				if (stuff.contains(relVar)) {
					relIsNeeded = true;
				}
			}

			if (relIsNeeded && ( rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK ) ) {
				if (RuntimeProperties.isDebugEnabled())
	    			db.p("Required");
				stuff.addAll(rel.getInputs());
			} else if( rel.getType() == RelType.TYPE_METHOD_WITH_SUBTASK ) {
				boolean need = true;
				HashSet<Var> tmpSbtInputs = new HashSet<Var>();
				for (Rel subtask : rel.getSubtasks() ) {
					if (RuntimeProperties.isDebugEnabled())
		    			db.p("Optimizing subtask: " + subtask );
					HashSet<Var> tmpSbtOutputs = new HashSet<Var>( subtask.getOutputs() );
					optimize( subtask.getAlgorithm(), tmpSbtOutputs );
					need &= subtask.getAlgorithm().isEmpty();
					if( !need && !relIsNeeded ) {
						removeThese.add(rel);
						break;
					}
					tmpSbtInputs.addAll(tmpSbtOutputs);
				}
				stuff.addAll(rel.getInputs());
				stuff.addAll(tmpSbtInputs);
			} else {
				if (RuntimeProperties.isDebugEnabled())
	    			db.p("Removed");
				removeThese.add(rel);
			}
		}
		algorithm.removeAll(removeThese);
	}
}
