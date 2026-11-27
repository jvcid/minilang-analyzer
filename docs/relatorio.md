# MiniLang: analisador léxico, sintático e semântico

## 1. Objetivo

Este trabalho apresenta um analisador integrado para uma mini-linguagem imperativa. O sistema utiliza JFlex para reconhecer tokens e Java CUP para reconhecer a estrutura sintática e construir uma árvore sintática abstrata (AST). Após a análise sintática, um analisador semântico verifica declarações, escopos e compatibilidade de tipos.

O programa não executa os comandos da linguagem. Sua finalidade é analisar o código-fonte, mostrar a árvore, apresentar a tabela de símbolos e apontar erros com suas posições.

## 2. Linguagem MiniLang

A linguagem diferencia maiúsculas de minúsculas. Seus tipos são `int` e `bool`. As palavras reservadas são `int`, `bool`, `true`, `false`, `if`, `else` e `while`.

Identificadores seguem `[A-Za-z_][A-Za-z0-9_]*`; literais inteiros são decimais não negativos. Comentários de linha começam com `//`. Os operadores disponíveis são `=`, `+`, `-`, `*`, `/`, `%`, `<`, `<=`, `>`, `>=`, `==`, `!=`, `&&`, `||` e `!`.

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

A separação das expressões por níveis estabelece precedência: operadores unários; multiplicação, divisão e resto; soma e subtração; comparações; igualdade; `&&`; e `||`. Os operadores binários de cada nível associam à esquerda.

Os corpos de `if`, `else` e `while` são blocos. Essa decisão elimina a ambiguidade de associação de `else` e mantém a gramática concisa.

## 4. Organização e funcionamento

O `Main` lê o arquivo informado ou, sem argumento, usa `examples/programa.minilang`. O lexer transforma o texto em símbolos com linha e coluna. O parser aplica a gramática e cria nós da AST. Se houver erro léxico ou sintático, os diagnósticos são apresentados e a análise semântica não prossegue. Caso contrário, a AST é impressa e o `SemanticAnalyzer` percorre os comandos.

Os nós da AST ficam em `br.edu.minilang.ast`; a tabela e o analisador semântico ficam em `br.edu.minilang.semantics`; o registro de erros fica em `br.edu.minilang.diagnostics`; e a impressão da árvore fica em `br.edu.minilang.output`. As classes `Lexer`, `Parser` e `sym` são geradas a partir de `Lexer.flex` e `Parser.cup` durante o ciclo Maven.

## 5. Tabela de símbolos e regras semânticas

Cada símbolo registra nome, tipo, linha e coluna da declaração, identificador do escopo e nível de aninhamento. O escopo global tem nível 0. Cada bloco cria um escopo filho. A busca procura primeiro no escopo atual e depois nos escopos ancestrais. Uma declaração interna pode ocultar uma externa; uma nova declaração com nome já usado no mesmo escopo é inválida.

As regras de tipos implementadas são:

| Construção | Regra |
|---|---|
| `+`, `-`, `*`, `/`, `%` | Dois operandos `INT`; resultado `INT` |
| `-` unário | Operando `INT`; resultado `INT` |
| `<`, `<=`, `>`, `>=` | Dois operandos `INT`; resultado `BOOL` |
| `==`, `!=` | Operandos do mesmo tipo; resultado `BOOL` |
| `&&`, `||`, `!` | Operandos `BOOL`; resultado `BOOL` |
| Atribuição e inicializador | Tipo da expressão igual ao da variável |
| Condição de `if` e `while` | Resultado `BOOL` |

O analisador reporta variáveis não declaradas, redeclarações no mesmo escopo e incompatibilidades de tipos. Declarações sem inicializador são permitidas; o analisador não executa o programa nem avalia divisões por zero.

## 6. Configuração e execução

O projeto usa Java 21 como versão de compilação, Maven, JFlex 1.9.1, Java CUP 11b-20160615-3 e `maven-compiler-plugin` 3.13.0. A especificação do lexer está em `src/main/jflex/Lexer.flex`; a gramática e as ações do parser, em `src/main/cup/Parser.cup`.

Compilação:

```powershell
mvn clean compile
```

Execução pela configuração **Application** do IntelliJ:

- Main class: `br.edu.minilang.Main`;
- Working directory: raiz do projeto;
- Program arguments: caminho do arquivo, por exemplo `examples/programa.minilang`.

Sem argumento, o arquivo `examples/programa.minilang` é utilizado por padrão.

## 7. Validação executada

Os casos abaixo foram executados pela classe `Main`. Os resultados são apresentados como diagnósticos observados no console.

| Caso | Entrada ou regra | Resultado observado |
|---|---|---|
| Válido | `examples/programa.minilang` | AST impressa; `total` e `ativo` em `E0`, `dobro` em `E1`; análise concluída sem erros |
| Léxico | `@` na linha 2 de `lexico.minilang` | `Léxico [2:1]: Caractere inválido: '@'.` |
| Sintático | Ponto e vírgula ausente em `sintatico.minilang` | `Sintático [1:15]: Token inesperado.` |
| Semântico: não declarada | `saldo = 10;` | `Semântico [1:1]: A variável 'saldo' não foi declarada.` |
| Semântico: tipo | `int idade = true;` | `Semântico [1:13]: O inicializador de 'idade' deve ter tipo INT, mas recebeu BOOL.` |

No caso semântico de tipo, a AST e a tabela também são exibidas antes do diagnóstico. No caso de variável não declarada, a tabela não contém símbolos porque o arquivo não declara variáveis.

## 8. Conclusão

O projeto integra JFlex e Java CUP a uma AST própria e a uma análise semântica com tabela de símbolos e escopos aninhados. Os exemplos executados demonstram entradas válidas e erros léxicos, sintáticos e semânticos com indicação de linha e coluna.
