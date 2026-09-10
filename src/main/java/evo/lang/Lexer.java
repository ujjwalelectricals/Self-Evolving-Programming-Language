package evo.lang;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Phase 1 lexer for EVO. Dependency-free and deterministic. */
public final class Lexer {
    private static final Map<String, TokenType> KEYWORDS = Map.of(
            "let", TokenType.LET, "fn", TokenType.FN, "return", TokenType.RETURN,
            "if", TokenType.IF, "else", TokenType.ELSE, "while", TokenType.WHILE,
            "true", TokenType.TRUE, "false", TokenType.FALSE
    );

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start;
    private int current;
    private int line = 1;
    private int column = 1;
    private int tokenColumn = 1;

    public Lexer(String source) {
        this.source = source == null ? "" : source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            tokenColumn = column;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line, column));
        return List.copyOf(tokens);
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(' -> add(TokenType.LEFT_PAREN);
            case ')' -> add(TokenType.RIGHT_PAREN);
            case '{' -> add(TokenType.LEFT_BRACE);
            case '}' -> add(TokenType.RIGHT_BRACE);
            case ',' -> add(TokenType.COMMA);
            case ';' -> add(TokenType.SEMICOLON);
            case '+' -> add(TokenType.PLUS);
            case '-' -> add(TokenType.MINUS);
            case '*' -> add(TokenType.STAR);
            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    add(TokenType.SLASH);
                }
            }
            case '=' -> add(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
            case '!' -> {
                if (match('=')) add(TokenType.BANG_EQUAL);
                else fail("Unexpected '!'. Use '!='.");
            }
            case '<' -> add(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
            case '>' -> add(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
            case ' ', '\r', '\t' -> { }
            case '\n' -> { line++; column = 1; }
            case '"' -> string();
            default -> {
                if (isDigit(c)) number();
                else if (isIdentifierStart(c)) identifier();
                else fail("Unexpected character '" + c + "'.");
            }
        }
    }

    private void identifier() {
        while (isIdentifierPart(peek())) advance();
        String text = source.substring(start, current);
        add(KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER));
    }

    private void number() {
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
        }
        String lexeme = source.substring(start, current);
        add(TokenType.NUMBER, Double.parseDouble(lexeme));
    }

    private void string() {
        StringBuilder value = new StringBuilder();
        while (!isAtEnd() && peek() != '"') {
            char c = advance();
            if (c == '\n') {
                line++;
                column = 1;
            } else if (c == '\\' && !isAtEnd()) {
                char escaped = advance();
                switch (escaped) {
                    case 'n' -> value.append('\n');
                    case 'r' -> value.append('\r');
                    case 't' -> value.append('\t');
                    case '"' -> value.append('"');
                    case '\\' -> value.append('\\');
                    default -> fail("Unsupported escape sequence: \\" + escaped);
                }
            } else {
                value.append(c);
            }
        }
        if (isAtEnd()) fail("Unterminated string.");
        advance();
        add(TokenType.STRING, value.toString());
    }

    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        advance();
        return true;
    }

    private char peek() { return isAtEnd() ? '\0' : source.charAt(current); }
    private char peekNext() { return current + 1 >= source.length() ? '\0' : source.charAt(current + 1); }
    private char advance() { char c = source.charAt(current++); column++; return c; }
    private boolean isAtEnd() { return current >= source.length(); }
    private static boolean isDigit(char c) { return c >= '0' && c <= '9'; }
    private static boolean isIdentifierStart(char c) { return Character.isLetter(c) || c == '_'; }
    private static boolean isIdentifierPart(char c) { return Character.isLetterOrDigit(c) || c == '_'; }
    private void add(TokenType type) { add(type, null); }
    private void add(TokenType type, Object literal) { tokens.add(new Token(type, source.substring(start, current), literal, line, tokenColumn)); }
    private void fail(String message) { throw new IllegalArgumentException("Lexer error at " + line + ":" + tokenColumn + ": " + message); }
}
