package com.hollingsworth.arsnouveau.common.perk;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForgeMod;

public class DepthsPerk extends Perk {

    public static DepthsPerk INSTANCE = new DepthsPerk(ArsNouveau.prefix( "thread_depths"));

    public DepthsPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = new ImmutableMultimap.Builder<>();
        if(slotValue >= 3){
            modifiers.put(NeoForgeMod.SWIM_SPEED.value(), new AttributeModifier(INSTANCE.getRegistryName(), 2.0, AttributeModifier.Operation.ADD_VALUE));
        }

        return modifiers.build();
    }

    @Override
    public String getLangDescription() {
        return "Greatly increases the amount of time you may breathe underwater by reducing the chance your air will decrease. If this perk is in slot 3 or higher, you will no longer lose air and your swim speed is greatly increased. Stacks with Respiration Enchantments.";
    }

    @Override
    public String getLangName() {
        return "Depths";
    }
}
