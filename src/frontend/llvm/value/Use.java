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

package frontend.llvm.value;

import frontend.llvm.value.user.User;

public class Use {
    private User user;
    private int argno;

    public Use(User user, int argno) {
        this.user = user;
        this.argno = argno;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof Use)) {
            return false;
        }
        Use use = (Use) obj;
        return user.equals(use.user) && argno == use.argno;
    }

    public User getUser() {
        return user;
    }

    public int getArgNo() {
        return argno;
    }
}
