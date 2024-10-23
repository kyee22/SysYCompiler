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

package frontend.llvm;

import frontend.llvm.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TypeTest {

    private Module module;
    private Type voidTy;
    private Type another_voidTy;
    private Type labelTy;
    private Type another_labelTy;
    private Type i1Ty;
    private Type another_i1Ty;
    private Type i32Ty;
    private Type another_i32Ty;
    private Type float32Ty;
    private Type another_float32Ty;
    private Type int32PtrTy;
    private Type another_int32PtrTy;
    private Type floatPtrTy;
    private Type another_floatPtrTy;
    private Type p1, p2, p3, p4, p5, p6, p7;
    private Type arr1, arr2, arr3, arr4, arr5, arr6, arr7;
    private Type f1, f2, f3, f4, f5, f6, f7;
    private Type complex1,complex2,complex3,complex4;

    @BeforeEach
    public void setUp() {
        module = new Module();
         voidTy = module.getVoidType();
         another_voidTy = module.getVoidType();
         labelTy = module.getLabelType();
         another_labelTy = module.getLabelType();
         i1Ty = module.getInt1Type();
         another_i1Ty = module.getInt1Type();
         i32Ty = module.getInt32Type();
         another_i32Ty = module.getInt32Type();
         float32Ty = module.getFloatType();
         another_float32Ty = module.getFloatType();
         int32PtrTy = module.getInt32PointerType();
         another_int32PtrTy = module.getInt32PointerType();
         floatPtrTy = module.getFloatPointerType();
         another_floatPtrTy = module.getFloatPointerType();

         arr1 = module.getArrayType(i32Ty, 10);
         arr2 = module.getArrayType(i1Ty, 20);
         arr3 = module.getArrayType(float32Ty, 30);
         arr4 = module.getArrayType(arr1, 10);
         arr5 = module.getArrayType(arr4, 20);
         arr6 = module.getArrayType(arr5, 10);
         arr7 = module.getArrayType(arr6, 1);

         p1 = module.getPointerType(i32Ty);
         p2 = module.getPointerType(i1Ty);
         p3 = module.getPointerType(float32Ty);
         p4 = module.getPointerType(arr1);
         p5 = module.getPointerType(arr2);
         p6 = module.getPointerType(p5);
         p7 = module.getPointerType(p6);

         f1 = module.getFunctionType(voidTy, List.of());
         f2 = module.getFunctionType(float32Ty, List.of());
         f3 = module.getFunctionType(i32Ty, List.of(int32PtrTy, int32PtrTy));
         f4 = module.getFunctionType(float32Ty, List.of(float32Ty, floatPtrTy, floatPtrTy));
         f5 = module.getFunctionType(voidTy, List.of(int32PtrTy, i32Ty, int32PtrTy, another_i32Ty));
         f6 = module.getFunctionType(i32Ty, List.of(int32PtrTy));
         f7 = module.getFunctionType(i1Ty, List.of(int32PtrTy, int32PtrTy));

         complex1 = module.getFunctionType(i32Ty, List.of(another_i32Ty, floatPtrTy, another_float32Ty));
         complex2 = module.getFunctionType(another_i32Ty, List.of(i32Ty, another_floatPtrTy, float32Ty));

         Type tmp1 = module.getArrayType(i32Ty, 5);
         Type tmp2 = module.getArrayType(another_i32Ty, 5);
         Type tmp3 = module.getArrayType(tmp2, 10);
         Type tmp4 = module.getArrayType(tmp1, 10);
         Type tmp5 = module.getPointerType(tmp3);
         Type tmp6 = module.getPointerType(tmp4);
         Type tmp7 = module.getPointerType(tmp5);
         Type tmp8 = module.getPointerType(tmp6);
         complex3 = module.getFunctionType(float32Ty, List.of(int32PtrTy, tmp5, tmp8));
         complex4 = module.getFunctionType(another_float32Ty, List.of(module.getPointerType(module.getInt32Type()), tmp6, tmp7));
    }


    @Test
    void getSize() {
        assertThrows(RuntimeException.class, voidTy::getSize);
        assertThrows(RuntimeException.class, another_voidTy::getSize);
        assertThrows(RuntimeException.class, labelTy::getSize);
        assertThrows(RuntimeException.class, another_labelTy::getSize);
        assertThrows(RuntimeException.class, f1::getSize);
        assertThrows(RuntimeException.class, f2::getSize);
        assertThrows(RuntimeException.class, f3::getSize);
        assertThrows(RuntimeException.class, f4::getSize);
        assertThrows(RuntimeException.class, f5::getSize);
        assertThrows(RuntimeException.class, f6::getSize);
        assertThrows(RuntimeException.class, f7::getSize);
        assertDoesNotThrow(int32PtrTy::getSize);
        assertEquals(1, i1Ty.getSize());
        assertEquals(1, another_i1Ty.getSize());
        assertEquals(4, i32Ty.getSize());
        assertEquals(4, another_i32Ty.getSize());
        assertEquals(4, float32Ty.getSize());
        assertEquals(4, another_float32Ty.getSize());
        assertEquals(8, int32PtrTy.getSize());
        assertEquals(8, another_int32PtrTy.getSize());
        assertEquals(8, floatPtrTy.getSize());
        assertEquals(8, another_floatPtrTy.getSize());

        assertEquals(40, arr1.getSize());
        assertEquals(20, arr2.getSize());
        assertEquals(120, arr3.getSize());
        assertEquals(400, arr4.getSize());
        assertEquals(8000, arr5.getSize());
        assertEquals(80000, arr6.getSize());
        assertEquals(80000, arr7.getSize());

        assertEquals(8, p1.getSize());
        assertEquals(8, p2.getSize());
        assertEquals(8, p3.getSize());
        assertEquals(8, p4.getSize());
        assertEquals(8, p5.getSize());
        assertEquals(8, p6.getSize());
        assertEquals(8, p7.getSize());

        assertTrue(complex1 == complex2);
        assertTrue(complex3 == complex4);
    }

    @Test
    void testPrint() {
        assertEquals("void", voidTy.print());
        assertEquals("void", another_voidTy.print());
        assertEquals("label", labelTy.print());
        assertEquals("label", another_labelTy.print());
        assertEquals("i1", i1Ty.print());
        assertEquals("i1", another_i1Ty.print());
        assertEquals("i32", i32Ty.print());
        assertEquals("i32", another_i32Ty.print());
        assertEquals("float", float32Ty.print());
        assertEquals("float", another_float32Ty.print());
        assertEquals("i32*", int32PtrTy.print());
        assertEquals("i32*", another_int32PtrTy.print());
        assertEquals("float*", floatPtrTy.print());
        assertEquals("float*", another_floatPtrTy.print());

        assertEquals("[10 x i32]", arr1.print());
        assertEquals("[20 x i1]", arr2.print());
        assertEquals("[30 x float]", arr3.print());
        assertEquals("[10 x [10 x i32]]", arr4.print());
        assertEquals("[20 x [10 x [10 x i32]]]", arr5.print());
        assertEquals("[10 x [20 x [10 x [10 x i32]]]]", arr6.print());
        assertEquals("[1 x [10 x [20 x [10 x [10 x i32]]]]]", arr7.print());

        assertEquals("i32*", p1.print());
        assertEquals("i1*", p2.print());
        assertEquals("float*", p3.print());
        assertEquals("[10 x i32]*", p4.print());
        assertEquals("[20 x i1]*", p5.print());
        assertEquals("[20 x i1]**", p6.print());
        assertEquals("[20 x i1]***", p7.print());

        assertEquals("void ()", f1.print());
        assertEquals("float ()", f2.print());
        assertEquals("i32 (i32*, i32*)", f3.print());
        assertEquals("float (float, float*, float*)", f4.print());
        assertEquals("void (i32*, i32, i32*, i32)", f5.print());
        assertEquals("i32 (i32*)", f6.print());
        assertEquals("i1 (i32*, i32*)", f7.print());
    }

    @Test
    public void testIsXXX() {
        assertTrue(voidTy.isVoidType());
        assertTrue(another_voidTy.isVoidType());
        assertFalse(labelTy.isVoidType());
        assertFalse(another_labelTy.isVoidType());
        assertFalse(i1Ty.isVoidType());
        assertTrue(labelTy.isLabelType());
        assertTrue(another_labelTy.isLabelType());
        assertTrue(i1Ty.isIntegerType());
        assertTrue(another_i1Ty.isInt1Type());
        assertFalse(another_i32Ty.isInt1Type());
        assertTrue(i32Ty.isIntegerType());
        assertTrue(float32Ty.isFloatType());
        assertFalse(float32Ty.isIntegerType());
        assertTrue(another_float32Ty.isFloatType());
        assertFalse(another_float32Ty.isIntegerType());
        assertTrue(another_float32Ty.isFloatType());
        assertTrue(int32PtrTy.isPointerType());
        assertTrue(another_int32PtrTy.isPointerType());
        assertTrue(floatPtrTy.isPointerType());
        assertTrue(another_floatPtrTy.isPointerType());
        assertTrue(arr1.isArrayType());
        assertTrue(arr2.isArrayType());
        assertFalse(arr3.isPointerType());
        assertFalse(arr4.isPointerType());
        assertTrue(p1.isPointerType());
        assertTrue(p2.isPointerType());
        assertTrue(p3.isPointerType());
        assertTrue(f1.isFunctionType());
    }


    @Test
    void getPointerElementType() {
        assertThrows(RuntimeException.class, arr1::getPointerElementType);
        assertThrows(RuntimeException.class, arr2::getPointerElementType);
        assertThrows(RuntimeException.class, i32Ty::getPointerElementType);
        assertThrows(RuntimeException.class, float32Ty::getPointerElementType);
        assertThrows(RuntimeException.class, another_float32Ty::getPointerElementType);
        assertTrue(p1.getPointerElementType().isInt32Type());
        assertTrue(p2.getPointerElementType().isInt1Type());
        assertTrue(p3.getPointerElementType().isFloatType());
        assertTrue(p4.getPointerElementType().isArrayType());
        assertTrue(p5.getPointerElementType().isArrayType());
        assertTrue(p6.getPointerElementType().isPointerType());
        assertTrue(p7.getPointerElementType().isPointerType());
    }

    @Test
    void getArrayElementType() {
        assertThrows(RuntimeException.class, p1::getArrayElementType);
        assertThrows(RuntimeException.class, p2::getArrayElementType);
        assertThrows(RuntimeException.class, i32Ty::getArrayElementType);
        assertThrows(RuntimeException.class, floatPtrTy::getArrayElementType);
        assertThrows(RuntimeException.class, float32Ty::getArrayElementType);
        assertThrows(RuntimeException.class, another_float32Ty::getArrayElementType);
        assertTrue(arr1.getArrayElementType().isInt32Type());
        assertTrue(arr2.getArrayElementType().isInt1Type());
        assertTrue(arr3.getArrayElementType().isFloatType());
        assertTrue(arr4.getArrayElementType().isArrayType());
        assertTrue(arr5.getArrayElementType().isArrayType());
        assertTrue(arr6.getArrayElementType().isArrayType());
        assertTrue(arr7.getArrayElementType().isArrayType());
    }
}