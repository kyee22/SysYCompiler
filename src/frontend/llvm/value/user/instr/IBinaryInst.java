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
import frontend.llvm.type.Type;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Value;

public class IBinaryInst extends Instruction {

    private IBinaryInst(OpID id, Value v1, Value v2, BasicBlock parent) {
        super(parent.getModule().getInt32Type(), id, parent);
        if (!(v1.getType().isInt32Type() && v2.getType().isInt32Type())) {
            throw new IllegalArgumentException("Type of binary instruction must be int32 type");
        }
        addOperand(v1);
        addOperand(v2);
    }

    public static IBinaryInst createAdd(Value v1, Value v2, BasicBlock parent) {return new IBinaryInst(OpID.ADD, v1, v2, parent);}
    public static IBinaryInst createSub(Value v1, Value v2, BasicBlock parent) {return new IBinaryInst(OpID.SUB, v1, v2, parent);}
    public static IBinaryInst createMul(Value v1, Value v2, BasicBlock parent) {return new IBinaryInst(OpID.MUL, v1, v2, parent);}
    public static IBinaryInst createSdiv(Value v1, Value v2, BasicBlock parent) {return new IBinaryInst(OpID.SDIV, v1, v2, parent);}
    public static IBinaryInst createSrem(Value v1, Value v2, BasicBlock parent) {return new IBinaryInst(OpID.SREM, v1, v2, parent);}

    @Override
    public String print() {
        return IRPrinter.printBinaryInst(this);
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
