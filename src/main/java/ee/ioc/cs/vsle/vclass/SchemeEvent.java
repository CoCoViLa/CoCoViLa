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

import javax.swing.JPanel;

public class SchemeEvent {

    /**
     * The object that was involved in current event, if any.
     */
    private GObj object;

    /**
     * The scheme
     */
    private Scheme scheme;

    private JPanel window;
    
    /**
     * @return Returns the window.
     */
    public JPanel getWindow() {
        return window;
    }

    /**
     * @param window The window to set.
     */
    public void setWindow(JPanel window) {
        this.window = window;
    }

    /**
     * @return Returns the object.
     */
    public GObj getObject() {
        return object;
    }

    /**
     * @param object The object to set.
     */
    public void setObject(GObj object) {
        this.object = object;
    }

    /**
     * @return Returns the scheme.
     */
    public Scheme getScheme() {
        return scheme;
    }

    /**
     * @param scheme The scheme to set.
     */
    public void setScheme(Scheme scheme) {
        this.scheme = scheme;
    }
    
}
