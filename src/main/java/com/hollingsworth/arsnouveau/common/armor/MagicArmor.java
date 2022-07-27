package com.hollingsworth.arsnouveau.common.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.api.mana.IManaEquipment;
import com.hollingsworth.arsnouveau.api.armor.PerkAttributes;
import com.hollingsworth.arsnouveau.common.armor.perks.IPerkHolder;
import com.hollingsworth.arsnouveau.common.armor.perks.TickablePerk;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.UUID;


public abstract class MagicArmor extends ArmorItem implements IManaEquipment, IPerkHolder {

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
        this.getPerks(stack).getPerks().forEach((k,v)->{
            if(k instanceof TickablePerk)
            {
                ((TickablePerk) k).tick(stack, world, player, v);
            }
        });
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> attributes = new ImmutableMultimap.Builder<>();
        attributes.putAll(super.getDefaultAttributeModifiers(pEquipmentSlot));
        if (this.slot == pEquipmentSlot) {
            UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
            attributes.put(PerkAttributes.CAP_BONUS.get(), new AttributeModifier(uuid, "max_mana_armor", this.getMaxManaBoost(stack), AttributeModifier.Operation.ADDITION));
            attributes.put(PerkAttributes.REGEN_BONUS.get(), new AttributeModifier(uuid, "mana_regen_armor", this.getManaRegenBonus(stack), AttributeModifier.Operation.ADDITION));
        }
        return attributes.build();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        IPerkHolder.appendPerkTooltip(tooltip, stack, getPerks(stack));
    }

}
