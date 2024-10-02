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

package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static utils.StringUtils.resolveNumOfFormat;

class StringUtilsTest {
    @Test
    public void testResolveNumOfFormat() {
        assertEquals(4, resolveNumOfFormat("%d%c%d%c"));
        assertEquals(1, resolveNumOfFormat("%d\n"));
        assertEquals(1, resolveNumOfFormat("%d "));
        assertEquals(7, resolveNumOfFormat("%c (%d,%d) (%d,%d) (%d,%d)\n"));
        assertEquals(2, resolveNumOfFormat("%dddddd%d"));
        assertEquals(1, resolveNumOfFormat("%dddddd%"));
        assertEquals(1, resolveNumOfFormat("%%ddddd%%"));
        assertEquals(0, resolveNumOfFormat("%%xdddd%%"));
        assertEquals(2, resolveNumOfFormat("%d\n%d\n"));
        assertEquals(6, resolveNumOfFormat("%d%d%d%d%d%d"));
        assertEquals(3, resolveNumOfFormat("%d %d %d\n"));
        assertEquals(4, resolveNumOfFormat("%dc%cd%dcc%cdd"));
        assertEquals(0, resolveNumOfFormat(" c cc ccc%"));
        assertEquals(0, resolveNumOfFormat(" d dd ddd%"));
        assertEquals(0, resolveNumOfFormat("% cd d% ddd% cddd%%%"));
        assertEquals(4, resolveNumOfFormat("%d %c (%d) %d"));
        assertEquals(5, resolveNumOfFormat("%d%c%c%d%d\n"));
        assertEquals(4, resolveNumOfFormat("%d %c %d %% %c"));
        assertEquals(4, resolveNumOfFormat("%%d%%d%%d%%d"));
        assertEquals(0, resolveNumOfFormat("This is a test string with no formats."));
        assertEquals(10, resolveNumOfFormat("Input: %d, %c, %d, %c, %d, %d, %d, %d, %c, %d\n"));
        assertEquals(1, resolveNumOfFormat("%d %x %y"));
        assertEquals(5, resolveNumOfFormat("Start %d %c middle %d end %d %c."));
    }
}