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

import frontend.llvm.IRPrinter;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Value;

public class FBinaryInst extends Instruction {
    private FBinaryInst(OpID opID, Value v1, Value v2, BasicBlock parent) {
        super(parent.getModule().getFloatType(), opID, parent);
        if (!(v1.getType().isFloatType() && v2.getType().isFloatType())) {
            throw new IllegalArgumentException("Type of binary instruction must be float type");
        }
        addOperand(v1);
        addOperand(v2);
    }

    public static FBinaryInst createFadd(Value v1, Value v2, BasicBlock parent) {
        return new FBinaryInst(OpID.FADD, v1, v2, parent);
    }

    public static FBinaryInst createFsub(Value v1, Value v2, BasicBlock parent) {
        return new FBinaryInst(OpID.FSUB, v1, v2, parent);
    }

    public static FBinaryInst createFmul(Value v1, Value v2, BasicBlock parent) {
        return new FBinaryInst(OpID.FMUL, v1, v2, parent);
    }

    public static FBinaryInst createFdiv(Value v1, Value v2, BasicBlock parent) {
        return new FBinaryInst(OpID.FDIV, v1, v2, parent);
    }

    @Override
    public String print() {
        return IRPrinter.printBinaryInst(this);
    }
}
