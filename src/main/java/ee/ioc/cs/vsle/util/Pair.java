package ee.ioc.cs.vsle.util;

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

public class Pair<F, S> {

    private F first;
    private S second;
    
    public Pair( F first, S second ) {
        this.first = first;
        this.second = second;
    }

    /**
     * @return the first
     */
    public F getFirst() {
        return first;
    }

    /**
     * @return the second
     */
    public S getSecond() {
        return second;
    }

    /**
     * @param first the first to set
     */
    public Pair<F, S> setAtFirst( F first ) {
        return new Pair<F, S>( first, second );
    }

    /**
     * @param second the second to set
     */
    public Pair<F, S> setAtSecond( S second ) {
        return new Pair<F, S>( first, second );
    }
    
}
