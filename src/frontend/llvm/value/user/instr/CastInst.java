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

package frontend.llvm.value.user.instr;

import frontend.llvm.IRPrinter;
import frontend.llvm.type.IntegerType;
import frontend.llvm.type.Type;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Value;

public class CastInst extends Instruction {
    private CastInst(OpID opID, Value val, Type destType, BasicBlock bb) {
        super(destType, opID, bb);
        switch (opID) {
            case SEXT:
            case ZEXT:
                if (!val.getType().isIntegerType()) {
                    throw new IllegalArgumentException("ExtInst requires integer type");
                }
                if (!destType.isIntegerType()) {
                    throw new IllegalArgumentException("ExtInst requires integer type");
                }
                if (((IntegerType) val.getType()).getNumBits() >= ((IntegerType) destType).getNumBits()) {
                    throw new IllegalArgumentException("operand bit size is not smaller than destination type bit size");
                }
                break;
            case TRUNCT:
                if (!val.getType().isIntegerType()) {
                    throw new IllegalArgumentException("ExtInst requires integer type");
                }
                if (!destType.isIntegerType()) {
                    throw new IllegalArgumentException("ExtInst requires integer type");
                }
                if (((IntegerType) val.getType()).getNumBits() <= ((IntegerType) destType).getNumBits()) {
                    throw new IllegalArgumentException("operand bit size is not bigger than destination type bit size");
                }
                break;
            case FPTOSI:
                //todo
                break;
            case SITOFP:
                //todo
                break;
            default:
                throw new IllegalArgumentException("Unsupported opID: " + opID);
        }
        addOperand(val);
    }

    public static CastInst createZext(Value val, Type destType, BasicBlock bb) {return new CastInst(OpID.ZEXT, val, destType, bb);}
    public static CastInst createZextToInt8(Value val, BasicBlock bb) {return new CastInst(OpID.ZEXT, val, bb.getModule().getInt8Type(), bb);}
    public static CastInst createZextToInt32(Value val, BasicBlock bb) {return new CastInst(OpID.ZEXT, val, bb.getModule().getInt32Type(), bb);}
    public static CastInst createSext(Value val, Type destType, BasicBlock bb) {return new CastInst(OpID.SEXT, val, destType, bb);}
    public static CastInst createSextToInt32(Value val, BasicBlock bb) {return new CastInst(OpID.SEXT, val, bb.getModule().getInt32Type(), bb);}
    public static CastInst createTrunc(Value val, Type destType, BasicBlock bb) {return new CastInst(OpID.TRUNCT, val, destType, bb);}
    public static CastInst createTruncToInt8(Value val, BasicBlock bb) {return new CastInst(OpID.TRUNCT, val, bb.getModule().getInt8Type(), bb);}
    public static CastInst createTruncToInt1(Value val, BasicBlock bb) {return new CastInst(OpID.TRUNCT, val, bb.getModule().getInt1Type(), bb);}
    public static CastInst createFptosi(Value val, Type destType, BasicBlock bb) {return new CastInst(OpID.FPTOSI, val, destType, bb);}
    public static CastInst createFptosiToInt32(Value val, BasicBlock bb) {return new CastInst(OpID.FPTOSI, val, bb.getModule().getInt32Type(), bb);}
    public static CastInst createSitofp(Value val, Type destType, BasicBlock bb) {return new CastInst(OpID.SITOFP, val, destType, bb);}

    public Type getDestType() {return getType();}

    @Override
    public String print() {
        StringBuilder instrIr = new StringBuilder();
        instrIr.append("%").append(this.getName())
                .append(" = ").append(getInstrOpName())
                .append(" ").append(getOperand(0).getType().print())
                .append(" ").append(IRPrinter.printAsOp(getOperand(0), false))
                .append(" to ").append(getDestType().print());
        return instrIr.toString();
    }

    @Override
    public <T> T accept(InstVisitor<T> visitor) {return visitor.visit(this);}
}
