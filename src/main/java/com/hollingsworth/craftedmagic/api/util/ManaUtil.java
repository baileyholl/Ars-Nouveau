package com.hollingsworth.craftedmagic.api.util;

import com.hollingsworth.craftedmagic.api.spell.AbstractSpellPart;
import com.hollingsworth.craftedmagic.armor.MagicArmor;
import com.hollingsworth.craftedmagic.enchantment.EnchantmentRegistry;
import com.hollingsworth.craftedmagic.api.spell.AugmentType;
import com.hollingsworth.craftedmagic.api.spell.AbstractEffect;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ManaUtil {

    public static int calculateCost(ArrayList<AbstractSpellPart> recipe) {
        int cost = 0;
        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart spell = recipe.get(i);
            if (spell instanceof AbstractEffect) {

                ArrayList<AugmentType> augments = new ArrayList<>();
                for (int j = i + 1; j < recipe.size(); j++) {
                    AbstractSpellPart next_spell = recipe.get(j);
                    if (next_spell instanceof AugmentType) {
                        augments.add((AugmentType) next_spell);
                    } else {
                        break;
                    }
                }

                cost += ((AbstractEffect) spell).getAdjustedManaCost(augments);
                System.out.println(cost);
            }
        }
        return cost;
    }

    public static int getMaxMana(PlayerEntity e){
        AtomicInteger max = new AtomicInteger(100);
        e.getEquipmentAndArmor().forEach(i->{
            if(i.getItem() instanceof MagicArmor){
                //max.addAndGet(((MagicArmor) i.getItem()).getMaxManaBonus());
                int newMax = max.get() +((MagicArmor) i.getItem()).getMaxManaBonus() + 25 * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, i);
                max.set(newMax);
            }
        });
        return max.get();
    }

    public static int getArmorRegen(PlayerEntity e) {
        int regen = 0;
        for(ItemStack i : e.getEquipmentAndArmor()){
            if(i.getItem() instanceof MagicArmor){
                MagicArmor armor = ((MagicArmor) i.getItem());
                regen += armor.getRegenBonus() + 5 * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, i);
            }
        }
        return regen;
    }
}
