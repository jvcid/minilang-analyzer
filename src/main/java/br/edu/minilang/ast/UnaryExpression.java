package br.edu.minilang.ast;

public class UnaryExpression extends Expression {
    private final UnaryOperator operator;
    private final Expression operand;

    public UnaryExpression(
            int line,
            int column,
            UnaryOperator operator,
            Expression operand
    ) {
        super(line, column);
        this.operator = operator;
        this.operand = operand;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    public Expression getOperand() {
        return operand;
    }
}