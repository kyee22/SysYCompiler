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

import frontend.llvm.type.IntegerType;
import frontend.llvm.type.Type;

import java.util.HashMap;
import java.util.Map;
import frontend.llvm.Module;

public class ConstantInt extends Constant {
    private final int value;
    private static Map<Module, Map<Integer, ConstantInt>> intCache = new HashMap<>();
    private static Map<Module, Map<Boolean, ConstantInt>> boolCache = new HashMap<>();

    private ConstantInt(Type type, int value) {
        super(type, "");
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String print() {
        Type type = getType();
        if (type instanceof IntegerType) {
            IntegerType intType = (IntegerType) type;
            if (intType.getNumBits() == 1) {
                return value == 0 ? "false" : "true";
            } else {
                return Integer.toString(value);
            }
        }
        return "";
    }

    public static ConstantInt get(int val, Module module) {
        if (!intCache.containsKey(module)) {
            intCache.put(module, new HashMap<>());
        }
        return intCache.get(module).computeIfAbsent(val, k -> new ConstantInt(module.getInt32Type(), val));
    }

    public static ConstantInt get(boolean val, Module module) {
        if (!boolCache.containsKey(module)) {
            boolCache.put(module, new HashMap<>());
        }
        return boolCache.get(module).computeIfAbsent(val, k -> new ConstantInt(module.getInt1Type(), val ? 1 : 0));
    }
}
