package evo.lang;

import java.util.List;

public final class ParserSelfTest {
    public static void main(String[] args) {
        String source = "let x = 10 + 2 * 3; fn add(a, b) { return a + b; } if (x >= 16) { x = add(x, 1); } else { x = 0; } while (x != 20) { x = x + 1; }";
        Parser.Program program = new Parser(new Lexer(source).scanTokens()).parse();
        require(program.statements().size() == 4, "top-level statement count");
        require(program.statements().get(0) instanceof Parser.VarStmt, "let statement");
        require(program.statements().get(1) instanceof Parser.FunctionStmt, "function statement");
        require(program.statements().get(2) instanceof Parser.IfStmt, "if statement");
        require(program.statements().get(3) instanceof Parser.WhileStmt, "while statement");

        Parser.VarStmt var = (Parser.VarStmt) program.statements().get(0);
        require(var.initializer() instanceof Parser.BinaryExpr, "operator precedence AST");
        Parser.BinaryExpr plus = (Parser.BinaryExpr) var.initializer();
        require(plus.operator().type() == TokenType.PLUS, "outer plus");
        require(plus.right() instanceof Parser.BinaryExpr, "multiply nested under plus");

        expectFailure("let = 3;", "Expected variable name after 'let'.");
        expectFailure("if (true { let x = 1; }", "Expected ')' after condition.");
        expectFailure("fn f(a a) { return a; }", "Expected ')' after parameters.");
        expectFailure("3 = 4;", "Invalid assignment target.");

        System.out.println("ParserSelfTest: PASS");
    }

    private static void expectFailure(String source, String expected) {
        try {
            new Parser(new Lexer(source).scanTokens()).parse();
            throw new AssertionError("Expected parser failure: " + expected);
        } catch (IllegalArgumentException ex) {
            if (!ex.getMessage().contains(expected)) throw new AssertionError("Wrong parser error: " + ex.getMessage());
        }
    }

    private static void require(boolean condition, String name) {
        if (!condition) throw new AssertionError("ParserSelfTest failed: " + name);
    }
}
