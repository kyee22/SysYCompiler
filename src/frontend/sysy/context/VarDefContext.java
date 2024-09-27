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

public class VarDefContext extends Context {
    private TerminalContext IDENFR_ = null;
    private TerminalContext LBRACK_ = null;
    private TerminalContext RBRACK_ = null;
    private TerminalContext ASSIGN_ = null;
    private ConstExpContext constExp = null;
    private InitValContext initVal = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof ConstExpContext) {
            constExp = (ConstExpContext) context;
        } else if (context instanceof InitValContext) {
            initVal = (InitValContext) context;
        } else {
            ASSERT(false, "VarDef only accepts ConstExpContext or InitValContext, InitValContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(IDENFR)) {
            IDENFR_ = ctx;
        } else if (ctx.getToken().is(LBRACK)) {
            LBRACK_ = ctx;
        } else if (ctx.getToken().is(RBRACK)) {
            RBRACK_ = ctx;
        } else if (ctx.getToken().is(ASSIGN)) {
            ASSIGN_ = ctx;
        } else {
            ASSERT(false, "VarDef only accepts TerminalContext or IDENFR or LBRACK or RBRACK_or ASSIGN");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
