/**
 * 
 */
package ee.ioc.cs.vsle.factoryStorage;

import java.util.*;

/**
 * @author pavelg
 *
 */
public abstract class FactoryStorage {

	protected final static Hashtable<String, IFactory> s_storage = new Hashtable<String, IFactory>();
	/**
	 * 
	 */
	public FactoryStorage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void register( IFactory factory ) {
		s_storage.put( factory.getInterfaceInstance(), factory );
	}
	
	protected static List<IFactory> getAllInstances( String prefix ) {
		ArrayList<IFactory> list = new ArrayList<IFactory>();
		
		for( Iterator<String> it = s_storage.keySet().iterator(); it.hasNext(); ) {
			String key = it.next();
			if ( key.startsWith( prefix ) ) {
				list.add( s_storage.get( key ) );
			}
		}
		
		return list;
	}
}
