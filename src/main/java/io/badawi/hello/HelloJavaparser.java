package io.badawi.hello;

import io.badawi.coverage.CoverageInstrumenter;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.StringReader;

public class HelloJavaparser {
  public static void main(String[] args) throws ParseException {
    CompilationUnit unit = JavaParser.parse(new StringReader(new StringBuilder()
      .append("package io.badawi.hello;\n\n")
      .append("public class Hello {\n")
      .append("  public static void main(String[] args) {\n")
      .append("    System.out.println(\"hello, world\");\n")
      .append("  }\n")
      .append("}\n").toString()));
    
    CoverageInstrumenter.instrument(unit);
    System.out.println(unit.toString());
  }
}
