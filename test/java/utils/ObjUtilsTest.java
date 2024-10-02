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

package java.utilss;

import org.junit.jupiter.api.Test;

import static frontend.sysy.token.TokenType.*;
import static org.junit.jupiter.api.Assertions.*;
import static utils.ObjUtils.any;

class ObjUtilsTest {

    @Test
    public void test() {
        assertFalse(any(4, 1, 2, 3));
        assertTrue(any(3, 1, 2, 3));
        assertFalse(any("4", 1, 2, 3));
        assertFalse(any(INTTK, 1, 2, 3));
        assertTrue(any(INTTK, 1, 2, INTTK));
    }

}