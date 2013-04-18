package io.badawi.coverage;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.ModifierSet;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.type.ReferenceType;
import japa.parser.ast.type.VoidType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import com.google.common.collect.Multimap;

public class CoverageInstrumenter {
  private static boolean isMainMethod(MethodDeclaration method) {
    return ModifierSet.isPublic(method.getModifiers())
        && ModifierSet.isStatic(method.getModifiers())
        && method.getType() instanceof VoidType
        && method.getParameters().size() == 1
        && method.getParameters().get(0).getType().equals(
            new ReferenceType(new ClassOrInterfaceType("String"), 1));
  }

  private static MethodDeclaration findMainMethod(CompilationUnit unit) {
    final MethodDeclaration[] method = new MethodDeclaration[1];
    unit.accept(new VoidVisitorAdapter<Object>() {
      @Override public void visit(MethodDeclaration node, Object arg) {
        if (isMainMethod(node)) {
          method[0] = node;
        }
      }
    }, null);
    return method[0];
  }

  public static void instrument(CompilationUnit unit) {
    CoverageInstrumentationVisitor visitor = new CoverageInstrumentationVisitor();
    unit.accept(visitor, null);
    Multimap<String, Integer> executableLines = visitor.getExecutableLines();
    MethodDeclaration mainMethod = findMainMethod(unit);
    List<Statement> body = mainMethod.getBody().getStmts();
    for (String className : executableLines.keySet()) {
      for (Integer line : executableLines.get(className)) {
        body.add(0, AstUtil.createMethodCall("io.badawi.coverage.runtime.CoverageTracker.markExecutable",
            new StringLiteralExpr(className), new IntegerLiteralExpr(String.valueOf(line))));
      }
    }
    body.add(AstUtil.createMethodCall(
        "io.badawi.coverage.runtime.CoverageTracker.writeCoverageToFile",
        new StringLiteralExpr("coverage_report.txt")));
  }
}
