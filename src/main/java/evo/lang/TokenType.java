package evo.lang;

public enum TokenType {
    IDENTIFIER,
    NUMBER,
    STRING,

    LET,
    FN,
    RETURN,
    IF,
    ELSE,
    WHILE,
    TRUE,
    FALSE,

    PLUS,
    MINUS,
    STAR,
    SLASH,
    EQUAL,
    EQUAL_EQUAL,
    BANG_EQUAL,
    LESS,
    LESS_EQUAL,
    GREATER,
    GREATER_EQUAL,

    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACE,
    RIGHT_BRACE,
    COMMA,
    SEMICOLON,

    EOF
}
