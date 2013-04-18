package io.badawi.coverage;

import japa.parser.ast.Node;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.expr.QualifiedNameExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;
import java.util.Stack;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class CoverageInstrumentationVisitor extends VoidVisitorAdapter<Object> {
    private Multimap<String, Integer> executableLines = HashMultimap.create();
    
    public Multimap<String, Integer> getExecutableLines() {
      return executableLines;
    }
    
    private Statement makeCoverageTrackingCall(Node node) {
      executableLines.put(currentClass.toString(), node.getBeginLine());
      return AstUtil.createCoverageTrackerCall("markExecuted",
          new StringLiteralExpr(currentClass.toString()),
          new IntegerLiteralExpr(String.valueOf(node.getBeginLine())));
    }
    
    private NameExpr currentPackage;
    private NameExpr currentClass;
    
    @Override public void visit(PackageDeclaration node, Object arg) {
      currentPackage = node.getName();
      super.visit(node, arg);
    }
    
    @Override public void visit(ClassOrInterfaceDeclaration node, Object arg) {
      currentClass = new QualifiedNameExpr(currentPackage, node.getName());
      super.visit(node, arg);
    }
    
    private Stack<List<Statement>> blocks = new Stack<List<Statement>>();

    @Override
    public void visit(BlockStmt stmt, Object arg) {
      blocks.push(Lists.newArrayList(stmt.getStmts()));
      super.visit(stmt, blocks);
      stmt.setStmts(blocks.pop());
    }

    @Override
    public void visit(ExpressionStmt node, Object arg) {
      List<Statement> body = blocks.peek();
      body.add(body.indexOf(node), makeCoverageTrackingCall(node));
    }
}
