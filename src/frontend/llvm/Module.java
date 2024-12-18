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
    private IntegerType int8Ty = new IntegerType(8, this);
    private IntegerType int32Ty = new IntegerType(32, this);
    private FloatType float32Ty = new FloatType(this);

    private Map<Type, PointerType> pointerTyCache = new HashMap<>();
    private Map<Type, Map<Integer, ArrayType>> arrayTyCache = new HashMap<>();
    private Map<Type, Map<List<Type>, FunctionType>> functionTyCache = new HashMap<>();

    // todo: hash code, equals, pair

    /******************************** api about type ********************************/
    public Type getVoidType() {return voidTy;}
    public Type getLabelType() {return labelTy;}
    public IntegerType getInt1Type() {return int1Ty;}
    public IntegerType getInt8Type() {return int8Ty;}
    public IntegerType getInt32Type() {return int32Ty;}
    public FloatType getFloatType() {return float32Ty;}
    public PointerType getInt32PointerType() {return getPointerType(int32Ty);}
    public PointerType getInt8PointerType() {return getPointerType(int8Ty);}
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

    /******************************** api about global variables ********************************/
    public void addGlobalVariable(GlobalVariable globalVariable) {globalList.add(globalVariable);}
    public List<GlobalVariable> getGlobalVariables() {return globalList;}

    /******************************** api about functions ********************************/
    public void addFunction(Function function) {functions.add(function);}
    public List<Function> getFunctions() {return functions;}

    /******************************** api about output ********************************/
    public void setPrintName() {
        for (Function function : functions) {
            function.setInstrName();
        }
    }

    public String print() {
        setPrintName();
        StringBuilder sb = new StringBuilder();
        for (Function function: functions) {
            if (function.isDeclaration()) {
                sb.append(function.print());
            }
        }
        sb.append("\n");
        for (GlobalVariable globalVariablel: globalList) {
            sb.append(globalVariablel.print())
              .append("\n");
        }
        sb.append("\n");
        for (Function function: functions) {
            if (!function.isDeclaration()) {
                sb.append(function.print()).append("\n\n");
            }
        }
        return sb.toString();
    }

    public void removeUnreachedInsts() {
        for (Function function : getFunctions()) {
            function.removeUnreachedInsts();
        }
    }
}
