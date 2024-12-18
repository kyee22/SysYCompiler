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

import java.util.List;

public class BasicBlockCFGBuilder implements CFGBuilder<BasicBlock> {
    public CFG<BasicBlock> analyze(Function function) {
        BasicBlockCFG cfg = new BasicBlockCFG();
        // 随便创建一个基本块作为 entry
        BasicBlock entry = BasicBlock.create(function.getParent(), "entry");
        cfg.setEntry(entry);
        // 随便创建一个基本块作作为 entry
        BasicBlock exit = BasicBlock.create(function.getParent(), "exit");
        cfg.setExit(exit);

        List<BasicBlock> basicBlocks = function.getBasicBlocks();
        cfg.addEdge(new Edge<>(entry, basicBlocks.get(0)));

        for (int i = 0; i < basicBlocks.size(); i++) {
            BasicBlock curr = basicBlocks.get(i);
            cfg.addNode(curr);

            for (BasicBlock succ : curr.getSuccBasicBlocks()) {
                cfg.addEdge(new Edge<>(curr, succ));
            }

            if (curr.isTerminated() && curr.getTerminator().isRet()) {
                cfg.addEdge(new Edge<>(curr, exit));
            }
        }

        return cfg;
    }
}
