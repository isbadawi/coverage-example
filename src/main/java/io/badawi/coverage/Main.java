package io.badawi.coverage;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

public class Main {
  
  private static void abortIf(boolean condition, String message, Object... args) {
    if (condition) {
      System.err.printf(message + "\n", args);
      System.exit(1);
    }
  }
  
  private static File getOrCreateDirectory(String filename) {
    File outputDir = new File(filename);
    abortIf(outputDir.exists() && !outputDir.isDirectory(),
        "%s is not a directory", outputDir.getAbsolutePath());
    if (!outputDir.exists()) {
      outputDir.mkdirs();
    }
    return outputDir;
  }
  
  public static void main(String[] args) throws IOException, ParseException {
    List<String> arguments = Arrays.asList(args);
    Iterable<String> files = Iterables.filter(arguments, new Predicate<String>() {
      public boolean apply(String s) {
        return s.endsWith(".java");
      }
    });

    int directoryFlag = arguments.indexOf("-d");
    abortIf(directoryFlag == -1 || arguments.size() <= directoryFlag + 1,
        "usage: CoverageInstrumenter -d output-dir <java files>");
    File outputDir = getOrCreateDirectory(arguments.get(directoryFlag + 1));
    for (String filename : files) {
      CompilationUnit unit = JavaParser.parse(new File(filename));
      CoverageInstrumenter.instrument(unit);
      File outputFile = new File(outputDir, filename);
      Files.write(unit.toString(), outputFile, Charsets.UTF_8);
    }
  }
}
