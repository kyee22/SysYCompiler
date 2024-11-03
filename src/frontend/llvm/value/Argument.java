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

package frontend.llvm.value;

import frontend.llvm.IRPrinter;
import frontend.llvm.type.Type;

public class Argument extends Value {
    private Function parent;
    private int argno;

    public Argument(Type type, String name, Function parent, int argno) {
        super(type, name);
        this.parent = parent;
        this.argno = argno;
    }

    @Override
    public String print() {
        return IRPrinter.printAsOp(this, true);
    }
}
