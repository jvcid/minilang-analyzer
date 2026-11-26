package br.edu.minilang.ast;

import br.edu.minilang.semantics.Type;

public class Declaration extends Statement {
    private final Type type;
    private final String name;
    private final Expression initializer;

    public Declaration(
            int line,
            int column,
            Type type,
            String name,
            Expression initializer
    ) {
        super(line, column);
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Expression getInitializer() {
        return initializer;
    }
}