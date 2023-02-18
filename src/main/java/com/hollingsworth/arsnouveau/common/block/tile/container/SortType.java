/*
 * MIT License
 *
 * Copyright 2020 klikli-dev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.hollingsworth.arsnouveau.common.block.tile.container;


import net.minecraft.util.StringRepresentable;

import java.util.HashMap;
import java.util.Map;

public enum SortType implements StringRepresentable {
    AMOUNT(0),
    NAME(1),
    MOD(2);

    //region Fields
    private static final Map<Integer, SortType> lookup = new HashMap<Integer, SortType>();

    static {
        for (SortType sortType : SortType.values()) {
            lookup.put(sortType.getValue(), sortType);
        }
    }

    private final int value;

    //endregion Fields
    //region Initialization
    SortType(int value) {
        this.value = value;
    }
    //endregion Initialization

    //region Static Methods
    public static SortType get(int value) {
        return lookup.get(value);
    }
    //endregion Getter / Setter

    //region Getter / Setter
    public int getValue() {
        return this.value;
    }
    //endregion Overrides

    //region Overrides
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
    //endregion Static Methods

    //region Methods
    public SortType next() {
        return values()[(this.ordinal() + 1) % SortType.values().length];
    }
    //endregion Methods
}
