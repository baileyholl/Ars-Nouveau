package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.*;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.block.tile.SpellSensorTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectInvisibility;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber(modid = ArsNouveau.MODID)
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
        if (e.spell.unsafeList().contains(EffectInvisibility.INSTANCE) && e.rayTraceResult instanceof BlockHitResult blockHitResult) {
            if (e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile) {
                ghostWeaveTile.setVisibility(true);
            }
        }
    }

    @SubscribeEvent
    public static void preSpellDamage(SpellDamageEvent.Pre e) {
        if (e.damageSource.is(DamageTypeTags.IS_FIRE) && e.caster.hasEffect(ModPotions.IMMOLATE_EFFECT)) {
            e.damage += 2 * (e.caster.getEffect(ModPotions.IMMOLATE_EFFECT).getAmplifier() + 1);
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
            //TODO: reimplement manaboost/regen attributes

//            if (itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT) > 0) {
//                ResourceLocation uuid = getEnchantBoostBySlot(event.getSlotType());
//                event.addModifier(PerkAttributes.MAX_MANA, new AttributeModifier(uuid, ServerConfig.MANA_BOOST_BONUS.get() * itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT), AttributeModifier.Operation.ADD_VALUE));
//            }
//            if (itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT) > 0) {
//                ResourceLocation uuid = getEnchantBoostBySlot(event.getSlotType());
//                event.addModifier(PerkAttributes.MANA_REGEN_BONUS, new AttributeModifier(uuid, ServerConfig.MANA_REGEN_ENCHANT_BONUS.get() * itemStack.getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT), AttributeModifier.Operation.ADD_VALUE));
//            }
        }

    }

    public static ResourceLocation getEnchantBoostBySlot(EquipmentSlot type) {
        return switch (type) {
            case CHEST -> ArsEvents.CHEST;
            case LEGS -> ArsEvents.LEGS;
            case FEET -> ArsEvents.FEET;
            case HEAD -> ArsEvents.HEAD;
            default -> ArsEvents.CHEST;
        };
    }

    static final ResourceLocation CHEST = ArsNouveau.prefix("chest_enchant");
    static final ResourceLocation LEGS = ArsNouveau.prefix("legs_enchant");
    static final ResourceLocation FEET = ArsNouveau.prefix("feet_enchant");
    static final ResourceLocation HEAD = ArsNouveau.prefix("head_enchant");
}
