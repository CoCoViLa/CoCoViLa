package ee.ioc.cs.vsle.parser;

import org.antlr.v4.runtime.CharStream;

/**
 * @author Pavel Grigorenko
 */
public interface AntlrSpecificationSourceProvider extends SpecificationSourceProvider<CharStream> {

  CharStream getSource(String specificationName);

  AntlrSpecificationSourceProvider NOP = new AntlrSpecificationSourceProvider() {

    @Override
    public CharStream getSource(String specificationName) {
      throw new SpecificationLoader.SpecificationNotFoundException("Not found: " + specificationName);
    }
  };

}
