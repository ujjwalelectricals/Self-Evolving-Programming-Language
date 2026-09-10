package evo.lang;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Evo {
    private Evo() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || "help".equals(args[0])) { printHelp(); return; }
        Path file;
        String source;
        switch (args[0]) {
            case "version" -> { System.out.println("EVO 0.2.0 — Phase 2 parser + AST"); return; }
            case "lex", "parse" -> {
                if (args.length != 2) { System.err.println("Usage: java evo.lang.Evo " + args[0] + " <file.evo>"); System.exit(2); }
                file = Path.of(args[1]);
                source = Files.readString(file);
            }
            default -> { System.err.println("Unknown command: " + args[0]); System.exit(2); return; }
        }

        List<Token> tokens = new Lexer(source).scanTokens();
        if ("lex".equals(args[0])) {
            tokens.forEach(System.out::println);
            return;
        }
        Parser.Program program = new Parser(tokens).parse();
        System.out.print(new AstPrinter().print(program));
    }

    private static void printHelp() {
        System.out.println("EVO — Self-Evolving Programming Language");
        System.out.println("  java evo.lang.Evo version");
        System.out.println("  java evo.lang.Evo lex <file.evo>");
        System.out.println("  java evo.lang.Evo parse <file.evo>");
    }
}
