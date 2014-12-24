package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.*;
import ee.ioc.cs.vsle.vclass.Alias;
import ee.ioc.cs.vsle.vclass.ClassField;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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
}
