package br.edu.minilang.diagnostics;

import java.util.ArrayList;
import java.util.List;

public class DiagnosticReporter {
    private final List<Diagnostic> diagnostics = new ArrayList<>();

    public void report(
            Diagnostic.Phase phase,
            int line,
            int column,
            String message
    ) {
        diagnostics.add(new Diagnostic(phase, line, column, message));
    }

    public boolean hasErrors() {
        return !diagnostics.isEmpty();
    }

    public List<Diagnostic> getDiagnostics() {
        return List.copyOf(diagnostics);
    }
}