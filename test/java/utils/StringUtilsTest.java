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

import frontend.llvm.Module;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static utils.StringUtils.resolveNumOfPlaceHoders;
import static utils.StringUtils.sha1;

class StringUtilsTest {
    @Test
    public void testSplitFormatString() {
        // 基本测试用例
        assertEquals(List.of("123", "%d", "35", "%d", "%c"),
                StringUtils.splitFormatString("123%d35%d%c"));

        // 开头是格式符
        assertEquals(List.of("%d", "35", "%d", "%c"),
                StringUtils.splitFormatString("%d35%d%c"));

        // 结尾是格式符
        assertEquals(List.of("123", "%d", "35", "%d", "%c", "%d"),
                StringUtils.splitFormatString("123%d35%d%c%d"));

        // 只有格式符
        assertEquals(List.of("%d", "%c", "%d"),
                StringUtils.splitFormatString("%d%c%d"));

        // 没有格式符
        assertEquals(List.of("123456"),
                StringUtils.splitFormatString("123456"));

        // 格式符之间没有文本
        assertEquals(List.of("%d", "%c", "%d"),
                StringUtils.splitFormatString("%d%c%d"));

        // 多个连续的格式符
        assertEquals(List.of("abc", "%d", "%d", "%c", "def", "%d", "%c"),
                StringUtils.splitFormatString("abc%d%d%cdef%d%c"));

        // 格式符嵌套在文本中
        assertEquals(List.of("abc", "%d", "def", "%c", "ghid"),
                StringUtils.splitFormatString("abc%ddef%cghid"));

        // 空字符串
        assertEquals(List.of(),
                StringUtils.splitFormatString(""));

        // 单个字符
        assertEquals(List.of("a"),
                StringUtils.splitFormatString("a"));

        // 单个格式符
        assertEquals(List.of("%d"),
                StringUtils.splitFormatString("%d"));

        // 格式符前后有空格
        assertEquals(List.of(" ", "%d", " ", "%c", " "),
                StringUtils.splitFormatString(" %d %c "));

        // 格式符中间有空格
        assertEquals(List.of("abc", "%d", " ", "%c", "def"),
                StringUtils.splitFormatString("abc%d %cdef"));

        // 非格式符的百分号
        assertEquals(List.of("123", "%d", "35", "%d",  "%c"),
                StringUtils.splitFormatString("123%d35%d%c"));

        assertEquals(List.of("\\n", "%d", "\\n", "%d", "\\n", "%c", "\\n"),
                StringUtils.splitFormatString("\\n%d\\n%d\\n%c\\n"));
    }

    @Test
    public void testResolveAsciis() {
        // 基本测试用例
        assertEquals(List.of((int)'a', (int)'b', (int)'c'), StringUtils.resolveAsciis("\"abc\""));
        assertEquals(List.of((int)'1', (int)'a', 10), StringUtils.resolveAsciis("\"1a\\n\""));

        // 含转义字符的测试用例
        assertEquals(List.of((int)'H', (int)'e', (int)'l', (int)'l', (int)'o', 10), StringUtils.resolveAsciis("\"Hello\\n\""));
        assertEquals(List.of((int)'A', (int)'B', 9, (int)'C'), StringUtils.resolveAsciis("\"AB\\tC\""));
        assertEquals(List.of(7, (int)'B', 32, (int)'C'), StringUtils.resolveAsciis("\"\\aB C\""));
        assertEquals(List.of(8, 32, (int)'a'), StringUtils.resolveAsciis("\"\\b a\""));

        // 多个转义字符
        assertEquals(List.of((int)'t', (int)'e', (int)'s', (int)'t', 10), StringUtils.resolveAsciis("\"test\\n\""));
        assertEquals(List.of((int)'C', (int)'o', (int)'d', (int)'e', 34), StringUtils.resolveAsciis("\"Code\\\"\""));
        assertEquals(List.of((int)'s', (int)'t', (int)'r', (int)'i', (int)'n', (int)'g', 39), StringUtils.resolveAsciis("\"string'\""));

        // 空字符和回车符
        assertEquals(List.of(0), StringUtils.resolveAsciis("\"\\0\""));
        assertEquals(List.of((int)'L', (int)'i', (int)'n', (int)'e', 13), StringUtils.resolveAsciis("\"Line\\r\""));

        // 混合输入
        assertEquals(List.of((int)'t', (int)'e', (int)'s', (int)'t', (int)'1', (int)'2', (int)'3', 10), StringUtils.resolveAsciis("\"test123\\n\""));

        // 处理反斜杠
        assertEquals(List.of((int)'\\', (int)'N'), StringUtils.resolveAsciis("\"\\N\""));
        assertEquals(List.of(9), StringUtils.resolveAsciis("\"\\t\""));

        assertEquals(List.of(34, 34), StringUtils.resolveAsciis("\"\\\"\\\"\""));

    }

    @Test
    public void testResolveAscii() {
        assertEquals(0, StringUtils.resolveAscii("0"));
        assertEquals(1, StringUtils.resolveAscii("1"));
        assertEquals(2, StringUtils.resolveAscii("2"));
        assertEquals(123, StringUtils.resolveAscii("123"));
        assertEquals(124, StringUtils.resolveAscii("00124"));
        assertEquals(97, StringUtils.resolveAscii("'a'"));

        // 测试32到126的所有可见字符
        for (int i = 32; i <= 126; i++) {
            char c = (char) i;
            assertEquals(i, StringUtils.resolveAscii("'" + c + "'"), "Testing character: " + c + " " + i);
        }

        // 测试转义字符
        assertEquals(7, StringUtils.resolveAscii("'\\a'")); // 响铃
        assertEquals(8, StringUtils.resolveAscii("'\\b'")); // 退格
        assertEquals(9, StringUtils.resolveAscii("'\\t'")); // 制表符
        assertEquals(10, StringUtils.resolveAscii("'\\n'")); // 换行符
        assertEquals(11, StringUtils.resolveAscii("'\\v'")); // 垂直制表符
        assertEquals(12, StringUtils.resolveAscii("'\\f'")); // 换页
        assertEquals(13, StringUtils.resolveAscii("'\\r'")); // 回车
        assertEquals(34, StringUtils.resolveAscii("'\\\"'")); // 双引号
        assertEquals(39, StringUtils.resolveAscii("'\\''")); // 单引号
        assertEquals(92, StringUtils.resolveAscii("'\\\\'")); // 反斜杠
        assertEquals(0, StringUtils.resolveAscii("'\\0'")); // 空字符
    }

    @Test
    public void testResolveNumOfPlaceHoders() {
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
        assertEquals(0, resolveNumOfPlaceHoders("%%"));
        assertEquals(0, resolveNumOfPlaceHoders("%f%lf%u%lld%llu"));
    }

    @Test
    public void testSha1() {
        Module module1 = new Module();
        Module module2 = new Module();
        assertEquals(sha1(1, 2, 3), sha1(1, 2, 3));
        assertNotEquals(sha1(1, 2, 3, 4, 5), sha1(1, 2, 3, 4, 4));
        assertEquals(sha1(module1.getInt32Type().hashCode()), sha1(module1.getInt32Type().hashCode()));
        assertEquals(sha1(module2.getInt32Type().hashCode()), sha1(module2.getInt32Type().hashCode()));
        assertEquals(sha1(module1.getInt32Type(), module2.getInt32Type()),
                     sha1(module1.getInt32Type(), module2.getInt32Type()));
        assertNotEquals(sha1(module1.getInt32Type(), module2.getInt32Type()),
                        sha1(module2.getInt32Type(), module1.getInt32Type()));
        assertNotEquals(sha1(module1.getInt32Type().hashCode()), sha1(module2.getInt32Type().hashCode()));
        assertNotEquals(sha1(module1), sha1(module2));
        assertNotEquals(sha1(65), sha1('A'));
    }
}