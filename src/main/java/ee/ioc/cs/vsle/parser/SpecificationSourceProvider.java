package ee.ioc.cs.vsle.parser;

import org.antlr.v4.runtime.CharStream;

/**
 * @author Pavel Grigorenko
 */
public interface SpecificationSourceProvider {

  CharStream getSource(String specificationName);

  SpecificationSourceProvider NOP = new SpecificationSourceProvider() {

    @Override
    public CharStream getSource(String specificationName) {
      throw new SpecificationLoader.SpecificationNotFoundException("Not found: " + specificationName);
    }
  };

}
