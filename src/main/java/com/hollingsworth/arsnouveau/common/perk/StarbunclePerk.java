package com.hollingsworth.arsnouveau.common.perk;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;

public class StarbunclePerk extends Perk {

    public static final StarbunclePerk INSTANCE = new StarbunclePerk(new ResourceLocation(ArsNouveau.MODID, "thread_starbuncle"));
    public static final UUID PERK_SPEED_UUID = UUID.fromString("46937d0b-123c-4786-95b5-748afd50f398");
    public static final UUID PERK_STEP_UUID = UUID.fromString("46937d0b-123c-4786-95b5-748afd50f398");
    protected StarbunclePerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> modifiers = new ImmutableMultimap.Builder<>();
        modifiers.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(PERK_SPEED_UUID, "StarbunclePerk", 0.2 * slotValue, AttributeModifier.Operation.MULTIPLY_TOTAL));
        if(slotValue >= 3){
            modifiers.put(ForgeMod.STEP_HEIGHT_ADDITION.get(), new AttributeModifier(PERK_STEP_UUID, "StarbuncleStepPerk", 2.0, AttributeModifier.Operation.ADDITION));
        }

        return modifiers.build();
    }

    @Override
    public String getLangName() {
        return "Starbuncle";
    }

    @Override
    public String getLangDescription() {
        return "Increases the speed of the player by 20%% each level and grants step assist at level 3.";
    }
}
