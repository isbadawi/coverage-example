package io.badawi.coverage;

import japa.parser.ast.CompilationUnit;

public class CoverageInstrumenter {
  public static void instrument(Iterable<CompilationUnit> units) {
    for (CompilationUnit unit : units) {
      CoverageInstrumentationVisitor visitor = new CoverageInstrumentationVisitor();
      unit.accept(visitor, null);
    }
  }
}
