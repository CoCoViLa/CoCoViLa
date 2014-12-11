package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.*;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

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

  //[z->y],a -> b {methodName};		//VALID
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

  //[z,x->y,u],a -> b {methodName};	//VALID
//  [z->y] -> b.ou {methodName};	//VALID
  @Test(expected = UnknownVariableException.class)
  public void testAxiom_UndeclaredVars() {
    String spec = "a -> b {methodName};";
    loadSpec(spec);
  }
}
