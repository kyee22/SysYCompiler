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

package frontend.llvm.value.user.constant;

import frontend.llvm.value.user.User;
import frontend.llvm.type.Type;

public abstract class Constant extends User {
    public Constant(Type type, String name) {
        super(type, name);
    }

    public abstract String print();
}
