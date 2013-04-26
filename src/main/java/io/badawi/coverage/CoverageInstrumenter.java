package io.badawi.coverage;

import io.badawi.coverage.runtime.CoverageTracker;
import japa.parser.ast.CompilationUnit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class CoverageInstrumenter {
  public static void instrument(Iterable<CompilationUnit> units) {
    Multimap<String, Integer> executableLines = HashMultimap.create();
    for (CompilationUnit unit : units) {
      CoverageInstrumentationVisitor visitor = new CoverageInstrumentationVisitor();
      unit.accept(visitor, null);
      executableLines.putAll(visitor.getExecutableLines());
    }

    for (String filename : executableLines.keySet()) {
      for (Integer line : executableLines.get(filename)) {
        CoverageTracker.markExecutable(filename, line);
      }
    }
  }
}
