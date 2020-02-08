package com.hollingsworth.craftedmagic.api.util;

import com.hollingsworth.craftedmagic.api.AbstractSpellPart;
import com.hollingsworth.craftedmagic.spell.augment.AugmentType;
import com.hollingsworth.craftedmagic.spell.effect.EffectType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.ArrayList;

public class ManaUtil {

    public static int calculateCost(ArrayList<AbstractSpellPart> recipe) {
        int cost = 0;
        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart spell = recipe.get(i);
            if (spell instanceof EffectType) {

                ArrayList<AugmentType> augments = new ArrayList<>();
                for (int j = i + 1; j < recipe.size(); j++) {
                    AbstractSpellPart next_spell = recipe.get(j);
                    if (next_spell instanceof AugmentType) {
                        augments.add((AugmentType) next_spell);
                    } else {
                        break;
                    }
                }

                cost += ((EffectType) spell).getAdjustedManaCost(augments);
                System.out.println(cost);
            }
        }
        System.out.println(cost);
        return cost;
    }
}
