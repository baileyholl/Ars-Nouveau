package com.hollingsworth.arsnouveau.api.ritual;

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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class AbstractRitual {

    public RitualBrazierTile tile;

    private RitualContext context;

    public RandomSource rand = RandomSource.create();


    public AbstractRitual() {
    }

    public AbstractRitual(RitualBrazierTile tile, RitualContext context) {
        this.tile = tile;
        this.setContext(context);
    }


    public void tryTick() {
        if (tile == null || !getContext().isStarted || getContext().isDone) {
            return;
        }

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
        this.getConsumedItems().add(stack.copy());
        stack.shrink(1);
        BlockUtil.safelyUpdateState(getWorld(), tile.getBlockPos());
    }

    public boolean didConsumeItem(ItemLike item) {
        for (ItemStack i : getConsumedItems()) {
            if (i.getItem() == item.asItem())
                return true;
        }
        return false;
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
        return Component.translatable("item." + getRegistryName().getNamespace() + "." + getRegistryName().getPath()).getString();
    }

    public String getDescription() {
        return Component.translatable(getDescriptionKey()).getString();
    }

    public String getDescriptionKey() {
        return getRegistryName().getNamespace() + ".ritual_desc." + getRegistryName().getPath();
    }

    public int getManaCost() {
        return 0;
    }

    public boolean consumesMana() {
        return getManaCost() > 0;
    }

    public void setNeedsMana(boolean needMana) {
        getContext().needsManaToRun = needMana;
        BlockUtil.safelyUpdateState(getWorld(), tile.getBlockPos());
    }

    public boolean needsManaNow() {
        return getContext().needsManaToRun;
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

    public @Nonnull RitualContext getContext() {
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
        return 50;
    }

    public String getLangName() {
        return "";
    }

    public String getLangDescription() {
        return "";
    }
}
