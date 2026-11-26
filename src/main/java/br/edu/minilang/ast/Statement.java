package br.edu.minilang.ast;

public abstract class Statement extends AstNode {
    protected Statement(int line, int column) {
        super(line, column);
    }
}