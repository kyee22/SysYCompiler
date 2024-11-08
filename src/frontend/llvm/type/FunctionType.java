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

import java.util.ArrayList;
import java.util.List;

public class FunctionType extends Type {
    private Type returnType;
    private List<Type> argumentTypes = new ArrayList<>();

    public FunctionType(Type returnType, List<Type> argumentTypes) {
        super(Type.TypeID.FunctionTyID, null); // Assuming Module not needed here
        if (!isValidReturnType(returnType)) {
            throw new IllegalArgumentException("Invalid return type for function!");
        }
        this.returnType = returnType;

        for (Type arg : argumentTypes) {
            if (!isValidArgumentType(arg)) {
                throw new IllegalArgumentException("Invalid argument type for function!");
            }
            this.argumentTypes.add(arg);
        }
    }

    private static boolean isValidReturnType(Type ty) {
        return ty.isIntegerType() || ty.isVoidType() || ty.isFloatType();
    }
    private static boolean isValidArgumentType(Type ty) {
        return ty.isIntegerType() || ty.isPointerType() || ty.isFloatType();
    }

    public static FunctionType get(Type returnType, List<Type> argumentTypes) {
        return returnType.getModule().getFunctionType(returnType, argumentTypes);
    }

    public int getNumberOfArgs() { return argumentTypes.size(); }
    public Type getParamType(int index) { return argumentTypes.get(index); }
    public Type getReturnType() { return returnType; }
    public List<Type> getArgumentTypes() { return argumentTypes; }
}

