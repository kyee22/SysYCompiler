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
import frontend.llvm.type.FunctionType;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.user.User;
import frontend.llvm.value.user.constant.ConstantInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InstructionTest {
    private Module module;
    private Function function;
    private BasicBlock bb;
    private Instruction add;
    private Instruction sub;
    private Instruction mul;
    private Instruction div;
    private Instruction mod;

    @BeforeEach
    void setUp() {
        module = new Module();
        FunctionType functionType = module.getFunctionType(module.getVoidType(), List.of());
        function = Function.create(functionType, "foo", module);
        bb = BasicBlock.create(module, "bb", function);
        ConstantInt v1 = ConstantInt.get(110, module);
        ConstantInt v2 = ConstantInt.get(120, module);
        add = IBinaryInst.createAdd(v1, v2, bb);
        sub = IBinaryInst.createSub(add, v2, bb);
        mul = IBinaryInst.createMul(add, sub, bb);
        div = IBinaryInst.createSdiv(mul, sub, bb);
        //mod = IBinaryInst.cre
    }

    @Test
    void testPrint() {
        assertEquals("% = add i32 110, 120", add.print());
        assertEquals("% = sub i32 %, 120", sub.print());
        assertEquals("% = mul i32 %, %", mul.print());
        assertEquals("% = sdiv i32 %, %", div.print());

        function.setInstrName();

        assertTrue(add.isAdd());
        assertTrue(sub.isSub());
        assertTrue(mul.isMul());
        assertTrue(div.isDiv());
        assertFalse(sub.isAdd());
        assertFalse(sub.isVoid());

        assertTrue(add.getType().isInt32Type());
        assertTrue(sub.getType().isInt32Type());
        assertTrue(mul.getType().isInt32Type());
        assertTrue(div.getType().isInt32Type());

        assertEquals(2, add.getUseList().size());
        assertEquals(2, sub.getUseList().size());
        assertEquals(1, mul.getUseList().size());
        assertEquals(0, div.getUseList().size());

        assertEquals("%op1 = add i32 110, 120", add.print());
        assertEquals("%op2 = sub i32 %op1, 120", sub.print());
        assertEquals("%op3 = mul i32 %op1, %op2", mul.print());
        assertEquals("%op4 = sdiv i32 %op3, %op2", div.print());

        assertEquals(0, add.getUseList().get(0).getArgNo());
        assertEquals(1, sub.getUseList().get(1).getArgNo());
        assertEquals("op2", ((User) div.getOperand(0)).getOperand(1).getName());
        assertEquals("op3", add.getUseList().get(1).getUser().getName());
    }
}
