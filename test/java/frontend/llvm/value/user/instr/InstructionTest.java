/*
 * SysYCompiler: A Compiler for SysY.
 *
 * SysYCompiler is an individually developed course project
 * for Compiling Techniques @ School of Computer Science &
 * Engineering, Beihang University, Fall 2024.
 *c
 * Copyright (C) 2024 Yixuan Kuang <kyee22@buaa.edu.cn>
 *
 * This file is part of SysYCompiler.
 */

package frontend.llvm.value.user.instr;

import frontend.llvm.Module;
import frontend.llvm.type.ArrayType;
import frontend.llvm.type.FunctionType;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.user.User;
import frontend.llvm.value.user.constant.ConstantFP;
import frontend.llvm.value.user.constant.ConstantInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InstructionTest {
    private Module module;
    private Function function;
    private Function function2;
    private BasicBlock bb;
    private Instruction add;
    private Instruction sub;
    private Instruction mul;
    private Instruction div;
    private Instruction mod;
    private Instruction icmp;
    private Instruction call1, call2;
    private Instruction br, cond_br;
    private Instruction voidret, ret;
    private Instruction add2;
    private Instruction gpt1, gpt2, gpt3;
    private Instruction alloca1, alloca2;

    @BeforeEach
    void setUp() {
        module = new Module();
        FunctionType functionType = module.getFunctionType(module.getVoidType(), List.of());
        function = Function.create(functionType, "foo", module);
        bb = BasicBlock.create(module, "hello", function);
        ConstantInt v1 = ConstantInt.getInt(110, module);
        ConstantInt v2 = ConstantInt.getInt(120, module);
        add = IBinaryInst.createAdd(v1, v2, bb);
        sub = IBinaryInst.createSub(add, v2, bb);
        mul = IBinaryInst.createMul(add, sub, bb);
        div = IBinaryInst.createSdiv(mul, sub, bb);
        icmp = ICmpInst.createEq(mul, div, bb);
        voidret = ReturnInst.createVoidRet(bb);

        call1 = CallInstr.createCall(function, List.of(), bb);


        functionType = module.getFunctionType(module.getFloatType(), List.of(module.getInt32Type(), module.getInt1Type()));
        function2 = Function.create(functionType, "bar", module);
        call2 = CallInstr.createCall(function2, List.of(mul, icmp), bb);

        BasicBlock bb1 = BasicBlock.create(module, "good", function);
        BasicBlock bb2 = BasicBlock.create(module, "", function);
        br = BranchInst.createBr(bb1, bb);
        cond_br = BranchInst.createCondBr(icmp, bb2, bb1, bb);
        assertEquals(3, bb.getSuccBasicBlocks().size());
        assertEquals(1, bb2.getPrevBasicBlocks().size());
        assertEquals(2, bb1.getPrevBasicBlocks().size());

        BasicBlock func2_bb = BasicBlock.create(module, "", function2);
        add2 = IBinaryInst.createAdd(v2, v1, func2_bb);
        ConstantFP fp = ConstantFP.get(1.1f, module);
        ret = ReturnInst.createRet(fp, func2_bb);

        ArrayType arr1dTy = module.getArrayType(module.getInt32Type(), 20);
        ArrayType arr2dTy = module.getArrayType(arr1dTy, 10);
        ArrayType arr3dTy = module.getArrayType(arr2dTy, 5);

        alloca1 = AllocaInst.createAlloca(arr3dTy, bb);
        gpt1 = GetElementPtrInst.createGep(alloca1, List.of(ConstantInt.getInt(0, module), ConstantInt.getInt(1, module), ConstantInt.getInt(2, module), ConstantInt.getInt(3,module)), bb);
        gpt2 = GetElementPtrInst.createGep(alloca1, List.of(ConstantInt.getInt(0, module), ConstantInt.getInt(1, module)), bb);
        gpt3 = GetElementPtrInst.createGep(alloca1, List.of(ConstantInt.getInt(0, module), add, sub), bb);
    }


    @Test
    void testPrint() {
        assertEquals("% = add i32 110, 120", add.print());
        assertEquals("% = sub i32 %, 120", sub.print());
        assertEquals("% = mul i32 %, %", mul.print());
        assertEquals("% = sdiv i32 %, %", div.print());
        assertEquals("% = icmp eq i32 %, %", icmp.print());
        assertEquals("call void @foo()", call1.print());
        assertEquals("% = call float @bar(i32 %, i1 %)", call2.print());
        assertEquals("br label %label_good", br.print());
        assertEquals("br i1 %, label %, label %label_good", cond_br.print());
        assertEquals("ret void", voidret.print());
        assertEquals("% = alloca [5 x [10 x [20 x i32]]]", alloca1.print());
        assertEquals("% = getelementptr [5 x [10 x [20 x i32]]], [5 x [10 x [20 x i32]]]* %, i32 0, i32 1, i32 2, i32 3", gpt1.print());
        assertEquals("% = getelementptr [5 x [10 x [20 x i32]]], [5 x [10 x [20 x i32]]]* %, i32 0, i32 1", gpt2.print());
        assertEquals("% = getelementptr [5 x [10 x [20 x i32]]], [5 x [10 x [20 x i32]]]* %, i32 0, i32 %, i32 %", gpt3.print());

        assertEquals("% = add i32 120, 110", add2.print());
        assertEquals("ret float 0x3ff19999a0000000", ret.print());

        function.setInstrName();
        function2.setInstrName();

        assertTrue(add.isAdd());
        assertTrue(sub.isSub());
        assertTrue(mul.isMul());
        assertTrue(div.isDiv());
        assertFalse(sub.isAdd());
        assertFalse(sub.isVoid());
        assertTrue(icmp.isCmp());
        assertFalse(icmp.isFCmp());
        assertTrue(call1.isVoid());
        assertFalse(call2.isVoid());
        assertTrue(br.isBr());
        assertTrue(br.isVoid());
        assertTrue(cond_br.isBr());
        assertTrue(cond_br.isVoid());
        assertTrue(ret.isRet());
        assertTrue(ret.isVoid());
        assertTrue(alloca1.isAlloca());
        assertFalse(alloca1.isVoid());
        assertTrue(gpt1.isGEP());
        assertTrue(gpt2.isGEP());
        assertTrue(gpt3.isGEP());
        assertFalse(gpt2.isVoid());
        assertFalse(gpt2.isVoid());
        assertFalse(gpt3.isVoid());


        assertTrue(add.getType().isInt32Type());
        assertTrue(sub.getType().isInt32Type());
        assertTrue(mul.getType().isInt32Type());
        assertTrue(div.getType().isInt32Type());
        assertTrue(icmp.getType().isInt1Type());
        assertTrue(call1.getType().isVoidType());
        assertTrue(call2.getType().isFloatType());
        assertTrue(br.getType().isVoidType());
        assertTrue(cond_br.getType().isVoidType());
        assertTrue(alloca1.getType().isPointerType());
        assertTrue(gpt1.getType().isPointerType());
        assertTrue(gpt1.getType().getPointerElementType().isInt32Type());
        assertTrue(gpt2.getType().isPointerType());
        assertTrue(gpt2.getType().getPointerElementType().isArrayType());
        assertTrue(gpt2.getType().getPointerElementType().getArrayElementType().isArrayType());
        assertTrue(gpt2.getType().getPointerElementType().getArrayElementType().getArrayElementType().isInt32Type());
        assertTrue(gpt3.getType().isPointerType());
        assertTrue(gpt3.getType().getPointerElementType().isArrayType());
        assertTrue(gpt3.getType().getPointerElementType().getArrayElementType().isInt32Type());


        assertEquals(3, add.getUseList().size());
        assertEquals(3, sub.getUseList().size());
        assertEquals(3, mul.getUseList().size());
        assertEquals(1, div.getUseList().size());
        assertEquals(2, icmp.getUseList().size());
        assertEquals(3, alloca1.getUseList().size());
        assertEquals(5, gpt1.getNumOperand());
        assertEquals(3, gpt2.getNumOperand());
        assertEquals(4, gpt3.getNumOperand());

        assertEquals("%op1 = add i32 110, 120", add.print());
        assertEquals("%op2 = sub i32 %op1, 120", sub.print());
        assertEquals("%op3 = mul i32 %op1, %op2", mul.print());
        assertEquals("%op4 = sdiv i32 %op3, %op2", div.print());
        assertEquals("%op5 = icmp eq i32 %op3, %op4", icmp.print());
        assertEquals("call void @foo()", call1.print());
        assertEquals("%op6 = call float @bar(i32 %op3, i1 %op5)", call2.print());
        assertEquals("br label %label_good", br.print());
        assertEquals("br i1 %op5, label %label12, label %label_good", cond_br.print());
        assertEquals("ret void", voidret.print());
        assertEquals("%op7 = alloca [5 x [10 x [20 x i32]]]", alloca1.print());
        assertEquals("%op8 = getelementptr [5 x [10 x [20 x i32]]], [5 x [10 x [20 x i32]]]* %op7, i32 0, i32 1, i32 2, i32 3", gpt1.print());
        assertEquals("%op9 = getelementptr [5 x [10 x [20 x i32]]], [5 x [10 x [20 x i32]]]* %op7, i32 0, i32 1", gpt2.print());
        assertEquals("%op10 = getelementptr [5 x [10 x [20 x i32]]], [5 x [10 x [20 x i32]]]* %op7, i32 0, i32 %op1, i32 %op2", gpt3.print());


        assertEquals(0, add.getUseList().get(0).getArgNo());
        assertEquals(1, sub.getUseList().get(1).getArgNo());
        assertEquals("op2", ((User) div.getOperand(0)).getOperand(1).getName());
        assertEquals("op3", add.getUseList().get(1).getUser().getName());

        assertEquals("%op3 = add i32 120, 110", add2.print());
        assertEquals("ret float 0x3ff19999a0000000", ret.print());

    }
}
