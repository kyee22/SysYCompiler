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

import frontend.llvm.value.BasicBlock;
import midend.analysis.graph.cfg.CFG;

import java.util.HashSet;
import java.util.Set;

public class UnreachableBlockElimWalker extends BaseCFGWalker<BasicBlock> {
    private Set<BasicBlock> walked;

    @Override
    public void dfs(CFG<BasicBlock> cfg) {
        walked = new HashSet<>();
        super.dfs(cfg);
        for (BasicBlock bb: cfg) {
            if (!walked.contains(bb)) {
                bb.eraseFromParent();
            }
        }
    }

    @Override
    public void enterStaff(BasicBlock basicBlock) {
        walked.add(basicBlock);
    }
}
