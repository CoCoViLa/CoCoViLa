package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.ClassRelation;
import ee.ioc.cs.vsle.synthesize.RelType;
import ee.ioc.cs.vsle.synthesize.SpecParseException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Grigorenko
 */
public class VariableDeclarationTest {

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
    assertEquals("a= 2", cr.getMethod());
  }

  @Test(expected = SpecParseException.class)
  public void testPrimitiveTypeDeclaration_withExtraComma() {
    String spec = "int a, b,;";
    specificationLoader.loadSpecification(wrapSpec(spec), null);
  }

  @Test
  public void testJavaTypeDeclaration_singleVar() {
    String spec = "Set set;";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "set", "Set");
  }

  @Test
  public void testJavaTypeDeclaration_withAssignment() {
    String spec = "Integer i = new Integer(1);";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "i", "Integer");
  }

  @Test
  public void testJavaTypeDeclaration_fullyQualified() {
    String spec = "java.lang.Double d = 12D;";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "d", "java.lang.Double");
  }

  @Test
  public void testJavaTypeDeclaration_withGenericsAndDiamond() {
    String spec = "List<String> list = new ArrayList<>();";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "list", "List<String>");
  }

  @Test
  public void testStringDeclaration_literalAssignment() {
    String spec = "String s = \"my string\";";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "s", "String");
  }

  @Test
  public void testStringDeclaration_literalAssignment_withNestedQuotes() {
    String spec = "String s = \"hello \\\"WORLD\\\"!\";";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "s", "String");
    ClassRelation cr = ac.getClassRelations().iterator().next();
    assertEquals(RelType.TYPE_EQUATION, cr.getType());
    assertEquals("s", cr.getOutput().getName());
    assertEquals("s = \"hello \\\"WORLD\\\"!\"", cr.getMethod());
  }

  @Test
  @Ignore //FIXME
  public void testStringDeclaration_newInstance() {
    String spec = "String s2 = new String(\"my second string\");";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "s2", "String");
    ClassRelation cr = ac.getClassRelations().iterator().next();
    assertEquals(RelType.TYPE_EQUATION, cr.getType());
    assertEquals("s2", cr.getOutput().getName());
    assertEquals("s2 = new String(\"my second string\")", cr.getMethod());
  }

  @Test
  public void testPrimitiveArrayDeclaration_ints() {
    String spec = "int[] arr = new int[]{1,2,3};";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "arr", "int[]");
    ClassRelation cr = ac.getClassRelations().iterator().next();
    assertEquals(RelType.TYPE_EQUATION, cr.getType());
    assertEquals("arr", cr.getOutput().getName());
    assertEquals("arr = new int[]{1,2,3}", cr.getMethod());
  }

  @Test
  @Ignore //FIXME
  public void testStringArrayDeclaration() {
    String spec = "String[] arr = new String[]{\"1\",\"2\",\"3\"};";
    AnnotatedClass ac = specificationLoader.loadSpecification(wrapSpec(spec), null);
    checkVarAndType(ac, "arr", "String[]");
    ClassRelation cr = ac.getClassRelations().iterator().next();
    assertEquals(RelType.TYPE_EQUATION, cr.getType());
    assertEquals("arr", cr.getOutput().getName());
    assertEquals("arr = new String[]{\"1\",\"2\",\"3\"}", cr.getMethod());
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
