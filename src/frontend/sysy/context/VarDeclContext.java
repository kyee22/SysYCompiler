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

public class VarDeclContext extends Context {
    public List<TerminalContext> COMMA_ = new ArrayList<>();
    public TerminalContext SEMICN_ = null;
    public BTypeContext bType = null;
    public List<VarDefContext> varDef = new ArrayList<>();

    @Override
    public void add(TerminalContext ctx) {
        super.add(ctx);
        if (ctx.getToken().is(COMMA)) {
            COMMA_.add(ctx);
        } else if (ctx.getToken().is(SEMICN)) {
            SEMICN_ = ctx;
        } else {
            ASSERT(false, "VarDecl only accepts COMMA or SEMICN");
        }
    }

    @Override
    public void add(Context ctx) {
        super.add(ctx);
        if (ctx instanceof BTypeContext) {
            bType = (BTypeContext) ctx;
        } else if (ctx instanceof VarDefContext) {
            varDef.add((VarDefContext) ctx);
        } else {
            ASSERT(false, "VarDecl only accepts BTypeContext, VarDefContext, VarDefContext");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public BTypeContext bType() {
        return bType;
    }
}
