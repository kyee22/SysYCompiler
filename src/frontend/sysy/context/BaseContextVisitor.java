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

public class BaseContextVisitor<T> implements ContextVisitor<T> {
    @Override
    public T visitDefault(Context ctx) {
        T r = null;
        for (Context child : ctx.getChildren()) {
            r = child.accept(this);
        }
        return r;
    }
}
