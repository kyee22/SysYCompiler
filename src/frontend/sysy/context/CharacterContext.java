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

public class CharacterContext extends Context {
    private TerminalContext CHRCON_ = null;

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(CHRCON)) {
            CHRCON_ = ctx;
        } else {
            ASSERT(false, "Character only accept CHRCON");
        }
    }

    @Override
    public void add(Context context) {
        ASSERT(false, "Character only accept Terminal Context");
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public TerminalContext CHRCON() {return CHRCON_;}
}
