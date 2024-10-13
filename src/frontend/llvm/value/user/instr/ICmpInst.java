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

package frontend.llvm.value.user.instr;

import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Value;

public class ICmpInst extends Instruction {
    public ICmpInst(OpID opID, Value v1, Value v2, BasicBlock parent) {
        super(parent.getModule().getInt1Type(), opID, parent);
    }

    public String print() {
        return null;
    }
}
