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
import static frontend.sysy.token.TokenType.RPARENT;
import static utils.AssertUtils.ASSERT;

public class GetCharStmtContext extends Context {
    private TerminalContext ASSIGN_ = null;
    private TerminalContext SEMICN_ = null;
    private TerminalContext GETCHARTK_ = null;
    private TerminalContext LPARENT_ = null;
    private TerminalContext RPARENT_ = null;
    private LValContext lVal = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof LValContext) {
            lVal = (LValContext) context;
        } else {
            ASSERT(false, "GetChar only accepts LValContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(ASSIGN)) {
            ASSIGN_ = ctx;
        } else if (ctx.getToken().is(SEMICN)) {
            SEMICN_ = ctx;
        } else if (ctx.getToken().is(GETCHARTK)) {
            GETCHARTK_ = ctx;
        } else if (ctx.getToken().is(GETCHARTK)) {
        } else if (ctx.getToken().is(LPARENT)) {
            LPARENT_ = ctx;
        } else if (ctx.getToken().is(RPARENT)) {
            RPARENT_ = ctx;
        } else {
            ASSERT(false, "GetChar only accepts ASSIGN or SEMICN or GETCHARTK or LPARENT or RPARENT");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
