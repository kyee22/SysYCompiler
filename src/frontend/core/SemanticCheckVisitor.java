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
import frontend.sysy.typesystem.FuncType;
import frontend.sysy.typesystem.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static frontend.error.ErrorType.*;
import static utils.StringUtils.resolveNumOfPlaceHoders;

// todo: 考虑全局变量 bType, funcType,
//  在 visit bType, funcType 时设置

public class SemanticCheckVisitor extends BaseContextVisitor<Type> {

    private boolean isVoidReturn = false;
    private Stack<Boolean> loopStack = new Stack<>();
    public List<SymbolTable> symbolTables = new ArrayList<>();
    private List<ErrorListener> errorListeners = new ArrayList<>();
    private SymbolTable<Type> curScope = new SymbolTable(symbolTables.size()  + 1);
    private Type declType = null;
    private List<Type> paramTypesQueue = new ArrayList<>();

    public SemanticCheckVisitor() {
        symbolTables.add(curScope);
    }

    @Override
    public Type visit(BlockContext ctx) {
        if (!(ctx.getParent() instanceof FuncDefContext) && !(ctx.getParent() instanceof MainFuncDefContext)) {
            curScope = new SymbolTable(symbolTables.size()  + 1, curScope);
            symbolTables.add(curScope);
        }

        Type r = super.visit(ctx);

        curScope = curScope.getParent();
        return r;
    }

    @Override
    public Type visit(VarDeclContext ctx) {
        BTypeContext bType = ctx.bType();
        declType = bType.INTTK() != null ? Type.makeIntType()
                                         : Type.makeCharType();
        return super.visit(ctx);
    }

    @Override
    public Type visit(VarDefContext ctx) {
        Type r = super.visit(ctx);
        Token ident = ctx.IDENFR().getToken();
        if (curScope.localResolve(ident.getText()) != null) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), REDEF_SYM);
            return r;
        }

        Type type = declType;
        if (ctx.LBRACK() != null) {
            type = declType.isInt() ? Type.makeIntArrayType()
                                    : Type.makeCharArrayType();
        }
        curScope.define(ident.getText(), type);

        return r;
    }

    @Override
    public Type visit(ConstDeclContext ctx) {
        BTypeContext bType = ctx.bType();
        declType = bType.INTTK() != null ? Type.makeConstIntType()
                                         : Type.makeConstCharType();
        return super.visit(ctx);
    }

    @Override
    public Type visit(ConstDefContext ctx) {
        Type r = super.visit(ctx);
        Token ident = ctx.IDENFR().getToken();
        if (curScope.localResolve(ident.getText()) != null) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), REDEF_SYM);
            return r;
        }
        Type type = declType;
        if (ctx.LBRACK() != null) {
            type = declType.isConstInt() ? Type.makeConstIntArrayType()
                                         : Type.makeConstCharArrayType();
        }
        curScope.define(ident.getText(), type);

        return r;
    }

    @Override
    public Type visit(FuncDefContext ctx) {
        Token ident = ctx.IDENFR().getToken();
        if (curScope.localResolve(ident.getText()) != null) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), REDEF_SYM);
        }

        FuncTypeContext funcType = ctx.funcType();

        isVoidReturn = funcType.VOIDTK() != null;

        // 准备好内层作用域
        SymbolTable<Type> innerScope = new SymbolTable(symbolTables.size()  + 1, curScope);
        symbolTables.add(innerScope);
        // 先进入到内层作用域
        curScope = innerScope;
        paramTypesQueue.clear();
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
        }
        // 再回到全局作用域定义函数
        curScope = curScope.getParent();
        Type type = funcType.VOIDTK() != null ? Type.makeVoidFuncType(List.copyOf(paramTypesQueue)) :
                    funcType.INTTK()  != null ? Type.makeIntFuncType(List.copyOf(paramTypesQueue)) :
                                                Type.makeCharFuncType(List.copyOf(paramTypesQueue));
        if (curScope.localResolve(ident.getText()) == null) {
            curScope.define(ident.getText(), type);
        }
        // 再又一次进入到内层作用域
        curScope = innerScope;
        Type r = visit(ctx.block());

        BlockContext block = ctx.block();
        if (!isVoidReturn) {
            int nBlockItem = block.blockItem().size();
            if (nBlockItem == 0
                    || block.blockItem(nBlockItem - 1).stmt() == null
                    || block.blockItem(nBlockItem - 1).stmt().returnStmt() == null) {
                notifyErrorListeners(ctx.block().RBRACE().getToken().getLineno(), ctx.block().RBRACE().getToken().getColno(), RETURN_MISSING);
            }
        }

        return r;
    }

    @Override
    public Type visit(MainFuncDefContext ctx) {
        isVoidReturn = false;

        curScope = new SymbolTable(symbolTables.size()  + 1, curScope);
        symbolTables.add(curScope);
        paramTypesQueue.clear();

        Type r = super.visit(ctx);

        BlockContext block = ctx.block();
        if (!isVoidReturn) {
            int nBlockItem = block.blockItem().size();
            if (nBlockItem == 0
                    || block.blockItem(nBlockItem - 1).stmt() == null
                    || block.blockItem(nBlockItem - 1).stmt().returnStmt() == null) {
                notifyErrorListeners(ctx.block().RBRACE().getToken().getLineno(), ctx.block().RBRACE().getToken().getColno(), RETURN_MISSING);
            }
        }

        return r;
    }

    @Override
    public Type visit(FuncFParamContext ctx) {
        Token ident = ctx.IDENFR().getToken();
        if (curScope.localResolve(ident.getText()) != null) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), REDEF_SYM);
        }

        BTypeContext bType = ctx.bType();
        Type type = bType.INTTK() != null ? Type.makeIntType()
                                          : Type.makeCharType();
        if (ctx.LBRACK() != null) {
            type = type.isInt() ? Type.makeIntArrayType()
                                : Type.makeCharArrayType();
        }
        paramTypesQueue.add(type);//todo: REDEF时是略过还是加入到函数签名
        if (curScope.localResolve(ident.getText()) == null) {
            curScope.define(ident.getText(), type);
        }

        return super.visit(ctx);
    }

    @Override
    public Type visit(LValContext ctx) {
        super.visit(ctx);
        Token ident = ctx.IDENFR().getToken();
        if (curScope.globalResolve(ident.getText()) == null) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), UNDEF_SYM);
            return null;
        } else {
            Type type = curScope.globalResolve(ident.getText());
            if (ctx.LBRACK() != null) {
                type = type.isCharArray() ? Type.makeCharType()
                                          : Type.makeIntType();
            }
            return type;
        }
    }

    @Override
    public Type visit(UnaryExpContext ctx) {
        if (ctx.IDENFR() != null) { // 说明是 Call Exp
            Token ident = ctx.IDENFR().getToken();
            Type funcType = curScope.globalResolve(ident.getText());
            if (funcType == null || (!funcType.isVoidFunc() && !funcType.isCharFunc() && !funcType.isIntFunc())) {
                notifyErrorListeners(ident.getLineno(), ident.getColno(), UNDEF_SYM);
            } else {
                List<Type> paramTypes = ((FuncType) funcType).getParamTypes();
                int nArgs = ctx.funcRParams() == null ? 0 : ctx.funcRParams().exp().size();

                //if (ctx.funcRParams() != null) {
                //    visit(ctx.funcRParams());
                //}

                if (nArgs != paramTypes.size()) {
                    notifyErrorListeners(ident.getLineno(), ident.getColno(), PARAM_COUNT_MISMATCH);
                } else {
                    boolean match = true;
                    for (int i = 0; i < nArgs; i++) {
                        if (!Type.match(paramTypes.get(i), visit(ctx.funcRParams().exp(i)))) {
                            match = false;
                        }
                    }
                    if (!match) {
                        notifyErrorListeners(ident.getLineno(), ident.getColno(), PARAM_TYPE_MISMATCH);
                    }
                }

                return funcType.isVoidFunc() ? Type.makeVoidType() :
                       funcType.isCharFunc() ? Type.makeCharType() : Type.makeIntType();
            }
        } else if (ctx.unaryExp() != null) {
            return visit(ctx.unaryExp());
        } else if (ctx.primaryExp() != null) {
            return visit(ctx.primaryExp());
        }
        return null;
    }

    @Override
    public Type visit(AssignStmtContext ctx) {
        Type r = super.visit(ctx);

        Token ident = ctx.lVal().IDENFR().getToken();
        Type type = curScope.globalResolve(ident.getText());
        if (type != null && type.isConst()) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), CONST_ASSIGN_ERROR);
        }

        return r;
    }

    @Override
    public Type visit(GetIntStmtContext ctx) {
        Type r = super.visit(ctx);

        Token ident = ctx.lVal().IDENFR().getToken();
        Type type = curScope.globalResolve(ident.getText());
        if (type != null && type.isConst()) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), CONST_ASSIGN_ERROR);
        }

        return r;
    }

    @Override
    public Type visit(GetCharStmtContext ctx) {
        Type r = super.visit(ctx);

        Token ident = ctx.lVal().IDENFR().getToken();
        Type type = curScope.globalResolve(ident.getText());
        if (type != null && type.isConst()) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), CONST_ASSIGN_ERROR);
        }

        return r;
    }

    @Override
    public Type visit(ForStmtContext ctx) {
        Type r = super.visit(ctx);

        Token ident = ctx.lVal().IDENFR().getToken();
        Type type = curScope.globalResolve(ident.getText());
        if (type != null && type.isConst()) {
            notifyErrorListeners(ident.getLineno(), ident.getColno(), CONST_ASSIGN_ERROR);
        }

        return r;
    }

    @Override
    public Type visit(ReturnStmtContext ctx) {
        if (isVoidReturn && ctx.exp() != null) {
            Token token = ctx.RETURNTK().getToken();
            notifyErrorListeners(token.getLineno(), token.getColno(), RETURN_MISMATCH_VOID);
        }

        return super.visit(ctx);
    }

    @Override
    public Type visit(ForloopStmtContext ctx) {

        loopStack.push(true);
        Type r = super.visit(ctx);
        loopStack.pop();

        return r;
    }

    @Override
    public Type visit(BreakStmtContext ctx) {
        if (loopStack.isEmpty()) {
            Token token = ctx.BREAKTK().getToken();
            notifyErrorListeners(token.getLineno(), token.getColno(), BREAK_CONTINUE_OUTSIDE_LOOP);
        }

        return super.visit(ctx);
    }

    @Override
    public Type visit(ContinueStmtContext ctx) {
        if (loopStack.isEmpty()) {
            Token token = ctx.CONTINUETK().getToken();
            notifyErrorListeners(token.getLineno(), token.getColno(), BREAK_CONTINUE_OUTSIDE_LOOP);
        }

        return super.visit(ctx);
    }

    @Override
    public Type visit(PrintfStmtContext ctx) {
        if (resolveNumOfPlaceHoders(ctx.STRCON().getToken().getText()) != ctx.exp().size()) {
            Token token = ctx.PRINTFTK().getToken();
            notifyErrorListeners(token.getLineno(), token.getColno(), FORMAT_COUNT_MISMATCH);
        }

        return super.visit(ctx);
    }

    // For the sake of back propagation of type in expressions
    @Override
    public Type visit(NumberContext ctx) {
        super.visit(ctx);
        return Type.makeIntType();
    }

    @Override
    public Type visit(CharacterContext ctx) {
        super.visit(ctx);
        return Type.makeCharType();
    }

    @Override
    public Type visit(PrimaryExpContext ctx) {
        if (ctx.exp() != null) {
            return visit(ctx.exp());
        } else if (ctx.lVal() != null) {
            return visit(ctx.lVal());
        } else if (ctx.number() != null) {
            return visit(ctx.number());
        } else if (ctx.character() != null) {
            return visit(ctx.character());
        }

        return null;
    }

    @Override
    public Type visit(MulExpContext ctx) {
        if (ctx.unaryExp().size() == 1) {
            return visit(ctx.unaryExp(0));
        }
        boolean hasVoidOprand = false;
        for (UnaryExpContext unaryExp : ctx.unaryExp()) {
            Type type = visit(unaryExp);
            if (type == null || type.isVoid()) {
                hasVoidOprand = true;
            }
        }
        return hasVoidOprand ? Type.makeVoidType() : Type.makeIntType();
    }

    @Override
    public Type visit(AddExpContext ctx) {
        if (ctx.mulExp().size() == 1) {
            return visit(ctx.mulExp(0));
        }
        boolean hasVoidOprand = false;
        for (MulExpContext mulExp : ctx.mulExp()) {
            Type type = visit(mulExp);
            if (type == null || type.isVoid()) {
                hasVoidOprand = true;
            }
        }
        return hasVoidOprand ? Type.makeVoidType() : Type.makeIntType();
    }

    public void addErrorListener(ErrorListener listener) {
        errorListeners.add(listener);
    }

    private void notifyErrorListeners(int lineno, int colno, ErrorType errorType) {
        for (ErrorListener listener : errorListeners) {
            listener.onError(lineno, colno, errorType);  // notify all listeners
        }
    }

    public List<SymbolTable> getSymbolTables() {
        return symbolTables;
    }
}
