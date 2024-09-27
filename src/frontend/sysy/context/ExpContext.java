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

import static utils.AssertUtils.ASSERT;

public class ExpContext extends Context {
    private AddExpContext addExp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof AddExpContext) {
            addExp = (AddExpContext) context;
        } else {
            ASSERT(false, "Exp only accepts AddExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        ASSERT(false, "Exp only accepts Non-Terminal Context");
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
