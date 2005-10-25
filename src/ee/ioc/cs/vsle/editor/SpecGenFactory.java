
package ee.ioc.cs.vsle.editor;

import java.util.*;

import ee.ioc.cs.vsle.factoryStorage.*;

/**
 * @author pavelg
 *
 */
public final class SpecGenFactory extends FactoryStorage {

	public final static String s_prefix = "\\SPECGEN";
	
	//by default
	private ISpecGenerator m_currentInstance = (ISpecGenerator)new SpecGenerator.Factory().getInstance();
	
	private final static SpecGenFactory s_instance = new SpecGenFactory();

	private SpecGenFactory() {}

	public static SpecGenFactory getInstance() {
		return s_instance;
	}
	
	public static List<IFactory> getAllInstances() {
		return getAllInstances( s_prefix );
	}
	
	public void setCurrentSpecGen( ISpecGenerator curr ) {
		m_currentInstance = curr;
	}
	
	public ISpecGenerator getCurrentSpecGen() {
		return m_currentInstance;
	}
}
