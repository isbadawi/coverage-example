package io.badawi.coverage;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertSame;

public class CoverageInstrumenterTest {
  private CompilationUnit helloClassfile;
  private MethodDeclaration mainMethod;

  @Before
  public void initializeHelloClass() throws ParseException {
    helloClassfile = JavaParser.parse(new StringReader(new StringBuilder()
        .append("package io.badawi.hello;\n\n")
        .append("public class Hello {\n")
        .append("  public static void main(String[] args) {\n")
        .append("    System.out.println(\"hello, world\");\n")
        .append("  }\n")
        .append("}\n").toString()));
    mainMethod = (MethodDeclaration) helloClassfile.getTypes().get(0).getMembers().get(0);
  }

  @Test
  public void testCanRecognizeMainMethod() {
    assertTrue(CoverageInstrumenter.isMainMethod(mainMethod));
  }
  
  @Test
  public void testCanFindMainMethod() {
    assertSame(mainMethod, CoverageInstrumenter.findMainMethod(helloClassfile));
  }

}
