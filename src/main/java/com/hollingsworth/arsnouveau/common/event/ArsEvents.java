package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.*;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.PlayerCaster;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.block.tile.GhostWeaveTile;
import com.hollingsworth.arsnouveau.common.spell.effect.EffectInvisibility;
import com.hollingsworth.arsnouveau.setup.config.ServerConfig;
import com.hollingsworth.arsnouveau.setup.registry.EnchantmentRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class ArsEvents {

    @SubscribeEvent
    public static void costCalc(SpellCostCalcEvent e){
        if(e.context.getCasterTool().isEmpty()){
            return;
        }
        if(e.context.getCaster() instanceof PlayerCaster livingCaster && e.context.getCasterTool().is(ItemsRegistry.CASTER_TOME.get())){
            int maxMana = ManaUtil.getMaxMana(livingCaster.player);
            if (e.currentCost > maxMana) {
                e.currentCost = maxMana;
            } else {
                e.currentCost /= 2;
            }
        }
    }

    @SubscribeEvent
    public static void regenCalc(ManaRegenCalcEvent e) {
        if (e.getEntity() != null && e.getEntity().hasEffect(ModPotions.HEX_EFFECT.get())) {
            e.setRegen(e.getRegen() / 2.0);
        }
    }

    @SubscribeEvent
    public static void spellResolve(SpellResolveEvent.Post e) {
        if(e.spell.recipe.contains(EffectInvisibility.INSTANCE) && e.rayTraceResult instanceof BlockHitResult blockHitResult){
            if(e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile){
                ghostWeaveTile.setVisibility(true);
            }
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent e) {
        if(e.rayTraceResult instanceof BlockHitResult blockHitResult && e.world.getBlockEntity(blockHitResult.getBlockPos()) instanceof GhostWeaveTile ghostWeaveTile){
            ghostWeaveTile.setVisibility(false);
        }
    }

    @SubscribeEvent
    public static void adjustAttributeCaps(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getAttributes().getInstance(PerkAttributes.MAX_MANA.get()).setBaseValue(ServerConfig.INIT_MAX_MANA.get());
        event.getEntity().getAttributes().getInstance(PerkAttributes.MANA_REGEN_BONUS.get()).setBaseValue(ServerConfig.INIT_MANA_REGEN.get());
    }

    @SubscribeEvent
    public static void modifyItemAttributes(ItemAttributeModifierEvent event) {
        if (event.getItemStack().isEnchanted()){
            if ((event.getItemStack().getItem() instanceof ArmorItem armor) && !(event.getSlotType() == armor.getEquipmentSlot()))
                return;
            if ((event.getItemStack().getItem() instanceof ShieldItem) && !(event.getSlotType() == EquipmentSlot.OFFHAND))
                return;

            if (event.getItemStack().getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.get()) > 0){
                event.addModifier(PerkAttributes.MAX_MANA.get(), new AttributeModifier(UUID.fromString("f2239f81-4253-42a1-b596-234f42675484"),"max_mana_enchant", ServerConfig.MANA_BOOST_BONUS.get() * event.getItemStack().getEnchantmentLevel(EnchantmentRegistry.MANA_BOOST_ENCHANTMENT.get()), AttributeModifier.Operation.ADDITION));
            }
            if (event.getItemStack().getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.get()) > 0){
                event.addModifier(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(UUID.fromString("1024a8dd-a341-43c1-a6e4-0765032dc14c"),"mana_regen_enchant", ServerConfig.MANA_REGEN_ENCHANT_BONUS.get() * event.getItemStack().getEnchantmentLevel(EnchantmentRegistry.MANA_REGEN_ENCHANTMENT.get()), AttributeModifier.Operation.ADDITION));
            }
        }

    }

}
