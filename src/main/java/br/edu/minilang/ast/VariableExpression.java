package br.edu.minilang.ast;

public class VariableExpression extends Expression {
    private final String name;

    public VariableExpression(int line, int column, String name) {
        super(line, column);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}