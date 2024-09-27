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
import frontend.error.ErrorType;
import frontend.sysy.context.*;
import frontend.sysy.token.Token;
import frontend.sysy.token.TokenType;

import static frontend.sysy.token.TokenType.*;
import static frontend.error.ErrorType.*;
import static utils.AssertUtils.ASSERT;

import java.util.*;

public class Parser {

    private final LinkedList<Token> tokens;
    private Token token = null;
    private ListIterator<Token> iterator = null;
    private List<ErrorListener> errorListeners = new ArrayList<>();
    private Context ast = null;

    public Parser(List<Token> tokens) {
        this.tokens = new LinkedList<>(tokens);
        iterator = tokens.listIterator();
    }

    /**     TODO
     *          lineno √
     *          EOFTK
     *
     *          1. 修改 assert
     *          2. 修改 可能空指针
     */

    public void engine() {
        if (!iterator.hasNext()) {
            throw new RuntimeException("[Parser] Got empty tokens.");
        }
        token = iterator.next();
        ast = parseCompUnitContext();
    }

    public Context getAst() {
        return ast;
    }

    /**
     * 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
     * compUnit    : (decl)* (funcDef)* mainFuncDef EOF
     *             ;
     */
    public CompUnitContext parseCompUnitContext() {
        CompUnitContext compUnit = new CompUnitContext();

        // buggy!!! `nextToken` & `nextnextToken` are static!!
        // they should dynamicly update as 'while' loop goes on!!!
        //Token nextToken = peekNext(iterator, tokens, 1);
        //Token nextnextToken = peekNext(iterator, tokens, 2);
        while ((token.is(CONSTTK))
                || (token.any(INTTK, CHARTK) && peekNext(1).is(IDENFR) && !peekNext(2).is(LPARENT))) {
            compUnit.add(parseDeclContext());
        }
        while (token.any(INTTK, CHARTK, VOIDTK) && peekNext(1).is(IDENFR) && peekNext(2).is(LPARENT)) {
            compUnit.add(parseFuncDefContext());
        }
        compUnit.add(parseMainFuncDefContext());
        ASSERT(!iterator.hasNext() && token == null, "Expected EOF");

        return compUnit;
    }

    /**
     * 声明 Decl → ConstDecl | VarDecl
     * decl        : constDecl
     *             | varDecl
     *             ;
     */
    public DeclContext parseDeclContext() {
        if (token.is(CONSTTK)) {
            return parseConstDeclContext();
        } else  {
            return parseVarDeclContext();
        }
    }

    /**
     * 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
     * constDecl   : CONST bType constDef (COMMA constDef)* SEMICOLON
     *             ;
     */
    public ConstDeclContext parseConstDeclContext() {
        ConstDeclContext constDecl = new ConstDeclContext();

        constDecl.add(match(token, CONSTTK));
        constDecl.add(parseBTypeContext());
        constDecl.add(parseConstDefContext());

        while (token.is(COMMA)) {
            constDecl.add(match(token, COMMA));
            constDecl.add(parseConstDefContext());
        }

        if (token.is(SEMICN)) {
            constDecl.add(match(token, SEMICN));
        } else {
            // error handle by CATCH
            notifyErrorListeners(constDecl.getEndLineno(), MISSING_SEMICOLON);
        }
        return constDecl;
    }

    public BTypeContext parseBTypeContext() {
        BTypeContext bType = new BTypeContext();

        bType.add(match(token, INTTK, CHARTK));

        return bType;
    }

    public ConstDefContext parseConstDefContext() {
        ConstDefContext constDef = new ConstDefContext();

        constDef.add(match(token, IDENFR));

        if (token.is(LBRACK)) {
            constDef.add(match(token, LBRACK));
            constDef.add(parseConstExpContext());
            if (token.is(RBRACK)) {
                constDef.add(match(token, RBRACK));
            } else {
                notifyErrorListeners(constDef.getEndLineno(), MISSING_RBRACK);
            }
        }

        constDef.add(match(token, ASSIGN));
        constDef.add(parseConstInitValContext());
        return constDef;
    }

    public ConstInitValContext parseConstInitValContext() {
        ConstInitValContext constInitVal = new ConstInitValContext();

        if (token.is(STRCON)) {
            constInitVal.add(match(token, STRCON));
        } else if (token.is(LBRACE)) {
            constInitVal.add(match(token, LBRACE));
            // 试探 constExp
            if (token.any(IDENFR, INTCON, CHRCON, LPARENT, PLUS, MINU, NOT)) {
                constInitVal.add(parseConstExpContext());
                while (token.is(COMMA)) {
                    constInitVal.add(match(token, COMMA));
                    constInitVal.add(parseConstExpContext());
                }
            }
            constInitVal.add(match(token, RBRACE));
        } else {
            constInitVal.add(parseConstExpContext());
        }

        return constInitVal;
    }

    /**
     * 变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
     * varDecl     : bType varDef (COMMA varDef)* SEMICOLON
     *             ;
     */
    public VarDeclContext parseVarDeclContext() {
        VarDeclContext varDecl = new VarDeclContext();

        varDecl.add(parseBTypeContext());
        varDecl.add(parseVarDefContext());
        while (token.is(COMMA)) {
            varDecl.add(match(token, COMMA));
            varDecl.add(parseVarDefContext());
        }

        if (token.is(SEMICN)) {
            varDecl.add(match(token, SEMICN));
        } else {
            notifyErrorListeners(varDecl.getEndLineno(), MISSING_SEMICOLON);
        }

        return varDecl;
    }

    private VarDefContext parseVarDefContext() {
        VarDefContext varDef = new VarDefContext();

        varDef.add(match(token, IDENFR));
        if (token.is(LBRACK)) {
            varDef.add(match(token, LBRACK));
            varDef.add(parseConstExpContext());
            if (token.is(RBRACK)) {
                varDef.add(match(token, RBRACK));
            } else {
                notifyErrorListeners(varDef.getEndLineno(), MISSING_RBRACK);
            }
        }

        if (token.is(ASSIGN)) {
            varDef.add(match(token, ASSIGN));
            varDef.add(parseInitValContext());
        }

        return varDef;
    }

    private InitValContext parseInitValContext() {
        InitValContext initVal = new InitValContext();

        if (token.is(LBRACE)) {
            initVal.add(match(token, LBRACE));
            // 试探 exp
            if (token.any(IDENFR, INTCON, CHRCON, LPARENT, PLUS, MINU, NOT)) {
                initVal.add(parseExpContext());
                while (token.is(COMMA)) {
                    initVal.add(match(token, COMMA));
                    initVal.add(parseExpContext());
                }
            }
            initVal.add(match(token, RBRACE));
        } else if (token.is(STRCON)) {
            initVal.add(match(token, STRCON));
        } else {
            initVal.add(parseExpContext());
        }

        return initVal;
    }

    private FuncDefContext parseFuncDefContext() {
        FuncDefContext funcDef = new FuncDefContext();

        funcDef.add(parseFuncTypeContext());
        funcDef.add(match(token, IDENFR));
        funcDef.add(match(token, LPARENT));
        // 试探形参
        if (token.any(INTTK, CHARTK)) {
            funcDef.add(parseFuncFParamsContext());
        }
        if (token.is(RPARENT)) {
            funcDef.add(match(token, RPARENT));
        } else {
            notifyErrorListeners(funcDef.getEndLineno(), MISSING_RPARENT);
        }
        funcDef.add(parseBlockContext());

        return funcDef;
    }

    private MainFuncDefContext parseMainFuncDefContext() {
        MainFuncDefContext mainFuncDef = new MainFuncDefContext();

        mainFuncDef.add(match(token, INTTK));
        mainFuncDef.add(match(token, MAINTK));
        mainFuncDef.add(match(token, LPARENT));
        if (token.is(RPARENT)) {
            mainFuncDef.add(match(token, RPARENT));
        } else {
            notifyErrorListeners(mainFuncDef.getEndLineno(), MISSING_RPARENT);
        }
        mainFuncDef.add(parseBlockContext());

        return mainFuncDef;
    }

    private FuncTypeContext parseFuncTypeContext() {
        FuncTypeContext funcType = new FuncTypeContext();

        funcType.add(match(token, VOIDTK, INTTK, CHARTK));

        return funcType;
    }

    private FuncFParamsContext parseFuncFParamsContext() {
        FuncFParamsContext funcFParams = new FuncFParamsContext();

        funcFParams.add(parseFuncFParamContext());
        while (token.is(COMMA)) {
            funcFParams.add(match(token, COMMA));
            funcFParams.add(parseFuncFParamContext());
        }

        return funcFParams;
    }

    private FuncFParamContext parseFuncFParamContext() {
        FuncFParamContext funcFParam = new FuncFParamContext();

        funcFParam.add(parseBTypeContext());
        funcFParam.add(match(token, IDENFR));
        if (token.is(LBRACK)) {
            funcFParam.add(match(token, LBRACK));
            if (token.is(RBRACK)) {
                funcFParam.add(match(token, RBRACK));
            } else {
                notifyErrorListeners(funcFParam.getEndLineno(), MISSING_RBRACK);
            }
        }

        return funcFParam;
    }

    private BlockContext parseBlockContext() {
        BlockContext block = new BlockContext();

        block.add(match(token, LBRACE));
        // 试探 blockItem: 这样试探?
        //while (token.any(CONSTTK, INTTK, CHARTK, // 1) 试探 decl
        //        IDENFR, // 2) 试探 lVal
        //        IDENFR, STRCON, CHRCON, LPARENT, PLUS, MINU, NOT, // 3) 试探 exp
        //        SEMICN, // * exp 还可能是空的
        //        LBRACE, // 4) 试探 block
        //        IFTK, FORTK,
        //        BREAKTK, RETURNTK, CONTINUETK,
        //        GETINTTK, GETCHARTK, PRINTFTK)) {
        //    block.add(parseBlockItemContext());
        //}
        // 试探 blockItem: 或者这样试探?
        while (!token.is(RBRACE)) {
            block.add(parseBlockItemContext());
        }
        block.add(match(token, RBRACE));

        return block;
    }

    private BlockItemContext parseBlockItemContext() {
        BlockItemContext blockItem = new BlockItemContext();

        if (token.any(CONSTTK, INTTK, CHARTK)) {
            blockItem.add(parseDeclContext());
        } else {
            blockItem.add(parseStmtContext());
        }

        return blockItem;
    }

    private StmtContext parseStmtContext() {
        if (token.is(LBRACE)) {
            return parseBlockStmtContext();
        } else if (token.is(IFTK)) {
            return parseIfStmtContext();
        } else if (token.is(FORTK)) {
            return parseForloopStmtContext();
        } else if (token.is(BREAKTK)) {
            return parseBreakStmtContext();
        } else if (token.is(CONTINUETK)) {
            return parseContinueStmtContext();
        } else if (token.is(RETURNTK)) {
            return parseReturnStmtContext();
        } else if (token.is(PRINTFTK)) {
            return parsePrintfStmtContext();
        } else {
           ListIterator<Token> probeIterator = tokens.listIterator(iterator.nextIndex() - 1);
           boolean hasAssign = false;
           while (probeIterator.hasNext()) {
               Token probeToken = probeIterator.next();
               if (probeToken.is(ASSIGN)) {
                   hasAssign = true;
               }
               // todo
               if (!probeToken.any(IDENFR, LPARENT, INTCON, CHRCON, PLUS, MINU, NOT, MULT, DIV, MOD, RPARENT, LBRACK, RBRACK)) {
                   break;
               }
               if (probeToken.is(SEMICN)) {
                   break;
               }
           }
           return hasAssign ? parseAssignStmtContext() : parseExpStmtContext();
        }
    }


    private AssignStmtContext parseAssignStmtContext() {
        AssignStmtContext assignStmt = new AssignStmtContext();

        assignStmt.add(parseLValContext());
        assignStmt.add(match(token, ASSIGN));
        if (token.any(GETINTTK, GETCHARTK)) {
            assignStmt.add(match(token, GETINTTK, GETCHARTK));
            assignStmt.add(match(token, LPARENT));
            if (token.is(RPARENT)) {
                assignStmt.add(match(token, RPARENT));
            } else {
                notifyErrorListeners(assignStmt.getEndLineno(), MISSING_RPARENT);
            }
        } else {
            assignStmt.add(parseExpContext());
        }

        if (token.is(SEMICN)) {
            assignStmt.add(match(token, SEMICN));
        } else {
            notifyErrorListeners(assignStmt.getEndLineno(), MISSING_SEMICOLON);
        }

        return assignStmt;
    }

    private ExpStmtContext parseExpStmtContext() {
        ExpStmtContext expStmt = new ExpStmtContext();

        // 试探 exp
        if (token.any(IDENFR, INTCON, CHRCON, LPARENT, PLUS, MINU, NOT)) {
            expStmt.add(parseExpContext());
        }
        if (token.is(SEMICN)) {
            expStmt.add(match(token, SEMICN));
        } else {
            notifyErrorListeners(expStmt.getEndLineno(), MISSING_SEMICOLON);
        }

        return expStmt;
    }

    private BlockStmtContext parseBlockStmtContext() {
        BlockStmtContext blockStmt = new BlockStmtContext();

        blockStmt.add(parseBlockContext());

        return blockStmt;
    }

    private IfStmtContext parseIfStmtContext() {
        IfStmtContext ifStmt = new IfStmtContext();

        ifStmt.add(match(token, IFTK));
        ifStmt.add(match(token, LPARENT));
        ifStmt.add(parseCondContext());
        if (token.is(RPARENT)) {
            ifStmt.add(match(token, RPARENT));
        } else {
            notifyErrorListeners(ifStmt.getEndLineno(), MISSING_RPARENT);
        }
        ifStmt.add(parseStmtContext());
        if (token.is(ELSETK)) {
            ifStmt.add(match(token, ELSETK));
            ifStmt.add(parseStmtContext());
        }

        return ifStmt;
    }

    private ForloopStmtContext parseForloopStmtContext() {
        ForloopStmtContext forloopStmt = new ForloopStmtContext();

        forloopStmt.add(match(token, FORTK));
        forloopStmt.add(match(token, LPARENT));
        // 试探 forStmt
        if (token.is(IDENFR)) {
            forloopStmt.add(parseForStmtContext());
        }
        forloopStmt.add(match(token, SEMICN));
        // 试探 cond
        if (token.any(IDENFR, INTCON, CHRCON, LPARENT, PLUS, MINU, NOT)) {
            forloopStmt.add(parseCondContext());
        }
        forloopStmt.add(match(token, SEMICN));
        // 试探 forStmt
        if (token.is(IDENFR)) {
            forloopStmt.add(parseForStmtContext());
        }
        if (token.is(RPARENT)) {
            forloopStmt.add(match(token, RPARENT));
        } else {
            notifyErrorListeners(forloopStmt.getEndLineno(), MISSING_RPARENT);
        }
        forloopStmt.add(parseStmtContext());

        return forloopStmt;
    }

    private BreakStmtContext parseBreakStmtContext() {
        BreakStmtContext breakStmt = new BreakStmtContext();

        breakStmt.add(match(token, BREAKTK));
        if (token.is(SEMICN)) {
            breakStmt.add(match(token, SEMICN));
        } else {
            notifyErrorListeners(breakStmt.getEndLineno(), MISSING_SEMICOLON);
        }

        return breakStmt;
    }

    private ContinueStmtContext parseContinueStmtContext() {
        ContinueStmtContext continueStmt = new ContinueStmtContext();

        continueStmt.add(match(token, CONTINUETK));
        if (token.is(SEMICN)) {
            continueStmt.add(match(token, SEMICN));
        } else {
            notifyErrorListeners(continueStmt.getEndLineno(), MISSING_SEMICOLON);
        }

        return continueStmt;
    }

    private ReturnStmtContext parseReturnStmtContext() {
        ReturnStmtContext returnStmt = new ReturnStmtContext();

        returnStmt.add(match(token, RETURNTK));
        // 试探 exp
        if (token.any(IDENFR, INTCON, CHRCON, LPARENT, PLUS, MINU, NOT)) {
            returnStmt.add(parseExpContext());
        }
        if (token.is(SEMICN)) {
            returnStmt.add(match(token, SEMICN));
        } else {
            notifyErrorListeners(returnStmt.getEndLineno(), MISSING_SEMICOLON);
        }

        return returnStmt;
    }

    private PrintfStmtContext parsePrintfStmtContext() {
        PrintfStmtContext printfStmt = new PrintfStmtContext();

        printfStmt.add(match(token, PRINTFTK));
        printfStmt.add(match(token, LPARENT));
        printfStmt.add(match(token, STRCON));
        while (token.is(COMMA)) {
            printfStmt.add(match(token, COMMA));
            printfStmt.add(parseExpContext());
        }
        if (token.is(RPARENT)) {
            printfStmt.add(match(token, RPARENT));
        } else {
            notifyErrorListeners(printfStmt.getEndLineno(), MISSING_RPARENT);
        }
        if (token.is(SEMICN)) {
            printfStmt.add(match(token, SEMICN));
        } else {
            notifyErrorListeners(printfStmt.getEndLineno(), MISSING_SEMICOLON);
        }

        return printfStmt;
    }

    private ForStmtContext parseForStmtContext() {
        ForStmtContext forStmt = new ForStmtContext();

        forStmt.add(parseLValContext());
        forStmt.add(match(token, ASSIGN));
        forStmt.add(parseExpContext());

        return forStmt;
    }

    private ExpContext parseExpContext() {
        ExpContext exp = new ExpContext();

        exp.add(parseAddExpContext());

        return exp;
    }


    private CondContext parseCondContext() {
        CondContext cond = new CondContext();

        cond.add(parseLOrExpContext());

        return cond;
    }

    private LValContext parseLValContext() {
        LValContext lVal = new LValContext();

        lVal.add(match(token, IDENFR));
        if (token.is(LBRACK)) {
            lVal.add(match(token, LBRACK));
            lVal.add(parseExpContext());
            if (token.is(RBRACK)) {
                lVal.add(match(token, RBRACK));
            } else {
                notifyErrorListeners(lVal.getEndLineno(), MISSING_RBRACK);
            }
        }

        return lVal;
    }

    private PrimaryExpContext parsePrimaryExpContext() {
        PrimaryExpContext primaryExp = new PrimaryExpContext();

        if (token.is(LPARENT)) {
            primaryExp.add(match(token, LPARENT));
            primaryExp.add(parseExpContext());
            if (token.is(RPARENT)) {
                primaryExp.add(match(token, RPARENT));
            } else {
                notifyErrorListeners(primaryExp.getEndLineno(), MISSING_RPARENT);
            }
        } else if (token.is(IDENFR)) {
            primaryExp.add(parseLValContext());
        } else if (token.is(INTCON)) {
            primaryExp.add(parseNumberContext());
        } else if (token.is(CHRCON)) {
            primaryExp.add(parseCharacterContext());
        }

        return primaryExp;
    }

    private NumberContext parseNumberContext() {
        NumberContext number = new NumberContext();

        number.add(match(token, INTCON));

        return number;
    }

    private CharacterContext parseCharacterContext() {
        CharacterContext character = new CharacterContext();

        character.add(match(token, CHRCON));

        return character;
    }

    private UnaryExpContext parseUnaryExpContext() {
        UnaryExpContext unaryExp = new UnaryExpContext();

        if (token.is(IDENFR) && peekNext(iterator, tokens, 1).is(LPARENT)) {
            unaryExp.add(match(token, IDENFR));
            unaryExp.add(match(token, LPARENT));
            // 试探 exp
            if (token.any(IDENFR, LPARENT, CHRCON, INTCON, PLUS, MINU, NOT)) {
                unaryExp.add(parseFuncRParamsContext());
            }
            if (token.is(RPARENT)) {
                unaryExp.add(match(token, RPARENT));
            } else {
                notifyErrorListeners(unaryExp.getEndLineno(), MISSING_RPARENT);
            }
        } else if (token.any(PLUS, MINU, NOT)) {
            unaryExp.add(parseUnaryOpContext());
            unaryExp.add(parseUnaryExpContext());
        } else {
            unaryExp.add(parsePrimaryExpContext());
        }

        return unaryExp;
    }

    private UnaryOpContext parseUnaryOpContext() {
        UnaryOpContext unaryOp = new UnaryOpContext();

        unaryOp.add(match(token, PLUS, MINU, NOT));

        return unaryOp;
    }

    private FuncRParamsContext parseFuncRParamsContext() {
        FuncRParamsContext funcRParams = new FuncRParamsContext();

        funcRParams.add(parseExpContext());
        while (token.is(COMMA)) {
            funcRParams.add(match(token, COMMA));
            funcRParams.add(parseExpContext());
        }

        return funcRParams;
    }

    private MulExpContext parseMulExpContext() {
        MulExpContext mulExp = new MulExpContext();

        mulExp.add(parseUnaryExpContext());
        while (token.any(MULT, DIV, MOD)) {
            TerminalContext op = match(token, MULT, DIV, MOD);
            UnaryExpContext right = parseUnaryExpContext();

            MulExpContext tmp = new MulExpContext();
            tmp.add(mulExp);
            tmp.add(op);
            tmp.add(right);
            mulExp = tmp;
        }

        return mulExp;
    }

    private AddExpContext parseAddExpContext() {
        AddExpContext addExp = new AddExpContext();

        addExp.add(parseMulExpContext());
        while (token.any(PLUS, MINU)) {
            TerminalContext op = match(token, PLUS, MINU);
            MulExpContext right = parseMulExpContext();

            AddExpContext tmp = new AddExpContext();
            tmp.add(addExp);
            tmp.add(op);
            tmp.add(right);
            addExp = tmp;
        }

        return addExp;
    }

    private RelExpContext parseRelExpContext() {
        RelExpContext relExp = new RelExpContext();
        relExp.add(parseAddExpContext());

        while (token.any(LSS, GRE, LEQ, GEQ)) {
            TerminalContext op = match(token, LSS, GRE, LEQ, GEQ);
            AddExpContext right = parseAddExpContext();

            RelExpContext tmp = new RelExpContext();
            tmp.add(relExp);
            tmp.add(op);
            tmp.add(right);
            relExp = tmp;
        }

        return relExp;
    }

    private EqExpContext parseEqExpContext() {
        EqExpContext eqExp = new EqExpContext();

        eqExp.add(parseRelExpContext());
        while (token.any(EQL, NEQ)) {
            TerminalContext op = match(token, EQL, NEQ);
            RelExpContext right = parseRelExpContext();

            EqExpContext tmp = new EqExpContext();
            tmp.add(eqExp);
            tmp.add(op);
            tmp.add(right);
            eqExp = tmp;
        }

        return eqExp;
    }

    private LAndExpContext parseLAndExpContext() {
        LAndExpContext lAndExp = new LAndExpContext();

        lAndExp.add(parseEqExpContext());
        while (token.is(AND)) {
            TerminalContext op = match(token, AND);
            EqExpContext right = parseEqExpContext();

            LAndExpContext tmp = new LAndExpContext();
            tmp.add(lAndExp);
            tmp.add(op);
            tmp.add(right);
            lAndExp = tmp;
        }

        return lAndExp;
    }

    private LOrExpContext parseLOrExpContext() {
        LOrExpContext lOrExp = new LOrExpContext();

        lOrExp.add(parseLAndExpContext());
        while (token.is(OR)) {
            TerminalContext op = match(token, OR);
            LAndExpContext right = parseLAndExpContext();

            LOrExpContext tmp = new LOrExpContext();
            tmp.add(lOrExp);
            tmp.add(op);
            tmp.add(right);
            lOrExp = tmp;
        }

        return lOrExp;
    }

    private ConstExpContext parseConstExpContext() {
        ConstExpContext constExp = new ConstExpContext();

        constExp.add(parseAddExpContext());

        return constExp;
    }

    private TerminalContext match(Token token, TokenType... type) {
        ASSERT(token.any(type), "mismatch token: " + token.getText() + " at line " + token.getLineno() + ", expected " + type[0].toString() + " or ...");
        TerminalContext r = new TerminalContext(token);
        if (iterator.hasNext()) {
            this.token = iterator.next();
        } else {
            this.token = null;
        }
        return r;
    }

    public void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    private void notifyErrorListeners(int lineno, ErrorType errorType) {
        for (ErrorListener listener : errorListeners) {
            listener.onError(lineno, errorType);  // 通知所有监听者
        }
    }

    private Token peekNext(int k) {
        return peekNext(iterator, tokens, k);
    }

    public static <T> T peekNext(ListIterator<T> iterator, LinkedList<T> list, int k) {
        // 获取当前迭代器的索引
        int currentIndex = iterator.nextIndex();

        // 检查是否可以预览到第 k 个元素
        if (currentIndex + k - 1 < list.size()) {
            return list.get(currentIndex + k - 1); // 返回第 k 个元素
        } else {
            return null; // 如果没有足够的元素则返回 null
        }
    }
}
