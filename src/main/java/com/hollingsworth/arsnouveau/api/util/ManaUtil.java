package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.armor.MagicArmor;
import com.hollingsworth.arsnouveau.enchantment.EnchantmentRegistry;
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
            if (!(spell instanceof AbstractAugment)) {

                ArrayList<AbstractAugment> augments = SpellRecipeUtil.getAugments(recipe, i, null);
                cost += spell.getAdjustedManaCost(augments);
            }
        }
//        System.out.println("Cost: " + cost);
        return cost;
    }

    public static int getMaxMana(PlayerEntity e){
        AtomicInteger max = new AtomicInteger(100);
        e.getEquipmentAndArmor().forEach(i->{
            if(i.getItem() instanceof IManaEquipment){
                //max.addAndGet(((MagicArmor) i.getItem()).getMaxManaBonus());
                int newMax = max.get() +((MagicArmor) i.getItem()).getMaxManaBoost() + 25 * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, i);
                max.set(newMax);
            }
        });
        return max.get();
    }

    public static int getArmorRegen(PlayerEntity e) {
        int regen = 0;
        for(ItemStack i : e.getEquipmentAndArmor()){
            if(i.getItem() instanceof IManaEquipment){
                MagicArmor armor = ((MagicArmor) i.getItem());
                regen += armor.getManaRegenBonus() + 2 * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, i);
            }
        }
        return regen;
    }
}
