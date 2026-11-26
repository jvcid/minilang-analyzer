package br.edu.minilang.semantics;

import java.util.ArrayList;
import java.util.List;

public class SymbolTable {
    private Scope currentScope;
    private int nextScopeId;
    private final List<Symbol> allSymbols = new ArrayList<>();

    public SymbolTable() {
        this.currentScope = new Scope(0, null);
        this.nextScopeId = 1;
    }

    public void enterScope() {
        currentScope = new Scope(nextScopeId++, currentScope);
    }

    public void exitScope() {
        if (currentScope.getParent() == null) {
            throw new IllegalStateException(
                    "Não é possível sair do escopo global."
            );
        }

        currentScope = currentScope.getParent();
    }

    public boolean declare(
            String name,
            Type type,
            int line,
            int column
    ) {
        Symbol symbol = new Symbol(
                name,
                type,
                line,
                column,
                currentScope.getId(),
                currentScope.getLevel()
        );

        boolean declared = currentScope.declare(symbol);

        if (declared) {
            allSymbols.add(symbol);
        }

        return declared;
    }

    public Symbol lookup(String name) {
        Scope scope = currentScope;

        while (scope != null) {
            Symbol symbol = scope.findLocal(name);

            if (symbol != null) {
                return symbol;
            }

            scope = scope.getParent();
        }

        return null;
    }

    public List<Symbol> getAllSymbols() {
        return List.copyOf(allSymbols);
    }

    public Scope getCurrentScope() {
        return currentScope;
    }
}