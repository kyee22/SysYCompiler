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

import static utils.AssertUtils.ASSERT;


public class ConstExpContext extends Context {
    private AddExpContext addExp = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof AddExpContext) {
            addExp = (AddExpContext) context;
        } else {
            ASSERT(false, "ConstExp only accepts AddExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        ASSERT(false, "ConstExp doesn't accept TerminalContext");
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
