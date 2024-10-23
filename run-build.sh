#!/bin/bash

# 切换到lab/Compiler目录
cd lab/Compiler

# 设置类路径，包括src、lib目录中的JAR文件
LIB_DIR="lib"
CLASSPATH=".:src:$(ls lib/*.jar | tr '\n' ':')"

# 输出类路径以验证
echo "Classpath: $CLASSPATH"

# 使用find命令来查找所有Java文件并编译它们
find . -name "*.java" | xargs -n 1 javac -encoding UTF-8 -cp "$CLASSPATH" -d .