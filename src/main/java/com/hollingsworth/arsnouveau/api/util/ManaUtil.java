package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.mana.IManaDiscountEquipment;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.atomic.AtomicInteger;

public class ManaUtil {

    public static int getPlayerDiscounts(LivingEntity e, Spell spell, ItemStack casterStack) {
        if (e == null) return 0;
        AtomicInteger discounts = new AtomicInteger();
        CuriosUtil.getAllWornItems(e).ifPresent(items -> {
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack item = items.getStackInSlot(i);
                if (item.getItem() instanceof IManaDiscountEquipment discountItem)
                    discounts.addAndGet(discountItem.getManaDiscount(item, spell));
            }
        });
        for (ItemStack armor : e.getArmorSlots()){
            if (armor.getItem() instanceof IManaDiscountEquipment discountItem)
                discounts.addAndGet(discountItem.getManaDiscount(armor, spell));
        }
        if(casterStack.getItem() instanceof IManaDiscountEquipment discountEquipment){
            discounts.addAndGet(discountEquipment.getManaDiscount(casterStack, spell));
        }
        return discounts.get();
    }

    public static double getCurrentMana(LivingEntity e) {
        IManaCap mana = CapabilityRegistry.getMana(e).orElse(null);
        if (mana == null)
            return 0;
        return mana.getCurrentMana();
    }

    public record Mana(int Max, float Reserve){
        //Usable max mana
        public int getRealMax(){
            return (int) (Max  * (1.0 - Reserve));
        }
    }

    // Calculate Max Mana & Mana Reserve to keep track of the mana reserved by familiars & co.
    public static Mana calcMaxMana(Player e) {
        IManaCap mana = CapabilityRegistry.getMana(e).orElse(null);
        if (mana == null)
            return new Mana(0, 0f);

        double rawMax = PerkUtil.perkValue(e, PerkAttributes.MAX_MANA.get());

        int tier = mana.getBookTier();
        int numGlyphs = mana.getGlyphBonus();
        rawMax += numGlyphs * ServerConfig.GLYPH_MAX_BONUS.get();
        rawMax += tier * ServerConfig.TIER_MAX_BONUS.get();

        int max = (int) rawMax;

        MaxManaCalcEvent event = new MaxManaCalcEvent(e, max);
        MinecraftForge.EVENT_BUS.post(event);
        max = event.getMax();
        float reserve = event.getReserve();
        return new Mana(max, reserve);
    }


    //Returns the max mana of the player, not including the mana reserved by familiars & co.
    public static int getMaxMana(Player e) {
        return calcMaxMana(e).getRealMax();
    }

    public static double getManaRegen(Player e) {
        IManaCap mana = CapabilityRegistry.getMana(e).orElse(null);

        if(mana == null) return 0;
        double regen = 0;

        if (e.getAttribute(PerkAttributes.MANA_REGEN_BONUS.get()) != null)
            regen += e.getAttributeValue(PerkAttributes.MANA_REGEN_BONUS.get());

        int tier = mana.getBookTier();
        double numGlyphs = mana.getGlyphBonus();
        regen += numGlyphs * ServerConfig.GLYPH_REGEN_BONUS.get();
        regen += tier * ServerConfig.TIER_REGEN_BONUS.get();

        ManaRegenCalcEvent event = new ManaRegenCalcEvent(e, regen);
        MinecraftForge.EVENT_BUS.post(event);
        regen = event.getRegen();
        return regen;
    }

}
