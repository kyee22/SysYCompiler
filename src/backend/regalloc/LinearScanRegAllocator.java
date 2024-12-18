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

/**
 * Linear Scan Register Allocationz
 *
 * Reference:
 *      ACM Transactions on Programming Languages and Systems, Vol. 21, No. 5, September 1999, Pages 895–913
 *      (https://web.cs.ucla.edu/~palsberg/course/cs132/linearscan.pdf)
 */

public class LinearScanRegAllocator extends BaseRegAllocator {
    Map<Value, Register> regMap;
    Map<Value, Interval> intervals;
    TreeSet<Interval> active;
    Stack<Register> freeRegPool = new Stack<>();
    int threshold;


    @Override
    public Map<Value, Register> allocate(Function function) {
        /*      prepare and initialize the required data structures    */
        usedSavadRegs.clear();
        regMap = new HashMap<>();
        intervals = new HashMap<>();
        active = new TreeSet<>(Comparator
                .comparingInt(Interval::getEndPoint)
                .thenComparingInt(Interval::getStartPoint)
                .thenComparingInt(i -> i.value.hashCode())
        );
        freeRegPool = new Stack<>();
        freeRegPool.addAll(List.of(
                //REG_V1,
                REG_S7, REG_S6, REG_S5, REG_S4, REG_S3, REG_S2, REG_S1, REG_S0,
                REG_T9, REG_T8, REG_T7, REG_T6, REG_T5, REG_T4,
                REG_V1, REG_FP, REG_GP, REG_K0, REG_K1
        ));
        threshold = freeRegPool.size();

        /*      initialize the live intervals with the result of live variable analysis    */
        setUpLiveIntervals(function);

        /*      begin algorithm    */
        // extract all intervals in order of increasing start point
        List<Map.Entry<Value, Interval>> entryList = new ArrayList<>(intervals.entrySet());
        entryList.sort(Comparator.comparingInt(entry -> entry.getValue().getStartPoint()));

        for (Map.Entry<Value, Interval> entry : entryList) {
            Interval interval = entry.getValue();
            expireOldIntervals(interval);
            if (active.size() == threshold) {
                spillAtInterval(interval);
            } else {
                Register reg = freeRegPool.pop();
                // maintain info about use of saved regs, for the sake of stack frame
                if (!usedSavadRegs.contains(reg)) {
                    usedSavadRegs.add(reg);
                }
                regMap.put(interval.value, reg);
                active.add(interval);
            }
        }

        return regMap;
    }

    private void expireOldIntervals(Interval curInterval) {
        List<Interval> deleteLst = new ArrayList<>();

        for (Interval interval : active) {
            // Note that `active` is sorted by increasing end point,
            // such that we may early break to speed up scanning
            if (interval.endPoint > curInterval.startPoint) {
                break;
            }
            deleteLst.add(interval);
            freeRegPool.push(regMap.get(interval.value));
        }

        active.removeAll(deleteLst);
    }

    private void spillAtInterval(Interval curInterval) {
        Interval spill = active.last();
        if (spill.endPoint > curInterval.endPoint) {
            regMap.put(curInterval.value, regMap.get(spill.value));
            regMap.remove(spill.value);     // spill <- stack location
            active.pollLast();
            active.add(curInterval);
        } else {
                                            // i <- stack location
        }
    }


    private void setUpLiveIntervals(Function function) {
        LiveVariableAnalysis analysis = new LiveVariableAnalysis();
        DataflowResult<Instruction, SetFact<Value>> liveVarsRes = analysis.analyze(function);
        CFG<Instruction> cfg = analysis.getCfg();
        (new InstDfnMarkWalker()).dfs(analysis.getCfg());
        intervals = new HashMap<>();

        for (Instruction inst : cfg) {
            for (Value val : liveVarsRes.getOutFact(inst)) {
                if (!intervals.containsKey(val)) {
                    intervals.put(val, new Interval(inst.getIndex(), inst.getIndex(), val));
                }
                intervals.get(val).startPoint = Integer.min(intervals.get(val).startPoint, inst.getIndex());
            }
            for (Value val : liveVarsRes.getInFact(inst)) {
                if (!intervals.containsKey(val)) {
                    intervals.put(val, new Interval(inst.getIndex(), inst.getIndex(), val));
                }
                intervals.get(val).endPoint = Integer.max(intervals.get(val).endPoint, inst.getIndex());
            }
        }
    }


    private class Interval {
        int startPoint;
        int endPoint;
        Value value;

        Interval(int startPoint, int endPoint, Value value) {
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.value = value;
        }

        int getStartPoint() {
            return startPoint;
        }

        int getEndPoint() {
            return endPoint;
        }
    }

}
