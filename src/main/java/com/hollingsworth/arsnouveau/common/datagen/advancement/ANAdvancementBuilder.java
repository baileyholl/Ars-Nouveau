package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.google.common.collect.Maps;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Copied from Advancements.Builder with extentions to reduce copy pasta
 */
public class ANAdvancementBuilder implements net.minecraftforge.common.extensions.IForgeAdvancementBuilder{
    @Nullable
    private ResourceLocation parentId;
    @Nullable
    private Advancement parent;
    @Nullable
    private DisplayInfo display;
    private AdvancementRewards rewards = AdvancementRewards.EMPTY;
    private Map<String, Criterion> criteria = Maps.newLinkedHashMap();
    @Nullable
    private String[][] requirements;
    private RequirementsStrategy requirementsStrategy = RequirementsStrategy.AND;
    private String modid;
    private String fileKey;

    private ANAdvancementBuilder(@Nullable ResourceLocation pParentId, @Nullable DisplayInfo pDisplay, AdvancementRewards pRewards, Map<String, Criterion> pCriteria, String[][] pRequirements) {
        this.parentId = pParentId;
        this.display = pDisplay;
        this.rewards = pRewards;
        this.criteria = pCriteria;
        this.requirements = pRequirements;
    }

    private ANAdvancementBuilder(String modid, String fileKey) {
        this.modid = modid;
        this.fileKey = fileKey;
    }

    public static ANAdvancementBuilder builder(String modid, String fileKey) {
        return new ANAdvancementBuilder(modid, fileKey);
    }

    public ANAdvancementBuilder parent(Advancement pParent) {
        this.parent = pParent;
        return this;
    }

    public ANAdvancementBuilder parent(ResourceLocation pParentId) {
        this.parentId = pParentId;
        return this;
    }

    public ANAdvancementBuilder display(ItemStack pStack, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, FrameType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
        return this.display(new DisplayInfo(pStack, pTitle, pDescription, pBackground, pFrame, pShowToast, pAnnounceToChat, pHidden));
    }

    public ANAdvancementBuilder display(ItemLike pItem, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, FrameType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
        return this.display(new DisplayInfo(new ItemStack(pItem.asItem()), pTitle, pDescription, pBackground, pFrame, pShowToast, pAnnounceToChat, pHidden));
    }

    public ANAdvancementBuilder display(DisplayInfo pDisplay) {
        this.display = pDisplay;
        return this;
    }

    // The following displays cannot be used for roots.
    public ANAdvancementBuilder display(ItemStack pItem, FrameType pFrame) {
        return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), null, pFrame, true, true, false));
    }

    public ANAdvancementBuilder display(ItemLike pItem, FrameType pFrame) {
        return this.display(new ItemStack(pItem), pFrame);
    }

    // The following displays cannot be used for roots.
    public ANAdvancementBuilder display(ItemStack pItem, FrameType pFrame, boolean hidden) {
        return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), null, pFrame, true, true, hidden));
    }

    public ANAdvancementBuilder display(ItemLike pItem, FrameType pFrame, boolean hidden) {
        return this.display(new ItemStack(pItem), pFrame, hidden);
    }


    public ANAdvancementBuilder rewards(AdvancementRewards.Builder pRewardsBuilder) {
        return this.rewards(pRewardsBuilder.build());
    }

    public ANAdvancementBuilder rewards(AdvancementRewards pRewards) {
        this.rewards = pRewards;
        return this;
    }

    public ANAdvancementBuilder addCriterion(String pKey, CriterionTriggerInstance pCriterion) {
        return this.addCriterion(pKey, new Criterion(pCriterion));
    }

    public ANAdvancementBuilder addCriterion(CriterionTriggerInstance pCriterion) {
        return this.addCriterion(fileKey, new Criterion(pCriterion));
    }

    public ANAdvancementBuilder addCriterion(String pKey, Criterion pCriterion) {
        if (this.criteria.containsKey(pKey)) {
            throw new IllegalArgumentException("Duplicate criterion " + pKey);
        } else {
            this.criteria.put(pKey, pCriterion);
            return this;
        }
    }

    public ANAdvancementBuilder requirements(RequirementsStrategy pStrategy) {
        this.requirementsStrategy = pStrategy;
        return this;
    }

    public ANAdvancementBuilder requirements(String[][] pRequirements) {
        this.requirements = pRequirements;
        return this;
    }

    public ANAdvancementBuilder normalItemRequirement(ItemLike item){
        return this.display(item, FrameType.TASK).requireItem(item);
    }

    public ANAdvancementBuilder requireItem(ItemLike item){
        return this.addCriterion("has_" + ForgeRegistries.ITEMS.getKey(item.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
    }

    public MutableComponent getComponent(String type){
        return Component.translatable(modid + ".adv." + type + "." + fileKey);
    }

    /**
     * Tries to resolve the parent of this advancement, if possible. Returns true on success.
     */
    public boolean canBuild(Function<ResourceLocation, Advancement> pParentLookup) {
        if (this.parentId == null) {
            return true;
        } else {
            if (this.parent == null) {
                this.parent = pParentLookup.apply(this.parentId);
            }

            return this.parent != null;
        }
    }

    public Advancement build(ResourceLocation pId) {
        if (!this.canBuild((p_138407_) -> {
            return null;
        })) {
            throw new IllegalStateException("Tried to build incomplete advancement!");
        } else {
            if (this.requirements == null) {
                this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
            }

            return new Advancement(pId, this.parent, this.display, this.rewards, this.criteria, this.requirements, false);
        }
    }

    public Advancement save(Consumer<Advancement> pConsumer, String pId) {
        Advancement advancement = this.build(new ResourceLocation(pId));
        pConsumer.accept(advancement);
        return advancement;
    }

    public Advancement save(Consumer<Advancement> pConsumer) {
        return this.save(pConsumer, new ResourceLocation(modid, fileKey).toString());
    }

    public String toString() {
        return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
    }

    public Map<String, Criterion> getCriteria() {
        return this.criteria;
    }
}

