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
import java.util.UUID;

public class StepHeightPerk extends Perk {

    public static final StepHeightPerk INSTANCE = new StepHeightPerk(ArsNouveau.prefix( "thread_high_step"));

    public static final UUID PERK_STEP_UUID = UUID.fromString("46937d0b-123c-4786-95b5-748afd50f398");

    public StepHeightPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = new ImmutableMultimap.Builder<>();
        modifiers.put(NeoForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(PERK_STEP_UUID, "StepPerk", slotValue, AttributeModifier.Operation.ADDITION));
        return modifiers.build();
    }

    @Override
    public String getLangName() {
        return "High Step";
    }

    @Override
    public String getLangDescription() {
        return "Increases step height by one for each level.";
    }
}
