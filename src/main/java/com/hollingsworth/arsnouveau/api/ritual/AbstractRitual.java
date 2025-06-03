package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.IConfigurable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public abstract class AbstractRitual implements IConfigurable {

    public RitualBrazierTile tile;

    private RitualContext context;

    public RandomSource rand = RandomSource.create();

    public UUID playerUUID;

    public AbstractRitual() {
    }

    public void tryTick(RitualBrazierTile tickingTile){
        if (tickingTile == null || !getContext().isStarted || getContext().isDone) {
            return;
        }
        this.tile = tickingTile;
        tick();
    }

    public @Nullable BlockPos getPos() {
        return tile != null ? tile.getBlockPos() : null;
    }

    public @Nullable Level getWorld() {
        return tile != null ? tile.getLevel() : null;
    }

    public boolean canStart(@Nullable Player player) {
        return true;
    }

    public List<ItemStack> getConsumedItems() {
        return getContext().consumedItems;
    }

    public boolean canConsumeItem(ItemStack stack) {
        return false;
    }

    public void onItemConsumed(ItemStack stack) {
        this.getConsumedItems().add(stack.split(1));
        BlockUtil.safelyUpdateState(getWorld(), tile.getBlockPos());
    }

    public boolean didConsumeItem(ItemLike item) {
        for (ItemStack i : getConsumedItems()) {
            if (i.getItem() == item.asItem())
                return true;
        }
        return false;
    }

    public int itemConsumedCount(Predicate<ItemStack> stackPredicate){
        int total = 0;
        for(ItemStack stack : getConsumedItems()){
            if(stackPredicate.test(stack)){
                total += stack.getCount();
            }
        }
        return total;
    }

    /**
     * Returns the list of consumed items in Item x Num format. Because rituals can consume above the itemstack count, stacks are also stored as unique entries.
     */
    public List<String> getFormattedConsumedItems() {
        Map<String, Integer> map = new HashMap<>();
        for (ItemStack i : getConsumedItems()) {
            String name = i.getHoverName().getString();
            if (map.containsKey(name)) {
                map.put(name, map.get(name) + 1);
            } else {
                map.put(name, 1);
            }
        }
        List<String> list = new ArrayList<>();
        for (String s : map.keySet()) {
            list.add(s + " x " + map.get(s));
        }
        return list;
    }

    public void incrementProgress() {
        getContext().progress++;
    }

    public int getProgress() {
        return getContext().progress;
    }

    public void onStart(@Nullable Player player) {
        getContext().isStarted = true;
        if (player != null) this.playerUUID = player.getUUID();
    }

    public boolean isRunning() {
        return getContext().isStarted && !getContext().isDone;
    }

    public boolean isDone() {
        return getContext().isDone;
    }

    public void setFinished() {
        getContext().isDone = true;
    }

    protected abstract void tick();

    public void onEnd() {
        this.getContext().isDone = true;
    }

    public String getName() {
        return Component.translatable(ArsNouveau.MODID + ".tablet_of", Component.translatable("item." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()).getString()).getString();
    }

    public String getDescription() {
        return Component.translatable(getDescriptionKey()).getString();
    }

    public String getDescriptionKey() {
        return getRegistryName().getNamespace() + ".ritual_desc." + getRegistryName().getPath();
    }

    /**
     * Gets the source cost for this ritual.
     * Override this method to provide a custom source cost.
     * If you want to make the cost configurable, use the config system in buildConfig()
     * and return the configured value here.
     * 
     * @return The amount of source this ritual consumes per tick
     */
    public int getSourceCost() {
        return COST != null ? COST.get() : getDefaultSourceCost();
    }

    public int getDefaultSourceCost() {
        return 0;
    }

    public boolean consumesSource() {
        return getSourceCost() > 0;
    }

    public void setNeedsSource(boolean needMana) {
        getContext().needsSourceToRun = needMana;
        BlockUtil.safelyUpdateState(getWorld(), tile.getBlockPos());
    }

    public boolean needsSourceNow() {
        return getContext().needsSourceToRun;
    }

    public boolean takeSourceNow(){
        setNeedsSource(true);
        return tile.takeSource();
    }

    public void write(HolderLookup.Provider provider, CompoundTag tag) {
        CompoundTag contextTag = new CompoundTag();
        getContext().write(provider, contextTag);
        tag.put("context", contextTag);
    }

    // Called once the ritual tile has created a new instance of this ritual
    public void read(HolderLookup.Provider provider, CompoundTag tag) {
        this.setContext(RitualContext.read(provider, tag.getCompound("context")));
    }

    public @NotNull RitualContext getContext() {
        if (context == null)
            context = new RitualContext();
        return context;
    }

    public void setContext(RitualContext context) {
        this.context = context;
    }

    public abstract ResourceLocation getRegistryName();

    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(255),
                rand.nextInt(22),
                rand.nextInt(255)
        );
    }

    public ParticleColor getOuterColor() {
        return getCenterColor();
    }

    public int getParticleIntensity() {
        return 10;
    }

    public String getLangName() {
        return "";
    }

    public String getLangDescription() {
        return "";
    }

    /**
     * If this ritual can appear in villager trades
     */
    @Deprecated(since = "4.11.0", forRemoval = true)
    public boolean canBeTraded(){
        return true;
    }

    public void onDestroy() {}

    public void onStatusChanged(boolean status) {}

    public void modifyTooltips(List<Component> tooltips) {}

    // IConfigurable implementation
    @Nullable
    private ModConfigSpec config;
    @Nullable
    private ModConfigSpec.IntValue COST;

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        int cost = getDefaultSourceCost();
        if (cost > 0) {
            COST = builder
                    .comment("The amount of source this ritual consumes per operation. Set to 0 to disable source consumption.")
                    .defineInRange("source_cost", getDefaultSourceCost(), 0, Integer.MAX_VALUE);
        }
    }

    @Override
    public @Nullable ModConfigSpec getConfigSpec() {
        return config;
    }
    
    @Override
    public void setConfigSpec(@Nullable ModConfigSpec config) {
        this.config = config;
    }

    public String getSubFolder() {
        return "rituals";
    }
}
