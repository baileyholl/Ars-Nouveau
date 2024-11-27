package com.hollingsworth.arsnouveau.api.perk;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Represents the actions or benefits a perk may provide.
 */
public interface IPerk {

    /**
     * @param modifiers          The current modifiers, remember {@link ItemAttributeModifiers#withModifierAdded} creates a new instance,
     *                           so you must assign it back to the original or chain the call in the return
     * @param stack              The stack with the perk
     * @param slotValue          The value of the slot the perk is in
     * @param equipmentSlotGroup The slot where the attribute will be applied, seen in tooltips
     * @return Modified modifiers
     */
    default @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue, EquipmentSlotGroup equipmentSlotGroup) {
        return modifiers;
    }

    /**
     * Use {@link IPerk#applyAttributeModifiers} instead
     */
    @Deprecated(forRemoval = true)
    default Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return new ImmutableMultimap.Builder<Attribute, AttributeModifier>().build();
    }

    @Deprecated(forRemoval = true)
    default ImmutableMultimap.Builder<Attribute, AttributeModifier> attributeBuilder() {
        return new ImmutableMultimap.Builder<>();
    }


    default PerkSlot minimumSlot() {
        return PerkSlot.ONE;
    }

    /**
     * @param slot   The slot the perk is being applied to
     * @param stack  The stack the perk is being applied to
     * @param player The player applying the perk
     * @return Whether the perk is valid for the given slot, defaults to check slot level
     */
    default boolean validForSlot(PerkSlot slot, ItemStack stack, Player player) {
        if (this.minimumSlot().value() > slot.value()) {
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.invalid_for_slot", this.minimumSlot().value()));
            return false;
        }
        return true;
    }

    /**
     * Return the initial perk data when this Perk is added to an item for the first time.
     */
    default @Nullable CompoundTag getInitTag(){
        return null;
    }

    ResourceLocation getRegistryName();

    default String getName() {
        return Component.translatable(getRegistryName().getNamespace() + ".thread_of", Component.translatable("item." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()).getString()).getString();
    }

    default Component getPerkName(){
        return Component.translatable("item." + getRegistryName().getNamespace() + "." + getRegistryName().getPath());
    }

    default String getLangName() {
        return "";
    }

    default String getLangDescription() {
        return "";
    }

    default String getDescriptionKey() {
        return getRegistryName().getNamespace() + ".perk_desc." + getRegistryName().getPath();
    }
}
