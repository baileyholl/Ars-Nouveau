package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.Config;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.concurrent.atomic.AtomicInteger;

public class ManaUtil {


    public static int getPlayerDiscounts(LivingEntity e) {
        AtomicInteger discounts = new AtomicInteger();
        CuriosUtil.getAllWornItems(e).ifPresent(items -> {

            for (int i = 0; i < items.getSlots(); i++) {
                Item item = items.getStackInSlot(i).getItem();
                if (item instanceof IManaEquipment)
                    discounts.addAndGet(((IManaEquipment) item).getManaDiscount(items.getStackInSlot(i)));
            }
        });
        return discounts.get();
    }

    public static double getCurrentMana(LivingEntity e) {
        IManaCap mana = CapabilityRegistry.getMana(e).orElse(null);
        if (mana == null)
            return 0;
        return mana.getCurrentMana();
    }

    public static int getMaxMana(Player e) {
        IManaCap mana = CapabilityRegistry.getMana(e).orElse(null);
        if (mana == null)
            return 0;
        int max = Config.INIT_MAX_MANA.get();
        for (ItemStack i : e.getAllSlots()) {
            if (i.getItem() instanceof IManaEquipment) {
                max += (((IManaEquipment) i.getItem()).getMaxManaBoost(i));
            }
            max += (Config.MANA_BOOST_BONUS.get() * i.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.get()));
        }

        IItemHandlerModifiable items = CuriosUtil.getAllWornItems(e).orElse(null);
        if (items != null) {
            for (int i = 0; i < items.getSlots(); i++) {
                Item item = items.getStackInSlot(i).getItem();
                if (item instanceof IManaEquipment iMana)
                    max += iMana.getMaxManaBoost(items.getStackInSlot(i));
            }
        }

        int tier = mana.getBookTier();
        int numGlyphs = mana.getGlyphBonus();
        max += numGlyphs * Config.GLYPH_MAX_BONUS.get();
        max += tier * Config.TIER_MAX_BONUS.get();

        MaxManaCalcEvent event = new MaxManaCalcEvent(e, max);
        MinecraftForge.EVENT_BUS.post(event);
        max = event.getMax();
        return max;
    }

    public static double getManaRegen(Player e) {
        IManaCap mana = CapabilityRegistry.getMana(e).orElse(null);
        if (mana == null)
            return 0;
        double regen = Config.INIT_MANA_REGEN.get();
        for (ItemStack i : e.getAllSlots()) {
            if (i.getItem() instanceof MagicArmor armor) {
                regen += armor.getManaRegenBonus(i);
            }
            regen += Config.MANA_REGEN_ENCHANT_BONUS.get() * i.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.get());
        }
        IItemHandlerModifiable items = CuriosUtil.getAllWornItems(e).orElse(null);
        if (items != null) {
            for (int i = 0; i < items.getSlots(); i++) {
                Item item = items.getStackInSlot(i).getItem();
                if (item instanceof IManaEquipment)
                    regen += ((IManaEquipment) item).getManaRegenBonus(items.getStackInSlot(i));
            }
        }

        int tier = mana.getBookTier();
        double numGlyphs = mana.getGlyphBonus();
        regen += numGlyphs * Config.GLYPH_REGEN_BONUS.get();
        regen += tier;
        if (e.hasEffect(ModPotions.MANA_REGEN_EFFECT.get()))
            regen += Config.MANA_REGEN_POTION.get() * (1 + e.getEffect(ModPotions.MANA_REGEN_EFFECT.get()).getAmplifier());
        ManaRegenCalcEvent event = new ManaRegenCalcEvent(e, regen);
        MinecraftForge.EVENT_BUS.post(event);
        regen = event.getRegen();
        return regen;
    }
}
