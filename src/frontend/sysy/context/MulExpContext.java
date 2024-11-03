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

public class MulExpContext extends Context {
    private List<TerminalContext> OP_ = new ArrayList<>();
    private List<UnaryExpContext> unaryExp = new ArrayList<>();

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof UnaryExpContext) {
            unaryExp.add((UnaryExpContext) context);
        } else {
            ASSERT(false, "MulExp only accepts UnaryExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().any(MULT, MOD, DIV)) {
            OP_.add(ctx);
        } else {
            ASSERT(false, "MulExp only accepts MULT or DIV or MOD");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public List<UnaryExpContext> unaryExp() {
        return unaryExp;
    }

    public UnaryExpContext unaryExp(int index) {
        return unaryExp.get(index);
    }

    public List<TerminalContext> OP() {
        return OP_;
    }

    public TerminalContext OP(int index) {
        return OP_.get(index);
    }
}
