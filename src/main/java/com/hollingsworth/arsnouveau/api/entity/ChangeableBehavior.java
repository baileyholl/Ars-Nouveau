package com.hollingsworth.arsnouveau.api.entity;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class ChangeableBehavior implements IWandable {
    public List<WrappedGoal> goals = new ArrayList<>();

    public Level level;

    public Entity entity;

    public ChangeableBehavior(Entity entity, CompoundTag tag) {
        this.level = entity.level;
        this.entity = entity;
    }

    public void tick() {

    }

    public void getTooltip(Consumer<Component> tooltip) {
    }

    public CompoundTag toTag(CompoundTag tag) {
        return tag;
    }

    public double getX() {
        return entity.getX();
    }

    public double getY() {
        return entity.getY();
    }

    public double getZ() {
        return entity.getZ();
    }

    public void pickUpItem(ItemEntity entity) {
    }

    public abstract ResourceLocation getRegistryName();

    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    public ItemStack getStackForRender() {
        return ItemStack.EMPTY;
    }


    /**
     * @return true if onWanded can remove the current accessory, false if it only needs to reset connections
     */
    public boolean clearOrRemove() {
        return true;
    }

    public void syncTag() {

    }
}
