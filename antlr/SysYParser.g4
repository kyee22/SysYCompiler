// Copyright (C) 2024 Yixuan Kuang <kyee22@buaa.edu.cn>

parser grammar SysYParser;

options {
    tokenVocab = SysYLexer;
}

program     : compUnit
            ;


// 编译单元 CompUnit → {Decl} {FuncDef} MainFuncDef
compUnit    : (decl)* (funcDef)* mainFuncDef EOF
            ;

// 声明 Decl → ConstDecl | VarDecl
decl        : constDecl
            | varDecl
            ;


// 常量声明 ConstDecl → 'const' BType ConstDef { ',' ConstDef } ';'
constDecl   : CONST bType constDef (COMMA constDef)* SEMICOLON
            ;

// 基本类型 BType → 'int' | 'char'
bType       : INT
            | CHAR
            ;

// 常量定义 ConstDef → Ident [ '[' ConstExp ']' ] '=' ConstInitVal
constDef    : IDENT (L_BRACKT constExp R_BRACKT)? ASSIGN constInitVal
            ;

// 常量初值 ConstInitVal → ConstExp | '{' [ ConstExp { ',' ConstExp } ] '}' | StringConst
constInitVal: constExp
            | L_BRACE (constExp (COMMA constExp)*)? R_BRACE
            | STRING_CONST
            ;

// 变量声明 VarDecl → BType VarDef { ',' VarDef } ';'
varDecl     : bType varDef (COMMA varDef)* SEMICOLON
            ;

// 变量定义 VarDef → Ident [ '[' ConstExp ']' ] | Ident [ '[' ConstExp ']' ] '=' InitVal
varDef      : IDENT (L_BRACKT constExp R_BRACKT)?
            | IDENT (L_BRACKT constExp R_BRACKT)? ASSIGN initVal
            ;

// 变量初值 InitVal → Exp | '{' [ Exp { ',' Exp } ] '}' | StringConst
initVal     : exp
            | L_BRACE (exp (COMMA exp)*)? R_BRACE
            | STRING_CONST
            ;


// 函数定义 FuncDef → FuncType Ident '(' [FuncFParams] ')' Block
funcDef     : funcType IDENT L_PAREN (funcFParams)? R_PAREN block
            ;

// 主函数定义 MainFuncDef → 'int' 'main' '(' ')' Block
mainFuncDef : INT MAIN L_PAREN R_PAREN block
            ;

// 函数类型 FuncType → 'void' | 'int' | 'char'
funcType    : VOID
            | INT
            | CHAR
            ;

// 函数形参表 FuncFParams → FuncFParam { ',' FuncFParam }
funcFParams : funcFParam (COMMA funcFParam)*
            ;

// 函数形参 FuncFParam → BType Ident ['[' ']']
funcFParam  : bType IDENT (L_BRACKT R_BRACKT)?
            ;

// 语句块 Block → '{' { BlockItem } '}'
block       : L_BRACE (blockItem)* R_BRACE
            ;

//语句块项 BlockItem → Decl | Stmt
blockItem   : decl
            | stmt
            ;

/**
    语句 Stmt → LVal '=' Exp ';' // 每种类型的语句都要覆盖
            | [Exp] ';' //有无Exp两种情况
            | Block
            | 'if' '(' Cond ')' Stmt [ 'else' Stmt ] // 1.有else 2.无else
            | 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt // 1. 无缺省， 1种情况 2.
            ForStmt与Cond中缺省一个， 3种情况 3. ForStmt与Cond中缺省两个， 3种情况 4. ForStmt与Cond全部
            缺省， 1种情况
            | 'break' ';' | 'continue' ';'
            | 'return' [Exp] ';' // 1.有Exp 2.无Exp
            | LVal '=' 'getint''('')'';'
            | LVal '=' 'getchar''('')'';'
            | 'printf''('StringConst {','Exp}')'';' // 1.有Exp 2.无Exp
*/

// with tag
stmt : lVal ASSIGN exp SEMICOLON                                                    #AssignStatement
     | (exp)? SEMICOLON                                                             #ExprStatement
     | block                                                                        #BlockStatement
     | IF L_PAREN cond R_PAREN ifStmt=stmt (ELSE elseStmt=stmt)?                    #IfStatement
     | FOR L_PAREN (forStmt)? SEMICOLON (cond)? SEMICOLON (forStmt)? R_PAREN stmt   #ForStatement
     | BREAK SEMICOLON                                                              #BreakStatement
     | CONTINUE SEMICOLON                                                           #ContinueStatement
     | RETURN (exp)? SEMICOLON                                                      #ReturnStatement
     | lVal ASSIGN GETINT L_PAREN R_PAREN SEMICOLON                                 #GetIntStatement
     | lVal ASSIGN GETCHAR L_PAREN R_PAREN SEMICOLON                                #GetCharStatement
     | PRINTF L_PAREN STRING_CONST (COMMA exp)* R_PAREN SEMICOLON                   #PrintfStatement
     ;

// 语句 ForStmt → LVal '=' Exp
forStmt     : lVal ASSIGN exp
            ;

// 表达式 Exp → AddExp
exp         : addExp
            ;

// 条件表达式 Cond → LOrExp
cond        : lOrExp
            ;

// 左值表达式 LVal → Ident ['[' Exp ']']
lVal        : IDENT (L_BRACKT exp R_BRACKT)?
            ;

// 基本表达式 PrimaryExp → '(' Exp ')' | LVal | Number | Character
primaryExp  : L_PAREN exp R_PAREN
            | lVal
            | number
            | character
            ;

// 数值 Number → IntConst
number      : INTEGER_CONST
            ;

character   : CHARACTER_CONST
            ;

// 一元表达式 UnaryExp → PrimaryExp | Ident '(' [FuncRParams] ')' | UnaryOp UnaryExp
unaryExp    : primaryExp
            | IDENT L_PAREN (funcRParams)? R_PAREN
            | unaryOp unaryExp
            ;

// 单目运算符 UnaryOp → '+' | '−' | '!'
unaryOp     : PLUS
            | MINUS
            | NOT
            ;

// 函数实参表 FuncRParams → Exp { ',' Exp }
funcRParams : exp (COMMA exp)*
            ;

// 乘除模表达式 MulExp → UnaryExp | MulExp ('*' | '/' | '%') UnaryExp
mulExp      : unaryExp
            | mulExp (MUL | DIV | MOD) unaryExp
            ;

// 加减表达式 AddExp → MulExp | AddExp ('+' | '−') MulExp
addExp      : mulExp
            | addExp (PLUS | MINUS) mulExp
            ;

// 关系表达式 RelExp → AddExp | RelExp ('<' | '>' | '<=' | '>=') AddExp
relExp      : addExp
            | relExp (LT | GT | LE | GE) addExp
            ;

// 相等性表达式 EqExp → RelExp | EqExp ('==' | '!=') RelExp
eqExp       : relExp
            | eqExp (EQ | NEQ) relExp
            ;

// 逻辑与表达式 LAndExp → EqExp | LAndExp '&&' EqExp
lAndExp     : eqExp
            | lAndExp AND eqExp
            ;

// 逻辑或表达式 LOrExp → LAndExp | LOrExp '||' LAndExp
lOrExp      : lAndExp
            | lOrExp OR lAndExp
            ;

// 常量表达式 ConstExp → AddExp 注： 使用的 Ident 必须是常量
constExp    : addExp
            ;


