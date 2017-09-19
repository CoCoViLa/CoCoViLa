/**
 * 
 */
package ee.ioc.cs.vsle.factoryStorage;

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

/**
 * @author pavelg
 *
 */
public abstract class FactoryStorage<T> {

	protected final static Hashtable<Class<?>, List<IFactory<?>>> s_storage = new Hashtable<Class<?>, List<IFactory<?>>>();

	protected T currentInstance;
	
	public static <T> void register( IFactory<T> factory ) {
	  List list;
	  if((list = s_storage.get(factory.getInterfaceClass())) == null ) {
	    list = new ArrayList();
	    s_storage.put( factory.getInterfaceClass(), list );
	  }
	  list.add( factory );
	}
	
	protected static <T> List<IFactory<T>> getAllInstances( Class<T> _class ) {
	  List list = s_storage.get(_class);
	  return (List<IFactory<T>>)list;
	}
}
