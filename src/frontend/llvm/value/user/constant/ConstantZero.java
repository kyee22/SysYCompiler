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

import java.util.HashMap;
import java.util.Map;

public class ConstantZero extends Constant {
    private ConstantZero(Type type) {
        super(type, "");
    }

    @Override
    public String print() {
        return "zeroinitializer";
    }

    private static final Map<Type, ConstantZero> cache = new HashMap<>();

    public static ConstantZero get(Type type) {
        return cache.computeIfAbsent(type, t -> new ConstantZero(t));
    }

}
