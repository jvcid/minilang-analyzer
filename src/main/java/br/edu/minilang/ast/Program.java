package br.edu.minilang.ast;

import java.util.List;

public class Program extends AstNode {
    private final List<Statement> statements;

    public Program(int line, int column, List<Statement> statements) {
        super(line, column);
        this.statements = List.copyOf(statements);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}