# SysY Compiler 设计文档

<center>匡亦煊</center>

<center>（计算机学院 22371092）

## 参考编译器介绍





![]![](./img/refer-logo.drawio.svg))



## 编译器总体设计



## 词法分析设计





*   左递归？
    *   解析时按右递归解析
    *   但评测时需要输出左递归的AST结构怎么办？
        *   ==> 这些节点在addChildren时逆序add
*   错误处理？
    *   ==> match 时分为 SoftMatch 和 HardMatch
    *   HardMatch不匹配则回退
    *   SoftMatch不匹配则继续匹配但记录下错误信息
        *   在尝试解析某语法单元时已经记录了一些SoftMatch的错误记录，但某一时刻失配回退时错误记录（就冗余在那里了）是误报的怎么办？
        *   ==> 回退时不但维护token流的回退，还维护错误记录的回退



*   补充 getintstmt,getcharstmt
*   各stmt作为stmt的属性



## 语法分析设计



>   **Definition (正则表达式)**
>
>   给定字母表 $\Sigma$,  $\Sigma$ 上的正则表达式由且仅由以下规则定义:
>
>   (1) $\epsilon$ 是正则表达式;
>
>   (2) $\forall a \in \Sigma$, $a$是正则表达式;
>
>   (3) 如果 $r$ 是正则表达式, 则 $(r)$ 是正则表达式;
>
>   (4) 如果 $r$ 与 s 是正则表达式, 则 $r|s$, $rs$, $r∗$ 也是正则表达式。



*   `gen` 定义了规则 (2) ：

    ```java
    private static Generator gen(TokenType type, ErrorType errorType) {...}
    private static Generator gen(TokenType type) {...}
    
    ```

*   `cat`、`or`、`optional` 和 `any` 依次定义了规则 (3) 和 (4) 的 `拼接`、`|`、`()` 和 `*`  操作：

    ```java
    private static Generator cat(Generator... generators) {...}
    public static Generator or(Generator... generators) {...}
    public static Generator option(Generator gen) {...}
    public static Generator any(Generator gen) {...}
    ```




*   当容许错误的时候，是否会混淆其他语法单元
    *   `int func() {return 0;}` 因为容许缺失 `;` 会将 `int func` 混淆成 $VarDecl$；
    *   `a = getint();` 因为容许缺失将会 `a` 直接匹配为 $ExpStmt$；
*   不容许错误的时候，也要尽可能长的匹配
    *   `int lval = call();` 应该将 `call` 匹配为 $UnaryExp$ 的调用分支，而不是直接将 `call`  $UnaryExp$ 的 $PrimaryExp$ 分支。



*   `int a ...` 
*   `a ...`
*   `a( ...`
*   a`[ ...`