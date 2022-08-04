package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes a set of perks from an itemstack.
 */
public abstract class StackPerkHolder extends ItemstackData implements IPerkHolder<ItemStack> {
    protected PerkSet perkSet = new PerkSet();

    public StackPerkHolder(ItemStack stack) {
        super(stack);
        CompoundTag tag = getItemTag(stack);
        if(tag != null) {
            CompoundTag perkTag = tag.getCompound("perkSet");
            perkSet = new PerkSet(perkTag);
        }
        perkSet.onMutated = this::writeItem;
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        tag.put("perkSet", perkSet.serialize());
    }

    @Override
    public @NotNull PerkSet getPerkSet() {
        return perkSet;
    }

    @Override
    public String getTagString() {
        return "an_stack_perks";
    }

}
