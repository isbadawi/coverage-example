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

  public static void markExecutable(String className, int line) {
    coverage.put(className, line, false);
  }

  public static void markExecuted(String className, int line) {
    coverage.put(className, line, true);
  }

  public static byte markExecuted(String className, int line, byte expression) {
    markExecuted(className, line);
    return expression;
  }

  public static char markExecuted(String className, int line, char expression) {
    markExecuted(className, line);
    return expression;
  }

  public static int markExecuted(String className, int line, int expression) {
    markExecuted(className, line);
    return expression;
  }

  public static long markExecuted(String className, int line, long expression) {
    markExecuted(className, line);
    return expression;
  }

  public static boolean markExecuted(String className, int line, boolean expression) {
    markExecuted(className, line);
    return expression;
  }

  public static float markExecuted(String className, int line, float expression) {
    markExecuted(className, line);
    return expression;
  }

  public static double markExecuted(String className, int line, double expression) {
    markExecuted(className, line);
    return expression;
  }

  public static <T> T markExecuted(String className, int line, T expression) {
    markExecuted(className, line);
    return expression;
  }
}
