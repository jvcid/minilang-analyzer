package br.edu.minilang.ast;

public enum UnaryOperator {
    NEGATE("-"),
    NOT("!");

    private final String symbol;

    UnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}