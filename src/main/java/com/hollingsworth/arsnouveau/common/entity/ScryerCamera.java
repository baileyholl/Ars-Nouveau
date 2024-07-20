package com.hollingsworth.arsnouveau.common.entity;


import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.camera.ICameraMountable;
import com.hollingsworth.arsnouveau.common.block.ScryerCrystal;
import com.hollingsworth.arsnouveau.common.block.tile.ScryerCrystalTile;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketSetCameraView;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Camera work is taken from SecurityCraft:
 * https://github.com/Geforce132/SecurityCraft/blob/1.18.2/src/main/java/net/geforcemods/securitycraft/entity/camera/SecurityCamera.java
 */
public class ScryerCamera extends Entity {
    public final double cameraSpeed;
    public int screenshotSoundCooldown;
    protected int redstoneCooldown;
    protected int toggleNightVisionCooldown;
    private boolean shouldProvideNightVision;
    public float zoomAmount;
    public boolean zooming;
    private int viewDistance;
    private boolean loadedChunks;
    boolean hasSentChunks;
    private ChunkTrackingView cameraChunks = null;
    private static final List<Player> DISMOUNTED_PLAYERS = new ArrayList<>();

    public ScryerCamera(EntityType<ScryerCamera> type, Level level) {
        super(type, level);
        this.cameraSpeed = 3.3d;
        this.screenshotSoundCooldown = 0;
        this.redstoneCooldown = 0;
        this.toggleNightVisionCooldown = 0;
        this.shouldProvideNightVision = false;
        this.zoomAmount = 1.0F;
        this.zooming = false;
        this.viewDistance = -1;
        this.loadedChunks = false;
        this.noPhysics = true;
    }

    public ScryerCamera(Level level, BlockPos pos) {
        this(ModEntities.SCRYER_CAMERA.get(), level);
        if (level.getBlockEntity(pos) instanceof ScryerCrystalTile cam) {
            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.5D;
            double z = pos.getZ() + 0.5D;
            if (cam.down) {
                y += 0.25D;
            }

            this.setPos(x, y, z);
            this.setInitialPitchYaw();
        } else {
            this.discard();
        }
    }

    public ScryerCamera(Level level, BlockPos pos, ScryerCamera oldCamera) {
        this(level, pos);
        oldCamera.discardCamera();
    }

    private void setInitialPitchYaw() {
        Direction facing = this.level.getBlockState(this.blockPosition()).getValue(ScryerCrystal.FACING);
        if (facing == Direction.NORTH) {
            this.setYRot(180.0F);
        } else if (facing == Direction.WEST) {
            this.setYRot(90.0F);
        } else if (facing == Direction.SOUTH) {
            this.setYRot(0.0F);
        } else if (facing == Direction.EAST) {
            this.setYRot(270.0F);
        } else if (facing == Direction.DOWN) {
            this.setXRot(75.0F);
        }

    }

    public static boolean hasRecentlyDismounted(Player player) {
        return DISMOUNTED_PLAYERS.remove(player);
    }

    protected boolean repositionEntityAfterLoad() {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {

    }

    public void tick() {
        if (this.level.isClientSide) {
            if (this.screenshotSoundCooldown > 0) {
                --this.screenshotSoundCooldown;
            }

            if (this.redstoneCooldown > 0) {
                --this.redstoneCooldown;
            }

            if (this.toggleNightVisionCooldown > 0) {
                --this.toggleNightVisionCooldown;
            }
        } else if (this.level.getBlockState(this.blockPosition()).getBlock() != BlockRegistry.SCRYERS_CRYSTAL.get()) {
            this.discard();
        }

    }

    public ChunkTrackingView getCameraChunks() {
        return cameraChunks;
    }

    public void setChunkLoadingDistance(int chunkLoadingDistance) {
        cameraChunks = ChunkTrackingView.of(chunkPosition(), chunkLoadingDistance);
    }


    public boolean hasSentChunks() {
        return hasSentChunks;
    }

    public void setHasSentChunks(boolean hasSentChunks) {
        this.hasSentChunks = hasSentChunks;
    }

    public float getZoomAmount() {
        return this.zoomAmount;
    }

    public boolean isCameraDown() {
        BlockEntity var2 = this.level.getBlockEntity(this.blockPosition());
        return var2 instanceof ScryerCrystalTile cam && cam.down;
    }

    public void setRotation(float yaw, float pitch) {
        this.setRot(yaw, pitch);
    }

    public void stopViewing(ServerPlayer player) {
        if (!this.level.isClientSide) {
            this.discard();
            player.camera = player;
            Networking.sendToPlayerClient(new PacketSetCameraView(player), player);
            DISMOUNTED_PLAYERS.add(player);
        }
    }

    @Override
    public void remove(RemovalReason pReason) {
        super.remove(pReason);
        discardCamera();
    }

    public void discardCamera() {
        if (!this.level.isClientSide) {

            if (level.getBlockEntity(this.blockPosition()) instanceof ICameraMountable camMount) {
                camMount.stopViewing();
            }

            SectionPos chunkPos = SectionPos.of(blockPosition());
            int chunkLoadingDistance = cameraChunks instanceof ChunkTrackingView.Positioned positionedChunks ? positionedChunks.viewDistance() : level().getServer().getPlayerList().getViewDistance();

            for (int x = chunkPos.getX() - chunkLoadingDistance; x <= chunkPos.getX() + chunkLoadingDistance; x++) {
                for (int z = chunkPos.getZ() - chunkLoadingDistance; z <= chunkPos.getZ() + chunkLoadingDistance; z++) {
                    ArsNouveau.ticketController.forceChunk((ServerLevel) level(), this, x, z, false, false);
                }
            }
        }
    }

    public void setHasLoadedChunks(int initialViewDistance) {
        this.loadedChunks = true;
        this.viewDistance = initialViewDistance;
    }

    public boolean hasLoadedChunks() {
        return this.loadedChunks;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
    }

    public boolean isAlwaysTicking() {
        return true;
    }
}
