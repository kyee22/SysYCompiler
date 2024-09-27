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

import java.util.ArrayList;
import java.util.List;

import static frontend.sysy.token.TokenType.*;
import static utils.AssertUtils.ASSERT;

public class LAndExpContext extends Context {
    private TerminalContext OP_ = null;
    private EqExpContext eqExp = null;
    private LAndExpContext lAndExp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof EqExpContext) {
            eqExp = (EqExpContext) context;
        } else if (context instanceof LAndExpContext) {
            lAndExp = (LAndExpContext) context;
        } else {
            ASSERT(false, "LAndExp only accepts EqExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(AND)) {
            OP_ = ctx;
        } else {
            ASSERT(false, "LAndExp only accepts AND");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
