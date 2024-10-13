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

import java.util.Set;

public class PointerType extends Type {
    private Type elementType;
    private static Set<TypeID> allowedTypes = Set.of(TypeID.IntegerTyID, TypeID.FloatTyID, TypeID.ArrayTyID, TypeID.PointerTyID);

    public PointerType(Type elementType) {
        super(Type.TypeID.PointerTyID, elementType.getModule());
        if (!allowedTypes.contains(elementType.getTypeId())) {
            throw new IllegalArgumentException("Not allowed type for pointer");
        }
        this.elementType = elementType;
    }

    public Type getElementType() { return elementType; }

    public static PointerType get(Type elementType) {
        return elementType.getModule().getPointerType(elementType);
    }
}

