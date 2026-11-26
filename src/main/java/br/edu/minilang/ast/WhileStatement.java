package br.edu.minilang.ast;

public class WhileStatement extends Statement {
    private final Expression condition;
    private final Block body;

    public WhileStatement(
            int line,
            int column,
            Expression condition,
            Block body
    ) {
        super(line, column);
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getBody() {
        return body;
    }
}