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
import frontend.llvm.value.user.instr.CallInstr;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
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
        // 太神奇了, 这个潜在的 bug, 遍历到第 1 个时把第 1 个删去了,
        // 所有第 2 个接着成为了第 1 个, 但因此就遍历一次就结束了,
        // 第 2 个(现在是第 1 个现在根本就没操作到), 加一个 copyOf 复制引用就好了
        for (Use use : List.copyOf(useList)) {
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

    public abstract String print();

    public <T extends Value> T as(Class<T> clazz) {
        return clazz.cast(this);
    }

    public <T extends Value> boolean is(Class<T> clazz) {
        return clazz.isInstance(this);
    }
}
