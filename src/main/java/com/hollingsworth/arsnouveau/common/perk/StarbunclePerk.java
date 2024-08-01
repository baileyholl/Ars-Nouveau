package com.hollingsworth.arsnouveau.common.perk;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StarbunclePerk extends Perk {

    public static final StarbunclePerk INSTANCE = new StarbunclePerk(ArsNouveau.prefix( "thread_starbuncle"));
    public static final UUID PERK_SPEED_UUID = UUID.fromString("46937d0b-123c-4786-95b5-748afd50f398");

    protected StarbunclePerk(ResourceLocation key) {
        super(key);
    }

    public @NotNull ItemAttributeModifiers applyAttributeModifiers(ItemAttributeModifiers modifiers, ItemStack stack, int slotValue) {
        return modifiers.withModifierAdded(Attributes.MOVEMENT_SPEED, new AttributeModifier(INSTANCE.getRegistryName(), 0.2 * slotValue, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL), EquipmentSlotGroup.ARMOR);
    }

    @Override
    public String getLangName() {
        return "The Starbuncle";
    }

    @Override
    public String getLangDescription() {
        return "Increases the speed of the player by 20%% each level.";
    }
}
