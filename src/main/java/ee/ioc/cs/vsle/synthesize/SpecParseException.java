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

import ee.ioc.cs.vsle.api.CoCoViLaRuntimeException;

public class SpecParseException extends CoCoViLaRuntimeException {

    private String metaClass;

    private String line;
    public SpecParseException(String message) {
        super(message);
    }

    SpecParseException(String message, SpecParseException e) {
        super(message, e);
        setLine(e.getLine());
    }

    /**
     * Returns the specification line that caused the exception.
     * @return the specification line
     */
    public String getLine() {
        return line;
    }

    /**
     * Sets the specification line that caused the exception.
     * @param line the specification line that caused the exception
     */
    public void setLine(String line) {
        this.line = line;
    }

    public String getMetaClass() {
        return metaClass;
    }

    public void setMetaClass(String metaClass) {
        this.metaClass = metaClass;
    }
}
