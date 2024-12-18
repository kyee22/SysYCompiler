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

package midend.analysis.dataflow.analysis;

import frontend.llvm.value.BasicBlock;
import midend.analysis.dataflow.fact.SetFact;
import midend.analysis.graph.cfg.BasicBlockCFGBuilder;
import midend.analysis.graph.cfg.CFG;

public class DominationAnalysis extends
        AbstractDataflowAnalysis<BasicBlock, SetFact<BasicBlock>> {
    public DominationAnalysis() {
        super(new BasicBlockCFGBuilder());
    }

    @Override
    public boolean isForward() {
        return true;
    }

    @Override
    public SetFact<BasicBlock> newBoundaryFact(CFG<BasicBlock> cfg) {
        SetFact<BasicBlock> r = new SetFact<>();
        r.add(cfg.getEntry());
        return r;
    }

    @Override
    public SetFact<BasicBlock> newInitialFact() {
        SetFact<BasicBlock> r = new SetFact<>();
        r.addAll(function.getBasicBlocks());
        return r;
    }

    @Override
    public void meetInto(SetFact<BasicBlock> fact, SetFact<BasicBlock> target) {
        target.intersect(fact);
    }

    @Override
    public boolean transferNode(BasicBlock bb, SetFact<BasicBlock> in, SetFact<BasicBlock> out) {
        SetFact<BasicBlock> oldOut = out.copy();
        out.set(in.copy());
        out.add(bb);
        return !out.equals(oldOut);
    }
}