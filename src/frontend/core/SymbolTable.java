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

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SymbolTable<T> {
    private Map<String, T> table = new LinkedHashMap<>();
    private int level;
    private SymbolTable<T> parent;
    private List<SymbolTable<T>> children = new ArrayList();
    private int id;

    public SymbolTable(int id) {
        this.id = id;
        this.level = 1;
        this.parent = null;
    }

    public SymbolTable(int id, SymbolTable<T> parent) {
        this.id = id;
        this.parent = parent;
        this.level = this.parent.level + 1;
        this.parent.addChild(this);
    }


    public void define(String name, T value) {
        table.put(name, value);
    }

    public T localResolve(String name) {
        if (table.containsKey(name)) {
            return table.get(name);
        }
        return null;
    }

    public T globalResolve(String name) {
        if (table.containsKey(name)) {
            return table.get(name);
        } else if (parent != null) {
            return parent.globalResolve(name);
        }
        return null;
    }

    public void addChild(SymbolTable<T> child) {
        children.add(child);
    }

    public List<String> getRecords() {
        return table.entrySet().stream()
                .map(entry -> id + " " + entry.getKey() + " " + entry.getValue().toString())
                .collect(Collectors.toList());
    }

    public List<SymbolTable<T>> getChildren() {
        return children;
    }

    public SymbolTable<T> getParent() {
        return parent;
    }

    public int getId() {
        return id;
    }
}
