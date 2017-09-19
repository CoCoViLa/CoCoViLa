/**
 * 
 */
package ee.ioc.cs.vsle.table;

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

import java.util.*;

import ee.ioc.cs.vsle.table.exception.*;

/**
 * @author pavelg
 *
 * This list does not allow duplicate additions (by the id)
 */
public class TableFieldList <T extends TableField> extends ArrayList<T> {

    @Override
    public void add( int index, T element ) {
        checkField( element );
        super.add( index, element );
    }

    @Override
    public boolean add( T element ) {
        checkField( element );
        return super.add( element );
    }

    @Override
    public boolean addAll( Collection<? extends T> c ) {
        checkAllFields( c );
        return super.addAll( c );
    }

    @Override
    public boolean addAll( int index, Collection<? extends T> c ) {
        checkAllFields( c );
        return super.addAll( index, c );
    }

    private void checkAllFields( Collection<? extends T> c ) {
        
        for ( TableField tableField : c ) {
            checkField( tableField );
        }
    }
    
    private void checkField( TableField f ) {
        if( containsByID( f ) ) {
            throw new TableException( "Fields with the same ID are not allowed!\n" + f + "\nList: " + toString() );
        }
    }
    
    public boolean containsByID( TableField f ) {
        
        return getFieldByID( f.getId() ) != null;
    }
    
    public T getFieldByID( String id ) {
        
        for ( T field : this ) {
            if( field.getId().equals( id ) ) {
                return field;
            }
        }
        
        return null;
    }
}
