package com.hollingsworth.arsnouveau.api.perk;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


/**
 * Represents the actions or benefits a perk may provide.
 */
public interface IPerk {

    default Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue){
        return new ImmutableMultimap.Builder<Attribute, AttributeModifier>().build();
    }

    default ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder(){
        return new ImmutableMultimap.Builder<>();
    }


    default PerkSlot minimumSlot(){
        return PerkSlot.ONE;
    }

    /**
     * @param slot   The slot the perk is being applied to
     * @param stack  The stack the perk is being applied to
     * @param player The player applying the perk
     * @return Whether the perk is valid for the given slot, defaults to check slot level
     */
    default boolean validForSlot(PerkSlot slot, ItemStack stack, Player player){
        if(this.minimumSlot().value > slot.value){
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.invalid_for_slot", this.minimumSlot().value));
            return false;
        }
        return true;
    }

    ResourceLocation getRegistryName();

    default String getName() {
        return Component.translatable(getRegistryName().getNamespace() + ".thread_of", Component.translatable("item." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()).getString()).getString();
    }

    default String getLangName(){
        return "";
    }

    default String getLangDescription(){
        return "";
    }

    default String getDescriptionKey() {
        return getRegistryName().getNamespace() + ".perk_desc." + getRegistryName().getPath();
    }
}
