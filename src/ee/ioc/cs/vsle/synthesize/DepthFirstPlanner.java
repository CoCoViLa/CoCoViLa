/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.awt.Component;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;
import ee.ioc.cs.vsle.vclass.*;

/**
 * @author pavelg
 * 
 */
public class DepthFirstPlanner implements IPlanner {

	private static DepthFirstPlanner s_planner = null;

    private DepthFirstPlanner() {
    }

    public static DepthFirstPlanner getInstance() {
        if ( s_planner == null ) {
            s_planner = new DepthFirstPlanner();
        }
        return s_planner;
    }

    public String getDescription() {
    	return "Depth First (Experimental)";
    }
    
    public ArrayList invokePlaning( Problem problem, boolean computeAll ) {
    	long startTime = System.currentTimeMillis();
    	
        ProgramRunner.clearFoundVars();
        ArrayList<Rel> algorithm = new ArrayList<Rel>();
        
        //manage axioms
        for ( Iterator axiomIter = problem.getAxioms().iterator(); axiomIter
                                   .hasNext(); ) {
            Rel rel = ( Rel ) axiomIter.next();
            problem.getKnownVars().addAll( rel.getOutputs() );
            algorithm.add( rel );
            axiomIter.remove();
        }

        problem.getFoundVars().addAll(problem.getKnownVars());
        
        //invoke linear planning
        if ( linearForwardSearch( problem, algorithm, problem.getTargetVars(), 
        		// do not optimize yet if subtasks exist
        		( computeAll || !problem.getRelsWithSubtasks().isEmpty() ) ) 
        		&& !computeAll ) {
        	if ( RuntimeProperties.isDebugEnabled() )
    			db.p( "Problem solved without subtasks");
        } else {
        	subtaskPlanning( problem, algorithm );
        	linearForwardSearch( problem, algorithm, problem.getTargetVars(), computeAll );
        }
		if ( RuntimeProperties.isDebugEnabled() )
			db.p( "Planning time: " + ( System.currentTimeMillis() - startTime ) + "ms.");
        return algorithm;
    }
    
	private boolean linearForwardSearch(Problem problem, List<Rel> algorithm, 
			Set<Var> targetVars,
			boolean computeAll) {

		Set<Var> foundVars = problem.getFoundVars();
		/*
		 * while iterating through hashset, items cant be removed from/added to
		 * that set. Theyre collected into these sets and added/removedall
		 * together after iteration is finished
		 */
		HashSet<Var> newVars = new HashSet<Var>();
		HashSet<Var> removableVars = new HashSet<Var>();
		// backup goals for later optimization
		HashSet<Var> allTargetVars = new HashSet<Var>( targetVars );
		
		boolean changed = true;

		if (RuntimeProperties.isDebugEnabled())
			db.p("------Starting linear planning with (sub)goals: "
					+ targetVars + "--------");

		int counter = 1;

		while ((!computeAll && changed && !targetVars.isEmpty())
				|| (changed && computeAll)) {

			if (RuntimeProperties.isDebugEnabled())
				db.p("----Iteration " + counter + " ----");

			counter++;
			changed = false;
			// remove targets if they're already known
			for (Iterator<Var> targetIter = targetVars.iterator(); targetIter
					.hasNext();) {
				Var targetVar = targetIter.next();
				if (problem.getKnownVars().contains(targetVar)) {
					targetIter.remove();
				}
			}
			// iterate through all knownvars
			if (RuntimeProperties.isDebugEnabled())
				db.p("Known:" + problem.getKnownVars());
			
			for (Var var : problem.getKnownVars() ) {
				
				if (RuntimeProperties.isDebugEnabled())
					db.p("Current Known: " + var);
				// Check the relations of all components
				for (Rel rel : var.getRels() ) {
					if (RuntimeProperties.isDebugEnabled())
						db.p("And its rel: " + rel);
					if (problem.getAllRels().contains(rel)) {
						rel.setUnknownInputs(rel.getUnknownInputs() - 1);

						if (RuntimeProperties.isDebugEnabled())
							db.p("problem contains it " + rel
									+ " unknownInputs: "
									+ rel.getUnknownInputs());

						removableVars.add(var);

						if (rel.getUnknownInputs() == 0
								&& rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK) {

							if (RuntimeProperties.isDebugEnabled())
								db.p("rel is ready to be used " + rel);

							boolean relIsNeeded = false;

							if (RuntimeProperties.isDebugEnabled())
								db.p("tema outputsid " + rel.getOutputs());

							for (Var relVar : rel.getOutputs() ) {

								if (!foundVars.contains(relVar)) {
									relIsNeeded = true;
								}
							}

							if (rel.getOutputs().isEmpty()) {
								relIsNeeded = true;
							}
							if (RuntimeProperties.isDebugEnabled())
								db.p("relIsNeeded " + relIsNeeded);

							if (relIsNeeded) {

								if (RuntimeProperties.isDebugEnabled())
									db.p("ja vajati " + rel);

								if (!rel.getOutputs().isEmpty()) {
									newVars.addAll(rel.getOutputs());
									foundVars.addAll(rel.getOutputs());
								}
								algorithm.add(rel);
							}

							problem.getAllRels().remove(rel);
							changed = true;
						}
					}
				}
			}

			if (RuntimeProperties.isDebugEnabled())
				db.p("foundvars " + foundVars);

			problem.getKnownVars().addAll(newVars);
			problem.getKnownVars().removeAll(removableVars);
			newVars.clear();
		}
		if (RuntimeProperties.isDebugEnabled())
			db.p("algorithm " + algorithm);

		if (!computeAll) {
			Optimizer.getInstance().optimize( algorithm, allTargetVars );
			
			if (RuntimeProperties.isDebugEnabled())
				db.p("Optimized algorithm" + algorithm.toString() + "\n");
		}

		ProgramRunner.addAllFoundVars(foundVars);

		return targetVars.isEmpty();
	}

	private static int maxDepth = 1;//0..2, i.e. 3
	
	private void subtaskPlanning(Problem problem, ArrayList<Rel> algorithm ) {
		
		if (RuntimeProperties.isDebugEnabled()) {
			db.p( "!!!--------- Starting Planning With Subtasks ---------!!!" );
			db.p( "maxDepth: " + ( maxDepth + 1 ) );
		}
		
		for ( Rel relWithSubtask : problem.getRelsWithSubtasks() ) {
			for ( Rel subtask : relWithSubtask.getSubtasks() ) {
				
				problem.addSubtask( subtask );
				subtask.setParentRel( relWithSubtask );
			}
		}
		
		subtaskPlanningImpl( problem, algorithm, new ArrayList(), 0 );
	}
	
	private boolean subtaskPlanningImpl(Problem problem, List<Rel> algorithm,
			ArrayList subgoals, int depth) {

		List<Var> newVars = new ArrayList<Var>();
		// or
		OR: for (Rel subtaskRel : problem.getRelsWithSubtasks()) {
			if (RuntimeProperties.isDebugEnabled())
				db.p( "OR: rel with subtasks - " + subtaskRel + " depth: " + ( depth + 1 ) );
			// this is true if all subtasks are solvable
			boolean allSolved = true;
			// and
			AND: for (Rel subtask : subtaskRel.getSubtasks()) {
				if (RuntimeProperties.isDebugEnabled())
					db.p( "AND: subtask - " + subtask );
				// lets clone the environment
				Problem problemNew = problem.getCopy();
				// we need fresh cloned subtask instance
				Rel subtaskNew = problemNew.getSubtask(subtask);

				prepareSubtask( problemNew, subtaskNew );
				
				boolean solved = 
					// note that we use algorithm of old subtask
					linearForwardSearch( problemNew, subtask.getAlgorithm(), 
							// never optimize here
							new HashSet<Var>(subtaskNew.getOutputs()), true );
				
				if( solved ) {
					if (RuntimeProperties.isDebugEnabled())
						db.p( "Subtask: " + subtask + " solved" );
					continue AND;
				} else if( !solved && ( depth == maxDepth ) ) {
					if (RuntimeProperties.isDebugEnabled())
						db.p( "Subtask: " + subtask + " not solved and cannot go any deeper" );
					continue OR;
				}
				
				if (RuntimeProperties.isDebugEnabled())
					db.p( "Recursing deeper" );
				
				subtaskPlanningImpl( problemNew, subtask.getAlgorithm(), subgoals, depth + 1 );
				
				if (RuntimeProperties.isDebugEnabled())
					db.p( "Back to depth " + ( depth + 1 ) );
				
				// note that we use algorithm of old subtask
				solved = linearForwardSearch( problemNew, subtask.getAlgorithm(), 
						// always optimize here in order to get rid of unnecessary subtask instances
						new HashSet<Var>(subtaskNew.getOutputs()), false );
				
				if (RuntimeProperties.isDebugEnabled())
					db.p( "Subtask: " + subtask + " solved: " + solved );
				
				allSolved &= solved;
				if( !allSolved ) {
					continue OR;
				}
			}
			
			if( allSolved ) {
				algorithm.add( subtaskRel );
				newVars.addAll( subtaskRel.getOutputs() );
			}
		}
		
		problem.addKnown( newVars );
		problem.getFoundVars().addAll( newVars );
		return false;
	}

	private void prepareSubtask(Problem problem, Rel subtask) {
		// [x->y] assume x is known
		for (Var input : subtask.getInputs()) {

			problem.addKnown(input);
			// if input var is alias then all its vars should be known as well
			if (input.getField().isAlias()) {
				for (ClassField field : input.getField().getVars()) {
					String object = (input.getObject().equals("this")) ? ""
							: input.getObject().substring(5) + ".";
					Var var = problem.getVarByFullName(object
							+ field.toString());

					if (var != null) {
						problem.getKnownVars().add(var);
					}
				}
			}
		}

		HashSet<Rel> removableRels = new HashSet<Rel>();
		// remove all rels with otputs same as subtask inputs
		for (Var subtaskInput : subtask.getInputs()) {

			for (Rel rel : problem.getAllRels()) {
				if ( ( rel.getOutputs().size() > 0 ) 
						&& ( rel.getOutputs().get(0) == subtaskInput ) ) {
					removableRels.add( rel );
//					problem.getAllRels().remove(rel);// rel??? iter.remove();
				}
			}
		}
		
		// remove all rels with inputs same as subtask outputs
		for (Var subtaskOutput : subtask.getOutputs()) {
			for (Rel rel : problem.getAllRels()) {
				if (rel.getType() == RelType.TYPE_EQUATION
						&& rel.getInputs().get(0) == subtaskOutput) {
					removableRels.add( rel );
//					problem.getAllRels().remove(rel);// rel??? iter.remove();
				}
			}
		}
		problem.getAllRels().removeAll(removableRels);
		removableRels.clear();
	}
	
	public Component getCustomOptionComponent() {
		final JSpinner spinner = new JSpinner( new SpinnerNumberModel( maxDepth + 1, 1, 3, 1 ) );
		spinner.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				Integer value = (Integer)((JSpinner)e.getSource()).getValue();
				maxDepth = value - 1;
			}});
		JPanel panel = new JPanel()
		{
			public void setEnabled( boolean b ) {
				super.setEnabled( b );
				spinner.setEnabled( b );
			}
		};
		panel.add( new JLabel("Max Depth: ") );
		panel.add( spinner );
    	return panel;
    }  
}
