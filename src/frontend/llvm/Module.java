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


import frontend.llvm.type.*;
import frontend.llvm.value.Function;
import frontend.llvm.value.user.GlobalVariable;

import java.util.*;

public class Module {
    private List<Function> functions = new ArrayList<>();

    private List<GlobalVariable> globalList = new ArrayList<>();

    private Type voidTy = new Type(Type.TypeID.VoidTyID, this);
    private Type labelTy = new Type(Type.TypeID.LabelTyID, this);
    private IntegerType int1Ty = new IntegerType(1, this);
    private IntegerType int32Ty = new IntegerType(32, this);
    private FloatType float32Ty = new FloatType(this);


    private Map<Type, PointerType> pointerTyCache = new HashMap<>();
    private Map<Type, Map<Integer, ArrayType>> arrayTyCache = new HashMap<>();
    private Map<Type, Map<List<Type>, FunctionType>> functionTyCache = new HashMap<>();

    //private Map<Pai>
    // todo: hash code, equals, pair

    public Type getVoidType() {return voidTy;}
    public Type getLabelType() {return labelTy;}
    public IntegerType getInt1Type() {return int1Ty;}
    public IntegerType getInt32Type() {return int32Ty;}
    public FloatType getFloatType() {return float32Ty;}
    public PointerType getInt32PointerType() {return getPointerType(int32Ty);}
    public PointerType getFloatPointerType() {return getPointerType(float32Ty);}

    public PointerType getPointerType(Type elementType) {
        if (!pointerTyCache.containsKey(elementType)) {
            pointerTyCache.put(elementType, new PointerType(elementType));
        }
        return pointerTyCache.get(elementType);
    }

    public ArrayType getArrayType(Type elementType, int numElements) {
        if (!arrayTyCache.containsKey(elementType)) {
            arrayTyCache.put(elementType, new HashMap<>());
        }
        if (!arrayTyCache.get(elementType).containsKey(numElements)) {
            arrayTyCache.get(elementType).put(numElements, new ArrayType(elementType, numElements));
        }
        return arrayTyCache.get(elementType).get(numElements);
    }

    public FunctionType getFunctionType(Type returnType, List<Type> argumentTypes) {
        if (!functionTyCache.containsKey(returnType)) {
            functionTyCache.put(returnType, new HashMap<>());
        }
        if (!functionTyCache.get(returnType).containsKey(argumentTypes)) {
            functionTyCache.get(returnType).put(argumentTypes, new FunctionType(returnType, argumentTypes));
        }
        return functionTyCache.get(returnType).get(argumentTypes);
    }

    public void addGlobalVariable(GlobalVariable globalVariable) {
        globalList.add(globalVariable);
    }

    public List<GlobalVariable> getGlobalVariables() {
        return globalList;
    }

    public void addFunction(Function function) {
        functions.add(function);
    }

    public List<Function> getFunctions() {
        return functions;
    }
}
