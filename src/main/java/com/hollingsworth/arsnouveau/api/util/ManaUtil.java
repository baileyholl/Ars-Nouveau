package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.block.tile.ManaJarTile;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.items.SpellBook;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ManaUtil {

    public static int getRecipeCost(List<AbstractSpellPart> recipe) {
        int cost = 0;
        for (int i = 0; i < recipe.size(); i++) {
            AbstractSpellPart spell = recipe.get(i);
            if (!(spell instanceof AbstractAugment)) {

                List<AbstractAugment> augments = SpellRecipeUtil.getAugments(recipe, i, null);
                cost += spell.getAdjustedManaCost(augments);
            }
        }
//        System.out.println("Cost: " + cost);
        return cost;
    }

    public static int getPlayerDiscounts(LivingEntity e){
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

    public static int getCastingCost(List<AbstractSpellPart> recipe, LivingEntity e){
        int cost = getRecipeCost(recipe) - getPlayerDiscounts(e);
        return Math.max(cost, 0);
    }


    public static int getMaxMana(PlayerEntity e){
        AtomicInteger max = new AtomicInteger(100);
        e.getEquipmentAndArmor().forEach(i->{
            if(i.getItem() instanceof IManaEquipment){
                //max.addAndGet(((MagicArmor) i.getItem()).getMaxManaBonus());
                max.addAndGet(((IManaEquipment) i.getItem()).getMaxManaBoost());

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
        if(e.getHeldItem(Hand.MAIN_HAND).getItem() instanceof SpellBook && e.getHeldItem(Hand.MAIN_HAND).hasTag()){
            ArrayList<AbstractSpellPart> list = SpellBook.getUnlockedSpells(e.getHeldItem(Hand.MAIN_HAND).getTag());
            int numUnlocks = list.stream().filter(p -> p instanceof AbstractAugment || p instanceof AbstractEffect).collect(Collectors.toList()).size() - 3;
            max.addAndGet(numUnlocks * 15);
            max.addAndGet(((SpellBook)e.getHeldItem(Hand.MAIN_HAND).getItem()).tier.ordinal() * 50);
        }
        return max.get();
    }

    public static int getArmorRegen(PlayerEntity e) {
        AtomicInteger regen = new AtomicInteger();
        for(ItemStack i : e.getEquipmentAndArmor()){
            if(i.getItem() instanceof MagicArmor){
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
        if(e.getHeldItem(Hand.MAIN_HAND).getItem() instanceof SpellBook && e.getHeldItem(Hand.MAIN_HAND).hasTag()){
            ArrayList<AbstractSpellPart> list = SpellBook.getUnlockedSpells(e.getHeldItem(Hand.MAIN_HAND).getTag());
            int numUnlocks = list.stream().filter(p -> p instanceof AbstractAugment || p instanceof AbstractEffect).collect(Collectors.toList()).size() - 3;

            regen.addAndGet(numUnlocks/3);
            regen.addAndGet(((SpellBook)e.getHeldItem(Hand.MAIN_HAND).getItem()).tier.ordinal());
        }
        return regen.get();
    }

    /**
     * Searches for nearby mana jars that have enough mana.
     * Returns the position where the mana was taken, or null if none were found.
     */
    @Nullable
    public static BlockPos takeManaNearby(BlockPos pos, World world, int range, int mana){
        final BlockPos[] pos1 = {null};
        BlockPos.getAllInBox(pos.add(range, range, range), pos.add(-range, -range, -range)).forEach(blockPos -> {
            blockPos = blockPos.toImmutable();
            if(pos1[0] == null && world.getTileEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(blockPos)).getCurrentMana() >= mana) {
                ((ManaJarTile) world.getTileEntity(blockPos)).removeMana(mana);
                pos1[0] = blockPos;
            }
        });
        return pos1[0];
    }
}
