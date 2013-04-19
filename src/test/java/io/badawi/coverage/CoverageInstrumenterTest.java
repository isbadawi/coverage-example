package io.badawi.coverage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Ints;

public class CoverageInstrumenterTest {
  private CompilationUnit helloClassfile;
  private Iterable<MethodDeclaration> nonMainMethods;
  private MethodDeclaration mainMethod;
  
  private MethodDeclaration getMethod(CompilationUnit classfile, int i) {
    return (MethodDeclaration) classfile.getTypes().get(0).getMembers().get(i);
  }
  
  private Iterable<MethodDeclaration> getMethods(final CompilationUnit classfile, int... is) {
    return Iterables.transform(Ints.asList(is), new Function<Integer, MethodDeclaration>() {
      public MethodDeclaration apply(Integer i) {
        return getMethod(classfile, i);
      }
    });
  }
  
  private String method(String signature) {
    return new StringBuilder()
      .append("  ").append(signature).append(" {\n")
      .append("    System.out.println(\"hello, world\");\n")
      .append("  }").toString();
  }

  @Before
  public void initializeHelloClass() throws ParseException {
    helloClassfile = JavaParser.parse(new StringReader(new StringBuilder()
        .append("package io.badawi.hello;\n\n")
        .append("public class Hello {\n")
        .append(method("public static void notMain(String[] args)"))
        .append(method("public static void main(String[] args)"))
        .append(method("public static int main(String[] args)"))
        .append(method("public static void main(String[][] args)"))
        .append(method("static void main(String[] args)"))
        .append(method("public void main(String[] args)"))
        .append(method("public static void main()"))
        .append("}\n").toString()));
    nonMainMethods = getMethods(helloClassfile, 0, 2, 3, 4, 5, 6);
    mainMethod = getMethod(helloClassfile, 1);
  }

  @Test
  public void testCanRecognizeMainMethod() {
    for (MethodDeclaration nonMainMethod : nonMainMethods) {
      assertFalse(CoverageInstrumenter.isMainMethod(nonMainMethod));
    }
    assertTrue(CoverageInstrumenter.isMainMethod(mainMethod));
  }
  
  @Test
  public void testCanFindMainMethod() {
    assertSame(mainMethod, CoverageInstrumenter.findMainMethod(helloClassfile));
  }

}
