package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import static com.hollingsworth.arsnouveau.setup.BlockRegistry.PORTAL_TILE_TYPE;

public class PortalTile extends TileEntity {
    public BlockPos warpPos;
    public String dimID;
    public PortalTile() {
        super(PORTAL_TILE_TYPE);
    }

    public void warp(Entity e){

        if(!world.isRemote && warpPos != null && !(world.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {

            world.playSound(null, warpPos, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
            e.teleportKeepLoaded(warpPos.getX(), warpPos.getY(), warpPos.getZ());
            ((ServerWorld) world).spawnParticle(ParticleTypes.PORTAL, warpPos.getX(),  warpPos.getY() + 1,  warpPos.getZ(),
                    4,(this.world.rand.nextDouble() - 0.5D) * 2.0D, -this.world.rand.nextDouble(), (this.world.rand.nextDouble() - 0.5D) * 2.0D, 0.1f);

        }
    }


    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        this.dimID = compound.getString("dim");
        this.warpPos = NBTUtil.getBlockPos(compound, "warp");
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if(this.warpPos != null){
            NBTUtil.storeBlockPos(compound, "warp", this.warpPos);
        }
        compound.putString("dim", this.dimID);

        return super.write(compound);
    }

}
