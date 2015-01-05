package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author Pavel Grigorenko
 */
@RunWith(Parameterized.class)
public abstract class AbstractParserTest {

  @Parameterized.Parameter(value = 0)
  public TestSpecLoader specificationLoader;
  @Parameterized.Parameter(value = 1)
  public TestSpecStorage specificationSourceProvider;
  @Parameterized.Parameter(value = 2)
  public String testRunId;

  @Parameterized.Parameters(name = "{2}")
  public static Collection<Object[]> data() {
    AntlrSpecProvider antlrSpecProvider = new AntlrSpecProvider();
    StringSpecProvider stringSpecProvider = new StringSpecProvider();

    return Arrays.asList(new Object[][]{
            { new AntlrTestSpecLoader(new SpecificationLoader(antlrSpecProvider, null)), antlrSpecProvider, "ANTLR parser" },
            { new RegexParserTestSpecLoader(new SpecParser(null, stringSpecProvider)), stringSpecProvider, "REGEX parser" }});
  }

  @Before
  public void init() {
//    specificationSourceProvider = new TestSpecProvider();
//    specificationLoader = new SpecificationLoader(specificationSourceProvider, null);
  }

  AnnotatedClass loadSpec(String spec) {
    return specificationLoader.loadSpec(wrapSpec(spec));
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

  static ClassRelation assertClassRelation(AnnotatedClass ac, RelType rt, String[] inputs, String[] outputs, String method) {
    return assertClassRelation(ac, rt, inputs, outputs, new String[0], method);
  }

  static ClassRelation assertClassRelation(AnnotatedClass ac, RelType rt, String[] inputs, String[] outputs, String[] exceptions, String method) {
    for(ClassRelation cr : ac.getClassRelations()) {
      if(!checkClassRelation(cr, rt, inputs, outputs, exceptions, method)) {
        continue;
      }
      return cr;
    }
    fail("Class relation not found");
    return null;
  }

  static void assertClassRelation(ClassRelation cr, RelType rt, String[] inputs, String[] outputs, String method) {
    assertClassRelation(cr, rt, inputs, outputs, new String[0], method);
  }

  static void assertClassRelation(ClassRelation cr, RelType rt, String[] inputs, String[] outputs, String[] exceptions, String method) {
    if(!checkClassRelation(cr, rt, inputs, outputs, exceptions, method)) {
      fail("Class relation does not match");
    }
  }

  static boolean checkClassRelation(ClassRelation cr, RelType rt, String[] inputs, String[] outputs, String[] exceptions, String method) {
    if(rt != cr.getType()) { return false; }
    if(method != null && !method.equals(cr.getMethod())) { return false; }
    else if(method == null && cr.getMethod() != null) { return false; }
    if(inputs.length != cr.getInputs().size()) { return false; }
    int i = 0;
    for(ClassField cf : cr.getInputs()) {
      if(!cf.getName().equals(inputs[i++])) { return false; }
    }
    if(outputs.length != cr.getOutputs().size()) { return false; }
    i = 0;
    for(ClassField cf : cr.getOutputs()) {
      if(!cf.getName().equals(outputs[i++])) { return false; }
    }
    if(exceptions.length != cr.getExceptions().size()) { return false; }
    i = 0;
    for(ClassField cf : cr.getExceptions()) {
      if(!cf.getName().equals(exceptions[i++])) { return false; }
    }
    return true;
  }

  protected void assertAlias(AnnotatedClass ac, String aliasName, String[] boundVars) {
    assertAlias(ac, aliasName, boundVars, "Object");
  }

  protected void assertAlias(AnnotatedClass ac, String aliasName, String[] boundVars, String aliasType) {
    assertAlias(ac, aliasName, boundVars, aliasType, false);
  }

  protected void assertAlias(AnnotatedClass ac, String aliasName, String[] boundVars, String aliasType, boolean isWildcard) {
    assertNotNull("Alias type cannot be null", aliasType);
    assertAliasType(ac, aliasName, aliasType);
    assertClassRelation(ac, RelType.TYPE_ALIAS, boundVars, vars(aliasName), "alias");
    if(!isWildcard) {
      assertClassRelation(ac, RelType.TYPE_ALIAS, vars(aliasName), boundVars, "alias");
    }
  }

  protected void assertAliasType(AnnotatedClass ac, String aliasName, String aliasType) {
    Alias alias = (Alias)ac.getFieldByName(aliasName);
    assertEquals("alias var type is incorrect", aliasType, alias.getVarType());
  }

  static String[] vars(String... vars) {
    return vars;
  }

  static class AntlrSpecProvider extends TestSpecStorage implements AntlrSpecificationSourceProvider {

    @Override
    public CharStream getSource(String specificationName) {
      if(map.containsKey(specificationName)) {
        return new ANTLRInputStream(map.get(specificationName));
      }
      return AntlrSpecificationSourceProvider.NOP.getSource(specificationName);
    }
  }

  static class StringSpecProvider extends TestSpecStorage implements SpecificationSourceProvider<String> {

    @Override
    public String getSource(String spec) {
      return null;
    }
  }

  static class TestSpecStorage {
    protected HashMap<String, String> map = new HashMap<String, String>();

    public void add(String className, String spec) {
      map.put(className, wrapSpec(spec));
    }
  }

  interface TestSpecLoader {
    AnnotatedClass loadSpec(String spec);
  }

  static class AntlrTestSpecLoader implements TestSpecLoader {

    private final SpecificationLoader specificationLoader;

    AntlrTestSpecLoader(SpecificationLoader specificationLoader) {

      this.specificationLoader = specificationLoader;
    }

    @Override
    public AnnotatedClass loadSpec(String spec) {
      return specificationLoader.loadSpecification(wrapSpec(spec), null);
    }
  }

  static class RegexParserTestSpecLoader implements TestSpecLoader {

    private final SpecParser specParser;

    public RegexParserTestSpecLoader(SpecParser specParser) {
      this.specParser = specParser;
    }

    @Override
    public AnnotatedClass loadSpec(String spec) {
      try {
        return specParser.parseSpecification(spec, null, null).iterator().next();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
