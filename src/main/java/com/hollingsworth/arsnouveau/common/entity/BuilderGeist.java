package com.hollingsworth.arsnouveau.common.entity;

import com.hollingsworth.arsnouveau.common.block.tile.BuilderGeistTile;
import com.hollingsworth.arsnouveau.common.entity.statemachine.IStateEvent;
import com.hollingsworth.arsnouveau.common.entity.statemachine.SimpleStateMachine;
import com.hollingsworth.arsnouveau.common.entity.statemachine.buildergeist.BuilderGeistState;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class BuilderGeist extends PathfinderMob {

    public SimpleStateMachine<BuilderGeistState, IStateEvent> stateMachine = new SimpleStateMachine<>(new BuilderGeistState(this));
    public @Nullable BlockPos homePos;

    public BuilderGeist(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    public BuilderGeist(Level level) {
        this(ModEntities.BUILDER_GEIST.get(), level);
    }

    @Override
    public void tick() {
        super.tick();
        if (!level.isClientSide) {
            stateMachine.tick();
        }
    }

    public @Nullable BuilderGeistTile getHome() {
        if (homePos != null && level.isLoaded(homePos) && level.getBlockEntity(homePos) instanceof BuilderGeistTile tile) {
            return tile;
        }
        return null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (homePos != null) {
            compound.putLong("homePos", homePos.asLong());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        homePos = compound.contains("homePos") ? BlockPos.of(compound.getLong("homePos")) : null;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }
}
