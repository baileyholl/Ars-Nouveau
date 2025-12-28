package com.hollingsworth.arsnouveau.api.event;

import com.hollingsworth.arsnouveau.common.light.DynamLightUtil;
import com.hollingsworth.arsnouveau.common.light.LambDynamicLight;
import com.hollingsworth.arsnouveau.common.light.LightManager;
import dev.lambdaurora.lambdynlights.api.behavior.DynamicLightBehavior;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

public class FadeLightTimedEvent implements ITimedEvent, LambDynamicLight, DynamicLightBehavior {
    protected int luminance = 0;
    private int lastLuminance = 0;
    private LongOpenHashSet lambdynlights$trackedLitChunkPos = new LongOpenHashSet();

    private final BoundingBox box;
    public final Vec3 targetPos;
    public
    int ticksLeft;
    int starterTicks;
    int startLuminance;
    Level level;

    public FadeLightTimedEvent(Level level, Vec3 pos, int duration, int startLuminance) {
        this.box = new BoundingBox(
                Mth.floor(pos.x),  Mth.floor(pos.y),  Mth.floor(pos.z),
                Mth.ceil(pos.x),  Mth.ceil(pos.y),  Mth.ceil(pos.z)
        );
        this.targetPos = pos;
        ticksLeft = duration;
        this.starterTicks = duration;
        this.startLuminance = startLuminance;
        this.level = level;
    }

    @Override
    public void tick(boolean serverSide) {
        if (serverSide) return;

        var context = LightManager.dynamicLightsContext;
        if (context == null) {
            // We do not want to update the entity on the server.
            if (!LightManager.shouldUpdateDynamicLight()) {
                luminance = 0;
            } else if (LightManager.shouldUpdateDynamicLight()) {
                if (this.isExpired()) {
                    this.ars_nouveau$setDynamicLightEnabled(false);
                } else {
                    this.ars_nouveau$dynamicLightTick();
                    LightManager.updateTracking(this);
                }
            }
        } else {
            this.ars_nouveau$dynamicLightTick();
        }

        ticksLeft--;
        if (ticksLeft <= 0 && context == null) {
            this.ars_nouveau$setDynamicLightEnabled(false);
        }
    }

    @Override
    public boolean isExpired() {
        return ticksLeft <= 0;
    }

    @Override
    public double ars_nouveau$getDynamicLightX() {
        return targetPos.x;
    }

    @Override
    public double ars_nouveau$getDynamicLightY() {
        return targetPos.y;
    }

    @Override
    public double ars_nouveau$getDynamicLightZ() {
        return targetPos.z;
    }

    @Override
    public Level ars_nouveau$getDynamicLightWorld() {
        return level;
    }

    @Override
    public void ars_nouveau$resetDynamicLight() {
        this.lastLuminance = 0;
    }

    @Override
    public int ars_nouveau$getLuminance() {
        return luminance;
    }

    @Override
    public void ars_nouveau$dynamicLightTick() {
        luminance = starterTicks == 0 ? 0 : (int) ((double) startLuminance * ((double) ticksLeft / (double) this.starterTicks));
    }

    @Override
    public boolean ars_nouveau$shouldUpdateDynamicLight() {
        return LightManager.shouldUpdateDynamicLight();
    }

    @Override
    public boolean ars_nouveau$updateDynamicLight(LevelRenderer renderer) {
        int luminance = this.ars_nouveau$getLuminance();

        if (luminance != this.lastLuminance) {
            this.lastLuminance = luminance;

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
            this.ars_nouveau$scheduleTrackedChunksRebuild(renderer);
            // Update tracked lit chunks.
            this.lambdynlights$trackedLitChunkPos = newPos;
            return true;
        }
        return false;
    }

    @Override
    public void ars_nouveau$scheduleTrackedChunksRebuild(LevelRenderer renderer) {
        if (Minecraft.getInstance().level == this.level)
            for (long pos : this.lambdynlights$trackedLitChunkPos) {
                LightManager.scheduleChunkRebuild(renderer, pos);
            }
    }

    //region LambDynamicLights
    @Override
    public @Range(from = 0L, to = 15L) double lightAtPos(BlockPos pos, double falloffRatio) {
        double dx = pos.getX() - this.targetPos.x + 0.5;
        double dy = pos.getY() - this.targetPos.y + 0.5;
        double dz = pos.getZ() - this.targetPos.z + 0.5;

        double distanceSquared = dx * dx + dy * dy + dz * dz;

        // Ensure 1:1 with the base mod.
        return LightManager.maxDynamicLightLevel(distanceSquared, 0, this.luminance);
    }

    @Override
    public @NotNull BoundingBox getBoundingBox() {
        return this.box;
    }

    @Override
    public boolean hasChanged() {
        if (this.lastLuminance != this.luminance) {
            this.lastLuminance = this.luminance;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isRemoved() {
        return this.isExpired();
    }
    //endregion
}
