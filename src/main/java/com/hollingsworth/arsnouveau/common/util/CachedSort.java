package com.hollingsworth.arsnouveau.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

public class CachedSort {
    record IntIntPair(int left, int right) implements Comparable<IntIntPair> {
        @Override
        public int compareTo(IntIntPair o) {
            return Integer.compare(this.left, o.left);
        }
    }

    public static <T> void sortByCachedIntKey(List<T> list, ToIntFunction<T> mappingFunction) {
        IntIntPair[] indices = new IntIntPair[list.size()];

        for (int i = 0; i < list.size(); i++) {
            indices[i] = new IntIntPair(mappingFunction.applyAsInt(list.get(i)), i);
        }

        Arrays.sort(indices);
        for (int i = 0; i < list.size(); i++) {
            var index = indices[i].right;
            while (index < i) {
                index = indices[index].right;
            }
            indices[i] = new IntIntPair(indices[i].left, index);
            Collections.swap(list, i, index);
        }
    }
}
