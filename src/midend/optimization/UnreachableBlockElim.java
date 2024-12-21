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
import midend.analysis.graph.UnreachableBlockElimWalker;
import midend.analysis.graph.cfg.BasicBlockCFG;
import midend.analysis.graph.cfg.BasicBlockCFGBuilder;
import midend.analysis.graph.cfg.CFG;

public class UnreachableBlockElim implements Pass {

    @Override
    public void run(Function function) {
        CFG<BasicBlock> cfg = (new BasicBlockCFGBuilder()).analyze(function);
        (new UnreachableBlockElimWalker()).dfs(cfg);
    }
}
