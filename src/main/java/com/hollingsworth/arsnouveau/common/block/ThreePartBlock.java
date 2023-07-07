package com.hollingsworth.arsnouveau.common.block;

import net.minecraft.util.StringRepresentable;

public enum ThreePartBlock implements StringRepresentable {
    HEAD("head"),
    FOOT("foot"),
    OTHER("other");
    private final String name;

    ThreePartBlock(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.name;
    }

    public String getSerializedName() {
        return this.name;
    }
}