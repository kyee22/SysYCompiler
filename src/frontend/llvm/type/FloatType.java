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

package frontend.llvm.type;

import frontend.llvm.Module;

public class FloatType extends Type {
    public FloatType(frontend.llvm.Module module) {
        super(Type.TypeID.FloatTyID, module); // Assuming Module not needed here
    }

    public static FloatType get(Module module) {
        return module.getFloatType();
    }
}