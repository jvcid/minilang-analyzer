# Relatório técnico: analisador léxico, sintático e semântico da MiniLang

## Repositório

[github.com/jvcid/minilang-analyzer](https://github.com/jvcid/minilang-analyzer)

## 1. Objetivo

Este trabalho apresenta um analisador integrado para uma mini-linguagem imperativa. O sistema utiliza JFlex para reconhecer tokens e Java CUP para reconhecer a estrutura sintática e construir uma árvore sintática abstrata (AST). Após a análise sintática, um analisador semântico verifica declarações, escopos e compatibilidade de tipos.

O programa não executa os comandos da linguagem. Sua finalidade é analisar o código-fonte, mostrar a árvore, apresentar a tabela de símbolos e apontar erros com suas posições.

## 2. Linguagem MiniLang

A linguagem diferencia maiúsculas de minúsculas. Seus tipos são `int` e `bool`. As palavras reservadas são `int`, `bool`, `true`, `false`, `if`, `else` e `while`.

Identificadores seguem `[A-Za-z_][A-Za-z0-9_]*`. Os literais inteiros são números decimais não negativos. Comentários de linha começam com `//`. A linguagem reconhece os operadores `=`, `+`, `-`, `*`, `/`, `%`, `<`, `<=`, `>`, `>=`, `==`, `!=`, `&&`, `||` e `!`.

Exemplo válido:

```text
int total = 10;
bool ativo = true;

{
    int dobro = total * 2;

    if (ativo && dobro >= 20) {
        total = dobro;
    } else {
        total = 0;
    }
}

while (total > 0) {
    total = total - 1;
}
```

## 3. Gramática livre de contexto

Os nomes em maiúsculas são terminais produzidos pelo lexer. `ε` representa a cadeia vazia. A gramática está escrita em BNF.

```text
programa          -> lista_comandos
lista_comandos    -> lista_comandos comando | ε
comando           -> declaracao | atribuicao | bloco | condicional | repeticao

declaracao        -> tipo IDENTIFICADOR inicializador_opcional PONTO_E_VIRGULA
tipo              -> INT | BOOL
inicializador_opcional -> ATRIBUICAO expressao | ε
atribuicao        -> IDENTIFICADOR ATRIBUICAO expressao PONTO_E_VIRGULA
bloco             -> ABRE_CHAVE lista_comandos FECHA_CHAVE
condicional       -> IF ABRE_PARENTESE expressao FECHA_PARENTESE bloco senao_opcional
senao_opcional    -> ELSE bloco | ε
repeticao         -> WHILE ABRE_PARENTESE expressao FECHA_PARENTESE bloco

expressao         -> expressao_ou
expressao_ou      -> expressao_ou OU_LOGICO expressao_e | expressao_e
expressao_e       -> expressao_e E_LOGICO igualdade | igualdade
igualdade         -> igualdade IGUAL relacional
                   | igualdade DIFERENTE relacional
                   | relacional
relacional        -> relacional MENOR aditiva
                   | relacional MENOR_IGUAL aditiva
                   | relacional MAIOR aditiva
                   | relacional MAIOR_IGUAL aditiva
                   | aditiva
aditiva           -> aditiva MAIS multiplicativa
                   | aditiva MENOS multiplicativa
                   | multiplicativa
multiplicativa    -> multiplicativa VEZES unaria
                   | multiplicativa DIVISAO unaria
                   | multiplicativa RESTO unaria
                   | unaria
unaria            -> NAO_LOGICO unaria | MENOS unaria | primaria
primaria          -> LITERAL_INTEIRO
                   | TRUE
                   | FALSE
                   | IDENTIFICADOR
                   | ABRE_PARENTESE expressao FECHA_PARENTESE
```

A separação das expressões por níveis define a precedência dos operadores. A ordem, do maior para o menor nível, é: operadores unários, multiplicação e divisão, soma e subtração, comparações, igualdade, `&&` e `||`. Os operadores binários de cada nível associam à esquerda.

Os corpos de `if`, `else` e `while` são blocos. Essa decisão elimina a ambiguidade de associação de `else` e mantém a gramática concisa.

## 4. Organização e funcionamento

O `Main` lê o arquivo informado ou, sem argumento, usa `examples/programa.minilang`. O lexer transforma o texto em símbolos com linha e coluna. O parser aplica a gramática e cria nós da AST. Se houver erro léxico ou sintático, os diagnósticos são apresentados e a análise semântica não prossegue. Caso contrário, a AST é impressa e o `SemanticAnalyzer` percorre os comandos.

Os nós da AST ficam em `br.edu.minilang.ast`. A tabela de símbolos e o analisador semântico ficam em `br.edu.minilang.semantics`. O registro de erros está em `br.edu.minilang.diagnostics`, enquanto a impressão da árvore é feita por `br.edu.minilang.output`. Durante o ciclo Maven, as classes `Lexer`, `Parser` e `sym` são geradas a partir de `Lexer.flex` e `Parser.cup`.

## 5. Tabela de símbolos e regras semânticas

Cada símbolo registra nome, tipo, linha e coluna da declaração, identificador do escopo e nível de aninhamento. O escopo global tem nível 0. Cada bloco cria um escopo filho. A busca começa no escopo atual e segue pelos escopos ancestrais. Uma declaração interna pode ocultar uma declaração externa. Uma variável não pode ser declarada novamente no mesmo escopo.

As regras de tipos implementadas são:

| Construção | Regra |
|---|---|
| `+`, `-`, `*`, `/`, `%` | Dois operandos `INT`, com resultado `INT` |
| `-` unário | Operando `INT`, com resultado `INT` |
| `<`, `<=`, `>`, `>=` | Dois operandos `INT`, com resultado `BOOL` |
| `==`, `!=` | Operandos do mesmo tipo, com resultado `BOOL` |
| `&&`, `||`, `!` | Operandos `BOOL`, com resultado `BOOL` |
| Atribuição e inicializador | Tipo da expressão igual ao da variável |
| Condição de `if` e `while` | Resultado `BOOL` |

O analisador reporta variáveis não declaradas, redeclarações no mesmo escopo e incompatibilidades de tipos. Declarações sem inicializador são permitidas. O analisador não executa o programa nem verifica divisões por zero.

## 6. Configuração e execução

O projeto usa Java 21, Maven, JFlex 1.9.1, Java CUP 11b-20160615-3 e `maven-compiler-plugin` 3.13.0. A especificação do lexer está em `src/main/jflex/Lexer.flex`. A gramática e as ações do parser estão em `src/main/cup/Parser.cup`.

Compilação:

```powershell
mvn clean compile
```

Execução pela configuração **Application** do IntelliJ:

- Main class: `br.edu.minilang.Main`
- Working directory: raiz do projeto
- Program arguments: caminho do arquivo, por exemplo `examples/programa.minilang`.

Sem argumento, o arquivo `examples/programa.minilang` é utilizado por padrão.

## 7. Validação executada

Os casos abaixo foram executados pela classe `Main`. Os resultados são apresentados como diagnósticos observados no console.

| Caso | Entrada ou regra | Resultado observado |
|---|---|---|
| Válido | `examples/programa.minilang` | AST impressa. `total` e `ativo` aparecem em `E0`, enquanto `dobro` aparece em `E1`. A análise termina sem erros. |
| Léxico | `@` na linha 2 de `lexico.minilang` | `Léxico [2:1]: Caractere inválido: '@'.` |
| Sintático | Ponto e vírgula ausente em `sintatico.minilang` | `Sintático [1:15]: Token inesperado.` |
| Semântico: não declarada | `saldo = 10;` | `Semântico [1:1]: A variável 'saldo' não foi declarada.` |
| Semântico: tipo | `int idade = true;` | `Semântico [1:13]: O inicializador de 'idade' deve ter tipo INT, mas recebeu BOOL.` |

No erro de tipo, a AST e a tabela são exibidas antes do diagnóstico. No caso da variável não declarada, a tabela fica vazia porque o arquivo não contém declarações.

## 8. Conclusão

O projeto integra JFlex e Java CUP a uma AST própria e a uma análise semântica com tabela de símbolos e escopos aninhados. Os exemplos executados demonstram entradas válidas e erros léxicos, sintáticos e semânticos com indicação de linha e coluna.
