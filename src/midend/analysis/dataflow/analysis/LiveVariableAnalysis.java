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

import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.Instruction;
import midend.analysis.dataflow.fact.SetFact;
import midend.analysis.graph.cfg.CFG;
import midend.analysis.graph.cfg.InstCFGBuilder;

import java.util.Optional;

public class LiveVariableAnalysis extends
        AbstractDataflowAnalysis<Instruction, SetFact<Value>> {

    public LiveVariableAnalysis() {
        super(new InstCFGBuilder());
    }

    public static final String ID = "livevar";

    @Override
    public boolean isForward() {
        return false;
    }

    @Override
    public SetFact<Value> newBoundaryFact(CFG<Instruction> cfg) {
        // TODO - finish me
        return new SetFact<>();
    }

    @Override
    public SetFact<Value> newInitialFact() {
        // TODO - finish me
        return new SetFact<>();
    }

    @Override
    public void meetInto(SetFact<Value> fact, SetFact<Value> target) {
        // TODO - finish me
        target.union(fact);
    }

    @Override
    public boolean transferNode(Instruction inst, SetFact<Value> in, SetFact<Value> out) {
        SetFact<Value> nextIn = out.copy();

        Optional<Value> def = inst.getDef();
        if (def.isPresent()) {
            nextIn.remove(def.get());
        }
        nextIn.addAll(inst.getUses());

        boolean r = !nextIn.equals(in);
        //in = nextIn;      // Wrong!!
        in.set(nextIn);     // Correct!!

        // return flag indicating whether In changed
        return r;
    }
}
