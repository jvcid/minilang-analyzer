package br.edu.minilang.ast;

public abstract class Expression extends AstNode {
    protected Expression(int line, int column) {
        super(line, column);
    }
}