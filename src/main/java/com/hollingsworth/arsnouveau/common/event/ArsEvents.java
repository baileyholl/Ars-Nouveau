package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.*;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SpellSensorTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectInvisibility;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void costCalc(SpellCostCalcEvent e) {
        if (e.context.getCasterTool().isEmpty()) {
            return;
        }
        if (e.context.getCaster() instanceof PlayerCaster livingCaster && e.context.getCasterTool().is(ItemsRegistry.CASTER_TOME.get())) {
            int maxMana = ManaUtil.getMaxMana(livingCaster.player);
            if (e.currentCost > maxMana) {
                e.currentCost = maxMana;
            } else {
                e.currentCost /= 2;
            }
        }
    }

    @SubscribeEvent
    public static void castEvent(SpellCastEvent castEvent) {
        SpellSensorTile.onSpellCast(castEvent);
    }


    @SubscribeEvent
    public static void regenCalc(ManaRegenCalcEvent e) {

        /* Replaced by negative multiplier on AttributeModifier
        if (e.getEntity() != null && e.getEntity().hasEffect(ModPotions.HEX_EFFECT.get())) {
            e.setRegen(e.getRegen() / 2.0);
        }
         */
    }

    @SubscribeEvent
    public static void spellResolve(SpellResolveEvent.Post e) {
        SpellSensorTile.onSpellResolve(e);
        if (e.spell.recipe.contains(EffectInvisibility.INSTANCE) && e.rayTraceResult instanceof BlockHitResult blockHitResult) {
            if (e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile) {
                ghostWeaveTile.setVisibility(true);
            }
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent e) {
        if (e.rayTraceResult instanceof BlockHitResult blockHitResult && e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile) {
            ghostWeaveTile.setVisibility(false);
        }
    }

    @SubscribeEvent
    public static void modifyItemAttributes(ItemAttributeModifierEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.isEnchanted()) {
            if (itemStack.getItem() instanceof ArmorItem armor) {
                if (!(event.getSlotType() == armor.getEquipmentSlot())) {
                    return;
                }
            } else if (event.getSlotType() != EquipmentSlot.MAINHAND && event.getSlotType() != EquipmentSlot.OFFHAND) {
                return;
            } else if (itemStack.getItem() instanceof ShieldItem && !(event.getSlotType() == EquipmentSlot.OFFHAND))
                return;

            if (itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.get()) > 0) {
                UUID uuid = getEnchantBoostBySlot(event.getSlotType());
                event.addModifier(PerkAttributes.MAX_MANA.get(), new AttributeModifier(uuid, "max_mana_enchant", ServerConfig.MANA_BOOST_BONUS.get() * itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.get()), AttributeModifier.Operation.ADDITION));
            }
            if (itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.get()) > 0) {
                UUID uuid = getEnchantBoostBySlot(event.getSlotType());
                event.addModifier(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(uuid, "mana_regen_enchant", ServerConfig.MANA_REGEN_ENCHANT_BONUS.get() * itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.get()), AttributeModifier.Operation.ADDITION));
            }
        }

    }

    public static UUID getEnchantBoostBySlot(EquipmentSlot type) {
        return switch (type) {
            case CHEST -> UUID.fromString("fe9f03b8-b958-450c-a498-81b7ba72118b");
            case LEGS -> UUID.fromString("052583f6-12ec-427a-aae2-82d79128bbab");
            case FEET -> UUID.fromString("7ea8f56f-f865-4ac1-bc20-ffd5c8300464");
            case HEAD -> UUID.fromString("79a1b1cd-3aaa-4913-8991-5c8540632f6b");
            default -> UUID.fromString("f2239f81-4253-42a1-b596-234f42675484");
        };
    }

}
