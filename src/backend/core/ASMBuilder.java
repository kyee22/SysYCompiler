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

package backend.core;

import backend.mips.Register;
import frontend.llvm.value.user.GlobalVariable;

import java.security.PublicKey;

import static backend.core.ASMPrinter.*;
import static backend.mips.Instruction.*;

public class ASMBuilder {
    public static final String SEG_DATA = "data";
    public static final String SEG_TEXT = "text";
    private StringBuilder buf = new StringBuilder();

    public void init() {
        buf.append("# Copyright (C) 2024 Yixuan Kuang <kyee22@buaa.edu.cn>\n");
    }

    public void setSegment(String segName) {
        buf.append("\n.")
                .append(segName)
                .append("\n");
    }

    public void buildGloabalVar(GlobalVariable globalVariable) {
        buf.append("\t")
                .append(print(globalVariable))
                .append("\n");
    }

    public void buildLabel(String labelName) {
        buf.append("\n")
                .append(labelName)
                .append(":\n");
    }

    public void buildBreak() {
        buf.append("\n");
    }

    public void buildComment(String comment) {
        buf.append("\t# ").append(comment).append("\n");
    }


    public void buildBne(Register src1, Register src2, String labelName) {buf.append(printOp3(INST_BNE, src1, src2, labelName));}
    public void buildSyscall() {buf.append(printOp0(INST_SYSCALL));}
    public void buildSll(Register dest, Register src, int shiftBits) {buf.append(printOp3(INST_SLL, dest, src, shiftBits));}
    public void buildAndi(Register dest, Register src, int imm) {buf.append(printOp3(INST_ANDI, dest, src, imm));}
    public void buildSlt(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_SLT, dest, src1, src2));}
    public void buildSle(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_SLE, dest, src1, src2));}
    public void buildSgt(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_SGT, dest, src1, src2));}
    public void buildSge(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_SGE, dest, src1, src2));}
    public void buildSeq(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_SEQ, dest, src1, src2));}
    public void buildSne(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_SNE, dest, src1, src2));}
    public void buildMflo(Register dest) {buf.append(printOp1(INST_MFLO, dest));}
    public void buildMfhi(Register dest) {buf.append(printOp1(INST_MFHI, dest));}
    public void buildRem(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_REM, dest, src1, src2));}
    public void buildDiv(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_DIV, dest, src1, src2));}
    public void buildMul(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_MUL, dest, src1, src2));}
    public void buildOri(Register dest, Register src, int imm) {buf.append(printOp3(INST_ORI, dest, src, imm));}
    public void buildLi(Register dest, int imm) {buf.append(printOp2(INST_LI, dest, imm));}
    public void buildLui(Register dest, int imm) {buf.append(printOp2(INST_LUI, dest, imm));}
    public void buildJal(String label) {buf.append(printOp1(INST_JAL, label));}
    public void buildJ(String label) {buf.append(printOp1(INST_J, label));}
    public void buildJr(Register dest) {buf.append(printOp1(INST_JR, dest));}
    public void buildLa(Register dest, String addr) {buf.append(printOp2(INST_LA, dest, addr));}
    public void buildAddu(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_ADDU, dest, src1, src2));}
    public void buildAddiu(Register dest, Register src, int imm) {buf.append(printOp3(INST_ADDIU, dest, src, imm));}
    public void buildSubu(Register dest, Register src1, Register src2) {buf.append(printOp3(INST_SUBU, dest, src1, src2));}
    public void buildSubiu(Register dest, Register src, int imm) {buf.append(printOp3(INST_SUBIU, dest, src, imm));}
    public void buildMove(Register dest, Register src) {buf.append(printOp2(INST_MOVE, dest, src));}
    public void buildSw(Register src, Register base, int offset) {buf.append(printStore(INST_SW, src, base, offset));}
    public void buildLw(Register dest, Register base, int offset) {buf.append(printLoad(INST_LW, dest, base, offset));}
    public void buildSb(Register src, Register base, int offset) {buf.append(printStore(INST_SB, src, base, offset));}
    public void buildLb(Register dest, Register base, int offset) {buf.append(printLoad(INST_LB, dest, base, offset));}

    public String dump() {
        return buf.toString();
    }
}
