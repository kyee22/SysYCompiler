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

public class UnaryOpContext extends Context {
    private TerminalContext PLUS_ = null;
    private TerminalContext MINU_ = null;
    private TerminalContext NOT_ = null;


    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(PLUS)) {
            PLUS_ = ctx;
        } else if (ctx.getToken().is(MINU)) {
            MINU_ = ctx;
        } else if (ctx.getToken().is(NOT)) {
            NOT_ = ctx;
        } else {
            ASSERT(false, "UnaryOp only accept PLUS or MINU or NOT");
        }
    }

    @Override
    public void add(Context context) {
        ASSERT(false, "UnaryOp doesn't accept Non-Terminal Context");
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public TerminalContext PLUS() {return PLUS_;}
    public TerminalContext MINUS() {return MINU_;}
}
