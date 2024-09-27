

*   关于读函数：
    *   `int a = getint();`  不合法
    *   `int a; a = getint();` 合法
    *   `getint()` 一次读入一行；需要有换行符。
  
* 关于逻辑运算符 `!`：
    *   `int cond = validate(a, b, c); if (!cond) { ... }` 合法；
    *   `if (!validate(a, b, c)) { ... }` 也合法；
 
* 关于条件表达式：
   *   `if ((a * a + b * b == c * c) || (a * a + c * c == b * b) || (b * b + c * c == a * a)` 不合法
   *   `if (a * a + b * b == c * c || a * a + c * c == b * b || b * b + c * c == a * a)` 合法
   *   `return a == 1 && b == 2;` 不合法

*   关于返回值：
    *   `return a == 1 && b == 1;`不合法
    *   `if (a == 1 & b == 1) return 1; ...` 合法
*   关于循环：
    *   `while (n) {...}` 不合法
    *   `for (; n; ) {...}` 不合法