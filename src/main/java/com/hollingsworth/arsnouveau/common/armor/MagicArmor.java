package com.hollingsworth.arsnouveau.common.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.perk.*;
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

import java.util.List;
import java.util.Map;

public abstract class MagicArmor extends ArmorItem implements IManaEquipment, IDyeable {

    public MagicArmor(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (world.isClientSide())
            return;
        RepairingPerk.attemptRepair(stack, player);
        ArmorPerkHolder perkProvider = new ArmorPerkHolder(stack);
        perkProvider.getPerkSet().getPerkMap().forEach((k, v)->{
            if(k instanceof TickablePerk tickablePerk) {
                tickablePerk.tick(stack, world, player, v);
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
        attributes.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
        if (this.slot == pEquipmentSlot) {
//            UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
//            attributes.put(PerkAttributes.MAX_MANA_BONUS.get(), new AttributeModifier(uuid, "max_mana_armor", this.getMaxManaBoost(stack), AttributeModifier.Operation.ADDITION));
//            attributes.put(PerkAttributes.MANA_REGEN_BONUS.get(), new AttributeModifier(uuid, "mana_regen_armor", this.getManaRegenBonus(stack), AttributeModifier.Operation.ADDITION));
            IPerkProvider<ItemStack> perkProvider = ArsNouveauAPI.getInstance().getPerkProvider(stack.getItem());
            if(perkProvider != null){
                IPerkHolder<ItemStack> perkHolder = perkProvider.getPerkHolder(stack);
                PerkSet perkSet = perkHolder.getPerkSet();
                for(Map.Entry<IPerk, Integer> entry : perkSet.getPerkMap().entrySet()){
                    attributes.putAll(entry.getKey().getModifiers(this.slot, stack, entry.getValue()));
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
            perkProvider.getPerkHolder(stack).appendPerkTooltip(tooltip, stack);
            if(perkProvider.getPerkHolder(stack) instanceof ArmorPerkHolder armorPerkHolder){
                tooltip.add(Component.translatable("ars_nouveau.tier", armorPerkHolder.getTier() + 1).withStyle(ChatFormatting.GOLD));
            }
        }
    }

    @Override
    public void onDye(ItemStack stack, DyeColor dyeColor) {
        ArmorPerkHolder perkHolder = new ArmorPerkHolder(stack);
        perkHolder.setColor(dyeColor.getName());
    }

    public static class ArmorPerkHolder extends StackPerkHolder {

        private String color;
        private int tier;

        public ArmorPerkHolder(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
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
        public int getMaxSlots() {
            if(tier == 0){
                return 2;
            }else if(tier == 1){
                return 3;
            }
            return 4 + (tier - 2);
        }
    }
}
