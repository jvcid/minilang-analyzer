package br.edu.minilang;

import br.edu.minilang.ast.Program;
import br.edu.minilang.diagnostics.DiagnosticReporter;
import br.edu.minilang.lexer.Lexer;
import br.edu.minilang.output.AstPrinter;
import br.edu.minilang.parser.Parser;
import br.edu.minilang.semantics.SemanticAnalyzer;
import br.edu.minilang.semantics.Symbol;
import br.edu.minilang.semantics.SymbolTable;

import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Path sourcePath;

        if (args.length == 0) {
            sourcePath = Path.of("examples", "programa.minilang");
        } else if (args.length == 1) {
            sourcePath = Path.of(args[0]);
        } else {
            System.err.println("Informe no máximo um caminho de arquivo.");
            return;
        }

        DiagnosticReporter diagnostics = new DiagnosticReporter();

        System.out.println("Analisando: " + sourcePath.toAbsolutePath());

        try (Reader reader = Files.newBufferedReader(
                sourcePath,
                StandardCharsets.UTF_8
        )) {
            Lexer lexer = new Lexer(reader, diagnostics);
            Parser parser = new Parser(lexer);
            parser.setDiagnosticReporter(diagnostics);

            java_cup.runtime.Symbol parseResult = parser.parse();
            Program program = (Program) parseResult.value;

            if (diagnostics.hasErrors()) {
                printDiagnostics(diagnostics);
                return;
            }

            System.out.println("Árvore sintática:");
            System.out.println(new AstPrinter().print(program));

            SemanticAnalyzer analyzer = new SemanticAnalyzer(diagnostics);
            analyzer.analyze(program);

            printSymbolTable(analyzer.getSymbolTable());
            printDiagnostics(diagnostics);

        } catch (Exception exception) {
            printDiagnostics(diagnostics);

            if (!diagnostics.hasErrors()) {
                System.err.println(
                        "Não foi possível analisar o arquivo: "
                                + exception.getMessage()
                );
            }
        }
    }

    private static void printSymbolTable(SymbolTable symbolTable) {
        System.out.println();
        System.out.println("Tabela de símbolos:");
        System.out.printf(
                "%-18s %-8s %-10s %-8s %-12s%n",
                "Nome",
                "Tipo",
                "Escopo",
                "Nível",
                "Declaração"
        );

        for (Symbol symbol : symbolTable.getAllSymbols()) {
            System.out.printf(
                    "%-18s %-8s %-10s %-8d %d:%d%n",
                    symbol.getName(),
                    symbol.getType(),
                    "E" + symbol.getScopeId(),
                    symbol.getScopeLevel(),
                    symbol.getDeclarationLine(),
                    symbol.getDeclarationColumn()
            );
        }
    }

    private static void printDiagnostics(DiagnosticReporter diagnostics) {
        if (!diagnostics.hasErrors()) {
            System.out.println();
            System.out.println("Análise concluída sem erros.");
            return;
        }

        System.err.println();
        System.err.println("Erros encontrados:");

        diagnostics.getDiagnostics()
                .forEach(diagnostic -> System.err.println(diagnostic));
    }
}