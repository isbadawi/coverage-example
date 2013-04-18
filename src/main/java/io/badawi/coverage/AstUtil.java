package io.badawi.coverage;

import japa.parser.ASTHelper;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;

public class AstUtil {
  public static Statement createMethodCall(String qualifiedName, Expression... arguments) {
    int dot = qualifiedName.lastIndexOf('.');
    NameExpr scope = ASTHelper.createNameExpr(qualifiedName.substring(0, dot));
    MethodCallExpr call = new MethodCallExpr(scope, qualifiedName.substring(dot + 1));
    for (Expression argument : arguments) {
      ASTHelper.addArgument(call, argument);
    }
    return new ExpressionStmt(call);
  }
  
  public static Statement createCoverageTrackerCall(String method, Expression... arguments) {
    return createMethodCall("io.badawi.coverage.runtime.CoverageTracker." + method, arguments);
  }

  private AstUtil() {}
}
