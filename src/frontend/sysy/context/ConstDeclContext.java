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

public class ConstDeclContext extends Context {
    private TerminalContext CONSTTK_ = null;
    private List<TerminalContext> COMMA_ = new ArrayList<>();
    private TerminalContext SEMICN_ = null;
    private BTypeContext bType = null;
    private List<ConstDefContext> constDef = new ArrayList<>();


    @Override
    public void add(TerminalContext cxt) {
        super.add(cxt);
        if (cxt.getToken().is(CONSTTK)) {
            CONSTTK_ = cxt;
        } else if (cxt.getToken().is(COMMA)) {
            COMMA_.add(cxt);
        } else if (cxt.getToken().is(SEMICN)) {
            SEMICN_ = cxt;
        } else {
            ASSERT(false, "ConstDecl only accepts CONSTTK or COMMA or SEMICN");
        }
    }

    @Override
    public void add(Context ctx) {
        super.add(ctx);
        if (ctx instanceof ConstDefContext) {
            constDef.add((ConstDefContext) ctx);
        } else if (ctx instanceof BTypeContext) {
            bType = (BTypeContext) ctx;
        } else {
            ASSERT(false, "ConstDecl only accepts BTypeContext, ConstDefContext");
        }
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

