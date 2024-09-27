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

public class PrimaryExpContext extends Context {
    private TerminalContext LPARENT_ = null;
    private TerminalContext RPARENT_ = null;
    private ExpContext exp = null;
    private LValContext lVal = null;
    private NumberContext number = null;
    private CharacterContext character = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof LValContext) {
            lVal = (LValContext) context;
        } else if (context instanceof NumberContext) {
            number = (NumberContext) context;
        } else if (context instanceof CharacterContext) {
            character = (CharacterContext) context;
        } else if (context instanceof ExpContext) {
            exp = (ExpContext) context;
        } else {
            ASSERT(false, "PrimaryExp only accepts LValContext, NumberContext, CharacterContext, ExpContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(LPARENT)) {
            LPARENT_ = ctx;
        } else if (ctx.getToken().is(RPARENT)) {
            RPARENT_ = ctx;
        } else {
            ASSERT(false, "PrimaryExp only accepts LPARENT or RPARENT");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
