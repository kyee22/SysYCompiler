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

public class AddExpContext extends Context {
    private TerminalContext OP_ = null;
    private MulExpContext mulExp = null;
    private AddExpContext addExp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof MulExpContext) {
            mulExp = (MulExpContext) context;
        } else if (context instanceof AddExpContext) {
            addExp = (AddExpContext) context;
        } else {
            ASSERT(false, "AddExp only accepts MulExpContext, AddExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().any(PLUS, MINU)) {
            OP_ = ctx;
        } else {
            ASSERT(false, "AddExp only accepts PLUS and MINU");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

}
