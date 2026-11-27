package br.edu.minilang.lexer;

import br.edu.minilang.diagnostics.Diagnostic;
import br.edu.minilang.diagnostics.DiagnosticReporter;
import br.edu.minilang.parser.sym;
import java.io.Reader;
import java_cup.runtime.Symbol;

%%

%public
%class Lexer
%unicode
%cup
%line
%column

%{
    private DiagnosticReporter diagnostics = new DiagnosticReporter();

    public Lexer(Reader reader, DiagnosticReporter diagnostics) {
        this(reader);
        this.diagnostics = diagnostics;
    }

    private Symbol symbol(int token) {
        return new Symbol(token, yyline + 1, yycolumn + 1);
    }

    private Symbol symbol(int token, Object value) {
        return new Symbol(token, yyline + 1, yycolumn + 1, value);
    }
%}

Identifier = [A-Za-z_][A-Za-z0-9_]*
Integer    = 0|[1-9][0-9]*
Whitespace = [ \t\f]+

%eofval{
    return symbol(sym.EOF);
%eofval}

%%

"int"           { return symbol(sym.INT); }
"bool"          { return symbol(sym.BOOL); }
"true"          { return symbol(sym.TRUE); }
"false"         { return symbol(sym.FALSE); }
"if"            { return symbol(sym.IF); }
"else"          { return symbol(sym.ELSE); }
"while"         { return symbol(sym.WHILE); }

"=="            { return symbol(sym.IGUAL); }
"!="            { return symbol(sym.DIFERENTE); }
"<="            { return symbol(sym.MENOR_IGUAL); }
">="            { return symbol(sym.MAIOR_IGUAL); }
"&&"            { return symbol(sym.E_LOGICO); }
"||"            { return symbol(sym.OU_LOGICO); }

"="             { return symbol(sym.ATRIBUICAO); }
"<"             { return symbol(sym.MENOR); }
">"             { return symbol(sym.MAIOR); }
"+"             { return symbol(sym.MAIS); }
"-"             { return symbol(sym.MENOS); }
"*"             { return symbol(sym.VEZES); }
"/"             { return symbol(sym.DIVISAO); }
"%"             { return symbol(sym.RESTO); }
"!"             { return symbol(sym.NAO_LOGICO); }

"("             { return symbol(sym.ABRE_PARENTESE); }
")"             { return symbol(sym.FECHA_PARENTESE); }
"{"             { return symbol(sym.ABRE_CHAVE); }
"}"             { return symbol(sym.FECHA_CHAVE); }
";"             { return symbol(sym.PONTO_E_VIRGULA); }

"//"[^\r\n]*    { /* comentário de linha */ }
{Whitespace}    { /* ignorar espaços e tabulações */ }
\r\n|\r|\n      { /* yyline e yycolumn acompanham as posições */ }

0[0-9]+ {
    diagnostics.report(
        Diagnostic.Phase.LEXICAL,
        yyline + 1,
        yycolumn + 1,
        "Literal inteiro não pode começar com zero."
    );
}

{Integer} {
    return symbol(sym.LITERAL_INTEIRO, yytext());
}

{Identifier} {
    return symbol(sym.IDENTIFICADOR, yytext());
}

. {
    diagnostics.report(
        Diagnostic.Phase.LEXICAL,
        yyline + 1,
        yycolumn + 1,
        "Caractere inválido: '" + yytext() + "'."
    );
}