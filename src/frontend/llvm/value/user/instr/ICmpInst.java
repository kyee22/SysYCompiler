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

public class ICmpInst extends Instruction {
    public ICmpInst(OpID opID, Value v1, Value v2, BasicBlock parent) {
        super(parent.getModule().getInt1Type(), opID, parent);
        if (!(v1.getType().isInt32Type() && v2.getType().isInt32Type())) {
            throw new IllegalArgumentException("CmpInst operands are not both i32");
        }
        addOperand(v1);
        addOperand(v2);
    }

    public static ICmpInst createGt(Value v1, Value v2, BasicBlock parent) {
        return new ICmpInst(OpID.GT, v1, v2, parent);
    }

    public static ICmpInst createGe(Value v1, Value v2, BasicBlock parent) {
        return new ICmpInst(OpID.GE, v1, v2, parent);
    }

    public static ICmpInst createLt(Value v1, Value v2, BasicBlock parent) {
        return new ICmpInst(OpID.LT, v1, v2, parent);
    }

    public static ICmpInst createLe(Value v1, Value v2, BasicBlock parent) {
        return new ICmpInst(OpID.LE, v1, v2, parent);
    }

    public static ICmpInst createEq(Value v1, Value v2, BasicBlock parent) {
        return new ICmpInst(OpID.EQ, v1, v2, parent);
    }

    public static ICmpInst createNe(Value v1, Value v2, BasicBlock parent) {
        return new ICmpInst(OpID.NE, v1, v2, parent);
    }

    @Override
    public String print() {
        return IRPrinter.printCmpInts(this);
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
