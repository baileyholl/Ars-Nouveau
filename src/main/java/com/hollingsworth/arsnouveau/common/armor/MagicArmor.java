package com.hollingsworth.arsnouveau.common.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.mana.ManaAttributes;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

import net.minecraft.world.item.Item.Properties;

public abstract class MagicArmor extends ArmorItem implements IManaEquipment {

    public MagicArmor(ArmorMaterial materialIn, EquipmentSlot slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (world.isClientSide() || world.getGameTime() % 200 != 0 || stack.getDamageValue() == 0)
            return;

        CapabilityRegistry.getMana(player).ifPresent(mana -> {
            if (mana.getCurrentMana() > 20) {
                mana.removeMana(20);
                stack.setDamageValue(stack.getDamageValue() - 1);
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
        attributes.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
        if (this.slot == pEquipmentSlot) {
            UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
            attributes.put(ManaAttributes.MAX_MANA.get(), new AttributeModifier(uuid, "max_mana_armor", this.getMaxManaBoost(stack), AttributeModifier.Operation.ADDITION));
            attributes.put(ManaAttributes.MANA_REGEN.get(), new AttributeModifier(uuid, "mana_regen_armor", this.getManaRegenBonus(stack), AttributeModifier.Operation.ADDITION));
        }
        return attributes.build();
    }

}
