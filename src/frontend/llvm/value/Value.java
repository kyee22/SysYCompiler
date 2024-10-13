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

package frontend.llvm.value;

import frontend.llvm.type.Type;
import frontend.llvm.value.user.User;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public abstract class Value {
    protected Type type;
    protected String name;
    protected List<Use> useList;

    public Value(Type type, String name) {
        this.type = type;
        this.name = name;
        this.useList = new LinkedList<>();
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public List<Use> getUseList() {
        return useList;
    }

    public boolean setName(String name) {
        if (this.name.isEmpty()) {
            this.name = name;
            return true;
        }
        return false;
    }

    public void addUse(User user, int argNo) {
        useList.add(new Use(user, argNo));
    }

    public void removeUse(User user, int argNo) {
        useList.removeIf(use -> use.equals(new Use(user, argNo)));
    }

    public void replaceAllUseWith(Value newVal) {
        if (this == newVal) return;
        for (Use use : useList) {
            use.getUser().setOperand(use.getArgNo(), newVal);
        }
    }

    public void replaceUseWithIf(Value newVal, Predicate<Use> shouldReplace) {
        if (this == newVal) return;
        for (Use use : useList) {
            if (!shouldReplace.test(use)) continue;
            use.getUser().setOperand(use.getArgNo(), newVal);
        }
    }

    //public abstract String print();// TODO: 所有的子类print实现之后

    public <T extends Value> T as(Class<T> clazz) {
        return clazz.cast(this);
    }

    public <T extends Value> boolean is(Class<T> clazz) {
        return clazz.isInstance(this);
    }
}
