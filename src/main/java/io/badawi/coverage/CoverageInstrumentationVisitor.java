package io.badawi.coverage;

import japa.parser.ASTHelper;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.expr.ArrayAccessExpr;
import japa.parser.ast.expr.ArrayCreationExpr;
import japa.parser.ast.expr.AssignExpr;
import japa.parser.ast.expr.BinaryExpr;
import japa.parser.ast.expr.BooleanLiteralExpr;
import japa.parser.ast.expr.CastExpr;
import japa.parser.ast.expr.CharLiteralExpr;
import japa.parser.ast.expr.ClassExpr;
import japa.parser.ast.expr.ConditionalExpr;
import japa.parser.ast.expr.DoubleLiteralExpr;
import japa.parser.ast.expr.EnclosedExpr;
import japa.parser.ast.expr.Expression;
import japa.parser.ast.expr.FieldAccessExpr;
import japa.parser.ast.expr.InstanceOfExpr;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.IntegerLiteralMinValueExpr;
import japa.parser.ast.expr.LongLiteralExpr;
import japa.parser.ast.expr.LongLiteralMinValueExpr;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.NullLiteralExpr;
import japa.parser.ast.expr.ObjectCreationExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.expr.SuperExpr;
import japa.parser.ast.expr.ThisExpr;
import japa.parser.ast.expr.UnaryExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.ForStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.ModifierVisitorAdapter;

import java.io.File;
import java.util.List;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class CoverageInstrumentationVisitor extends ModifierVisitorAdapter<Object> {
  private Multimap<String, Integer> executableLines = HashMultimap.create();

  public Multimap<String, Integer> getExecutableLines() {
    return executableLines;
  }

  private String filename;

  private Expression makeCoverageTrackingCall(Expression node) {
    return makeCoverageTrackingCall(node, true);
  }

  private Expression makeCoverageTrackingCall(Expression node, boolean passInNode) {
    executableLines.put(filename, node.getBeginLine());
    MethodCallExpr call = AstUtil.createCoverageTrackerCall("markExecuted",
        new StringLiteralExpr(filename),
        new IntegerLiteralExpr(String.valueOf(node.getBeginLine())));
    if (passInNode) {
      ASTHelper.addArgument(call, node);
    }
    return call;
  }

  @Override public Node visit(CompilationUnit unit, Object arg) {
    filename = ((File) unit.getData()).getAbsolutePath();
    return super.visit(unit, arg);
  }

  @Override
  public Node visit(ExpressionStmt n, Object arg) {
    if (!(n.getExpression() instanceof MethodCallExpr)) {
      return super.visit(n, arg);
    }
    BlockStmt block = new BlockStmt();
    ASTHelper.addStmt(block, makeCoverageTrackingCall(n.getExpression(), false));
    ASTHelper.addStmt(block, n);
    return block;
  }

  private List<Expression> instrument(List<Expression> exprs) {
    List<Expression> newExprs = Lists.newArrayList(exprs);
    for (int i = 0, j = 0; i < exprs.size(); i++, j++) {
      Expression expr = exprs.get(i);
      if (expr instanceof MethodCallExpr) {
        newExprs.add(j++, makeCoverageTrackingCall(expr, false));
      }
    }
    return newExprs;
  }

  @Override
  public Node visit(ForStmt n, Object arg) {
    n.setInit(instrument(n.getInit()));
    n.setCompare((Expression) n.getCompare().accept(this, arg));
    n.setBody((Statement) n.getBody().accept(this, arg));
    n.setUpdate(instrument(n.getUpdate()));
    return n;
  }

  // I don't like what's below this line either, but I'm not sure what to do about it.
  // There doesn't seem to be mechanism for having one visit method apply to all expressions.

  @Override
  public Node visit(ArrayAccessExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(ArrayCreationExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(AssignExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(BinaryExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(BooleanLiteralExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(CastExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(CharLiteralExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(ClassExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(ConditionalExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(DoubleLiteralExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(EnclosedExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(FieldAccessExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(InstanceOfExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(IntegerLiteralExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(IntegerLiteralMinValueExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(LongLiteralExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(LongLiteralMinValueExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(MethodCallExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  private boolean insideStatement(Node n) {
    while (n != null && !(n instanceof Statement)) {
      n = n.getParentNode();
    }
    return n instanceof Statement;
  }

  @Override
  public Node visit(NameExpr n, Object arg) {
    if (insideStatement(n)) {
      return makeCoverageTrackingCall(n);
    }
    return n;
  }

  @Override
  public Node visit(NullLiteralExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(ObjectCreationExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(StringLiteralExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(SuperExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(ThisExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }

  @Override
  public Node visit(UnaryExpr n, Object arg) {
    return makeCoverageTrackingCall(n);
  }
}
