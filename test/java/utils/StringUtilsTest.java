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
import static utils.StringUtils.resolveNumOfPlaceHoders;

class StringUtilsTest {
    @Test
    public void testresolveNumOfPlaceHoders() {
        assertEquals(4, resolveNumOfPlaceHoders("%d%c%d%c"));
        assertEquals(1, resolveNumOfPlaceHoders("%d\n"));
        assertEquals(1, resolveNumOfPlaceHoders("%d "));
        assertEquals(7, resolveNumOfPlaceHoders("%c (%d,%d) (%d,%d) (%d,%d)\n"));
        assertEquals(2, resolveNumOfPlaceHoders("%dddddd%d"));
        assertEquals(1, resolveNumOfPlaceHoders("%dddddd%"));
        assertEquals(1, resolveNumOfPlaceHoders("%%ddddd%%"));
        assertEquals(0, resolveNumOfPlaceHoders("%%xdddd%%"));
        assertEquals(2, resolveNumOfPlaceHoders("%d\n%d\n"));
        assertEquals(6, resolveNumOfPlaceHoders("%d%d%d%d%d%d"));
        assertEquals(3, resolveNumOfPlaceHoders("%d %d %d\n"));
        assertEquals(4, resolveNumOfPlaceHoders("%dc%cd%dcc%cdd"));
        assertEquals(0, resolveNumOfPlaceHoders(" c cc ccc%"));
        assertEquals(0, resolveNumOfPlaceHoders(" d dd ddd%"));
        assertEquals(0, resolveNumOfPlaceHoders("% cd d% ddd% cddd%%%"));
        assertEquals(4, resolveNumOfPlaceHoders("%d %c (%d) %d"));
        assertEquals(5, resolveNumOfPlaceHoders("%d%c%c%d%d\n"));
        assertEquals(4, resolveNumOfPlaceHoders("%d %c %d %% %c"));
        assertEquals(4, resolveNumOfPlaceHoders("%%d%%d%%d%%d"));
        assertEquals(0, resolveNumOfPlaceHoders("This is a test string with no formats."));
        assertEquals(10, resolveNumOfPlaceHoders("Input: %d, %c, %d, %c, %d, %d, %d, %d, %c, %d\n"));
        assertEquals(1, resolveNumOfPlaceHoders("%d %x %y"));
        assertEquals(5, resolveNumOfPlaceHoders("Start %d %c middle %d end %d %c."));
    }
}