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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static frontend.error.ErrorType.*;
import static utils.StringUtils.resolveNumOfFormat;

public class SemanticCheckVisitor extends BaseContextVisitor<Void> {

    private boolean isVoidReturn = false;
    private boolean hasReturnExp = false;
    private Stack<Boolean> loopStack = new Stack<>();
    private List<ErrorListener> errorListeners = new ArrayList<>();


    @Override
    public Void visit(FuncDefContext ctx) {
        FuncTypeContext funcType = ctx.funcType();
        isVoidReturn = funcType.VOIDTK() != null;
        hasReturnExp = false;

        Void r = super.visit(ctx);

        // todo: int 函数 return;的话?
        BlockContext block = ctx.block();
        if (!isVoidReturn) {
            int nBlockItem = block.blockItem().size();
            if (nBlockItem == 0
                    || block.blockItem(nBlockItem - 1).stmt() == null
                    || block.blockItem(nBlockItem - 1).stmt().returnStmt() == null) {
                notifyErrorListeners(ctx.block().RBRACE().getToken().getLineno(), RETURN_MISSING);
            }
        }

        return r;
    }

    @Override
    public Void visit(MainFuncDefContext ctx) {
        isVoidReturn = false;
        hasReturnExp = false;

        Void r = super.visit(ctx);

        // todo: int 函数 return;的话?
        BlockContext block = ctx.block();
        if (!isVoidReturn) {
            int nBlockItem = block.blockItem().size();
            if (nBlockItem == 0
                    || block.blockItem(nBlockItem - 1).stmt() == null
                    || block.blockItem(nBlockItem - 1).stmt().returnStmt() == null) {
                notifyErrorListeners(ctx.block().RBRACE().getToken().getLineno(), RETURN_MISSING);
            }
        }

        return r;
    }

    @Override
    public Void visit(ReturnStmtContext ctx) {
        hasReturnExp = ctx.exp() != null;

        if (isVoidReturn && hasReturnExp) {
            notifyErrorListeners(ctx.RETURNTK().getToken().getLineno(), RETURN_MISMATCH_VOID);
        }

        return super.visit(ctx);
    }

    @Override
    public Void visit(ForloopStmtContext ctx) {

        loopStack.push(true);
        Void r = super.visit(ctx);
        loopStack.pop();

        return r;
    }

    @Override
    public Void visit(BreakStmtContext ctx) {
        if (loopStack.isEmpty()) {
            notifyErrorListeners(ctx.BREAKTK().getToken().getLineno(), BREAK_CONTINUE_OUTSIDE_LOOP);
        }

        return super.visit(ctx);
    }

    @Override
    public Void visit(ContinueStmtContext ctx) {
        if (loopStack.isEmpty()) {
            notifyErrorListeners(ctx.CONTINUETK().getToken().getLineno(), BREAK_CONTINUE_OUTSIDE_LOOP);
        }

        return super.visit(ctx);
    }

    @Override
    public Void visit(PrintfStmtContext ctx) {
        if (resolveNumOfFormat(ctx.STRCON().getToken().getText()) != ctx.exp().size()) {
            notifyErrorListeners(ctx.PRINTFTK().getToken().getLineno(), FORMAT_COUNT_MISMATCH);
        }

        return super.visit(ctx);
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
