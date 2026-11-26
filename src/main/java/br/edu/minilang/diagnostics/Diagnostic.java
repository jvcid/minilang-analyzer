package br.edu.minilang.diagnostics;

public class Diagnostic {
    public enum Phase {
        LEXICAL("Léxico"),
        SYNTACTIC("Sintático"),
        SEMANTIC("Semântico");

        private final String label;

        Phase(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final Phase phase;
    private final int line;
    private final int column;
    private final String message;

    public Diagnostic(Phase phase, int line, int column, String message) {
        this.phase = phase;
        this.line = line;
        this.column = column;
        this.message = message;
    }

    public Phase getPhase() {
        return phase;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "%s [%d:%d]: %s".formatted(
                phase.getLabel(),
                line,
                column,
                message
        );
    }
}