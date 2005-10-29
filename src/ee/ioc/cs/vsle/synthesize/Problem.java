package ee.ioc.cs.vsle.synthesize;

import java.io.*;
import java.util.*;


class Problem implements Cloneable, Serializable {

    private Set<Rel> axioms = new HashSet<Rel>(); 
    private Set<Var> knownVars = new HashSet<Var>(); 
    private Set<Var> targetVars = new HashSet<Var>(); 
    private Map<String, Var> allVars = new HashMap<String, Var>();
    private Set<Rel> allRels = new HashSet<Rel>(); 
    private Set<Rel> subtaskRels = new HashSet<Rel>(); 
    private Set<Rel> subtasks = new HashSet<Rel>(); 
    private Vector<Rel> subGoal = null;

    private HashSet<Var> foundVars = new HashSet<Var>();

    Var getVarByFullName( String field ) {
       // System.err.println( "getVarByField: " + field.toString() );
        for( Iterator it = allVars.values().iterator(); it.hasNext(); ) {
            Var var = (Var)it.next();
            //System.err.println( "var: " + var );

            if( var.toString().equals( field ) ) {
                return var;
            }
        }
        return null;
    }

    Rel getSubtask( Rel subt ) {
        for ( Iterator<Rel> iter = subtasks.iterator(); iter.hasNext(); ) {
            Rel subtask = iter.next();
            if ( subtask.equals( subt ) )
                return subtask;
        }
        return null;
    }

    protected Set<Rel> getSubtasks() {
        return subtasks;
    }

    protected void addSubtask( Rel rel ) {
        subtasks.add( rel );
    }

    protected Map<String, Var> getAllVars() {
        return allVars;
    }

    protected Set<Rel> getAxioms() {
        return axioms;
    }

    protected Set<Var> getKnownVars() {
        return knownVars;
    }

    protected Set<Var> getFoundVars() {
        return foundVars;
    }


    protected Set<Var> getTargetVars() {
        return targetVars;
    }

    protected Set<Rel> getAllRels() {
        return allRels;
    }

    protected Set<Rel> getSubtaskRels() {
        return subtaskRels;
    }

    protected void addAxiom( Rel rel ) {
        axioms.add( rel );
    }

    protected void addKnown( Var var ) {
        knownVars.add( var );
    }

    protected void addKnown( List<Var> vars ) {
        knownVars.addAll( vars );
    }

    protected void addTarget( Var var ) {
        targetVars.add( var );
    }

    protected void addRel( Rel rel ) {
        allRels.add( rel );
    }

    protected void addAllRels( HashSet<Rel> set ) {
        allRels.addAll( set );
    }

    protected void addSubtaskRel( Rel rel ) {
        subtaskRels.add( rel );
    }

    protected void addVar( Var var ) {
        allVars.put( var.getObject() + "." + var.getName(), var );
    }

    protected Problem getCopy() {
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

    public Object clone() {
        try {
            Problem problem = ( Problem )super.clone();

//            HashMap cloneRels = new HashMap();
//            HashMap cloneVars = new HashMap();
//
//            for ( Iterator iter = allRels.iterator(); iter.hasNext(); ) {
//                Rel rel = ( Rel ) iter.next();
//                cloneRels.put( rel, rel.clone() );
//            }
//
//            for ( Iterator iter = allVars.keySet().iterator(); iter.hasNext(); ) {
//                String varObj = ( String ) iter.next();
//                Var var = ( Var ) allVars.get( varObj );
//                cloneVars.put( var, var.clone() );
//            }

            return problem;
        } catch ( CloneNotSupportedException e ) {
            return null;
        }

    }

    public String toString() {
        return ( "All: " + allVars
                 + "\n Rels: " + allRels
                 + "\n Known: " + knownVars
                 + "\n Targets:" + targetVars
                 + "\n Axioms:" + axioms
                 + "\n Subtasks:" + subtaskRels
                 + "\n subGoal:" + subGoal
                 + "\n" );
    }

    public int isSubGoal( Rel comparableRel ) {

        if ( subGoal != null ) {

            for ( int i = 0; i < subGoal.size(); i++ ) {

                Rel rel = subGoal.get(i);

                if ( rel.equals( comparableRel ) ) {

                    return i;
                }
            }
        }

        return -1;
    }

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
}
