package br.edu.minilang.output;

import br.edu.minilang.ast.*;

public class AstPrinter {

    public String print(Program program) {
        StringBuilder output = new StringBuilder();
        append(output, 0, "Program");

        for (Statement statement : program.getStatements()) {
            printStatement(statement, 1, output);
        }

        return output.toString();
    }

    private void printStatement(
            Statement statement,
            int depth,
            StringBuilder output
    ) {
        if (statement instanceof Declaration declaration) {
            append(
                    output,
                    depth,
                    "Declaration: " + declaration.getType()
                            + " " + declaration.getName()
            );

            if (declaration.getInitializer() != null) {
                append(output, depth + 1, "Initializer");
                printExpression(declaration.getInitializer(), depth + 2, output);
            }

        } else if (statement instanceof Assignment assignment) {
            append(output, depth, "Assignment: " + assignment.getVariableName());
            printExpression(assignment.getValue(), depth + 1, output);

        } else if (statement instanceof Block block) {
            printBlock(block, depth, output);

        } else if (statement instanceof IfStatement conditional) {
            append(output, depth, "IfStatement");
            append(output, depth + 1, "Condition");
            printExpression(conditional.getCondition(), depth + 2, output);

            append(output, depth + 1, "Then");
            printBlock(conditional.getThenBlock(), depth + 2, output);

            if (conditional.getElseBlock() != null) {
                append(output, depth + 1, "Else");
                printBlock(conditional.getElseBlock(), depth + 2, output);
            }

        } else if (statement instanceof WhileStatement loop) {
            append(output, depth, "WhileStatement");
            append(output, depth + 1, "Condition");
            printExpression(loop.getCondition(), depth + 2, output);
            append(output, depth + 1, "Body");
            printBlock(loop.getBody(), depth + 2, output);
        }
    }

    private void printBlock(Block block, int depth, StringBuilder output) {
        append(output, depth, "Block");

        for (Statement statement : block.getStatements()) {
            printStatement(statement, depth + 1, output);
        }
    }

    private void printExpression(
            Expression expression,
            int depth,
            StringBuilder output
    ) {
        if (expression instanceof BinaryExpression binary) {
            append(
                    output,
                    depth,
                    "BinaryExpression: "
                            + binary.getOperator().getSymbol()
            );
            printExpression(binary.getLeft(), depth + 1, output);
            printExpression(binary.getRight(), depth + 1, output);

        } else if (expression instanceof UnaryExpression unary) {
            append(
                    output,
                    depth,
                    "UnaryExpression: "
                            + unary.getOperator().getSymbol()
            );
            printExpression(unary.getOperand(), depth + 1, output);

        } else if (expression instanceof LiteralExpression literal) {
            append(
                    output,
                    depth,
                    "Literal: " + literal.getLexeme()
                            + " [" + literal.getType() + "]"
            );

        } else if (expression instanceof VariableExpression variable) {
            append(output, depth, "Variable: " + variable.getName());
        }
    }

    private void append(StringBuilder output, int depth, String text) {
        output.append("  ".repeat(depth))
                .append(text)
                .append(System.lineSeparator());
    }
}