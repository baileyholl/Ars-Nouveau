package com.hollingsworth.arsnouveau.api.util;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

/**
 * Gets the next randomly weighted T in a map of Integers where the integer denotes their occurrence in the set.
 *
 * @param <T>
 */
public class DropDistribution<T> {
    private int totalNum;
    private final Map<T, Integer> map;
    public static final Random rand = new Random();


    public DropDistribution(Map<T, Integer> map) {
        for (Integer val : map.values()) {
            totalNum += val;
        }
        this.map = map;
    }

    @Nullable
    public T nextDrop() {
        if (totalNum <= 0)
            return null;
        int gen = DropDistribution.rand.nextInt(totalNum) + 1;
        int counter = 0;
        for (Map.Entry<T, Integer> entry : this.map.entrySet()) {
            counter += entry.getValue();
            if (gen <= counter) {
                return entry.getKey();
            }
        }
        return null;
    }
}
