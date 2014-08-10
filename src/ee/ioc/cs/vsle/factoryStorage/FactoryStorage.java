/**
 * 
 */
package ee.ioc.cs.vsle.factoryStorage;

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
