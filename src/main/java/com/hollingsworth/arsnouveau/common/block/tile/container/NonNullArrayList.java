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

import net.minecraft.core.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NonNullArrayList<E> extends NonNullList<E> {

    //region Fields
    protected final ArrayList<E> delegate;
    //endregion Fields

    //region Initialization
    protected NonNullArrayList() {
        this(new ArrayList<>(), null);
    }

    protected NonNullArrayList(ArrayList<E> delegateIn, @Nullable E listType) {
        super(delegateIn, listType);
        this.delegate = delegateIn;
    }
    //endregion Initialization

    //region Overrides

    //region Static Methods
    public static <E> NonNullArrayList<E> create() {
        return new NonNullArrayList<>();
    }

    public static <E> NonNullArrayList<E> withSize(int size, E fill) {
        return new NonNullArrayList<>(new ArrayList<>(Collections.nCopies(size, fill)), fill);
    }
    //endregion Overrides

    @SafeVarargs
    public static <E> NonNullList<E> from(E defaultElementIn, E... elements) {

        return new NonNullArrayList<>(Stream.of(elements).collect(Collectors.toCollection(ArrayList::new)),
                defaultElementIn);
    }

    /**
     * Warning! No null check here, you need to ensure that on your end!
     */
    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return this.delegate.addAll(index, c);
    }

    /**
     * Warning! No null check here, you need to ensure that on your end!
     */
    @Override
    public boolean addAll(@Nonnull Collection<? extends E> c) {
        return this.delegate.addAll(c);
    }
    //endregion Static Methods
}
