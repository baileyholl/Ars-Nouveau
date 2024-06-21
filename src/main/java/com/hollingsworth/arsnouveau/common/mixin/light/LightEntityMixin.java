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
import var;

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
    protected int lambdynlights$luminance = 0;
    @Unique
    private int lambdynlights$lastLuminance = 0;
    @Unique
    private long lambdynlights$lastUpdate = 0;
    @Unique
    private double lambdynlights$prevX;
    @Unique
    private double lambdynlights$prevY;
    @Unique
    private double lambdynlights$prevZ;
    @Unique
    private LongOpenHashSet lambdynlights$trackedLitChunkPos = new LongOpenHashSet();

    @Inject(method = "tick", at = @At("TAIL"))
    public void onTick(CallbackInfo ci) {
        // We do not want to update the entity on the server.
        if (level.isClientSide && !LightManager.shouldUpdateDynamicLight()) {
            lambdynlights$luminance = 0;
        }
        if (this.level.isClientSide() && LightManager.shouldUpdateDynamicLight()) {
            if (this.isRemoved()) {
                this.setDynamicLightEnabled(false);
            } else {
                this.dynamicLightTick();
                LightManager.updateTracking(this);
            }
        }
    }

    @Inject(method = "remove", at = @At("TAIL"))
    public void onRemove(CallbackInfo ci) {
        if (this.level.isClientSide()) {
            this.setDynamicLightEnabled(false);
        }
    }

    @Inject(method = "onClientRemoval", at = @At("TAIL"))
    public void removed(CallbackInfo ci) {
        if (this.level.isClientSide()) {
            this.setDynamicLightEnabled(false);
            if (lambdynlights$luminance > 0)
                EventQueue.getClientQueue().addEvent(new FadeLightTimedEvent(this.level(), this.position(), 8, lambdynlights$luminance));
        }
    }

    @Override
    public double getDynamicLightX() {
        return this.getX();
    }

    @Override
    public double getDynamicLightY() {
        return this.getEyeY();
    }

    @Override
    public double getDynamicLightZ() {
        return this.getZ();
    }

    @Override
    public Level getDynamicLightWorld() {
        return this.level;
    }

    @Override
    public void resetDynamicLight() {
        this.lambdynlights$lastLuminance = 0;
    }

    @Override
    public boolean shouldUpdateDynamicLight() {
        return LightManager.shouldUpdateDynamicLight() && DynamLightUtil.couldGiveLight((Entity) (Object) this);
    }

    @Override
    public void dynamicLightTick() {
        lambdynlights$luminance = 0;
        int luminance = DynamLightUtil.lightForEntity((Entity) (Object) this);
        if (luminance > this.lambdynlights$luminance)
            this.lambdynlights$luminance = luminance;
    }

    @Override
    public int getLuminance() {
        return this.lambdynlights$luminance;
    }

    @Override
    public boolean lambdynlights$updateDynamicLight(LevelRenderer renderer) {
        if (!this.shouldUpdateDynamicLight())
            return false;
        double deltaX = this.getX() - this.lambdynlights$prevX;
        double deltaY = this.getY() - this.lambdynlights$prevY;
        double deltaZ = this.getZ() - this.lambdynlights$prevZ;

        int luminance = this.getLuminance();

        if (Math.abs(deltaX) > 0.1D || Math.abs(deltaY) > 0.1D || Math.abs(deltaZ) > 0.1D || luminance != this.lambdynlights$lastLuminance) {
            this.lambdynlights$prevX = this.getX();
            this.lambdynlights$prevY = this.getY();
            this.lambdynlights$prevZ = this.getZ();
            this.lambdynlights$lastLuminance = luminance;

            var newPos = new LongOpenHashSet();

            if (luminance > 0) {
                var entityChunkPos = this.chunkPosition;
                var chunkPos = new BlockPos.MutableBlockPos(entityChunkPos.x, DynamLightUtil.getSectionCoord(this.getEyeY()), entityChunkPos.z);

                LightManager.scheduleChunkRebuild(renderer, chunkPos);
                LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);

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
                    LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
                }
            }
            // Schedules the rebuild of removed chunks.
            this.lambdynlights$scheduleTrackedChunksRebuild(renderer);
            // Update tracked lit chunks.
            this.lambdynlights$trackedLitChunkPos = newPos;
            return true;
        }
        return false;
    }

    @Override
    public void lambdynlights$scheduleTrackedChunksRebuild(LevelRenderer renderer) {
        if (Minecraft.getInstance().level == this.level)
            for (long pos : this.lambdynlights$trackedLitChunkPos) {
                LightManager.scheduleChunkRebuild(renderer, pos);
            }
    }
}
