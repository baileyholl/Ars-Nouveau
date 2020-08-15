package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class ManaUtil {

    public static int getRecipeCost(ArrayList<AbstractSpellPart> recipe) {
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

    public static int getPlayerDiscounts(PlayerEntity e){
        AtomicInteger discounts = new AtomicInteger();
        CuriosUtil.getAllWornItems(e).ifPresent(items ->{

            for(int i = 0; i < items.getSlots(); i++){
                Item item = items.getStackInSlot(i).getItem();
                if(item instanceof IManaEquipment)
                    discounts.addAndGet(((IManaEquipment) item).getManaDiscount());
            }
        });
        return discounts.get();
    }

    public static int getCastingCost(ArrayList<AbstractSpellPart> recipe, PlayerEntity e){
        int cost = getRecipeCost(recipe) - getPlayerDiscounts(e);
        return Math.max(cost, 0);
    }


    public static int getMaxMana(PlayerEntity e){
        AtomicInteger max = new AtomicInteger(100);
        e.getEquipmentAndArmor().forEach(i->{
            if(i.getItem() instanceof IManaEquipment){
                //max.addAndGet(((MagicArmor) i.getItem()).getMaxManaBonus());
                max.addAndGet(((MagicArmor) i.getItem()).getMaxManaBoost());

            }
            max.addAndGet( 25 * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT, i));
        });

        CuriosUtil.getAllWornItems(e).ifPresent(items ->{

            for(int i = 0; i < items.getSlots(); i++){
                Item item = items.getStackInSlot(i).getItem();
                if(item instanceof IManaEquipment)
                    max.addAndGet(((IManaEquipment) item).getMaxManaBoost());
            }
        });
        return max.get();
    }

    public static int getArmorRegen(PlayerEntity e) {
        AtomicInteger regen = new AtomicInteger();
        for(ItemStack i : e.getEquipmentAndArmor()){
            if(i.getItem() instanceof IManaEquipment){
                MagicArmor armor = ((MagicArmor) i.getItem());
                regen.addAndGet(armor.getManaRegenBonus());
            }
            regen.addAndGet(2 * EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT, i));
        }
        CuriosUtil.getAllWornItems(e).ifPresent(items ->{
            int newregen = regen.get();
            for(int i = 0; i < items.getSlots(); i++){
                Item item = items.getStackInSlot(i).getItem();
                if(item instanceof IManaEquipment)
                    newregen += ((IManaEquipment) item).getManaRegenBonus();
            }
            regen.set(newregen);

        });
        return regen.get();
    }
}
