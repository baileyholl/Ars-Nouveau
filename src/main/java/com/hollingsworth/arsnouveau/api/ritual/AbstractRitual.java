package com.hollingsworth.arsnouveau.api.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public abstract class AbstractRitual {

    public RitualBrazierTile tile;

    private RitualContext context;

    public RandomSource rand = RandomSource.create();


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

    public boolean canStart() {
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

    public void onStart() {
        getContext().isStarted = true;
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

    public int getSourceCost() {
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

    public void write(CompoundTag tag) {
        CompoundTag contextTag = new CompoundTag();
        getContext().write(contextTag);
        tag.put("context", contextTag);
    }

    // Called once the ritual tile has created a new instance of this ritual
    public void read(CompoundTag tag) {
        this.setContext(RitualContext.read(tag.getCompound("context")));
    }

    public@NotNull RitualContext getContext() {
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
    public boolean canBeTraded(){
        return true;
    }

    public void onDestroy() {}

    public void onStatusChanged(boolean status) {}
}
