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

public class ForloopStmtContext extends StmtContext {
    private TerminalContext FORTK_ = null;
    private TerminalContext LPARENT_ = null;
    private TerminalContext RPARENT_ = null;
    private TerminalContext SEMICN_1 = null;
    private TerminalContext SEMICN_2 = null;
    private CondContext cond = null;
    private ForStmtContext forStmt1 = null;
    private ForStmtContext forStmt2 = null;
    private StmtContext stmt = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof CondContext) {
            cond = (CondContext) context;
        } else if (context instanceof ForStmtContext) {
            if (forStmt1 == null) {
                forStmt1 = (ForStmtContext) context;
            } else {
                forStmt2 = (ForStmtContext) context;
            }
        } else if (context instanceof StmtContext) {
            stmt = (StmtContext) context;
        } else {
            ASSERT(false, "ForloopStmt only accepts CondContext, ForStmtContext, StmtContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(FORTK)) {
            FORTK_ = ctx;
        } else if (ctx.getToken().is(LPARENT)) {
            LPARENT_ = ctx;
        } else if (ctx.getToken().is(RPARENT)) {
            RPARENT_ = ctx;
        } else if (ctx.getToken().is(SEMICN)) {
            if (SEMICN_1 == null) {
                SEMICN_1 = ctx;
            } else {
                SEMICN_2 = ctx;
            }
        } else {
            ASSERT(false, "ForloopStmt only accepts FORTK or LPARENT or RPARENT or SEMICN");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
