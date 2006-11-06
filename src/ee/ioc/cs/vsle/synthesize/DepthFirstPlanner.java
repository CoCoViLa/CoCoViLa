/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import ee.ioc.cs.vsle.editor.*;
import ee.ioc.cs.vsle.util.*;

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
    	return "Depth First";
    }
    
    public ArrayList invokePlaning( Problem problem, boolean computeAll ) {
    	long startTime = System.currentTimeMillis();
    	
        ProgramRunner.clearFoundVars();
        ArrayList<Rel> algorithm = new ArrayList<Rel>();
        
        //manage axioms
        for ( Iterator<Rel> axiomIter = problem.getAxioms().iterator(); axiomIter.hasNext(); ) {
            Rel rel = axiomIter.next();
            algorithm.add( rel );
            axiomIter.remove();
        }

        problem.getFoundVars().addAll(problem.getKnownVars());
        
        //invoke linear planning
        if ( linearForwardSearch( problem, algorithm, problem.getGoals(), 
        		// do not optimize yet if subtasks exist
        		( computeAll || !problem.getRelsWithSubtasks().isEmpty() ) ) && !computeAll ) {
        	
        	if ( RuntimeProperties.isLogInfoEnabled() )
    			db.p( "Problem solved without subtasks");
        	
        } else if ( !problem.getRelsWithSubtasks().isEmpty() ) {
        	
        	subtaskPlanning( problem, algorithm );
        	
        	linearForwardSearch( problem, algorithm, problem.getGoals(), computeAll );
        	
        }
		if ( RuntimeProperties.isLogInfoEnabled() )
			db.p( "Planning time: " + ( System.currentTimeMillis() - startTime ) + "ms.");
		
		ProgramRunner.addAllFoundVars( problem.getFoundVars() );
		
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

		if (RuntimeProperties.isLogDebugEnabled())
			db.p("------Starting linear planning with (sub)goals: "
					+ targetVars + "--------");

		if (RuntimeProperties.isLogDebugEnabled())
			db.p("Algorithm " + algorithm);
		
		int counter = 1;

		while ((!computeAll && changed && !targetVars.isEmpty())
				|| (changed && computeAll)) {

			if (RuntimeProperties.isLogDebugEnabled())
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
			if (RuntimeProperties.isLogDebugEnabled())
				db.p("Known:" + problem.getKnownVars());
			
			for (Var var : problem.getKnownVars() ) {
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p("Current Known: " + var);
				// Check the relations of all components
				for (Rel rel : var.getRels() ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p("And its rel: " + rel);
					if (problem.getAllRels().contains(rel)) {
						if( !var.getField().isConstant() ) {
							rel.setUnknownInputs(rel.getUnknownInputs() - 1);
						}

						if (RuntimeProperties.isLogDebugEnabled())
							db.p("problem contains it " + rel
									+ " unknownInputs: "
									+ rel.getUnknownInputs());

						removableVars.add(var);

						if (rel.getUnknownInputs() == 0
								&& rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK) {

							if (RuntimeProperties.isLogDebugEnabled())
								db.p("rel is ready to be used " + rel);

							boolean relIsNeeded = false;

							if (RuntimeProperties.isLogDebugEnabled())
								db.p("tema outputsid " + rel.getOutputs());

							for (Var relVar : rel.getOutputs() ) {

								if (!foundVars.contains(relVar)) {
									relIsNeeded = true;
								}
							}

							if (rel.getOutputs().isEmpty()) {
								relIsNeeded = true;
							}
							if (RuntimeProperties.isLogDebugEnabled())
								db.p("relIsNeeded " + relIsNeeded);

							if (relIsNeeded) {

								if (RuntimeProperties.isLogDebugEnabled())
									db.p("ja vajati " + rel);

								if (!rel.getOutputs().isEmpty()) {
									newVars.addAll(rel.getOutputs());
									foundVars.addAll(rel.getOutputs());
								}
								algorithm.add(rel);
								if (RuntimeProperties.isLogDebugEnabled())
									db.p("algorithm " + algorithm);
							}

							problem.getAllRels().remove(rel);
							changed = true;
						}
					}
				}
			}

			if (RuntimeProperties.isLogDebugEnabled())
				db.p("foundvars " + foundVars);

			problem.getKnownVars().addAll(newVars);
			problem.getKnownVars().removeAll(removableVars);
			newVars.clear();
		}
		if (RuntimeProperties.isLogDebugEnabled())
			db.p("algorithm " + algorithm);

		if (!computeAll) {
			Optimizer.optimize( algorithm, new HashSet<Var>( allTargetVars ) );
		}

		return targetVars.isEmpty() || problem.getFoundVars().containsAll( allTargetVars );
	}

	private static int maxDepth = 2;//0..2, i.e. 3
	private static boolean m_isSubtaskRepetitionAllowed = false;
	
	private void subtaskPlanning(Problem problem, ArrayList<Rel> algorithm ) {
		
		if (RuntimeProperties.isLogDebugEnabled()) {
			db.p( "!!!--------- Starting Planning With Subtasks ---------!!!" );
		}
		
		int maxDepthBackup = maxDepth;
		
		if( !m_isSubtaskRepetitionAllowed ) {
			maxDepth = problem.getRelsWithSubtasks().size() - 1;
		}
		
		if (RuntimeProperties.isLogDebugEnabled()) {
			db.p( "maxDepth: " + ( maxDepth + 1 ) );
		}
		
		subtaskPlanningImpl( problem, algorithm, new ArrayList(), new HashSet<Rel>(), 0 );
		
		maxDepth = maxDepthBackup;
	}
	
	private boolean subtaskPlanningImpl(Problem problem, List<Rel> algorithm,
			ArrayList subgoals, HashSet<Rel> subtaskRelsInPath, int depth) {

		Set<Var> newVars = new HashSet<Var>();
		
		// or
		OR: for (Rel subtaskRel : problem.getRelsWithSubtasks()) {
			if (RuntimeProperties.isLogDebugEnabled())
				db.p( "OR: rel with subtasks - " + subtaskRel + " depth: " + ( depth + 1 ) );
			
			HashSet<Rel> newPath = new HashSet<Rel>();
			
			if( !m_isSubtaskRepetitionAllowed && subtaskRelsInPath.contains(subtaskRel) ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "This rel with subtasks is already in use, path: " + newPath );
				continue;
			} else if( !m_isSubtaskRepetitionAllowed ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "This rel with subtasks can be used, path: " + newPath );
				newPath.addAll( subtaskRelsInPath );
				newPath.add( subtaskRel );
			}
			
			// this is true if all subtasks are solvable
			boolean allSolved = true;
			// and
			AND: for (SubtaskRel subtask : subtaskRel.getSubtasks()) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "AND: subtask - " + subtask );
				// lets clone the environment
				Problem problemNew = problem.getCopy();
				// we need fresh cloned subtask instance
				SubtaskRel subtaskNew = problemNew.getSubtask(subtask);

				prepareSubtask( problemNew, subtaskNew );
				
				boolean solved = 
					linearForwardSearch( problemNew, subtaskNew.getAlgorithm(), 
							// never optimize here
							new HashSet<Var>(subtaskNew.getOutputs()), true );
				
				if( solved ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p( "Subtask: " + subtaskNew + " solved" );
					subtask.getAlgorithm().addAll( subtaskNew.getAlgorithm() );
					continue AND;
				} else if( !solved && ( depth == maxDepth ) ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p( "Subtask: " + subtaskNew + " not solved and cannot go any deeper" );
					continue OR;
				}
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Recursing deeper" );
				
				subtaskPlanningImpl( problemNew, subtaskNew.getAlgorithm(), subgoals, newPath, depth + 1 );
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Back to depth " + ( depth + 1 ) );
				
				solved = linearForwardSearch( problemNew, subtaskNew.getAlgorithm(), 
						// always optimize here in order to get rid of unnecessary subtask instances
						// temporary true while optimization does not work correctly
						new HashSet<Var>(subtaskNew.getOutputs()), true );
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Subtask: " + subtaskNew + " solved: " + solved );
				
				allSolved &= solved;
				if( !allSolved ) {
					continue OR;
				}
				// copy algorithm from cloned subtask to old
				subtask.getAlgorithm().addAll( subtaskNew.getAlgorithm() );
			}
			
			if( allSolved ) {
				algorithm.add( subtaskRel );
				//newVars.addAll( subtaskRel.getOutputs() );
				
				addKnownVarsToSet( problem, subtaskRel.getOutputs(), newVars );
//				for (Var output : subtaskRel.getOutputs() ) {
//
//					newVars.add(output);
//					// if output var is alias then all its vars should be found as well
//					if (output.getField().isAlias()) {
//						for (ClassField field : output.getField().getVars()) {
//							String object = (output.getObject().equals("this")) ? ""
//									: output.getObject().substring(5) + ".";
//							Var var = problem.getVarByFullName(object
//									+ field.toString());
//
//							if (var != null) {
//								newVars.add(var);
//							}
//						}
//					}
//				}
				
			} else if( !m_isSubtaskRepetitionAllowed ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "Subtasks not solved, removing rel from path " + subtaskRel );
				newPath.remove( subtaskRel );
			}
		}
		
		problem.getKnownVars().addAll( newVars );
		problem.getFoundVars().addAll( newVars );
		return false;
	}

	private void addKnownVarsToSet( Problem problem, List<Var> fromList, Set<Var> toSet ) {
		for (Var topvar : fromList ) {

			toSet.add(topvar);
			// if output var is alias then all its vars should be found as well
			if ( topvar.getField().isAlias() ) {
				for ( Var var : topvar.getChildVars() ) {

					toSet.add(var);
					if( var.getField().isAlias() ) {
						//this is used if we have alias in alias structure
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( "addVarsToList: alias " + var + " in alias " + topvar );
						addKnownVarsToSet( problem, var.getChildVars(), toSet );//recursion
					}
				}
			}
		}
	}
	
	private void prepareSubtask(Problem problem, Rel subtask) {
		// [x->y] assume x is known
		addKnownVarsToSet( problem, subtask.getInputs(), problem.getKnownVars() );
		
//		for (Var input : subtask.getInputs()) {
//
//			problem.getKnownVars().add(input);
//			// if input var is alias then all its vars should be known as well
//			if (input.getField().isAlias()) {
//				for (ClassField field : input.getField().getVars()) {
//					String object = (input.getObject().equals("this")) ? ""
//							: input.getObject().substring(5) + ".";
//					Var var = problem.getVarByFullName(object
//							+ field.toString());
//
//					if (var != null) {
//						problem.getKnownVars().add(var);
//					}
//				}
//			}
//		}

		HashSet<Rel> removableRels = new HashSet<Rel>();
		// remove all rels with otputs same as subtask inputs
		for (Var subtaskInput : subtask.getInputs()) {

			for (Rel rel : problem.getAllRels()) {
				if ( ( rel.getOutputs().size() > 0 ) 
						&& ( rel.getOutputs().get(0) == subtaskInput ) ) {
					removableRels.add( rel );
				}
			}
		}
		
		/* Pavel[27Mar06]
		 * probably we do not need the functionality commented below 
		 * because sometimes we want to compute something else 
		 * when subtask's output is known, e.g. -
		 * 
		 * double a,b,c,d,x,y;
		 * y=sin(x);
		 * a = x;
		 * b = y;
		 * c=a+b;
		 * [x->y]->d{f};
		 * 
		// remove all rels with inputs same as subtask outputs
		for (Var subtaskOutput : subtask.getOutputs()) {
			for (Rel rel : problem.getAllRels()) {
				if (rel.getType() == RelType.TYPE_EQUATION
						&& rel.getInputs().get(0) == subtaskOutput) {
					removableRels.add( rel );
				}
			}
		}
		*/
		
		problem.getAllRels().removeAll(removableRels);
		removableRels.clear();
	}
	
	public Component getCustomOptionComponent() {
		final JSpinner spinner = new JSpinner( new SpinnerNumberModel( maxDepth + 1, 1, 3, 1 ) );
		spinner.addChangeListener(new ChangeListener(){

			public void stateChanged(ChangeEvent e) {
				Integer value = (Integer)((JSpinner)e.getSource()).getValue();
				maxDepth = value - 1;
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "maxDepth " + ( maxDepth + 1 ) );
			}});
		final JPanel panel = new JPanel()
		{
			public void setEnabled( boolean b ) {
				super.setEnabled( b );
				spinner.setEnabled( b );
			}
		};
		panel.add( new JLabel("Max Depth: ") );
		panel.add( spinner );
		
		panel.setEnabled( m_isSubtaskRepetitionAllowed );
		
		final JCheckBox chbox = new JCheckBox( "Allow subtask recursive repetition", m_isSubtaskRepetitionAllowed );
		
		chbox.addChangeListener( new ChangeListener() {

			public void stateChanged(ChangeEvent e) {
				m_isSubtaskRepetitionAllowed = chbox.isSelected();
				panel.setEnabled( m_isSubtaskRepetitionAllowed );
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( "m_isSubtaskRepetitionAllowed " + m_isSubtaskRepetitionAllowed );
			}} );
		
		JPanel container = new JPanel( new GridLayout( 2, 0 ) );
		
		container.add( chbox );
		container.add( panel );
		
    	return container;
    }  
}
