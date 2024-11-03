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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static List<String> splitFormatString(String input) {
        // 使用正则表达式匹配%d和%c
        String regex = "(%d|%c)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        List<String> result = new ArrayList<>();
        int lastIndex = 0;

        while (matcher.find()) {
            // 添加格式符之前的文本部分
            if (matcher.start() > lastIndex) {
                result.add(input.substring(lastIndex, matcher.start()));
            }
            // 添加找到的格式符
            result.add(matcher.group());
            lastIndex = matcher.end();
        }

        // 如果最后一个匹配不是字符串的结尾，则添加剩余的部分
        if (lastIndex < input.length()) {
            result.add(input.substring(lastIndex));
        }

        return result;
    }

    public static List<Integer> resolveAsciis(String input) {
        List<Integer> asciiValues = new ArrayList<>();
        StringBuilder processedInput = new StringBuilder();
        // 去掉两侧的单引号
        input = input.trim().replaceAll("^\"|\"$", "");

        // 替换转义字符
        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);

            // 检查是否是转义字符的开始
            if (current == '\\' && i + 1 < input.length()) {
                char next = input.charAt(i + 1);
                switch (next) {
                    case 'a':
                        processedInput.append((char) 7); // 响铃
                        break;
                    case 'b':
                        processedInput.append((char) 8); // 退格
                        break;
                    case 't':
                        processedInput.append((char) 9); // 制表符
                        break;
                    case 'n':
                        processedInput.append((char) 10); // 换行符
                        break;
                    case 'v':
                        processedInput.append((char) 11); // 垂直制表符
                        break;
                    case 'f':
                        processedInput.append((char) 12); // 换页
                        break;
                    case 'r':
                        processedInput.append((char) 13); // 回车
                        break;
                    case '"':
                        processedInput.append((char) 34); // 双引号
                        break;
                    case '\'':
                        processedInput.append((char) 39); // 单引号
                        break;
                    case '\\':
                        processedInput.append((char) 92); // 反斜杠
                        break;
                    case '0':
                        processedInput.append((char) 0); // 空字符
                        break;
                    default:
                        processedInput.append(current); // 不是转义字符，保留
                        continue; // 跳过下一个字符
                }
                i++; // 跳过下一个字符
            } else {
                processedInput.append(current); // 不是转义字符，保留
            }
        }

        // 遍历处理后的字符
        for (char c : processedInput.toString().toCharArray()) {
            asciiValues.add((int) c); // 添加 ASCII 值
        }

        return asciiValues;
    }

    public static int resolveAscii(String input) {
        // 检查是否是纯数字字符串
        if (input.matches("\\d+")) {
            return Integer.parseInt(input); // 直接返回数字
        }

        // 去掉两侧的单引号
        input = input.trim().replaceAll("^'|'$", "");

        // 根据不同转义字符返回对应的ASCII值
        switch (input) {
            case "\\a":
                return 7;   // 响铃
            case "\\b":
                return 8;   // 退格
            case "\\t":
                return 9;   // 制表符
            case "\\n":
                return 10;  // 换行符
            case "\\v":
                return 11;  // 垂直制表符
            case "\\f":
                return 12;  // 换页
            case "\\r":
                return 13;  // 回车
            case "\\\"":
                return 34;  // 双引号
            case "\\'":
                return 39;  // 单引号
            case "\\\\":
                return 92;  // 反斜杠
            case "\\0":
                return 0;   // 空字符
            default:
                // 返回普通字符的ASCII值
                return (int) input.charAt(0);
        }
    }

    public static int resolveNumOfPlaceHoders(String formatStr) {
        List<String> placeHoders = List.of("%d", "%c");
        return placeHoders.stream()
                          .mapToInt(placeHoder -> countOccurrences(formatStr, placeHoder))
                          .sum();
    }

    public static int countOccurrences(String str, String placeholder) {
        Pattern pattern = Pattern.compile(placeholder);
        Matcher matcher = pattern.matcher(str);
        int count = 0;

        while (matcher.find()) {
            count++;
        }

        return count;
    }
}
