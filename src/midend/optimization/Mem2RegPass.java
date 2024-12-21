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
import frontend.llvm.type.Type;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.constant.ConstantInt;
import frontend.llvm.value.user.instr.*;
import midend.analysis.dataflow.analysis.DominationAnalysis;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.dataflow.fact.SetFact;
import midend.analysis.graph.BaseCFGWalker;
import midend.analysis.graph.cfg.CFG;

import java.util.*;

public class Mem2RegPass implements Pass {
    private Map<BasicBlock, SetFact<BasicBlock>> DF;
    private Map<PhiInst, AllocaInst> phi2alloc;
    CFG<BasicBlock> cfg;
    private Map<AllocaInst, Stack<Value>> stacks;
    private List<Instruction> deleteList;

    @Override
    public void run(Function function) {
        computeDominanceFrontier(function);
        insertPhiFunctions(function);
        rename(function);
        cleanEliminatedInsts();
    }

    public Map<BasicBlock, SetFact<BasicBlock>> computeDominanceFrontier(Function function) {
        // prepare result of Domination Analysis
        DominationAnalysis dominationAnalysis = new DominationAnalysis();
        DataflowResult<BasicBlock, SetFact<BasicBlock>> result = dominationAnalysis.analyze(function);
        function.setDomination(result);
        cfg = dominationAnalysis.getCfg();

        // initialize data structure
        DF = new HashMap<>();
        for (BasicBlock bb : cfg) {
            DF.put(bb, new SetFact<>());
        }

        // begin algorithm
        for (BasicBlock bb : cfg) {
            for (BasicBlock pred : cfg.getPredsOf(bb)) {
                BasicBlock x = pred;
                while (x != null && !bb.isStrictlyDominatedBy(x)) {
                    DF.get(x).add(bb);
                    x = x.getImmediateDominator();
                }
            }
        }
        return DF;
    }

    public void insertPhiFunctions(Function function) {
        // 识别 (1) 跨越多个基本块, (2) 可以被提升 的 (3) Alloca 变量
        List<AllocaInst> vals = new ArrayList<>();
        for (Instruction inst : function.getInstructions()) {
            if (inst instanceof AllocaInst allocaInst
                && allocaInst.isPromotable()
                && allocaInst.isGlobalName()) {
                vals.add(allocaInst);
            }
        }

        // Initialize data structure
        phi2alloc = new HashMap<>(); 

        // Begin algorithm
        for (AllocaInst v : vals) {
            Set<BasicBlock> F = new HashSet<>();    // 已经插入关于变量 v 的 phi 函数的基本块集合
            Set<BasicBlock> W = new HashSet<>();    // 所有定义了变量 v 的基本块集合
            Set<BasicBlock> defs = v.getDefs();
            W.addAll(defs);                         // 初始化 W 为 Defs(v)

            while (!W.isEmpty()) {
                BasicBlock X = W.iterator().next();
                W.remove(X);                        // 从 W 中获得一个基本块, X

                for (BasicBlock Y: DF.get(X)) {     // 在 X 的支配边界插入 phi 函数
                    if (!F.contains(Y)) {
                        F.add(Y);                   // 标记 v 在这个基本块已经插入了 phi 函数
                        PhiInst phiInst = PhiInst.createPhi(v.getAllocaType(), Y);
                                                    // 插入 phi 函数
                        phi2alloc.put(phiInst, v);  // 记录下这个 phi 函数是为哪个 v 设置的
                        if (!defs.contains(Y)) {    // 插入的 phi 函数也是对 v 的定义,
                            W.add(Y);               // 这些定义也要插入相应的 phi 函数,
                        }                           // 因而将 Y 插入到 W 中去。
                    }
                }
            }
        }
    }

    public void rename(Function function) {
        // 识别 (1) 可以被提升 的 (2) Alloca 变量
        // 注意: 这里不需要像 insertPhiFunctions 有 "跨越多个基本块" 的约束,
        //      同一个基本块的内存变量也可以提升, 只是不需要插入 phi 函数.
        stacks = new HashMap<>();
        deleteList = new ArrayList<>();
        for (Instruction inst : function.getInstructions()) {
            if (inst instanceof AllocaInst allocaInst
                    && allocaInst.isPromotable()) {
                stacks.put(allocaInst, new Stack<>());
                deleteList.add(inst);
            }
        }
        // rename 实际上也是在 cfg 进行 walk 的同时执行一些动作, 这里直接
        // 继承 BaseCFGWalker 并通过 override 在进入和退出节点时做一些动作
        (new RenameWalker()).dfs(cfg);
    }

    private class RenameWalker extends BaseCFGWalker<BasicBlock> {
        @Override
        public void enterStaff(BasicBlock basicBlock) {
            for (Instruction inst : basicBlock.getInstrs()) {
                if (inst instanceof PhiInst phiInst) {
                    AllocaInst who = phi2alloc.get(phiInst);
                    stacks.get(who).push(inst);
                } else if (inst instanceof StoreInst storeInst) {
                    Value ptr = storeInst.getOperand(1);
                    if (ptr instanceof AllocaInst allocaInst && stacks.containsKey(allocaInst)) {
                        stacks.get(allocaInst).push(inst.getOperand(0));
                        deleteList.add(storeInst);
                    }
                } else if (inst instanceof LoadInst loadInst) {
                    Value ptr = loadInst.getOperand(0);
                    if (ptr instanceof AllocaInst allocaInst && stacks.containsKey(allocaInst)) {
                        // 局部变量未初始化是可以的: https://judge.buaa.edu.cn/forum/thread.jsp?forum=811&thread=14234
                        Value replaced = stacks.get(allocaInst).isEmpty()
                                            ? makeDefaultUndefValue(allocaInst.getAllocaType(), basicBlock)
                                            : stacks.get(allocaInst).peek();
                        inst.replaceAllUseWith(replaced);
                        deleteList.add(loadInst);
                    }
                }
            }
            for (BasicBlock succ : basicBlock.getSuccBasicBlocks()) {
                for (Instruction inst : succ.getInstrs()) {
                    if (inst instanceof PhiInst phiInst) {
                        AllocaInst who = phi2alloc.get(phiInst);
                        Value defined = stacks.get(who).isEmpty()
                                            ? makeDefaultUndefValue(who.getAllocaType(), basicBlock)
                                            : stacks.get(who).peek();
                        phiInst.addPhiPairOperand(defined, basicBlock);
                    } else {
                        break;
                    }
                }
            }
        }

        @Override
        public void leaveStaff(BasicBlock basicBlock) {
            for (Instruction inst : basicBlock.getInstrs()) {
                if (inst instanceof PhiInst phiInst) {
                    AllocaInst who = phi2alloc.get(phiInst);
                    stacks.get(who).pop();
                } else if (inst instanceof StoreInst storeInst) {
                    Value ptr = storeInst.getOperand(1);
                    if (ptr instanceof AllocaInst allocaInst && stacks.containsKey(allocaInst)) {
                        stacks.get(allocaInst).pop();
                    }
                }
            }
        }
    }

    public void cleanEliminatedInsts() {
        for (Instruction inst : deleteList) {
            inst.eraseFromParent();
        }
    }

    private static Value makeDefaultUndefValue(Type type, BasicBlock basicBlock) {
        if (type.isInt1Type()) {
            return ConstantInt.getBool(false, basicBlock.getModule());
        } else if (type.isInt8Type()) {
            return ConstantInt.getChar(0, basicBlock.getModule());
        } else if (type.isInt32Type()) {
            return ConstantInt.getInt(0, basicBlock.getModule());
        }
        throw new IllegalArgumentException("Unsupported type: " + type);
    }
}
