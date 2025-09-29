package com.hollingsworth.arsnouveau.common.mixin.light;

import com.hollingsworth.arsnouveau.api.event.EventQueue;
import com.hollingsworth.arsnouveau.api.event.FadeLightTimedEvent;
import com.hollingsworth.arsnouveau.common.light.DynamLightUtil;
import com.hollingsworth.arsnouveau.common.light.LambDynamicLight;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Entity.class)
public abstract class LightEntityMixin implements LambDynamicLight {
    @Shadow
    public Level level;

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getEyeY();

    @Shadow
    public abstract double getZ();

    @Shadow
    public abstract double getY();


    @Shadow
    public abstract EntityType<?> getType();

    @Shadow
    public abstract BlockPos blockPosition();

    @Shadow
    public abstract boolean isRemoved();

    @Shadow
    private ChunkPos chunkPosition;

    @Shadow
    public abstract Level level();

    @Shadow
    public abstract BlockPos getOnPos();

    @Shadow
    public abstract double getZ(double pScale);

    @Shadow
    public abstract Vec3 position();

    @Unique
    protected int ars_nouveau$luminance = 0;
    @Unique
    private int ars_nouveau$lastLuminance = 0;

    @Unique
    private double ars_nouveau$prevX;
    @Unique
    private double ars_nouveau$prevY;
    @Unique
    private double ars_nouveau$prevZ;
    @Unique
    private LongOpenHashSet ars_nouveau$trackedLitChunkPos = new LongOpenHashSet();

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        // We do not want to update the entity on the server.
        if (level.isClientSide && !LightManager.shouldUpdateDynamicLight()) {
            ars_nouveau$luminance = 0;
        }
        if (this.level.isClientSide() && LightManager.shouldUpdateDynamicLight()) {
            if (this.isRemoved()) {
                this.ars_nouveau$setDynamicLightEnabled(false);
            } else {
                this.ars_nouveau$dynamicLightTick();
                LightManager.updateTracking(this);
            }
        }
    }

    @Inject(method = "remove", at = @At("TAIL"))
    public void onRemove(CallbackInfo ci) {
        if (this.level.isClientSide()) {
            this.ars_nouveau$setDynamicLightEnabled(false);
        }
    }

    @Inject(method = "onClientRemoval", at = @At("TAIL"))
    public void removed(CallbackInfo ci) {
        if (this.level.isClientSide()) {
            this.ars_nouveau$setDynamicLightEnabled(false);
            if (ars_nouveau$luminance > 0)
                EventQueue.getClientQueue().addEvent(new FadeLightTimedEvent(this.level(), this.position(), 8, ars_nouveau$luminance));
        }
    }

    @Override
    public double ars_nouveau$getDynamicLightX() {
        return this.getX();
    }

    @Override
    public double ars_nouveau$getDynamicLightY() {
        return this.getEyeY();
    }

    @Override
    public double ars_nouveau$getDynamicLightZ() {
        return this.getZ();
    }

    @Override
    public Level ars_nouveau$getDynamicLightWorld() {
        return this.level;
    }

    @Override
    public void ars_nouveau$resetDynamicLight() {
        this.ars_nouveau$lastLuminance = 0;
    }

    @Override
    public boolean ars_nouveau$shouldUpdateDynamicLight() {
        return LightManager.shouldUpdateDynamicLight() && DynamLightUtil.couldGiveLight((Entity) (Object) this);
    }

    @Override
    public void ars_nouveau$dynamicLightTick() {
        ars_nouveau$luminance = 0;
        int luminance = DynamLightUtil.lightForEntity((Entity) (Object) this);
        if (luminance > this.ars_nouveau$luminance)
            this.ars_nouveau$luminance = luminance;
    }

    @Override
    public int ars_nouveau$getLuminance() {
        return this.ars_nouveau$luminance;
    }

    @Override
    public boolean ars_nouveau$updateDynamicLight(LevelRenderer renderer) {
        if (!this.ars_nouveau$shouldUpdateDynamicLight())
            return false;
        double deltaX = this.getX() - this.ars_nouveau$prevX;
        double deltaY = this.getY() - this.ars_nouveau$prevY;
        double deltaZ = this.getZ() - this.ars_nouveau$prevZ;

        int luminance = this.ars_nouveau$getLuminance();

        if (Math.abs(deltaX) > 0.1D || Math.abs(deltaY) > 0.1D || Math.abs(deltaZ) > 0.1D || luminance != this.ars_nouveau$lastLuminance) {
            this.ars_nouveau$prevX = this.getX();
            this.ars_nouveau$prevY = this.getY();
            this.ars_nouveau$prevZ = this.getZ();
            this.ars_nouveau$lastLuminance = luminance;

            var newPos = new LongOpenHashSet();

            if (luminance > 0) {
                var entityChunkPos = this.chunkPosition;
                var chunkPos = new BlockPos.MutableBlockPos(entityChunkPos.x, DynamLightUtil.getSectionCoord(this.getEyeY()), entityChunkPos.z);

                LightManager.scheduleChunkRebuild(renderer, chunkPos);
                LightManager.updateTrackedChunks(chunkPos, this.ars_nouveau$trackedLitChunkPos, newPos);

                var directionX = (this.blockPosition().getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
                var directionY = (Mth.floor(this.getEyeY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
                var directionZ = (this.blockPosition().getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

                for (int i = 0; i < 7; i++) {
                    if (i % 4 == 0) {
                        chunkPos.move(directionX); // X
                    } else if (i % 4 == 1) {
                        chunkPos.move(directionZ); // XZ
                    } else if (i % 4 == 2) {
                        chunkPos.move(directionX.getOpposite()); // Z
                    } else {
                        chunkPos.move(directionZ.getOpposite()); // origin
                        chunkPos.move(directionY); // Y
                    }
                    LightManager.scheduleChunkRebuild(renderer, chunkPos);
                    LightManager.updateTrackedChunks(chunkPos, this.ars_nouveau$trackedLitChunkPos, newPos);
                }
            }
            // Schedules the rebuild of removed chunks.
            this.ars_nouveau$scheduleTrackedChunksRebuild(renderer);
            // Update tracked lit chunks.
            this.ars_nouveau$trackedLitChunkPos = newPos;
            return true;
        }
        return false;
    }

    @Override
    public void ars_nouveau$scheduleTrackedChunksRebuild(LevelRenderer renderer) {
        if (Minecraft.getInstance().level == this.level)
            for (long pos : this.ars_nouveau$trackedLitChunkPos) {
                LightManager.scheduleChunkRebuild(renderer, pos);
            }
    }
}
