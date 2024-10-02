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

public class FuncTypeContext extends Context {
    private TerminalContext VOIDTK_ = null;
    private TerminalContext INTTK_ = null;
    private TerminalContext CHARTK_ = null;

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(VOIDTK)) {
            VOIDTK_ = ctx;
        } else if (ctx.getToken().is(INTTK)) {
            INTTK_ = ctx;
        } else if (ctx.getToken().is(CHARTK)) {
            CHARTK_ = ctx;
        } else {
            ASSERT(false, "FuncType only accepts VOIDTK or INTTK or CHARTK");
        }
    }

    @Override
    public void add(Context context) {
        ASSERT(false, "FuncType only accepts Terminal Context");
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public TerminalContext VOIDTK() {
        return VOIDTK_;
    }

    public TerminalContext INTTK() {
        return INTTK_;
    }

    public TerminalContext CHARTK() {
        return CHARTK_;
    }
}
