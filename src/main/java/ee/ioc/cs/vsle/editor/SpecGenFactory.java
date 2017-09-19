
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

import java.util.*;

import ee.ioc.cs.vsle.factoryStorage.*;

/**
 * @author pavelg
 *
 */
public final class SpecGenFactory extends FactoryStorage<ISpecGenerator> {

	private final static SpecGenFactory s_instance = new SpecGenFactory();

	private SpecGenFactory() {
	  currentInstance = new SpecGenerator.Factory().getInstance();
	}

	public static SpecGenFactory getInstance() {
		return s_instance;
	}
	
	public List<IFactory<ISpecGenerator>> getAllInstances() {
		return getAllInstances( ISpecGenerator.class );
	}
	
	public void setCurrentSpecGen( ISpecGenerator curr ) {
		currentInstance = curr;
	}
	
	public ISpecGenerator getCurrentSpecGen() {
		return currentInstance;
	}
}
