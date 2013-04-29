package io.badawi.coverage;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;

public class Main {
  private static CompilationUnit parse(String filename) {
    try {
      return JavaParser.parse(new File(filename));
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static void main(String[] args) throws IOException, ParseException {
    List<String> arguments = Arrays.asList(args);

    int directoryFlag = arguments.indexOf("-d");
    if (directoryFlag == -1 || arguments.size() <= directoryFlag + 1) {
      System.err.println("usage: java io.badawi.coverage.Main -d output-dir <java files>\n");
      return;
    }

    File outputDir = new File(arguments.get(directoryFlag + 1));
    if (outputDir.exists() && !outputDir.isDirectory()) {
      System.err.printf("%s exists and is not a directory\n", outputDir.getAbsolutePath());
      return;
    }

    for (String file : arguments) {
      if (!file.endsWith(".java")) {
        continue;
      }
      CompilationUnit unit = parse(file);
      unit.accept(new CoverageInstrumentationVisitor(new File(file).getAbsolutePath()), null);
      File outputFile = new File(outputDir, file);
      Files.createParentDirs(outputFile);
      Files.write(unit.toString(), outputFile, Charsets.UTF_8);
    }
  }
}
