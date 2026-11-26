package br.edu.minilang.ast;

public class IfStatement extends Statement {
    private final Expression condition;
    private final Block thenBlock;
    private final Block elseBlock;

    public IfStatement(
            int line,
            int column,
            Expression condition,
            Block thenBlock,
            Block elseBlock
    ) {
        super(line, column);
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }

    public Expression getCondition() {
        return condition;
    }

    public Block getThenBlock() {
        return thenBlock;
    }

    public Block getElseBlock() {
        return elseBlock;
    }
}