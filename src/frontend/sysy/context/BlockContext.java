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

public class BlockContext extends Context {
    private TerminalContext LBRACE_ = null;
    private TerminalContext RBRACE_ = null;
    private List<BlockItemContext> blockItem = new ArrayList<>();

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof BlockItemContext) {
            blockItem.add((BlockItemContext) context);
        } else {
            ASSERT(false, "Block only accepts BlockItemContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(LBRACE)) {
            LBRACE_ = ctx;
        } else if (ctx.getToken().is(RBRACE)) {
            RBRACE_ = ctx;
        } else {
            ASSERT(false, "Block only accepts LBRACE or RBRACE");
        }
    }

    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
