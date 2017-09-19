package ee.ioc.cs.vsle.api;

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

import java.util.List;

/**
 * The interface of ports of scheme objects that is exposed to the
 * generated programs through the {@code ProgramContext} class.
 * @see ee.ioc.cs.vsle.api.ProgramContext
 * @see ee.ioc.cs.vsle.api.Scheme
 */
public interface Port {

    /**
     * Returns the name of the port.
     * @return name of the port
     */
    public String getName();

    /**
     * Returns the type of the port.
     * @return the type of the port
     */
    public String getType();

    /**
     * Returns the list of connections connected to this port.
     * @return the list of connections
     */
    public List<Connection> getConnections();

    /**
     * Returns the object the port belongs to.
     * @return the object the port belongs to
     */
    public SchemeObject getObject();
}
