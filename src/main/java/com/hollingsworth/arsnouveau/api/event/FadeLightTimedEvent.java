package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.common.light.DynamLightUtil;
import com.hollingsworth.arsnouveau.common.light.LambDynamicLight;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import var;

public class FadeLightTimedEvent implements ITimedEvent, LambDynamicLight {
    protected int lambdynlights$luminance = 0;
    private int lambdynlights$lastLuminance = 0;
    private long lambdynlights$lastUpdate = 0;
    private LongOpenHashSet lambdynlights$trackedLitChunkPos = new LongOpenHashSet();

    public Vec3 targetPos;
    public
    int ticksLeft;
    int starterTicks;
    int startLuminance;
    Level level;

    public FadeLightTimedEvent(Level level, Vec3 pos, int duration, int startLuminance) {
        this.targetPos = pos;
        ticksLeft = duration;
        this.starterTicks = duration;
        this.startLuminance = startLuminance;
        this.level = level;
    }

    @Override
    public void tick(boolean serverSide) {
        // We do not want to update the entity on the server.
        if (!serverSide && !LightManager.shouldUpdateDynamicLight()) {
            lambdynlights$luminance = 0;
        }
        if (!serverSide && LightManager.shouldUpdateDynamicLight()) {
            if (this.isExpired()) {
                this.setDynamicLightEnabled(false);
            } else {
                this.dynamicLightTick();
                LightManager.updateTracking(this);
            }
        }
        ticksLeft--;
        if (ticksLeft <= 0) {
            this.setDynamicLightEnabled(false);
        }
    }

    @Override
    public boolean isExpired() {
        return ticksLeft <= 0;
    }

    @Override
    public double getDynamicLightX() {
        return targetPos.x;
    }

    @Override
    public double getDynamicLightY() {
        return targetPos.y;
    }

    @Override
    public double getDynamicLightZ() {
        return targetPos.z;
    }

    @Override
    public Level getDynamicLightWorld() {
        return level;
    }

    @Override
    public void resetDynamicLight() {
        this.lambdynlights$lastLuminance = 0;
    }

    @Override
    public int getLuminance() {
        return lambdynlights$luminance;
    }

    @Override
    public void dynamicLightTick() {
        lambdynlights$luminance = starterTicks == 0 ? 0 : (int) ((double) startLuminance * ((double) ticksLeft / (double) this.starterTicks));
    }

    @Override
    public boolean shouldUpdateDynamicLight() {
        return LightManager.shouldUpdateDynamicLight();
    }

    @Override
    public boolean lambdynlights$updateDynamicLight(LevelRenderer renderer) {
        int luminance = this.getLuminance();

        if (luminance != this.lambdynlights$lastLuminance) {
            this.lambdynlights$lastLuminance = luminance;

            var newPos = new LongOpenHashSet();

            if (luminance > 0) {
                var entityChunkPos = new ChunkPos(BlockPos.containing(targetPos));
                var chunkPos = new BlockPos.MutableBlockPos(entityChunkPos.x, DynamLightUtil.getSectionCoord(this.targetPos.y), entityChunkPos.z);

                LightManager.scheduleChunkRebuild(renderer, chunkPos);
                LightManager.updateTrackedChunks(chunkPos, this.lambdynlights$trackedLitChunkPos, newPos);
                BlockPos blockPos = BlockPos.containing(targetPos);
                var directionX = (blockPos.getX() & 15) >= 8 ? Direction.EAST : Direction.WEST;
                var directionY = (Mth.floor(blockPos.getY()) & 15) >= 8 ? Direction.UP : Direction.DOWN;
                var directionZ = (blockPos.getZ() & 15) >= 8 ? Direction.SOUTH : Direction.NORTH;

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
