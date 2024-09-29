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

public class ReturnStmtContext extends Context {
    private TerminalContext RETURNTK_ = null;
    private TerminalContext SEMICN_ = null;
    private ExpContext exp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof ExpContext) {
            exp = (ExpContext) context;
        } else {
            ASSERT(false, "ReturnStmt only accepts ExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(RETURNTK)) {
            RETURNTK_ = ctx;
        } else if (ctx.getToken().is(SEMICN)) {
            SEMICN_ = ctx;
        } else {
            ASSERT(false, "ReturnStmt only accepts RETURNTK or SEMICN");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
