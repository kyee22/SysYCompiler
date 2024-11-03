import os
from pathlib import Path
from subprocess import run, PIPE, STDOUT
from shutil import copy2
import argparse

def compare(output_file: Path, expected_file: Path):
    output_lines = output_file.read_text().splitlines()
    expected_lines = expected_file.read_text().splitlines()

    cnt = 0

    if len(output_lines) != len(expected_lines):
        print(f"Error {cnt}: expected {len(expected_lines)} lines but got {len(output_lines)} lines")
        cnt += 1

    for i in range(min(len(output_lines), len(expected_lines))):
        output_line = output_lines[i] if i < len(output_lines) else None
        expected_line = expected_lines[i] if i < len(expected_lines) else None
        if output_line != expected_line:
            print(f"Error {cnt}: expected \"{expected_line}\" but got \"{output_line}\" at line {i + 1}")
            cnt += 1

    return cnt == 0

def run_batch_tests(batch_directory: Path, output_path: Path):
    log_file_path = Path("judge.log")

    # 清空 judge.log 文件
    with open(log_file_path, 'w') as f:
        f.write('')
    testcases = sorted([d for d in batch_directory.iterdir() if d.is_dir() and d != batch_directory])

    print_colored_text(f"Run test cases in {batch_directory} ...", ANSI_PURPLE)
    num_pass = 0
    num_fail = 0
    for testcase in testcases:
        if run_test_case(testcase, output_path, log_file_path):
            print_colored_text(f"[       OK ] TEST {testcase.name}", ANSI_GREEN)
            num_pass += 1
        else:
            print_colored_text(f"[   FAILED ] TEST {testcase.name}", ANSI_RED)
            num_fail += 1
    print_colored_text("", ANSI_PURPLE)
    print_colored_text(f"[  PASSED  ] {num_pass} tests.", ANSI_GREEN)
    if num_fail != 0:
        print_colored_text(f"[   FAILED ] {num_fail} tests.", ANSI_RED)
    return num_fail == 0

def run_test_case(testcase_directory: Path, output_path: Path, log_file_path: Path):
    testfile_path = testcase_directory / "testfile.txt"
    input_path = testcase_directory / "in.txt"
    ans_path = testcase_directory / "ans.txt"

    # Copy testfile to project root
    root_dir = '.'
    copy2(testfile_path, os.path.join(root_dir, "testfile.txt"))
    copy2(input_path, os.path.join(root_dir, "input.txt"))

    # Run the Java compiler
    cp = os.path.join('out', 'production', 'Compiler')
    compile_cmd = ["java", "-cp", cp, "Compiler"]
    try:
        result = run(compile_cmd, check=True, stdout=PIPE, stderr=STDOUT, cwd=root_dir)
    except subprocess.CalledProcessError as e:
        print(f"Error running Compiler.java: {e.stderr.decode()}")
        return False

    # Run the LLVM script and append output to judge.log
    script_path = os.path.join(root_dir, 'run-llvm.sh')
    if not os.path.exists(script_path):
        print(f"Error: Script '{script_path}' does not exist.")
        return False

    with open(log_file_path, 'a') as log_file:
        result = run(['./run-llvm.sh'], stdout=log_file, stderr=log_file, cwd=root_dir)
        if result.returncode != 0:
            print(f"Error running run-llvm.sh: see {log_file_path} for details")
            return False


    # Compare
    return compare(output_path, ans_path)

ANSI_RESET = "\u001B[0m"
ANSI_RED = "\u001B[31m"
ANSI_GREEN = "\u001B[32m"
ANSI_PURPLE = "\u001B[35m"

def print_colored_text(text, color_code):
    print(color_code + text + ANSI_RESET)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Run batch tests and compare results.")
    parser.add_argument("batch_directory", type=Path, help="Path to the batch directory containing test cases")
    parser.add_argument("output_path", type=Path, help="Path to the output file for test results")

    args = parser.parse_args()

    if not args.batch_directory.exists():
        print(f"Error: Batch directory '{args.batch_directory}' does not exist.")
        exit(1)

    if not args.output_path.parent.exists():
        print(f"Error: Output directory '{args.output_path.parent}' does not exist.")
        exit(1)

    success = run_batch_tests(args.batch_directory, args.output_path)
    if success:
        print("All tests passed.")
    else:
        print("Some tests failed.")