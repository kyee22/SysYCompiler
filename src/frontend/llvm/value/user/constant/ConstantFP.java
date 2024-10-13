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

import frontend.llvm.type.Type;
import frontend.llvm.Module;

import java.util.HashMap;
import java.util.Map;

public class ConstantFP extends Constant {
    private final float value;

    private ConstantFP(Type type, float value) {
        super(type, "");
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    public String print() {
        double val = value;
        return String.format("0x%x", Double.doubleToLongBits(val));
    }

    private static final Map<Module, Map<Float, ConstantFP>> floatCache = new HashMap<>();

    public static ConstantFP get(float val, Module module) {
        if (!floatCache.containsKey(module)) {
            floatCache.put(module, new HashMap<>());
        }
        return floatCache.get(module).computeIfAbsent(val, k -> new ConstantFP(module.getFloatType(), k));
    }
}
