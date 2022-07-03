package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.api.item.IWandable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public abstract class ChangeableBehavior implements IWandable {
    public List<WrappedGoal> goals = new ArrayList<>();

    public Level level;

    public Entity entity;

    public ChangeableBehavior(Entity entity, CompoundTag tag) {
        this.level = entity.level;
        this.entity = entity;
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putString("id", getRegistryName().toString());
        return tag;
    }

    public double getX(){
        return entity.getX();
    }

    public double getY(){
        return entity.getY();
    }

    public double getZ(){
        return entity.getZ();
    }

    public void pickUpItem(ItemEntity entity) {}

    protected abstract ResourceLocation getRegistryName();

}
