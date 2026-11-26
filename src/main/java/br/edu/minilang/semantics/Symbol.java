package br.edu.minilang.semantics;

public class Symbol {
    private final String name;
    private final Type type;
    private final int declarationLine;
    private final int declarationColumn;
    private final int scopeId;
    private final int scopeLevel;

    public Symbol(
            String name,
            Type type,
            int declarationLine,
            int declarationColumn,
            int scopeId,
            int scopeLevel
    ) {
        this.name = name;
        this.type = type;
        this.declarationLine = declarationLine;
        this.declarationColumn = declarationColumn;
        this.scopeId = scopeId;
        this.scopeLevel = scopeLevel;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public int getDeclarationLine() {
        return declarationLine;
    }

    public int getDeclarationColumn() {
        return declarationColumn;
    }

    public int getScopeId() {
        return scopeId;
    }

    public int getScopeLevel() {
        return scopeLevel;
    }
}