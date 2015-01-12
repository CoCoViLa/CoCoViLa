package ee.ioc.cs.vsle.parser;

/**
* @author Pavel Grigorenko
*/
public interface SpecificationSourceProvider<T> {
    T getSource(String spec);
}
