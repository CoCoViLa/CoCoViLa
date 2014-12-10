package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassRelation;
import ee.ioc.cs.vsle.synthesize.RelType;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.junit.Before;

import java.util.HashMap;

import static org.junit.Assert.fail;

/**
 * @author Pavel Grigorenko
 */
public abstract class AbstractParserTest {

  protected SpecificationLoader specificationLoader;
  protected TestSpecProvider specificationSourceProvider;

  @Before
  public void init() {
    specificationSourceProvider = new TestSpecProvider();
    specificationLoader = new SpecificationLoader(specificationSourceProvider, null);
  }

  AnnotatedClass loadSpec(String spec) {
    return specificationLoader.loadSpecification(wrapSpec(spec), null);
  }

  static String wrapSpec(String spec) {
    return wrapSpec(spec, "TestSpec");
  }

  static String wrapSpec(String spec, String metaClassName) {
    return "public class " + metaClassName + " {\n" +
            "    /*@ specification " + metaClassName + " {\n" +
            "       " + spec + "\n" +
            "    }@*/\n" +
            "}\n";
  }

  static void assertClassRelation(AnnotatedClass ac, RelType rt, String outputName, String method) {
    for(ClassRelation cr : ac.getClassRelations()) {
      if(rt == cr.getType() && outputName.equals(cr.getOutput().getName()) && method.equals(cr.getMethod())) {
        return;//found
      }
    }
    fail("Class relation not found");
  }

  static void assertClassRelation(AnnotatedClass ac, RelType rt, String[] inputs, String[] outputs, String method) {
    outer:
    for(ClassRelation cr : ac.getClassRelations()) {
      if(rt != cr.getType()) { continue; }
      if(!method.equals(cr.getMethod())) { continue; }
      if(inputs.length != cr.getInputs().size()) { continue; }
      int i = 0;
      for(ClassField cf : cr.getInputs()) {
        if(!cf.getName().equals(inputs[i++])) { continue outer; }
      }
      if(outputs.length != cr.getOutputs().size()) { continue; }
      i = 0;
      for(ClassField cf : cr.getOutputs()) {
        if(!cf.getName().equals(outputs[i++])) { continue outer; }
      }
      return;//found
    }
    fail("Class relation not found");
  }

  static class TestSpecProvider implements SpecificationSourceProvider {

    private HashMap<String, String> map = new HashMap<String, String>();

    public void add(String className, String spec) {
      map.put(className, wrapSpec(spec));
    }

    @Override
    public CharStream getSource(String specificationName) {
      if(map.containsKey(specificationName)) {
        return new ANTLRInputStream(map.get(specificationName));
      }
      return SpecificationSourceProvider.NOP.getSource(specificationName);
    }
  }
}
