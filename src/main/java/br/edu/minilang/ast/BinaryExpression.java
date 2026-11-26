package br.edu.minilang.ast;

public class BinaryExpression extends Expression {
    private final Expression left;
    private final BinaryOperator operator;
    private final Expression right;

    public BinaryExpression(
            int line,
            int column,
            Expression left,
            BinaryOperator operator,
            Expression right
    ) {
        super(line, column);
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public BinaryOperator getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}