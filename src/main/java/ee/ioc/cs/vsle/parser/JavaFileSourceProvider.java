package ee.ioc.cs.vsle.parser;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author Pavel Grigorenko
 */
public class JavaFileSourceProvider implements SpecificationSourceProvider {

  private static final Logger logger = LoggerFactory.getLogger(JavaFileSourceProvider.class);

  private final String basePath;

  public JavaFileSourceProvider(String basePath) {
    this.basePath = basePath;
  }

  @Override
  public String getSource(String specificationName) {
    String pathname = basePath + specificationName + ".java";
    return getStringFromFile(pathname);
  }

  protected String getStringFromFile(String pathname) {
    File file = new File(pathname);
    try {
      return FileUtils.readFileToString(file);
    }
    catch(Exception e) {
      throw new SpecificationNotFoundException("Specification source not found in " + pathname);
    }
  }
}
