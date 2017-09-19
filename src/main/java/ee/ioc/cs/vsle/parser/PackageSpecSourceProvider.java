package ee.ioc.cs.vsle.parser;

/*-
 * #%L
 * CoCoViLa
 * %%
 * Copyright (C) 2003 - 2017 Institute of Cybernetics at Tallinn University of Technology
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
