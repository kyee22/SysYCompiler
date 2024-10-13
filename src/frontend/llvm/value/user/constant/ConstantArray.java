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

import frontend.llvm.type.ArrayType;

import java.util.ArrayList;
import java.util.List;

public class ConstantArray extends Constant {
    private final List<Constant> constArray;

    private ConstantArray(ArrayType type, List<Constant> values) {
        super(type, "");
        this.constArray = new ArrayList<>(values);
        for (int i = 0; i < values.size(); i++) {
            addOperand(values.get(i));
        }
    }

    public Constant getElementValue(int index) {
        return constArray.get(index);
    }

    public int getSizeOfArray() {
        return constArray.size();
    }

    @Override
    public String print() {
        StringBuilder sb = new StringBuilder();
        //sb.append(getType().print());
        sb.append("[");
        for (int i = 0; i < constArray.size(); i++) {
            Constant element = constArray.get(i);
            if (!(element instanceof ConstantArray)) {
                sb.append(element.getType().print());
            }
            sb.append(" ");
            sb.append(element.print());
            if (i < constArray.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static ConstantArray get(ArrayType type, List<Constant> values) {
        return new ConstantArray(type, values);
    }
}
