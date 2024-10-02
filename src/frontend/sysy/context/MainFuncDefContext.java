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

public class MainFuncDefContext extends Context {
    private TerminalContext INTTK_ = null;
    private TerminalContext MAINTK_ = null;
    private TerminalContext LPARENT_ = null;
    private TerminalContext RPARENT_ = null;
    private BlockContext block = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof BlockContext) {
            block = (BlockContext) context;
        } else {
            ASSERT(false, "MainFuncDef only accepts BlockContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(INTTK)) {
            INTTK_ = ctx;
        } else if (ctx.getToken().is(MAINTK)) {
            MAINTK_ = ctx;
        } else if (ctx.getToken().is(LPARENT)) {
            LPARENT_ = ctx;
        } else if (ctx.getToken().is(RPARENT)) {
            RPARENT_ = ctx;
        } else {
            ASSERT(false, "MainFuncDef only accepts INTTK or MAINTK or LPARENT or RPARENT");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public BlockContext block() {
        return block;
    }
}
