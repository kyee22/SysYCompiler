# SysY Compiler

Lab assignments for *Compiling Techniques* @ Beihang University, Fall 2024  

## Usage

TODO

## Python Scripts

### Lab 0: Grammar Understanding

*   **submit assignments:**

    run

    ```bash
    $ cd pytest/testcases/grammar-understaing-private/
    $ python3 submit.py
    Successfully created txt_files.zip containing 18 .txt files.
    ```

    a `.zip` file containing all the required `.txt` files will be created in `pytest/testcases/grammar-understaing-private/`.

*   **test SysY file:**

    run

    ```bash
    $ cd pytest/testcases/grammar-understaing-private/
    $ python3 test.py testfile{x}.txt
    ```

    Some tedious staff will be made to execute `testfile{x}.txt` via `gcc`, with standard input redirected to `input{x}.txt` & standard output redirected to `output{x}.txt`. (`x` = 1, 2, ..., 6)

### Lab 1: Lexer

*   **submit assignments:**

    run

    ```bash
    $ python3 submit/submit.py                                
    压缩完成，压缩包保存到: submit/src_files.zip
    ```

    a `.zip` file meeting the following requirement will be created in `submit/`.

    ```
    .
    ├── Compiler.java
    ├── config.json
    └── frontend
             └── Lexer.java
    ```

*   **local lexer test:**

    ```bash
    $ python3 pytest/lexer-test.py                     
    [       OK ] TEST A.testcase1 (0.45 s)
    [       OK ] TEST A.testcase2 (0.45 s)
    [       OK ] TEST A.testcase3 (0.45 s)
    ......omitted ......
    [       OK ] TEST C.testcase15 (0.46 s)
    [       OK ] TEST C.testcase16 (0.51 s)
    
    [==========] 33 tests from 1 test suite ran. (14.64 s total)
    [  PASSED  ] 33 tests.
    
    ✅  Congratulations!
    ```

*   **local lexer error handling test:**

    ```bash
    $ python3 pytest/lexer-err-test.py                 
    [       OK ] TEST testcase1 (0.37 s)
    [       OK ] TEST testcase2 (0.24 s)
    [       OK ] TEST testcase3 (0.63 s)
    [       OK ] TEST testcase4 (0.55 s)
    
    [==========] 4 tests from 1 test suite ran. (1.84 s total)
    [  PASSED  ] 4 tests.
    
    ✅  Congratulations!
    ```

    

