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

package backend.regalloc;

import backend.mips.Register;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.Instruction;
import midend.analysis.dataflow.analysis.LiveVariableAnalysis;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.dataflow.fact.SetFact;
import midend.analysis.graph.InstDfnMarkWalker;
import midend.analysis.graph.cfg.CFG;

import java.util.*;

import static backend.mips.Register.*;

public class GraphColorRegAllocator extends BaseRegAllocator {
    Map<Value, Register> regMap;
    Stack<Register> freeRegPool = new Stack<>();
    Stack<Value> stack = new Stack<>();
    Map<Value, Set<Value>> neightbors1;
    Map<Value, Set<Value>> neightbors2;


    @Override
    public Map<Value, Register> allocate(Function function) {
        /*      prepare and initialize the required data structures    */
        usedSavadRegs.clear();
        neightbors1 = new HashMap<>();
        neightbors2 = new HashMap<>();
        stack = new Stack<>();
        regMap = new HashMap<>();

        freeRegPool = new Stack<>();
        freeRegPool.addAll(List.of(
                //REG_V1,
                REG_S7, REG_S6, REG_S5, REG_S4, REG_S3, REG_S2, REG_S1, REG_S0,
                REG_T9, REG_T8, REG_T7, REG_T6, REG_T5, REG_T4,
                REG_V1, REG_FP, REG_GP, REG_K0, REG_K1
        ));

        /*      initialize the live intervals with the result of live variable analysis    */
        setUpGraph(function);

        /*      begin algorithm    */
        int K = freeRegPool.size();
        boolean changed;
        do {
            changed = false;
            for (Value value : neightbors1.keySet()) {
                if (neightbors1.get(value).size() < K) {
                    stack.push(value);

                    for (Value pair : neightbors1.get(value)) {
                        neightbors1.get(pair).remove(value);
                    }
                    neightbors1.remove(value);

                    changed = true;
                    break;
                }
            }

            if (!changed && !neightbors1.isEmpty()) {
                Value value  = null;
                int maxDegree = Integer.MIN_VALUE;
                for (Value v : neightbors1.keySet()) {
                    if (neightbors1.get(v).size() > maxDegree) {
                        maxDegree = neightbors1.get(v).size();
                        value = v;
                    }
                }
                for (Value pair : neightbors1.get(value)) {
                    neightbors1.get(pair).remove(value);
                }
                neightbors1.remove(value);

                changed = true;
            }

        } while (changed);

        List<Value> vals = new ArrayList<>();
        vals.addAll(stack);

        for (int i = vals.size() - 1; i >= 0; i--) {
            Set<Register> regs = new HashSet<>();
            regs.addAll(freeRegPool);
            for (Value pair : neightbors2.get(vals.get(i))) {
                if (regMap.containsKey(pair)) {
                    regs.remove(regMap.get(pair));
                }
            }
            Register reg = regs.iterator().next();
            regMap.put(vals.get(i), reg);
            if (!usedSavadRegs.contains(reg)) {
                usedSavadRegs.add(reg);
            }
        }


        return regMap;
    }



    private void setUpGraph(Function function) {
        LiveVariableAnalysis analysis = new LiveVariableAnalysis();
        DataflowResult<Instruction, SetFact<Value>> liveVarsRes = analysis.analyze(function);
        CFG<Instruction> cfg = analysis.getCfg();
        (new InstDfnMarkWalker()).dfs(analysis.getCfg());

        //for (Instruction inst: cfg) {
        //    neightbors1.put(inst, new HashSet<>());
        //    neightbors2.put(inst, new HashSet<>());
        //}
        for (Instruction inst : cfg) {
            for (Value value : liveVarsRes.getOutFact(inst)) {
                if (value instanceof Instruction i && !i.isVoid() && !neightbors1.containsKey(i)) {
                    neightbors1.put(i, new HashSet<>());
                    neightbors2.put(i, new HashSet<>());
                }
            }
        }

        for (Instruction inst : cfg) {
            for (Value val1 : liveVarsRes.getOutFact(inst)) {
                for (Value val2 : liveVarsRes.getOutFact(inst)) {
                    if (val1 instanceof Instruction && val2 instanceof Instruction) {
                        buildEdge(val1, val2);
                    }
                }
            }
        }
    }

    private void buildEdge(Value val1, Value val2) {
        if (val1 == val2) {
            return;
        }
        neightbors1.get(val1).add(val2);
        neightbors2.get(val2).add(val1);
    }
}
