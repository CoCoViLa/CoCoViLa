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

	private ClassField context;
	
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
}
