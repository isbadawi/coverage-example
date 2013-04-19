package io.badawi.coverage.runtime;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Files;

public class CoverageTracker {
  private static Table<String, Integer, Boolean> coverage = HashBasedTable.create();
  
  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override public void run() {
        writeCoverageToFile();
      }
    });
  }
  
  private static void writeCoverageToFile() {
    String lcovCoverage = generateLcov();
    try {
      Files.write(lcovCoverage, new File("coverage_report.lcov"), Charsets.UTF_8);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  public static void markExecutable(String className, int line) {
    coverage.put(className, line, false);
  }
  
  public static void markExecuted(String className, int line) {
    coverage.put(className, line, true);
  }
  
  private static String generateLcov() {
    StringBuilder sb = new StringBuilder();
    for (String className : coverage.rowKeySet()) {
      sb.append("SF:" + className + "\n");
      for (Map.Entry<Integer, Boolean> line : coverage.row(className).entrySet()) {
        sb.append(String.format("DA:%d,%d\n", line.getKey(), line.getValue() ? 1 : 0));
      }
      sb.append("end_of_record\n");
    }
    return sb.toString();
  }
}
