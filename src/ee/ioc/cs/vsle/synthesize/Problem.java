package ee.ioc.cs.vsle.synthesize;

import java.io.*;
import java.util.*;


class Problem implements Serializable {

    private Set<Rel> axioms = new HashSet<Rel>(); 
    private Set<Var> knownVars = new HashSet<Var>(); 
    private Set<Var> targetVars = new HashSet<Var>(); 
    private Map<String, Var> allVars = new HashMap<String, Var>();
    private Set<Rel> allRels = new HashSet<Rel>(); 
    private Set<Rel> relWithSubtasks = new HashSet<Rel>(); 
    private Set<Rel> subtasks = new HashSet<Rel>(); 
    private Vector<Rel> subGoal = null;

    private HashSet<Var> foundVars = new HashSet<Var>();

    Var getVarByFullName( String field ) {
        for( Iterator it = allVars.values().iterator(); it.hasNext(); ) {
            Var var = (Var)it.next();

            if( var.toString().equals( field ) ) {
                return var;
            }
        }
        return null;
    }

    Set<Rel> getSubtasks() {
        return subtasks;
    }

    void addSubtask( Rel rel ) {
        subtasks.add( rel );
    }

    Map<String, Var> getAllVars() {
        return allVars;
    }

    Set<Rel> getAxioms() {
        return axioms;
    }

    Set<Var> getKnownVars() {
        return knownVars;
    }

    Set<Var> getFoundVars() {
        return foundVars;
    }


    Set<Var> getTargetVars() {
        return targetVars;
    }

    Set<Rel> getAllRels() {
        return allRels;
    }

    Set<Rel> getRelsWithSubtasks() {
        return relWithSubtasks;
    }

    void addAxiom( Rel rel ) {
        axioms.add( rel );
    }

     void addKnown( Var var ) {
        knownVars.add( var );
    }

     void addKnown( List<Var> vars ) {
        knownVars.addAll( vars );
    }

     void addTarget( Var var ) {
        targetVars.add( var );
    }

     void addRel( Rel rel ) {
        allRels.add( rel );
    }

     void addAllRels( HashSet<Rel> set ) {
        allRels.addAll( set );
    }

     void addRelWithSubtask( Rel rel ) {
        relWithSubtasks.add( rel );
    }

     void addVar( Var var ) {
        allVars.put( var.getObject() + "." + var.getName(), var );
    }

     Problem getCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream( bos );
            oos.writeObject( this );
            oos.flush();

            ByteArrayInputStream bis = new ByteArrayInputStream( bos.toByteArray() );

            oos.close();

            ObjectInputStream ois = new ObjectInputStream( bis );

            Problem problem = ( Problem ) ois.readObject();

            ois.close();

            return problem;

        } catch ( Exception e ) {
            return null;
        }
    }

    public String toString() {
        return ( "All: " + allVars
                 + "\n Rels: " + allRels
                 + "\n Known: " + knownVars
                 + "\n Targets:" + targetVars
                 + "\n Axioms:" + axioms
                 + "\n Subtasks:" + relWithSubtasks
                 + "\n subGoal:" + subGoal
                 + "\n" );
    }

//    public int isSubGoal( Rel comparableRel ) {
//
//        if ( subGoal != null ) {
//
//            for ( int i = 0; i < subGoal.size(); i++ ) {
//
//                Rel rel = subGoal.get(i);
//
//                if ( rel.equals( comparableRel ) ) {
//
//                    return i;
//                }
//            }
//        }
//
//        return -1;
//    }

    int currentDepth = 0;

    public void addToAlgorithm( Rel rel ) {
        if ( currentDepth > 1 ) {
            subGoal.get( currentDepth - 2 ).getAlgorithm().add( rel );
        } else {
//            algorithm.add( rel );
            throw new IllegalStateException(
				"Wrong Algorithm");
        }
    }

//    public boolean decreaseCurrentDepth() {
//        if( currentDepth > 1 ) {
//            subGoal.removeElementAt( subGoal.size() - 1 );
//            currentDepth--;
//        }
//        else
//            return false;
//
//        return currentDepth == 0;
//    }

    public Rel getSubGoal( int depth ) {
        return subGoal.get( depth - 1 );
    }

    public Rel getSubGoal() {
        return subGoal.get(currentDepth-1);
    }

    public void setSubGoal( Rel goal, int depth ) {
        if ( depth < 1 )
            throw new IllegalStateException(
                    "Root problem cannot contain subgoal" );

        currentDepth = depth;

        if ( subGoal == null )
            subGoal = new Vector<Rel>( ( subtasks.size() == 0 ) ? 5 : subtasks.size() );
//        System.err.println("Vector size: " + subGoal.size() + " goal " + subGoal);
        if ( depth - 1 >= subGoal.size() )
            subGoal.add( depth - 1, goal );
        else
            subGoal.set( depth - 1, goal );
//        System.err.println("Vector size2: " + subGoal.size() + " goal " + subGoal);
    }
    
    Rel getSubtask( Rel subt ) {
        for ( Iterator<Rel> iter = subtasks.iterator(); iter.hasNext(); ) {
            Rel subtask = iter.next();
            if ( subtask.equals( subt ) )
                return subtask;
        }
        return null;
    }
}
