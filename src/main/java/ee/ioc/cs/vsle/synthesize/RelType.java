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

public enum RelType {

            TYPE_DECLARATION,
            TYPE_JAVAMETHOD,
            TYPE_EQUATION,
            TYPE_ALIAS,
            TYPE_SUBTASK,
            TYPE_METHOD_WITH_SUBTASK,
            TYPE_UNIMPLEMENTED;

    private static int auxVarCounter = 0;
    private static int relCounter = 0;
    private static int varCounter = 0;

    final static String TAG_SUBTASK = "<<subtask>>";

    public final static int REL_HASH = "rel".hashCode();
    public final static int VAR_HASH = "var".hashCode();

    public static int tmpVarNr() {
        return auxVarCounter;
    }
    
    public static int nextTmpVarNr() {
        return auxVarCounter++;
    }
    
    public static int nextRelNr() {
        return relCounter++;
    }
    
    public static int nextVarNr() {
        return varCounter++;
    }
}
