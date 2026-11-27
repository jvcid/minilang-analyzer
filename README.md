# MiniLang

Analisador léxico, sintático e semântico de uma mini-linguagem imperativa, implementado em Java com JFlex e Java CUP.

## Objetivo

O projeto demonstra a integração de um gerador léxico e um gerador sintático, a construção de uma árvore sintática e a aplicação de regras semânticas básicas. O analisador reconhece programas MiniLang, mostra a AST, preenche a tabela de símbolos e informa erros com linha e coluna.

## Linguagem

A MiniLang oferece:

- Declarações `int` e `bool`;
- Atribuições;
- Expressões aritméticas, relacionais e lógicas;
- Blocos `{}` com escopos aninhados;
- Estruturas `if`/`else` e `while`.

Os corpos de `if`, `else` e `while` devem ser blocos. Comentários de linha começam com `//`.

## Tecnologias

- Java 21 como versão de compilação;
- Maven;
- JFlex 1.9.1;
- Java CUP 11b-20160615-3.

## Estrutura

```text
src/main/java/br/edu/minilang/ast/          nós da árvore sintática
src/main/java/br/edu/minilang/semantics/    símbolos, escopos e análise semântica
src/main/java/br/edu/minilang/diagnostics/  registro e apresentação de erros
src/main/java/br/edu/minilang/output/       impressão da AST
src/main/jflex/Lexer.flex                   especificação do lexer
src/main/cup/Parser.cup                     gramática e ações do parser
examples/programa.minilang                  entrada válida demonstrativa
examples/invalid/                            entradas com erros
```

As classes `Lexer`, `Parser` e `sym` são geradas pelo Maven dentro de `target/generated-sources`.

## Compilar

Na raiz do projeto:

```powershell
mvn clean compile
```

## Executar

No IntelliJ, execute `br.edu.minilang.Main`, use a raiz do repositório como diretório de trabalho e informe o arquivo em **Program arguments**. Por exemplo:

```text
examples/programa.minilang
```

Sem argumento, `Main` usa `examples/programa.minilang` como entrada padrão. O programa imprime a AST, a tabela de símbolos e os diagnósticos encontrados.

## Exemplos de validação

| Arquivo | Resultado esperado |
|---|---|
| `examples/programa.minilang` | AST e tabela; sem erros |
| `examples/invalid/lexico.minilang` | Caractere inválido com posição |
| `examples/invalid/sintatico.minilang` | Erro por falta de `;` |
| `examples/invalid/semantico-tipo.minilang` | Inicializador incompatível com o tipo declarado |
| `examples/invalid/semantico-nao-declarada.minilang` | Uso de variável não declarada |

## Regras semânticas

A tabela de símbolos registra nome, tipo, posição da declaração e escopo. Um bloco cria um escopo filho. Variáveis de escopos ancestrais podem ser usadas em blocos internos; nomes podem ser ocultados em blocos internos, mas não redeclarados no mesmo escopo.

Operações aritméticas e comparações relacionais exigem operandos inteiros. Operações lógicas e condições de controle exigem valores booleanos. Atribuições e inicializadores devem ter o mesmo tipo da variável.

Consulte [`docs/relatorio.md`](docs/relatorio.md) para a gramática formal, descrição dos componentes e resultados registrados.
