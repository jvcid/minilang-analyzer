package br.edu.minilang.ast;

import br.edu.minilang.semantics.Type;

public class LiteralExpression extends Expression {
    private final String lexeme;
    private final Type type;

    public LiteralExpression(int line, int column, String lexeme, Type type) {
        super(line, column);
        this.lexeme = lexeme;
        this.type = type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Type getType() {
        return type;
    }
}