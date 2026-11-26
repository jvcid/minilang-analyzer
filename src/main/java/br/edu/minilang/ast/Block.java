package br.edu.minilang.ast;

import java.util.List;

public class Block extends Statement {
    private final List<Statement> statements;

    public Block(int line, int column, List<Statement> statements) {
        super(line, column);
        this.statements = List.copyOf(statements);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}