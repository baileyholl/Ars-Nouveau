package com.hollingsworth.arsnouveau.api.perk;

import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Serializes a set of perks from an itemstack.
 */
public class StackPerkProvider extends ItemstackData implements IPerkProvider{
    PerkSet perkSet;
    public StackPerkProvider(ItemStack stack) {
        super(stack);
        CompoundTag tag = getItemTag(stack);
        perkSet = new PerkSet();
        if(tag == null)
            return;

    }

    @Override
    public void writeToNBT(CompoundTag tag) {

    }

    @Override
    public @NotNull PerkSet getPerkSet() {
        return perkSet;
    }

    @Override
    public String getTagString() {
        return "an_perks";
    }

}
