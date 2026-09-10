package evo.lang;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Evo {
    private Evo() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || "help".equals(args[0])) {
            printHelp();
            return;
        }
        if ("version".equals(args[0])) {
            System.out.println("EVO 0.1.0 — Phase 1 lexer");
            return;
        }
        if (!"lex".equals(args[0]) || args.length != 2) {
            System.err.println("Usage: java evo.lang.Evo lex <file.evo>");
            System.exit(2);
        }

        Path file = Path.of(args[1]);
        String source = Files.readString(file);
        List<Token> tokens = new Lexer(source).scanTokens();
        tokens.forEach(System.out::println);
    }

    private static void printHelp() {
        System.out.println("EVO — Self-Evolving Programming Language");
        System.out.println("  java evo.lang.Evo version");
        System.out.println("  java evo.lang.Evo lex <file.evo>");
    }
}
