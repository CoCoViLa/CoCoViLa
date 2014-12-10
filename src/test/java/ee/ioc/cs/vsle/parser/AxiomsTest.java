package ee.ioc.cs.vsle.parser;

import ee.ioc.cs.vsle.synthesize.AnnotatedClass;
import ee.ioc.cs.vsle.synthesize.RelType;
import ee.ioc.cs.vsle.synthesize.UnknownVariableException;
import org.junit.Test;

/**
 * @author Pavel Grigorenko
 */
public class AxiomsTest extends AbstractParserTest {

  @Test
  public void testSimpleAxiom() {
    String spec = "int a, b;\n a -> b {m};";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_JAVAMETHOD, "b", "m");
  }

  @Test
  public void testSimpleAxiom_withControlVars() {
    String spec = "int a, b;\n" +
            "void init, done;\n" +
            "init, a -> b, done {m};";
    AnnotatedClass ac = loadSpec(spec);
    assertClassRelation(ac, RelType.TYPE_JAVAMETHOD, new String[]{ "init", "a"}, new String[] { "b", "done" }, "m");
  }

  @Test(expected = UnknownVariableException.class)
  public void testAxiom_UndeclaredVars() {
    String spec = "a -> b {methodName};";
    loadSpec(spec);
  }
}
