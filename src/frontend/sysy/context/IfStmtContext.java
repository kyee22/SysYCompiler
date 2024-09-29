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

public class IfStmtContext extends Context {
    private TerminalContext IFTK_ = null;
    private TerminalContext ELSETK_ = null;
    private TerminalContext LPARENT_ = null;
    private TerminalContext RPARENT_ = null;
    private CondContext cond = null;
    private StmtContext ifStmt = null;
    private StmtContext elseStmt = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof CondContext) {
            cond = (CondContext) context;
        } else if (context instanceof StmtContext) {
            if (ifStmt == null) {
                ifStmt = (StmtContext) context;
            } else {
                elseStmt = (StmtContext) context;
            }
        } else {
            ASSERT(false, "IfStmt only accepts CondContext, StmtContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(IFTK)) {
            IFTK_ = ctx;
        } else if (ctx.getToken().is(LPARENT)) {
            LPARENT_ = ctx;
        } else if (ctx.getToken().is(RPARENT)) {
            RPARENT_ = ctx;
        } else if (ctx.getToken().is(ELSETK)) {
            ELSETK_ = ctx;
        } else {
            ASSERT(false, "IfStmt only accepts IFTK or LPARENT or RPARENT or ELSETK");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
