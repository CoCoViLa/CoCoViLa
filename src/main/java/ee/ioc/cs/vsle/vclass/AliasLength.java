/**
 * 
 */
package ee.ioc.cs.vsle.vclass;

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

import ee.ioc.cs.vsle.util.*;

/**
 * @author pavelg
 *
 */
public class AliasLength extends ClassField {
    
    private Alias alias;
    //class AnnotatedClass is not serializable
    private String annotatedClassName;
    
    public AliasLength(Alias alias, String annotatedClassName) {
        super(alias.getName() + "_LENGTH", TypeUtil.TYPE_INT);
        
        this.annotatedClassName = annotatedClassName;
        this.alias = alias;
    }

    @Override
    public boolean isAliasLength() {
        return true;
    }

    /**
     * @return the alias
     */
    public Alias getAlias() {
        return alias;
    }

    /**
     * @return the ac
     */
    public String getAnnotatedClass() {
        return annotatedClassName;
    }

}
