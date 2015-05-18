package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.vclass.PackageClass;
import ee.ioc.cs.vsle.vclass.VPackage;
import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Pavel Grigorenko
 */
public class PackageSpecSourceProvider extends JavaFileSourceProvider {

  private final VPackage _package;

  public PackageSpecSourceProvider(VPackage _package) {
    super(_package.getDir());
    this._package = _package;
  }

  @Override
  public String getSource(String specificationName) {

    final PackageClass _packageClass = _package.getClass(specificationName);
    String sourceFile;
    if (_packageClass != null && (sourceFile = _packageClass.getInfo().getSource()) != null) {
      final String source = getStringFromFile(_package.getDir() + sourceFile);
      if (source == null) {
        throw new SpecificationNotFoundException("Specification source not found in " + sourceFile);
      }
      return source;
    }

    return super.getSource(specificationName);
  }
}
