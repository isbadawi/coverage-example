package io.badawi.coverage.runtime;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CoverageTracker {
  private static Map<String, Map<Integer, Boolean>> coverage =
      new HashMap<String, Map<Integer, Boolean>>();

  static {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override public void run() {
        writeCoverageToFile();
      }
    });
  }

  private static void writeCoverageToFile() {
    String lcovCoverage = generateLcov();
    FileWriter writer = null;
    try {
      writer = new FileWriter("coverage_report.lcov");
      writer.write(lcovCoverage);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      try {
        writer.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static String generateLcov() {
    StringBuilder sb = new StringBuilder();
    for (String className : coverage.keySet()) {
      sb.append("SF:" + className + "\n");
      for (Map.Entry<Integer, Boolean> line : coverage.get(className).entrySet()) {
        sb.append(String.format("DA:%d,%d\n", line.getKey(), line.getValue() ? 1 : 0));
      }
      sb.append("end_of_record\n");
    }
    return sb.toString();
  }

  public static void markExecutable(String filename, int line) {
    if (!coverage.containsKey(filename)) {
      coverage.put(filename, new HashMap<Integer, Boolean>());
    }
    coverage.get(filename).put(line, false);
  }

  public static void markExecuted(String filename, int line) {
    if (!coverage.containsKey(filename)) {
      coverage.put(filename, new HashMap<Integer, Boolean>());
    }
    coverage.get(filename).put(line, true);
  }

  public static byte markExecuted(String filename, int line, byte expression) {
    markExecuted(filename, line);
    return expression;
  }

  public static char markExecuted(String filename, int line, char expression) {
    markExecuted(filename, line);
    return expression;
  }

  public static int markExecuted(String filename, int line, int expression) {
    markExecuted(filename, line);
    return expression;
  }

  public static long markExecuted(String filename, int line, long expression) {
    markExecuted(filename, line);
    return expression;
  }

  public static boolean markExecuted(String filename, int line, boolean expression) {
    markExecuted(filename, line);
    return expression;
  }

  public static float markExecuted(String filename, int line, float expression) {
    markExecuted(filename, line);
    return expression;
  }

  public static double markExecuted(String filename, int line, double expression) {
    markExecuted(filename, line);
    return expression;
  }

  public static <T> T markExecuted(String filename, int line, T expression) {
    markExecuted(filename, line);
    return expression;
  }
}
