package br.edu.minilang.ast;

public class Assignment extends Statement {
    private final String variableName;
    private final Expression value;

    public Assignment(
            int line,
            int column,
            String variableName,
            Expression value
    ) {
        super(line, column);
        this.variableName = variableName;
        this.value = value;
    }

    public String getVariableName() {
        return variableName;
    }

    public Expression getValue() {
        return value;
    }
}