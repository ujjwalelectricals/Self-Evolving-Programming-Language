package evo.lang;

import java.util.List;

public final class LexerSelfTest {
    public static void main(String[] args) {
        String source = "let x = 12.5; // comment\nif (x >= 10) { return x != 0; }\n";
        List<Token> tokens = new Lexer(source).scanTokens();

        require(tokens.get(0).type() == TokenType.LET, "let keyword");
        require(tokens.get(1).type() == TokenType.IDENTIFIER, "identifier");
        require(tokens.get(2).type() == TokenType.EQUAL, "assignment");
        require(tokens.get(3).type() == TokenType.NUMBER, "number");
        require(Double.valueOf(12.5).equals(tokens.get(3).literal()), "number literal");
        require(tokens.stream().anyMatch(t -> t.type() == TokenType.GREATER_EQUAL), ">=");
        require(tokens.stream().anyMatch(t -> t.type() == TokenType.BANG_EQUAL), "!=");
        require(tokens.get(tokens.size() - 1).type() == TokenType.EOF, "EOF");

        System.out.println("LexerSelfTest: PASS (" + tokens.size() + " tokens)");
    }

    private static void require(boolean condition, String name) {
        if (!condition) throw new AssertionError("LexerSelfTest failed: " + name);
    }
}
