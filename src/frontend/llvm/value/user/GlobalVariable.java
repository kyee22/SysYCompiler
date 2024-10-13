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

package frontend.llvm.value.user;

import frontend.llvm.IRPrinter;
import frontend.llvm.type.PointerType;
import frontend.llvm.type.Type;
import frontend.llvm.value.user.constant.Constant;
import frontend.llvm.Module;

public class GlobalVariable extends User {
    private boolean isConst;
    private Constant initVal;

    // Private constructor to prevent direct instantiation
    private GlobalVariable(String name, Module module, Type type, boolean isConst, Constant init) {
        super(type, name);
        this.isConst = isConst;
        this.initVal = init;
        module.addGlobalVariable(this);
        if (init != null) {
            super.addOperand(init);
        }
    }

    // Static factory method for creating instances
    public static GlobalVariable create(String name, Module module, Type type, boolean isConst, Constant init) {
        return new GlobalVariable(name, module, PointerType.get(type), isConst, init);
    }

    public Constant getInit() {
        return initVal;
    }

    public boolean isConst() {
        return isConst;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append(IRPrinter.printAsOp(this, false));
        sb.append(" = ");
        sb.append(isConst() ? "constant " : "global ");
        sb.append(getType().getPointerElementType().print());
        sb.append(" ");
        sb.append(getInit() != null ? getInit().print() : "");
        return sb.toString();
    }
}
