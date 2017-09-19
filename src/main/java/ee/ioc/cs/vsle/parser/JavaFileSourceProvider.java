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
