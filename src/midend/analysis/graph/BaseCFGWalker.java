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

package midend.analysis.graph;

public class BaseCFGWalker<T> extends AbstractCFGWalker<T> {
    @Override
    public void enterStaff(T t) {
        // default: do nothing
    }

    @Override
    public void leaveStaff(T t) {
        // default: do nothing
    }
}
