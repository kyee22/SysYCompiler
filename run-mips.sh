#!/bin/bash

MARS_JAR_FILE="test/resources/mars/MARS-2024.jar"
MIPS_FILE="mips.txt"
INPUT_FILE="input.txt"
OUTPUT_FILE="output.txt"
INST_STAT_FILE="InstructionStatistics.txt"

# Check if the input file exists
if [[ ! -f "$INPUT_FILE" ]]; then
    echo "Input file $INPUT_FILE does not exist."
    exit 1
fi
echo "Input file $INPUT_FILE exists."

if [[ ! -f "$MARS_JAR_FILE" ]]; then
    echo "MARS Jar file $MARS_JAR_FILE does not exist."
    exit 1
fi
echo "MARS Jar file $MARS_JAR_FILE exists."

if [[ ! -f "$MIPS_FILE" ]]; then
    echo "MIPS file $MIPS_FILE does not exist."
    exit 1
fi
echo "MIPS file $MIPS_FILE exists."


# Execute the program and redirect input/output
echo "Executing the program with input from input.txt..."
java -jar $MARS_JAR_FILE $MIPS_FILE < $INPUT_FILE | sed '1,2d' > $OUTPUT_FILE
exit_code=$?

echo "================= 🛡️ Exit Code 🛡️ =================="
echo $exit_code
echo "================= 🌟 Std Input 🌟 =================="
cat $INPUT_FILE
echo ""
echo "================= 🚀 Std Output 🚀 ================="
cat $OUTPUT_FILE
echo ""
echo "================= 📝 Instr Stat 📝 ================="
cat $INST_STAT_FILE
echo "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"

if [[ $exit_code -eq 0 ]]; then
    echo "Program executed successfully. Output redirected to output.txt."
else
    echo "Error executing the program. Exit code: $exit_code"
    exit 1
fi

# Indicate completion
echo "Program execution completed."
