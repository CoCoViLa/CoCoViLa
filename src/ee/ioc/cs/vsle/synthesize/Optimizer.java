package ee.ioc.cs.vsle.synthesize;

import java.util.ArrayList;
import java.util.HashSet;

/**
 */
public class Optimizer {
	/**
	 Takes an algorithm and optimizes it to only calculate the variables that are targets.
	 @return an algorithm for calculating the target variables
	 @param algorithm an unoptimized algorithm
	 @param targets the variables which the algorithm has to calculate (other branches are removed)
	 */
	static ArrayList optimize(ArrayList algorithm, HashSet targets) {
		HashSet stuff = targets;
		Rel rel;
		Var relVar;
		ArrayList removeThese = new ArrayList();

		for (int i = algorithm.size() - 1; i >= 0; i--) {
            if(!(algorithm.get(i) instanceof String))
                continue;
            rel = (Rel) algorithm.get(i);
			boolean relIsNeeded = false;

			for (int j = 0; j < rel.outputs.size(); j++) {
				relVar = (Var) rel.outputs.get(j);
				if (stuff.contains(relVar)) {
					relIsNeeded = true;
				}
			}

			if (relIsNeeded) {
				stuff.addAll(rel.inputs);
			} else {
				removeThese.add(rel);
			}
		}
		algorithm.removeAll(removeThese);
		return algorithm;
	}

    /** @link dependency */
    /*# Rel lnkRel; */
}
