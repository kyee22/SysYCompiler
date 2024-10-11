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

package frontend.sysy.context;

import static frontend.sysy.token.TokenType.*;
import static utils.AssertUtils.ASSERT;

public class UnaryExpContext extends Context {
    private TerminalContext LPARENT_ = null;
    private TerminalContext RPARENT_ = null;
    private TerminalContext IDENFR_ = null;
    private PrimaryExpContext primaryExp = null;
    private FuncRParamsContext funcRParams = null;
    private UnaryOpContext unaryOp = null;
    private UnaryExpContext unaryExp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof UnaryExpContext) {
            unaryExp = (UnaryExpContext) context;
        } else if (context instanceof FuncRParamsContext) {
            funcRParams = (FuncRParamsContext) context;
        } else if (context instanceof UnaryOpContext) {
            unaryOp = (UnaryOpContext) context;
        } else if (context instanceof PrimaryExpContext) {
            primaryExp = (PrimaryExpContext) context;
        } else {
            ASSERT(false, "UnaryExp only accept PrimaryExpContext or FuncRParamsContext or UnaryOpContext or UnaryExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(LPARENT)) {
            LPARENT_ = ctx;
        } else if (ctx.getToken().is(RPARENT)) {
            RPARENT_ = ctx;
        } else if (ctx.getToken().is(IDENFR)) {
            IDENFR_ = ctx;
        } else {
            ASSERT(false, "UnaryExp only accept LPAREN or RPAREN or IDENFR");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public TerminalContext IDENFR() {
        return IDENFR_;
    }

    public FuncRParamsContext funcRParams() {
        return funcRParams;
    }

    public PrimaryExpContext primaryExp() {
        return primaryExp;
    }

    public UnaryExpContext unaryExp() {
        return unaryExp;
    }
}

