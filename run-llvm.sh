#!/bin/bash

# Define the library file path
LIBRARY_FILE="test/resources/llvm_ir_io/lib.ll"
LLVM_IR_FILE="llvm_ir.txt"
INPUT_FILE="input.txt"
OUTPUT_FILE="output.txt"

# Check if the input file exists
if [[ ! -f "$INPUT_FILE" ]]; then
    echo "Input file input.txt does not exist."
    exit 1
fi
echo "Input file input.txt exists."

# Check if llvm_ir.txt exists
if [[ ! -f "$LLVM_IR_FILE" ]]; then
    echo "LLVM IR file llvm_ir.txt does not exist."
    exit 1
fi
echo "LLVM IR file llvm_ir.txt exists."

# Check if the library file exists
if [[ ! -f "$LIBRARY_FILE" ]]; then
    echo "Library file $LIBRARY_FILE does not exist."
    exit 1
fi
echo "Library file $LIBRARY_FILE exists."

# Link object file and library using llvm-link
echo "Linking $LLVM_IR_FILE and $LIBRARY_FILE..."
llvm-link llvm_ir.txt "$LIBRARY_FILE" -o llvm_program
if [[ $? -eq 0 ]]; then
    echo "Successfully linked $LLVM_IR_FILE and $LIBRARY_FILE into llvm_program."
else
    echo "Error linking files."
    exit 1
fi

chmod +x ./llvm_program

# Execute the program and redirect input/output
echo "Executing the program with input from input.txt..."
./llvm_program < $INPUT_FILE > $OUTPUT_FILE
exit_code=$?

echo "================= 🌟 Exit Code 🌟 =================="
echo $exit_code
echo "================= 📝 Std Input 📝 =================="
cat $INPUT_FILE
echo ""
echo "================= 🍟 Std Output 🍟 ================="
cat $OUTPUT_FILE
echo ""
echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"

if [[ $exit_code -eq 0 ]]; then
    echo "Program executed successfully. Output redirected to output.txt."
else
    echo "Error executing the program. Exit code: $exit_code"
    exit 1
fi

# Indicate completion
echo "Program execution completed."
rm ./llvm_program