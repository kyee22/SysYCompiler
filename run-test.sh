#!/bin/bash

# 切换到lab/Compiler目录
cd lab/Compiler

# 设置类路径，包括src、lib目录中的JAR文件
LIB_DIR="lib"
CLASSPATH=".:src:lib/*"

# 使用find命令来查找所有Java文件并编译它们
find . -name "*.java" | xargs -n 1 javac -encoding UTF-8 -cp "$CLASSPATH" -d .

# 运行JUnit测试
# 对于JUnit 5，使用以下命令
java -cp "$CLASSPATH" org.junit.platform.console.ConsoleLauncher --scan-class-path --select-class test.java.CompilerTest

# 如果使用JUnit 4，可以使用以下命令
# java -cp "$CLASSPATH" org.junit.runner.TextUI test.java.CompilerTest
