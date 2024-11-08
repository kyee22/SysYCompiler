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

import org.junit.jupiter.api.Test;

import static backend.core.ASMGenerator.*;
import static org.junit.jupiter.api.Assertions.*;

class ASMGeneratorTest {

    @Test
    void testGetHigh16Bits() {
        assertEquals(0xffff, getHigh16Bits(-1));
        assertEquals(1, getHigh16Bits(0x1ffff));
        assertEquals(2, getHigh16Bits(0x2ffff));
        assertEquals(1, getHigh16Bits(0x10000));
    }

    @Test
    void testGetLow16Bits() {
        assertEquals(8888, getLow16Bits(8888));
        assertEquals(0xffff, getLow16Bits(-1));
        assertEquals(0xffff, getLow16Bits(0x1ffff));
        assertEquals(0, getLow16Bits(0x10000));
    }

    @Test
    void testCanImmediateHold() {
        assertFalse(canImmediateHold(83413));
        assertTrue(canImmediateHold(0xffff));
        assertFalse(canImmediateHold(0x10000));
        assertFalse(canImmediateHold(-1));
        assertFalse(canImmediateHold(Integer.MAX_VALUE));
        assertFalse(canImmediateHold(Integer.MIN_VALUE));
    }

    @Test
    void testNeedsTruncation() {
        assertFalse(needsTruncation(1, 8));
        assertFalse(needsTruncation(88, 8));
        assertFalse(needsTruncation(255, 8));
        assertTrue(needsTruncation(256, 8));
        assertTrue(needsTruncation(-1, 8));
        assertTrue(needsTruncation(-8, 1));
        assertTrue(needsTruncation(2, 1));
        assertTrue(needsTruncation(3, 1));
        assertFalse(needsTruncation(0, 1));
        assertFalse(needsTruncation(1, 1));
    }
}