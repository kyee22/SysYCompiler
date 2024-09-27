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

public class LOrExpContext extends Context {
    private TerminalContext OP_ = null;
    private LAndExpContext lAndExp = null;
    private LOrExpContext lOrExp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof LAndExpContext) {
            lAndExp = (LAndExpContext) context;
        } else if (context instanceof LOrExpContext) {
            lOrExp = (LOrExpContext) context;
        } else {
            ASSERT(false, "LOrExp only accepts LAndExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(OR)) {
            OP_ = ctx;
        } else {
            ASSERT(false, "LOrExp only accepts OR");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
