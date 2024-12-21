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
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.GlobalVariable;
import frontend.llvm.value.user.instr.GetElementPtrInst;
import frontend.llvm.value.user.instr.Instruction;
import midend.analysis.dataflow.analysis.LiveVariableAnalysis;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.dataflow.fact.SetFact;
import midend.analysis.graph.cfg.CFG;

public class DeadCodeDetect implements Pass {
    // todo: 有副作用的语句不能删
    // todo: 删的时候还要维护好 llvm value 之间的关系

    @Override
    public void run(Function function) {
        boolean changed;
        do {
            changed = singlePass(function);
        } while (changed);
    }

    private boolean singlePass(Function function) {
        LiveVariableAnalysis analysis = new LiveVariableAnalysis();
        DataflowResult<Instruction, SetFact<Value>> liveVars = analysis.analyze(function);
        CFG<Instruction> cfg = analysis.getCfg();

        boolean changed = false;
        for (Instruction inst : function.getInstructions()) {
            if (cfg.isEntry(inst) || cfg.isExit(inst)) {
                continue;
            }
            Value defVal = (inst.isStore())
                    ? inst.getOperand(1)
                    : inst.getDef().isPresent()
                        ? inst.getDef().get()
                        : null;
            if (hasNoSideEffect(inst) && !liveVars.getResult(inst).contains(defVal)) {
                inst.eraseFromParent();
                changed = true;
            }
            //if (inst.isRet() || inst.isBr() || inst.isCall()) {
            //    continue;
            //}
            //if (inst.isStore()) {
            //    Value dest = inst.getOperand(1);
            //    if (!(dest instanceof GlobalVariable)
            //            && !(dest instanceof GetElementPtrInst)
            //            && !liveVars.getResult(inst).contains(dest)) {
            //        changed = true;
            //        inst.eraseFromParent();
            //    }
            //} else if (!liveVars.getResult(inst).contains(inst)) {
            //    changed = true;
            //    inst.eraseFromParent();
            //}
        }
        return changed;
    }

    private static boolean hasNoSideEffect(Instruction inst) {
        if (inst.isRet() ||
                // Jumped-to target block may have instructions that have side effect
                inst.isBr() ||
                // Function call may have side effect
                inst.isCall()) {
            return false;
        }
        if (inst.isStore()) {
            Value dest = inst.getOperand(1);
            // modify global data may have side effect
            if (dest instanceof GlobalVariable ||
                    // modify array fields may have side effect
                    dest instanceof GetElementPtrInst) {
                return false;
            }
        }

        return true;
    }
}
