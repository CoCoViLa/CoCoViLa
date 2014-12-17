/**
 * 
 */
package ee.ioc.cs.vsle.factoryStorage;

/**
 * @author pavelg
 *
 */
public interface IFactory<T> {
	
  public Class<? super T> getInterfaceClass();
  
	public T getInstance();
	
	public String getDescription();
}
