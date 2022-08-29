package com.hollingsworth.arsnouveau.api.perk;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import com.hollingsworth.arsnouveau.common.util.SerializationUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializes a set of perks from an itemstack.
 */
public abstract class StackPerkHolder extends ItemstackData implements IPerkHolder<ItemStack> {
    private ImmutableList<IPerk> perks;

    public StackPerkHolder(ItemStack stack) {
        super(stack);
        CompoundTag tag = getItemTag(stack);
        List<IPerk> perkList = new ArrayList<>();
        if(tag != null && tag.contains("perks")) {
            ListTag perkTag = tag.getList("perks", SerializationUtil.COMPOUND_TAG_TYPE);
            for (int i = 0; i < perkTag.size(); i++) {
                CompoundTag perkId = perkTag.getCompound(i);
                String perkName = perkId.getString("perk");
                IPerk iPerk = ArsNouveauAPI.getInstance().getPerkMap().getOrDefault(new ResourceLocation(perkName), StarbunclePerk.INSTANCE);
                perkList.add(iPerk);
            }
        }
        perks = ImmutableList.copyOf(perkList);
    }

    @Override
    public void writeToNBT(CompoundTag tag) {
        ListTag listTag = new ListTag();
        getPerks().forEach((perk) -> {
            CompoundTag perkTag = new CompoundTag();
            perkTag.putString("perk", perk.getRegistryName().toString());
            listTag.add(perkTag);
        });
        tag.put("perks", listTag);
    }

    @Override
    public ImmutableList<IPerk> getPerks() {
        return perks;
    }

    @Override
    public void setPerks(List<IPerk> perks) {
        this.perks = ImmutableList.copyOf(perks);
        writeItem();
    }

    @Override
    public String getTagString() {
        return "an_stack_perks";
    }
}
