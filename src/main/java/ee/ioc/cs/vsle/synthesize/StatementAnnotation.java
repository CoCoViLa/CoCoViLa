package ee.ioc.cs.vsle.synthesize;

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
