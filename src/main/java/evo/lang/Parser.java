package evo.lang;

import java.util.ArrayList;
import java.util.List;

/** Recursive-descent parser for EVO Phase 2. */
public final class Parser {
    private final List<Token> tokens;
    private int current;

    public Parser(List<Token> tokens) { this.tokens = tokens == null ? List.of() : tokens; }
    public Program parse() { List<Stmt> statements = new ArrayList<>(); while (!isAtEnd()) statements.add(declaration()); return new Program(statements); }
    private Stmt declaration() { if (match(TokenType.LET)) return letStatement(previous()); if (match(TokenType.FN)) return functionDeclaration(previous()); return statement(); }
    private Stmt letStatement(Token keyword) { Token name=consume(TokenType.IDENTIFIER,"Expected variable name after 'let'."); Expr init=match(TokenType.EQUAL)?expression():null; consume(TokenType.SEMICOLON,"Expected ';' after variable declaration."); return new VarStmt(name,init,keyword.line(),keyword.column()); }
    private Stmt functionDeclaration(Token keyword) { Token name=consume(TokenType.IDENTIFIER,"Expected function name."); consume(TokenType.LEFT_PAREN,"Expected '(' after function name."); List<Token> params=new ArrayList<>(); if(!check(TokenType.RIGHT_PAREN)){ do{params.add(consume(TokenType.IDENTIFIER,"Expected parameter name."));}while(match(TokenType.COMMA)); } consume(TokenType.RIGHT_PAREN,"Expected ')' after parameters."); consume(TokenType.LEFT_BRACE,"Expected '{' before function body."); return new FunctionStmt(name,params,block(),keyword.line(),keyword.column()); }
    private Stmt statement(){ if(match(TokenType.RETURN)){Token k=previous(); Expr v=check(TokenType.SEMICOLON)?null:expression(); consume(TokenType.SEMICOLON,"Expected ';' after return value."); return new ReturnStmt(v,k.line(),k.column());} if(match(TokenType.IF))return ifStatement(previous()); if(match(TokenType.WHILE))return whileStatement(previous()); if(match(TokenType.LEFT_BRACE))return new BlockStmt(block()); Expr e=expression(); consume(TokenType.SEMICOLON,"Expected ';' after expression."); return new ExprStmt(e); }
    private Stmt ifStatement(Token keyword){consume(TokenType.LEFT_PAREN,"Expected '(' after 'if'."); Expr c=expression(); consume(TokenType.RIGHT_PAREN,"Expected ')' after condition."); Stmt t=statement(); Stmt e=match(TokenType.ELSE)?statement():null; return new IfStmt(c,t,e,keyword.line(),keyword.column());}
    private Stmt whileStatement(Token keyword){consume(TokenType.LEFT_PAREN,"Expected '(' after 'while'."); Expr c=expression(); consume(TokenType.RIGHT_PAREN,"Expected ')' after condition."); return new WhileStmt(c,statement(),keyword.line(),keyword.column());}
    private List<Stmt> block(){List<Stmt>s=new ArrayList<>(); while(!check(TokenType.RIGHT_BRACE)&&!isAtEnd())s.add(declaration()); consume(TokenType.RIGHT_BRACE,"Expected '}' after block."); return s;}
    private Expr expression(){return assignment();}
    private Expr assignment(){Expr e=equality(); if(match(TokenType.EQUAL)){Token eq=previous(); Expr v=assignment(); if(e instanceof VariableExpr var)return new AssignExpr(var.name(),v,eq.line(),eq.column()); throw error(eq,"Invalid assignment target.");} return e;}
    private Expr equality(){Expr e=comparison(); while(match(TokenType.EQUAL_EQUAL,TokenType.BANG_EQUAL)){Token o=previous(); e=new BinaryExpr(e,o,comparison());} return e;}
    private Expr comparison(){Expr e=term(); while(match(TokenType.LESS,TokenType.LESS_EQUAL,TokenType.GREATER,TokenType.GREATER_EQUAL)){Token o=previous(); e=new BinaryExpr(e,o,term());} return e;}
    private Expr term(){Expr e=factor(); while(match(TokenType.PLUS,TokenType.MINUS)){Token o=previous(); e=new BinaryExpr(e,o,factor());} return e;}
    private Expr factor(){Expr e=unary(); while(match(TokenType.STAR,TokenType.SLASH)){Token o=previous(); e=new BinaryExpr(e,o,unary());} return e;}
    private Expr unary(){if(match(TokenType.MINUS))return new UnaryExpr(previous(),unary()); return call();}
    private Expr call(){Expr e=primary(); while(match(TokenType.LEFT_PAREN)){List<Expr>a=new ArrayList<>(); if(!check(TokenType.RIGHT_PAREN)){do{a.add(expression());}while(match(TokenType.COMMA));} Token close=consume(TokenType.RIGHT_PAREN,"Expected ')' after arguments."); if(!(e instanceof VariableExpr v))throw error(close,"Only named functions can be called in Phase 2."); e=new CallExpr(v.name(),a,close.line(),close.column());} return e;}
    private Expr primary(){if(match(TokenType.FALSE))return new LiteralExpr(false); if(match(TokenType.TRUE))return new LiteralExpr(true); if(match(TokenType.NUMBER,TokenType.STRING))return new LiteralExpr(previous().literal()); if(match(TokenType.IDENTIFIER))return new VariableExpr(previous()); if(match(TokenType.LEFT_PAREN)){Expr e=expression(); consume(TokenType.RIGHT_PAREN,"Expected ')' after expression."); return new GroupingExpr(e);} throw error(peek(),"Expected expression.");}
    private boolean match(TokenType...types){for(TokenType t:types)if(check(t)){advance();return true;}return false;}
    private Token consume(TokenType t,String m){if(check(t))return advance();throw error(peek(),m);}
    private boolean check(TokenType t){return !isAtEnd()&&peek().type()==t;}
    private Token advance(){if(!isAtEnd())current++;return previous();}
    private boolean isAtEnd(){return peek().type()==TokenType.EOF;}
    private Token peek(){return tokens.isEmpty()?new Token(TokenType.EOF,"",null,1,1):tokens.get(Math.min(current,tokens.size()-1));}
    private Token previous(){return tokens.get(current-1);}
    private IllegalArgumentException error(Token t,String m){return new IllegalArgumentException("Parser error at "+t.line()+":"+t.column()+": "+m);}

    public record Program(List<Stmt> statements) {}
    public sealed interface Stmt permits VarStmt,ExprStmt,FunctionStmt,ReturnStmt,IfStmt,WhileStmt,BlockStmt {}
    public record VarStmt(Token name,Expr initializer,int line,int column) implements Stmt {}
    public record ExprStmt(Expr expression) implements Stmt {}
    public record FunctionStmt(Token name,List<Token> parameters,List<Stmt> body,int line,int column) implements Stmt {}
    public record ReturnStmt(Expr value,int line,int column) implements Stmt {}
    public record IfStmt(Expr condition,Stmt thenBranch,Stmt elseBranch,int line,int column) implements Stmt {}
    public record WhileStmt(Expr condition,Stmt body,int line,int column) implements Stmt {}
    public record BlockStmt(List<Stmt> statements) implements Stmt {}
    public sealed interface Expr permits BinaryExpr,UnaryExpr,LiteralExpr,GroupingExpr,VariableExpr,AssignExpr,CallExpr {}
    public record BinaryExpr(Expr left,Token operator,Expr right) implements Expr {}
    public record UnaryExpr(Token operator,Expr right) implements Expr {}
    public record LiteralExpr(Object value) implements Expr {}
    public record GroupingExpr(Expr expression) implements Expr {}
    public record VariableExpr(Token name) implements Expr {}
    public record AssignExpr(Token name,Expr value,int line,int column) implements Expr {}
    public record CallExpr(Token name,List<Expr>arguments,int line,int column) implements Expr {}
}
