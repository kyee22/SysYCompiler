import os
import shutil
import subprocess
import time

GREEN = '\033[92m'
RED = '\033[91m'
RESET = '\033[0m'

# 项目根目录和测试用例目录
project_root = "."
testcases_root = os.path.join(project_root, "test", "python", "testcases", "lexer-error-private")

# 遍历所有的测试用例
def run_tests():
    total_tests = 0
    passed_tests = 0
    start_total_time = time.time()

    for testcase in sorted(os.listdir(testcases_root), key=lambda x: int(''.join(filter(str.isdigit, x)))):
        testcase_path = os.path.join(testcases_root, testcase)
        testfile_path = os.path.join(testcase_path, "testfile.txt")
        ans_path = os.path.join(testcase_path, "ans.txt")

        # 1. 覆盖根目录的 testfile.txt
        shutil.copy(testfile_path, os.path.join(project_root, "testfile.txt"))

        # 2. 运行 Java 编译器并测量运行时间
        total_tests += 1
        start_time = time.time()
        result = run_java_compiler()
        end_time = time.time()
        runtime = end_time - start_time

        if not result:
            print(f"{RED}Test {testcase} failed to run.{RESET}")
            continue

        # 3. 读取 lexer.txt 和 ans.txt
        with open(os.path.join(project_root, "error.txt"), 'r') as error_output:
            with open(ans_path, 'r') as expected_output:
                error_lines = error_output.readlines()
                expected_lines = expected_output.readlines()

                # 4. 比较两个文件的内容
                if error_lines == expected_lines:
                    passed_tests += 1
                    print(f"{GREEN}[       OK ] TEST {testcase} ({runtime:.2f} s){RESET}")
                else:
                    print(f"{RED}[   FAILED ] TEST {testcase} ({runtime:.2f} s){RESET}")
                    print(f"Differences found in {testcase}:\n")
                    for i, (error_line, expected_line) in enumerate(zip(error_lines, expected_lines)):
                        if error_line != expected_line:
                            print(f"💡 We expected \"{expected_line.strip()}\" but we got \"{error_line.strip()}\" at line {i + 1}")

    # 总运行时间和测试总结
    end_total_time = time.time()
    total_runtime = (end_total_time - start_total_time)
    print("\n[==========] {} tests from 1 test suite ran. ({:.2f} s total)".format(total_tests, total_runtime))
    print(f"{GREEN}[  PASSED  ] {passed_tests} tests.{RESET}")
    if passed_tests != total_tests:
        print(f"{RED}[  FAILED  ] {total_tests - passed_tests} tests.{RESET}")
        print(f"{RED}\n❌  Oops!{RESET}")
    else:
        print(f"{GREEN}\n✅  Congratulations!{RESET}")

def run_java_compiler():
    # 运行 Java 编译器
    cp = os.path.join('out', 'production', 'Compiler')
    compile_cmd = ["java", "-cp", cp, "Compiler"]
    try:
        subprocess.run(compile_cmd, check=True)
        return True
    except subprocess.CalledProcessError as e:
        print(f"Error running Compiler.java: {e}")
        return False

if __name__ == "__main__":
    run_tests()
