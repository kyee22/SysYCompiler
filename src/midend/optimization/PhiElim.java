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

package midend.optimization;

import frontend.llvm.Module;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.Instruction;
import frontend.llvm.value.user.instr.PhiInst;
import frontend.llvm.value.user.instr.pinstr.MoveInst;

import java.util.List;

public class PhiElim implements Pass {
    @Override
    public void run(Function function) {
        for (BasicBlock bb : function.getBasicBlocks()) {
            eliminatePhiFunctions(bb);
        }
    }

    private void eliminatePhiFunctions(BasicBlock bb) {
        List<Instruction> insts = List.copyOf(bb.getInstrs());
        for (Instruction inst : insts) {
            if (inst instanceof PhiInst phiInst) {
                for (int i = 0; i < phiInst.getOperands().size() / 2; i++) {
                    Value value = phiInst.getOperand(i * 2);
                    BasicBlock pred = (BasicBlock) phiInst.getOperand(i * 2 + 1);
                    MoveInst.createMove(value, phiInst, pred);
                }
                phiInst.eraseFromParent();
            } else {
                break;
            }
        }
    }
}
