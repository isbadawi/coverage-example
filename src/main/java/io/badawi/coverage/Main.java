package io.badawi.coverage;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
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

  private static CompilationUnit parse(String filename) {
    File sourceFile = new File(filename);
    CompilationUnit unit;
    try {
      unit = JavaParser.parse(sourceFile);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
    unit.setData(sourceFile);
    return unit;
  }

  public static void main(String[] args) throws IOException, ParseException {
    List<String> arguments = Arrays.asList(args);
    List<String> files = FluentIterable.from(arguments)
        .filter(new Predicate<String>() {
          public boolean apply(String s) {
            return s.endsWith(".java");
          }
        }).toList();

    int directoryFlag = arguments.indexOf("-d");
    abortIf(directoryFlag == -1 || arguments.size() <= directoryFlag + 1,
        "usage: CoverageInstrumenter -d output-dir -e entry-point-class <java files>");

    int entryPointFlag = arguments.indexOf("-e");
    abortIf(entryPointFlag == -1 || arguments.size() <= entryPointFlag + 1,
        "usage: CoverageInstrumenter -d output-dir -e entry-point-class <java files>");

    File outputDir = getOrCreateDirectory(arguments.get(directoryFlag + 1));
    String entryPointClass = arguments.get(entryPointFlag + 1);

    List<CompilationUnit> units = FluentIterable.from(files)
        .transform(new Function<String, CompilationUnit>() {
          public CompilationUnit apply(String filename) {
            return parse(filename);
          }
        }).toList();
    CoverageInstrumenter.instrument(units, entryPointClass);

    for (CompilationUnit unit : units) {
      File outputFile = new File(outputDir, ((File) unit.getData()).getPath());
      Files.createParentDirs(outputFile);
      Files.write(unit.toString(), outputFile, Charsets.UTF_8);
    }
  }
}
