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

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

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
