package com.hollingsworth.arsnouveau.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntFunction;

public class CachedSort {
    public static <T> void sortByCachedIntKey(List<T> list, ToIntFunction<T> mappingFunction) {
        long[] indices = new long[list.size()];

        for (int i = 0; i < list.size(); i++) {
            indices[i] = ((long) mappingFunction.applyAsInt(list.get(i)) << 32L) + i;
        }

        Arrays.sort(indices);
        for (int i = 0; i < list.size(); i++) {
            int index = (int) (indices[i] & 0xFFFF_FFFFL);
            while (index < i) {
                index = (int) (indices[index] & 0xFFFF_FFFFL);
            }
            indices[i] = (indices[i] << 32L) + (index & 0xFFFF_FFFFL);
            Collections.swap(list, i, index);
        }
    }
}