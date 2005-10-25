/**
 * 
 */
package ee.ioc.cs.vsle.factoryStorage;

/**
 * @author pavelg
 *
 */
public interface IFactory {
	
	public String getInterfaceInstance();
	
	public Object getInstance();
	
	public String getDescription();
}
