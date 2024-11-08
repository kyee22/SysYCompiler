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
import frontend.llvm.value.Argument;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Value;

public class StoreInst extends Instruction {
    private StoreInst(Value val, Value ptr, BasicBlock bb) {
        super(bb.getModule().getVoidType(), OpID.STORE, bb);
        if (ptr.getType().getPointerElementType() != val.getType()) {
            throw new IllegalArgumentException("StoreInst ptr is not a pointer to val type");
        }
        addOperand(val);
        addOperand(ptr);
    }

    public static StoreInst createStore(Value val, Value ptr, BasicBlock bb) {
        return new StoreInst(val, ptr, bb);
    }


    @Override
    public String print() {
        StringBuilder instrIr = new StringBuilder();

        instrIr.append(getInstrOpName())
                .append(" ")
                .append(getOperand(0).getType().print())
                .append(" ")
                .append(IRPrinter.printAsOp(getOperand(0), false))
                .append(", ")
                .append(IRPrinter.printAsOp(getOperand(1), true));

        return instrIr.toString();
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
