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

package midend.analysis.graph;

import frontend.llvm.value.user.instr.Instruction;
import midend.analysis.graph.cfg.CFG;

public class InstDfnMarkWalker extends BaseCFGWalker<Instruction> {
    private int seq = 0;

    @Override
    public void dfs(CFG<Instruction> cfg) {
        seq = 0;
        super.dfs(cfg);
    }

    @Override
    public void enterStaff(Instruction instruction) {
        instruction.setIndex(seq++);
    }

    @Override
    public void leaveStaff(Instruction instruction) {}
}
