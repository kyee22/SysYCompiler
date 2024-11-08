/*
 * SysYCompiler: A Compiler for SysY.
 *
 * SysYCompiler is an individually developed course project
 * for Compiling Techniques @ School of Computer Science &
 * Engineering, Beihang University, Fall 2024.
 *
 * Copyright (C) 2024 Yixuan Kuang <kyee22@buaa.edu.cn>
 *
 * This file is part of SysYCompiler.
 */

package frontend.core;

import frontend.llvm.IRBuilder;
import frontend.llvm.Module;
import frontend.llvm.type.*;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.GlobalVariable;
import frontend.llvm.value.user.constant.Constant;
import frontend.llvm.value.user.constant.ConstantArray;
import frontend.llvm.value.user.constant.ConstantInt;
import static frontend.sysy.token.TokenType.*;

import frontend.llvm.value.user.constant.ConstantZero;
import frontend.sysy.context.*;
import frontend.sysy.token.TokenType;
import utils.FileUtils;
import utils.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class IRGenVisitor extends BaseContextVisitor<Value> {
    private Module module = new Module();
    private IRBuilder builder = new IRBuilder(module, null);
    private SymbolTable<Value> curScope = new SymbolTable<>();
    private Map<String, Value> stringLiteralPool = new HashMap<>();

    private Stack<BasicBlock> continueTargets = new Stack<>();
    private Stack<BasicBlock> breakTargets = new Stack<>();
    
    /******************* 预处理常量 *******************/
    private ConstantInt i32Zero = ConstantInt.getInt(0, module);
    private IntegerType i32Ty = module.getInt32Type();
    private IntegerType i8Ty = module.getInt8Type();
    private IntegerType i1Ty = module.getInt1Type();

    /******************* VarDef ConstDef FuncFParam 的综合属性 *******************/
    private Type baseType = null;

    /******************* InitVal ConstInitVal 的继承属性 *******************/
    private Type defType = null;
    private Value defAddr = null;

    /******************* FuncDef 的综合属性 *******************/
    private List<Type> paramTypes = new ArrayList<>();
    private List<String> paramNames = new ArrayList<>();
    private Type retType = null;

    /******************* UnaryExp#CallExp 的综合属性 *******************/
    private Stack<List<Value>> args = new Stack<>();

    private BasicBlock trueBlock = null, falseBlock = null, genBlock = null;

    public IRGenVisitor() {
        loadLibraries();
    }

    @Override
    public Value visit(BTypeContext ctx) {
        baseType = ctx.INTTK() != null ? i32Ty : i8Ty;
        return null;
    }

    @Override
    public Value visit(VarDefContext ctx) {
        defType = baseType;
        if (ctx.LBRACK() != null) {
            if (visit(ctx.constExp()) instanceof ConstantInt lenInt) {
                defType = module.getArrayType(defType, lenInt.getValue());
            } else {
                throw new RuntimeException("Array length is not compile-time constant");
            }
        }

        String name = ctx.IDENFR().getToken().getText();
        Value value;
        if (curScope.getParent() == null) {
            Constant init;
            if (ctx.initVal() == null) {
                init = ConstantZero.get(defType);
            } else if (ctx.initVal().LBRACE() != null) {
                ArrayType arrayType = (ArrayType) defType;
                List<Constant> elems = new ArrayList<>();
                int i = 0;
                for (; i < ctx.initVal().exp().size(); ++i) {
                    elems.add((Constant) icast(arrayType.getElementType(), visit(ctx.initVal().exp(i))));
                }
                for (; i < arrayType.getNumberOfElements(); ++i) {
                    elems.add(makeDefaultInit(arrayType.getElementType()));
                }
                init = ConstantArray.get(arrayType, elems);
            } else if (ctx.initVal().STRCON() != null) {
                init = makeGlobalString((ArrayType) defType, ctx.initVal().STRCON().getToken().getText());
            } else {
                init = (Constant) icast(defType, visit(ctx.initVal().exp(0)));
            }
            value = GlobalVariable.create(name, module, defType, false, init);
        } else {
            value = builder.createAlloca(defType);
            if (ctx.initVal() != null) {
                defAddr = value;
                visit(ctx.initVal());
            }
        }
        curScope.define(name, value);
        return null;
    }

    @Override
    public Value visit(ConstDefContext ctx) {
        defType = baseType;
        if (ctx.LBRACK() != null) {
            if (visit(ctx.constExp()) instanceof ConstantInt lenInt) {
                defType = module.getArrayType(defType, lenInt.getValue());
            } else {
                throw new RuntimeException("Array length is not compile-time constant");
            }
        }
        String name = ctx.IDENFR().getToken().getText();
        Value value;

        if (curScope.getParent() == null) {
            Constant init;
            if (ctx.constInitVal().LBRACE() != null) {
                ArrayType arrayType = (ArrayType) defType;
                List<Constant> elems = new ArrayList<>();
                int i = 0;
                for (; i < ctx.constInitVal().constExp().size(); ++i) {
                    elems.add((Constant) icast(arrayType.getElementType(), visit(ctx.constInitVal().constExp(i))));
                }
                for (; i < arrayType.getNumberOfElements(); ++i) {
                    elems.add(makeDefaultInit(arrayType.getElementType()));
                }
                init = ConstantArray.get(arrayType, elems);
            } else if (ctx.constInitVal().STRCON() != null) {
                init = makeGlobalString((ArrayType) defType, ctx.constInitVal().STRCON().getToken().getText());
            } else {
                init = (Constant) icast(defType, visit(ctx.constInitVal().constExp(0)));
            }
            value = GlobalVariable.create(name, module, defType, true, init);
        } else {
            value = builder.createAlloca(defType);
            defAddr = value;
            Value r = visit(ctx.constInitVal());
            if (ctx.constInitVal().LBRACE() == null && ctx.constInitVal().STRCON() == null) {
                value = r;
            }
        }
        curScope.define(name, value);
        return null;
    }

    @Override
    public Value visit(InitValContext ctx) {
        if (curScope.getParent() != null) {
            if (ctx.LBRACE() != null) {
                ArrayType arrayType = (ArrayType) defType;
                int numberOfElements = arrayType.getNumberOfElements();
                int expSize = ctx.exp().size();

                for (int i = 0; i < numberOfElements; ++i) {
                    Value val = i < expSize
                            ? icast(arrayType.getElementType(), visit(ctx.exp(i)))
                            : makeDefaultInit(arrayType.getElementType());

                    Value addr = builder.createGetElementPtr(defAddr,
                            List.of(i32Zero, ConstantInt.getInt(i, module)));
                    builder.createStore(val, addr);
                }
            } else if (ctx.STRCON() != null) {
                makeLocalString((ArrayType) defType, ctx.STRCON().getToken().getText(), defAddr);
            } else {
                Value value = icast(defType, visit(ctx.exp(0)));
                builder.createStore(value, defAddr);
            }
        }
        return null;
    }

    @Override
    public Value visit(ConstInitValContext ctx) {
        if (curScope.getParent() != null) {
            if (ctx.LBRACE() != null) {
                ArrayType arrayType = (ArrayType) defType;
                int numberOfElements = arrayType.getNumberOfElements();
                int constExpSize = ctx.constExp().size();

                for (int i = 0; i < numberOfElements; ++i) {
                    Value val = i < constExpSize
                            ? icast(arrayType.getElementType(), visit(ctx.constExp(i)))
                            : makeDefaultInit(arrayType.getElementType());

                    Value addr = builder.createGetElementPtr(defAddr,
                            List.of(i32Zero, ConstantInt.getInt(i, module)));
                    builder.createStore(val, addr);
                }
            } else if (ctx.STRCON() != null) {
                makeLocalString((ArrayType) defType, ctx.STRCON().getToken().getText(), defAddr);
            } else {
                return icast(defType, visit(ctx.constExp(0)));
            }
        }
        return null;
    }

    @Override
    public Value visit(ReturnStmtContext ctx) {
        Type returnType = builder.getInsertBlock().getParent().getReturnType();
        if (!returnType.isVoidType()) {
            Value retVal = icast(returnType, visit(ctx.exp()));
            return builder.createRet(retVal);
        } else {
            return builder.createVoidRet();
        }
    }

    @Override
    public Value visit(NumberContext ctx) {
        int ascii = StringUtils.resolveAscii(ctx.INTCON().getToken().getText());
        return ConstantInt.getInt(ascii, module);
    }

    @Override
    public Value visit(CharacterContext ctx) {
        int ascii = StringUtils.resolveAscii(ctx.CHRCON().getToken().getText());
        return ConstantInt.getInt(ascii, module);
    }

    @Override
    public Value visit(LValContext ctx) {
        String name = ctx.IDENFR().getToken().getText();
        Value addr = curScope.globalResolve(name);

        if (addr instanceof ConstantInt) {
            return addr;
        }

        Value offset = null;
        if (ctx.LBRACK() != null) {
            offset = icast(i32Ty, visit(ctx.exp()));
        }

        if (addr instanceof GlobalVariable globalVar && globalVar.isConst()) {
            Value init = globalVar.getInit();
            if (!init.getType().isArrayType()) {
                return init;
            }
            if (init instanceof ConstantArray initArray
                    && offset != null
                    && offset instanceof ConstantInt offsetInt) {
                return initArray.getElementValue(offsetInt.getValue());
            }
        }

        if (ctx.LBRACK() != null) {
            Type elemTy = ((PointerType) addr.getType()).getElementType();
            if (elemTy.isArrayType()) {
                addr = builder.createGetElementPtr(addr,
                        List.of(i32Zero, offset));
            } else {
                addr = builder.createLoad(addr);
                addr = builder.createGetElementPtr(addr, List.of(offset));
            }
        }
        return addr;
    }

    @Override
    public Value visit(PrimaryExpContext ctx) {
        if (ctx.number() != null) {
            return visit(ctx.number());
        } else if (ctx.character() != null) {
            return visit(ctx.character());
        } else if (ctx.exp() != null) {
            return visit(ctx.exp());
        } else {
            Value val = visit(ctx.lVal());
            // 如果返回的是 Contant, 说明是在 visitLVal 时已经求解好的常量
            if (val instanceof Constant) {
                return val;
            }
            // 否则返回的是地址
            Type elemTy = ((PointerType) val.getType()).getElementType();
            if (elemTy.isArrayType()) { // 1: 数组名 evaluate 为首元素地址
                return builder.createGetElementPtr(val, List.of(i32Zero, i32Zero));
            } else {                    // 2: 基本值 evaluate 为某个具体的值
                return builder.createLoad(val);
            }
        }
    }

    @Override
    public Value visit(UnaryExpContext ctx) {
        if (ctx.primaryExp() != null) {
            return visit(ctx.primaryExp());
        } else if (ctx.unaryOp() != null) {
            Value value = visit(ctx.unaryExp());
            // 处理一元运算符合
            if (ctx.unaryOp().PLUS() != null) { // PLUS 则直接返回
                return value;
            } else if (ctx.unaryOp().MINUS() != null) { // MINU 则返回 0 - value 的结果
                return eval(i32Zero, value, MINU);
            } else {
                return eval(i32Zero, value, EQL);
            }
        } else {
            Function function = (Function) curScope.globalResolve(ctx.IDENFR().getToken().getText());
            args.add(new ArrayList<>());
            if (ctx.funcRParams() != null) {
                visit(ctx.funcRParams());
            }
            List<Value> castedArgs = new ArrayList<>();
            for (int i = 0; i < args.peek().size(); ++i) {
                castedArgs.add(icast(function.getFunctionType().getParamType(i), args.peek().get(i)));
            }
            args.pop();
            return builder.createCall(function, castedArgs);
        }
    }

    @Override
    public Value visit(FuncRParamsContext ctx) {
        for (int i = 0; i < ctx.exp().size(); ++i) {
            args.peek().add(visit(ctx.exp(i)));
        }
        return null;
    }

    @Override
    public Value visit(MulExpContext ctx) {
        Value value = visit(ctx.unaryExp(0));
        for (int i = 1; i < ctx.unaryExp().size(); ++i) {
            value = eval(value, visit(ctx.unaryExp(i)), ctx.OP(i - 1).getToken().getType());
        }
        return value;
    }

    @Override
    public Value visit(AddExpContext ctx) {
        Value value = visit(ctx.mulExp(0));
        for (int i = 1; i < ctx.mulExp().size(); ++i) {
            value = eval(value, visit(ctx.mulExp(i)), ctx.OP(i - 1).getToken().getType());
        }
        return value;
    }

    @Override
    public Value visit(RelExpContext ctx) {
        Value value = visit(ctx.addExp(0));
        for (int i = 1; i < ctx.addExp().size(); ++i) {
            value = eval(value, visit(ctx.addExp(i)), ctx.OP(i - 1).getToken().getType());
        }
        return value;
    }

    @Override
    public Value visit(EqExpContext ctx) {
        builder.setInsertPoint(genBlock);
        Value value = visit(ctx.relExp(0));
        for (int i = 1; i < ctx.relExp().size(); ++i) {
            value = eval(value, visit(ctx.relExp(i)), ctx.OP(i - 1).getToken().getType());
        }
        Value cond = icast(i1Ty, eval(value, i32Zero, NEQ));
        builder.createCondBr(cond, trueBlock, falseBlock);
        return null;
    }

    @Override
    public Value visit(LAndExpContext ctx) {
        BasicBlock hold = trueBlock;
        for (int i = 0; i < ctx.eqExp().size() - 1; ++i) {
            trueBlock = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
            visit(ctx.eqExp(i));
            genBlock = trueBlock;
        }
        trueBlock = hold;
        visit(ctx.eqExp(ctx.eqExp().size() - 1));
        return null;
    }

    @Override
    public Value visit(LOrExpContext ctx) {
        BasicBlock hold = falseBlock;
        for (int i = 0; i < ctx.lAndExp().size() - 1; ++i) {
            falseBlock = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
            visit(ctx.lAndExp(i));
            genBlock = falseBlock;
        }
        falseBlock = hold;
        visit(ctx.lAndExp(ctx.lAndExp().size() - 1));
        return null;
    }

    @Override
    public Value visit(IfStmtContext ctx) {

        BasicBlock ifTrue = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
        trueBlock = ifTrue;
        BasicBlock ifEnd = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
        falseBlock = ifEnd;
        BasicBlock ifFalse = null;
        if (ctx.ELSETK() != null) {
            ifFalse = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
            falseBlock = ifFalse;
        }
        genBlock = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
        builder.createBr(genBlock);
        visit(ctx.cond());

        builder.setInsertPoint(ifTrue);
        visit(ctx.ifStmt());
        builder.createBr(ifEnd);

        if (ctx.ELSETK() != null) {
            builder.setInsertPoint(ifFalse);
            visit(ctx.elseStmt());
            builder.createBr(ifEnd);
        }
        builder.setInsertPoint(ifEnd);
        return null;
    }

    @Override
    public Value visit(ForloopStmtContext ctx) {
        if (ctx.forStmt1() != null) {
            visit(ctx.forStmt1());
        }

        BasicBlock forEnd = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
        BasicBlock forTrue = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
        genBlock = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
        BasicBlock continueTarget = genBlock;
        builder.createBr(genBlock);
        if (ctx.cond() == null) {
            builder.setInsertPoint(genBlock);
            builder.createBr(forTrue);
        } else {
            trueBlock = forTrue;
            falseBlock = forEnd;
            visit(ctx.cond());
        }

        if (ctx.forStmt2() != null) {
            BasicBlock forStmt2 = BasicBlock.create(module, "", builder.getInsertBlock().getParent());
            builder.setInsertPoint(forStmt2);
            visit(ctx.forStmt2());
            builder.createBr(continueTarget);
            continueTarget = forStmt2;
        }

        continueTargets.add(continueTarget);
        breakTargets.add(forEnd);
        builder.setInsertPoint(forTrue);
        visit(ctx.stmt());
        builder.createBr(continueTarget);
        continueTargets.pop();
        breakTargets.pop();

        builder.setInsertPoint(forEnd);
        return null;
    }

    @Override
    public Value visit(BreakStmtContext ctx) {
        builder.createBr(breakTargets.peek());
        return null;
    }

    @Override
    public Value visit(ContinueStmtContext ctx) {
        builder.createBr(continueTargets.peek());
        return null;
    }

    /******************************* BEGIN func defs *******************************/
    @Override
    public Value visit(MainFuncDefContext ctx) {
        FunctionType funcTy = FunctionType.get(i32Ty, List.of());
        Function func = Function.create(funcTy, "main", module);
        BasicBlock bb = BasicBlock.create(module, "", func);
        builder.setInsertPoint(bb);
        curScope = new SymbolTable(curScope);
        return visit(ctx.block());
    }

    @Override
    public Value visit(FuncDefContext ctx) {
        paramNames.clear();
        paramTypes.clear();
        visit(ctx.funcType());
        if (ctx.funcFParams() != null) {
            visit(ctx.funcFParams());
        }
        String funcName = ctx.IDENFR().getToken().getText();
        FunctionType functionType = module.getFunctionType(retType, paramTypes);
        Function function = Function.create(functionType, funcName, module);

        curScope.define(funcName, function);
        BasicBlock bb = BasicBlock.create(module, "", function);
        builder.setInsertPoint(bb);

        curScope = new SymbolTable(curScope);
        for (int i = 0; i < paramTypes.size(); ++i) {
            Value addr = builder.createAlloca(paramTypes.get(i));
            builder.createStore(function.getArguments().get(i), addr);
            curScope.define(paramNames.get(i), addr);
        }
        Value r = visit(ctx.block());
        if (retType.isVoidType() && !builder.getInsertBlock().isTerminated()) {
            builder.createVoidRet();
        }
        return r;
    }


    @Override
    public Value visit(FuncFParamContext ctx) {
        visit(ctx.bType());
        defType = baseType;
        if (ctx.LBRACK() != null) {
            defType = module.getPointerType(defType);
        }
        paramTypes.add(defType);
        paramNames.add(ctx.IDENFR().getToken().getText());
        return null;
    }

    @Override
    public Value visit(FuncTypeContext ctx) {
        if (ctx.VOIDTK() != null) {
            retType = module.getVoidType();
        } else if (ctx.INTTK() != null) {
            retType = i32Ty;
        } else if (ctx.CHARTK() != null) {
            retType = i8Ty;
        } else {
            throw new RuntimeException("Unsupported function type");
        }
        return null;
    }

    @Override
    public Value visit(BlockContext ctx) {
        if (!(ctx.getParent() instanceof FuncDefContext) && !(ctx.getParent() instanceof MainFuncDefContext)) {
            curScope = new SymbolTable(curScope);
        }
        Value r = super.visit(ctx);
        curScope = curScope.getParent();
        return r;
    }
    /******************************* END func defs *******************************/

    @Override
    public Value visit(PrintfStmtContext ctx) {
        Function putint = (Function) curScope.globalResolve("putint");
        Function putch = (Function) curScope.globalResolve("putch");
        Function putstr = (Function) curScope.globalResolve("putstr");
        int i = 0;
        String strcon = ctx.STRCON().getToken().getText();
        for (String str : StringUtils.splitFormatString(strcon.substring(1, strcon.length() - 1))) {
            switch (str) {
                case "%d":
                    builder.createCall(putint, List.of(icast(i32Ty, visit(ctx.exp(i++)))));
                    break;
                case "%c":
                    builder.createCall(putch, List.of(icast(i32Ty, visit(ctx.exp(i++)))));
                    break;
                default:
                    if (!str.isEmpty()) {
                        Value addr = fromStringLiteralPool("\"" + str + "\"");
                        addr = builder.createGetElementPtr(addr, List.of(i32Zero, i32Zero));
                        builder.createCall(putstr, List.of(addr));
                    }
                    break;
            }
        }
        return null;
    }

    /******************************* BEGIN stmts about assignment *******************************/
    @Override
    public Value visit(AssignStmtContext ctx) {
        Value addr = visit(ctx.lVal());
        Type destType = ((PointerType) addr.getType()).getElementType();
        Value val = icast(destType, visit(ctx.exp()));
        return builder.createStore(val, addr);
    }

    @Override
    public Value visit(ForStmtContext ctx) {
        Value addr = visit(ctx.lVal());
        Type destType = ((PointerType) addr.getType()).getElementType();
        Value val = icast(destType, visit(ctx.exp()));
        return builder.createStore(val, addr);
    }

    @Override
    public Value visit(GetIntStmtContext ctx) {
        Value addr = visit(ctx.lVal());
        Type destType = ((PointerType) addr.getType()).getElementType();
        Function getint = (Function) curScope.globalResolve("getint");
        Value retVal = builder.createCall(getint, List.of());
        Value val = icast(destType, retVal);
        return builder.createStore(val, addr);
    }

    @Override
    public Value visit(GetCharStmtContext ctx) {
        Value addr = visit(ctx.lVal());
        Type destType = ((PointerType) addr.getType()).getElementType();
        Function getint = (Function) curScope.globalResolve("getchar");
        Value retVal = builder.createCall(getint, List.of());
        Value val = icast(destType, retVal);
        return builder.createStore(val, addr);
    }
    /******************************* END stmts about assignment *******************************/


    /******************************* BEGIN helper functions *******************************/
    private Value eval(Value value1, Value value2, TokenType opTy) {
        // 作为右值运算的操作数必须整值提升
        value1 = icast(i32Ty, value1);
        value2 = icast(i32Ty, value2);

        // 编译时完成常量运算
        if (value1 instanceof ConstantInt c1 && value2 instanceof ConstantInt c2) {
            int result = switch (opTy) {
                case PLUS   -> c1.getValue() +  c2.getValue();
                case MINU   -> c1.getValue() -  c2.getValue();
                case MULT   -> c1.getValue() *  c2.getValue();
                case DIV    -> c1.getValue() /  c2.getValue();
                case MOD    -> c1.getValue() %  c2.getValue();
                case LSS    -> c1.getValue() <  c2.getValue() ? 1 : 0;
                case LEQ    -> c1.getValue() <= c2.getValue() ? 1 : 0;
                case GRE    -> c1.getValue() >  c2.getValue() ? 1 : 0;
                case GEQ    -> c1.getValue() >= c2.getValue() ? 1 : 0;
                case EQL    -> c1.getValue() == c2.getValue() ? 1 : 0;
                case NEQ    -> c1.getValue() != c2.getValue() ? 1 : 0;
                default -> throw new RuntimeException("Unknown op type: " + opTy);
            };
            return ConstantInt.getInt(result, module);
        }
        
        return switch (opTy) {
            case PLUS  -> builder.createAdd(value1, value2);
            case MINU  -> builder.createSub(value1, value2);
            case MULT  -> builder.createMul(value1, value2);
            case DIV   -> builder.createSdiv(value1, value2);
            case MOD   -> builder.createSrem(value1, value2);
            case LSS   -> builder.createLt(value1, value2);
            case LEQ   -> builder.createLe(value1, value2);
            case GRE   -> builder.createGt(value1, value2);
            case GEQ   -> builder.createGe(value1, value2);
            case EQL   -> builder.createEq(value1, value2);
            case NEQ   -> builder.createNe(value1, value2);
            default    -> { throw new RuntimeException("Unknown op type: " + opTy); }
        };
    }

    private Constant makeDefaultInit(Type type) {
        //return ConstantZero.get(type);
        if (type.isInt8Type()) {
            return ConstantInt.getChar(0, module);
        }
        if (type.isInt32Type()) {
            return i32Zero;
        }
        if (type.isArrayType()) {
            ArrayType arrayType = (ArrayType) type;
            List<Constant> elems = IntStream.range(0, arrayType.getNumberOfElements())
                    .mapToObj(i -> makeDefaultInit(arrayType.getElementType()))
                    .collect(Collectors.toList());
            return ConstantArray.get(arrayType, elems);
        }
        throw new RuntimeException("Unsupported type: " + type);
    }

    private Value icast(Type destType, Value value) {
        if (destType instanceof IntegerType ty1 && value.getType() instanceof IntegerType ty2
                && ty1.getNumBits() != ty2.getNumBits()) {
            if (value instanceof ConstantInt constValue) {
                int val = constValue.getValue();
                value = destType.isInt1Type()
                        ? ConstantInt.getBool(val != 0, module)
                        : destType.isInt8Type()
                            ? ConstantInt.getChar(val & 0xff, module)
                            : ConstantInt.getInt(val, module);
            } else {
                value = destType.isInt1Type()
                        ? builder.createTruncToInt1(value)
                        : destType.isInt8Type()
                            ? ty2.getNumBits() > 8
                                ? builder.createTruncToInt8(value)
                                : builder.createZextToInt8(value)
                            : builder.createZextToInt32(value);
            }
        }
        return value;
    }

    private ConstantArray makeGlobalString(ArrayType arrayType, String str) {
        List<Integer> asciis = StringUtils.resolveAsciis(str);
        int numberOfElements = arrayType.getNumberOfElements();

        List<Constant> elems = IntStream.range(0, numberOfElements)
                .mapToObj(i -> i < asciis.size() ? ConstantInt.getChar(asciis.get(i), module) : makeDefaultInit(arrayType.getElementType()))
                .collect(Collectors.toList());

        return ConstantArray.get(arrayType, elems);
    }

    private Value fromStringLiteralPool(String str) {
        return stringLiteralPool.computeIfAbsent(str, s -> {
            List<Integer> asciis = StringUtils.resolveAsciis(s);
            List<Constant> elems = IntStream.range(0, asciis.size())
                    .mapToObj(i -> ConstantInt.getChar(asciis.get(i), module))
                    .collect(Collectors.toList());
            elems.add(ConstantInt.getChar('\0', module));

            ArrayType arrayType = module.getArrayType(i8Ty, asciis.size() + 1);
            Constant init = ConstantArray.get(arrayType, elems);
            return GlobalVariable.create(".str" + stringLiteralPool.size(), module, arrayType, true, init);
        });
    }


    private void makeLocalString(ArrayType arrayType, String str, Value baseAddr) {
        List<Integer> asciis = StringUtils.resolveAsciis(str);
        int numberOfElements = arrayType.getNumberOfElements();

        for (int i = 0; i < numberOfElements; ++i) {
            Value val = i < asciis.size()
                    ? ConstantInt.getChar(asciis.get(i), module)
                    : makeDefaultInit(arrayType.getElementType());

            Value addr = builder.createGetElementPtr(baseAddr,
                    List.of(i32Zero, ConstantInt.getInt(i, module)));
            builder.createStore(val, addr);
        }
    }
    /******************************* END helper functions *******************************/

    public Module getModule() {return builder.getModule();}

    private void loadLibraries() {
        FunctionType functionType = module.getFunctionType(i32Ty, List.of());
        Function getint = Function.create(functionType, "getint", module);
        Function getchar = Function.create(functionType, "getchar", module);

        functionType = module.getFunctionType(module.getVoidType(), List.of(i32Ty));
        Function putint = Function.create(functionType, "putint", module);
        Function putch = Function.create(functionType, "putch", module);

        functionType = module.getFunctionType(module.getVoidType(), List.of(module.getInt8PointerType()));
        Function putstr = Function.create(functionType, "putstr", module);

        curScope.define("getint", getint);
        curScope.define("getchar", getchar);
        curScope.define("putint", putint);
        curScope.define("putch", putch);
        curScope.define("putstr", putstr);
    }

    public void dump(String filePath) {
        FileUtils.writeStringToFile(filePath, module.print());
    }
}
