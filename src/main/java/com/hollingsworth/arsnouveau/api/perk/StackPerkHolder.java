package com.hollingsworth.arsnouveau.api.perk;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.api.nbt.ItemstackData;
import com.hollingsworth.arsnouveau.api.registry.PerkRegistry;
import com.hollingsworth.arsnouveau.common.perk.StarbunclePerk;
import com.hollingsworth.arsnouveau.common.util.SerializationUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serializes a set of perks from an itemstack.
 */
public abstract class StackPerkHolder extends ItemstackData implements IPerkHolder<ItemStack> {
    private List<IPerk> perks;
    private int tier;
    private Map<IPerk, CompoundTag> perkTags;

    public StackPerkHolder(ItemStack stack) {
        super(stack);
        perkTags = new HashMap<>();
        CompoundTag tag = getItemTag(stack);
        List<IPerk> perkList = new ArrayList<>();
        if(tag != null){
            tier = tag.getInt("tier");
        }
        if(tag != null && tag.contains("perks")) {
            ListTag perkTagList = tag.getList("perks", SerializationUtil.COMPOUND_TAG_TYPE);
            for (int i = 0; i < perkTagList.size(); i++) {
                CompoundTag perkTag = perkTagList.getCompound(i);
                String perkName = perkTag.getString("perk");
                CompoundTag perkData = perkTag.getCompound("data");
                IPerk iPerk = PerkRegistry.getPerkMap().getOrDefault(ResourceLocation.tryParse(perkName), StarbunclePerk.INSTANCE);
                perkList.add(iPerk);
                this.perkTags.put(iPerk, perkData);
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
            perkTag.put("data", perkTags.getOrDefault(perk, new CompoundTag()));
            listTag.add(perkTag);
        });
        tag.putInt("tier", tier);
        tag.put("perks", listTag);
    }

    @Override
    public List<IPerk> getPerks() {
        return perks;
    }

    @Override
    public void setPerks(List<IPerk> perks) {
        this.perks = new ArrayList<>(perks);
        writeItem();
    }

    public int getTier() {
        return this.tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
        writeItem();
    }

    @Override
    public @Nullable CompoundTag getTagForPerk(IPerk perk) {
        return this.perkTags.getOrDefault(perk, null);
    }

    @Override
    public void setTagForPerk(IPerk perk, CompoundTag tag) {
        perkTags.put(perk, tag);
        writeItem();
    }

    @Override
    public String getTagString() {
        return "an_stack_perks";
    }
}
