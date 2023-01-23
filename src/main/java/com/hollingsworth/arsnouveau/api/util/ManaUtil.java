package com.hollingsworth.arsnouveau.api.util;

import com.hollingsworth.arsnouveau.api.event.ManaRegenCalcEvent;
import com.hollingsworth.arsnouveau.api.event.MaxManaCalcEvent;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.enchantment.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.concurrent.atomic.AtomicInteger;

public class ManaUtil {

    public static int getPlayerDiscounts(LivingEntity e) {
        AtomicInteger discounts = new AtomicInteger();
        CuriosUtil.getAllWornItems(e).ifPresent(items -> {
            for (int i = 0; i < items.getSlots(); i++) {
                ItemStack item = items.getStackInSlot(i);
                if (item.getItem() instanceof IManaEquipment discountItem )
                    discounts.addAndGet(discountItem.getManaDiscount(item));
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

        int max = ServerConfig.INIT_MAX_MANA.get();

        max += PerkUtil.perkValue(e, PerkAttributes.FLAT_MANA_BONUS.get());

        for(ItemStack i : e.getAllSlots()){
            max += (ServerConfig.MANA_BOOST_BONUS.get() * i.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.get()));
        }

        int tier = mana.getBookTier();
        int numGlyphs = mana.getGlyphBonus();
        max += numGlyphs * ServerConfig.GLYPH_MAX_BONUS.get();
        max += tier * ServerConfig.TIER_MAX_BONUS.get();

        max *= PerkUtil.perkValue(e, PerkAttributes.MAX_MANA_BONUS.get());
        MaxManaCalcEvent event = new MaxManaCalcEvent(e, max);
        MinecraftForge.EVENT_BUS.post(event);
        max = event.getMax();
        return max;
    }

    public static double getManaRegen(Player e) {
        IManaCap mana = CapabilityRegistry.getMana(e).orElse(null);

        if(mana == null) return 0;
        double regen = ServerConfig.INIT_MANA_REGEN.get();

        if (e.getAttribute(PerkAttributes.MANA_REGEN_BONUS.get()) != null)
            regen += e.getAttributeValue(PerkAttributes.MANA_REGEN_BONUS.get());

        for(ItemStack i : e.getAllSlots()){
            regen += ServerConfig.MANA_REGEN_ENCHANT_BONUS.get() * i.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.get());
        }
        int tier = mana.getBookTier();
        double numGlyphs = mana.getGlyphBonus();
        regen += numGlyphs * ServerConfig.GLYPH_REGEN_BONUS.get();
        regen += tier * ServerConfig.TIER_REGEN_BONUS.get();
        if (e.hasEffect(ModPotions.MANA_REGEN_EFFECT.get()))
            regen += ServerConfig.MANA_REGEN_POTION.get() * (1 + e.getEffect(ModPotions.MANA_REGEN_EFFECT.get()).getAmplifier());
        ManaRegenCalcEvent event = new ManaRegenCalcEvent(e, regen);
        MinecraftForge.EVENT_BUS.post(event);
        regen = event.getRegen();
        return regen;
    }

}
