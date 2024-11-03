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

public class IntegerType extends Type {
    private int numBits;

    public IntegerType(int numBits, Module module) {
        super(Type.TypeID.IntegerTyID, module);
        this.numBits = numBits;
    }

    public int getNumBits() { return numBits; }

    @Override
    public boolean isInt32Type() { return numBits == 32; }
    @Override
    public boolean isInt8Type() { return numBits == 8; }
    @Override
    public boolean isInt1Type() { return numBits == 1; }
}