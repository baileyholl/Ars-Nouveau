package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.google.common.collect.Maps;
import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Copied from Advancements.Builder with extentions to reduce copy pasta
 */
public class ANAdvancementBuilder implements net.neoforged.neoforge.common.extensions.IAdvancementBuilderExtension{
    @Nullable
    private ResourceLocation parentId;
    @Nullable
    private Advancement parent;
    @Nullable
    private DisplayInfo display;
    private AdvancementRewards rewards = AdvancementRewards.EMPTY;
    private Map<String, Criterion<?>> criteria = Maps.newLinkedHashMap();
    @Nullable
    private AdvancementRequirements requirements;
    private AdvancementRequirements.Strategy requirementsStrategy = AdvancementRequirements.Strategy.AND;
    private String modid;
    private String fileKey;

    private ANAdvancementBuilder(@Nullable ResourceLocation pParentId, @Nullable DisplayInfo pDisplay, AdvancementRewards pRewards, Map<String, Criterion<?>> pCriteria, AdvancementRequirements pRequirements) {
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

    public ANAdvancementBuilder parent(AdvancementHolder parent) {
        return this.parent(parent.id());
    }

    public ANAdvancementBuilder parent(ResourceLocation pParentId) {
        this.parentId = pParentId;
        return this;
    }

    public ANAdvancementBuilder display(ItemStack pStack, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, AdvancementType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
        return this.display(new DisplayInfo(pStack, pTitle, pDescription, Optional.ofNullable(pBackground), pFrame, pShowToast, pAnnounceToChat, pHidden));
    }

    public ANAdvancementBuilder display(ItemLike pItem, Component pTitle, Component pDescription, @Nullable ResourceLocation pBackground, AdvancementType pFrame, boolean pShowToast, boolean pAnnounceToChat, boolean pHidden) {
        return this.display(new DisplayInfo(new ItemStack(pItem.asItem()), pTitle, pDescription, Optional.ofNullable(pBackground), pFrame, pShowToast, pAnnounceToChat, pHidden));
    }

    public ANAdvancementBuilder display(DisplayInfo pDisplay) {
        this.display = pDisplay;
        return this;
    }

    // The following displays cannot be used for roots.
    public ANAdvancementBuilder display(ItemStack pItem, AdvancementType pFrame) {
        return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), null, pFrame, true, true, false));
    }

    public ANAdvancementBuilder display(ItemLike pItem, AdvancementType pFrame) {
        return this.display(new ItemStack(pItem), pFrame);
    }

    // The following displays cannot be used for roots.
    public ANAdvancementBuilder display(ItemStack pItem, AdvancementType pFrame, boolean hidden) {
        return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("desc"), null, pFrame, true, true, hidden));
    }

    public ANAdvancementBuilder display(ItemLike pItem, AdvancementType pFrame, boolean hidden) {
        return this.display(new ItemStack(pItem), pFrame, hidden);
    }


    public ANAdvancementBuilder rewards(AdvancementRewards.Builder pRewardsBuilder) {
        return this.rewards(pRewardsBuilder.build());
    }

    public ANAdvancementBuilder rewards(AdvancementRewards pRewards) {
        this.rewards = pRewards;
        return this;
    }

    public ANAdvancementBuilder addCriterion(Criterion<?> pCriterion) {
        return this.addCriterion(fileKey, pCriterion);
    }

    public ANAdvancementBuilder addCriterion(String pKey, Criterion<?> pCriterion) {
        if (this.criteria.containsKey(pKey)) {
            throw new IllegalArgumentException("Duplicate criterion " + pKey);
        } else {
            this.criteria.put(pKey, pCriterion);
            return this;
        }
    }

    public ANAdvancementBuilder requirements(AdvancementRequirements.Strategy pStrategy) {
        this.requirementsStrategy = pStrategy;
        return this;
    }

    public ANAdvancementBuilder requirements(AdvancementRequirements pRequirements) {
        this.requirements = pRequirements;
        return this;
    }

    public ANAdvancementBuilder normalItemRequirement(ItemLike item){
        return this.display(item, AdvancementType.TASK).requireItem(item);
    }

    public ANAdvancementBuilder requireItem(ItemLike item){
        return this.addCriterion("has_" + BuiltInRegistries.ITEM.getKey(item.asItem()).getPath(), InventoryChangeTrigger.TriggerInstance.hasItems(item));
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

    public Advancement build() {

        if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.create(this.criteria.keySet());
        }
        if(this.criteria.isEmpty()){
            throw new IllegalStateException("Advancement " + fileKey + " has no criteria " + this);
        }

        return new Advancement(Optional.ofNullable(this.parentId), Optional.ofNullable(this.display), this.rewards, this.criteria, this.requirements, false);

    }

    public AdvancementHolder save(Consumer<AdvancementHolder> pConsumer, ResourceLocation pId) {
        var adv =  this.build();
        AdvancementHolder advancement = new AdvancementHolder(pId, adv);
        pConsumer.accept(advancement);
        return advancement;
    }

    public AdvancementHolder save(Consumer<AdvancementHolder> pConsumer) {
        return this.save(pConsumer, ArsNouveau.prefix(fileKey));
    }

    public String toString() {
        return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + this.requirements + "}";
    }

    public Map<String, Criterion<?>> getCriteria() {
        return this.criteria;
    }
}

