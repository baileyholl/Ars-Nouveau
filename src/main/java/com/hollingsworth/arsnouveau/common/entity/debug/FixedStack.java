package com.hollingsworth.arsnouveau.common.entity.debug;

import java.util.Stack;

public class FixedStack<T> extends Stack<T> {
    private int maxSize;

    public FixedStack(int size) {
        super();
        this.maxSize = size;
    }

    @Override
    public T push(T object) {
        //If the stack is too big, remove elements until it's the right size.
        while (this.size() >= maxSize) {
            this.remove(0);
        }
        return super.push(object);
    }
}