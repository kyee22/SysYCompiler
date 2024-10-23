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

public class FCmpInst extends Instruction {
    private FCmpInst(OpID opID, Value v1, Value v2, BasicBlock parent) {
        super(parent.getModule().getInt1Type(), opID, parent);
        if (!(v1.getType().isFloatType() && v2.getType().isFloatType())) {
            throw new IllegalArgumentException("FCmpInst operands are not both float");
        }
        addOperand(v1);
        addOperand(v2);
    }

    public static FCmpInst createFgt(Value v1, Value v2, BasicBlock parent) {
        return new FCmpInst(OpID.FGT, v1, v2, parent);
    }

    public static FCmpInst createFge(Value v1, Value v2, BasicBlock parent) {
        return new FCmpInst(OpID.FGE, v1, v2, parent);
    }

    public static FCmpInst createFlt(Value v1, Value v2, BasicBlock parent) {
        return new FCmpInst(OpID.FLT, v1, v2, parent);
    }

    public static FCmpInst createFle(Value v1, Value v2, BasicBlock parent) {
        return new FCmpInst(OpID.FLE, v1, v2, parent);
    }

    public static FCmpInst createFeq(Value v1, Value v2, BasicBlock parent) {
        return new FCmpInst(OpID.FEQ, v1, v2, parent);
    }

    public static FCmpInst createFne(Value v1, Value v2, BasicBlock parent) {
        return new FCmpInst(OpID.FNE, v1, v2, parent);
    }

    @Override
    public String print() {
        return IRPrinter.printCmpInts(this);
    }
}
