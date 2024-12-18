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

package backend.mips;

public enum Instruction {
    INST_ADDU("addu"),
    INST_ADDIU("addiu"),
    INST_SUBU("subu"),
    INST_SUBIU("subiu"),
    INST_LW("lw"),
    INST_SW("sw"),
    INST_LB("lb"),
    INST_SB("sb"),

    INST_OR("or"),
    INST_ORI("ori"),

    INST_J("j"),
    INST_JR("jr"),
    INST_JAL("jal"),
    INST_JALR("jalr"),

    INST_LA("la"),
    INST_LI("li"),
    INST_LUI("lui"),
    INST_MOVE("move"),

    INST_MUL("mul"),
    INST_DIV("div"),
    INST_REM("rem"),
    INST_MFHI("mfhi"),
    INST_MFLO("mflo"),

    INST_SLT("slt"),      // 设置小于则为1
    INST_SLE("sle"),
    INST_SGT("sgt"),
    INST_SGE("sge"),
    INST_SEQ("seq"),
    INST_SNE("sne"),

    INST_AND("and"),
    INST_ANDI("andi"),

    INST_SLL("sll"),
    INST_SRA("sra"),

    INST_XOR("xor"),      // 按位异或
    INST_NOR("nor"),      // 按位非或
    INST_BEQ("beq"),      // 相等则分支
    INST_BNE("bne"),      // 不相等则分支
    INST_SYSCALL("syscall"); // 系统调用

    private final String mnemonic;

    Instruction(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    @Override
    public String toString() {
        return mnemonic;
    }

    public static Instruction fromString(String mnemonic) {
        for (Instruction instr : values()) {
            if (instr.getMnemonic().equals(mnemonic)) {
                return instr;
            }
        }
        throw new IllegalArgumentException("Unknown instruction: " + mnemonic);
    }
}
