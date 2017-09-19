package ee.ioc.cs.vsle.synthesize;

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

import java.util.LinkedHashMap;
import java.util.Map;

/**
* @author Pavel Grigorenko
*/
public class StatementAnnotation {
  private String name;
  private Map<String, String> valueMap;

  public StatementAnnotation(String name) {
    this.name = name;
  }

  public void putValue(String value) {
    ensureMap();
    valueMap.put("value", value);
  }

  public void putValue(String id, String value) {
    ensureMap();
    valueMap.put(id, value);
  }

  private void ensureMap() {
    if (valueMap == null) {
      valueMap = new LinkedHashMap<String, String>();
    }
  }

  @Override
  public String toString() {
    return "Annotation{" +
            "name='" + name + '\'' +
            ", valueMap=" + valueMap +
            '}';
  }
}
