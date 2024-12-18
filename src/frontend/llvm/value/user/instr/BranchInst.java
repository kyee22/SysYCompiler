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

import java.util.ArrayList;
import java.util.List;

public class BranchInst extends Instruction {
    public BranchInst(Value cond, BasicBlock ifTrue, BasicBlock ifFalse, BasicBlock bb) {
        super(bb.getModule().getVoidType(), OpID.BR, bb);

        if (cond == null) { // conditionless jump
            if (ifFalse != null) {
                throw new IllegalArgumentException("Given false-bb on conditionless jump");
            }
            addOperand(ifTrue);
            ifTrue.addPrevBasicBlock(bb);
            bb.addSuccBasicBlock(ifTrue);
        } else {
            if (!cond.getType().isInt1Type()) {
                throw new RuntimeException("BranchInst condition is not i1");
            }
            addOperand(cond);
            addOperand(ifTrue);
            addOperand(ifFalse);
            ifTrue.addPrevBasicBlock(bb);
            bb.addSuccBasicBlock(ifTrue);
            ifFalse.addPrevBasicBlock(bb);
            bb.addSuccBasicBlock(ifFalse);
        }
    }

    public static BranchInst createCondBr(Value cond, BasicBlock ifTrue, BasicBlock ifFalse, BasicBlock bb) {
        return new BranchInst(cond, ifTrue, ifFalse, bb);
    }

    public static BranchInst createBr(BasicBlock ifTrue, BasicBlock bb) {
        return createCondBr(null, ifTrue, null, bb);
    }

    @Override
    public String print() {
        StringBuilder instIr = new StringBuilder();
        instIr.append(getInstrOpName());
        instIr.append(" ");
        instIr.append(IRPrinter.printAsOp(getOperand(0), true));
        if (isCondBr()) {
            instIr.append(", ");
            instIr.append(IRPrinter.printAsOp(getOperand(1), true));
            instIr.append(", ");
            instIr.append(IRPrinter.printAsOp(getOperand(2), true));
        }
        return instIr.toString();
    }

    @Override
    public void eraseFromParent() {
        List<BasicBlock> successors = new ArrayList<>();
        if (isCondBr()) {
            successors.add((BasicBlock) getOperand(1));
            successors.add((BasicBlock) getOperand(2));
        } else {
            successors.add((BasicBlock) getOperand(0));
        }

        for (BasicBlock successor : successors) {
            if (successor != null) {
                successor.removePrevBasicBlock(getParent());
                getParent().removeSuccBasicBlock(successor);
            }
        }
        super.eraseFromParent();
    }

    public boolean isCondBr() {
        return getNumOperand() == 3;
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
