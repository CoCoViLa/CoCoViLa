package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.*;
import org.junit.Ignore;
import org.junit.Test;

/**
* @author Pavel Grigorenko
*/
public class AliasTest extends AbstractParserTest {

  @Test
  public void testAlias_oneVar() {
    String spec = "int a;\n " +
            "alias x = (a);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a"));
  }

  @Test
  public void testAlias_moreVars() {
    String spec = "int a, b, c;\n " +
            "alias x = (a, b, c);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a", "b", "c"));
  }

  @Test
  @Ignore("redefining alias should not be allowed")//FIXME
  /*
int a = 0, b = 1, c = 2;
void r;
alias x = (a, b);
x = (c);
x->r{test};

gives:
Object[] alias_x_0 = new Object[3];
alias_x_0[0] = a;
alias_x_0[1] = b;
alias_x_0[2] = c;
test(alias_x_0);
   */
  public void testAlias_redefine() {
    String spec = "int a, b, c;\n " +
            "alias x = (a, b);" +
            "x = (c);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a", "b", "c"));
  }

  @Test
  public void testAlias_moreVarsWithType() {
    String spec = "String a, b, c;\n " +
            "alias (String) x = (a, b, c);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a", "b", "c"), "String");
  }

  @Test(expected = AliasException.class)
  public void testAlias_badType() {
    String spec = "String a, b;" +
                  "int c;\n " +
                  "alias (String) x = (a, b, c);";
    loadSpec(spec);
  }

  @Test(expected = SpecParseException.class)//FIXME needs better error message
  public void testAlias_noVars() {
    String spec = "alias x = ();";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars());
  }

  @Test
  public void testAlias_empty() {
    String spec = "alias x;";
    AnnotatedClass ac = loadSpec(spec);
    assertAliasType(ac, "x", "Object");
  }

  @Test
  public void testAlias_emptyWithType() {
    String spec = "alias (int) x;";
    AnnotatedClass ac = loadSpec(spec);
    assertAliasType(ac, "x", "int");
  }

  @Test
  public void testAlias_twoLineDecl() {
    String spec = "int a, b, c;\n " +
            "alias x;" +
            "x = (a, b, c);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a", "b", "c"));
  }

  @Test
  public void testAlias_twoLineDeclWithType() {
    String spec = "double a, b, c;\n " +
            "alias (double) x;" +
            "x = (a, b, c);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a", "b", "c"), "double");
  }

  @Test
  public void testAlias_twoLineDeclOldSyntax() {
    String spec = "double a, b, c;\n " +
            "alias (double) x;" +
            "x = [a, b, c];";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a", "b", "c"), "double");
  }

  @Test(expected = AliasException.class)
  public void testAlias_twoLineDeclWithBadType() {
    String spec = "double a, b;" +
            "String c;\n " +
            "alias (double) x;" +
            "x = (a, b, c);";
    loadSpec(spec);
  }

  @Test
  public void testAlias_twoLineDeclMixedTypes() {
    String spec = "double a, b;" +
            "String c;\n " +
            "alias x;" +
            "x = (a, b, c);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("a", "b", "c"), "Object");
  }

  @Test
  public void testAlias_wildcard() {
    String spec = "alias x = (*.a);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("*.a"), "Object", true);
  }

  @Test
  public void testAlias_wildcardTwoLineDecl() {
    String spec = "alias (String) x;" +
            "x = (*.a);";
    AnnotatedClass ac = loadSpec(spec);
    assertAlias(ac, "x", vars("*.a"), "String", true);
  }

  @Test(expected = SpecParseException.class)
  public void testAlias_twoWildcards() {
    String spec = "alias x = (*.a, *.b);";
    loadSpec(spec);
  }

  @Test
  public void testAlias_elemAccess() {
    String spec = "int a, b, c, d;\n" +
            "alias x = (a, b);\n" +
            "alias y = (c, d);\n" +
            "alias z = (x, y);\n" +
            "z.0 = z.1;";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_EQUATION, vars("z.0"), vars("z.1"), "z.1= z.0");
    assertClassRelation(ac, RelType.TYPE_EQUATION, vars("z.1"), vars("z.0"), "z.0= z.1");
  }

  @Test
  @Ignore("works with regexp parser")//FIXME
  public void testAlias_elemAccessWildcardIndices() {
    String spec = "int a = 1, b, c = 2, d;\n" +
            "alias x = (a, b);\n" +
            "alias y = (c, d);\n" +
            "alias z = (x, y);\n" +
            "z.*.0 -> z.*.1 {test};";//(a, c) -> (b, d)
    System.out.println(spec);
    AnnotatedClass ac = loadSpec(spec);
  }

  @Test
  @Ignore("works with regexp parser")//FIXME
  public void testAlias_elemAccessWildcardVarNames() {
    specificationSourceProvider.add("M", wrapSpec("int u, v;", "M"));
    String spec = "M a, b;\n" +
            "alias x = (a, b);\n" +
            "a.u = 1;\n" +
            "b.u = 2;\n" +
            "x.*.u -> x.*.v {test};";//(a.u, b.u) -> (a.v, b.v)
    System.out.println(spec);
    AnnotatedClass ac = loadSpec(spec);
  }
}
