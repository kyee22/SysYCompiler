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

public class RelExpContext extends Context {
    private List<TerminalContext> OP_ = new ArrayList<>();
    private List<AddExpContext> addExp = new ArrayList<>();


    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof AddExpContext) {
            addExp.add((AddExpContext) context);
        } else {
            ASSERT(false, "RelExp only accepts AddExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().any(LSS, GRE, LEQ, GEQ)) {
            OP_.add(ctx);
        } else {
            ASSERT(false, "RelExp only accepts LSS or GRE or LEQ or GEQ");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
