# EVO — Self-Evolving Programming Language

EVO is a programming language project built from scratch with one long-term goal: a compiler/runtime that can experiment with optimization strategies, measure real performance, and eventually evolve better optimization policies.

The implementation is intentionally dependency-free. The compiler toolchain is written in Java 21+ so it can be built and run on Windows with a JDK and no package manager or external runtime.

## Current status

### Phase 1 — Lexer ✅

The front end tokenizes EVO source with identifiers/keywords, numbers, strings, comments, operators, punctuation, source locations, and deterministic lexer errors.

### Phase 2 — Parser + AST ✅

Tokens are now parsed into a typed abstract syntax tree using a recursive-descent parser. Phase 2 supports:

- variable declarations: `let name = expression;`
- assignments
- function declarations and parameters
- return statements
- `if` / `else`
- `while`
- blocks
- unary minus
- arithmetic and comparison precedence
- grouping with parentheses
- named function calls with arguments
- deterministic parser errors with source locations
- a stable AST printer for compiler debugging

Pipeline:

```text
EVO source
   ↓
Lexer
   ↓
Tokens
   ↓
Parser
   ↓
AST
```

Example:

```evo
let answer = 42;
let message = "Hello from EVO";

fn add(a, b) {
    return a + b;
}

if (answer >= 42) {
    print(message);
}
```

## Run on Windows

Requirements:

- JDK 21 or newer
- PowerShell

Build:

```powershell
.\build.ps1
```

Run lexer + parser regression tests:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-tests.ps1
```

Lex a program:

```powershell
java -cp .\out evo.lang.Evo lex .\examples\hello.evo
```

Parse and print its AST:

```powershell
java -cp .\out evo.lang.Evo parse .\examples\hello.evo
```

Show version:

```powershell
java -cp .\out evo.lang.Evo version
```

## Architecture roadmap

```text
Phase 1   Lexer                         ✅
Phase 2   Parser + AST                  ✅
Phase 3   Semantic analysis             🚧
Phase 4   Bytecode / IR
Phase 5   Virtual machine
Phase 6   Baseline optimizer
Phase 7   Benchmark harness
Phase 8   Competing optimization passes
Phase 9   Evolution/search engine
Phase 10  Self-improving optimization
```

EVO will not claim to be “self-evolving” merely because it has a genetic algorithm. The end goal is measurable: generate or select optimization strategies, benchmark them against reproducible programs, keep strategies that improve the objective, and prevent regressions with correctness tests.

## Design principles

1. Correctness before optimization.
2. Deterministic tests and reproducible benchmarks.
3. No hidden dependence on network services or paid APIs.
4. Every performance improvement must preserve language semantics.
5. Keep compiler stages isolated so experiments can be swapped without rewriting the whole toolchain.
6. Treat optimization as an empirical problem: measure first, then evolve.

## Project structure

```text
src/
  main/java/evo/lang/
    TokenType.java
    Token.java
    Lexer.java
    Parser.java
    AstPrinter.java
    Evo.java
  test/java/evo/lang/
    LexerSelfTest.java
    ParserSelfTest.java
examples/
  hello.evo
build.ps1
run-tests.ps1
```

## License

MIT
