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

import frontend.llvm.IRBuilder;
import frontend.llvm.Module;
import frontend.llvm.type.FunctionType;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import midend.analysis.dataflow.analysis.DominationAnalysis;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.dataflow.fact.SetFact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class Mem2RegPassTest {
    frontend.llvm.Module module;
    Function function;
    IRBuilder irBuilder;
    Mem2RegPass mem2RegPass;

    @BeforeEach
    void setUp() {
        module = new Module();
        FunctionType funcTy = module.getFunctionType(module.getVoidType(), List.of(module.getInt32Type()));
        function = Function.create(funcTy, "func", module);
        irBuilder = new IRBuilder(module, null);
        mem2RegPass = new Mem2RegPass();
    }

    @Test
    void testComputeDominanceFrontier() {
        // 测试 CFG 示例来自 https://judge.buaa.edu.cn/customizedColumn/chapterMD.jsp?columnID=873&subColumnID=901
        BasicBlock bb1 = BasicBlock.create(module, "1", function);
        BasicBlock bb2 = BasicBlock.create(module, "2", function);
        BasicBlock bb3 = BasicBlock.create(module, "3", function);
        BasicBlock bb4 = BasicBlock.create(module, "4", function);
        BasicBlock bb5 = BasicBlock.create(module, "5", function);
        BasicBlock bb6 = BasicBlock.create(module, "6", function);
        BasicBlock bb7 = BasicBlock.create(module, "7", function);
        BasicBlock bb8 = BasicBlock.create(module, "8", function);

        irBuilder.setInsertPoint(bb8);
        irBuilder.createVoidRet();

        bb1.addSuccBasicBlock(bb2);

        bb2.addSuccBasicBlock(bb3);
        bb2.addSuccBasicBlock(bb4);

        bb3.addSuccBasicBlock(bb7);

        bb4.addSuccBasicBlock(bb5);
        bb4.addSuccBasicBlock(bb6);

        bb5.addSuccBasicBlock(bb7);

        bb6.addSuccBasicBlock(bb4);

        bb7.addSuccBasicBlock(bb8);

        Map<BasicBlock, SetFact<BasicBlock>> fontier = mem2RegPass.computeDominanceFrontier(function);
        assertEquals(0, fontier.get(bb1).size());
        assertEquals(0, fontier.get(bb2).size());
        assertEquals(0, fontier.get(bb7).size());
        assertEquals(0, fontier.get(bb8).size());

        assertEquals(1, fontier.get(bb6).size());
        assertTrue(fontier.get(bb6).contains(bb4));

        assertEquals(1, fontier.get(bb3).size());
        assertTrue(fontier.get(bb3).contains(bb7));
        assertEquals(1, fontier.get(bb5).size());
        assertTrue(fontier.get(bb5).contains(bb7));
        assertEquals(2, fontier.get(bb4).size());
        assertTrue(fontier.get(bb4).contains(bb7));
        assertTrue(fontier.get(bb4).contains(bb4));
    }
}