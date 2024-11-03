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

package frontend.llvm.value.user.constant;

import frontend.llvm.Module;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConstantTest {
    private Module module;
    private Constant const_i32;
    private Constant another_const_i32;
    private Constant const_i1;
    private Constant const_float;
    private Constant const_zero;
    private Constant const_arr;

    @BeforeEach
    void setUp() {
        module = new Module();
        const_i32 = ConstantInt.getInt(2024, module);
        another_const_i32 = ConstantInt.getInt(1189, module);
        const_i1 = ConstantInt.getBool(true, module);
        const_float = ConstantFP.get(114.514f, module);
        const_zero = ConstantZero.get(module.getInt32Type());
        const_arr = ConstantArray.get(module.getArrayType(module.getInt32Type(), 2), List.of(const_i32, another_const_i32));
    }

    @Test
    void testPrint() {
        assertEquals("2024", const_i32.print());
        assertEquals("1189", another_const_i32.print());
        assertEquals("true", const_i1.print());
        assertEquals("0x405ca0e560000000", const_float.print());
        assertEquals("[i32 2024, i32 1189]", const_arr.print());
    }

    @Test
    void testGetVal() {
        assertEquals(2024, ((ConstantInt) const_i32).getValue());
        assertEquals(1189, ((ConstantInt) another_const_i32).getValue());
        assertEquals(1, ((ConstantInt) const_i1).getValue());
        assertEquals(114.514f, ((ConstantFP) const_float).getValue());
        assertEquals(2, ((ConstantArray) const_arr).getSizeOfArray());
        assertEquals(2, const_arr.getNumOperand());
        assertEquals(1, const_i32.getUseList().size());
        assertEquals(1, another_const_i32.getUseList().size());
        assertEquals(0, const_i1.getUseList().size());
        assertEquals(2024, ((ConstantInt)(((ConstantArray) const_arr).getElementValue(0))).getValue());
        assertEquals(1189, ((ConstantInt)(((ConstantArray) const_arr).getElementValue(1))).getValue());
    }
}