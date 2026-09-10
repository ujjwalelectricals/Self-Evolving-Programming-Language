package evo.lang;

/** Compact deterministic AST printer for compiler debugging. */
public final class AstPrinter {
    public String print(Parser.Program program) {
        StringBuilder out = new StringBuilder();
        for (Parser.Stmt statement : program.statements()) printStmt(statement, out, 0);
        return out.toString();
    }

    private void printStmt(Parser.Stmt stmt, StringBuilder out, int depth) {
        indent(out, depth);
        switch (stmt) {
            case Parser.VarStmt v -> { out.append("let ").append(v.name().lexeme()); if (v.initializer()!=null) { out.append(" = "); printExpr(v.initializer(),out); } out.append(';').append('\n'); }
            case Parser.ExprStmt e -> { printExpr(e.expression(),out); out.append(';').append('\n'); }
            case Parser.ReturnStmt r -> { out.append("return"); if(r.value()!=null){out.append(' ');printExpr(r.value(),out);} out.append(';').append('\n'); }
            case Parser.FunctionStmt f -> { out.append("fn ").append(f.name().lexeme()).append("("); for(int i=0;i<f.parameters().size();i++){if(i>0)out.append(", ");out.append(f.parameters().get(i).lexeme());} out.append(") {\n"); for(Parser.Stmt child:f.body()) printStmt(child,out,depth+1); indent(out,depth); out.append("}\n"); }
            case Parser.IfStmt i -> { out.append("if "); printExpr(i.condition(),out); out.append(" {\n"); printStmt(i.thenBranch(),out,depth+1); indent(out,depth); out.append('}'); if(i.elseBranch()!=null){out.append(" else {\n"); printStmt(i.elseBranch(),out,depth+1); indent(out,depth); out.append('}');} out.append('\n'); }
            case Parser.WhileStmt w -> { out.append("while "); printExpr(w.condition(),out); out.append(" {\n"); printStmt(w.body(),out,depth+1); indent(out,depth); out.append("}\n"); }
            case Parser.BlockStmt b -> { out.append("{\n"); for(Parser.Stmt child:b.statements()) printStmt(child,out,depth+1); indent(out,depth); out.append("}\n"); }
        }
    }

    private void printExpr(Parser.Expr expr, StringBuilder out) {
        switch (expr) {
            case Parser.LiteralExpr l -> out.append(format(l.value()));
            case Parser.VariableExpr v -> out.append(v.name().lexeme());
            case Parser.GroupingExpr g -> { out.append('('); printExpr(g.expression(),out); out.append(')'); }
            case Parser.UnaryExpr u -> { out.append(u.operator().lexeme()); printExpr(u.right(),out); }
            case Parser.BinaryExpr b -> { out.append('('); printExpr(b.left(),out); out.append(' ').append(b.operator().lexeme()).append(' '); printExpr(b.right(),out); out.append(')'); }
            case Parser.AssignExpr a -> { out.append(a.name().lexeme()).append(" = "); printExpr(a.value(),out); }
            case Parser.CallExpr c -> { out.append(c.name().lexeme()).append('('); for(int i=0;i<c.arguments().size();i++){if(i>0)out.append(", ");printExpr(c.arguments().get(i),out);} out.append(')'); }
        }
    }

    private static String format(Object value){return value instanceof String ? "\""+value+"\"" : String.valueOf(value);}
    private static void indent(StringBuilder out,int depth){out.append("  ".repeat(depth));}
}
