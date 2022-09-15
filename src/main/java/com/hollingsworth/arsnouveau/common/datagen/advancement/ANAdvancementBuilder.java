package com.hollingsworth.arsnouveau.common.datagen.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

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
        return this.display(new DisplayInfo(pItem, this.getComponent("title"), this.getComponent("description"), null, pFrame, true, true, false));
    }

    public ANAdvancementBuilder display(ItemLike pItem, FrameType pFrame) {
        return this.display(new DisplayInfo(new ItemStack(pItem.asItem()), this.getComponent("title"), this.getComponent("description"), null, pFrame, true, true, false));
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
        return Component.translatable(modid + ".advancement." + type + "." + fileKey);
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

            return new Advancement(pId, this.parent, this.display, this.rewards, this.criteria, this.requirements);
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

    public JsonObject serializeToJson() {
        if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
        }

        JsonObject jsonobject = new JsonObject();
        if (this.parent != null) {
            jsonobject.addProperty("parent", this.parent.getId().toString());
        } else if (this.parentId != null) {
            jsonobject.addProperty("parent", this.parentId.toString());
        }

        if (this.display != null) {
            jsonobject.add("display", this.display.serializeToJson());
        }

        jsonobject.add("rewards", this.rewards.serializeToJson());
        JsonObject jsonobject1 = new JsonObject();

        for(Map.Entry<String, Criterion> entry : this.criteria.entrySet()) {
            jsonobject1.add(entry.getKey(), entry.getValue().serializeToJson());
        }

        jsonobject.add("criteria", jsonobject1);
        JsonArray jsonarray1 = new JsonArray();

        for(String[] astring : this.requirements) {
            JsonArray jsonarray = new JsonArray();

            for(String s : astring) {
                jsonarray.add(s);
            }

            jsonarray1.add(jsonarray);
        }

        jsonobject.add("requirements", jsonarray1);
        return jsonobject;
    }

    public void serializeToNetwork(FriendlyByteBuf pBuffer) {
        if (this.requirements == null) {
            this.requirements = this.requirementsStrategy.createRequirements(this.criteria.keySet());
        }

        pBuffer.writeNullable(this.parentId, FriendlyByteBuf::writeResourceLocation);
        pBuffer.writeNullable(this.display, (p_214831_, p_214832_) -> {
            p_214832_.serializeToNetwork(p_214831_);
        });
        Criterion.serializeToNetwork(this.criteria, pBuffer);
        pBuffer.writeVarInt(this.requirements.length);

        for(String[] astring : this.requirements) {
            pBuffer.writeVarInt(astring.length);

            for(String s : astring) {
                pBuffer.writeUtf(s);
            }
        }

    }

    public String toString() {
        return "Task Advancement{parentId=" + this.parentId + ", display=" + this.display + ", rewards=" + this.rewards + ", criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + "}";
    }

    /** @deprecated Forge: use {@linkplain #fromJson(JsonObject, DeserializationContext, net.minecraftforge.common.crafting.conditions.ICondition.IContext) overload with context}. */
    @Deprecated
    public static ANAdvancementBuilder fromJson(JsonObject pJson, DeserializationContext pContext) {
        return fromJson(pJson, pContext, net.minecraftforge.common.crafting.conditions.ICondition.IContext.EMPTY);
    }

    public static ANAdvancementBuilder fromJson(JsonObject pJson, DeserializationContext pContext, net.minecraftforge.common.crafting.conditions.ICondition.IContext context) {
        if ((pJson = net.minecraftforge.common.crafting.ConditionalAdvancement.processConditional(pJson, context)) == null) return null;
        ResourceLocation resourcelocation = pJson.has("parent") ? new ResourceLocation(GsonHelper.getAsString(pJson, "parent")) : null;
        DisplayInfo displayinfo = pJson.has("display") ? DisplayInfo.fromJson(GsonHelper.getAsJsonObject(pJson, "display")) : null;
        AdvancementRewards advancementrewards = pJson.has("rewards") ? AdvancementRewards.deserialize(GsonHelper.getAsJsonObject(pJson, "rewards")) : AdvancementRewards.EMPTY;
        Map<String, Criterion> map = Criterion.criteriaFromJson(GsonHelper.getAsJsonObject(pJson, "criteria"), pContext);
        if (map.isEmpty()) {
            throw new JsonSyntaxException("Advancement criteria cannot be empty");
        } else {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(pJson, "requirements", new JsonArray());
            String[][] astring = new String[jsonarray.size()][];

            for(int i = 0; i < jsonarray.size(); ++i) {
                JsonArray jsonarray1 = GsonHelper.convertToJsonArray(jsonarray.get(i), "requirements[" + i + "]");
                astring[i] = new String[jsonarray1.size()];

                for(int j = 0; j < jsonarray1.size(); ++j) {
                    astring[i][j] = GsonHelper.convertToString(jsonarray1.get(j), "requirements[" + i + "][" + j + "]");
                }
            }

            if (astring.length == 0) {
                astring = new String[map.size()][];
                int k = 0;

                for(String s2 : map.keySet()) {
                    astring[k++] = new String[]{s2};
                }
            }

            for(String[] astring1 : astring) {
                if (astring1.length == 0 && map.isEmpty()) {
                    throw new JsonSyntaxException("Requirement entry cannot be empty");
                }

                for(String s : astring1) {
                    if (!map.containsKey(s)) {
                        throw new JsonSyntaxException("Unknown required criterion '" + s + "'");
                    }
                }
            }

            for(String s1 : map.keySet()) {
                boolean flag = false;

                for(String[] astring2 : astring) {
                    if (ArrayUtils.contains(astring2, s1)) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    throw new JsonSyntaxException("Criterion '" + s1 + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
                }
            }

            return new ANAdvancementBuilder(resourcelocation, displayinfo, advancementrewards, map, astring);
        }
    }

    public static ANAdvancementBuilder fromNetwork(FriendlyByteBuf pBuffer) {
        ResourceLocation resourcelocation = pBuffer.readNullable(FriendlyByteBuf::readResourceLocation);
        DisplayInfo displayinfo = pBuffer.readNullable(DisplayInfo::fromNetwork);
        Map<String, Criterion> map = Criterion.criteriaFromNetwork(pBuffer);
        String[][] astring = new String[pBuffer.readVarInt()][];

        for(int i = 0; i < astring.length; ++i) {
            astring[i] = new String[pBuffer.readVarInt()];

            for(int j = 0; j < astring[i].length; ++j) {
                astring[i][j] = pBuffer.readUtf();
            }
        }

        return new ANAdvancementBuilder(resourcelocation, displayinfo, AdvancementRewards.EMPTY, map, astring);
    }

    public Map<String, Criterion> getCriteria() {
        return this.criteria;
    }
}

