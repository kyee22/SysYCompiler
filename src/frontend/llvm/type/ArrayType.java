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

package frontend.llvm.type;

public class ArrayType extends Type {
    private Type elementType;
    private int numberOfElements;

    public ArrayType(Type elementType, int numberOfElements) {
        super(Type.TypeID.ArrayTyID, elementType.getModule()); // Assuming Module not needed here
        if (!isValidElementType(elementType)) {
            throw new IllegalArgumentException("Not a valid type for array element!");
        }
        this.elementType = elementType;
        this.numberOfElements = numberOfElements;
    }

    private static boolean isValidElementType(Type ty) {
        return ty.isIntegerType() || ty.isArrayType() || ty.isFloatType();
    }

    public static ArrayType get(Type elementType, int numberOfElements) {
        return elementType.getModule().getArrayType(elementType, numberOfElements);
    }

    public Type getElementType() { return elementType; }

    public int getNumberOfElements() { return numberOfElements; }
}
