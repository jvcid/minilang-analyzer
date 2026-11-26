package br.edu.minilang.semantics;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scope {
    private final int id;
    private final int level;
    private final Scope parent;
    private final Map<String, Symbol> symbols = new LinkedHashMap<>();

    public Scope(int id, Scope parent) {
        this.id = id;
        this.parent = parent;
        this.level = parent == null ? 0 : parent.level + 1;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public Scope getParent() {
        return parent;
    }

    public boolean declare(Symbol symbol) {
        if (symbols.containsKey(symbol.getName())) {
            return false;
        }

        symbols.put(symbol.getName(), symbol);
        return true;
    }

    public Symbol findLocal(String name) {
        return symbols.get(name);
    }

    public Collection<Symbol> getSymbols() {
        return symbols.values();
    }
}