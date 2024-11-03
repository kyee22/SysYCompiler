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

package frontend.llvm;

import frontend.llvm.type.Type;
import frontend.llvm.value.BasicBlock;
import frontend.llvm.value.Function;
import frontend.llvm.value.Value;
import frontend.llvm.value.user.instr.*;

import java.util.List;

public class IRBuilder {
    private Module module;
    private BasicBlock basicBlock;

    public IRBuilder(Module module, BasicBlock basicBlock) {
        this.module = module;
        this.basicBlock = basicBlock;
    }

    public Module getModule() {return module;}
    public BasicBlock getInsertBlock() {return basicBlock;}
    public void setInsertPoint(BasicBlock basicBlock) {this.basicBlock = basicBlock;}

    public Instruction createBr(BasicBlock ifTrue) {return BranchInst.createBr(ifTrue, basicBlock);}
    public Instruction createCondBr(Value cond, BasicBlock ifTrue, BasicBlock ifFalse) {return BranchInst.createCondBr(cond, ifTrue, ifFalse, basicBlock);}
    public Instruction createLt(Value val1, Value val2) {return ICmpInst.createLt(val1, val2, basicBlock);}
    public Instruction createLe(Value val1, Value val2) {return ICmpInst.createLe(val1, val2, basicBlock);}
    public Instruction createGt(Value val1, Value val2) {return ICmpInst.createGt(val1, val2, basicBlock);}
    public Instruction createGe(Value val1, Value val2) {return ICmpInst.createGe(val1, val2, basicBlock);}
    public Instruction createEq(Value val1, Value val2) {return ICmpInst.createEq(val1, val2, basicBlock);}
    public Instruction createNe(Value val1, Value val2) {return ICmpInst.createNe(val1, val2, basicBlock);}
    public Instruction createCall(Function function, List<Value> arguments) {return CallInstr.createCall(function, arguments, basicBlock);}
    public Instruction createLoad(Value ptr) {return LoadInst.createLoad(ptr, basicBlock);}
    public Instruction createStore(Value val, Value ptr) {return StoreInst.createStore(val, ptr, basicBlock);}
    public Instruction createGetElementPtr(Value ptr, List<Value> idxs) {return GetElementPtrInst.createGep(ptr, idxs, basicBlock);}
    public Instruction createAlloca(Type type) {return AllocaInst.createAlloca(type, basicBlock);}
    public Instruction createAdd(Value value1, Value value2) {return IBinaryInst.createAdd(value1, value2, basicBlock);}
    public Instruction createSub(Value value1, Value value2) {return IBinaryInst.createSub(value1, value2, basicBlock);}
    public Instruction createMul(Value value1, Value value2) {return IBinaryInst.createMul(value1, value2, basicBlock);}
    public Instruction createSdiv(Value value1, Value value2) {return IBinaryInst.createSdiv(value1, value2, basicBlock);}
    public Instruction createSrem(Value value1, Value value2) {return IBinaryInst.createSrem(value1, value2, basicBlock);}
    public Instruction createTruncToInt1(Value value) {return CastInst.createTruncToInt1(value, basicBlock);}
    public Instruction createTruncToInt8(Value value) {return CastInst.createTruncToInt8(value, basicBlock);}
    public Instruction createZextToInt8(Value value) {return CastInst.createZextToInt8(value, basicBlock);}
    public Instruction createZextToInt32(Value value) {return CastInst.createZextToInt32(value, basicBlock);}
    public Instruction createRet(Value value) {return ReturnInst.createRet(value, basicBlock);}
    public Instruction createVoidRet() {return ReturnInst.createVoidRet(basicBlock);}
}
