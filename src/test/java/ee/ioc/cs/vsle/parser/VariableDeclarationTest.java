package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.RelType;
import ee.ioc.cs.vsle.synthesize.SpecParseException;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Pavel Grigorenko
 */
public class VariableDeclarationTest extends AbstractParserTest {

  @Test
  public void testPrimitiveTypeDeclaration_twoVarsInOneLine() {
    String spec = "int a = 2, b;";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "a", "int");
    checkVarAndType(ac, "b", "int");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "a", "a= 2");
  }

  @Test(expected = SpecParseException.class)
  public void testPrimitiveTypeDeclaration_withExtraComma() {
    String spec = "int a, b,;";
    loadSpec(spec);
  }

  @Test
  public void testJavaTypeDeclaration_singleVar() {
    String spec = "Set set;";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "set", "Set");
  }

  @Test
  public void testJavaTypeDeclaration_withAssignment() {
    String spec = "Integer i = new Integer(1);";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "i", "Integer");
  }

  @Test
  public void testJavaTypeDeclaration_fullyQualified() {
    String spec = "java.lang.Double d = 12D;";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "d", "java.lang.Double");
  }

  @Test
  public void testJavaTypeDeclaration_withGenericsAndDiamond() {
    String spec = "List<String> list = new ArrayList<>();";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "list", "List<String>");
  }

  @Test
  public void testStringDeclaration_literalAssignment() {
    String spec = "String s = \"my string\";";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "s", "String");
  }

  @Test
  public void testStringDeclaration_literalAssignment_withNestedQuotes() {
    String spec = "String s = \"hello \\\"WORLD\\\"!\";";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "s", "String");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "s", "s = \"hello \\\"WORLD\\\"!\"");
  }

  @Test
  @Ignore //FIXME
  public void testStringDeclaration_newInstance() {
    String spec = "String s2 = new String(\"my second string\");";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "s2", "String");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "s2", "s2 = new String(\"my second string\")");
  }

  @Test
  public void testPrimitiveArrayDeclaration_ints() {
    String spec = "int[] arr = new int[]{1,2,3};";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "arr", "int[]");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "arr", "arr = new int[]{1,2,3}");
  }

  @Test
  @Ignore //FIXME
  public void testStringArrayDeclaration() {
    String spec = "String[] arr = new String[]{\"1\",\"2\",\"3\"};";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "arr", "String[]");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "arr", "arr = new String[]{\"1\",\"2\",\"3\"}");
  }

  @Test
  public void testMetaclassDeclaration() {
    specificationSourceProvider.add("Metaclass", wrapSpec( "int a;\n int b = 2;\n" ));
    String spec = "Metaclass mc;";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "mc", "Metaclass");
  }

  @Test
  public void testMetaclassDeclaration_inlineAssignments() {
    specificationSourceProvider.add("Metaclass", wrapSpec( "int a;\n int b = 2;\n" ));
    String spec = "Metaclass a (a=1), b;";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "a", "Metaclass");
    checkVarAndType(ac, "b", "Metaclass");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "a.a", "a.a= 1");
  }

  @Test(expected = SpecParseException.class)
  public void testMetaclassDeclaration_inlineFaultyAssignment() {
    specificationSourceProvider.add("Metaclass", wrapSpec( "int a;\n int b = 2;\n" ));
    String spec = "Metaclass a (a=1, b=), b;";
    loadSpec(spec);
  }

  @Test
  public void testMetaclassDeclaration_inlineAssignmentsWithExpressions() {
    specificationSourceProvider.add("Metaclass", wrapSpec( "int a;\n int b = 2;\n" ));
    String spec = "Metaclass a (a=1+2, b=a*3);";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_EQUATION, "a.a", "a.a= (1 + 2)");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "a.b", "a.b= (a * 3)");
  }

  @Test
  public void testStaticPrimitiveTypeDeclaration_twoVarsInOneLine() {
    String spec = "static int a = 2, b;";
    AnnotatedClass ac = loadSpec(spec);
    ClassField a = checkVarAndType(ac, "a", "int");
    ClassField b = checkVarAndType(ac, "b", "int");
    assertClassRelation(ac, RelType.TYPE_EQUATION, "a", "a= 2");
    assertTrue("Should be static", a.isStatic());
    assertTrue("Should be static", b.isStatic());
  }

  @Test
  public void testConstantPrimitiveTypeDeclaration_withAssignment() {
    String spec = "const double PI = 3.14;";
    AnnotatedClass ac = loadSpec(spec);
    ClassField PI = checkVarAndType(ac, "PI", "double");
    assertTrue("Should be constant", PI.isConstant());
    assertEquals("3.14", PI.getValue());
  }

  @Test(expected = SpecParseException.class)
  public void testConstantPrimitiveTypeDeclaration_noAssignment() {
    String spec = "const double PI;";
    loadSpec(spec);
  }

  @Test
  public void testVoidTypeDeclaration() {
    String spec = "void ready;";
    AnnotatedClass ac = loadSpec(spec);
    checkVarAndType(ac, "ready", "void");
  }

  private ClassField checkVarAndType(AnnotatedClass ac, String var, String type) {
    assertTrue("Variable '" + var + "' not found", ac.hasField(var));
    ClassField classField = ac.getFieldByName(var);
    assertEquals("Type of variable '" + var + "' is incorrect", type, classField.getType());
    return classField;
  }
}
