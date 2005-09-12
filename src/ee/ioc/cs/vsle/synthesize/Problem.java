package ee.ioc.cs.vsle.synthesize;

import java.io.*;
import java.util.*;


class Problem implements Cloneable, Serializable {

    private Set axioms = new HashSet(); //Collections.synchronizedSet(new HashSet());
    private Set knownVars = new HashSet(); //Collections.synchronizedSet(new HashSet());
    private Set targetVars = new HashSet(); //Collections.synchronizedSet(new HashSet());
    private Map allVars = new HashMap(); //Collections.synchronizedMap(new HashMap());
    private Set allRels = new HashSet(); //Collections.synchronizedSet(new HashSet());
    private Set subtaskRels = new HashSet(); //Collections.synchronizedSet(new HashSet());
    private Set subtasks = new HashSet(); //Collections.synchronizedSet(new HashSet());
    private Vector subGoal = null;

    private HashSet foundVars = new HashSet();

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
        for ( Iterator iter = subtasks.iterator(); iter.hasNext(); ) {
            Rel subtask = ( Rel ) iter.next();
            if ( subtask.equals( subt ) )
                return subtask;
        }
        return null;
    }

    protected Set getSubtasks() {
        return subtasks;
    }

    protected void addSubtask( Rel rel ) {
        subtasks.add( rel );
    }

    protected Map getAllVars() {
        return allVars;
    }

    protected Set getAxioms() {
        return axioms;
    }

    protected Set getKnownVars() {
        return knownVars;
    }

    protected Set getFoundVars() {
        return foundVars;
    }


    protected Set getTargetVars() {
        return targetVars;
    }

    protected Set getAllRels() {
        return allRels;
    }

    protected Set getSubtaskRels() {
        return subtaskRels;
    }

    protected void addAxiom( Rel rel ) {
        axioms.add( rel );
    }

    protected void addKnown( Var var ) {
        knownVars.add( var );
    }

    protected void addKnown( List vars ) {
        knownVars.addAll( vars );
    }

    protected void addTarget( Var var ) {
        targetVars.add( var );
    }

    protected void addRel( Rel rel ) {
        allRels.add( rel );
    }

    protected void addAllRels( HashSet set ) {
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

            HashMap cloneRels = new HashMap();
            HashMap cloneVars = new HashMap();

            for ( Iterator iter = allRels.iterator(); iter.hasNext(); ) {
                Rel rel = ( Rel ) iter.next();
                cloneRels.put( rel, rel.clone() );
            }

            for ( Iterator iter = allVars.keySet().iterator(); iter.hasNext(); ) {
                String varObj = ( String ) iter.next();
                Var var = ( Var ) allVars.get( varObj );
                cloneVars.put( var, var.clone() );
            }

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

                Rel rel = ( Rel ) subGoal.get(i);

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
            ( ( Rel ) subGoal.get( currentDepth - 2 ) ).getAlgorithm().add( rel );
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
        return ( Rel ) subGoal.get( depth - 1 );
    }

    public Rel getSubGoal() {
        return ( Rel ) subGoal.get(currentDepth-1);
    }

    public void setSubGoal( Rel goal, int depth ) {
        if ( depth < 1 )
            throw new IllegalStateException(
                    "Root problem cannot contain subgoal" );

        currentDepth = depth;

        if ( subGoal == null )
            subGoal = new Vector( ( subtasks.size() == 0 ) ? 5 : subtasks.size() );
//        System.err.println("Vector size: " + subGoal.size() + " goal " + subGoal);
        if ( depth - 1 >= subGoal.size() )
            subGoal.add( depth - 1, goal );
        else
            subGoal.set( depth - 1, goal );
//        System.err.println("Vector size2: " + subGoal.size() + " goal " + subGoal);
    }
}
