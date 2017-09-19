/**
 * 
 */
package ee.ioc.cs.vsle.table.exception;

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
 * @author pavelg
 *
 */
public class TableInputConstraintViolationException extends TableException {

    /**
     * @param message
     */
    public TableInputConstraintViolationException( String message ) {
        super( message );
    }

    /**
     * @param cause
     */
    public TableInputConstraintViolationException( Throwable cause ) {
        super( cause );
    }

    /**
     * @param message
     * @param cause
     */
    public TableInputConstraintViolationException( String message,
            Throwable cause ) {
        super( message, cause );
    }

}
