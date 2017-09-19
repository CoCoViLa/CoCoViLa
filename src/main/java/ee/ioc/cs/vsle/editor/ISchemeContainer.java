package ee.ioc.cs.vsle.editor;

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

import ee.ioc.cs.vsle.vclass.*;

/**
 * @author pavelg
 * 
 * Interface used by ProgramRunner
 */
public interface ISchemeContainer {

    /**
     * Returns a loaded package
     * 
     * @return
     */
    public VPackage getPackage();
    
    /**
     * Registers runner id
     * 
     * @param id
     */
    public void registerRunner( long id );
    
    /**
     * Unregisters runner id
     * 
     * @param id
     */
    public void unregisterRunner( long id );
    
    /**
     * Returns working folder of a package
     * 
     * @return
     */
    String getWorkDir();
    
    /**
     * Returns the reference of the internal object list
     * 
     * @return object list
     */
    ObjectList getObjectList();

    /**
     * Returns a scheme
     * 
     * @return
     */
    Scheme getScheme();
    
    String getSchemeName();
    
    /**
     * Calls repaint
     */
    public void repaint();
}
