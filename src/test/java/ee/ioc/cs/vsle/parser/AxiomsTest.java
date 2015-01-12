package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * @author Pavel Grigorenko
 */
public class AxiomsTest extends AbstractParserTest {

  @Test
  public void testSimpleAxiom() {
    String spec = "int a, b;\n a -> b {m};";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_JAVAMETHOD, vars("a"), vars("b"), "m");
  }

  @Test
  public void testSimpleAxiom_withControlVars() {
    String spec = "int a, b;\n" +
            "void init, done;\n" +
            "init, a -> b, done {m};";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_JAVAMETHOD, vars("init", "a"), vars("b", "done"), "m");
  }

  @Test
  public void testSimpleAxiom_withMetaclassVars() {
    specificationSourceProvider.add("M1", wrapSpec( "int x;\n M2 y;\n" ));
    specificationSourceProvider.add("M2", wrapSpec( "int z;" ));
    String spec = "M1 a, b, c;\n" +
            "a,b.x -> c.y.z {m};";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_JAVAMETHOD, vars("a", "b.x"), vars("c.y.z"), "m");
  }

  @Test
  public void testSimpleAxiom_NoInput() {
    String spec = "int c;\n -> c {m};";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_JAVAMETHOD, vars(), vars("c"), "m");
  }

  @Test(expected = SpecParseException.class)
  public void testSimpleAxiom_NoOutput() {
    String spec = "int c;\n c -> {m};";
    loadSpec(spec);
  }

  @Test
  public void testSimpleAxiom_withExceptions() {
    String spec = "int a, b;\n a -> b,(Exception), (RuntimeException) {m};";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_JAVAMETHOD, vars("a"), vars("b"), vars("Exception", "RuntimeException"), "m");
  }

  @Test
  public void testAxiom_withSubtask() {
    String spec = "int a, b, x, y;\n [x -> y], a -> b {m};";
    AnnotatedClass ac = loadSpec(spec);
    ClassRelation cr = assertClassRelation(ac, RelType.TYPE_METHOD_WITH_SUBTASK, vars("a"), vars("b"), "m");
    assertEquals(1, cr.getSubtasks().size());
    SubtaskClassRelation subtask = cr.getSubtasks().iterator().next();
    assertClassRelation(subtask, RelType.TYPE_SUBTASK, vars("x"), vars("y"), null);
    assertFalse("Should not be independent", subtask.isIndependent());
  }

  @Test
  public void testAxiom_withSubtaskMoreVars() {
    specificationSourceProvider.add("M", wrapSpec( "int n, m, k, l;\n" ));
    String spec = "M a, b, x, y;\n [x.n, u.m -> y.k, v.l], a.n -> b.m {m};";
    AnnotatedClass ac = loadSpec(spec);
    ClassRelation cr = assertClassRelation(ac, RelType.TYPE_METHOD_WITH_SUBTASK, vars("a.n"), vars("b.m"), "m");
    assertEquals(1, cr.getSubtasks().size());
    SubtaskClassRelation subtask = cr.getSubtasks().iterator().next();
    assertClassRelation(subtask, RelType.TYPE_SUBTASK, vars("x.n", "u.m"), vars("y.k", "v.l"), null);
    assertFalse("Should not be independent", subtask.isIndependent());
  }

  @Test
  public void testAxiomNoInputs_withSubtask() {
    String spec = "int a, b, x, y;\n [x -> y] -> b {m};";
    AnnotatedClass ac = loadSpec(spec);
    ClassRelation cr = assertClassRelation(ac, RelType.TYPE_METHOD_WITH_SUBTASK, vars(), vars("b"), "m");
    assertEquals(1, cr.getSubtasks().size());
    SubtaskClassRelation subtask = cr.getSubtasks().iterator().next();
    assertClassRelation(subtask, RelType.TYPE_SUBTASK, vars("x"), vars("y"), null);
    assertFalse("Should not be independent", subtask.isIndependent());
  }

  @Test
  public void testAxiom_withTwoSubtask() {
    String spec = "int a, b, x, y, u, v;\n [x -> y], [u -> v], a -> b {m};";
    AnnotatedClass ac = loadSpec(spec);
    ClassRelation cr = assertClassRelation(ac, RelType.TYPE_METHOD_WITH_SUBTASK, vars("a"), vars("b"), "m");
    assertEquals(2, cr.getSubtasks().size());
    Iterator<SubtaskClassRelation> iterator = cr.getSubtasks().iterator();
    {
      SubtaskClassRelation subtask = iterator.next();
      assertClassRelation(subtask, RelType.TYPE_SUBTASK, vars("x"), vars("y"), null);
      assertFalse("Should not be independent", subtask.isIndependent());
    }
    {
      SubtaskClassRelation subtask = iterator.next();
      assertClassRelation(subtask, RelType.TYPE_SUBTASK, vars("u"), vars("v"), null);
      assertFalse("Should not be independent", subtask.isIndependent());
    }
  }

  @Test(expected = SpecParseException.class)
  public void testAxiom_withSubtaskBadOrder() {
    assumeTrue(specificationLoader.isAntlrParser());
    
    String spec = "int a, b, x, y, u, v;\n a, [u -> v] -> b {m};";
    loadSpec(spec);
  }

  @Test(expected = SpecParseException.class)
  public void testAxiom_withTwoSubtaskBadOrder() {
    assumeTrue(specificationLoader.isAntlrParser());

    String spec = "int a, b, x, y, u, v;\n [x -> y], a, [u -> v] -> b {m};";
    loadSpec(spec);
  }

  @Test
  public void testGoal_noInput() {
    String spec = "int a, b;\n -> b;";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_UNIMPLEMENTED, vars(), vars("b"), null);
  }

  @Test
  public void testGoal() {
    String spec = "int a, b;\n a -> b;";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_UNIMPLEMENTED, vars("a"), vars("b"), null);
  }

  @Test(expected = SpecParseException.class)
  public void testGoal_noOutput() {
    String spec = "int a, b;\n a, b ->;";
    loadSpec(spec);
  }

  @Test(expected = UnknownVariableException.class)
  public void testAxiom_UndeclaredVars() {
    String spec = "a -> b {methodName};";
    loadSpec(spec);
  }

  @Test
  public void testAxiom_withIndependentSubtask() {
    specificationSourceProvider.add("M", wrapSpec( "int x, y;\n", "M" ));
    String spec = "int a, b;\n" +
            "M m;\n" +
            "[M |- x -> y], a -> b {meth};";
    AnnotatedClass ac = specificationLoader.isAntlrParser() ? loadSpec(spec) :  loadSpecs(spec).getType("this");
    ClassRelation cr = assertClassRelation(ac, RelType.TYPE_METHOD_WITH_SUBTASK, vars("a"), vars("b"), "meth");
    assertEquals(1, cr.getSubtasks().size());
    SubtaskClassRelation subtask = cr.getSubtasks().iterator().next();
    assertClassRelation(subtask, RelType.TYPE_SUBTASK, vars("x"), vars("y"), null);
    assertTrue("Should be independent", subtask.isIndependent());
  }

  @Test
  public void testAxiom_withIndependentSubtask_noInputs() {
    specificationSourceProvider.add("M", wrapSpec( "int x, y;\n", "M" ));
    String spec = "int a, b;\n" +
            "M m;\n" +
            "[M |- -> y] -> b {meth};";
    AnnotatedClass ac = specificationLoader.isAntlrParser() ? loadSpec(spec) :  loadSpecs(spec).getType("this");
    ClassRelation cr = assertClassRelation(ac, RelType.TYPE_METHOD_WITH_SUBTASK, vars(), vars("b"), "meth");
    assertEquals(1, cr.getSubtasks().size());
    SubtaskClassRelation subtask = cr.getSubtasks().iterator().next();
    assertClassRelation(subtask, RelType.TYPE_SUBTASK, vars(), vars("y"), null);
    assertTrue("Should be independent", subtask.isIndependent());
  }
}
