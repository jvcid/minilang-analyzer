package br.edu.minilang.semantics;

import br.edu.minilang.ast.*;
import br.edu.minilang.diagnostics.Diagnostic;
import br.edu.minilang.diagnostics.DiagnosticReporter;

public class SemanticAnalyzer {
    private final SymbolTable symbolTable;
    private final DiagnosticReporter diagnostics;

    public SemanticAnalyzer(DiagnosticReporter diagnostics) {
        this.symbolTable = new SymbolTable();
        this.diagnostics = diagnostics;
    }

    public void analyze(Program program) {
        for (Statement statement : program.getStatements()) {
            analyzeStatement(statement);
        }
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    private void analyzeStatement(Statement statement) {
        if (statement instanceof Declaration declaration) {
            analyzeDeclaration(declaration);
        } else if (statement instanceof Assignment assignment) {
            analyzeAssignment(assignment);
        } else if (statement instanceof Block block) {
            analyzeBlock(block);
        } else if (statement instanceof IfStatement conditional) {
            analyzeCondition(conditional.getCondition(), "if");
            analyzeBlock(conditional.getThenBlock());

            if (conditional.getElseBlock() != null) {
                analyzeBlock(conditional.getElseBlock());
            }
        } else if (statement instanceof WhileStatement loop) {
            analyzeCondition(loop.getCondition(), "while");
            analyzeBlock(loop.getBody());
        } else {
            throw new IllegalArgumentException(
                    "Tipo de comando não reconhecido: "
                            + statement.getClass().getSimpleName()
            );
        }
    }

    private void analyzeDeclaration(Declaration declaration) {
        boolean alreadyDeclared =
                symbolTable.getCurrentScope()
                        .findLocal(declaration.getName()) != null;

        if (alreadyDeclared) {
            report(
                    declaration,
                    "A variável '" + declaration.getName()
                            + "' já foi declarada neste escopo."
            );
        }

        Type initializerType = null;

        if (declaration.getInitializer() != null) {
            initializerType = checkExpression(declaration.getInitializer());

            if (initializerType != null
                    && initializerType != declaration.getType()) {
                report(
                        declaration.getInitializer(),
                        "O inicializador de '" + declaration.getName()
                                + "' deve ter tipo " + declaration.getType()
                                + ", mas recebeu " + initializerType + "."
                );
            }
        }

        // A variável passa a existir depois de analisar seu inicializador.
        if (!alreadyDeclared) {
            symbolTable.declare(
                    declaration.getName(),
                    declaration.getType(),
                    declaration.getLine(),
                    declaration.getColumn()
            );
        }
    }

    private void analyzeAssignment(Assignment assignment) {
        Symbol symbol = symbolTable.lookup(assignment.getVariableName());
        Type valueType = checkExpression(assignment.getValue());

        if (symbol == null) {
            report(
                    assignment,
                    "A variável '" + assignment.getVariableName()
                            + "' não foi declarada."
            );
            return;
        }

        if (valueType != null && valueType != symbol.getType()) {
            report(
                    assignment.getValue(),
                    "A atribuição à variável '" + assignment.getVariableName()
                            + "' exige tipo " + symbol.getType()
                            + ", mas recebeu " + valueType + "."
            );
        }
    }

    private void analyzeBlock(Block block) {
        symbolTable.enterScope();

        try {
            for (Statement statement : block.getStatements()) {
                analyzeStatement(statement);
            }
        } finally {
            symbolTable.exitScope();
        }
    }

    private void analyzeCondition(Expression condition, String construct) {
        Type conditionType = checkExpression(condition);

        if (conditionType != null && conditionType != Type.BOOL) {
            report(
                    condition,
                    "A condição de '" + construct
                            + "' deve ter tipo BOOL, mas recebeu "
                            + conditionType + "."
            );
        }
    }

    private Type checkExpression(Expression expression) {
        if (expression instanceof LiteralExpression literal) {
            return literal.getType();
        }

        if (expression instanceof VariableExpression variable) {
            Symbol symbol = symbolTable.lookup(variable.getName());

            if (symbol == null) {
                report(
                        variable,
                        "A variável '" + variable.getName()
                                + "' não foi declarada."
                );
                return null;
            }

            return symbol.getType();
        }

        if (expression instanceof UnaryExpression unary) {
            return checkUnaryExpression(unary);
        }

        if (expression instanceof BinaryExpression binary) {
            return checkBinaryExpression(binary);
        }

        throw new IllegalArgumentException(
                "Tipo de expressão não reconhecido: "
                        + expression.getClass().getSimpleName()
        );
    }

    private Type checkUnaryExpression(UnaryExpression expression) {
        Type operandType = checkExpression(expression.getOperand());

        if (operandType == null) {
            return null;
        }

        return switch (expression.getOperator()) {
            case NEGATE -> {
                if (operandType != Type.INT) {
                    report(expression, "O operador '-' exige um operando INT.");
                    yield null;
                }
                yield Type.INT;
            }
            case NOT -> {
                if (operandType != Type.BOOL) {
                    report(expression, "O operador '!' exige um operando BOOL.");
                    yield null;
                }
                yield Type.BOOL;
            }
        };
    }

    private Type checkBinaryExpression(BinaryExpression expression) {
        Type leftType = checkExpression(expression.getLeft());
        Type rightType = checkExpression(expression.getRight());

        if (leftType == null || rightType == null) {
            return null;
        }

        return switch (expression.getOperator()) {
            case ADD, SUBTRACT, MULTIPLY, DIVIDE, REMAINDER -> {
                if (leftType != Type.INT || rightType != Type.INT) {
                    report(
                            expression,
                            "O operador '"
                                    + expression.getOperator().getSymbol()
                                    + "' exige dois operandos INT."
                    );
                    yield null;
                }
                yield Type.INT;
            }

            case LESS, LESS_EQUAL, GREATER, GREATER_EQUAL -> {
                if (leftType != Type.INT || rightType != Type.INT) {
                    report(
                            expression,
                            "O operador '"
                                    + expression.getOperator().getSymbol()
                                    + "' exige dois operandos INT."
                    );
                    yield null;
                }
                yield Type.BOOL;
            }

            case EQUAL, NOT_EQUAL -> {
                if (leftType != rightType) {
                    report(
                            expression,
                            "O operador '"
                                    + expression.getOperator().getSymbol()
                                    + "' exige operandos do mesmo tipo."
                    );
                    yield null;
                }
                yield Type.BOOL;
            }

            case AND, OR -> {
                if (leftType != Type.BOOL || rightType != Type.BOOL) {
                    report(
                            expression,
                            "O operador '"
                                    + expression.getOperator().getSymbol()
                                    + "' exige dois operandos BOOL."
                    );
                    yield null;
                }
                yield Type.BOOL;
            }
        };
    }

    private void report(AstNode node, String message) {
        diagnostics.report(
                Diagnostic.Phase.SEMANTIC,
                node.getLine(),
                node.getColumn(),
                message
        );
    }
}