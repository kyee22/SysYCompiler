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

import frontend.llvm.IRBuilder;
import frontend.llvm.Module;
import frontend.llvm.type.FunctionType;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import midend.analysis.dataflow.fact.DataflowResult;
import midend.analysis.dataflow.fact.SetFact;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DominationAnalysisTest {
    Module module;
    Function function;
    IRBuilder irBuilder;
    DataflowResult<BasicBlock, SetFact<BasicBlock>> result;
    @BeforeEach
    void setUp() {
        module = new Module();
        FunctionType funcTy = module.getFunctionType(module.getVoidType(), List.of(module.getInt32Type()));
        function = Function.create(funcTy, "func", module);
        irBuilder = new IRBuilder(module, null);
    }

    @AfterEach
    void tearDown() {
        System.out.println("====================== domination analysis ======================");
        function.setInstrName();
        for (BasicBlock bb : function.getBasicBlocks()) {
            System.out.print(bb.getName() + " ");
            System.out.println(result.getOutFact(bb));
        }
    }

    @Test
    public void testDominationAnalysis1() {
        // 测试 CFG 示例来自 https://judge.buaa.edu.cn/customizedColumn/chapterMD.jsp?columnID=873&subColumnID=901
        BasicBlock A = BasicBlock.create(module, "A", function);
        BasicBlock B = BasicBlock.create(module, "B", function);
        BasicBlock C = BasicBlock.create(module, "C", function);
        BasicBlock D = BasicBlock.create(module, "D", function);
        BasicBlock E = BasicBlock.create(module, "E", function);

        A.addSuccBasicBlock(B);
        A.addSuccBasicBlock(C);

        B.addSuccBasicBlock(D);

        C.addSuccBasicBlock(D);
        C.addSuccBasicBlock(E);

        D.addSuccBasicBlock(A);
        D.addSuccBasicBlock(E);

        irBuilder.setInsertPoint(E);
        irBuilder.createVoidRet();

        DominationAnalysis dominationAnalysis = new DominationAnalysis();
        result = dominationAnalysis.analyze(function);
        function.setDomination(result);

        assertTrue(B.isStrictlyDominatedBy(A));
        assertFalse(B.isStrictlyDominatedBy(B));
        assertEquals(A, E.getImmediateDominator());
    }

    @Test
    public void testDominationAnalysis2() {
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

        DominationAnalysis dominationAnalysis = new DominationAnalysis();
        result = dominationAnalysis.analyze(function);
        function.setDomination(result);

        assertTrue(bb7.isStrictlyDominatedBy(bb1));
        assertTrue(bb7.isStrictlyDominatedBy(bb2));
        assertEquals(bb2, bb7.getImmediateDominator());

        assertFalse(bb5.isStrictlyDominatedBy(bb5));
        assertTrue(bb5.isStrictlyDominatedBy(bb4));
        assertTrue(bb5.isStrictlyDominatedBy(bb2));
        assertTrue(bb5.isStrictlyDominatedBy(bb1));
        assertFalse(bb5.isStrictlyDominatedBy(bb3));
        assertEquals(bb4, bb5.getImmediateDominator());

        assertTrue(bb8.isStrictlyDominatedBy(bb2));
        assertFalse(bb8.isStrictlyDominatedBy(bb4));
        assertEquals(bb7, bb8.getImmediateDominator());
        assertEquals(3, bb8.getStrictDominators().size()); // label_entry excluded

        assertEquals(bb4, bb6.getImmediateDominator());
    }

    @Test
    public void testDominationAnalysis3() {
        // 测试 CFG 样例来自 https://www.penlab.me/2020/12/06/loops/
        BasicBlock bb1 = BasicBlock.create(module, "1", function);
        BasicBlock bb2 = BasicBlock.create(module, "2", function);
        BasicBlock bb3 = BasicBlock.create(module, "3", function);
        BasicBlock bb4 = BasicBlock.create(module, "4", function);
        BasicBlock bb5 = BasicBlock.create(module, "5", function);
        BasicBlock bb6 = BasicBlock.create(module, "6", function);
        BasicBlock bb7 = BasicBlock.create(module, "7", function);
        BasicBlock bb8 = BasicBlock.create(module, "8", function);
        BasicBlock bb9 = BasicBlock.create(module, "9", function);
        BasicBlock bb10 = BasicBlock.create(module, "10", function);

        bb1.addSuccBasicBlock(bb2);
        bb1.addSuccBasicBlock(bb3);

        bb2.addSuccBasicBlock(bb3);

        bb3.addSuccBasicBlock(bb4);

        bb4.addSuccBasicBlock(bb3);
        bb4.addSuccBasicBlock(bb5);
        bb4.addSuccBasicBlock(bb6);

        bb5.addSuccBasicBlock(bb7);

        bb6.addSuccBasicBlock(bb7);

        bb7.addSuccBasicBlock(bb4);
        bb7.addSuccBasicBlock(bb8);

        bb8.addSuccBasicBlock(bb3);
        bb8.addSuccBasicBlock(bb9);
        bb8.addSuccBasicBlock(bb10);

        bb9.addSuccBasicBlock(bb1);

        bb10.addSuccBasicBlock(bb7);

        DominationAnalysis dominationAnalysis = new DominationAnalysis();
        result = dominationAnalysis.analyze(function);
        function.setDomination(result);

        assertEquals(null, bb1.getImmediateDominator());   // label_entry excluded
        assertEquals(bb1, bb2.getImmediateDominator());
        assertEquals(bb1, bb3.getImmediateDominator());
        assertEquals(bb3, bb4.getImmediateDominator());
        assertEquals(bb4, bb5.getImmediateDominator());
        assertEquals(bb4, bb6.getImmediateDominator());
        assertEquals(bb4, bb7.getImmediateDominator());
        assertEquals(bb7, bb8.getImmediateDominator());
        assertEquals(bb8, bb9.getImmediateDominator());
        assertEquals(bb8, bb10.getImmediateDominator());
    }

    @Test
    public void testDominationAnalysis4() {
        // 测试 CFG 样例来自 https://oiwiki.org/graph/dominator-tree/
        BasicBlock bb1 = BasicBlock.create(module, "1", function);
        BasicBlock bb2 = BasicBlock.create(module, "2", function);
        BasicBlock bb3 = BasicBlock.create(module, "3", function);
        BasicBlock bb4 = BasicBlock.create(module, "4", function);
        BasicBlock bb5 = BasicBlock.create(module, "5", function);
        BasicBlock bb6 = BasicBlock.create(module, "6", function);
        BasicBlock bb7 = BasicBlock.create(module, "7", function);
        BasicBlock bb8 = BasicBlock.create(module, "8", function);
        BasicBlock bb9 = BasicBlock.create(module, "9", function);

        bb1.addSuccBasicBlock(bb2);

        bb2.addSuccBasicBlock(bb3);
        bb2.addSuccBasicBlock(bb5);
        bb2.addSuccBasicBlock(bb6);

        bb3.addSuccBasicBlock(bb4);

        bb4.addSuccBasicBlock(bb5);

        bb6.addSuccBasicBlock(bb7);
        bb6.addSuccBasicBlock(bb9);

        bb7.addSuccBasicBlock(bb8);

        bb9.addSuccBasicBlock(bb8);

        irBuilder.setInsertPoint(bb8);
        irBuilder.createVoidRet();

        irBuilder.setInsertPoint(bb5);
        irBuilder.createVoidRet();

        DominationAnalysis dominationAnalysis = new DominationAnalysis();
        result = dominationAnalysis.analyze(function);
        function.setDomination(result);

        assertEquals(bb1, bb2.getImmediateDominator());
        assertEquals(bb2, bb3.getImmediateDominator());
        assertEquals(bb3, bb4.getImmediateDominator());
        assertEquals(bb2, bb5.getImmediateDominator());
        assertEquals(bb2, bb6.getImmediateDominator());
        assertEquals(bb6, bb7.getImmediateDominator());
        assertEquals(bb6, bb8.getImmediateDominator());
        assertEquals(bb6, bb9.getImmediateDominator());
    }
}