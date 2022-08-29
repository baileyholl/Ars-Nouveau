package com.hollingsworth.arsnouveau.common.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.*;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.IDyeable;
import com.hollingsworth.arsnouveau.common.perk.RepairingPerk;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public abstract class MagicArmor extends ArmorItem implements IManaEquipment, IDyeable {

    public MagicArmor(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (world.isClientSide())
            return;
        RepairingPerk.attemptRepair(stack, player);
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
        if(perkHolder == null)
            return;
        for(PerkInstance instance : perkHolder.getPerkInstances()) {
            if(instance.getPerk() instanceof TickablePerk tickablePerk){
                tickablePerk.tick(stack, world, player, instance.getSlot().value);
            }
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
        attributes.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
        if (this.slot == pEquipmentSlot) {
            IPerkProvider<ItemStack> perkProvider = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());
            if(perkProvider != null){
                IPerkHolder<ItemStack> perkHolder = perkProvider.getPerkHolder(stack);
                for(PerkInstance perkInstance : perkHolder.getPerkInstances()){
                    IPerk perk = perkInstance.getPerk();
                    attributes.putAll(perk.getModifiers(this.slot, stack, perkInstance.getSlot().value));
                }
            }
        }
        return attributes.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        IPerkProvider<ItemStack> perkProvider = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());
        if (perkProvider != null) {
            if(perkProvider.getPerkHolder(stack) instanceof ArmorPerkHolder armorPerkHolder){
                tooltip.add(Component.translatable("ars_nouveau.tier", armorPerkHolder.getTier() + 1).withStyle(ChatFormatting.GOLD));
            }
            perkProvider.getPerkHolder(stack).appendPerkTooltip(tooltip, stack);
        }
    }

    @Override
    public void onDye(ItemStack stack, DyeColor dyeColor) {
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(stack);
        if(perkHolder instanceof ArmorPerkHolder armorPerkHolder){
            armorPerkHolder.setColor(dyeColor.getName());
        }
    }

    public static class ArmorPerkHolder extends StackPerkHolder {

        private String color;
        private int tier;
        private List<List<PerkSlot>> slotsForTier;

        public ArmorPerkHolder(ItemStack stack, List<List<PerkSlot>> slotsForTier) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            this.slotsForTier = slotsForTier;
            if(tag == null)
                return;
            color = tag.getString("color");
            tier = tag.getInt("tier");
        }

        public String getColor() {
            return color == null ? DyeColor.PURPLE.getName() : color;
        }

        public void setColor(String color) {
            this.color = color;
            writeItem();
        }

        public int getTier(){
            return this.tier;
        }

        public void setTier(int tier){
            this.tier = tier;
            writeItem();
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            super.writeToNBT(tag);
            if(color != null)
                tag.putString("color", color);
            tag.putInt("tier", tier);
        }

        @Override
        public List<PerkSlot> getSlotsForTier() {
            List<PerkSlot> slots = new ArrayList<>(slotsForTier.get(tier));
            slots.sort(Comparator.comparingInt((a) -> -a.value));
            return slots;
        }
    }
}
