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

public class ForStmtContext extends Context {
    private LValContext lVal = null;
    private TerminalContext ASSIGN_ = null;
    private ExpContext exp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof LValContext) {
            lVal = (LValContext) context;
        } else if (context instanceof ExpContext) {
            exp = (ExpContext) context;
        } else {
            ASSERT(false, "ForStmt only accepts LValContext, ExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(ASSIGN)) {
            ASSIGN_ = ctx;
        } else {
            System.out.println(ctx.getToken().getText() + " at line " + ctx.getToken().getLineno());
            ASSERT(false, "ForStmt only accepts ASSIGN");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public LValContext lVal() {
        return lVal;
    }

    public ExpContext exp() {
        return exp;
    }
}
