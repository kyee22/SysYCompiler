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

public class InitValContext extends Context {
    private TerminalContext LBRACE_ = null;
    private TerminalContext RBRACE_ = null;
    private List<TerminalContext> COMMA_ = new ArrayList<>();
    private TerminalContext STRCON_ = null;
    private List<ExpContext> exp = new ArrayList<>();

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof ExpContext) {
            exp.add((ExpContext) context);
        } else {
            ASSERT(false, "InitVal only accepts ExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(LBRACE)) {
            LBRACE_ = ctx;
        } else if (ctx.getToken().is(RBRACE)) {
            RBRACE_ = ctx;
        } else if (ctx.getToken().is(COMMA)) {
            COMMA_.add(ctx);
        } else if (ctx.getToken().is(STRCON)) {
            STRCON_ = ctx;
        } else {
            ASSERT(false, "InitVal only accepts LBRACE or RBRACE or COMMA or STRCON");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public List<ExpContext> exp() {
        return exp;
    }

    public ExpContext exp(int index) {
        return exp.get(index);
    }

    public TerminalContext LBRACE() {
        return LBRACE_;
    }

    public TerminalContext STRCON() {
        return STRCON_;
    }
}
