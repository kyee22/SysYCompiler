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

package backend.core;

import frontend.llvm.Module;
import frontend.llvm.type.ArrayType;
import frontend.llvm.type.Type;
import frontend.llvm.value.user.constant.ConstantArray;
import frontend.llvm.value.user.constant.ConstantInt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static backend.core.ASMPrinter.print;
import static org.junit.jupiter.api.Assertions.*;

class ASMPrinterTest {
    private Module module;
    private ASMPrinter printer;
    private Type i32Ty;
    private Type i8Ty;
    private Type voidTy;
    private Type i32PtrTy;
    private Type i8PtrTy;
    private ArrayType i32ArrTy;
    private ArrayType i8ArrTy;
    
    @BeforeEach
    void setUp() {
        module = new Module();
        printer = new ASMPrinter();
        i32Ty = module.getInt32Type();
        i8Ty = module.getInt8Type();
        voidTy = module.getVoidType();
        i32PtrTy = module.getInt32PointerType();
        i8PtrTy = module.getInt8PointerType();
        i32ArrTy = module.getArrayType(i32Ty, 8);
        i8ArrTy = module.getArrayType(i8Ty, 6);
    }

    @Test
    public void testPrintType() {
        assertEquals(".word", print(i32Ty));
        assertEquals(".byte", print(i8Ty));
        assertEquals(".word", print(i32ArrTy));
        assertEquals(".byte", print(i8ArrTy));
        assertThrows(RuntimeException.class, () -> print(voidTy));
        assertThrows(RuntimeException.class, () -> print(i32PtrTy));
        assertThrows(RuntimeException.class, () -> print(i8PtrTy));
    }
    
    @Test
    public void testPrintConstant() {
        ConstantInt zero = ConstantInt.getInt(0, module);
        ConstantInt one = ConstantInt.getInt(1, module);
        ConstantInt two = ConstantInt.getInt(2, module);
        ConstantInt three = ConstantInt.getInt(3, module);
        ConstantInt four = ConstantInt.getInt(4, module);
        ConstantInt five = ConstantInt.getInt(5, module);
        ConstantInt six = ConstantInt.getInt(6, module);

        assertEquals("0", print(zero));
        assertEquals("1", print(one));
        assertEquals("2", print(two));
        assertEquals("3", print(three));
        assertEquals("4", print(four));
        assertEquals("5", print(five));
        assertEquals("6", print(six));

        ConstantInt a = ConstantInt.getChar('a', module);
        ConstantInt b = ConstantInt.getChar('b', module);
        ConstantInt u = ConstantInt.getChar('u', module);
        ConstantInt c = ConstantInt.getChar('c', module);
        ConstantInt comma = ConstantInt.getChar(',', module);
        ConstantInt o = ConstantInt.getChar('o', module);
        ConstantInt p = ConstantInt.getChar('p', module);
        ConstantInt i = ConstantInt.getChar('i', module);
        ConstantInt m = ConstantInt.getChar('m', module);
        ConstantInt l = ConstantInt.getChar('l', module);
        ConstantInt r = ConstantInt.getChar('r', module);
        ConstantInt e = ConstantInt.getChar('e', module);

        assertEquals("97", print(a)); // 'a'
        assertEquals("98", print(b)); // 'b'
        assertEquals("117", print(u)); // 'u'
        assertEquals("99", print(c)); // 'c'
        assertEquals("44", print(comma)); // ','
        assertEquals("111", print(o)); // 'o'
        assertEquals("112", print(p)); // 'p'
        assertEquals("105", print(i)); // 'i'
        assertEquals("109", print(m)); // 'm'
        assertEquals("108", print(l)); // 'l'
        assertEquals("114", print(r)); // 'r'
        assertEquals("101", print(e)); // 'e'


        ConstantArray intArr = ConstantArray.get(i32ArrTy, List.of(two, zero, two, four, one, one, zero, four));
        ConstantArray chrArr = ConstantArray.get(i8ArrTy, List.of(b, u, a, a, comma, c, o, m, p, i, l, e, r));

        assertEquals("2, 0, 2, 4, 1, 1, 0, 4", print(intArr));
        assertEquals("98, 117, 97, 97, 44, 99, 111, 109, 112, 105, 108, 101, 114", print(chrArr));
    }
}