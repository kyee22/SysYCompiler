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


import frontend.llvm.Module;

public class Type {
    public enum TypeID {
        VoidTyID,       LabelTyID,      IntegerTyID,    FunctionTyID,
        ArrayTyID,      PointerTyID,    FloatTyID
    };

    protected TypeID tid;
    protected frontend.llvm.Module module;

    public Type(TypeID tid, frontend.llvm.Module module) {
        this.tid = tid;
        this.module = module;
    }

    public TypeID getTypeId() { return tid; }

    public boolean isVoidType() { return tid == TypeID.VoidTyID; }
    public boolean isLabelType() { return tid == TypeID.LabelTyID; }
    public boolean isIntegerType() { return tid == TypeID.IntegerTyID; }
    public boolean isFunctionType() { return tid == TypeID.FunctionTyID; }
    public boolean isArrayType() { return tid == TypeID.ArrayTyID; }
    public boolean isPointerType() { return tid == TypeID.PointerTyID; }
    public boolean isFloatType() { return tid == TypeID.FloatTyID; }
    public boolean isInt32Type() {return isIntegerType() && ((IntegerType) this).getNumBits() == 32;}
    public boolean isInt8Type() {return isIntegerType() && ((IntegerType) this).getNumBits() == 8;}
    public boolean isInt1Type() {return isIntegerType() && ((IntegerType) this).getNumBits() == 1;}

    public Type getPointerElementType() {
        if (isPointerType()) {
            return ((PointerType) this).getElementType();
        }
        throw new RuntimeException("called on non-pointer type");
    }

    public Type getArrayElementType() {
        if (isArrayType()) {
            return ((ArrayType) this).getElementType();
        }
        throw new RuntimeException("called on non-array type");
    }

    public Module getModule() {
        return module;
    }

    public int getSize() {
        switch (getTypeId()) {
            case IntegerTyID:
                if (isInt1Type() || isInt8Type()) {
                    return 1;
                } else if (isInt32Type()) {
                    return 4;
                } else {
                    throw new RuntimeException("unexpected int type bits");
                }
            case ArrayTyID: {
                ArrayType arrayType = (ArrayType) this;
                int elementSize = arrayType.getElementType().getSize();
                int numElements = arrayType.getNumberOfElements();
                return elementSize * numElements;
            }
            case PointerTyID: {
                return 8;
            }
            case FloatTyID: {
                return 4;
            }
            case FunctionTyID:
            case VoidTyID:
            case LabelTyID: {
                throw new RuntimeException("bad use on getSize");
            }
            default:
                throw new RuntimeException("unreachable");
        }
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        switch (getTypeId()) {
            case VoidTyID:
                sb.append("void");
                break;
            case LabelTyID:
                sb.append("label");
                break;
            case IntegerTyID:
                sb.append("i");
                sb.append(((IntegerType) this).getNumBits());
                break;
            case FunctionTyID:
                sb.append(((FunctionType) this).getReturnType().print());
                sb.append(" (");
                for (int i = 0; i < ((FunctionType)this).getNumberOfArgs(); ++i) {
                    if (i > 0) {
                        sb.append(", ");
                    }
                    sb.append(((FunctionType) this).getParamType(i).print());
                }
                sb.append(")");
                break;
            case PointerTyID:
                sb.append(getPointerElementType().print());
                sb.append("*");
                break;
            case ArrayTyID:
                sb.append("[");
                sb.append(((ArrayType) this).getNumberOfElements());
                sb.append(" x ");
                sb.append(((ArrayType) this).getElementType().print());
                sb.append("]");
                break;
            case FloatTyID:
                sb.append("float");
                break;
            default:
                break;
        }
        return sb.toString();
    }
}
