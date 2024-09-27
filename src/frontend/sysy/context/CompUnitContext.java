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

public class CompUnitContext extends Context {
    private List<DeclContext> decl = new ArrayList<>();
    private List<FuncDefContext> funcDef = new ArrayList<>();
    private MainFuncDefContext mainFuncDef = null;

    @Override
    public void add(Context context) {
        super.add(context);
        if (context instanceof MainFuncDefContext) {
            mainFuncDef = (MainFuncDefContext) context;
        } else if (context instanceof DeclContext) {
            decl.add((DeclContext) context);
        } else if (context instanceof FuncDefContext) {
            funcDef.add((FuncDefContext) context);
        } else {
            ASSERT(false, "CompUnit only accepts MainFuncDefContext, DeclContext, FuncDefContext");
        }
    }

    @Override
    public void add(TerminalContext ctx) {
        ASSERT(false, "CompUnit only accepts Non-Terminal Context");
    }

    @Override
    public <T> T accept(ContextVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
