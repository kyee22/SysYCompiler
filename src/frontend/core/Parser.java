/*
 * SysYCompiler: A Compiler for SysY.
 *
 * SysYCompiler is an individually developed course project
 * for Compiling Techniques @ School of Computer Science &
 * Engineering, Beihang University, Fall 2024.
 *
 * Copyright (C) 2024 Yixuan Kuang <kyee22@buaa.edu.cn>
 *
 * This file is part of SysYCompiler.
 */

package frontend.core;

import frontend.error.ErrorListener;
import frontend.error.ErrorMessage;
import frontend.error.ErrorType;
import frontend.sysy.context.*;
import frontend.sysy.token.Token;
import frontend.sysy.token.TokenType;
import utils.DEBUG;

import static frontend.sysy.context.ContextType.*;
import static frontend.sysy.token.TokenType.*;
import static frontend.error.ErrorType.*;
import static utils.AssertUtils.ASSERT;

import java.util.*;
import java.util.function.Function;

public class Parser {
    /*
     *  The idea of designing parse rule in declarative-programming instead
     *  of objective-oriented-programming takes great inspiration from
     *  https://github.com/Toby-Shi-cloud/SysY-Compiler-2023
     *
     *  Compared to OOP, which focus on Object, functional programming which
     *  focus on Function, helps generate functions from functions to support
     *  declarative programming.
     */

    private final List<Token> tokens;
    private List<ErrorMessage> errors = new ArrayList<>();
    private int pos = 0;
    private List<ErrorListener> errorListeners = new ArrayList<>();
    private Context ast = null;

    private interface Generator extends Function<Parser, Optional<List<Context>>> {}

    private TerminalContext hardMatch(TokenType type) {
        if (pos >= tokens.size()) {
            DEBUG.println("HardMatch Overflow");
            return null;
        }
        if (tokens.get(pos).is(type)) {
            DEBUG.println("OK! Match " + type.toString() + " at line " + tokens.get(pos).getLineno() + ": " + tokens.get(pos).getText());
            return new TerminalContext(tokens.get(pos++));
        }
        DEBUG.println("HardMatch " + type.toString() + " at line " + tokens.get(pos).getLineno() + ": " + tokens.get(pos).getText());
        return null;
    }

    private TerminalContext softMatch(TokenType type, ErrorType errorType) {
        if (pos >= tokens.size()) {
            return null;
        }
        if (!tokens.get(pos).is(type)) {
            DEBUG.println("Oop! SoftMatch " + type.toString() + " at line " + tokens.get(pos).getLineno() + ": " + tokens.get(pos).getText());
            int lineno = tokens.get(pos - 1).getLineno();
            Token dummyToken = Token.makeToken(type, "", lineno);
            errors.add(new ErrorMessage(lineno, errorType));
            return new TerminalContext(dummyToken);
        } else {
            return hardMatch(type);
        }
    }

    private static Generator gen(TokenType type, ErrorType errorType) {
        return (Parser self) -> {
            Context terminal = self.softMatch(type, errorType);
            if (terminal != null) {
                List<Context> result = List.of(terminal);
                return Optional.of(result);
            }
            return Optional.empty();
        };
    }

    private static Generator gen(TokenType type) {
        return (Parser self) -> {
            Context terminal = self.hardMatch(type);
            if (terminal != null) {
                List<Context> result = List.of(terminal);
                return Optional.of(result);
            }
            return Optional.empty();
        };
    }

    private static Generator gen(ContextType type) {
        return (Parser self) -> {
            DEBUG.lprint("gen " + type + " ...");
            if (self.pos < self.tokens.size()) DEBUG.print(" at line " + self.tokens.get(self.pos).getLineno() + ": " + self.tokens.get(self.pos).getText()  + " \n");

            Context context = self.parse(type);
            if (context != null) {
                List<Context> result = List.of(context);
                DEBUG.rprintln("gen " + type.toString() + " ok!");
                return Optional.of(result);
            }

            DEBUG.rprintln("gen " + type + " failed!");
            return Optional.empty();
        };
    }

    private static Generator cat(Generator... generators) {
        return (Parser self) -> {
            // save the progress of parsing
            // & error handling for roll back
            int rollBack = self.pos;
            List<ErrorMessage> errors = List.copyOf(self.errors);
            List<Context> result = new ArrayList<>();

            for (Generator generator : generators) {
                Optional<List<Context>> currentResult = generator.apply(self);
                if (!currentResult.isPresent()) {
                    // if any branch fails, roll back
                    self.pos = rollBack; // 1: roll back the progress of parsing
                    self.errors.clear(); // 2: roll back the progress of error handling
                    self.errors.addAll(errors);
                    return Optional.empty();
                }
                result.addAll(currentResult.get());
            }
            return Optional.of(result);
        };
    }

    public static Generator or(Generator... generators) {
        return (Parser self) -> {
            for (Generator generator : generators) {
                Optional<List<Context>> result = generator.apply(self);
                if (result.isPresent()) {
                    return result;
                }
            }
            return Optional.empty();
        };
    }

    public static Generator option(Generator gen) {
        return (Parser self) -> {
            Optional<List<Context>> result = gen.apply(self);
            if (result.isPresent()) {
                return result;
            }
            return Optional.of(List.of());
        };
    }

    public static Generator any(Generator gen) {
        return (Parser self) -> {
            List<Context> result = new ArrayList<>();
            while (true) {
                Optional<List<Context>> child = gen.apply(self);
                if (child.isPresent()) {
                    result.addAll(child.get());
                } else {
                    break;
                }
            }
            return Optional.of(result);
        };
    }

    private Context generate(ContextType type, Generator generator) {
        Optional<List<Context>> result = generator.apply(this);
        if (!result.isPresent()) {
            return null;
        }
        Context context = switch (type) {
            case CompUnit -> new CompUnitContext();
            case Decl -> new DeclContext();
            case ConstDecl -> new ConstDeclContext();
            case BType -> new BTypeContext();
            case ConstDef -> new ConstDefContext();
            case ConstInitVal -> new ConstInitValContext();
            case VarDecl -> new VarDeclContext();
            case VarDef -> new VarDefContext();
            case InitVal -> new InitValContext();
            case FuncDef -> new FuncDefContext();
            case MainFuncDef -> new MainFuncDefContext();
            case FuncType -> new FuncTypeContext();
            case FuncFParams -> new FuncFParamsContext();
            case FuncFParam -> new FuncFParamContext();
            case Block -> new BlockContext();
            case BlockItem -> new BlockItemContext();
            case Stmt -> new StmtContext();
            case AssignStmt -> new AssignStmtContext();
            case ExpStmt -> new ExpStmtContext();
            case BlockStmt -> new BlockStmtContext();
            case IfStmt -> new IfStmtContext();
            case ForloopStmt -> new ForloopStmtContext();
            case BreakStmt -> new BreakStmtContext();
            case ContinueStmt -> new ContinueStmtContext();
            case ReturnStmt -> new ReturnStmtContext();
            case GetIntStmt -> new GetIntStmtContext();
            case GetCharStmt -> new GetCharStmtContext();
            case PrintfStmt -> new PrintfStmtContext();
            case ForStmt -> new ForStmtContext();
            case Exp -> new ExpContext();
            case Cond -> new CondContext();
            case LVal -> new LValContext();
            case PrimaryExp -> new PrimaryExpContext();
            case Number -> new NumberContext();
            case Character -> new CharacterContext();
            case UnaryExp -> new UnaryExpContext();
            case UnaryOp -> new UnaryOpContext();
            case FuncRParams -> new FuncRParamsContext();
            case MulExp -> new MulExpContext();
            case AddExp -> new AddExpContext();
            case RelExp -> new RelExpContext();
            case EqExp -> new EqExpContext();
            case LAndExp -> new LAndExpContext();
            case LOrExp -> new LOrExpContext();
            case ConstExp -> new ConstExpContext();
        };
        List<Context> children = result.get();

        DEBUG.println("generating " + type + " with " + children.size() + " children");
        for (Context child : children) {
            if (child instanceof TerminalContext) {
                context.add((TerminalContext) child);
            } else {
                context.add(child);
            }
        }
        return context;
    }

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void engine() {
        ast = parse(CompUnit);
        if (ast == null) {
            ASSERT(false, "Parse Error");
        }
        if (pos < tokens.size()) {
            ASSERT(false, "Expect EOF");
        }
        if (!errors.isEmpty()) {
            for (ErrorMessage error : errors) {
                notifyErrorListeners(error.getLineno(), error.getType());
            }
        }
    }

    public Context getAst() {
        return ast;
    }

    private Context parse(ContextType type) {
        Generator generator = switch (type) {
            // 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
            case CompUnit ->        cat(any(or(gen(FuncDef), gen(Decl))),
                                        gen(MainFuncDef));

            // 声明 Decl → ConstDecl | VarDecl
            case Decl ->             or(gen(ConstDecl),
                                        gen(VarDecl));

            // 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
            case ConstDecl ->       cat(gen(CONSTTK),
                                        gen(BType),
                                        gen(ConstDef),
                                        any(cat(gen(COMMA), gen(ConstDef))),
                                        gen(SEMICN, MISSING_SEMICOLON));

            // 基本类型 BType → 'int' | 'char'
            case BType ->            or(gen(INTTK),
                                        gen(CHARTK));

            // 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
            case ConstDef ->        cat(gen(IDENFR),
                                        option(cat(gen(LBRACK), gen(ConstExp), gen(RBRACK, MISSING_RBRACK))),
                                        gen(ASSIGN),
                                        gen(ConstInitVal));

            // 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
            case ConstInitVal ->     or(gen(ConstExp),
                                        cat(gen(LBRACE), option(cat(gen(ConstExp), any(cat(gen(COMMA), gen(ConstExp))))), gen(RBRACE)),
                                        gen(STRCON));

            // 变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
            case VarDecl ->         cat(gen(BType),
                                        gen(VarDef),
                                        any(cat(gen(COMMA), gen(VarDef))),
                                        gen(SEMICN, MISSING_SEMICOLON));

            // 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
            case VarDef ->          cat(gen(IDENFR),
                                        option(cat(gen(LBRACK), gen(ConstExp), gen(RBRACK, MISSING_RBRACK))),
                                        option(cat(gen(ASSIGN), gen(InitVal))));

            // 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
            case InitVal ->          or(gen(Exp),
                                        cat(gen(LBRACE), option(cat(gen(Exp), any(cat(gen(COMMA), gen(Exp))))), gen(RBRACE)),
                                        gen(STRCON));

            // 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
            case FuncDef ->         cat(gen(FuncType),
                                        gen(IDENFR),
                                        gen(LPARENT),
                                        option(gen(FuncFParams)),
                                        gen(RPARENT, MISSING_RPARENT),
                                        gen(Block));

            // 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
            case MainFuncDef ->     cat(gen(INTTK),
                                        gen(MAINTK),
                                        gen(LPARENT),
                                        gen(RPARENT, MISSING_RPARENT),
                                        gen(Block));

            // 函数类型 FuncType → 'void' | 'int' | 'char'
            case FuncType ->         or(gen(VOIDTK),
                                        gen(INTTK),
                                        gen(CHARTK));

            // 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
            case FuncFParams ->     cat(gen(FuncFParam),
                                        any(cat(gen(COMMA), gen(FuncFParam))));

            // 函数形参 FuncFParam → BType Ident ['[' ']']
            case FuncFParam ->      cat(gen(BType),
                                        gen(IDENFR),
                                        option(cat(gen(LBRACK), gen(RBRACK, MISSING_RBRACK))));

            // 语句块 Block → '{' { BlockItem } '}'
            case Block ->           cat(gen(LBRACE),
                                        any(gen(BlockItem)),
                                        gen(RBRACE));

            //语句块项 BlockItem → Decl | Stmt
            case BlockItem ->        or(gen(Decl),
                                        gen(Stmt));

            case Stmt ->             or(gen(AssignStmt),
                                        gen(BlockStmt),
                                        gen(IfStmt),
                                        gen(ForloopStmt),
                                        gen(BreakStmt),
                                        gen(ContinueStmt),
                                        gen(ReturnStmt),
                                        gen(GetIntStmt),
                                        gen(GetCharStmt),
                                        gen(PrintfStmt),
                                        gen(ExpStmt));

            case AssignStmt ->      cat(gen(LVal),
                                        gen(ASSIGN),
                                        gen(Exp),
                                        gen(SEMICN, MISSING_SEMICOLON));

            case ExpStmt ->          or(gen(SEMICN),
                                        cat(gen(Exp), gen(SEMICN, MISSING_SEMICOLON)));

            case BlockStmt ->           gen(Block);

            case IfStmt ->          cat(gen(IFTK),
                                        gen(LPARENT),
                                        gen(Cond),
                                        gen(RPARENT, MISSING_RPARENT),
                                        gen(Stmt),
                                        option(cat(gen(ELSETK), gen(Stmt))));

            case ForloopStmt ->     cat(gen(FORTK),
                                        gen(LPARENT),
                                        option(gen(ForStmt)),
                                        gen(SEMICN),
                                        option(gen(Cond)),
                                        gen(SEMICN),
                                        option(gen(ForStmt)),
                                        gen(RPARENT),
                                        gen(Stmt));

            case BreakStmt ->       cat(gen(BREAKTK),
                                        gen(SEMICN, MISSING_SEMICOLON));

            case ContinueStmt ->    cat(gen(CONTINUETK),
                                        gen(SEMICN, MISSING_SEMICOLON));

            case ReturnStmt ->      cat(gen(RETURNTK),
                                        option(gen(Exp)),
                                        gen(SEMICN, MISSING_SEMICOLON));

            case GetIntStmt ->     cat(gen(LVal),
                                       gen(ASSIGN),
                                       gen(GETINTTK),
                                       gen(LPARENT),
                                       gen(RPARENT, MISSING_RPARENT),
                                       gen(SEMICN, MISSING_SEMICOLON));

            case GetCharStmt ->     cat(gen(LVal),
                                        gen(ASSIGN),
                                        gen(GETCHARTK),
                                        gen(LPARENT),
                                        gen(RPARENT, MISSING_RPARENT),
                                        gen(SEMICN, MISSING_SEMICOLON));

            case PrintfStmt ->      cat(gen(PRINTFTK),
                                        gen(LPARENT),
                                        gen(STRCON),
                                        any(cat(gen(COMMA), gen(Exp))),
                                        gen(RPARENT, MISSING_RPARENT),
                                        gen(SEMICN, MISSING_SEMICOLON));

            // 语句 ForStmt → LVal '=' Exp
            case ForStmt ->         cat(gen(LVal),
                                        gen(ASSIGN),
                                        gen(Exp));

            // 表达式 Exp → AddExp
            case Exp ->                 gen(AddExp);

            // 条件表达式 Cond → LOrExp
            case Cond ->                gen(LOrExp);

            // 左值表达式 LVal → Ident ['[' Exp ']']
            case LVal ->            cat(gen(IDENFR),
                                        option(cat(gen(LBRACK), gen(Exp), gen(RBRACK, MISSING_RBRACK))));

            // 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character
            case PrimaryExp ->       or(cat(gen(LPARENT), gen(Exp), gen(RPARENT, MISSING_RPARENT)),
                                        gen(LVal),
                                        gen(Number),
                                        gen(Character));
            // 数值 Number → IntConst
            case Number ->              gen(INTCON);

            // 字符 Character → CharConst
            case Character ->           gen(CHRCON);

            // 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
            case UnaryExp ->         or(cat(gen(IDENFR), gen(LPARENT), option(gen(FuncRParams)), gen(RPARENT, MISSING_RPARENT)),
                                        gen(PrimaryExp),
                                        cat(gen(UnaryOp), gen(UnaryExp)));

            // 单目运算符 UnaryOp → '+' | '−' | '!'
            case UnaryOp ->          or(gen(PLUS),
                                        gen(MINU),
                                        gen(NOT));

            // 函数实参表 FuncRParams → Exp { ',' Exp }
            case FuncRParams ->     cat(gen(Exp),
                                        any(cat(gen(COMMA), gen(Exp))));

            // 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
            case MulExp ->          cat(gen(UnaryExp),
                                        any(cat(or(gen(MULT), gen(DIV), gen(MOD)), gen(UnaryExp))));

            // 加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
            case AddExp ->          cat(gen(MulExp),
                                        any(cat(or(gen(PLUS), gen(MINU)), gen(MulExp))));

            // 关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
            case RelExp ->          cat(gen(AddExp),
                                        any(cat(or(gen(LSS), gen(GRE), gen(LEQ), gen(GEQ)), gen(AddExp))));

            // 相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
            case EqExp ->           cat(gen(RelExp),
                                        any(cat(or(gen(EQL), gen(NEQ)), gen(RelExp))));

            // 逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
            case LAndExp ->         cat(gen(EqExp),
                                        any(cat(gen(AND), gen(EqExp))));

            // 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
            case LOrExp ->          cat(gen(LAndExp),
                                        any(cat(gen(OR), gen(LAndExp))));

            // 常量表达式 ConstExp → AddExp 注： 使用的 Ident 必须是常量
            case ConstExp ->            gen(AddExp);
        };
        return generate(type, generator);
    }

    public void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    private void notifyErrorListeners(int lineno, ErrorType errorType) {
        for (ErrorListener listener : errorListeners) {
            listener.onError(lineno, errorType);  // notify all listeners
        }
    }
}
