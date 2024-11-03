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

package frontend.llvm.value.user;

import frontend.llvm.Module;
import frontend.llvm.type.ArrayType;
import frontend.llvm.value.user.constant.Constant;
import frontend.llvm.value.user.constant.ConstantArray;
import frontend.llvm.value.user.constant.ConstantInt;
import frontend.llvm.value.user.constant.ConstantZero;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GlobalVariableTest {
    private Module module;
    GlobalVariable g1, g2, g3, g4;

    @BeforeEach
    void setUp() {
        module = new Module();
        assertEquals(0, module.getGlobalVariables().size());
        g1 = GlobalVariable.create("global1", module, module.getInt32Type(), true, ConstantInt.getInt(2024, module));
        g2 = GlobalVariable.create("global2", module, module.getInt32Type(), false, ConstantInt.getInt(1989, module));
        ArrayType arrayType = module.getArrayType(module.getInt32Type(), 2);
        ConstantArray constantArray = ConstantArray.get(arrayType, List.of(g1.getInit(), g1.getInit()));
        g3 = GlobalVariable.create("global3", module, arrayType, false, constantArray);
        assertEquals(3, module.getGlobalVariables().size());

        ArrayType arrayType2 = module.getArrayType(module.getInt32Type(), 10);
        List<Constant> value = new ArrayList<>(List.of(g1.getInit()));
        for (int i = value.size(); i < arrayType2.getNumberOfElements(); i++) {
            value.add(ConstantZero.get(arrayType2.getArrayElementType()));
        }
        ConstantArray constantArray2 = ConstantArray.get(arrayType2, value);
        g4 = GlobalVariable.create("global4", module, arrayType2, true, constantArray2);
        assertEquals(10, constantArray2.getNumOperand());
        assertEquals(1, g4.getNumOperand());
        assertEquals(4, g1.getInit().getUseList().size());
        assertEquals(0, g1.getInit().getUseList().get(0).getArgNo());
        assertEquals("global1", g1.getInit().getUseList().get(0).getUser().getName());
        assertEquals(0, g1.getInit().getUseList().get(1).getArgNo());
        assertEquals("", g1.getInit().getUseList().get(1).getUser().getName());
        assertEquals(1, g1.getInit().getUseList().get(2).getArgNo());
        assertEquals("", g1.getInit().getUseList().get(2).getUser().getName());
        assertEquals(0, g1.getInit().getUseList().get(3).getArgNo());
        assertEquals("", g1.getInit().getUseList().get(3).getUser().getName());
        assertEquals(4, module.getGlobalVariables().size());
    }

    @Test
    void testPrint() {
        assertEquals("@global1 = constant i32 2024", g1.print());
        assertEquals("@global2 = global i32 1989", g2.print());
        assertEquals("@global3 = global [2 x i32] [i32 2024, i32 2024]", g3.print());
        assertEquals("@global4 = constant [10 x i32] [i32 2024, i32 zeroinitializer, i32 zeroinitializer, i32 zeroinitializer, i32 zeroinitializer, i32 zeroinitializer, i32 zeroinitializer, i32 zeroinitializer, i32 zeroinitializer, i32 zeroinitializer]", g4.print());
    }
}