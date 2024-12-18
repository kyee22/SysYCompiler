# COPYRIGHT

## `frontend.llvm`

>   *Copyright © 2024 中国科学技术大学《编译原理与技术》课程组*

中国科学技术大学的《编译原理与技术》课程从 LLVM IR 中裁剪出了适用于教学的精简的 IR 子集，并将其命名为 Light IR。同时依据 LLVM 的设计，为 Light IR 提供了配套简化的 C++ 库，用于生成 IR。

笔者重写了该 C++ 库的 Java 版本，作为 IR 生成的 API，应用到 2024 北京航空航天大学《编译技术》课程实验中，对应 Project 下的`frontend.llvm` package。

*   Light IR：https://cscourse.ustc.edu.cn/vdir/Gitlab/compiler_staff/2024ustc-jianmu-compiler/-/tree/main/src/lightir
*   Light IR Spec @USTC 2024: https://ustc-compiler-2024.github.io/homepage/lab2/LightIR%20C%2B%2B/
*   Light IR Spec @USTC 2023: https://ustc-compiler-principles.github.io/2023/common/LightIR/

## `midend.analysis`

>   *Copyright (C) 2022 Tian Tan <tiantan@nju.edu.cn>*
>
>   *Copyright (C) 2022 Yue Li <yueli@nju.edu.cn>*

[Tai-e](https://github.com/pascal-lab/Tai-e) 是一个分析 Java 程序的静态程序分析框架，由南京大学谭添和李樾设计，分为教学版和科研版两个版本。

笔者从 [Tai-e（教学版）](https://github.com/pascal-lab/Tai-e-assignments) 剪裁出了数据流分析中活跃变量分析相关的部分代码，应用到 2024 北京航空航天大学《编译技术》课程实验中，作为中端优化和后端优化的基础设施。
