package io.badawi.coverage.runtime;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

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
    // TODO
  }
  
  
  public static void markExecutable(String className, int line) {
    coverage.put(className, line, false);
  }
  
  public static void markExecuted(String className, int line) {
    coverage.put(className, line, true);
  }
}
