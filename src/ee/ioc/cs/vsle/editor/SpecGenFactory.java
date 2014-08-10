
package ee.ioc.cs.vsle.editor;

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
