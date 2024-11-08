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

public class LoadInst extends Instruction {
    private LoadInst(Value ptr, BasicBlock bb) {
        super(ptr.getType().getPointerElementType(), OpID.LOAD, bb);
        if (!(getType().isIntegerType() || getType().isFloatType() || getType().isPointerType())) {
            // todo i8
            throw new IllegalArgumentException("Should not load value with type except int/float");
        }
        addOperand(ptr);
    }

    public static LoadInst createLoad(Value ptr, BasicBlock bb) {
        return new LoadInst(ptr, bb);
    }

    @Override
    public String print() {
        StringBuilder instrIr = new StringBuilder();

        if (!getOperand(0).getType().isPointerType()) {
            throw new RuntimeException("Not a pointer type");
        }

        instrIr.append("%")
                .append(getName())
                .append(" = ")
                .append(getInstrOpName())
                .append(" ")
                .append(getOperand(0).getType().getPointerElementType().print())
                .append(",")
                .append(" ")
                .append(IRPrinter.printAsOp(getOperand(0), true));

        return instrIr.toString();
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
