package io.badawi.coverage;

import japa.parser.ast.CompilationUnit;
import japa.parser.ast.Node;
import japa.parser.ast.expr.IntegerLiteralExpr;
import japa.parser.ast.expr.StringLiteralExpr;
import japa.parser.ast.stmt.BlockStmt;
import japa.parser.ast.stmt.ExpressionStmt;
import japa.parser.ast.stmt.Statement;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
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
    
    private String filename;
    
    private Statement makeCoverageTrackingCall(Node node) {
      executableLines.put(filename, node.getBeginLine());
      return AstUtil.createCoverageTrackerCall("markExecuted",
          new StringLiteralExpr(filename),
          new IntegerLiteralExpr(String.valueOf(node.getBeginLine())));
    }
    
    @Override public void visit(CompilationUnit unit, Object arg) {
      filename = ((File) unit.getData()).getAbsolutePath();
      super.visit(unit, arg);
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
