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
import static ee.ioc.cs.vsle.synthesize.CodeGenerator.*;

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
    
    public ArrayList<Rel> invokePlaning( Problem problem, boolean computeAll ) {
    	long startTime = System.currentTimeMillis();
    	
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
        	
        	int foundVarsCount;
        	//do planning until no new vars are computed
        	do {
        		foundVarsCount = problem.getFoundVars().size();
//        		System.out.println( "Before sb " + foundVarsCount + " " + problem.getFoundVars() );
        		subtaskPlanning( problem, algorithm, computeAll );
//        		System.out.println( "After sb " + problem.getFoundVars().size() + " " + problem.getFoundVars() );
        		if( foundVarsCount < problem.getFoundVars().size() )
        		{
        			foundVarsCount = problem.getFoundVars().size();
        			
        			linearForwardSearch( problem, algorithm, problem.getGoals(), computeAll );
//        			System.out.println( "After lp " + problem.getFoundVars().size() + " " + problem.getFoundVars() );
        			if( ( foundVarsCount == problem.getFoundVars().size() ) 
        					|| problem.getFoundVars().containsAll( problem.getGoals() ) ) {
        				break;
        			}
        		}
        	} while( foundVarsCount < problem.getFoundVars().size() );
        }
		if ( RuntimeProperties.isLogInfoEnabled() )
			db.p( "Planning time: " + ( System.currentTimeMillis() - startTime ) + "ms.");
		
        return algorithm;
    }
    
	private boolean linearForwardSearch(Problem p, List<Rel> algorithm, 
			Set<Var> targetVars,
			boolean computeAll) {
		
		/*
		 * while iterating through hashset, items cant be removed from/added to
		 * that set. Theyre collected into these sets and added/removedall
		 * together after iteration is finished
		 */
		Set<Var> newVars = new LinkedHashSet<Var>();
		Set<Var> relOutputs = new LinkedHashSet<Var>();
		Set<Var> removableVars = new LinkedHashSet<Var>();
		// backup goals for later optimization
		Set<Var> allTargetVars = new LinkedHashSet<Var>( targetVars );
		
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
			for (Iterator<Var> targetIter = targetVars.iterator(); targetIter.hasNext();) {
				Var targetVar = targetIter.next();
				if (p.getFoundVars().contains(targetVar)) {
					targetIter.remove();
				}
			}
			// iterate through all knownvars
			if (RuntimeProperties.isLogDebugEnabled())
				db.p("Known:" + p.getKnownVars());
			
			for (Var var : p.getKnownVars() ) {
				
				if (RuntimeProperties.isLogDebugEnabled())
					db.p("Current Known: " + var);
				
				// Check the relations of all components
				for (Rel rel : var.getRels() ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p("And its rel: " + rel);
					if (p.getAllRels().contains(rel)) {
						if( !var.getField().isConstant() ) {
							rel.removeUnknownInput( var );
						}

						if (RuntimeProperties.isLogDebugEnabled())
							db.p("problem contains it " + rel
									+ " unknownInputs: "
									+ rel.getUnknownInputCount());

						removableVars.add(var);

						if (rel.getUnknownInputCount() == 0
								&& rel.getType() != RelType.TYPE_METHOD_WITH_SUBTASK) {

							if (RuntimeProperties.isLogDebugEnabled())
								db.p("rel is ready to be used " + rel);

							boolean relIsNeeded = false;

							if (RuntimeProperties.isLogDebugEnabled())
								db.p("tema outputsid " + rel.getOutputs());

							for (Var relVar : rel.getOutputs() ) {

								if (!p.getFoundVars().contains(relVar)) {
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
									relOutputs.clear();
									addVarsToSet( rel.getOutputs(), relOutputs );
									newVars.addAll( relOutputs );
									p.getFoundVars().addAll( relOutputs );
								}
								algorithm.add(rel);
								if (RuntimeProperties.isLogDebugEnabled())
									db.p("algorithm " + algorithm);
							}

							p.getAllRels().remove(rel);
							changed = true;
						}
					}
				}
			}

			if (RuntimeProperties.isLogDebugEnabled())
				db.p("foundvars " + p.getFoundVars());

			p.getKnownVars().addAll(newVars);
			p.getKnownVars().removeAll(removableVars);
			newVars.clear();
		}
		if (RuntimeProperties.isLogDebugEnabled())
			db.p("algorithm " + algorithm);

		if (!computeAll) {
			Optimizer.optimize( algorithm, new HashSet<Var>( allTargetVars ) );
			
			if (RuntimeProperties.isLogDebugEnabled())
				db.p("optimized algorithm " + algorithm);
		}

		if (RuntimeProperties.isLogDebugEnabled())
			db.p("\n---!!!Finished linear planning!!!---\n");
		
		return targetVars.isEmpty() || p.getFoundVars().containsAll( allTargetVars );
	}

	private static int maxDepth = 2;//0..2, i.e. 3
	private static boolean m_isSubtaskRepetitionAllowed = false;
	
	private void subtaskPlanning( Problem problem, ArrayList<Rel> algorithm, boolean computeAll ) {
		
		if (RuntimeProperties.isLogDebugEnabled()) 
			db.p( "!!!--------- Starting Planning With Subtasks ---------!!!" );
		
		int maxDepthBackup = maxDepth;
		
		if( !m_isSubtaskRepetitionAllowed ) {
			maxDepth = problem.getRelsWithSubtasks().size() - 1;
		}
		
		if (RuntimeProperties.isLogDebugEnabled()) 
			db.p( "maxDepth: " + ( maxDepth + 1 ) );
		
		subtaskPlanningImpl( problem, algorithm, new LinkedHashSet<Rel>(), 0, computeAll );
		
		maxDepth = maxDepthBackup;
	}
	
	private void subtaskPlanningImpl(Problem problem, List<Rel> algorithm,
			Set<Rel> subtaskRelsInPath, int depth, boolean computeAll ) {

		Set<Var> newVars = new LinkedHashSet<Var>();
		
		// or
		OR: for (Rel subtaskRel : problem.getRelsWithSubtasks()) {
			if (RuntimeProperties.isLogDebugEnabled())
				db.p( p(depth) + "OR: rel with subtasks - " + subtaskRel.getMethod() + " depth: " + ( depth + 1 ) );
			if( ( subtaskRel.getUnknownInputCount() > 0 ) 
					|| newVars.containsAll( subtaskRel.getOutputs() ) 
					|| problem.getFoundVars().containsAll( subtaskRel.getOutputs() ) ) {
					
				if (RuntimeProperties.isLogDebugEnabled()) {
					db.p( p(depth) + "skipped" );
					if( subtaskRel.getUnknownInputCount() > 0 ) {
						db.p( p(depth) + "because subtaskRel.getUnknownInputCount() > 0 : " + subtaskRel.getUnknownInputCount() 
								+ " " + subtaskRel.printUnknownInputs() );
					} else if( newVars.containsAll( subtaskRel.getOutputs() ) ) {
						db.p( p(depth) + "because all outputs in newVars" );
					} else if( problem.getFoundVars().containsAll( subtaskRel.getOutputs() ) ) {
						db.p( p(depth) + "because all outputs in FoundVars" );
					}
				}
				continue OR;
			}
			
			Set<Rel> newPath = new LinkedHashSet<Rel>();
			
			if( !m_isSubtaskRepetitionAllowed && subtaskRelsInPath.contains(subtaskRel) ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p(depth) + "This rel with subtasks is already in use, path: " + newPath );
				continue;
			} else if( !m_isSubtaskRepetitionAllowed ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p(depth) + "This rel with subtasks can be used, path: " + newPath );
				newPath.addAll( subtaskRelsInPath );
				newPath.add( subtaskRel );
			}
			
			// this is true if all subtasks are solvable
			boolean allSolved = true;
			// and
			AND: for (SubtaskRel subtask : subtaskRel.getSubtasks()) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p(depth) + "AND: subtask - " + subtask );
				
				if( subtask.isIndependent() ) {
					if (RuntimeProperties.isLogDebugEnabled())
						db.p( "Independent!!!" );
					if( subtask.isSolvable() == null ) {
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( "Not solved yet" );
						//independent subtask is solved only once
						Problem context = subtask.getContext();
						ArrayList<Rel> alg = invokePlaning( context, false );
						boolean solved = context.getFoundVars().containsAll( context.getGoals() );
						if( solved ) {
							subtask.setSolvable( Boolean.TRUE );
							subtask.getAlgorithm().addAll( alg );
						} else {
							subtask.setSolvable( Boolean.FALSE );
						}
						allSolved &= solved;
					} else if( subtask.isSolvable() == Boolean.TRUE ) {
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( "Already solved" );
						allSolved &= true;
					} else {
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( "Not solvable" );
						allSolved &= false;
					}
					if (RuntimeProperties.isLogDebugEnabled())
						db.p( "End of independent subtask " + subtask );
				} else {
					// lets clone the environment
					Problem problemNew = problem.getCopy();
					// we need fresh cloned subtask instance
					SubtaskRel subtaskNew = problemNew.getSubtask(subtask);

					prepareSubtask( problemNew, subtaskNew );

					Set<Var> goals = new LinkedHashSet<Var>();
					addVarsToSet( subtaskNew.getOutputs(), goals );

					boolean solved = 
						linearForwardSearch( problemNew, subtaskNew.getAlgorithm(), 
								// never optimize here
								goals, true );

					if( solved ) {
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( p(depth) + "Subtask: " + subtaskNew + " solved" );
						subtask.getAlgorithm().addAll( subtaskNew.getAlgorithm() );
						allSolved &= solved;
						continue AND;
					} else if( !solved && ( depth == maxDepth ) ) {
						if (RuntimeProperties.isLogDebugEnabled())
							db.p( p(depth) + "Subtask: " + subtaskNew + " not solved and cannot go any deeper" );
						continue OR;
					}

					if (RuntimeProperties.isLogDebugEnabled())
						db.p( p(depth) + "Recursing deeper" );

					List<Rel> newAlg = new ArrayList<Rel>( subtaskNew.getAlgorithm() );

					subtaskPlanningImpl( problemNew, newAlg, newPath, depth + 1, false );

					if (RuntimeProperties.isLogDebugEnabled())
						db.p( p(depth) + "Back to depth " + ( depth + 1 ) );

					solved = linearForwardSearch( problemNew, newAlg, 
							// always optimize here in order to get rid of unnecessary subtask instances
							// temporary true while optimization does not work correctly
							goals, true );

					if (RuntimeProperties.isLogDebugEnabled())
						db.p( p(depth) + "Subtask: " + subtaskNew + " solved: " + solved );

					allSolved &= solved;

					// if at least one subtask is not solvable, try another branch
					if( !allSolved ) {
						continue OR;
					}
					// copy algorithm from cloned subtask to old
					subtask.getAlgorithm().addAll( newAlg );
				}
				
			}
			
			if( allSolved ) {
				algorithm.add( subtaskRel );
				
				addVarsToSet( subtaskRel.getOutputs(), newVars );
				
//				if( computeAll && ( depth == 0 ) ) {
//					//if this branch has been solved, no need for another OR
//					break OR;
//				}
//				if( depth == 0 ) {
//					addKnownVarsToSet( subtaskRel.getOutputs(), problem.getFoundVars() );
//				}
				
			} else if( !m_isSubtaskRepetitionAllowed ) {
				if (RuntimeProperties.isLogDebugEnabled())
					db.p( p(depth) + "Subtasks not solved, removing rel from path " + subtaskRel );
				newPath.remove( subtaskRel );
			}
		}
		
		problem.getKnownVars().addAll( newVars );
		problem.getFoundVars().addAll( newVars );
		return;// false;
	}

	private void prepareSubtask(Problem problem, Rel subtask) {
		// [x->y] assume x is known
		Set<Var> flatVars = new LinkedHashSet<Var>();
		addVarsToSet( subtask.getInputs(), flatVars );
		problem.getKnownVars().addAll( flatVars );
		problem.getFoundVars().addAll( flatVars );
	}
	
	
	private String p( int depth ) {
		String s = "\t";
		for( int i = 0; i < depth; i++ ) {
			s += s;
		}
		return s;
	}
	
	public Component getCustomOptionComponent() {
		final JSpinner spinner = new JSpinner( new SpinnerNumberModel( maxDepth + 1, 1, 10, 1 ) );
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
