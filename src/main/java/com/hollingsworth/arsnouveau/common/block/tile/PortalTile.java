package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.PortalBlock;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;

import java.util.List;

import static com.hollingsworth.arsnouveau.setup.BlockRegistry.PORTAL_TILE_TYPE;

public class PortalTile extends ModdedTile implements ITickable, ITooltipProvider {
    public BlockPos warpPos;
    public String dimID;
    public Vec2 rotationVec;
    public String displayName;
    public boolean isHorizontal;

    public PortalTile(BlockPos pos, BlockState state) {
        super(PORTAL_TILE_TYPE, pos, state);
    }

    public void warp(Entity e) {
        if (!level.isClientSide && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock) && !e.getType().is(EntityTags.PORTAL_BLACKLISTED)) {
            e.moveTo(warpPos.getX() + 0.5, warpPos.getY(), warpPos.getZ() + 0.5,
                    rotationVec != null ? rotationVec.y : e.yRot, rotationVec != null ? rotationVec.x : e.xRot);
            e.xRot = rotationVec != null ? rotationVec.x : e.xRot;
            e.yRot = rotationVec != null ? rotationVec.y : e.yRot;
            Networking.sendToNearby(level, e, new PacketWarpPosition(e.getId(), e.getX() + 0.5, e.getY(), e.getZ()+ 0.5, e.xRot, e.yRot));
            ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                    4, (this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
        }
    }


    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.dimID = compound.getString("dim");
        this.warpPos = NBTUtil.getBlockPos(compound, "warp");
        this.rotationVec = new Vec2(compound.getFloat("xRot"), compound.getFloat("yRot"));
        this.displayName = compound.getString("display");
        this.isHorizontal = compound.getBoolean("horizontal");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
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
    }

    @Override
    public void tick() {
        if (!level.isClientSide && warpPos != null && !(level.getBlockState(warpPos).getBlock() instanceof PortalBlock)) {
            List<Entity> entities = level.getEntitiesOfClass(Entity.class, new AABB(worldPosition));
            for (Entity e : entities) {
                if (e.getType().is(EntityTags.PORTAL_BLACKLISTED) || (e instanceof EntityFollowProjectile)) {
                    continue;
                }
                level.playSound(null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0f, 1.0f);
                e.teleportTo(warpPos.getX()+ 0.5, warpPos.getY(), warpPos.getZ()+ 0.5);
                ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, warpPos.getX(), warpPos.getY() + 1, warpPos.getZ(),
                        4, (this.level.random.nextDouble() - 0.5D) * 2.0D, -this.level.random.nextDouble(), (this.level.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
                if (rotationVec != null) {
                    e.xRot = rotationVec.x;
                    e.yRot = rotationVec.y;
                    Networking.sendToNearby(e.level, e, new PacketWarpPosition(e.getId(), warpPos.getX()+ 0.5, warpPos.getY(), warpPos.getZ()+ 0.5, e.xRot, e.yRot));

                }
            }
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (this.displayName != null) {
            tooltip.add(new TextComponent(this.displayName));
        }
    }

    public boolean update(){
        if(this.worldPosition != null && this.level != null){
            level.sendBlockUpdated(this.worldPosition, level.getBlockState(worldPosition),  level.getBlockState(worldPosition), 2);
            return true;
        }
        return false;
    }
}
