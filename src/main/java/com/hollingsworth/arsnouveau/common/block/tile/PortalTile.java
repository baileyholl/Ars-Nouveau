package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.PortalBlock;

import net.minecraft.block.BlockState;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

import static com.hollingsworth.arsnouveau.setup.BlockRegistry.PORTAL_TILE_TYPE;

public class PortalTile extends TileEntity implements ITickableTileEntity {
    public BlockPos warpPos;
    public String dimID;
    public PortalTile() {
        super(PORTAL_TILE_TYPE);
    }



    public void warp(Entity e){
        if(!level.isClientSide && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {
            e.moveTo(warpPos.getX() +0.5, warpPos.getY(), warpPos.getZ() +0.5, e.yRot, e.xRot);
            Networking.sendToNearby(level, e, new PacketWarpPosition(e.getId(), e.getX(), e.getY(), e.getZ()));
            ((ServerWorld) level).sendParticles(ParticleTypes.PORTAL, warpPos.getX(),  warpPos.getY() + 1,  warpPos.getZ(),
                    4,(this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
        }
    }


    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        this.dimID = compound.getString("dim");
        this.warpPos = NBTUtil.getBlockPos(compound, "warp");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        if(this.warpPos != null){
            NBTUtil.storeBlockPos(compound, "warp", this.warpPos);
        }
        compound.putString("dim", this.dimID);

        return super.save(compound);
    }

    @Override
    public void tick() {
        if(!level.isClientSide && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(worldPosition));
            for(Entity e : entities){
                level.playSound(null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
                e.teleportTo(warpPos.getX(), warpPos.getY(), warpPos.getZ());
                            ((ServerWorld) level).sendParticles(ParticleTypes.PORTAL, warpPos.getX(),  warpPos.getY() + 1,  warpPos.getZ(),
                    4,(this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);

            }
        }
    }
}
