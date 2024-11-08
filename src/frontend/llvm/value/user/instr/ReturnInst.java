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

public class ReturnInst extends Instruction {

    private ReturnInst(Value value, BasicBlock bb) {
        super(bb.getModule().getVoidType(), OpID.RET, bb);
        if (value == null) {
            if (!bb.getParent().getReturnType().isVoidType()) {
                throw new IllegalArgumentException("Non-Void function lack a return value");
            }
        } else {
            if (bb.getParent().getReturnType().isVoidType()) {
                throw new IllegalArgumentException("Void function returning a value");
            }
            if (bb.getParent().getReturnType() != value.getType()) {
                throw new IllegalArgumentException("Return type mismatch");
            }
            addOperand(value);
        }
    }

    public static ReturnInst createRet(Value value, BasicBlock bb) {
        return new ReturnInst(value, bb);
    }

    public static ReturnInst createVoidRet(BasicBlock bb) {
        return createRet(null, bb);
    }

    public boolean isVoidReturn() {
        return getNumOperand() == 0;
    }

    @Override
    public String print() {
        StringBuilder instIr = new StringBuilder();
        instIr.append(getInstrOpName());
        instIr.append(" ");
        if (!isVoidReturn()) {
            instIr.append(getOperand(0).getType().print());
            instIr.append(" ");
            instIr.append(IRPrinter.printAsOp(getOperand(0), false));
        } else {
            instIr.append("void");
        }
        return instIr.toString();
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
