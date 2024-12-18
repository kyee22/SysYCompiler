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

import java.util.ArrayList;
import java.util.List;

public class PhiInst extends Instruction {
    private PhiInst(Type ty, List<Value> vals, List<BasicBlock> valBbs, BasicBlock bb) {
        super(ty, OpID.PHI, bb);
        if (vals.size() != valBbs.size()) {
            throw new IllegalArgumentException("Unmatched vals and bbs");
        }
        for (int i = 0; i < vals.size(); i++) {
            if (vals.get(i).getType() != ty) {
                throw new IllegalArgumentException("Bad type for phi");
            }
            addOperand(vals.get(i));
            addOperand(valBbs.get(i));
        }
    }

    public static PhiInst createPhi(Type ty, BasicBlock bb, List<Value> vals, List<BasicBlock> valBbs) {
        return new PhiInst(ty, vals, valBbs, bb);
    }

    public static PhiInst createPhi(Type ty, BasicBlock bb) {
        return createPhi(ty, bb, new ArrayList<>(), new ArrayList<>());
    }

    public void addPhiPairOperand(Value value, BasicBlock preBasicBlock) {
        if (getType() != value.getType()) {
            throw new IllegalArgumentException("Bad type for phi");
        }
        addOperand(value);
        addOperand(preBasicBlock);
    }


    @Override
    public String print() {
        StringBuilder instIr = new StringBuilder();
        instIr.append("%")
                .append(getName())
                .append(" = ")
                .append(getInstrOpName())
                .append(" ")
                .append(getType().print())
                .append(" ");
        boolean first = true;
        for (int i = 0; i < getOperands().size() / 2; i++) {
            if (!first) {
                instIr.append(", ");
            }
            first = false;
            instIr.append("[ ")
                    .append(IRPrinter.printAsOp(getOperand(2 * i), false))
                    .append(", ")
                    .append(IRPrinter.printAsOp(getOperand(2 * i + 1), false))
                    .append(" ]");
        }
        if (getOperands().size() / 2 < getParent().getPrevBasicBlocks().size()) {
            for (BasicBlock pred : getParent().getPrevBasicBlocks()) {
                if (!getOperands().contains(pred)) {
                    if (!first) {
                        instIr.append(", ");
                    }
                    first = false;
                    instIr.append("[ undef, ")
                            .append(IRPrinter.printAsOp(pred, false))
                            .append(" ]");
                }
            }
        }
        return instIr.toString();
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
