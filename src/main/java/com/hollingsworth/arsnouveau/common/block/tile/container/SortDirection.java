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

public enum SortDirection implements StringRepresentable {
    DOWN(0),
    UP(1);

    //region Fields
    private static final Map<Integer, SortDirection> lookup = new HashMap<Integer, SortDirection>();

    static {
        for (SortDirection sortDirection : SortDirection.values()) {
            lookup.put(sortDirection.getValue(), sortDirection);
        }
    }

    private final int value;

    //endregion Fields
    //region Initialization
    SortDirection(int value) {
        this.value = value;
    }
    //endregion Initialization

    //region Static Methods
    public static SortDirection get(int value) {
        return lookup.get(value);
    }

    //region Getter / Setter
    public int getValue() {
        return this.value;
    }
    //endregion Getter / Setter

    public boolean isDown() {
        return this == DOWN;
    }
    //endregion Overrides

    //region Overrides
    @Override
    public String getSerializedName() {
        return this.name().toLowerCase();
    }
    //endregion Static Methods

    //region Methods
    public SortDirection next() {
        return values()[(this.ordinal() + 1) % SortDirection.values().length];
    }
    //endregion Methods
}
