package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassRelation;
import ee.ioc.cs.vsle.synthesize.RelType;
import org.antlr.v4.runtime.CharStream;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Grigorenko
 */
public class SpecificationLoaderTest {

  private SpecificationLoader specificationLoader;

  @Before
  public void init() {
    specificationLoader = new SpecificationLoader(SpecificationSourceProvider.NOP, null);
  }

  @Test
  public void testPrimitiveTypeDeclaration_twoVarsInOneLine() {
    String spec = "int a = 2, b;";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "a", "int");
    checkVarAndType(ac, "b", "int");
    ClassRelation cr = ac.getClassRelations().iterator().next();
    assertEquals(RelType.TYPE_EQUATION, cr.getType());
    assertEquals("a", cr.getOutput().getName());
  }

  @Test
  public void testJavaTypeDeclaration_withGenerics() {
    String spec = "Set set;\n" +
                  "List<String> list;";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "set", "Set");
    checkVarAndType(ac, "list", "List<String>");
  }

  @Test
  public void testStringDeclaration() {
    String spec = "String s = \"my string\";\n" +
                  "String s2 = new String(\"my second string\");";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "s", "String");
    checkVarAndType(ac, "s2", "String");
  }

  private void checkVarAndType(AnnotatedClass ac, String var, String type) {
    assertTrue("Variable '" + var + "' not found", ac.hasField(var));
    assertEquals("Type of variable '" + var + "' is incorrect", type, ac.getFieldByName(var).getType());
  }

  private static String wrapSpec(String spec) {
    return "public class TestSpec {\n" +
            "    /*@ specification TestSpec {\n" +
            "       " + spec + "\n" +
            "    }@*/\n" +
            "}\n";
  }
}
