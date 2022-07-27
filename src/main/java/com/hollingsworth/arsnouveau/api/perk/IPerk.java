package com.hollingsworth.arsnouveau.api.perk;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import javax.print.attribute.Attribute;

/**
 * Represents the actions or benefits a perk may provide.
 */
public interface IPerk {

    default Multimap<Attribute, AttributeModifier> getModifiers(ItemStack stack){
        return new ImmutableMultimap.Builder<Attribute, AttributeModifier>().build();
    }

    // TODO: Expand on this, maybe logic for cost during crafting only? Something contextual to when it is applied.
    default int getSlotCost(){
        return 1;
    }

    /**
     * The maximum amount of times we count this perk before the rest are wasted.
     */
    int getCountCap();

    ResourceLocation getID();
}
