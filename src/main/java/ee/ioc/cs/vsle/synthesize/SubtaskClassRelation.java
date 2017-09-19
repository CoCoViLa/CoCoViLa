/**
 * 
 */
package ee.ioc.cs.vsle.synthesize;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import ee.ioc.cs.vsle.vclass.ClassField;

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
	
	public static SubtaskClassRelation createIndependentSubtask( String specLine, ClassField context ) {
		if( context == null ) {
			throw new IllegalStateException("Independent subtasks must have context!!!");
		}
		return new SubtaskClassRelation( specLine, context );
	}
	
	public static SubtaskClassRelation createDependentSubtask( String specLine ) {
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
                String res = context.getName();

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
