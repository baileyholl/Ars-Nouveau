package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.PortalBlock;

import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import net.minecraft.block.BlockState;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.hollingsworth.arsnouveau.setup.BlockRegistry.PORTAL_TILE_TYPE;

public class PortalTile extends TileEntity implements ITickableTileEntity, ITooltipProvider {
    public BlockPos warpPos;
    public String dimID;
    public Vector2f rotationVec;
    public String displayName;
    public boolean isHorizontal;

    public PortalTile() {
        super(PORTAL_TILE_TYPE);
    }

    public void warp(Entity e) {
        if (!level.isClientSide && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {
            e.moveTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5,
                    rotationVec != null ? rotationVec.y : e.yRot, rotationVec != null ? rotationVec.x : e.xRot);
            e.xRot = rotationVec != null ? rotationVec.x : e.xRot;
            e.yRot = rotationVec != null ? rotationVec.y : e.yRot;
            Networking.sendToNearby(level, e, new PacketWarpPosition(e.getId(), e.getX(), e.getY(), e.getZ(), e.xRot, e.yRot));
            ((ServerWorld) level).sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                    4, (this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
        }
    }


    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.dimID = compound.getString("dim");
        this.warpPos = NBTUtil.getBlockPos(compound, "warp");
        this.rotationVec = new Vector2f(compound.getFloat("xRot"), compound.getFloat("yRot"));
        this.displayName = compound.getString("display");
        this.isHorizontal = compound.getBoolean("horizontal");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if (this.warpPos != null) {
            NBTUtil.storeBlockPos(compound, "warp", this.warpPos);
        }
        compound.putString("dim", this.dimID);
        if (rotationVec != null) {
            compound.putFloat("xRot", rotationVec.x);
            compound.putFloat("yRot", rotationVec.y);
        }
        if (displayName != null) {
            compound.putString("display", displayName);
        }
        compound.putBoolean("horizontal", isHorizontal);
        return super.save(compound);
    }

    @Override
    public void tick() {
        if (!level.isClientSide && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(worldPosition));
            for (Entity e : entities) {
                if(e instanceof EntityFollowProjectile)
                    continue;
                level.playSound(null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                e.teleportTo(warpPos.getX(), warpPos.getY(), warpPos.getZ());
                ((ServerWorld) level).sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                        4, (this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                if (rotationVec != null) {
                    e.xRot = rotationVec.x;
                    e.yRot = rotationVec.y;
                    Networking.sendToNearby(e.level, e, new PacketWarpPosition(e.getId(), warpPos.getX(), warpPos.getY(), warpPos.getZ(), e.xRot, e.yRot));

                }
            }
        }
    }

    @Override
    public List<String> getTooltip() {
        ArrayList<String> list = new ArrayList();
        if (this.displayName != null) {
            list.add(this.displayName);
        }
        return list;
    }

    public boolean update(){
        if(this.worldPosition != null && this.level != null){
            level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 2);
            return true;
        }
        return false;
    }
    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition),pkt.getTag());
    }
}
