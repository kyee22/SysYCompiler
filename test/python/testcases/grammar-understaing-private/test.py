import os
import subprocess
import argparse
import sys

# 拼接的C代码内容
c_code_header = '''
#include<stdio.h>
int getchar() {
    char c;
    scanf("%c", &c);
    return (int)c;
}

int getint() {
    int t;
    scanf("%d", &t);
    while (getchar() != '\\n');
    return t;
}
'''



def create_and_run_c_file(txt_file_path):
    # 读取txt文件内容
    with open(txt_file_path, 'r') as txt_file:
        txt_content = txt_file.read()

    # 拼接所有的C代码
    c_code = c_code_header + txt_content

    # 定义C文件的名字
    c_file_path = 'generated_program.c'

    # 创建C文件，并写入拼接的内容
    with open(c_file_path, 'w') as c_file:
        c_file.write(c_code)

    # 编译C文件
    compile_command = ['gcc', c_file_path, '-o', 'generated_program']
    compile_result = subprocess.run(compile_command, capture_output=True, text=True)

    if compile_result.returncode == 0:
        print("Compilation successful!")

        # 动态生成输入文件名
        input_file_path = txt_file_path.replace('testfile', 'input').replace('.txt', '.txt')
        output_file_path = txt_file_path.replace('testfile', 'output').replace('.txt', '.txt')

        # 检查输入文件是否存在
        if not os.path.exists(input_file_path):
            print(f"Input file '{input_file_path}' not found!")
            return
        if not os.path.exists(output_file_path):
            print(f"Output file '{output_file_path}' not found!")
            return


        # 打开输入文件，将其作为标准输入重定向到可执行文件
        with open(input_file_path, 'r') as input_file, open(output_file_path, 'w') as output_file:
            run_command = ['./generated_program']
            subprocess.run(run_command, stdin=input_file, stdout=output_file)
    else:
        print("Compilation failed!")
        print(compile_result.stderr)

    # 删除生成的C文件和可执行文件
    # os.remove(c_file_path)
    # os.remove('generated_program')

if __name__ == "__main__":
    # 检查是否提供了足够的命令行参数
    if len(sys.argv) != 2:
        print("Usage: python test.py <path_to_txt_file>")
        sys.exit(1)

    # 从命令行获取.txt文件的路径
    txt_file_path = sys.argv[1]

    # 调用函数并传入获取到的路径
    create_and_run_c_file(txt_file_path)