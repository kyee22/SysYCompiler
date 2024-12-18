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

package midend.analysis.graph.cfg;

import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.user.instr.BranchInst;
import frontend.llvm.value.user.instr.Instruction;
import frontend.llvm.value.user.instr.pinstr.NopInst;

import java.util.List;

public class InstCFGBuilder implements CFGBuilder<Instruction> {
    public CFG<Instruction> analyze(Function function) {
        InstCFG cfg = new InstCFG();
        // 随便创建一条指令作为 entry
        Instruction entry = NopInst.create();
        cfg.setEntry(entry);
        // 随便创建一条指令作为 entry
        Instruction exit = NopInst.create();
        cfg.setExit(exit);

        List<Instruction> instrs = function.getInstructions();
        cfg.addEdge(new Edge<>(entry, instrs.get(0)));

        for (int i = 0; i < instrs.size(); i++) {
            Instruction curr = instrs.get(i);
            cfg.addNode(curr);
            if (curr.isRet()) {
                cfg.addEdge(new Edge<>(curr, exit));
            } else if (curr instanceof BranchInst br) {
                if (br.isCondBr()) {
                    cfg.addEdge(new Edge<>(curr, ((BasicBlock) br.getOperand(1)).getInstrs().get(0)));
                    cfg.addEdge(new Edge<>(curr, ((BasicBlock) br.getOperand(2)).getInstrs().get(0)));
                } else {
                    cfg.addEdge(new Edge<>(curr, ((BasicBlock) br.getOperand(0)).getInstrs().get(0)));
                }
            } else if (i < instrs.size() - 1) {
                cfg.addEdge(new Edge<>(curr, instrs.get(i + 1)));
            }
        }

        return cfg;
    }
}
