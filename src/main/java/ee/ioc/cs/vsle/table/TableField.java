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

/**
 * Class for table variables
 */
public class TableField {

    private String id;
    private String type;
    private Object defaultValue;
    
    public TableField( String id, String type ) {
        this.id = id;
        this.type = type;
    }
    
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param f
     * @return
     */
    public boolean equalsByID( TableField f ) {
        return id.equals( f.id );
    }
    
    @Override
    public boolean equals( Object obj ) {
        if( obj == null ) return false;
        
        return id.equals( ((TableField)obj).id ) && type.equals( ((TableField)obj).type );
    }

    private int lazyHash = -1;
    
    @Override
    public int hashCode() {
        if( lazyHash == -1 )
            return lazyHash = id.concat( type ).hashCode();
        
        return lazyHash;
    }

    @Override
    public String toString() {
        return "TableField: id= " + id + ", type=" + type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValueFromString( String value ) {
        defaultValue = Table.createDataObjectFromString( type, value );
    }
}
