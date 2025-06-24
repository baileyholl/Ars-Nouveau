package com.hollingsworth.arsnouveau.api.util;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;

import java.util.concurrent.atomic.AtomicInteger;

public class ManaUtil {

    public static int getPlayerDiscounts(LivingEntity e, Spell spell, ItemStack casterStack) {
        if (e == null) return 0;
        AtomicInteger discounts = new AtomicInteger();
        var items = CuriosUtil.getAllWornItems(e);
        if (items != null) {
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack item = items.getStackInSlot(i);
                if (item.getItem() instanceof IManaDiscountEquipment discountItem)
                    discounts.addAndGet(discountItem.getManaDiscount(item, spell));
            }
        }
        for (ItemStack armor : e.getArmorSlots()) {
            if (armor.getItem() instanceof IManaDiscountEquipment discountItem)
                discounts.addAndGet(discountItem.getManaDiscount(armor, spell));
        }
        if (casterStack.getItem() instanceof IManaDiscountEquipment discountEquipment) {
            discounts.addAndGet(discountEquipment.getManaDiscount(casterStack, spell));
        }
        return discounts.get();
    }

    public static double getCurrentMana(LivingEntity e) {
        IManaCap mana = CapabilityRegistry.getMana(e);
        if (mana == null)
            return 0;
        return mana.getCurrentMana();
    }

    public record Mana(int Max, float Reserve) {
        //Usable max mana
        public int getRealMax() {
            return (int) (Max * (1.0 - Reserve));
        }
    }

    // UUIDs for the configurable bonus on mana attributes, to include them in multiplier calculations.
    // Only updated if the value changes.
    static final ResourceLocation MAX_MANA_MODIFIER = ArsNouveau.prefix("max_mana_mod");
    static final ResourceLocation MANA_REGEN_MODIFIER = ArsNouveau.prefix("max_regen_mod");

    // Calculate Max Mana & Mana Reserve to keep track of the mana reserved by familiars & co.
    public static Mana calcMaxMana(Player e) {
        IManaCap mana = CapabilityRegistry.getMana(e);
        if (mana == null)
            return new Mana(0, 0f);

        double rawMax = 0;
        int tier = mana.getBookTier();
        int numGlyphs = mana.getGlyphBonus();

        rawMax += ServerConfig.INIT_MAX_MANA.get();
        rawMax += numGlyphs * ServerConfig.GLYPH_MAX_BONUS.get();
        rawMax += tier * ServerConfig.TIER_MAX_BONUS.get();

        var manaAttribute = e.getAttribute(PerkAttributes.MAX_MANA);
        if (manaAttribute != null) {
            var manaCache = manaAttribute.getModifier(MAX_MANA_MODIFIER);
            if (manaCache == null || manaCache.amount() != rawMax) {
                if (manaCache != null) manaAttribute.removeModifier(manaCache);
                manaAttribute.addTransientModifier(new AttributeModifier(MAX_MANA_MODIFIER, rawMax, AttributeModifier.Operation.ADD_VALUE));
            }
            rawMax = manaAttribute.getValue();
        }

        int max = (int) rawMax;

        MaxManaCalcEvent event = new MaxManaCalcEvent(e, max);
        NeoForge.EVENT_BUS.post(event);
        max = event.getMax();
        float reserve = event.getReserve();
        return new Mana(max, reserve);
    }


    //Returns the max mana of the player, not including the mana reserved by familiars & co.
    public static int getMaxMana(Player e) {
        return calcMaxMana(e).getRealMax();
    }

    public static double getManaRegen(Player e) {
        IManaCap mana = CapabilityRegistry.getMana(e);

        if (mana == null) return 0;
        double regen = 0;

        int tier = mana.getBookTier();
        double numGlyphs = mana.getGlyphBonus();
        regen += numGlyphs * ServerConfig.GLYPH_REGEN_BONUS.get();
        regen += tier * ServerConfig.TIER_REGEN_BONUS.get();
        regen += ServerConfig.INIT_MANA_REGEN.get();

        var manaAttribute = e.getAttribute(PerkAttributes.MANA_REGEN_BONUS);
        if (manaAttribute != null) {
            var manaCache = manaAttribute.getModifier(MANA_REGEN_MODIFIER);
            if (manaCache == null || manaCache.amount() != regen) {
                if (manaCache != null) manaAttribute.removeModifier(manaCache);
                manaAttribute.addTransientModifier(new AttributeModifier(MANA_REGEN_MODIFIER, regen, AttributeModifier.Operation.ADD_VALUE));
            }
            regen = manaAttribute.getValue();
        }

        ManaRegenCalcEvent event = new ManaRegenCalcEvent(e, regen);
        NeoForge.EVENT_BUS.post(event);
        regen = event.getRegen();
        return regen;
    }

}
