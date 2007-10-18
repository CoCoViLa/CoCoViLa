/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

import ee.ioc.cs.vsle.vclass.*;

/**
 * @author pavelg
 *
 */
public class SubtaskClassRelation extends ClassRelation {

	/**
     * 
     */
    private static final long serialVersionUID = -7008909209999088790L;

    private ClassField context;
	
	private int lazyHash = -1;
	
	/**
	 * @param specLine
	 */
	private SubtaskClassRelation( String specLine, ClassField context ) {
		super(RelType.TYPE_SUBTASK, specLine);
		this.context = context;
	}

	public boolean isIndependent() {
		return context != null;
	}

	public ClassField getContext() {
		if(!isIndependent()) {
			throw new IllegalStateException("Only independent subtasks have context!!!");
		}
		return context;
	}
	
	static SubtaskClassRelation createIndependentSubtask( String specLine, ClassField context ) {
		if( context == null ) {
			throw new IllegalStateException("Independent subtasks must have context!!!");
		}
		return new SubtaskClassRelation( specLine, context );
	}
	
	static SubtaskClassRelation createDependentSubtask( String specLine ) {
		return new SubtaskClassRelation( specLine, null );
	}

    @Override
    public boolean equals( Object obj ) {
        
        if( isIndependent() ) {
            return this.hashCode() == obj.hashCode();
        }
        
        return super.equals( obj );
    }

    @Override
    public int hashCode() {
        
        if( isIndependent() ) {
            if( lazyHash == -1 ) {
                String res = "";

                if( isIndependent() ) {
                    res += context.getName();
                }

                for( ClassField in : inputs ) {
                    res += in.getName();
                }

                for( ClassField out : outputs ) {
                    res += out.getName();
                }

                lazyHash = res.hashCode();
            }

            return lazyHash;
        }
        
        return super.hashCode();
    }
}
